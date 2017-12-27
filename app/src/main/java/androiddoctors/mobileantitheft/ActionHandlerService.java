package androiddoctors.mobileantitheft;

import android.Manifest;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.snatik.storage.Storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static androiddoctors.mobileantitheft.Activation.PREFERENCES;
import static com.android.volley.Request.Method.POST;

/**
 * Created by Sadda on 27-Apr-17.
 */

public class ActionHandlerService extends Service {

    static String LatCoordinates, LongCoordinates;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String KEY = intent.getStringExtra("Key");

        switch (KEY) {

            case "Normal Mode":
                turnOnSound();
                break;

            case "Find Mobile":
                findMobile();
                break;

            case "Lock Phone":
                lockPhone();
                break;

            case "Delete SMS":
                deleteSms();
                break;

            case "Delete Logs":
                deleteLogs();
                break;

            case "Delete Contacts":
                deleteContacts();
                break;

            case "Wipe Memory":
                wipeMemory();
                break;

            case "Backup Contacts":
                uploadContacts();
                break;

            case "Inform Emergency Contacts":
                informEmergencyContacts();
                break;

            case "Super User":
                lockPhone();
                deleteSms();
                deleteLogs();
                deleteContacts();
                wipeMemory();
                break;

            case "Factory Reset":
                factoryReset();
                break;

            default:
                break;
        }

        stopSelf();

        return START_STICKY;
    }
    private void informEmergencyContacts() {

        SQLiteHandler sqLiteHandler = new SQLiteHandler(this);
        Cursor cursor = sqLiteHandler.getUserData();
        cursor.moveToFirst();

        String emergencyContact1 = cursor.getString(6);
        String emergencyContact2 = cursor.getString(7);
        String emergencyContact3 = cursor.getString(8);

        String message = "Mobile Anti Theft App - Someone just inserted this SIM in your friend's lost device. Send commands on this number and perform needed operations.";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(emergencyContact1, null, message, null, null);
        smsManager.sendTextMessage(emergencyContact2, null, message, null, null);
        smsManager.sendTextMessage(emergencyContact3, null, message, null, null);

    }
    private void findMobile() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);


        boolean network_enabled = false;
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try{
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch (Exception ex){}
        if(network_enabled){
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                double latitude = location.getLatitude(), longitude = location.getLongitude();
            } else {
                LatLng latLng = new LatLng(0, 0);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    sendCoordinates(location.getLatitude(), location.getLongitude());
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            });

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    sendCoordinates(location.getLatitude(),location.getLongitude());
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            });
        }




    }
    protected void sendCoordinates(final double lat, final double lon){

        String url = "http://mobileantitheft.uphero.com/locationUpload.php";
        StringRequest request = new StringRequest(POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String address = null,country = null,countryCode = null,locality = null;

                Geocoder geocoder = new Geocoder(ActionHandlerService.this, Locale.getDefault());

                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(lat, lon, 1);
                    Address obj = addresses.get(0);
                    address = obj.getAddressLine(0);
                    country = obj.getCountryName();
                    countryCode = obj.getCountryCode();
                    locality = obj.getLocality();

                } catch (IOException e) {
                    e.printStackTrace();
                }


                String dateTime = DateFormat.getDateTimeInstance().format(new Date());
                SQLiteHandler sqLiteHandler = new SQLiteHandler(ActionHandlerService.this);
                Cursor cursor = sqLiteHandler.getUserData();
                cursor.moveToFirst();
                String email = cursor.getString(cursor.getColumnIndex("email"));

                Map<String,String> params = new HashMap<>();
                params.put("Address", address);
                params.put("Country", country);
                params.put("CountryCode", countryCode);
                params.put("Locality", locality);
                params.put("Latitude", String.valueOf(lat));
                params.put("Longitude", String.valueOf(lon));
                params.put("DateTime", dateTime);
                params.put("Email", email);
                return params;

                //Location Disabled Check To Be Implemented
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
    private void backupContacts() {
        String vfile = "contacts.vcf";
        final String storage_path = Environment.getExternalStorageDirectory().toString() + "/" + vfile;
        FileOutputStream mFileOutputStream = null;
        final File f = new File(storage_path);
        try {
            if (!f.exists())
                f.createNewFile();
            mFileOutputStream = new FileOutputStream(storage_path, false);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        getVcardString(this, mFileOutputStream);
    }
    private void getVcardString(Context context, FileOutputStream fileOutputStream) {
        // TODO Auto-generated method stub
        ArrayList<String> vCard = new ArrayList<String>();
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if(cursor!=null&&cursor.getCount()>0)
        {
            cursor.moveToFirst();
            for(int i =0;i<cursor.getCount();i++)
            {
                get(cursor,context,fileOutputStream,vCard);
                Log.d("TAG", "Contact "+(i+1)+"VcF String is"+vCard.get(i));
                cursor.moveToNext();
            }

        }
        else
        {
            Log.d("TAG", "No Contacts in Your Phone");
        }
        try
        {
            fileOutputStream.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    private void get(Cursor cursor,Context context, FileOutputStream fileOutputStream, ArrayList<String> vCard) {
        //cursor.moveToFirst();
        String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
        AssetFileDescriptor fd;
        try
        {
            fd = context.getContentResolver().openAssetFileDescriptor(uri, "r");
            FileInputStream fis = fd.createInputStream();
            byte[] buf = new byte[(int) fd.getDeclaredLength()];
            fis.read(buf);
            String vcardstring= new String(buf);
            vCard.add(vcardstring);

            fileOutputStream.write(vcardstring.toString().getBytes());

        }
        catch (Exception e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
    private void uploadContacts() {

        backupContacts();
        final String url = "http://mobileantitheft.uphero.com/fileupload.php";

        final String vfile = "contacts.vcf";
        final String storage_path = Environment.getExternalStorageDirectory().toString() + "/" + vfile;
        //final String storage_path = Environment.getExternalStorageDirectory().toString() + "/" + vfile;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final File f = new File(storage_path);
                String content_type = "vcf";

                SQLiteHandler sqLiteHandler = new SQLiteHandler(ActionHandlerService.this);
                Cursor cursor = sqLiteHandler.getUserData();
                cursor.moveToFirst();
                String email = cursor.getString(cursor.getColumnIndex("email"));

                OkHttpClient client = new OkHttpClient();
                RequestBody file_body = RequestBody.create(MediaType.parse(content_type),f);

                RequestBody request_body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("type",content_type)
                        .addFormDataPart("uploaded_file",vfile,file_body)
                        .addFormDataPart("folder_name",email.toLowerCase())
                        .build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(url)
                        .post(request_body)
                        .build();

                try{
                    okhttp3.Response response = client.newCall(request).execute();

                    if (!response.isSuccessful()){
                    }
                    else {
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
    }
    private void wipeMemory() {

        Storage storage = new Storage(getApplicationContext());
        String path = storage.getExternalStorageDirectory();
        storage.deleteDirectory(path);

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                Uri.parse("file://" +  Environment.getExternalStorageDirectory())));
    }
    private void deleteContacts() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
            contentResolver.delete(uri, null, null);
        }
    }
    private void deleteLogs() {
        getContentResolver().delete(Uri.parse("content://call_log/calls"), null, null);
    }
    private void deleteSms() {
        getContentResolver().delete(Uri.parse("content://sms/"), Telephony.Sms._ID + "!=?", new String[]{"0"});
    }
    private void lockPhone() {

        SharedPreferences preferences = getSharedPreferences(PREFERENCES,MODE_PRIVATE);
        int passwordLength = 4;
        String Key = preferences.getString("PIN","0000");


        DevicePolicyManager devicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(this, AdminReceiver.class);

        boolean isAdminActive = devicePolicyManager.isAdminActive(componentName);

        if (isAdminActive){
            devicePolicyManager.setPasswordQuality(componentName, DevicePolicyManager.PASSWORD_QUALITY_NUMERIC);
            devicePolicyManager.setPasswordMinimumLength(componentName, passwordLength);

            devicePolicyManager.resetPassword(Key, DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
            devicePolicyManager.lockNow();

        }
        else {
            Intent intent = new Intent(ActionHandlerService.this,AdministratorPermission.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }


    }
    private void turnOnSound(){
        AudioManager audioManager;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);

        Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        final Ringtone r = RingtoneManager.getRingtone(this, alarm);
        r.play();

        new CountDownTimer(60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                r.stop();
            }

        }.start();

    }
    private void factoryReset(){
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        devicePolicyManager.wipeData(0);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
