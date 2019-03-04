package com.foxconn.bandon.setting.date;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.foxconn.bandon.R;
import com.foxconn.bandon.utils.LogUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ZoneList {
    private static final String TAG = ZoneList.class.getSimpleName();

    public static final String KEY_ID = "id";
    public static final String KEY_DISPLAY_NAME = "name";
    public static final String KEY_OFFSET = "offset";
    private static final String KEY_GMT = "gmt";
    private static final String XML_TAG_TIMEZONE = "timezone";
    private static final int HOURS_1 = 60 * 60000;
    private Context mContext;

    ZoneList(Context context) {
        this.mContext = context;
    }

    public List<Map> getTimezoneSortedList(String sortKey) {
        MyComparator comparator = new MyComparator(sortKey);
        List<Map> timezoneSortedList = getZones();
        timezoneSortedList.sort(comparator);
        return timezoneSortedList;
    }

    private List<Map> getZones() {
        List<Map> myData = new ArrayList<>();
        long date = Calendar.getInstance().getTimeInMillis();
        try {
            XmlResourceParser xrp = mContext.getResources().getXml(R.xml.timezones);
            while (xrp.next() != XmlResourceParser.START_TAG)
                xrp.next();
            while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                while (xrp.getEventType() != XmlResourceParser.START_TAG) {
                    if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT) {
                        return myData;
                    }
                    xrp.next();
                }
                if (xrp.getName().equals(XML_TAG_TIMEZONE)) {
                    String id = xrp.getAttributeValue(0);
                    String displayName = xrp.nextText();
                    addItem(myData, id, displayName, date);
                }
                while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                    xrp.next();
                }
                xrp.next();
            }
            xrp.close();
        } catch (XmlPullParserException xppe) {
            LogUtils.e(TAG, "Ill-formatted timezones.xml file" + xppe);
        } catch (java.io.IOException ioe) {
            LogUtils.e(TAG, "Unable to read timezones.xml file" + ioe);
        }
        return myData;
    }


    private void addItem(List<Map> myData, String id, String displayName, long date) {
        Map<String, String> map = new HashMap<>();
        map.put(KEY_ID, id);
        map.put(KEY_DISPLAY_NAME, displayName);
        TimeZone tz = TimeZone.getTimeZone(id);
        int offset = tz.getOffset(date);
        int p = Math.abs(offset);
        StringBuilder name = new StringBuilder();
        name.append("GMT");
        if (offset < 0) {
            name.append('-');
        } else {
            name.append('+');
        }
        name.append(p / (HOURS_1));
        name.append(':');
        int min = p / 60000;
        min %= 60;
        if (min < 10) {
            name.append('0');
        }
        name.append(min);
        map.put(KEY_GMT, name.toString());
        map.put(KEY_OFFSET, String.valueOf(offset));
        myData.add(map);
    }

    private class MyComparator implements Comparator<Map> {
        private String mSortingKey;

        private MyComparator(String sortingKey) {
            mSortingKey = sortingKey;
        }

        public int compare(Map map1, Map map2) {
            Object value1 = map1.get(mSortingKey);
            Object value2 = map2.get(mSortingKey);
            if (!isComparable(value1)) {
                return isComparable(value2) ? 1 : 0;
            } else if (!isComparable(value2)) {
                return -1;
            }
            return ((Comparable) value1).compareTo(value2);
        }

        private boolean isComparable(Object value) {
            return (value != null) && (value instanceof Comparable);
        }
    }
}
