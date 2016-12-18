package cn.call110.zw.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import cn.call110.zw.model.FraudPhone;
import cn.call110.zw.model.JsonObject;
import cn.call110.zw.model.Phone;
import cn.call110.zw.utils.http.OkHttpUtils;
import cn.call110.zw.utils.http.callback.StringCallback;
import io.realm.Realm;
import okhttp3.Call;

/**
 * Created by Zane on 2016/11/9.
 */

public class DataUtils {
    public final static String user_info = "user_info";
    public static Gson gson = new Gson();
    private static SharedPreferences preferences;
    public static final String phoneHeadOff = "phone_switch"; // 电话拦截
    public static final String smsHeadOff = "sms_Head_off"; // 短信拦截
    public static final String alertSwitch = "alert_switch"; // 来电提示
    public static void init(Context context){
        preferences = context.getSharedPreferences(user_info, Context.MODE_PRIVATE);
    }
    public static boolean getDate(String key) {
        return preferences.getBoolean(key, false);
    }

    public static void initPhoneList() {
        Realm realm = Realm.getDefaultInstance();
        if(realm.where(FraudPhone.class).count() < 1){
            remoteData();
        }
    }

    private static String url = "http://138.128.204.17:8081/zw/p/getblackphone";
    public static void remoteData() {
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if(StringUtils.isNotBlank(response)) {
                            JsonObject json = gson.fromJson(response, JsonObject.class);
                            Realm realm = Realm.getDefaultInstance();
                            realm.executeTransaction(r -> {
                                r.createOrUpdateAllFromJson(Phone.class, gson.toJson(json.getData()));
                            });
                            realm.close();
                        }
                    }
                });
    }

    public static void saveDate(String key, Boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
}
