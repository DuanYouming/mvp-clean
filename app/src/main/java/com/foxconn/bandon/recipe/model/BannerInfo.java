package com.foxconn.bandon.recipe.model;

public class BannerInfo {

    private String url;
    private int res;
    private String hint;
    private String id;

    BannerInfo(int res, String hint, String id) {
        this.res = res;
        this.hint = hint;
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
