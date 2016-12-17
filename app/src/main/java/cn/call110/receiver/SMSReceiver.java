package cn.call110.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import java.util.ArrayList;
import java.util.List;

import cn.call110.sms.SmsHandler;
import cn.call110.sms.SmsObserver;
import cn.call110.sms.SmsResponseCallback;
import cn.call110.utils.DataUtils;

/**
 * Created by Zane on 2016/11/12.
 */

public class SMSReceiver extends BroadcastReceiver {

    private static SmsObserver smsObserver;
    @Override
    public void onReceive(Context context, Intent intent) {
// 检查发件人是否是黑名单号码，并且设置了短信拦截 or 全部拦截
//        Object[] objs = (Object[]) intent.getExtras().get("pdus");
//        for(Object obj : objs) {
//
//            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[])obj);
//            // 得到短信发件人
//            String sender = smsMessage.getOriginatingAddress();
//            smsMessage.
//            if("2".equals(mode) || "3".equals(mode)){
//                // 拦截短信
//                abortBroadcast();
//            }
//            // 智能拦截演示
//            String body = smsMessage.getMessageBody();
//            if(body.contains("fapiao")) {
//                abortBroadcast();
//            }
//        }

        if(DataUtils.getDate(DataUtils.smsHeadOff)){
            Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
            String format = intent.getStringExtra("format");
            int pduCount = messages.length;
            SmsMessage[] msgs = new SmsMessage[messages.length];
            List<String> phone = new ArrayList<>();
            for (Object obj : messages) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj, format);
                String sender = sms.getOriginatingAddress();
//                for(Phone p : DataUtils.black){
//                    if(sender.equals(p.getPhone())){
//                        abortBroadcast();
//                        phone.add(sender);
//                    }
//                }
                if(!phone.isEmpty()){
                    smsObserver = new SmsObserver(context, phone, new SmsHandler(new SmsResponseCallback() {
                        @Override
                        public void onCallbackSmsContent(String smsContent) {
                        }
                    }));
                    smsObserver.registerSMSObserver();
                }
            }
        }
    }
}
