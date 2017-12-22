package cn.edu.pku.zhangqixun.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.edu.pku.zhangqixun.bean.ForecastWeather;
import cn.edu.pku.zhangqixun.bean.Global;
import cn.edu.pku.zhangqixun.bean.WeatherInfo;
import cn.edu.pku.zhangqixun.minweather.R;
import cn.edu.pku.zhangqixun.util.MyUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForecastFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForecastFragment2 extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.tv_week_day3)
    TextView tvWeekDay3;
    @BindView(R.id.iv_type3)
    ImageView ivType3;
    @BindView(R.id.tv_wendu3)
    TextView tvWendu3;
    @BindView(R.id.tv_weather3)
    TextView tvWeather3;
    @BindView(R.id.tv_wind3)
    TextView tvWind3;


    @BindView(R.id.tv_week_day4)
    TextView tvWeekDay4;
    @BindView(R.id.iv_type4)
    ImageView ivType4;
    @BindView(R.id.tv_wendu4)
    TextView tvWendu4;
    @BindView(R.id.tv_weather4)
    TextView tvWeather4;
    @BindView(R.id.tv_wind4)
    TextView tvWind4;

    Unbinder unbinder;
    private String mParam1;
    private String mParam2;
    private List<ForecastWeather> forecastWeathers;
    public BroadcastReceiver mBroadcastReceiver;//天气信息广播接收器
    public ForecastFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForecastFragment2.
     */
    public static ForecastFragment2 newInstance(String param1, String param2) {
        ForecastFragment2 fragment = new ForecastFragment2();
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
        View view = inflater.inflate(R.layout.fragment_forecast2, container, false);
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


    /**
     * 设置数据到控件
     */
    private void setDatas() {
        if (forecastWeathers != null) {
            ForecastWeather weather3 = forecastWeathers.get(2);
            tvWeekDay3.setText(weather3.getDate());
            tvWendu3.setText(weather3.getLow()+"~"+weather3.getHigh());
            tvWeather3.setText(weather3.getType());
            tvWind3.setText(weather3.getFengli());

            ForecastWeather weather4 = forecastWeathers.get(3);
            tvWeekDay4.setText(weather4.getDate());
            tvWendu4.setText(weather4.getLow()+"~"+weather4.getHigh());
            tvWeather4.setText(weather4.getType());
            tvWind4.setText(weather4.getFengli());

            //设置图片
            String type1 = weather3.getType();
            String type2 = weather4.getType();
            MyUtils.setWeatherImg(ivType3,type1);
            MyUtils.setWeatherImg(ivType4,type2);
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }
}
