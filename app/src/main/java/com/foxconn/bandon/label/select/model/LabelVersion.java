package com.foxconn.bandon.label.select.model;

public class LabelVersion {

    /**
     * code : 0
     * info : 获取数据成功
     * data : {"latestVersion":"1.0.4","url":"http://smart-blink.xyz:8060/APPDownload/FoodTag.json","exists":"1"}
     */

    private String code;
    private String info;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * latestVersion : 1.0.4
         * url : http://smart-blink.xyz:8060/APPDownload/FoodTag.json
         * exists : 1
         */

        private String latestVersion;
        private String url;
        private String exists;

        public String getLatestVersion() {
            return latestVersion;
        }

        public void setLatestVersion(String latestVersion) {
            this.latestVersion = latestVersion;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getExists() {
            return exists;
        }

        public void setExists(String exists) {
            this.exists = exists;
        }
    }
}
