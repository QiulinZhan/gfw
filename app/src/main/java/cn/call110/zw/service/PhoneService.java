package cn.call110.zw.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import cn.call110.zw.ui.AlertUI;

/**
 * Created by Zane on 2016/11/13.
 */

public class PhoneService extends Service{
    private static WindowManager windowManager;
    private static AlertUI alert;
    private static WindowManager.LayoutParams params;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final String msg = intent.getStringExtra("msg");
        final boolean showFlag = intent.getBooleanExtra("showFlag", false);
        if (!showFlag) {
            if (windowManager != null) {
                windowManager.removeView(alert);
            }
            windowManager = null;
            alert = null;
            this.stopSelf();
        } else {
            if (windowManager == null) {
                windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                params = new WindowManager.LayoutParams();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    params.type = WindowManager.LayoutParams.TYPE_TOAST;
                } else {
                    params.type = WindowManager.LayoutParams.TYPE_PHONE;
                }
                params.format = PixelFormat.RGBA_8888;
                params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                params.gravity = Gravity.CENTER;
                params.y = -150;
                alert = new AlertUI(this);
                calcLength(msg);
                int height = alert.getMeasuredHeight();
                int width = alert.getMeasuredWidth();

                // 须指定宽度高度信息
                params.width = width;
                params.height = height;

                params.x = AlertUI.x;
                params.y = AlertUI.y;

                windowManager.addView(alert, params);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 计算悬浮窗的大小
     *
     * @param locationMsg
     */
    private void calcLength(String locationMsg) {
        alert.setText(locationMsg);
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        alert.measure(w, h);
    }

    /**
     * 更新位置
     */
    public static void refreshLocation() {
        params.x = AlertUI.x;
        params.y = AlertUI.y;
        windowManager.updateViewLayout(alert, params);
    }
}
