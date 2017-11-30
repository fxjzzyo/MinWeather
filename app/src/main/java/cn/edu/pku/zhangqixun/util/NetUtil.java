package cn.edu.pku.zhangqixun.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.edu.pku.zhangqixun.bean.TodayWeather;

/**
 * Created by fxjzzyo on 2017/10/12.
 */

public class NetUtil {
    public static final int NETWORN_NONE = 0;
    public static final int NETWORN_WIFI = 1;
    public static final int NETWORN_MOBILE = 2;
    public static int getNetworkState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager)
                context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return NETWORN_NONE;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            return NETWORN_MOBILE;
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            return NETWORN_WIFI;
        }
        return NETWORN_NONE;
    }

    /**
     * get方式从网络获取数据
     * @param url
     * @return
     */
    public static String getFromNet(String url) {
        String responseStr = "";
        HttpURLConnection con = null;
        TodayWeather todayWeather = null;
        try {
            URL murl = new URL(url);
            con = (HttpURLConnection) murl.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            InputStream in = con.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                response.append(str);
            }
             responseStr = response.toString();
            Log.d("myWeather", "respon: "+responseStr);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return responseStr;
    }

}
