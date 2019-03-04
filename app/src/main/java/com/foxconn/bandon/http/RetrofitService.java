package com.foxconn.bandon.http;

import com.foxconn.bandon.food.model.ColdRoomFood;
import com.foxconn.bandon.food.model.FridgeFood;
import com.foxconn.bandon.label.detail.model.LabelDetail;
import com.foxconn.bandon.label.detail.model.LabelItem;
import com.foxconn.bandon.label.select.model.Label;
import com.foxconn.bandon.label.select.model.LabelVersion;
import com.foxconn.bandon.setting.wallpaper.model.WallpaperBean;
import com.foxconn.bandon.weather.model.LocationInfo;
import com.foxconn.bandon.weather.model.WeatherInfo;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface RetrofitService {

    @POST("refrigerator/V1/device/register")
    Call<ResponseStr> register(@Body RequestBody data);

    @POST("deviceAddress/bind")
    Call<ResponseStr> bindDevice(@Body RequestBody data);

    @GET("foodTagVersion/V1/update?version=1.0.0")
    Call<LabelVersion> getLabelUrl();

    @GET
    Call<Label> getLabel(@Url String url);

    @POST("foodTag/detail/V1/select")
    Call<LabelItem> getLabelItem(@Body RequestBody body);

    @GET("foodStorage/detail/V1/select")
    Call<LabelDetail> getLabelDetail(@Query("id") int id);

    @POST("foodStorage/detail/V1/insert")
    Call<ResponseStr> saveLabelDetail(@Body RequestBody body);

    @POST("foodStorage/detail/V1/delete")
    Call<ResponseStr> deleteLabelDetail(@Body RequestBody body);

    @GET("foodStorage/list/V1/select")
    Call<FridgeFood> getFridgeFoods(@Query("deviceId") String deviceId, @Query("index") int index, @Query("storageRegion") String storageRegion);

    @POST("foodStorage/location/V1/update")
    Call<ResponseStr> updateLocation(@Body RequestBody data);

    @GET("foodStorage/refrigerating_compartment/V1/select")
    Call<ColdRoomFood> getFridgeFoods(@Query("deviceId") String deviceId);

    @GET
    Call<LocationInfo> getLocation(@Url String url);

    @GET("forservlet/getWeather")
    Call<WeatherInfo> getWeather(@Query("appKey") String key, @Query("location") String location);

    @GET("image/list")
    Call<WallpaperBean> getWallpapers(@Query("deviceId") String deviceId);

    @GET
    Call<ResponseBody> downWallpaper(@Url String url);
}
