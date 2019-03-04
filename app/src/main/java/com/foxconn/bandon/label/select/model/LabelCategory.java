package com.foxconn.bandon.label.select.model;

public class LabelCategory {

    private int icon;
    private String name;
    private String tag;

    public LabelCategory(int icon, String name, String tag) {
        this.icon = icon;
        this.name = name;
        this.tag = tag;
    }

    public int getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }
}
