package cn.edu.pku.zhangqixun.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.zhangqixun.bean.City;

/**
 * Created by fxjzzyo on 2017/11/1.
 */

public class CityDB {
    public static final String CITY_DB_NAME = "city.db";
    private static final String CITY_TABLE_NAME = "main.city";
    private SQLiteDatabase db;

    public CityDB(Context context, String path) {
        db = context.openOrCreateDatabase(path, Context.MODE_PRIVATE, null);

    }

    public List<City> getAllCity() {
        List<City> list = new ArrayList<City>();
        Cursor c = db.rawQuery("SELECT * from " + CITY_TABLE_NAME, null);
        while (c.moveToNext()) {
            String province = c.getString(c.getColumnIndex("province"));
            String city = c.getString(c.getColumnIndex("city"));
            String number = c.getString(c.getColumnIndex("number"));
            String allPY = c.getString(c.getColumnIndex("allpy"));
            String allFirstPY = c.getString(c.getColumnIndex("allfirstpy"));
            String firstPY = c.getString(c.getColumnIndex("firstpy"));
            City item = new City(province, city, number, firstPY, allPY, allFirstPY);
            list.add(item);
        }
        return list;
    }

    /**
     * 根据城市名，获取城市码
     * @param city
     * @return
     */
    public String getCityCodeByCity(String city) {
        String code = "";
        Cursor c = db.query(CITY_TABLE_NAME,new String[]{"number"},"city like ?",new String[]{city},null,null,null);
        while (c.moveToNext()) {
            code = c.getString(c.getColumnIndex("number"));
        }
        return code;

    }
    /**
     * 根据城市名，获取省份
     * @param city
     * @return
     */
    public String getProvinceByCity(String city) {
        String province = "";
        Cursor c = db.query(CITY_TABLE_NAME,new String[]{"province"},"city like ?",new String[]{city},null,null,null);
        while (c.moveToNext()) {
            province = c.getString(c.getColumnIndex("province"));
        }
        return province;
    }
    /**
     * 根据省，获取省会城市
     * @param province
     * @return
     */
    public String getMainCityByProvince(String province) {
        String city = "";
        Cursor c = db.query(CITY_TABLE_NAME,new String[]{"city"},"province like ?",new String[]{province},null,null,null,"1");
        while (c.moveToNext()) {
            city = c.getString(c.getColumnIndex("city"));
        }
        return city;
    }
}
