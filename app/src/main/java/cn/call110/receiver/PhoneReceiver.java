package cn.call110.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;
import java.util.Date;

import cn.call110.model.FraudPhone;
import cn.call110.model.Phone;
import cn.call110.service.PhoneService;
import cn.call110.utils.DataUtils;
import cn.call110.utils.DateUtils;
import io.realm.Realm;

public class PhoneReceiver extends BroadcastReceiver {
	private String phoneNumber = "";
	private TelephonyManager tm;
	private Context context;
	private static boolean isOutcalling = false;
	private boolean isAlert = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		if (intent.getAction() == Intent.ACTION_NEW_OUTGOING_CALL) {
			phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			phoneNumber = numberTrim(phoneNumber);
			if (phoneNumber != null) {
				isOutcalling = true;
				startService(context, phoneNumber, true);
			}
		}
		tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

	}

	PhoneStateListener listener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:
					// System.out.println("IDLE");
					if (isOutcalling)
						isOutcalling = false;
					else
						startService(context, phoneNumber, false);
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					boolean phoneHeadOff = DataUtils.getDate(DataUtils.phoneHeadOff);
					if(phoneHeadOff){
						for(Phone p : DataUtils.black){
							if(incomingNumber.equals(p.getPhone())){
								stopCall(incomingNumber, p.getRemark());
								return;
							}
						}
					}
					isAlert = DataUtils.getDate(DataUtils.alertSwitch);
					if(isAlert){
						for(Phone p : DataUtils.white){
							if(incomingNumber.equals(p.getPhone())){
								isOutcalling = false;
								phoneNumber = incomingNumber;
								startService(context, p.getRemark(), true);
								return;
							}
						}
						if(!phoneHeadOff){
							for(Phone p : DataUtils.black){
								if(incomingNumber.equals(p.getPhone())){
									isOutcalling = false;
									phoneNumber = incomingNumber;
									startService(context, p.getRemark(), true);
									return;
								}
							}
						}
					}
			}
		}
	};

	/**
	 * 启动服务
	 *
	 * @param context
	 * @param
	 */
	private void startService(Context context, String msg,
							  boolean operation) {
		if (msg != null && msg.length() > 0) {
			Intent intent = new Intent(context,PhoneService.class);
			intent.putExtra("msg",msg);
			intent.putExtra("showFlag", operation);
			context.startService(intent);
		}
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

	public void stopCall(String phone, String remark) {
		try {
			Method getITelephonyMethod = TelephonyManager.class
					.getDeclaredMethod("getITelephony", (Class[]) null);
			getITelephonyMethod.setAccessible(true);
			ITelephony telephony = (ITelephony) getITelephonyMethod.invoke(tm,
					(Object[]) null);
			// 拒接来电
			telephony.endCall();
			Realm realm = Realm.getDefaultInstance();
			realm.executeTransaction(e -> {
				FraudPhone fp = realm.createObject(FraudPhone.class);
				fp.setPhone(phone);
				fp.setCreateTime(DateUtils.formatDateTime(new Date()));
				fp.setRemark(remark);
			});
			realm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}