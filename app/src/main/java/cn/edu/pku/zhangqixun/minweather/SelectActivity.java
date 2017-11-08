package cn.edu.pku.zhangqixun.minweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.zhangqixun.adapter.MyAdapter;
import cn.edu.pku.zhangqixun.app.MyApplication;
import cn.edu.pku.zhangqixun.bean.City;

public class SelectActivity extends Activity implements View.OnClickListener {
private ImageView mBackBtn;
    private TextView mTitleCity;
    private ListView lvTitles;
    private List<City> citys;
    private MyAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);


        initViews();
        initDatas();
        initEvent();

    }

    private void initDatas() {
        citys = new ArrayList<>();
        //从数据库读取数据
        citys = MyApplication.getInstance().getCityList();


    }

    private void initEvent() {
        mBackBtn.setOnClickListener(this);
        String current_city = getIntent().getStringExtra("current_city");
        mTitleCity.setText("当前城市："+current_city);
        //初始化适配器
        myAdapter = new MyAdapter(this,citys);
        //设置适配器
        lvTitles.setAdapter(myAdapter);
        //为listView设置点击某一项监听
        lvTitles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               //获取点击项对于的city
                City city = citys.get(i);
                String number = city.getNumber();
                //提示点击信息
                Toast.makeText(SelectActivity.this,"你点击了："+city.getCity(),Toast.LENGTH_SHORT).show();
                //将该city的code传回主界面
                Intent intent = new Intent();
                intent.putExtra("cityCode", number);
                setResult(RESULT_OK, intent);
                SelectActivity.this.finish();
            }
        });
    }

    private void initViews() {
        mBackBtn = (ImageView) findViewById(R.id.title_back);

        mTitleCity = (TextView) findViewById(R.id.title_name);
        lvTitles = (ListView) findViewById(R.id.title_list);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back:
                Intent i = new Intent();
                i.putExtra("cityCode", "101160101");
                setResult(RESULT_OK, i);
                finish();
                break;
            default:
                break;
        }
    }
}
