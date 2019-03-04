package com.foxconn.bandon.messages.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;


public class DrawMerge {

    private DrawingPanel mDrawingPanel;
    private ImagePanel mImagePanel;

    DrawMerge(DrawingPanel drawingPanel, ImagePanel imagePanel) {
        this.mDrawingPanel = drawingPanel;
        this.mImagePanel = imagePanel;
    }

    public Bitmap toBitmap() {
        return overlay(mImagePanel.getBitmap(), mDrawingPanel.getBitmap());
    }

    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }
}

