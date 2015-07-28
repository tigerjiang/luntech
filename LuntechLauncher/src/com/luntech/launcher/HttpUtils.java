
package com.luntech.launcher;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {

    public static final String HTTP_HIDDEN_APP_URL = "http://zhiuitest.zhihetv.com/?m=hidden";
    public static final String HTTP_UPDATE_APP_URL = "http://zhiuitest.zhihetv.com/?m=ota_app";
    public static final String HTTP_CONFIG_APP_URL = "http://zhiuitest.zhihetv.com/?m=zhiui";
    public static final String HTTP_CONFIG_URL = "http://zhiuitest.zhihetv.com/?m=zhiui&a=config";

    /**
     * @param urlAll :请求接口
     * @param httpArg :参数
     * @return 返回结果
     */
    public static String requestResourcesFromServer(String httpUrl,String localFile) {
        BufferedReader reader = null;
        BufferedWriter writer =null;  
        String result = null;
        StringBuffer sbf = new StringBuffer();
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            writer = new BufferedWriter(new FileWriter(localFile));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                writer.write(strRead);
                sbf.append("\r\n");
            }
            writer.flush();
            reader.close();
            writer.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean checkConnectivity(Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            Logger.d("Network on");
            return true;
        }
        Logger.d("Network off");
        return false;
    }
}
