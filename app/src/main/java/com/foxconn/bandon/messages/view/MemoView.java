package com.foxconn.bandon.messages.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.foxconn.bandon.R;
import com.foxconn.bandon.messages.model.Memo;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.DateUtils;
import com.foxconn.bandon.utils.LogUtils;

import java.text.ParseException;
import java.util.Date;

@SuppressLint("ViewConstructor")
public class MemoView extends FrameLayout {
    private static final String TAG = MemoView.class.getSimpleName();

    private Memo mMemo;
    private ImageView mImg;
    private int sourceImgW;
    private int sourceImgH;

    public MemoView(@NonNull Context context, Memo memo) {
        super(context);
        mMemo = memo;
        bindMemo();
    }

    private void bindMemo() {
        @SuppressLint("InflateParams")
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.memo_item, this,false);
        String name = mMemo.getPath();
        mImg = itemView.findViewById(R.id.image);
        TextView createdTxt = itemView.findViewById(R.id.created);

        if (null != name) {
            Bitmap bitmap = BitmapFactory.decodeFile(Constant.LOCAL_MESSAGE_PATH + name);
            if(null== bitmap){
                LogUtils.d(TAG,"bitmap not found");
                return;
            }
            sourceImgW = bitmap.getWidth();
            sourceImgH = bitmap.getHeight();
            mImg.setImageBitmap(bitmap);
            mImg.setRotation(mMemo.getAngle());
            mImg.getLayoutParams().width = Math.round(sourceImgW * mMemo.getScale());
            mImg.getLayoutParams().height = Math.round(sourceImgH * mMemo.getScale());
            mImg.requestLayout();
        }

        try {
            createdTxt.setText(DateUtils.memoDate(new Date(mMemo.getTimes())));
        } catch (ParseException e) {
            LogUtils.e(TAG, "Parse timestamp error:" + e.toString());
        }

        addView(itemView);
    }

    public ImageView getContentImg() {
        return mImg;
    }

    public int getSourceImgW() {
        return sourceImgW;
    }

    public int getSourceImgH() {
        return sourceImgH;
    }
}