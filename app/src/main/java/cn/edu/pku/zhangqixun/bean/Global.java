package cn.edu.pku.zhangqixun.bean;

/**
 * Created by fxjzzyo on 2017/12/9.
 */

public class Global {
    public static String WEATHER_RECEIVED = "android.appwidget.action.APPWIDGET_UPDATE";//广播的ACTION
    public static String WEATHER_PM25_RECEIVED = "pm25info_received";//广播的ACTION
    public static String CITY_CODE = "cityCode";//全局的当前城市码
    public static String URL_BASE = "http://wthrcdn.etouch.cn/WeatherApi?citykey=";
    public static int FLAG = 0;//标志是省会还是普通城市，省会：1，普通：0


}
