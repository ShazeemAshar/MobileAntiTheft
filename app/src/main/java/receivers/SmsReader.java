package receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import services.ActionHandlerService;

import static android.content.Context.MODE_PRIVATE;
import static helpers.Constants.PREFERENCES;

public class SmsReader extends BroadcastReceiver {

    String vfile;


    SharedPreferences sharedPreferences;
    String PIN;

    String MESSAGE_DELETE_COMMAND, LOGS_DELETE_COMMAND, CONTACTS_DELETE_COMMAND, NORMAL_MODE,
             CONTACTS_BACKUP_COMMAND, MEMORY_ERASE_COMMAND, SUPER_COMMAND, PHONE_LOCK_COMMAND, FIND_MOBILE,FACTORY_RESET;

    String pin;

    Intent serviceIntent;

    @Override
    public void onReceive(final Context context, Intent intent) {

        sharedPreferences = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        PIN = sharedPreferences.getString("PIN", "0");
        Boolean AntiTheftEnabled = sharedPreferences.getBoolean("AntiTheftControl", false);

        serviceIntent = new Intent(context, ActionHandlerService.class);

        MESSAGE_DELETE_COMMAND = "Delete SMS";
        LOGS_DELETE_COMMAND = "Delete Logs";
        CONTACTS_DELETE_COMMAND = "Delete Contacts";
        NORMAL_MODE = "Normal Mode";
        CONTACTS_BACKUP_COMMAND = "Backup Contacts";
        MEMORY_ERASE_COMMAND = "Wipe Memory";
        SUPER_COMMAND = "Super User";
        PHONE_LOCK_COMMAND = "Lock Phone";
        FIND_MOBILE = "Find Mobile";
        FACTORY_RESET = "Factory Reset";


        pin = "#"+PIN;

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        vfile = "contacts.vcf";


        if (AntiTheftEnabled) {
            try {

                if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    assert pdusObj != null;
                    for (Object aPdusObj : pdusObj) {

                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                        //String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                        //String senderNum = phoneNumber;
                        String message = currentMessage.getDisplayMessageBody();

                        //Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);

                        if (message.equalsIgnoreCase(NORMAL_MODE + pin)) {

                            abortBroadcast();
                            serviceIntent.putExtra("Key", NORMAL_MODE);
                            context.startService(serviceIntent);

                        }

                        if (message.equalsIgnoreCase(FIND_MOBILE + pin)) {

                            abortBroadcast();
                            serviceIntent.putExtra("Key", FIND_MOBILE);
                            context.startService(serviceIntent);

                        }

                        if (message.equalsIgnoreCase(PHONE_LOCK_COMMAND + pin)) {

                            abortBroadcast();
                            serviceIntent.putExtra("Key", PHONE_LOCK_COMMAND);
                            context.startService(serviceIntent);

                        }


                        if (message.equalsIgnoreCase(MESSAGE_DELETE_COMMAND + pin)) {
                            abortBroadcast();
                            serviceIntent.putExtra("Key", MESSAGE_DELETE_COMMAND);
                            context.startService(serviceIntent);

                        }

                        if (message.equalsIgnoreCase(LOGS_DELETE_COMMAND + pin)) {
                            abortBroadcast();
                            serviceIntent.putExtra("Key", LOGS_DELETE_COMMAND);
                            context.startService(serviceIntent);

                        }

                        if (message.equalsIgnoreCase(CONTACTS_DELETE_COMMAND + pin)) {
                            abortBroadcast();

                            serviceIntent.putExtra("Key", CONTACTS_DELETE_COMMAND);
                            context.startService(serviceIntent);
                        }

                        if (message.equalsIgnoreCase(MEMORY_ERASE_COMMAND + pin)) {
                            abortBroadcast();

                            serviceIntent.putExtra("Key", MEMORY_ERASE_COMMAND);
                            context.startService(serviceIntent);

                        }

                        if (message.equalsIgnoreCase(CONTACTS_BACKUP_COMMAND + pin)) {
                            abortBroadcast();

                            serviceIntent.putExtra("Key", CONTACTS_BACKUP_COMMAND);
                            context.startService(serviceIntent);

                        }

                        if (message.equalsIgnoreCase(SUPER_COMMAND + pin)) {
                            abortBroadcast();

                            serviceIntent.putExtra("Key", SUPER_COMMAND);
                            context.startService(serviceIntent);

                        }
                        if (message.equalsIgnoreCase(FACTORY_RESET + pin)) {
                            abortBroadcast();

                            serviceIntent.putExtra("Key", FACTORY_RESET);
                            context.startService(serviceIntent);

                        }

                    }
                }

            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" + e);

            }
        }
    }
}

