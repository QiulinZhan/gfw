package cn.call110.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import cn.call110.model.Phone;
import cn.call110.utils.DataUtils;

/**
 * Created by zhan on 2016/11/12.
 */

public class CallSmsSafeService extends Service {


    private WindowManager wm;
    private TextView tv;
    private Context mContext = null;
    private InnerSmsReceiver receiver;

    private TelephonyManager tm;
    private MyListener listener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class InnerSmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 检查发件人是否是黑名单号码，并且设置了短信拦截 or 全部拦截
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for(Object obj : objs) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[])obj);
                // 得到短信发件人
                String sender = smsMessage.getOriginatingAddress();
                if("15526820477".equals(sender)){
                    // 拦截短信
                    abortBroadcast();
                }
//                // 智能拦截演示
//                String body = smsMessage.getMessageBody();
//                if(body.contains("fapiao")) {
//                    abortBroadcast();
//                }
            }
        }

    }

    @Override
    public void onCreate() {
        receiver = new InnerSmsReceiver();
        // 动态注册一个广播接收器
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(receiver, filter);

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }
    @Override
    public void onDestroy() {

        unregisterReceiver(receiver);
        receiver = null;

        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }
    private class MyListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                //挂断
                case TelephonyManager.CALL_STATE_IDLE:
                    try{
                        removeWindow();
                    }catch (Exception e){}
                    break;
                //接听
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                //等待
                case TelephonyManager.CALL_STATE_RINGING:
                    boolean phoneHeadOff = DataUtils.getDate(DataUtils.phoneHeadOff);
                    if(phoneHeadOff){
                        for(Phone p : DataUtils.black){
                            if(incomingNumber.equals(p.getPhone())){
                                stopCall();
                                return;
                            }
                        }
                    }
                    boolean isAlert = DataUtils.getDate(DataUtils.alertSwitch);
                    if(isAlert){
                        for(Phone p : DataUtils.white){
                            if(incomingNumber.equals(p.getPhone())){
                                showWindow(p.getRemark());
                                return;
                            }
                        }
                        if(!phoneHeadOff){
                            for(Phone p : DataUtils.black){
                                if(incomingNumber.equals(p.getPhone())){
                                    showWindow(p.getRemark());
                                    return;
                                }
                            }
                        }
                    }
                    break;
            }
        }

    }
    public void stopCall() {
        try {
            Method getITelephonyMethod = TelephonyManager.class
                    .getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            ITelephony telephony = (ITelephony) getITelephonyMethod.invoke(tm,
                    (Object[]) null);
            // 拒接来电
            telephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showWindow(String str){
        tv = new TextView(mContext);
        tv.setTextSize(25);
        //得到连接
        wm = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //构造显示参数
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        //在所有窗体之上
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

        //设置失去焦点，不能被点击
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //高度宽度
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //透明
        params.format = PixelFormat.RGBA_8888;
        //显示
        tv.setText("安全提醒:" + str);
        wm.addView(tv, params);
    }
    public void removeWindow(){
        if(wm != null)
            wm.removeView(tv);
    }
}
