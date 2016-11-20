package cn.call110;

import android.app.Application;
import android.util.TypedValue;

import com.zhy.autolayout.config.AutoLayoutConifg;

import java.util.concurrent.TimeUnit;
import cn.call110.utils.DataUtils;
import cn.call110.utils.http.OkHttpUtils;
import io.realm.Realm;
import okhttp3.OkHttpClient;

/**
 * Created by Zane on 2016/11/9.
 */

public class AApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DataUtils.init(this);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
        Realm.init(this);
        AutoLayoutConifg.getInstance().useDeviceSize().init(this);
    }
}
