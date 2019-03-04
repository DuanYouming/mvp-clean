package com.foxconn.bandon.setting.date;

import android.content.Context;
import com.foxconn.bandon.custom.NumberStringPicker;
import java.util.ArrayList;
import java.util.Locale;

class TimeSelector {
    private Context mContext;

    TimeSelector(Context context) {
        this.mContext = context;
    }

    void setPickerRangeAndIntValue(NumberStringPicker numberStringPicker,
                                   boolean isLoop, int start, int end, int initValue) {
        ArrayList<String> rangeList = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            rangeList.add(String.format(Locale.getDefault(), "%02d", i));
        }
        numberStringPicker.setRangeAndLoop(rangeList, isLoop, String.format(Locale.getDefault(), "%02d", initValue));
    }

    Context getContext() {
        return mContext;
    }
}
