package cn.edu.pku.zhangqixun.minweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import cn.edu.pku.zhangqixun.widget.ClearEiditText;

public class SelectActivity extends Activity implements View.OnClickListener {
private ImageView mBackBtn;
    private TextView mTitleCity;
    private ListView lvTitles;
    private List<City> citys;
    private MyAdapter myAdapter;
    private ClearEiditText mClearEiditText;
    private List<City> filterCities;
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
        mClearEiditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterData(charSequence);
                lvTitles.setAdapter(myAdapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void filterData(CharSequence charSequence) {
        filterCities = new ArrayList<>();
        if (TextUtils.isEmpty(charSequence)) {
            filterCities.clear();
            filterCities.addAll(citys);
        }else {
            filterCities.clear();
            for (City city :
                    citys) {
                if (city.getCity().indexOf(charSequence.toString()) != -1) {
                    filterCities.add(city);
                }
            }
        }
        myAdapter.updateListView(filterCities);

    }

    private void initViews() {
        mBackBtn = (ImageView) findViewById(R.id.title_back);

        mTitleCity = (TextView) findViewById(R.id.title_name);
        lvTitles = (ListView) findViewById(R.id.title_list);
        mClearEiditText = (ClearEiditText) findViewById(R.id.cet_clear_text);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back:
               /* Intent i = new Intent();
                i.putExtra("cityCode", "101160101");
                setResult(RESULT_OK, i);*/
                finish();
                break;
            default:
                break;
        }
    }
}
