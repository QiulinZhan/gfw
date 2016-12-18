package cn.call110.zw.service.sms;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Zane on 2016/11/13.
 */

public class SmsHandler extends Handler {

    private SmsResponseCallback mCallback;

    public SmsHandler(SmsResponseCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
    }
}