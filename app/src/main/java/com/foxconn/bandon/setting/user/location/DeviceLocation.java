package com.foxconn.bandon.setting.user.location;

public class DeviceLocation {
    private double lat;
    private double lon;
    private String province;
    private String city;
    private String district;

    DeviceLocation() {

    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(province);
        sb.append(city);
        sb.append(district);
        sb.append(" ");
        sb.append(lon);
        sb.append(",");
        sb.append(lat);
        return sb.toString();
    }
}
