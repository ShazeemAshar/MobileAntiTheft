package activities;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;

import android.support.v7.widget.SwitchCompat;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.IOException;

import androiddoctors.mobileantitheft.BaseActivity;
import androiddoctors.mobileantitheft.R;
import receivers.AdminReceiver;
import static helpers.Constants.PREFERENCES;

public class HomeActivity extends BaseActivity {


    SwitchCompat antiTheftControl,iconSwitch,simChangeSwitch,simRemovalSwitch,notificationsSwitch;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_home, null, false);
        drawerLayout.addView(contentView, 0);

        antiTheftControl =  findViewById(R.id.antiTheftControl);
        iconSwitch = findViewById(R.id.iconSwitch);
        simChangeSwitch =  findViewById(R.id.simChangeSwitch);
        simRemovalSwitch =  findViewById(R.id.simRemovalSwitch);
        notificationsSwitch = findViewById(R.id.notificationSwitch);

        sharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        registerIMSI();


        if (sharedPreferences.getBoolean("notificationsSwitch", false)) {
            notificationsSwitch.setChecked(true);
        }

        if (!sharedPreferences.getBoolean("notificationsSwitch", false)){
            checkDeviceAdmin();
            checkLocationPermissions();
            checkProtectionStatus();
        }

        if (sharedPreferences.getBoolean("AntiTheftControl", false)) {
            antiTheftControl.setChecked(true);
        }

        if (sharedPreferences.getBoolean("IconSwitch", false)) {
            iconSwitch.setChecked(true);
        }

        if (sharedPreferences.getBoolean("simChangeSwitch", false)) {
            simChangeSwitch.setChecked(true);
        }

        if (sharedPreferences.getBoolean("simRemovalSwitch", false)) {
            simRemovalSwitch.setChecked(true);
        }

        antiTheftControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("AntiTheftControl", true);
                    editor.apply();
                    Toast.makeText(HomeActivity.this, "Anti Theft Enabled", Toast.LENGTH_SHORT).show();
                } else {

                    AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
                    alert.setTitle("Confirmation");
                    alert.setMessage("You will not be able to remotely access your phone. Are you sure you want to disable Anti Theft Protection?");

                    alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            editor.putBoolean("AntiTheftControl", false);
                            editor.apply();
                            Toast.makeText(HomeActivity.this, "Anti Theft Disabled", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            antiTheftControl.setChecked(true);
                        }
                    });
                    alert.setCancelable(false);
                    alert.show();

                }
            }
        });

        iconSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    final AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
                    alert.setTitle("Confirmation");
                    alert.setMessage("If you hide icon you will not be able to see APP icon in menu. Dial #PIN and press call to open the App. ");

                    alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editor.putBoolean("IconSwitch", true);
                            editor.apply();
                            finish();
                            Toast.makeText(HomeActivity.this, "Icon Removed From Menu", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            iconSwitch.setChecked(false);
                        }
                    });
                    alert.setCancelable(false);
                    alert.show();

                } else {
                            editor.putBoolean("IconSwitch", false);
                            editor.apply();
                            Toast.makeText(HomeActivity.this, "Icon is Visible Now", Toast.LENGTH_SHORT).show();
                }
            }
        });

        simChangeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    editor.putBoolean("simChangeSwitch", true);
                    editor.apply();
                    Toast.makeText(HomeActivity.this, "Emergency Contacts will be informed on SIM change vis SMS", Toast.LENGTH_SHORT).show();
                }else {
                    editor.putBoolean("simChangeSwitch", false);
                    editor.apply();
                }

            }
        });

        simRemovalSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    editor.putBoolean("simRemovalSwitch", true);
                    editor.apply();
                    Toast.makeText(HomeActivity.this, "Phone will be locked on SIM change/removal", Toast.LENGTH_SHORT).show();

                }else {
                    editor.putBoolean("simRemovalSwitch", false);
                    editor.apply();
                }
            }
        });

        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    editor.putBoolean("notificationsSwitch", true);
                    editor.apply();
                    Toast.makeText(HomeActivity.this, "App Notifications Disabled", Toast.LENGTH_SHORT).show();

                }else {
                    editor.putBoolean("notificationsSwitch", false);
                    editor.apply();
                    Toast.makeText(HomeActivity.this, "App Notifications Enabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void checkDeviceAdmin(){
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName demoDeviceAdmin = new ComponentName(this, AdminReceiver.class);

        boolean isAdminActive = devicePolicyManager.isAdminActive(demoDeviceAdmin);

        if(!isAdminActive){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("Mobile Anti Theft at Risk");
            builder.setContentText("Administrative permissions are required for the app to function properly");
            builder.setAutoCancel(true);

            Intent resultIntent = new Intent(this, AdministratorPermission.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(HomeActivity.class);

            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            playNotificationSound();

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());
        }
        else {
            if (sharedPreferences.getBoolean("AntiTheftControl",false)){
            }
        }

    }

    private void playNotificationSound(){
        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(HomeActivity.this, defaultRingtoneUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    mp.release();
                }
            });
            mediaPlayer.start();
        } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    private void checkLocationPermissions(){
        boolean gps_enabled = false,network_enabled = false;

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception ignored){}
        try{
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch (Exception ignored){}
        if(!gps_enabled && !network_enabled){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("Mobile Anti Theft at Risk");
            builder.setContentText("Location permissions are required for the app to function properly");
            builder.setAutoCancel(true);

            Intent resultIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(HomeActivity.class);

            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            playNotificationSound();

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(2, builder.build());
        }
        else {
            if (sharedPreferences.getBoolean("AntiTheftControl",false)){
            }

        }
    }

    private void registerIMSI (){

        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }
        String IMSI = manager.getSubscriberId();

        if (IMSI == null){
            IMSI = "No SIM Found";
        }

        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("IMSI",IMSI);
        editor.apply();
    }

    private void checkProtectionStatus(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        if(!sharedPreferences.getBoolean("AntiTheftControl", false)){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("Mobile Anti Theft at Risk");
            builder.setContentText("Please enable Anti Theft Protection");
            builder.setAutoCancel(true);

            Intent resultIntent = new Intent(this,HomeActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(HomeActivity.class);

            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            playNotificationSound();

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(3, builder.build());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (sharedPreferences.getBoolean("IconSwitch",false)){
            PackageManager p = getPackageManager();
            ComponentName componentName = new ComponentName(this, androiddoctors.mobileantitheft.IntroActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
            p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
}