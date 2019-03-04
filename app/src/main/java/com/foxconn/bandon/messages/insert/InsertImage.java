package com.foxconn.bandon.messages.insert;


public class InsertImage {

    private String path;
    private boolean isLocal;
    private boolean isEmotion;

    public InsertImage(String path, boolean isLocal) {
        this.path = path;
        this.isLocal = isLocal;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public boolean isEmotion() {
        return isEmotion;
    }

    public void setEmotion(boolean emotion) {
        isEmotion = emotion;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
