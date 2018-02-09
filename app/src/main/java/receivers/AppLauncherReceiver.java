package receivers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import pk.encodersolutions.mobileantitheft.IntroActivity;

import static helpers.Constants.PREFERENCES;


public class AppLauncherReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        abortBroadcast();

        String phoneNumber = getResultData();
        if (phoneNumber == null) {
            phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        }

        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        String Key = preferences.getString("PIN","0000");

        if(phoneNumber.equals("#"+Key)){ // DialedNumber checking.
            setResultData(null);


            //
            PackageManager p = context.getPackageManager();
            ComponentName componentName = new ComponentName(context, pk.encodersolutions.mobileantitheft.IntroActivity.class); // activity which is first time open in manifest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
            p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            //

            // Start Application
            Intent i=new Intent(context,IntroActivity.class);
            //i.putExtra("extra_phone", phoneNumber);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
