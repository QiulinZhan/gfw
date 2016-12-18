package cn.call110.zw.service.sms;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import java.util.Date;
import java.util.List;

import cn.call110.zw.model.FraudSms;
import cn.call110.zw.utils.DateUtils;
import io.realm.Realm;

/**
 * Created by Zane on 2016/11/13.
 */

public class SmsObserver extends ContentObserver {
    private Context mContext;
    private Handler mHandler;
    private List<String> phoneList;
    public SmsObserver(Context context, List<String> phoneList, Handler handler) {
        super(handler);
        this.mContext = context;
        this.phoneList = phoneList;
    }


    /***
     * 注册短信变化观察者
     *
     * @see [类、类#方法、类#成员]
     */
    public void registerSMSObserver() {
        Uri uri = Uri.parse("content://sms");
        if (mContext != null) {
            mContext.getContentResolver().registerContentObserver(uri, true, this);
        }
    }

    /***
     * 注销短信变化观察者
     *
     * @see [类、类#方法、类#成员]
     */
    public void unregisterSMSObserver() {
        if (mContext != null) {
            mContext.getContentResolver().unregisterContentObserver(this);
        }
        if (mHandler != null) {
            mHandler = null;
        }
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        if (uri.toString().equals("content://sms/raw")) {
            return;
        }
        Uri inboxUri = Uri.parse("content://sms/inbox");//收件箱
        try {
            ContentResolver CR = mContext.getContentResolver();
            Cursor c = CR.query(inboxUri, null, null, null, "date desc");
            if (c != null) {
                if (c.moveToFirst()) {
                    String address = c.getString(c.getColumnIndex("address"));
//                    if(phoneList.stream().filter(e->e.equals(address)).findFirst().isPresent()){
                    String body = c.getString(c.getColumnIndex("body"));
                    int id = c.getInt(c.getColumnIndex("_id"));

                    int cout =  CR.delete(Uri.parse("content://sms"), "_id=" + id, null);
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(e -> {
                        FraudSms fp = realm.createObject(FraudSms.class);
                        fp.setAddress(address);
                        fp.setContent(body);
                        fp.setCreateTime(DateUtils.formatDateTime(new Date()));
//                            fp.setRemark(remark);
                    });
                    realm.close();
//                    }
//                    Log.i(getClass().getName(), "发件人为：" + address + " " + "短信内容为：" + body);
                }
                c.close();
                unregisterSMSObserver();
            }
        } catch (SecurityException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}