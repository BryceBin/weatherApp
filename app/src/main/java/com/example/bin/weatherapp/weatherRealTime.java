package com.example.bin.weatherapp;

/**
 * @Author: Bhy
 * @Date: 2018/11/26
 */
/**
 * Copyright 2018 bejson.com
 */
public class weatherRealTime {
    private String status;
    private String o3;
    private String co;
    private String temperature;
    private String pm10;
    private String skycon;
    private String cloudrate;
    private String aqi;
    private String dswrf;
    private String visibility;
    private String humidity;
    private String so2;
    private String pres;
    private String pm25;
    private String no2;
    private Wind wind;
    private Comfort comfort;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getO3() {
        return o3;
    }

    public void setO3(String o3) {
        this.o3 = o3;
    }

    public String getCo() {
        return co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPm10() {
        return pm10;
    }

    public void setPm10(String pm10) {
        this.pm10 = pm10;
    }

    public String getSkycon() {
        return skycon;
    }

    public void setSkycon(String skycon) {
        this.skycon = skycon;
    }

    public String getCloudrate() {
        return cloudrate;
    }

    public void setCloudrate(String cloudrate) {
        this.cloudrate = cloudrate;
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getDswrf() {
        return dswrf;
    }

    public void setDswrf(String dswrf) {
        this.dswrf = dswrf;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getSo2() {
        return so2;
    }

    public void setSo2(String so2) {
        this.so2 = so2;
    }

    public String getPres() {
        return pres;
    }

    public void setPres(String pres) {
        this.pres = pres;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getNo2() {
        return no2;
    }

    public void setNo2(String no2) {
        this.no2 = no2;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }
    public Wind getWind() {
        return wind;
}

    public void setComfort(Comfort comfort) {
        this.comfort = comfort;
    }
    public Comfort getComfort() {
        return comfort;
    }

    class Wind {
        private String direction;
        private String speed;

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        public String getSpeed() {
            return speed;
        }

        public void setSpeed(String speed) {
            this.speed = speed;
        }
    }

    class Comfort {
        private String index;
        private String desc;
        public void setIndex(String index) {
            this.index = index;
        }
        public String getIndex() {
            return index;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
        public String getDesc() {
            return desc;
        }

    }
}
