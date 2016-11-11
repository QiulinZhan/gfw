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
        phoneSwitch = (Switch) findViewById(R.id.phoneSwitch);
        phoneSwitch.setChecked(DataUtils.getDate(DataUtils.phoneHeadOff));
        phoneSwitch.setOnCheckedChangeListener((e, v) -> {
//            ephoneSwitch.setChecked(false);
//            if (!Settings.canDrawOverlays(this)) {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                        Uri.parse("package:" + getPackageName()));
//                startActivityForResult(intent,10);
//            }
            if (v) {
                SettingActivityPermissionsDispatcher.setAlertWithCheck(this);
            }
        });

//        phoneSwitch.setOnClickListener(e -> {
//            DataUtils.saveDate("phone_switch", !phoneSwitch.isChecked());
//        });
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 10) {
//            if (!Settings.canDrawOverlays(this)) {
//                // SYSTEM_ALERT_WINDOW permission not granted...
//                Toast.makeText(this,"not granted",Toast.LENGTH_SHORT);
//            }
//        }
//    }
    // 来电提示权限返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(this, requestCode + "----" + resultCode, Toast.LENGTH_SHORT);
        if (requestCode == 10) {
           if(!Settings.canDrawOverlays(this)){
               phoneSwitch.setChecked(false);
           }else{
               SettingActivityPermissionsDispatcher.setAlertWithCheck(this);
           }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(this, "Result", Toast.LENGTH_SHORT).show();
        // NOTE: delegate the permission handling to generated method
        SettingActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);

    }

    @NeedsPermission({Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE})
    void setPhone() {
        setSwitchOn();
        // NOTE: Perform action that requires the permission. If this is run by PermissionsDispatcher, the permission will have been granted
        Toast.makeText(this, "已开启拦截服务", Toast.LENGTH_SHORT).show();
    }


    @OnShowRationale({Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE})
    void requestPhone(PermissionRequest request) {
        Toast.makeText(this, "请求电话", Toast.LENGTH_SHORT).show();
        showRationaleDialog(R.string.phone_service, request);
    }


    @OnPermissionDenied({Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE})
    void phoneDenied() {
        setSwitchOff();
//        Toast.makeText(this, "拦截服务已关闭", Toast.LENGTH_SHORT).show();
        // NOTE: Deal with a denied permission, e.g. by showing specific UI
        // or disabling certain functionality
        Toast.makeText(this, "电话关闭", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE})
    void phoneNotAsk() {
        Toast.makeText(this, "电话不在询问", Toast.LENGTH_SHORT).show();
    }

    @NeedsPermission(Manifest.permission.SYSTEM_ALERT_WINDOW)
    void setAlert() {
//        setSwitchOn();
        Toast.makeText(this, "开启弹窗", Toast.LENGTH_SHORT).show();
    }


//    @OnShowRationale(Manifest.permission.SYSTEM_ALERT_WINDOW)
//    void requestAlert(PermissionRequest request) {
//        Toast.makeText(this, "请求弹窗", Toast.LENGTH_SHORT).show();
//        showRationaleDialog(R.string.phone_service, request);
//    }
//
//
//    @OnPermissionDenied(Manifest.permission.SYSTEM_ALERT_WINDOW)
//    void alertDenied() {
//        Toast.makeText(this, "弹窗取消", Toast.LENGTH_SHORT).show();
////        setSwitchOff();
////        Toast.makeText(this, "拦截服务已关闭", Toast.LENGTH_SHORT).show();
//        // NOTE: Deal with a denied permission, e.g. by showing specific UI
//        // or disabling certain functionality
////        Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
//    }
//
//    @OnNeverAskAgain(Manifest.permission.SYSTEM_ALERT_WINDOW)
//    void alertNotAsk() {
//        Toast.makeText(this, "弹窗不在询问", Toast.LENGTH_SHORT).show();
//    }

    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton("允许", (dialog, which) -> request.proceed())
                .setNegativeButton("拒绝", (dialog, which) -> request.cancel())
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    public void setSwitchOn(int) {
        phoneSwitch.setChecked(true);
        DataUtils.saveDate("phone_switch", true);
    }

    public void setSwitchOff() {
        phoneSwitch.setChecked(false);
        DataUtils.saveDate("phone_switch", false);
    }
    enum SwitchChange {
        PHONE(e->{}), SMS(e->{}), ALERT(e->{});
        private Comparator comparator;
        private SwitchChange(Comparator comparator) {
            this.comparator = comparator;
        }
    }
}