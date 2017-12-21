package cn.edu.pku.zhangqixun.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.Serializable;

import cn.edu.pku.zhangqixun.bean.Global;
import cn.edu.pku.zhangqixun.bean.TodayWeather;
import cn.edu.pku.zhangqixun.bean.WeatherInfo;
import cn.edu.pku.zhangqixun.minweather.R;

/**
 * Implementation of App Widget functionality.
 */
public class MyWeatherWidget extends AppWidgetProvider {
    private static String type, wendu, city;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_weather_widget);
        views.setTextViewText(R.id.appwidget_wendu, wendu + "°C");
        views.setTextViewText(R.id.appwidget_type, type);
        views.setTextViewText(R.id.appwidget_address, city);
        setWeatherImg(views, type);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /**
     * 根据天气类型设置天气图片
     *
     * @param type
     */
    public static void setWeatherImg(RemoteViews views, String type) {
        if (type == null) {
            return;
        }
        switch (type) {
            case "晴":
                views.setImageViewResource(R.id.iv_appwidget_icon, R.drawable.biz_plugin_weather_qing);
                break;
            case "阴":
                views.setImageViewResource(R.id.iv_appwidget_icon, R.drawable.biz_plugin_weather_yin);
                break;
            case "多云":
                views.setImageViewResource(R.id.iv_appwidget_icon, R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雾":
                views.setImageViewResource(R.id.iv_appwidget_icon, R.drawable.biz_plugin_weather_wu);
                break;
            case "沙尘暴":
                views.setImageViewResource(R.id.iv_appwidget_icon, R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "小雨":
                views.setImageViewResource(R.id.iv_appwidget_icon, R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "阵雨":
                views.setImageViewResource(R.id.iv_appwidget_icon, R.drawable.biz_plugin_weather_qing);
                break;
            case "雷阵雨":
                views.setImageViewResource(R.id.iv_appwidget_icon, R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                views.setImageViewResource(R.id.iv_appwidget_icon, R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "中雨":
                views.setImageViewResource(R.id.iv_appwidget_icon, R.drawable.biz_plugin_weather_zhongyu);
                break;
            case "大雨":
                views.setImageViewResource(R.id.iv_appwidget_icon, R.drawable.biz_plugin_weather_dayu);
                break;
            case "暴雨":
                views.setImageViewResource(R.id.iv_appwidget_icon, R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                views.setImageViewResource(R.id.iv_appwidget_icon, R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "特大暴雨":
                views.setImageViewResource(R.id.iv_appwidget_icon, R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "小雪":
                views.setImageViewResource(R.id.iv_appwidget_icon, R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "阵雪":
                views.setImageViewResource(R.id.iv_appwidget_icon, R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "中雪":
                views.setImageViewResource(R.id.iv_appwidget_icon, R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "大雪":
                views.setImageViewResource(R.id.iv_appwidget_icon, R.drawable.biz_plugin_weather_daxue);
                break;
            case "暴雪":
                views.setImageViewResource(R.id.iv_appwidget_icon, R.drawable.biz_plugin_weather_baoxue);
                break;
            case "雨夹雪":
                views.setImageViewResource(R.id.iv_appwidget_icon, R.drawable.biz_plugin_weather_yujiaxue);
                break;
            default:
                views.setImageViewResource(R.id.iv_appwidget_icon, R.drawable.biz_plugin_weather_qing);
                break;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (Global.FLAG == 1) {//不接受为了获取pm25而产生的广播
            return;
        }
        Log.i("tag", "onReceive");
        Serializable weather = intent.getSerializableExtra("weather");
        if (weather != null) {
            TodayWeather todayWeather = ((WeatherInfo) weather).getTodayWeather();
            type = todayWeather.getType();
            wendu = todayWeather.getWendu();
            city = todayWeather.getCity();
            // “更新”广播
            updateWeather(context);
        }
    }

    private void updateWeather(Context context) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_weather_widget);
        views.setTextViewText(R.id.appwidget_wendu, wendu + "°C");
        views.setTextViewText(R.id.appwidget_type, type);
        views.setTextViewText(R.id.appwidget_address, city);
        setWeatherImg(views, type);
        // Instruct the widget manager to update the widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetId = appWidgetManager.getAppWidgetIds(new ComponentName(
                context, MyWeatherWidget.class));
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i("tag", "onUpdate");
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.i("tag", "onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        Log.i("tag", "onDisabled");
    }
}

