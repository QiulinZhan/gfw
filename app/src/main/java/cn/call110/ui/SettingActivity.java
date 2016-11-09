package cn.call110.ui;

import android.Manifest;
import android.content.DialogInterface;
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
        phoneSwitch.setChecked(DataUtils.getDate("phone_switch"));
        phoneSwitch.setOnCheckedChangeListener((e, v) -> {
//            ephoneSwitch.setChecked(false);
            if (v) {
                SettingActivityPermissionsDispatcher.setPhonePermissionsWithCheck(this);
            }
        });
//        phoneSwitch.setOnClickListener(e -> {
//            DataUtils.saveDate("phone_switch", !phoneSwitch.isChecked());
//        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        Toast.makeText(this, grantResults.toString() + "--" + requestCode + "--" + permissions.toString(), Toast.LENGTH_SHORT).show();
        // NOTE: delegate the permission handling to generated method
        SettingActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);

    }

    @NeedsPermission({Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE})
    void setPhonePermissions() {
        setSwitchOn();
        // NOTE: Perform action that requires the permission. If this is run by PermissionsDispatcher, the permission will have been granted
//        Toast.makeText(this, "已开启拦截服务", Toast.LENGTH_SHORT).show();
    }


    @OnShowRationale({Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE})
    void showAlert(PermissionRequest request) {
        showRationaleDialog(R.string.phone_service, request);
    }


    @OnPermissionDenied({Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE})
    void phoneServiceDenied() {
        setSwitchOff();
//        Toast.makeText(this, "拦截服务已关闭", Toast.LENGTH_SHORT).show();
        // NOTE: Deal with a denied permission, e.g. by showing specific UI
        // or disabling certain functionality
//        Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE})
    void phoneServiceAsk() {
//        Toast.makeText(this, "不在询问", Toast.LENGTH_SHORT).show();
    }

    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton("允许", (dialog, which) -> request.proceed())
                .setNegativeButton("拒绝", (dialog, which) -> request.cancel())
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    public void setSwitchOn() {
        phoneSwitch.setChecked(true);
        DataUtils.saveDate("phone_switch", true);
    }

    public void setSwitchOff() {
        phoneSwitch.setChecked(false);
        DataUtils.saveDate("phone_switch", false);
    }
}