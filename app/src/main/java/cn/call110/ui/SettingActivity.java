package cn.call110.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;

import cn.call110.R;
import cn.call110.utils.DataUtils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class SettingActivity extends AppCompatActivity {
    Switch phoneSwitch;
    Switch smsSwitch;
    Switch alertSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(e -> this.onBackPressed());
        Button button = (Button) findViewById(R.id.update);
        button.setOnClickListener(e -> {
            DataUtils.remoteData();
        });
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
        smsSwitch = (Switch) findViewById(R.id.smsSwitch);
        smsSwitch.setChecked(DataUtils.getDate(DataUtils.smsHeadOff));
        smsSwitch.setOnCheckedChangeListener((e, v) -> {
            if(v){
                SettingActivityPermissionsDispatcher.setSmsWithCheck(this);
            }else{
                DataUtils.saveDate(DataUtils.smsHeadOff, false);
            }
        });

//        phoneSwitch.setOnClickListener(e -> {
//            DataUtils.saveDate("phone_switch", !phoneSwitch.isChecked());
//        });
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

    @NeedsPermission({Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE})
    void setPhone() {
        change(SwitchType.PHONE, true);
        Toast.makeText(this, "已开启拦截服务", Toast.LENGTH_SHORT).show();
    }

    @OnShowRationale({Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE})
    void requestPhone(PermissionRequest request) {
        Toast.makeText(this, "请求电话", Toast.LENGTH_SHORT).show();
        showRationaleDialog(R.string.phone_service, request);
    }

    @OnPermissionDenied({Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE})
    void phoneDenied() {
        change(SwitchType.PHONE, false);
        Toast.makeText(this, "电话关闭", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE})
    void phoneNotAsk() {
        Toast.makeText(this, "电话不在询问", Toast.LENGTH_SHORT).show();
    }

    @NeedsPermission(Manifest.permission.SYSTEM_ALERT_WINDOW)
    void setAlert() {
        change(SwitchType.ALERT, true);
        Toast.makeText(this, "开启弹窗", Toast.LENGTH_SHORT).show();
    }

    @OnShowRationale(Manifest.permission.SYSTEM_ALERT_WINDOW)
    void requestAlert(PermissionRequest request) {
        Toast.makeText(this, "请求弹窗", Toast.LENGTH_SHORT).show();
        showRationaleDialog(R.string.phone_service, request);
    }

    @OnPermissionDenied(Manifest.permission.SYSTEM_ALERT_WINDOW)
    void alertDenied() {
        change(SwitchType.ALERT, false);
        Toast.makeText(this, "弹窗取消", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.SYSTEM_ALERT_WINDOW)
    void alertNotAsk() {
        Toast.makeText(this, "弹窗不在询问", Toast.LENGTH_SHORT).show();
    }
    @NeedsPermission({Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.BROADCAST_SMS})
    void setSms() {
        change(SwitchType.SMS, true);
        Toast.makeText(this, "短信开启", Toast.LENGTH_SHORT).show();
    }

    @OnShowRationale({Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.BROADCAST_SMS})
    void requestSms(PermissionRequest request) {
        Toast.makeText(this, "短信服务说明", Toast.LENGTH_SHORT).show();
        showRationaleDialog(R.string.phone_service, request);
    }

    @OnPermissionDenied({Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.BROADCAST_SMS})
    void smsDenied() {
        change(SwitchType.SMS, false);
        Toast.makeText(this, "短信取消", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.BROADCAST_SMS})
    void smsNotAsk() {
        change(SwitchType.SMS, true);
        Toast.makeText(this, "短信不在询问", Toast.LENGTH_SHORT).show();
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
            case SMS:
                smsSwitch.setChecked(value);
                DataUtils.saveDate(DataUtils.smsHeadOff, value);
                break;
            default:
                alertSwitch.setChecked(value);
                DataUtils.saveDate(DataUtils.alertSwitch, value);
                break;
        }
    }
    private enum SwitchType{
        PHONE, SMS, ALERT
    }
}