package activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androiddoctors.mobileantitheft.BaseActivity;
import androiddoctors.mobileantitheft.R;

public class DeviceInfoActivity extends BaseActivity {

    TextView imei,manufacturer,model,apiLevel,androidVersion,simSerial,simNo,simOperator,osName,imsi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_device_info, null, false);
        drawerLayout.addView(contentView, 0);

        setTitle("Device Info");

        imei = (TextView) findViewById(R.id.imei);
        manufacturer = (TextView) findViewById(R.id.manufacturer);
        model = (TextView) findViewById(R.id.model);
        apiLevel = (TextView) findViewById(R.id.api);
        androidVersion = (TextView) findViewById(R.id.osVersion);
        simSerial = (TextView) findViewById(R.id.simSerial);
        simNo = (TextView) findViewById(R.id.simNo);
        simOperator = (TextView)findViewById(R.id.simOperator);
        osName = (TextView) findViewById(R.id.osName);
        imsi = (TextView) findViewById(R.id.imsi);

        getDeviceInfo();
    }

    private void getDeviceInfo(){
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
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
        }
        else {
            super.onBackPressed();
            startActivity(new Intent(this,HomeActivity.class));
        }

    }
}
