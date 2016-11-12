package cn.call110.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.call110.model.Phone;
import cn.call110.ui.SettingActivity;
import cn.call110.utils.DataUtils;

public class PhoneReceiver extends BroadcastReceiver {
    private static TelephonyManager manager;
	private static WindowManager wm;
	private static TextView tv;
	private static Context mContext = null;
	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context.getApplicationContext();
		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			// 如果是去电（拨出）
		} else {

			manager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
			// 设置一个监听器
			manager.listen(new PhoneStateListener() {
				@Override
				public void onCallStateChanged(int state, String incomingNumber) {
					// state 当前状态 incomingNumber,貌似没有去电的API
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
			}, PhoneStateListener.LISTEN_CALL_STATE);
		}
	}

	public void stopCall() {
        try {
            Method getITelephonyMethod = TelephonyManager.class
                    .getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            ITelephony telephony = (ITelephony) getITelephonyMethod.invoke(manager,
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
		wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
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
		wm.removeViewImmediate(tv);
	}
}