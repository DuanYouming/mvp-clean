package com.foxconn.bandon.setting.user.location;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;


public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    private Context mContext;
    private List<String> mMatchLocationList;
    private UpdateLocationListener mUpdateLocationListener;

    LocationAdapter(Context context) {
        mContext = context;
        mMatchLocationList = new ArrayList<>(LocationHelper.getInstance(mContext).getAllLocationMap().keySet());
        mMatchLocationList.sort(String::compareTo);
    }

    public interface UpdateLocationListener {
        void update(DeviceLocation deviceLocation);
    }

    public void setUpdateLocationListener(UpdateLocationListener updateLocationListener) {
        this.mUpdateLocationListener = updateLocationListener;
    }

    public void filter(String string) {
        mMatchLocationList.clear();
        for (String location : LocationHelper.getInstance(mContext).getAllLocationMap().keySet()) {
            if (location.contains(string)) {
                mMatchLocationList.add(location);
            }
        }

        if (mMatchLocationList.size() == 0) {
            mMatchLocationList.add("查無結果");
        }

        notifyDataSetChanged();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView locTxt;
        public ViewHolder(TextView textView) {
            super(textView);
            locTxt = textView;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 36);
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        flp.bottomMargin = 30;
        textView.setLayoutParams(flp);

        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final String loc = mMatchLocationList.get(position);
        holder.locTxt.setText(loc);
        if (!loc.equals("查無結果")) {
            holder.locTxt.setOnClickListener(v -> {
                if (mUpdateLocationListener != null) {
                    mUpdateLocationListener.update(LocationHelper.getInstance(mContext).getAllLocationMap().get(loc));
                }
            });
        } else {
            holder.locTxt.setOnClickListener(null);
        }

    }

    @Override
    public int getItemCount() {
        return mMatchLocationList.size();
    }


}
