package com.foxconn.bandon.food.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.foxconn.bandon.R;

import com.foxconn.bandon.food.model.FridgeFood;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.GlideApp;
import com.foxconn.bandon.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;


public abstract class FridgeCommonView extends FrameLayout {

    private static final String TAG = FridgeCommonView.class.getSimpleName();
    private List<FridgeFood.Label> mLabels;
    private ClickCallback callback;
    private Adapter mAdapter;

    public FridgeCommonView(@NonNull Context context) {
        this(context, null);
    }

    public FridgeCommonView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        detachedFromWindow();
    }

    private void setup() {
        LogUtils.d(TAG, "setup");
        inflate(getContext(), R.layout.fridge_common_view, this);
        RecyclerView listView = findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        listView.setLayoutManager(layoutManager);
        mLabels = new ArrayList<>();
        mAdapter = new Adapter(getContext(), mLabels);
        listView.setAdapter(mAdapter);
    }

    protected void setLabels(List<FridgeFood.Label> labels) {
        mLabels.clear();
        mLabels.addAll(labels);
        mAdapter.notifyDataSetChanged();
    }

    protected abstract void attachedToWindow();

    protected abstract void detachedFromWindow();

    protected void setClickCallback(ClickCallback callback){
        this.callback = callback;
    }

    interface ClickCallback {
        void onClick(int id);
    }


    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private List<FridgeFood.Label> mLabels;
        private LayoutInflater mInflater;

        public Adapter(Context context, List<FridgeFood.Label> labels) {
            this.mLabels = labels;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(mInflater.inflate(R.layout.fridge_item_view, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            FridgeFood.Label label = mLabels.get(position);
            holder.textName.setText(label.foodTagRename);
            holder.progressBar.setVisibility(VISIBLE);

            GlideApp.with(getContext()).asBitmap().load(label.foodTagSmallPicture).listener(new RequestListener<Bitmap>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                    holder.progressBar.setVisibility(GONE);
                    return false;
                }
            }).into(holder.imageIcon);

            holder.textHint.setText(label.iconColorDetail);
            if (TextUtils.equals(label.iconColor, Constant.FOOD_TYPE_EXPIRED)) {
                holder.textHint.setTextColor(Color.RED);
                holder.imageHint.setImageResource(R.drawable.ic_notification_red);
            } else if (TextUtils.equals(label.iconColor, Constant.FOOD_TYPE_SOON_EXPIRE)) {
                holder.textHint.setTextColor(Color.parseColor("#FDBB4D"));
                holder.imageHint.setImageResource(R.drawable.ic_notification_yellow);
            } else {
                holder.imageHint.setImageBitmap(null);
            }

            holder.view.setOnClickListener(v -> callback.onClick(mLabels.get(holder.getAdapterPosition()).id));

        }

        @Override
        public int getItemCount() {
            return null == mLabels ? 0 : mLabels.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView imageIcon;
            private TextView textName;
            private TextView textHint;
            private ImageView imageHint;
            private ProgressBar progressBar;
            private View view;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
                imageIcon = view.findViewById(R.id.image_icon);
                imageHint = view.findViewById(R.id.image_hint);
                textName = view.findViewById(R.id.text_name);
                textHint = view.findViewById(R.id.text_hint);
                progressBar = view.findViewById(R.id.progress_bar);
            }
        }
    }


}
