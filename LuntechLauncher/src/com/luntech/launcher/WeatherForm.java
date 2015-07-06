
package com.luntech.launcher;

public class WeatherForm {
    /** 城市名 */
    private String name;
    /** 城市编号 */
    private String id;
    /** 当前日期 */
    private String ddate;
    /** 星期 */
    private String week;
    /** 温度 */
    private String temp;
    /** 天气描述 */
    private String weather;
    /** 风力 */
    private String wind;
    /** 风向 */
    private String fx;

    public WeatherForm() {
    }

    /**
     * 构造方法
     * 
     * @param name
     * @param id
     * @param ddate
     * @param week
     * @param temp
     * @param weather
     * @param wind
     * @param fx
     */
    public WeatherForm(String name, String id, String ddate, String week, String temp,
            String weather, String wind, String fx) {
        super();
        this.name = name;
        this.id = id;
        this.ddate = ddate;
        this.week = week;
        this.temp = temp;
        this.weather = weather;
        this.wind = wind;
        this.fx = fx;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDdate() {
        return ddate;
    }

    public void setDdate(String ddate) {
        this.ddate = ddate;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getFx() {
        return fx;
    }

    public void setFx(String fx) {
        this.fx = fx;
    }

    @Override
    public String toString() {
        return "WeatherForm [name=" + name + ", id=" + id + ", ddate=" + ddate + ", week=" + week
                + ", temp=" + temp + ", weather=" + weather + ", wind=" + wind + ", fx=" + fx + "]";
    }
}
