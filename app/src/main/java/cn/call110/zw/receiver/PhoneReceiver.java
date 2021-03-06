package cn.call110.zw.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Date;

import cn.call110.zw.model.FraudPhone;
import cn.call110.zw.model.Phone;
import cn.call110.zw.service.PhoneService;
import cn.call110.zw.utils.DataUtils;
import cn.call110.zw.utils.DateUtils;
import io.realm.Realm;

public class PhoneReceiver extends BroadcastReceiver {
	private TelephonyManager tm;
	private Context context;
	private boolean isShow = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		isShow = false;
		// 拨打电话
		if (intent.getAction() == Intent.ACTION_NEW_OUTGOING_CALL) {
			// 如果主动拨打诈骗电话 有提醒
			// 获取号码
			String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			// 号码处理
			phoneNumber = numberTrim(phoneNumber);

			if (phoneNumber != null) {
				Realm realm = Realm.getDefaultInstance();
				Phone phone = realm.copyFromRealm(realm.where(Phone.class).equalTo("phone", phoneNumber).findFirst());
				realm.close();
				if(phone != null){
					startService(context, phone.getRemark()); // 开启提示窗
				}
			}
		} else {
		// 如果是来电则注册电话状态监听
			tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		}
	}

	PhoneStateListener listener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
				// 挂断
				case TelephonyManager.CALL_STATE_IDLE:
					startService(context, ""); // 关闭提示窗
					break;
				// 电话接听
				case TelephonyManager.CALL_STATE_OFFHOOK:
					break;
				// 电话等待
				case TelephonyManager.CALL_STATE_RINGING:
					callRinging(incomingNumber);
			}
		}
	};

	private void callRinging(String incomingNumber) {
		boolean alertSwitch = DataUtils.getDate(DataUtils.alertSwitch); // 来电提示开关
		boolean phoneHeadOff = DataUtils.getDate(DataUtils.phoneHeadOff); // 电话拦截开关
		// 如果开启电话拦截
		if(alertSwitch || phoneHeadOff){
			try (Realm realm = Realm.getDefaultInstance()) {
				Phone phone = realm.copyFromRealm(realm.where(Phone.class).equalTo("phone", incomingNumber).findFirst());
				if(phoneHeadOff && phone.getType() == 1){ // 如果拦截开关开启且来电号码是黑名单则拦截
					Method getITelephonyMethod = TelephonyManager.class
							.getDeclaredMethod("getITelephony", (Class[]) null);
					getITelephonyMethod.setAccessible(true);
					ITelephony telephony = (ITelephony) getITelephonyMethod.invoke(tm,
							(Object[]) null);
					// 拒接来电
					telephony.endCall();
					// 新增已拦截电话记录

					realm.executeTransaction(e -> {
						FraudPhone fp = realm.createObject(FraudPhone.class, phone.getPhone());
						fp.setCreateTime(DateUtils.formatDateTime(new Date()));
						fp.setRemark(phone.getRemark());
					});
				} else if(alertSwitch) { // 来电号码是白名单，来电提示开关开启
					startService(context, phone.getRemark()); // 来电提示窗口开启
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 启动服务
	 * msg
	 * @param context
	 * @param msg 提示信息如（骚扰电话）
	 */
	private void startService(Context context, String msg) {
		boolean show = false;
		if (isShow) {
		} else if(StringUtils.isNotBlank(msg)) {
			show = true;
			isShow = true;
		} else {
			return;
		}
		Intent intent = new Intent(context,PhoneService.class);
		intent.putExtra("msg",msg);
		intent.putExtra("showFlag", show);
		context.startService(intent);
	}

	/**
	 * 电话号码处理
	 *
	 * @return
	 */
	private String numberTrim(String phoneNumber) {
		if (phoneNumber != null && phoneNumber.startsWith("+86"))
			phoneNumber = phoneNumber.substring(3);
		return phoneNumber;
	}

}