package com.foxconn.bandon.main.model;

public class AppButton {
    private String title;
    private int backImage;
    private int srcImage;
    private String fragTag;

    public AppButton(String title, String fragTag, int backImage, int srcImage) {
        this.title = title;
        this.backImage = backImage;
        this.srcImage = srcImage;
        this.fragTag = fragTag;
    }

    public String getTitle() {
        return title;
    }

    public int getBackImage() {
        return backImage;
    }

    public int getSrcImage() {
        return srcImage;
    }

    public String getFragTag() {
        return fragTag;
    }
}
