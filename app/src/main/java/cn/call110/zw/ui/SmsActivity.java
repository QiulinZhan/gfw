package cn.call110.zw.ui;

import android.Manifest;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.widget.Switch;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.zhy.autolayout.AutoLayoutActivity;

import cn.call110.zw.R;
import cn.call110.zw.adapter.SmsAdapter;
import cn.call110.zw.model.FraudSms;
import cn.call110.zw.utils.DataUtils;
import io.realm.Realm;
import io.realm.RealmResults;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class SmsActivity extends AutoLayoutActivity {
    private Switch smsSwitch;
    private SwipeMenuListView mListView;
    private RealmResults<FraudSms> dataList;
    private SmsAdapter madapter;
    private Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.title);
        toolbar.setTitle("");
        title.setText(R.string.sms_setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(e -> this.onBackPressed());

        smsSwitch = (Switch) findViewById(R.id.smsSwitch);
        smsSwitch.setChecked(DataUtils.getDate(DataUtils.smsHeadOff));
        smsSwitch.setOnCheckedChangeListener((e, v) -> {
            if(v){
                SmsActivityPermissionsDispatcher.setSmsWithCheck(this);
            }else{
                DataUtils.saveDate(DataUtils.smsHeadOff, false);
            }
        });
        mListView = (SwipeMenuListView) findViewById(R.id.list);
        realm = Realm.getDefaultInstance();
        dataList = realm.where(FraudSms.class).findAll();

        madapter = new SmsAdapter(this, dataList);
        mListView.setAdapter(madapter);
        mListView.setMenuCreator(m -> {
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                    0x3F, 0x25)));
            deleteItem.setWidth(dp2px(90));
            deleteItem.setIcon(R.mipmap.ic_delete);
            m.addMenuItem(deleteItem);
        });

        // step 2. listener item click event
        mListView.setOnMenuItemClickListener((position, menu, index) -> {
            switch (index) {
                case 0:
                    realm.executeTransaction(e -> {
                        dataList.deleteFromRealm(position);
                        madapter.notifyDataSetChanged();
                    });
                    break;
                case 1:
                    // delete
//					delete(item);

                    break;
            }
            return false;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SmsActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    //    @NeedsPermission({"android.permission.WRITE_SMS", Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.BROADCAST_SMS})
    @NeedsPermission({Manifest.permission.READ_SMS, "android.permission.WRITE_SMS", Manifest.permission.RECEIVE_SMS, Manifest.permission.BROADCAST_SMS})
    void setSms() {
        change(true);
//        Toast.makeText(this, "短信开启", Toast.LENGTH_SHORT).show();
    }

    //    @OnShowRationale({"android.permission.WRITE_SMS", Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.BROADCAST_SMS})
    @OnShowRationale({Manifest.permission.READ_SMS, "android.permission.WRITE_SMS", Manifest.permission.RECEIVE_SMS, Manifest.permission.BROADCAST_SMS})
    void requestSms(PermissionRequest request) {
        showRationaleDialog(R.string.sms_service, request);
//        Toast.makeText(this, "短信服务说明", Toast.LENGTH_SHORT).show();
    }

    //    @OnPermissionDenied({"android.permission.WRITE_SMS", Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.BROADCAST_SMS})
    @OnPermissionDenied({Manifest.permission.READ_SMS, "android.permission.WRITE_SMS", Manifest.permission.RECEIVE_SMS, Manifest.permission.BROADCAST_SMS})
    void smsDenied() {
        change(false);
//        Toast.makeText(this, "短信取消", Toast.LENGTH_SHORT).show();
    }

    //    @OnNeverAskAgain({"android.permission.WRITE_SMS", Manifest.per
// .mission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.BROADCAST_SMS})
    @OnNeverAskAgain({Manifest.permission.READ_SMS, "android.permission.WRITE_SMS", Manifest.permission.RECEIVE_SMS, Manifest.permission.BROADCAST_SMS})
    void smsNotAsk() {
        change(true);
//        Toast.makeText(this, "短信不在询问", Toast.LENGTH_SHORT).show();
    }

    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton("允许", (dialog, which) -> request.proceed())
                .setNegativeButton("拒绝", (dialog, which) -> request.cancel())
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    void change(boolean value){
        smsSwitch.setChecked(value);
        DataUtils.saveDate(DataUtils.smsHeadOff, value);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
