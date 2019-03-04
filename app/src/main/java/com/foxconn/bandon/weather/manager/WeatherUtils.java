package com.foxconn.bandon.weather.manager;

import com.foxconn.bandon.R;

public class WeatherUtils {


    public static int getIcon(String weather) {

        switch (weather) {
            case "晴":
                return R.drawable.ic_sunny;
            case "多云":
                return R.drawable.ic_cloudy;
            case "少云":
                return R.drawable.ic_few_clouds;
            case "晴间多云":
                return R.drawable.ic_partly_cloudy;
            case "阴":
                return R.drawable.ic_overcast;
            case "有风":
                return R.drawable.ic_windy;
            case "平静":
                return R.drawable.ic_calm;
            case "微风":
                return R.drawable.ic_light_breeze;
            case "和风":
                return R.drawable.ic_moderate;
            case "清风":
                return R.drawable.ic_fresh_breeze;
            case "强风/劲风":
                return R.drawable.ic_strong_breeze;
            case "疾风":
                return R.drawable.ic_high_wind;
            case "大风":
                return R.drawable.ic_gale;
            case "烈风":
                return R.drawable.ic_strong_gale;
            case "风暴":
                return R.drawable.ic_storm;
            case "狂爆风":
                return R.drawable.ic_violent_storm;
            case "飓风":
                return R.drawable.ic_hurricane;
            case "龙卷风":
                return R.drawable.ic_tornado;
            case "热带风暴":
                return R.drawable.ic_tropical_storm;
            case "阵雨":
                return R.drawable.ic_shower_rain;
            case "强阵雨":
                return R.drawable.ic_heavy_shower_rain;
            case "雷阵雨":
                return R.drawable.ic_thundershower;
            case "强雷阵雨":
                return R.drawable.ic_heavy_thunderstorm;
            case "雷阵雨伴有冰雹":
                return R.drawable.ic_hail;
            case "小雨":
                return R.drawable.ic_light_rain;
            case "中雨":
                return R.drawable.ic_moderate_rain;
            case "大雨":
                return R.drawable.ic_heavy_rain;
            case "极端降雨":
                return R.drawable.ic_extreme_rain;
            case "毛毛雨/细雨":
                return R.drawable.ic_drizzle_rain;
            case "暴雨":
                return R.drawable.ic_storm;
            case "大暴雨":
                return R.drawable.ic_heavy_storm;
            case "特大暴雨":
                return R.drawable.ic_severe_storm;
            case "冻雨":
                return R.drawable.ic_freezing_rain;
            case "小雪":
                return R.drawable.ic_light_snow;
            case "中雪":
                return R.drawable.ic_moderate_snow;
            case "大雪":
                return R.drawable.ic_heavy_snow;
            case "暴雪":
                return R.drawable.ic_snowstorm;
            case "雨夹雪":
                return R.drawable.ic_sleet;
            case "雨雪天气":
                return R.drawable.ic_rain_and_snow;
            case "阵雨夹雪":
                return R.drawable.ic_shower_snow;
            case "阵雪":
                return R.drawable.ic_snow_flurry;
            case "薄雾":
                return R.drawable.ic_mist;
            case "雾":
                return R.drawable.ic_foggy;
            case "霾":
                return R.drawable.ic_haze;
            case "扬沙":
                return R.drawable.ic_sand;
            case "浮尘":
                return R.drawable.ic_dust;
            case "沙尘暴":
                return R.drawable.ic_duststorm;
            case "强沙尘暴":
                return R.drawable.ic_sandstorm;
            case "热":
                return R.drawable.ic_hot;
            case "冷":
                return R.drawable.ic_cold;
            case "未知":
                return R.drawable.ic_unknown;
            default:
                return R.drawable.ic_unknown;
        }
    }

}
