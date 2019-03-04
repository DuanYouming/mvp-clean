package com.foxconn.bandon.main.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.foxconn.bandon.R;
import com.foxconn.bandon.main.model.AppButton;

import java.util.List;


public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater mInflater;
    private List<AppButton> mAppButtons;
    private ItemClickListener onClickListener;

    AppsAdapter(Context context, List<AppButton> appButtons) {
        this.context = context;
        this.mAppButtons = appButtons;
        this.mInflater = LayoutInflater.from(context);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.main_list_buttons, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        AppButton appButton = mAppButtons.get(position);
        holder.title.setText(appButton.getTitle());
        holder.icon.setImageResource(appButton.getSrcImage());
        Drawable drawable = context.getDrawable(appButton.getBackImage());
        holder.view.setBackground(drawable);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null == mAppButtons) ? 0 : mAppButtons.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;
        View view;

        ViewHolder(View view) {
            super(view);
            icon = (ImageView) view.findViewById(R.id.apps_icon);
            title = (TextView) view.findViewById(R.id.apps_title);
            this.view = view;
        }

    }

    void setOnClickListener(ItemClickListener listener) {
        this.onClickListener = listener;
    }

    interface ItemClickListener {
        void onClick(int index);
    }
}
