package receivers;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import services.ActionHandlerService;

import static android.content.Context.MODE_PRIVATE;
import static helpers.Constants.PREFERENCES;


public class SimStateReceiver extends BroadcastReceiver {

    public Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        isSimAvailable();
    }

    public boolean isSimAvailable() {
        boolean isAvailable = false;
        TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT: //SimState = “No Sim Found!”;
                SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
                Boolean simRemovalSwitch = sharedPreferences.getBoolean("simRemovalSwitch", false);
                if (simRemovalSwitch){
                    Toast.makeText(context, "No Sim Found!", Toast.LENGTH_LONG).show();
                    lockPhone();
                }
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED: //SimState = “Network Locked!”;
                Toast.makeText(context, "Network Locked!", Toast.LENGTH_LONG).show();
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED: //SimState = “PIN Required to access SIM!”;
                Toast.makeText(context, "PIN Required to access SIM!", Toast.LENGTH_LONG).show();
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED: //SimState = “PUK Required to access SIM!”; // Personal Unblocking Code
                Toast.makeText(context, "PUK Required to access SIM!", Toast.LENGTH_LONG).show();
                break;
            case TelephonyManager.SIM_STATE_READY:
                if (matchIMSI()){
                    Toast.makeText(context,"Sim Matched",Toast.LENGTH_LONG).show();
                    break;
                }
                else {

                    lockPhone();

                    SharedPreferences sharedPreferences1 = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
                    Boolean simChangeSwitch = sharedPreferences1.getBoolean("simChangeSwitch", false);

                    if (simChangeSwitch){
                        Toast.makeText(context,"Sim Changed",Toast.LENGTH_LONG).show();
                        context.startService(new Intent(context, ActionHandlerService.class).putExtra("Key","Inform Emergency Contacts"));
                    }
                    break;
                }
            case TelephonyManager.SIM_STATE_UNKNOWN: //SimState = “Unknown SIM State!”;
                Toast.makeText(context, "Unknown State", Toast.LENGTH_LONG).show();
                break;
        }
        return false;
    }

    private boolean matchIMSI (){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        String IMSI = sharedPreferences.getString("IMSI",null);
        return IMSI.equals(getIMSI());
    }

    private String getIMSI(){
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getSubscriberId();
    }

    private void lockPhone() {

        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        int passwordLength = 4;
        String Key = preferences.getString("PIN","0000");

        DevicePolicyManager devicePolicyManager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(context, AdminReceiver.class);

        boolean isAdminActive = devicePolicyManager.isAdminActive(componentName);

        if (isAdminActive){
            devicePolicyManager.setPasswordQuality(componentName, DevicePolicyManager.PASSWORD_QUALITY_NUMERIC);
            devicePolicyManager.setPasswordMinimumLength(componentName, passwordLength);

            devicePolicyManager.resetPassword(Key, DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
            devicePolicyManager.lockNow();
        }
    }
}
