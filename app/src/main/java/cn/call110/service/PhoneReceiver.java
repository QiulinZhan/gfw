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
	private static boolean phoneHeadOff = DataUtils.getDate("phone_switch");
	private static boolean smsHeadOff = DataUtils.getDate("sms_HeadOff");
	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			// 如果是去电（拨出）
		} else {
			tv = new TextView(context);
			tv.setTextSize(25);
			//得到连接
			tv.setText("超级大骗子");
			wm = (WindowManager) context.getApplicationContext()
					.getSystemService(Context.WINDOW_SERVICE);
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
			wm.addView(tv, params);

			manager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
			// 设置一个监听器
			manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		}
	}
	
	PhoneStateListener listener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// state 当前状态 incomingNumber,貌似没有去电的API
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
				//手机空闲了
				case TelephonyManager.CALL_STATE_IDLE:
					break;
				//电话被挂起
				case TelephonyManager.CALL_STATE_OFFHOOK:
					break;
				// 当电话呼入时
				case TelephonyManager.CALL_STATE_RINGING:

					if(fuckIt(incomingNumber)){
						stopCall();
					}else{
						String i = DataUtils.black.toString();
						System.out.print(i);
					}
				break;
			}
		}
	};
	public boolean fuckIt(String number){
		if(!DataUtils.getDate("phone_switch")){
			return false;
		}
		for(Phone p : DataUtils.black){
			if(number.equals(p.getPhone()))
				return true;
		}
		return false;
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
}