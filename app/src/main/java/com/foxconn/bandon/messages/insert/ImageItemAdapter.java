package com.foxconn.bandon.messages.insert;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.request.target.ImageViewTarget;
import com.foxconn.bandon.R;
import com.foxconn.bandon.utils.GlideApp;
import com.foxconn.bandon.utils.ImageUtils;
import com.foxconn.bandon.utils.LogUtils;

import java.util.List;

public class ImageItemAdapter extends RecyclerView.Adapter<ImageItemAdapter.ViewHolder> {

    private static final String TAG = ImageItemAdapter.class.getSimpleName();
    private static final int EMOTION_SIZE = 110;
    private static final int IMAGE_SIZE = 245;
    private List<InsertImage> mImages;
    private LayoutInflater mInflater;
    private Context mContext;
    private int mType;
    private int mItemSelected = -1;
    private SelectedCallback mCallback;


    ImageItemAdapter(Context context, List<InsertImage> mImages, int type) {
        this.mImages = mImages;
        this.mType = type;
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ImageItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.insert_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageItemAdapter.ViewHolder holder, int position) {
        if (mType == InsertImageView.TYPE_IMAGE) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(IMAGE_SIZE, IMAGE_SIZE);
            params.leftMargin = 32;
            params.topMargin = 32;
            holder.container.setLayoutParams(params);
        } else {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(EMOTION_SIZE, EMOTION_SIZE);
            holder.container.setLayoutParams(params);
        }

        loadImage(holder.imageView, mImages.get(position));


        if (mItemSelected == position) {
            holder.container.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_draw_select_image_border));
        } else {
            holder.container.setBackground(null);
        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemSelected != -1) {
                    notifyItemChanged(mItemSelected);
                }
                mItemSelected = holder.getAdapterPosition();
                view.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_draw_select_image_border));
                if (mType == InsertImageView.TYPE_EMOTION) {
                    mImages.get(mItemSelected).setEmotion(true);
                } else {
                    mImages.get(mItemSelected).setEmotion(false);
                }
                mCallback.onItemSelected(mImages.get(mItemSelected));
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == mImages ? 0 : mImages.size();
    }

    public void setCallback(SelectedCallback callback) {
        this.mCallback = callback;
    }

    private void loadImage(ImageView imageView, InsertImage image) {
        if (!image.isLocal()) {
            Bitmap bitmap = ImageUtils.getBitmapFromAssets(mContext, image.getPath());
            if (mType == InsertImageView.TYPE_EMOTION) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageBitmap(ImageUtils.resize(bitmap, IMAGE_SIZE, IMAGE_SIZE));
            }
        } else {
            if (mType == InsertImageView.TYPE_EMOTION) {
                GlideApp.with(mContext).asBitmap().load(image.getPath()).into(imageView);
            } else {
                GlideApp.with(mContext).asBitmap().load(image.getPath()).override(IMAGE_SIZE,IMAGE_SIZE).into(new Transform(imageView));
           }
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View container;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            container = itemView;
        }

    }

    class Transform extends ImageViewTarget<Bitmap> {

        private ImageView mImageView;

        Transform(ImageView view) {
            super(view);
            mImageView = view;
        }

        @Override
        protected void setResource(@Nullable Bitmap resource) {
            if (null != resource) {
                mImageView.setImageBitmap(ImageUtils.resize(resource, IMAGE_SIZE, IMAGE_SIZE));
            } else {
                LogUtils.d(TAG, "resource is null");
            }
        }
    }

    public interface SelectedCallback {
        void onItemSelected(InsertImage image);
    }

}
