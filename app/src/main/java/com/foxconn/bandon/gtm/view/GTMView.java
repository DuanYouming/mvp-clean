package com.foxconn.bandon.gtm.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.foxconn.bandon.R;
import com.foxconn.bandon.gtm.model.FoodMessage;
import com.foxconn.bandon.gtm.model.GTMessage;
import com.foxconn.bandon.gtm.presenter.GTMessageManager;
import com.foxconn.bandon.utils.LogUtils;

import java.util.List;

public class GTMView extends FrameLayout {
    private static final String TAG = GTMView.class.getSimpleName();
    private ClickCallback callback;

    public GTMView(@NonNull Context context) {
        this(context, null);
    }

    public GTMView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LogUtils.d(TAG, "onAttachedToWindow");
        setup();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtils.d(TAG, "onDetachedFromWindow");
    }

    private void setup() {
        LogUtils.d(TAG, "setup");
        inflate(getContext(), R.layout.view_gtm, this);
        RecyclerView listView = findViewById(R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<GTMessage> messages = GTMessageManager.getInstance().getCurrentGTMessages();
        Adapter mAdapter = new Adapter(getContext(), messages);
        listView.setAdapter(mAdapter);
        findViewById(R.id.btn_close).setOnClickListener(v -> {
            if (null != callback) {
                callback.close();
            }
        });

    }

    public void setCallback(ClickCallback callback) {
        this.callback = callback;
    }

    public interface ClickCallback {
        void close();

        void startLabelDetailFragment(int id);

        void viewGTMessage(GTMessage message);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private List<GTMessage> values;
        private LayoutInflater inflater;

        public Adapter(Context context, List<GTMessage> values) {
            this.values = values;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = inflater.inflate(R.layout.gtm_item_view, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final GTMessage message = values.get(position);

            int iconId = 0;
            switch (message.getLevel()) {
                case GTMessage.LEVEL_LOW:
                    iconId = R.drawable.ic_notification_green;
                    break;
                case GTMessage.LEVEL_MIDDLE:
                    iconId = R.drawable.ic_notification_yellow;
                    break;
                case GTMessage.LEVEL_HIGH:
                    iconId = R.drawable.ic_notification_red;
                    break;
            }
            if (iconId != 0) {
                holder.icon.setImageResource(iconId);
            }
            holder.title.setText(message.getContent());
            holder.itemView.setOnClickListener(v -> {
                if (message instanceof FoodMessage) {
                    int id = ((FoodMessage) message).getTid();
                    callback.startLabelDetailFragment(id);
                } else {
                    callback.viewGTMessage(message);
                }
            });

        }

        @Override
        public int getItemCount() {
            return null == values ? 0 : values.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            private View itemView;
            private TextView title;
            private ImageView icon;

            ViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                title = itemView.findViewById(R.id.title);
                icon = itemView.findViewById(R.id.icon);
            }
        }
    }
}
