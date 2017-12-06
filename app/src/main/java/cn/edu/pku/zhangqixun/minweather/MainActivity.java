package cn.edu.pku.zhangqixun.minweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

import cn.edu.pku.zhangqixun.app.MyApplication;
import cn.edu.pku.zhangqixun.bean.TodayWeather;
import cn.edu.pku.zhangqixun.fragment.WeatherForecastFragment;
import cn.edu.pku.zhangqixun.util.NetUtil;
import cn.edu.pku.zhangqixun.util.SPFutils;

import static cn.edu.pku.zhangqixun.util.SPFutils.getStringData;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private ImageView mUpdateBtn;
    private ImageView mCitySelect;
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv,
            pmQualityTv,
            temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;
    private ProgressBar mProgressBar;
    private String currentCity;//当前城市

    public static String cityCode;//全局的cityCode

    //未来天气预测的fragment
    private WeatherForecastFragment forecastFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_info);
        //初始化控件
        initView();
        //初始化事件
        initEvent();

    }



    private void initEvent() {

        //显示上次显示的城市
         cityCode = SPFutils.getStringData(this,"main_city_code","101010100");
        Log.d("myWeather", cityCode);
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            queryWeatherCode(cityCode);
            //显示进度条
            setUpdateProgressbar(true);
        } else {
            //显示本地的，上次显示的天气信息
            setWeatherFromSpf();
            Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
        }

        mUpdateBtn.setOnClickListener(this);
        mCitySelect.setOnClickListener(this);
        //初始化forecastFragment
        forecastFragment = WeatherForecastFragment.newInstance("", "");
        //加载forecastFragment
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.ll_fragment, forecastFragment);
        fragmentTransaction.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    /**
     * 显示本地SharedPreferences的，上次的天气信息
     */
    private void setWeatherFromSpf() {
        TodayWeather todayWeather = new TodayWeather();
        todayWeather.setCity(getStringData(this,"city","北京"));//默认北京
        todayWeather.setUpdatetime(getStringData(this,"updatetime",todayWeather.getUpdatetime()));
        todayWeather.setWendu(getStringData(this, "wendu", todayWeather.getWendu()));
        todayWeather.setShidu(getStringData(this,"shidu",todayWeather.getShidu()));
        todayWeather.setPm25(getStringData(this,"pm25",todayWeather.getPm25()));
        todayWeather.setFengxiang(getStringData(this,"fengxiang",todayWeather.getFengxiang()));
        todayWeather.setFengli(getStringData(this,"fengli",todayWeather.getFengli()));
        todayWeather.setDate(getStringData(this,"date",todayWeather.getDate()));
        todayWeather.setHigh(getStringData(this,"high",todayWeather.getHigh()));
        todayWeather.setLow(getStringData(this,"low",todayWeather.getLow()));
        todayWeather.setType(getStringData(this,"type",todayWeather.getType()));
        //更新天气界面
        updateTodayWeather(todayWeather);

    }

    /**
     * 初始化控件
     */
    private void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.pb_update);
        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);

        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);



        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_update_btn:
                SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                String cityCode = sharedPreferences.getString("main_city_code", "101010100");
                Log.d("myWeather", cityCode);
                if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                    Log.d("myWeather", "网络OK");
                    //查询天气信息
                    queryWeatherCode(cityCode);
                    //显示更新进度条
                    setUpdateProgressbar(true);

                } else {
                    Log.d("myWeather", "网络挂了");
                    Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.title_city_manager:
                Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                intent.putExtra("current_city",currentCity);
                startActivityForResult(intent,1);
                break;
            default:
                break;
        }


    }

    /**
     * 设置进度条
     * @param show
     */
    private void setUpdateProgressbar(boolean show) {
        if (show) {
            mProgressBar.setVisibility(View.VISIBLE);
            mUpdateBtn.setVisibility(View.INVISIBLE);
            mUpdateBtn.setEnabled(false);
        }else {
            mProgressBar.setVisibility(View.GONE);
            mUpdateBtn.setVisibility(View.VISIBLE);
            mUpdateBtn.setEnabled(true);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode = data.getStringExtra("cityCode");
            Log.d("myWeather", "选择的城市代码为" + newCityCode);
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                //记录新的城市码
                MainActivity.cityCode = newCityCode;
                //查询新选择城市的天气情况
                queryWeatherCode(newCityCode);
                //显示进度条
                setUpdateProgressbar(true);
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 根据城市代码查询天气
     * @param cityCode
     */
    private void queryWeatherCode(final String cityCode) {
        //http://wthrcdn.etouch.cn/WeatherApi?citykey=101010100 兰州101160101
        final String address = MyApplication.URL_BASE + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                TodayWeather todayWeather = null;
                String response = NetUtil.getFromNet(MyApplication.URL_BASE+cityCode);
                if (!response.isEmpty()) {//如果返回不为空
                    //解析xml数据
                    todayWeather = parseXML(response);
                    //存储天气数据到sharedpreference
                    saveWeatherToSpf(todayWeather);
                    //存储当前城市代码
                    SPFutils.saveStringData(MainActivity.this,"main_city_code",cityCode);
                    if (todayWeather != null) {
                        Log.d("myWeather", todayWeather.toString());
                        //发送消息，更新UI
                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                    }
                }else {
                    Message msg = new Message();
                    msg.what = UPDATE_FAILED;
                    mHandler.sendMessage(msg);
                }



            }
        }).start();
    }

    /**
     *存储天气数据到sharedpreference
     * @param todayWeather
     */
    private void saveWeatherToSpf(TodayWeather todayWeather) {

        SPFutils.saveStringData(this,"city",todayWeather.getCity());
        SPFutils.saveStringData(this,"updatetime",todayWeather.getUpdatetime());
        SPFutils.saveStringData(this,"wendu",todayWeather.getWendu());
        SPFutils.saveStringData(this,"shidu",todayWeather.getShidu());
        SPFutils.saveStringData(this,"pm25",todayWeather.getPm25());
        SPFutils.saveStringData(this,"fengxiang",todayWeather.getFengxiang());
        SPFutils.saveStringData(this,"fengli",todayWeather.getFengli());
        SPFutils.saveStringData(this,"date",todayWeather.getDate());
        SPFutils.saveStringData(this,"high",todayWeather.getHigh());
        SPFutils.saveStringData(this,"low",todayWeather.getLow());
        SPFutils.saveStringData(this,"type",todayWeather.getType());

    }

    /**
     * 解析xml数据
     *
     * @param xmldata
     */
    private TodayWeather parseXML(String xmldata) {
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
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
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {

                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "city: " + xmlPullParser.getText());
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "updatetime: " + xmlPullParser.getText());
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "shidu: " + xmlPullParser.getText());
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "wendu: " + xmlPullParser.getText());
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "pm25: " + xmlPullParser.getText());
                                String pm25 = xmlPullParser.getText();
                                //设置pm2.5
                                todayWeather.setPm25(pm25);

                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "quality: " + xmlPullParser.getText());
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "fengxiang: " + xmlPullParser.getText());
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "fengli: " + xmlPullParser.getText());
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "date: " + xmlPullParser.getText());
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "high: " + xmlPullParser.getText());
                                todayWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "low: " + xmlPullParser.getText());
                                todayWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "type: " + xmlPullParser.getText());
                                String type = xmlPullParser.getText();
                                //设置天气类型（晴 雨...）
                                todayWeather.setType(type);
                                typeCount++;
                            }
                        }
                        break;
                    // 判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                // 进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }

    /**
     * 根据pm2.5的值设置图片
     * @param pm25
     */
    private void setPM25Img(String pm25) {
        if(pm25==null)
        {
            return;
        }
        int ipm25 = Integer.parseInt(pm25);

        if(ipm25>=0 && ipm25<=50){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
        }else if(ipm25>=51 && ipm25<=100){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
        }else if(ipm25>=101 &&ipm25<=150){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
        }else if(ipm25>=151 &&ipm25<=200){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
        }else if(ipm25>=201 && ipm25<=300)
        {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
        }else {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
        }
    }

    /**
     * 根据天气类型设置天气图片
     * @param type
     */
    private void setWeatherImg(String type) {
        switch (type) {
            case "晴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "阴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "多云":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雾":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "沙尘暴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "小雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "雷阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "中雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
            case "大雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "特大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "小雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "阵雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "中雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "大雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "暴雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "雨夹雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            default:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
        }
    }

    /**
     * 更新天气
     *
     * @param todayWeather
     */
    private void updateTodayWeather(TodayWeather todayWeather) {
        currentCity = todayWeather.getCity();
        city_name_Tv.setText(currentCity + "天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime() + "发布");
        humidityTv.setText("湿度：" + todayWeather.getShidu());
        if (todayWeather.getPm25() == null) {//当pm2.5没有值时，显示未知
            pmDataTv.setText("未知");
        }else {
            pmDataTv.setText(todayWeather.getPm25());
        }
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText( todayWeather.getLow()+ "~" +todayWeather.getHigh() );
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:" + todayWeather.getFengli());

        //设置天气图片
        setWeatherImg(todayWeather.getType());
        //设置pm2.5的图片
        setPM25Img(todayWeather.getPm25());

        Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();


    }

    private static final int UPDATE_TODAY_WEATHER = 1;
    private static final int UPDATE_FAILED = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            //隐藏进度条
            setUpdateProgressbar(false);
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                case UPDATE_FAILED:
                    Toast.makeText(MainActivity.this,"请求失败！",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
}
