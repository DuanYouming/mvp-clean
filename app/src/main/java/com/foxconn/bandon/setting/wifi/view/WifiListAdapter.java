package com.foxconn.bandon.setting.wifi.view;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.foxconn.bandon.R;
import com.foxconn.bandon.setting.wifi.model.WifiDevice;

import java.util.List;

public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.ViewHolder> {
    private static final int WIFI_SIGNAL_LEVEL = 4;
    private List<WifiDevice> mResults;
    private LayoutInflater mInflater;
    private int[] strengthImages = {R.drawable.ic_wifi_0s, R.drawable.ic_wifi_1s, R.drawable.ic_wifi_2s, R.drawable.ic_wifi_fulls};
    private ClickCallback callback;

    WifiListAdapter(Context mContext, List<WifiDevice> mResults) {
        this.mResults = mResults;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_wifi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        WifiDevice device = mResults.get(position);
        holder.mTextName.setText(device.getSSID());
        holder.mTextName.setSelected(device.isConnected());
        holder.mLocked.setVisibility(device.isOpen() ? View.INVISIBLE : View.VISIBLE);
        holder.mStrength.setImageResource(getStrengthImage(device.getLevel()));

        holder.parent.setOnClickListener(view -> {
            if (!holder.mTextName.isSelected()) {
                callback.onClick(holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return null == mResults ? 0 : mResults.size();
    }

    public void setCallback(ClickCallback callback) {
        this.callback = callback;
    }

    private int getStrengthImage(int level) {
        int strength = WifiManager.calculateSignalLevel(level, WIFI_SIGNAL_LEVEL);
        if (strength >= 0 && strength < strengthImages.length) {
            return strengthImages[strength];
        } else {
            return strengthImages[0];
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextName;
        ImageView mStrength;
        ImageView mLocked;
        View parent;

        ViewHolder(View itemView) {
            super(itemView);
            parent = itemView;
            mTextName = itemView.findViewById(R.id.info_text);
            mStrength = itemView.findViewById(R.id.signal);
            mLocked = itemView.findViewById(R.id.secured);
        }

    }

    interface ClickCallback {
        void onClick(int position);
    }
}
