package cn.edu.pku.zhangqixun.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.edu.pku.zhangqixun.adapter.MyPagerFragmentAdapter;
import cn.edu.pku.zhangqixun.minweather.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherForecastFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.iv_dot1)
    ImageView ivDot1;
    @BindView(R.id.iv_dot2)
    ImageView ivDot2;
    Unbinder unbinder;

    private MyPagerFragmentAdapter myPagerFragmentAdapter;
    private List<Fragment> fragments;

    private String mParam1;
    private String mParam2;


    public WeatherForecastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherForecastFragment.
     */
    public static WeatherForecastFragment newInstance(String param1, String param2) {
        WeatherForecastFragment fragment = new WeatherForecastFragment();
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
        View view = inflater.inflate(R.layout.fragment_weather_forecast, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initDatas();
        initEvent();

    }

    private void initDatas() {
        Log.i("tag", "weforefre init data");
        //初始化fragments
        fragments = new ArrayList<>();
        fragments.add(ForecastFragment1.newInstance("", ""));
        fragments.add(ForecastFragment2.newInstance("", ""));
        //初始化myPagerAdapter
        myPagerFragmentAdapter = new MyPagerFragmentAdapter(getChildFragmentManager(),fragments);

    }

    private void initEvent() {
        //为viewpager设置适配器
        vp.setAdapter(myPagerFragmentAdapter);
        //为viewpager添加滑动监听
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //设置圆点
                setDots(position);
                //设置当前选中项
                vp.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vp.setCurrentItem(0);//默认显示第一页
        setDots(0);
    }

    /**
     * 设置圆点的选中情况
     * @param position
     */
    private void setDots(int position) {
        //先重置
        ivDot1.setSelected(false);
        ivDot2.setSelected(false);
        //再设置
        if (position == 0) {
            ivDot1.setSelected(true);
        }else {
            ivDot2.setSelected(true);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
