package com.foxconn.bandon.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtils {
    public static void hide(View view, Context context) {
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (null != manager)
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
