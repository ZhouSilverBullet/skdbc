package com.hackson.skdbc.entity;

/**
 * Email: zhousaito@163.com
 * Created by zhousaito 2019-12-01 11:22
 * Version: 1.0
 * Description:
 */
public class CarInfoBean {
    private int icon;
    private String carName;
    private String time;
    //半日游，全天
    private String type;
    private String peopleNum;
    private float starNum;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(String peopleNum) {
        this.peopleNum = peopleNum;
    }

    public float getStarNum() {
        return starNum;
    }

    public void setStarNum(float starNum) {
        this.starNum = starNum;
    }
}
