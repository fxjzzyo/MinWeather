package cn.edu.pku.zhangqixun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.edu.pku.zhangqixun.bean.City;
import cn.edu.pku.zhangqixun.minweather.R;

/**
 * Created by fxjzzyo on 2017/11/8.
 */

public class MyAdapter extends BaseAdapter {
    private List<City> cities;
    private Context mContext;
    private LayoutInflater layoutInflater;

    public MyAdapter(Context context, List<City> cities) {
        this.cities = cities;
        this.mContext = context;
        this.layoutInflater = layoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return cities.size();
    }

    @Override
    public Object getItem(int i) {
        return cities.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void updateListView(List<City> cities) {
        this.cities.clear();
        this.cities.addAll(cities);
        this.notifyDataSetChanged();

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            //引入条目布局
            view = layoutInflater.inflate(R.layout.city_list_item, null);
            //绑定控件到viewHolder
            viewHolder.tvCity = (TextView) view.findViewById(R.id.tv_city_name);
            viewHolder.tvCode = (TextView) view.findViewById(R.id.tv_city_code);
            //将viewHolder设置到viewTag
            view.setTag(viewHolder);
        } else {
            //通过tag从view中获取viewholder
            viewHolder = (ViewHolder) view.getTag();
        }
        //获取一个city
        City city = cities.get(i);
        //将city信息设置到viewholder持有的控件上
        viewHolder.tvCity.setText(city.getCity());
        viewHolder.tvCode.setText(city.getNumber());

        return view;
    }

    /**
     * viewHolder类，用来持有view，提升界面刷新效率
     */
    class ViewHolder {
        TextView tvCity;
        TextView tvCode;

    }
}
