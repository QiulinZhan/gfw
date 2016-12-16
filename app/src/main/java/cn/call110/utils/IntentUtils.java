package cn.call110.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import cn.call110.R;

/**
 * Created by zhan on 2016/11/8.
 */

public class IntentUtils {
    public static void launch(Context from, Class to) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeCustomAnimation(from,
                        R.anim.head_in, R.anim.head_out);
        Intent intent = new Intent(from, to);
        ActivityCompat.startActivity((Activity) from, intent, options.toBundle());
    }
}
