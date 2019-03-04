package com.foxconn.bandon.custom.viewpager;

import android.view.View;

public interface CBViewHolderCreator {

    Holder createHolder(View itemView);

    int getLayoutId();

}
