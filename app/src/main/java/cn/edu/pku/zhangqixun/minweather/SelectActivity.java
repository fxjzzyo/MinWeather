package cn.edu.pku.zhangqixun.minweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectActivity extends Activity implements View.OnClickListener {
private ImageView mBackBtn;
    private TextView mTitleCity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        mTitleCity = (TextView) findViewById(R.id.title_name);
        String current_city = getIntent().getStringExtra("current_city");
        mTitleCity.setText("当前城市："+current_city);

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
