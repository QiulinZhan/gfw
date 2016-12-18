package cn.call110.zw.ui;

import android.Manifest;
import android.content.Intent;
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
import cn.call110.zw.adapter.PhoneAdapter;
import cn.call110.zw.model.FraudPhone;
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
public class SettingActivity extends AutoLayoutActivity {
    private Switch phoneSwitch;
    private Switch alertSwitch;
    private SwipeMenuListView mListView;
    private RealmResults<FraudPhone> dataList;
    private PhoneAdapter madapter;
    private Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.title);
        toolbar.setTitle("");
        title.setText(R.string.setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(e -> this.onBackPressed());

        alertSwitch = (Switch) findViewById(R.id.alertSwitch);
        alertSwitch.setChecked(DataUtils.getDate(DataUtils.alertSwitch));
        alertSwitch.setOnCheckedChangeListener((e, v) -> {
            if(v){
                SettingActivityPermissionsDispatcher.setAlertWithCheck(this);
            }else{
                DataUtils.saveDate(DataUtils.alertSwitch, false);
            }
        });
        phoneSwitch = (Switch) findViewById(R.id.phoneSwitch);
        phoneSwitch.setChecked(DataUtils.getDate(DataUtils.phoneHeadOff));
        phoneSwitch.setOnCheckedChangeListener((e, v) -> {
            if(v){
                SettingActivityPermissionsDispatcher.setPhoneWithCheck(this);
            }else{
                DataUtils.saveDate(DataUtils.phoneHeadOff, false);
            }
        });

        mListView = (SwipeMenuListView) findViewById(R.id.list);
        realm = Realm.getDefaultInstance();
        dataList = realm.where(FraudPhone.class).findAll();


        madapter = new PhoneAdapter(this, dataList);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SettingActivityPermissionsDispatcher.onActivityResult(this, requestCode);
    }
    // 来电提示权限返回


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SettingActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE})
    void setPhone() {
        change(SwitchType.PHONE, true);
//        Toast.makeText(this, "已开启拦截服务", Toast.LENGTH_SHORT).show();
    }

    @OnShowRationale({Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE})
    void requestPhone(PermissionRequest request) {
        showRationaleDialog(R.string.phone_service, request);
//        Toast.makeText(this, "请求电话", Toast.LENGTH_SHORT).show();
    }

    @OnPermissionDenied({Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE})
    void phoneDenied() {
        change(SwitchType.PHONE, false);
//        Toast.makeText(this, "电话关闭", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE})
    void phoneNotAsk() {
        change(SwitchType.PHONE, true);
//        Toast.makeText(this, "电话不在询问", Toast.LENGTH_SHORT).show();
    }

    @NeedsPermission(Manifest.permission.SYSTEM_ALERT_WINDOW)
    void setAlert() {
        change(SwitchType.ALERT, true);
//        Toast.makeText(this, "开启弹窗", Toast.LENGTH_SHORT).show();
    }

    @OnShowRationale(Manifest.permission.SYSTEM_ALERT_WINDOW)
    void requestAlert(PermissionRequest request) {
        showRationaleDialog(R.string.alert_service, request);
//        Toast.makeText(this, "请求弹窗", Toast.LENGTH_SHORT).show();
    }

    @OnPermissionDenied(Manifest.permission.SYSTEM_ALERT_WINDOW)
    void alertDenied() {
        change(SwitchType.ALERT, false);
//        Toast.makeText(this, "弹窗取消", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.SYSTEM_ALERT_WINDOW)
    void alertNotAsk() {
        change(SwitchType.ALERT, true);
    }

    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton("允许", (dialog, which) -> request.proceed())
                .setNegativeButton("拒绝", (dialog, which) -> request.cancel())
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    void change(SwitchType type, boolean value){
        switch(type){
            case PHONE:
                phoneSwitch.setChecked(value);
                DataUtils.saveDate(DataUtils.phoneHeadOff, value);
                break;
            default:
                alertSwitch.setChecked(value);
                DataUtils.saveDate(DataUtils.alertSwitch, value);
                break;
        }
    }
    private enum SwitchType{
        PHONE, ALERT
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