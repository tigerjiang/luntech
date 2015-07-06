
package com.luntech.launcher;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

public class WeatherQueryManageImpl implements WeatherQueryManage {
    private final String TAG = "message";

    @Override
    public WeatherForm[] weatherquery(String CityId) {
        WeatherForm[] WF = new WeatherForm[3];
        // http://m.weather.com.cn/data/101070101.html
        String URL = "http://m.weather.com.cn/data/" + CityId + ".html";
        String Weather_Result = "";
        HttpGet httpRequest = new HttpGet(URL);
        // 获得HttpResponse对象
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 取得返回的数据
                Weather_Result = EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (Exception e) {
            Log.i(TAG, e.toString());
            WF[0] = new WeatherForm();
            WF[1] = new WeatherForm();
            WF[2] = new WeatherForm();
            return WF;
        }
        // 以下是对返回JSON数据的解析
        if (null != Weather_Result && !"".equals(Weather_Result)) {
            try {
                JSONObject JO = new JSONObject(Weather_Result).getJSONObject("weatherinfo");
                for (int i = 0; i < JO.length(); i++) {
                    WeatherForm weaf = new WeatherForm();
                    // 3个日期暂时都存放一天的
                    weaf.setName(JO.getString("city"));
                    weaf.setDdate(JO.getString("date_y"));
                    weaf.setWeek(JO.getString("week"));
                    weaf.setTemp(JO.getString("temp" + (i + 1)));
                    weaf.setWind(JO.getString("wind" + (i + 1)));
                    weaf.setWeather(JO.getString("weather" + (i + 1)));
                    WF[i] = weaf;
                }
            } catch (JSONException e) {
                Log.i(TAG, e.toString());
                WF[0] = new WeatherForm();
                WF[1] = new WeatherForm();
                WF[2] = new WeatherForm();
                return WF;
            }
        }
        return WF;
    }
}
