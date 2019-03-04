package com.foxconn.bandon.setting.wallpaper.model;

import java.util.List;

public class WallpaperBean {

    private String code;
    private String info;
    private List<DataBean> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 8121
         * deviceId : F0FE6B145FFF
         * originalUrl : http://www.ijiaxiaoqu.com:8514/group1/M00/01/EE/Ci5dQVsExLWAdp1VAABKtvx2xk0409.jpg
         * thumbnailUrl : http://www.ijiaxiaoqu.com:8514/group1/M00/01/E4/Ci59JFsExLOAPkavAAAp6Hfqr80879.jpg
         * createTime : 2018-06-08 17:22:59
         * editTime : 2018-06-08 17:22:59
         * delFlag : false
         * pictureTag : 0
         */

        private int id;
        private String deviceId;
        private String originalUrl;
        private String thumnailUrl;
        private String createTime;
        private String editTime;
        private boolean delFlag;
        private String pictureTag;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getOriginalUrl() {
            return originalUrl;
        }

        public void setOriginalUrl(String originalUrl) {
            this.originalUrl = originalUrl;
        }

        public String getThumnailUrl() {
            return thumnailUrl;
        }

        public void setThumnailUrl(String thumnailUrl) {
            this.thumnailUrl = thumnailUrl;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getEditTime() {
            return editTime;
        }

        public void setEditTime(String editTime) {
            this.editTime = editTime;
        }

        public boolean isDelFlag() {
            return delFlag;
        }

        public void setDelFlag(boolean delFlag) {
            this.delFlag = delFlag;
        }

        public String getPictureTag() {
            return pictureTag;
        }

        public void setPictureTag(String pictureTag) {
            this.pictureTag = pictureTag;
        }
    }
}
