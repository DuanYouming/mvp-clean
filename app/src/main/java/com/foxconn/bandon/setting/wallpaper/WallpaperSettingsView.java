package com.foxconn.bandon.setting.wallpaper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.foxconn.bandon.R;
import com.foxconn.bandon.setting.BaseSettingView;
import com.foxconn.bandon.setting.wallpaper.model.WallpaperBean;
import com.foxconn.bandon.setting.wallpaper.model.WallpaperRepository;
import com.foxconn.bandon.setting.wallpaper.presenter.WallpaperSettingPresenter;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.GlideApp;

import java.util.ArrayList;
import java.util.List;

public class WallpaperSettingsView extends FrameLayout implements WallpaperContact.View, View.OnClickListener {
    public static final String TAG = WallpaperSettingsView.class.getSimpleName();
    private BaseSettingView.DismissCallback mCallback;
    private WallpaperContact.Presenter mPresenter;
    private List<WallpaperBean.DataBean> mLocalWallpapers = new ArrayList<>();
    private List<WallpaperBean.DataBean> mServerWallpapers = new ArrayList<>();
    private List<WallpaperBean.DataBean> mWallpapers = new ArrayList<>();
    private WallpaperBean.DataBean mSelected;
    private Adapter mAdapter;
    private View mProgress;

    public WallpaperSettingsView(@NonNull Context context) {
        super(context);
    }

    public WallpaperSettingsView(@NonNull Context context, BaseSettingView.DismissCallback callback) {
        this(context);
        this.mCallback = callback;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setup();
        new WallpaperSettingPresenter(this, WallpaperRepository.getInstance(new AppExecutors()));
        mPresenter.load();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        WallpaperRepository.destroyInstance();
    }

    private void setup() {
        inflate(getContext(), R.layout.layout_wallpaer_setting_view, this);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mAdapter = new Adapter(getContext(), mWallpapers);
        mRecyclerView.setAdapter(mAdapter);

        View btnConfirm = findViewById(R.id.btn_confirm);
        View btnCancel = findViewById(R.id.btn_cancel);
        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        mProgress = findViewById(R.id.progress);

    }

    @Override
    public void showDialog() {
        mProgress.setVisibility(VISIBLE);
    }

    @Override
    public void removeDialog() {
        mProgress.setVisibility(INVISIBLE);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateLocalWallpapers(List<WallpaperBean.DataBean> wallpapers) {
        mLocalWallpapers = wallpapers;
        mWallpapers.clear();
        mWallpapers.addAll(mLocalWallpapers);
        mWallpapers.addAll(mServerWallpapers);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateServerWallpapers(List<WallpaperBean.DataBean> wallpapers) {
        mServerWallpapers = wallpapers;
        mWallpapers.clear();
        mWallpapers.addAll(mLocalWallpapers);
        mWallpapers.addAll(mServerWallpapers);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setPresenter(WallpaperContact.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finish() {
        mCallback.onDismiss(TAG);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_confirm) {
            mPresenter.setWallpaper(mSelected);
        } else if (id == R.id.btn_cancel) {
            finish();
        }
    }


    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private static final int IMAGE_SIZE_WIDTH = 270;
        private static final int IMAGE_SIZE_HEIGHT = 480;
        private int mItemSelected = -1;
        private List<WallpaperBean.DataBean> wallpapers;
        private LayoutInflater mInflater;
        private Context mContext;

        Adapter(Context context, List<WallpaperBean.DataBean> wallpapers) {
            this.wallpapers = wallpapers;
            this.mInflater = LayoutInflater.from(context);
            this.mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.insert_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 10;
            params.topMargin = 32;
            holder.container.setLayoutParams(params);
            GlideApp.with(mContext).asBitmap().load(wallpapers.get(holder.getAdapterPosition()).getThumnailUrl()).centerCrop().override(IMAGE_SIZE_WIDTH, IMAGE_SIZE_HEIGHT).into(holder.imageView);

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
                    mSelected = wallpapers.get(holder.getAdapterPosition());
                }
            });

        }

        @Override
        public int getItemCount() {
            return null != wallpapers ? wallpapers.size() : 0;
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

    }

}
