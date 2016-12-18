package cn.call110.zw.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Zane on 2016/11/20.
 */

public class DateUtils {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat dataTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String Y_M_D = "yyyy-MM-dd";
    public static final String Y_M_D_HMS = "yyyy-MM-dd HH:mm:ss";

    public static String formatDateTime(Date date) {
        return dataTimeFormat.format(date);
    }
    public static String formatDateTime(Long timestamp) {
        return dataTimeFormat.format(new Date(timestamp));
    }
}
