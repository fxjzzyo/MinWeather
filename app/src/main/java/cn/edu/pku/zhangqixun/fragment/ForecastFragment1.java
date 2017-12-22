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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
       getActivity().unregisterReceiver(mBroadcastReceiver);
    }
}
