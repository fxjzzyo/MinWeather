package cn.edu.pku.zhangqixun.minweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.pku.zhangqixun.adapter.MyPagerAdapter;
import cn.edu.pku.zhangqixun.util.SPFutils;

public class GuideActivity extends Activity {

    @BindView(R.id.vp_guide)
    ViewPager vpGuide;
    @BindView(R.id.iv_dot1)
    ImageView ivDot1;
    @BindView(R.id.iv_dot2)
    ImageView ivDot2;
    @BindView(R.id.iv_dot3)
    ImageView ivDot3;

    private MyPagerAdapter myPagerAdapter;
    private List<View> views;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        //检查是否是第一次进入
        String isFirst = SPFutils.getStringData(this, "isFirst", "");
        if (!isFirst.isEmpty()) {
            //如果不是第一次进入，则直接跳转到mainactivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
            return;
        }
        //否则，展示引导页
        views = new ArrayList<>();
        View view1 = LayoutInflater.from(this).inflate(R.layout.guide1_layout, null);
        View view2 = LayoutInflater.from(this).inflate(R.layout.guide2_layout, null);
        View view3 = LayoutInflater.from(this).inflate(R.layout.guide3_layout, null);
        View btnStart = view3.findViewById(R.id.btn_enjoy);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //进入mainactivity
                Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                startActivity(intent);
                //记录第一次进入
                SPFutils.saveStringData(GuideActivity.this,"isFirst","isFirst");
                GuideActivity.this.finish();
            }
        });
        views.add(view1);
        views.add(view2);
        views.add(view3);
        myPagerAdapter = new MyPagerAdapter(views);

        vpGuide.setAdapter(myPagerAdapter);
        vpGuide.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vpGuide.setCurrentItem(0);
        setDots(0);
    }

    private void setDots(int position) {
        ivDot1.setSelected(false);
        ivDot2.setSelected(false);
        ivDot3.setSelected(false);
        switch (position) {
            case 0:
                ivDot1.setSelected(true);
                break;
            case 1:
                ivDot2.setSelected(true);
                break;
            case 2:
                ivDot3.setSelected(true);
                break;
            default:
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
