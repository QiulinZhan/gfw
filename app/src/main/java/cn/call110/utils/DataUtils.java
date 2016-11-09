package cn.call110.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import cn.call110.model.JsonObject;
import cn.call110.model.Phone;
import cn.call110.utils.http.OkHttpUtils;
import cn.call110.utils.http.callback.StringCallback;
import okhttp3.Call;

/**
 * Created by Zane on 2016/11/9.
 */

public class DataUtils {
    public final static String user_info = "user_info";
    public static SharedPreferences sharedPreferences;
    public static Gson gson = new Gson();
    private static SharedPreferences preferences;
    public static List<Phone> phones = new ArrayList<>();
    public static void init(Context context){
        preferences = context.getSharedPreferences(user_info, Context.MODE_PRIVATE);
    }
    public static void saveDate(String key, Boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public static boolean getDate(String key) {
        return preferences.getBoolean(key, false);
    }
    public static void savePhoneList(String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("phone_list", value);
        editor.commit();
    }
    public static void initPhoneList() {
        if(!phones.isEmpty()){
            return;
        }
        String str = preferences.getString("phone_list", null);
        if(str == null){
            remoteData();
        }else{
            phones = gson.fromJson(str,JsonObject.class).getData();
        }

    }
    private static String url = "http://138.128.204.17:8081/compass/p/blackphone";
    public static void remoteData() {
        OkHttpUtils.get()
                .url(url)
                .addParams("username", "admin")
                .addParams("password", "123321")
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        String s = "";
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if(response != null && response.length() > 0) {
                            String str = response.substring(5, response.length() - 1);
                            JsonObject json = gson.fromJson(str, JsonObject.class);
                            if(!json.getData().isEmpty()) {
                                phones = json.getData();
                                savePhoneList(str);
                            }
                        }
                    }
                });
    }
}
