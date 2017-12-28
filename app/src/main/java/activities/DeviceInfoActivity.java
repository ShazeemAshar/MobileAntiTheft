package activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androiddoctors.mobileantitheft.BaseActivity;
import androiddoctors.mobileantitheft.R;

public class DeviceInfoActivity extends BaseActivity {

    TextView imei, manufacturer, model, apiLevel, androidVersion, simSerial, simNo, simOperator, osName, imsi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_device_info, null, false);
        drawerLayout.addView(contentView, 0);

        initViews();

    }

    private void initViews() {
        setTitle("Device Info");

        imei = findViewById(R.id.imei);
        manufacturer = findViewById(R.id.manufacturer);
        model = findViewById(R.id.model);
        apiLevel = findViewById(R.id.api);
        androidVersion = findViewById(R.id.osVersion);
        simSerial = findViewById(R.id.simSerial);
        simNo = findViewById(R.id.simNo);
        simOperator = findViewById(R.id.simOperator);
        osName = findViewById(R.id.osName);
        imsi = findViewById(R.id.imsi);

        getDeviceInfo();
    }

    private void getDeviceInfo() {
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }

        String SimSerialNumber = manager.getSimSerialNumber();
        String SimNumber = manager.getLine1Number();
        String IMEI = manager.getDeviceId();
        String IMSI = manager.getSubscriberId();
        String Operator = manager.getNetworkOperatorName();

        if (IMSI == null){
            IMSI = "No SIM Found";
            SimSerialNumber = "No SIM Found";
            SimNumber = "No SIM Found";
            Operator = "No SIM Found";
        }

        imei.append(IMEI);
        manufacturer.append(Build.MANUFACTURER);
        model.append(android.os.Build.MODEL);
        osName.append(Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName());
        apiLevel.append(String.valueOf(android.os.Build.VERSION.SDK_INT));
        androidVersion.append(Build.VERSION.RELEASE);
        simSerial.append(SimSerialNumber);
        simNo.append(SimNumber);
        simOperator.append(Operator);
        imsi.append(IMSI);
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
