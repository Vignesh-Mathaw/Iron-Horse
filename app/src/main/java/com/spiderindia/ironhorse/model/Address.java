package com.spiderindia.ironhorse.model;

public class Address {
    private String id,name,mobile,landmark,flat_no,street,pincode,latitude,longitude,is_default, city_id,city_name,area_id,area_name;


    public Address(String id, String name, String mobile, String landmark, String flat_no,
                   String street, String pincode, String latitude, String longitude, String is_default,
                   String city_id, String city_name, String area_id, String area_name) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.landmark = landmark;
        this.flat_no = flat_no;
        this.street = street;
        this.pincode = pincode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.is_default = is_default;
        this.city_id = city_id;
        this.city_name = city_name;
        this.area_id = area_id;
        this.area_name = area_name;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getFlat_no() {
        return flat_no;
    }

    public void setFlat_no(String flat_no) {
        this.flat_no = flat_no;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getIs_default() {
        return is_default;
    }

    public void setIs_default(String is_default) {
        this.is_default = is_default;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getArea_id() {
        return area_id;
    }

    public void setArea_id(String area_id) {
        this.area_id = area_id;
    }

    public String getArea_name() {
        return area_name;

    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }





    @Override
    public String toString() {
        return city_name;
    }


}
