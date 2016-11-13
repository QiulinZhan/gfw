package cn.call110.sms;

/**
 * Created by Zane on 2016/11/13.
 */

public interface SmsResponseCallback {

    /**
     * 返回短信内容
     *
     * @param smsContent
     * @see [类、类#方法、类#成员]
     */
    void onCallbackSmsContent(String smsContent);
}
