package cn.edu.pku.zhangqixun.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.zhangqixun.bean.City;
import cn.edu.pku.zhangqixun.bean.Global;
import cn.edu.pku.zhangqixun.db.CityDB;
import cn.edu.pku.zhangqixun.util.SPFutils;

/**
 * Created by fxjzzyo on 2017/11/1.
 */

public class MyApplication extends Application {
    private static final String TAG = "MyAPP";

    private static MyApplication myApplication;
    public CityDB mCityDB;
    private List<City> cityList;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "MyApplication--onCreate");
        this.myApplication = this;
        //初始化城市码，默认北京
        Global.CITY_CODE= SPFutils.getStringData(this,"main_city_code","101010100");
        mCityDB = openCityDB();
        initCityList();
    }

    /**
     * 初始化城市列表
     */
    private void initCityList() {
        cityList = new ArrayList<City>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //准备城市列表
                prepareCityList();
            }
        }).start();
    }

    /**
     * 准备城市列表
     * @return
     */
    private boolean prepareCityList() {
        cityList = mCityDB.getAllCity();
        /*for (int i = 0; i < cityList.size(); i++) {
            String cityName = cityList.get(i).getCity();
            String cityCode = cityList.get(i).getNumber();
            Log.d(TAG, "city name: " + cityName+" city code: "+cityCode);
        }
*/
        return true;
    }

    /**
     * 获取城市列表
     * @return
     */
    public List<City> getCityList() {
        return cityList;
    }

    /**
     * 获取应用实例
     * @return
     */
    public static MyApplication getInstance() {
        return myApplication;
    }

    /**
     * 打开数据库，将assets中的数据库文件拷贝到app文件中
     * @return
     */
    private CityDB openCityDB() {
        //创建路径
        String path = "/data" + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases1"
                + File.separator
                + CityDB.CITY_DB_NAME;
        File db = new File(path);
        Log.d(TAG, path);
        //如果数据库不存在，则进行拷贝
        if (!db.exists()) {
            Log.d(TAG,"数据库不存在");
            String pathfolder = "/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + getPackageName()
                    + File.separator + "databases1"
                    + File.separator;
            File dirFirstFolder = new File(pathfolder);
            //如果目标文件夹不存在，则创建文件夹
            if (!dirFirstFolder.exists()) {
                dirFirstFolder.mkdirs();
                Log.i("MyApp", "mkdirs");
            }
            Log.i("MyApp", "db is not exists");
            //通过输入输出流将city.db文件输出到指定文件db中
            try {
                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                //出错直接退出，防止创建不完整数据库
                System.exit(0);
            }
        }
        return new CityDB(this, path);
    }
}
