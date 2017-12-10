package cn.edu.pku.zhangqixun.minweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

import cn.edu.pku.zhangqixun.bean.Global;
import cn.edu.pku.zhangqixun.bean.TodayWeather;
import cn.edu.pku.zhangqixun.bean.WeatherInfo;
import cn.edu.pku.zhangqixun.fragment.WeatherForecastFragment;
import cn.edu.pku.zhangqixun.service.MyService;
import cn.edu.pku.zhangqixun.util.NetUtil;
import cn.edu.pku.zhangqixun.util.SPFutils;

import static cn.edu.pku.zhangqixun.util.SPFutils.getStringData;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private ImageView mUpdateBtn;
    private ImageView mCitySelect;
    private TextView cityTv, timeTv, humidityTv,tvWendu,weekTv, pmDataTv,
            pmQualityTv,
            temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;
    private ProgressBar mProgressBar;
    private String currentCity;//当前城市

//    public static String cityCode;//全局的cityCode

    public BroadcastReceiver mBroadcastReceiver;//天气信息广播接收器

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
        //注册广播
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Serializable weather = intent.getSerializableExtra("weather");
                if (weather == null) {
                    Toast.makeText(MainActivity.this,"请求失败！",Toast.LENGTH_SHORT).show();
                }
                else {
                    TodayWeather todayWeather = ((WeatherInfo) weather).getTodayWeather();
                    updateTodayWeather(todayWeather);
                }
                //关闭进度条
                setUpdateProgressbar(false);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Global.WEATHER_RECEIVED);
        registerReceiver(mBroadcastReceiver, filter);

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

    /**
     * 启动查询天气的service
     */
    private void startWeatherService() {
        Intent intent = new Intent(this, MyService.class);
        startService(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            //启动查询天气的service
            startWeatherService();
            //显示进度条
            setUpdateProgressbar(true);
        } else {
            //显示本地的，上次显示的天气信息
            setWeatherFromSpf();
            //还原上次选择的城市码
            Global.CITY_CODE= SPFutils.getStringData(this,"main_city_code","101010100");
            Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        stopService(new Intent(this, MyService.class));
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
        tvWendu = (TextView) findViewById(R.id.tv_wendu);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);
    }

    /**
     * 点击事件
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_update_btn:
                SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                String cityCode = sharedPreferences.getString("main_city_code", "101010100");
                Log.d("myWeather", cityCode);
                if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                    Log.d("myWeather", "网络OK");
                    //启动查询天气的service
                    startWeatherService();
                    //显示更新进度条
                    setUpdateProgressbar(true);
                } else {
                    Log.d("myWeather", "网络挂了");
                    Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.title_city_manager://跳到城市选择页
                Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                intent.putExtra("current_city",currentCity);
                startActivity(intent);
//                startActivityForResult(intent,1);
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

  /**
   * 已不需要onActivityResult的方式来获取选择的城市，因为在城市选择页已经设定了全局的城市选择码
   * 而且onResume时会重新启动service查询，所以不需要在这里再次请求
  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode = data.getStringExtra("cityCode");
            Log.d("myWeather", "选择的城市代码为" + newCityCode);
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                //记录新的城市码
                Global.CITY_CODE = newCityCode;
                startWeatherService();
                //查询新选择城市的天气情况
//                queryWeatherCode(newCityCode);
                //显示进度条
                setUpdateProgressbar(true);
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }*/


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
        if (type == null) {
            return;
        }
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
        tvWendu.setText("温度：" + todayWeather.getWendu()+"°C");
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
}
