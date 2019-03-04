package com.foxconn.bandon.weather.model;

public class WeatherInfo {

    /**
     * code : 0
     * info : success
     * data : {"cityName":"深圳","weather":"阴","pm25":"16","qlty":"优","drsg":"建议着薄外套、开衫牛仔衫裤等服装。年老体弱者应适当添加衣物，宜着夹克衫、薄毛衣等。","comf":"舒适","sport":"有降水，推荐您在室内进行健身休闲运动；若坚持户外运动，须注意携带雨具并注意避雨防滑。","tmp":"22","windDir":"北风"}
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

    @Override
    public String toString() {
        return "WeatherInfo{" +
                "code='" + code + '\'' +
                ", info='" + info + '\'' +
                ", data=" + data.toString() +
                '}';
    }

    public static class DataBean {
        /**
         * cityName : 深圳
         * weather : 阴
         * pm25 : 16
         * qlty : 优
         * drsg : 建议着薄外套、开衫牛仔衫裤等服装。年老体弱者应适当添加衣物，宜着夹克衫、薄毛衣等。
         * comf : 舒适
         * sport : 有降水，推荐您在室内进行健身休闲运动；若坚持户外运动，须注意携带雨具并注意避雨防滑。
         * tmp : 22
         * windDir : 北风
         */

        private String cityName;
        private String weather;
        private String pm25;
        private String qlty;
        private String drsg;
        private String comf;
        private String sport;
        private String tmp;
        private String windDir;

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public String getWeather() {
            return weather;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }

        public String getPm25() {
            return pm25;
        }

        public void setPm25(String pm25) {
            this.pm25 = pm25;
        }

        public String getQlty() {
            return qlty;
        }

        public void setQlty(String qlty) {
            this.qlty = qlty;
        }

        public String getDrsg() {
            return drsg;
        }

        public void setDrsg(String drsg) {
            this.drsg = drsg;
        }

        public String getComf() {
            return comf;
        }

        public void setComf(String comf) {
            this.comf = comf;
        }

        public String getSport() {
            return sport;
        }

        public void setSport(String sport) {
            this.sport = sport;
        }

        public String getTmp() {
            return tmp;
        }

        public void setTmp(String tmp) {
            this.tmp = tmp;
        }

        public String getWindDir() {
            return windDir;
        }

        public void setWindDir(String windDir) {
            this.windDir = windDir;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "cityName='" + cityName + '\'' +
                    ", weather='" + weather + '\'' +
                    ", pm25='" + pm25 + '\'' +
                    ", qlty='" + qlty + '\'' +
                    ", drsg='" + drsg + '\'' +
                    ", comf='" + comf + '\'' +
                    ", sport='" + sport + '\'' +
                    ", tmp='" + tmp + '\'' +
                    ", windDir='" + windDir + '\'' +
                    '}';
        }
    }
}
