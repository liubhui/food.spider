package com.food.spider.model;import com.alibaba.fastjson.annotation.JSONField;import java.math.BigDecimal;/** * Created by user on 16-12-3. */public class Point {    @JSONField(name = "lat")    private BigDecimal latitude;    @JSONField(name = "lng")    private BigDecimal longitude;    public BigDecimal getLatitude() {        return latitude;    }    public void setLatitude(BigDecimal latitude) {        this.latitude = latitude;    }    public BigDecimal getLongitude() {        return longitude;    }    public void setLongitude(BigDecimal longitude) {        this.longitude = longitude;    }}