package cn.edu.pku.zhangqixun.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fxjzzyo on 2017/12/9.
 */

public class WeatherInfo implements Serializable{
    TodayWeather todayWeather;
    List<ForecastWeather> forecastWeathers;
    public WeatherInfo(){

    }

    public TodayWeather getTodayWeather() {
        return todayWeather;
    }

    public void setTodayWeather(TodayWeather todayWeather) {
        this.todayWeather = todayWeather;
    }

    public List<ForecastWeather> getForecastWeathers() {
        return forecastWeathers;
    }

    public void setForecastWeathers(List<ForecastWeather> forecastWeathers) {
        this.forecastWeathers = forecastWeathers;
    }
}
