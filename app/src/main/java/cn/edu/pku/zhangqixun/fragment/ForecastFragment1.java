package cn.edu.pku.zhangqixun.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.edu.pku.zhangqixun.bean.ForecastWeather;
import cn.edu.pku.zhangqixun.bean.Global;
import cn.edu.pku.zhangqixun.bean.WeatherInfo;
import cn.edu.pku.zhangqixun.minweather.R;
import cn.edu.pku.zhangqixun.util.MyUtils;
import cn.edu.pku.zhangqixun.util.NetUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForecastFragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForecastFragment1 extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.tv_week_day1)
    TextView tvWeekDay1;
    @BindView(R.id.iv_type1)
    ImageView ivType1;
    @BindView(R.id.tv_wendu1)
    TextView tvWendu1;
    @BindView(R.id.tv_weather1)
    TextView tvWeather1;
    @BindView(R.id.tv_wind1)
    TextView tvWind1;
    @BindView(R.id.tv_week_day2)
    TextView tvWeekDay2;
    @BindView(R.id.iv_type2)
    ImageView ivType2;
    @BindView(R.id.tv_wendu2)
    TextView tvWendu2;
    @BindView(R.id.tv_weather2)
    TextView tvWeather2;
    @BindView(R.id.tv_wind2)
    TextView tvWind2;

    Unbinder unbinder;

    private String mParam1;
    private String mParam2;

    private List<ForecastWeather> forecastWeathers;
    public BroadcastReceiver mBroadcastReceiver;//天气信息广播接收器

    public ForecastFragment1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForecastFragment1.
     */
    public static ForecastFragment1 newInstance(String param1, String param2) {
        ForecastFragment1 fragment = new ForecastFragment1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forecast1, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //注册广播
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Serializable weather = intent.getSerializableExtra("weather");
                if (weather == null) {
                    Toast.makeText(getActivity(),"请求失败！",Toast.LENGTH_SHORT).show();
                }
                else {
                     forecastWeathers = ((WeatherInfo) weather).getForecastWeathers();
                    setDatas();
                }

            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Global.WEATHER_RECEIVED);
        getActivity().registerReceiver(mBroadcastReceiver, filter);

    }

    @Override
    public void onResume() {
        super.onResume();

//        initDatas();
    }

    private void initDatas() {
        forecastWeathers = new ArrayList<>();
        queryWeatherFromNet();
    }

    private void queryWeatherFromNet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = NetUtil.getFromNet(Global.URL_BASE+ Global.CITY_CODE);
                Message message = new Message();
                if (response.isEmpty()) {
                    //请求失败
                    message.what = FAIL;
                } else {
                    //请求成功
                    message.what = SUCCESS;
                    Bundle bundle = new Bundle();
                    bundle.putString("response", response);
                    message.setData(bundle);
                }
                    handler.sendMessage(message);


            }
        }).start();
    }

    private static final int FAIL = 0;
    private static final int SUCCESS = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FAIL:
                    Toast.makeText(getActivity(), "网络请求失败！", Toast.LENGTH_SHORT).show();
                    break;
                case SUCCESS:
                    Bundle data = msg.getData();
                    String response = data.getString("response");
                    //解析数据
                    parseXml(response);
                    //设置数据
                    setDatas();
                    break;
                default:
                    break;
            }


        }
    };

    /**
     * 设置数据到控件
     */
    private void setDatas() {
        if (forecastWeathers != null) {
            ForecastWeather weather1 = forecastWeathers.get(0);
            tvWeekDay1.setText(weather1.getDate());
            tvWendu1.setText(weather1.getLow()+"~"+weather1.getHigh());
            tvWeather1.setText(weather1.getType());
            tvWind1.setText(weather1.getFengli());

            ForecastWeather weather2 = forecastWeathers.get(1);
            tvWeekDay2.setText(weather2.getDate());
            tvWendu2.setText(weather2.getLow()+"~"+weather2.getHigh());
            tvWeather2.setText(weather2.getType());
            tvWind2.setText(weather2.getFengli());



            //设置图片
            String type1 = weather1.getType();
            String type2 = weather2.getType();
            MyUtils.setWeatherImg(ivType1,type1);
            MyUtils.setWeatherImg(ivType2,type2);
        }


    }

    /**
     * 解析xml数据
     *
     * @param response
     */
    private void parseXml(String response) {
        ForecastWeather weather1 =null;
        ForecastWeather weather2=null;
        int fore1 = 2,fore2 = 3;
        int index = 0;//记录预测的天数，第几天
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(response));
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

                            case "weather":
                                index++;//预测天数加一
                                if (index == fore1) {
                                    weather1 = new ForecastWeather();
                                }else if(index == fore2)
                                {
                                    weather2 = new ForecastWeather();
                                }

                                break;
                            case "date":
                                eventType = xmlPullParser.next();
                                String date = xmlPullParser.getText();
                                if (index == fore1) {
                                    weather1.setDate(date);
                                }else if(index == fore2)
                                {
                                    weather2.setDate(date);
                                }
                                break;
                            case "high":
                                eventType = xmlPullParser.next();
                                String high = xmlPullParser.getText();
                                if (index == fore1) {
                                    weather1.setHigh(high);
                                }else if(index == fore2)
                                {
                                    weather2.setHigh(high);
                                }
                                break;
                            case "low":
                                eventType = xmlPullParser.next();
                                String low = xmlPullParser.getText();
                                if (index == fore1) {
                                    weather1.setLow(low);
                                }else if(index == fore2)
                                {
                                    weather2.setLow(low);
                                }
                                break;
                            case "type":
                                eventType = xmlPullParser.next();
                                String type = xmlPullParser.getText();
                                if (index == fore1) {
                                    weather1.setType(type);
                                }else if(index == fore2)
                                {
                                    weather2.setType(type);
                                }
                                break;
                            case "fengli":
                                eventType = xmlPullParser.next();
                                if (weather1 != null) {
                                    String fengli = xmlPullParser.getText();
                                    if (index == fore1) {
                                        weather1.setFengli(fengli);
                                    }else if(index == fore2)
                                    {
                                        weather2.setFengli(fengli);
                                    }
                                }

                                break;
                        }

                        break;
                    // 判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        String endTag = xmlPullParser.getName();
                        if (endTag != null) {
                            switch (endTag) {

                                case "weather":
                                    //添加预测天气
                                    if (index == fore1) {
                                        Log.i("tag", "we1: " + weather1.toString());
                                        forecastWeathers.add(weather1);
                                    }else if(index == fore2)
                                    {
                                        Log.i("tag", "we2: " + weather2.toString());
                                        forecastWeathers.add(weather2);
                                    }
                                    break;
                            }
                        }
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
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
       getActivity().unregisterReceiver(mBroadcastReceiver);
    }
}
