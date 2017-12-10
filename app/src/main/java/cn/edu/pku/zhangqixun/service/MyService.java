package cn.edu.pku.zhangqixun.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.zhangqixun.bean.ForecastWeather;
import cn.edu.pku.zhangqixun.bean.Global;
import cn.edu.pku.zhangqixun.bean.TodayWeather;
import cn.edu.pku.zhangqixun.bean.WeatherInfo;
import cn.edu.pku.zhangqixun.util.NetUtil;
import cn.edu.pku.zhangqixun.util.SPFutils;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("tag", "-------service create-------");


    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i("tag", "-------service start-------");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("tag", "-------service onDestroy-------");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("tag", "-------service onStartCommand-------");
        new QueryWeatherTask().execute(Global.CITY_CODE);

        return super.onStartCommand(intent, flags, startId);

    }

    /**
     * 根据城市代码查询天气
     *
     * @param cityCode
     */
    private WeatherInfo queryWeatherCode(final String cityCode) {
        //http://wthrcdn.etouch.cn/WeatherApi?citykey=101010100 兰州101160101
        final String address = Global.URL_BASE + cityCode;
        Log.d("myWeather", address);
        TodayWeather todayWeather = null;
        WeatherInfo weatherInfo = null;
        String response = NetUtil.getFromNet(Global.URL_BASE + cityCode);
        if (!response.isEmpty()) {//如果返回不为空
            //解析xml数据
            weatherInfo = parseXML(response);
            //存储天气数据到sharedpreference
            saveWeatherToSpf(weatherInfo.getTodayWeather());
            //存储当前城市代码
            SPFutils.saveStringData(getBaseContext(), "main_city_code", cityCode);
            return weatherInfo;
        }
        return weatherInfo;

    }

    /**
     * 存储天气数据到sharedpreference
     *
     * @param todayWeather
     */
    private void saveWeatherToSpf(TodayWeather todayWeather) {
        if (todayWeather != null) {
            SPFutils.saveStringData(this, "city", todayWeather.getCity());
            SPFutils.saveStringData(this, "updatetime", todayWeather.getUpdatetime());
            SPFutils.saveStringData(this, "wendu", todayWeather.getWendu());
            SPFutils.saveStringData(this, "shidu", todayWeather.getShidu());
            SPFutils.saveStringData(this, "pm25", todayWeather.getPm25());
            SPFutils.saveStringData(this, "fengxiang", todayWeather.getFengxiang());
            SPFutils.saveStringData(this, "fengli", todayWeather.getFengli());
            SPFutils.saveStringData(this, "date", todayWeather.getDate());
            SPFutils.saveStringData(this, "high", todayWeather.getHigh());
            SPFutils.saveStringData(this, "low", todayWeather.getLow());
            SPFutils.saveStringData(this, "type", todayWeather.getType());
        }
    }

    /**
     * 解析xml数据
     *
     * @param xmldata
     */
    private WeatherInfo parseXML(String xmldata) {
        WeatherInfo weatherInfo = null;
        TodayWeather todayWeather = null;
        List<ForecastWeather> forecastWeathers = null;
        ForecastWeather forecastWeather1 = null;
        ForecastWeather forecastWeather2 = null;
        ForecastWeather forecastWeather3 = null;
        ForecastWeather forecastWeather4 = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        int index = -1;//指示当前解析到第几天
        int future1 = 1, future2 = 2, future3 = 3, future4 = 4;//标记预测的第几天
        boolean flag = false;//标记是否中途结束解析，因为并不需要解析所有内容
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    // 判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    // 判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        String name = xmlPullParser.getName();
                        switch (name) {

                            case "resp":
                                weatherInfo = new WeatherInfo();
                                todayWeather = new TodayWeather();
                                break;

                            case "forecast":
                                forecastWeathers = new ArrayList<>();
                                break;
                            case "weather":
                                index++;//天数加一
                                if (index == 0) {

                                } else if (index == future1) {
                                    forecastWeather1 = new ForecastWeather();
                                } else if (index == future2) {
                                    forecastWeather2 = new ForecastWeather();
                                } else if (index == future3) {
                                    forecastWeather3 = new ForecastWeather();
                                } else if (index == future4) {
                                    forecastWeather4 = new ForecastWeather();
                                }

                                break;
                            case "date":
                                //往后走一个标签，走到标签的内容
                                eventType = xmlPullParser.next();
                                String date = xmlPullParser.getText();
                                if (index == 0) {
                                    todayWeather.setDate(date);
                                } else if (index == future1) {
                                    forecastWeather1.setDate(date);
                                } else if (index == future2) {
                                    forecastWeather2.setDate(date);
                                } else if (index == future3) {
                                    forecastWeather3.setDate(date);
                                } else if (index == future4) {
                                    forecastWeather4.setDate(date);
                                }

                                break;
                            case "high":
                                //往后走一个标签，走到标签的内容
                                eventType = xmlPullParser.next();
                                String high = xmlPullParser.getText();
                                if (index == 0) {
                                    todayWeather.setHigh(high);
                                } else if (index == future1) {
                                    forecastWeather1.setHigh(high);
                                } else if (index == future2) {
                                    forecastWeather2.setHigh(high);
                                } else if (index == future3) {
                                    forecastWeather3.setHigh(high);
                                } else if (index == future4) {
                                    forecastWeather4.setHigh(high);
                                }
                                break;
                            case "low":
                                //往后走一个标签，走到标签的内容
                                eventType = xmlPullParser.next();
                                String low = xmlPullParser.getText();
                                if (index == 0) {
                                    todayWeather.setLow(low);
                                } else if (index == future1) {
                                    forecastWeather1.setLow(low);
                                } else if (index == future2) {
                                    forecastWeather2.setLow(low);
                                } else if (index == future3) {
                                    forecastWeather3.setLow(low);
                                } else if (index == future4) {
                                    forecastWeather4.setLow(low);
                                }
                                break;
                            case "type":
                                //往后走一个标签，走到标签的内容
                                eventType = xmlPullParser.next();
                                String type = xmlPullParser.getText();
                                if (index == 0) {
                                    todayWeather.setType(type);
                                } else if (index == future1) {
                                    forecastWeather1.setType(type);
                                } else if (index == future2) {
                                    forecastWeather2.setType(type);
                                } else if (index == future3) {
                                    forecastWeather3.setType(type);
                                } else if (index == future4) {
                                    forecastWeather4.setType(type);
                                }
                                break;
                            case "fengli":
                                //往后走一个标签，走到标签的内容
                                eventType = xmlPullParser.next();
                                String fengli = xmlPullParser.getText();
                                if (index == 0) {
                                    todayWeather.setFengli(fengli);
                                } else if (index == future1) {
                                    forecastWeather1.setFengli(fengli);
                                } else if (index == future2) {
                                    forecastWeather2.setFengli(fengli);
                                } else if (index == future3) {
                                    forecastWeather3.setFengli(fengli);
                                } else if (index == future4) {
                                    forecastWeather4.setFengli(fengli);
                                }
                                break;
                            case "city":
                                //往后走一个标签，走到标签的内容
                                eventType = xmlPullParser.next();
                                String city = xmlPullParser.getText();
                                todayWeather.setCity(city);

                                break;
                            case "updatetime":
                                //往后走一个标签，走到标签的内容
                                eventType = xmlPullParser.next();
                                String updatetime = xmlPullParser.getText();
                                todayWeather.setUpdatetime(updatetime);

                                break;
                            case "shidu":
                                //往后走一个标签，走到标签的内容
                                eventType = xmlPullParser.next();
                                String shidu = xmlPullParser.getText();
                                todayWeather.setShidu(shidu);
                                break;
                            case "wendu":
                                //往后走一个标签，走到标签的内容
                                eventType = xmlPullParser.next();
                                String wendu = xmlPullParser.getText();
                                todayWeather.setWendu(wendu);
                                break;
                            case "fengxiang":
                                //往后走一个标签，走到标签的内容
                                eventType = xmlPullParser.next();
                                String fengxiang = xmlPullParser.getText();
                                todayWeather.setFengxiang(fengxiang);

                                break;
                            case "pm25":
                                //往后走一个标签，走到标签的内容
                                eventType = xmlPullParser.next();
                                String pm25 = xmlPullParser.getText();
                                todayWeather.setPm25(pm25);

                                break;
                            case "quality":
                                //往后走一个标签，走到标签的内容
                                eventType = xmlPullParser.next();
                                String quality = xmlPullParser.getText();
                                todayWeather.setFengxiang(quality);

                                break;
                            default:
                                break;
                        }

                        break;
                    // 判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        String endTag = xmlPullParser.getName();
                        if (endTag.equals("forecast")) {
                            flag = true;//当预测天气解析完毕就终止解析
                        }

                        break;
                    default:
                        break;
                }
                if (flag) {//当预测天气解析完毕就终止解析
                    break;
                }
                // 进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }
            //构造天气信息
            weatherInfo.setTodayWeather(todayWeather);
            forecastWeathers.add(forecastWeather1);
            forecastWeathers.add(forecastWeather2);
            forecastWeathers.add(forecastWeather3);
            forecastWeathers.add(forecastWeather4);
            weatherInfo.setForecastWeathers(forecastWeathers);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return weatherInfo;
    }

    public class QueryWeatherTask extends AsyncTask<String, Integer, WeatherInfo> {

        @Override
        protected WeatherInfo doInBackground(String... strings) {
            WeatherInfo weatherInfo = queryWeatherCode(strings[0]);
            return weatherInfo;


        }

        @Override
        protected void onPostExecute(WeatherInfo weatherInfo) {
            Intent intent = new Intent();
            intent.setAction(Global.WEATHER_RECEIVED);
            intent.putExtra("weather", weatherInfo);
            sendBroadcast(intent);
        }
    }
}
