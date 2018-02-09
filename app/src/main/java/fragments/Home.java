package fragments;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pk.encodersolutions.mobileantitheft.BaseActivity;
import pk.encodersolutions.mobileantitheft.R;
import databases.SQLiteHandler;
import receivers.AdminReceiver;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.android.volley.Request.Method.POST;
import static helpers.Constants.BASE_URL;
import static helpers.Constants.PREFERENCES;
import static utilities.utils.showToast;


public class Home extends android.app.Fragment {

    SwitchCompat antiTheftControl, iconSwitch, simChangeSwitch, simRemovalSwitch, notificationsSwitch;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    TextView imei, manufacturer, model, apiLevel, androidVersion, simSerial, simOperator, osName, imsi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Home");

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        requestPermissions();
        return view;
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},1);
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS},1);
            requestPermissions(new String[]{Manifest.permission.READ_SMS},1);
            requestPermissions(new String[]{Manifest.permission.SEND_SMS},1);
            requestPermissions(new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},1);
            requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG},1);
            requestPermissions(new String[]{Manifest.permission.WRITE_CALL_LOG},1);
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},1);
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS},1);
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            requestPermissions(new String[]{Manifest.permission.INTERNET},1);
            requestPermissions(new String[]{Manifest.permission.CAMERA},1);
            requestPermissions(new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS},1);
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }

    }

    private boolean checkInternetConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null) {
            showToast(getActivity(), "No Internet Connection");
            return false;
        } else {
            return true;
        }
    }

    private void reportProblem() {
        if (checkInternetConnectivity()) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Report a Problem");
            dialog.setMessage("Please write and report your problem, we will try to fix the problem as soon as possible.");

            final EditText problemInput = new EditText(getActivity());
            problemInput.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
            dialog.setView(problemInput);

            dialog.setPositiveButton("Report", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Reporting Problem");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    String url = BASE_URL + "/problemReport.php";
                    StringRequest request = new StringRequest(POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("success")) {
                                progressDialog.cancel();
                                showToast(getActivity(), "Your problem has been reported successfully");
                            } else {
                                progressDialog.cancel();
                                showToast(getActivity(), "Failed to report the problem");
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.cancel();
                            showToast(getActivity(), "Failed to report the problem");
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            String dateTime = DateFormat.getDateTimeInstance().format(new Date());
                            SQLiteHandler sqLiteHandler = new SQLiteHandler(getActivity());
                            Cursor cursor = sqLiteHandler.getUserData();
                            cursor.moveToFirst();
                            String email = cursor.getString(cursor.getColumnIndex("email"));

                            Map<String, String> params = new HashMap<>();
                            params.put("Problem", problemInput.getText().toString());
                            params.put("DateTime", dateTime);
                            params.put("Email", email);
                            return params;
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                    requestQueue.add(request);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.show();
        }
    }

    private void initViews(View view) {

        antiTheftControl = view.findViewById(R.id.antiTheftControl);
        iconSwitch = view.findViewById(R.id.iconSwitch);
        simChangeSwitch = view.findViewById(R.id.simChangeSwitch);
        simRemovalSwitch = view.findViewById(R.id.simRemovalSwitch);
        notificationsSwitch = view.findViewById(R.id.notificationSwitch);

        sharedPreferences = getActivity().getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        registerIMSI();


        if (sharedPreferences.getBoolean("notificationsSwitch", false)) {
            notificationsSwitch.setChecked(true);
        }

        if (!sharedPreferences.getBoolean("notificationsSwitch", false)) {
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
                    Toast.makeText(getActivity(), "Anti Theft Enabled", Toast.LENGTH_SHORT).show();
                } else {

                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle("Confirmation");
                    alert.setMessage("You will not be able to remotely access your phone. Are you sure you want to disable Anti Theft Protection?");

                    alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            editor.putBoolean("AntiTheftControl", false);
                            editor.apply();
                            Toast.makeText(getActivity(), "Anti Theft Disabled", Toast.LENGTH_SHORT).show();
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

                    final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle("Confirmation");
                    alert.setMessage("If you hide icon you will not be able to see APP icon in menu. Dial #PIN and press call to open the App. ");

                    alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editor.putBoolean("IconSwitch", true);
                            editor.apply();
                            Toast.makeText(getActivity(), "Icon Removed From Menu", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "Icon is Visible Now", Toast.LENGTH_SHORT).show();
                }
            }
        });

        simChangeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("simChangeSwitch", true);
                    editor.apply();
                    Toast.makeText(getActivity(), "Emergency Contacts will be informed on SIM change vis SMS", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putBoolean("simChangeSwitch", false);
                    editor.apply();
                }

            }
        });

        simRemovalSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("simRemovalSwitch", true);
                    editor.apply();
                    Toast.makeText(getActivity(), "Phone will be locked on SIM change/removal", Toast.LENGTH_SHORT).show();

                } else {
                    editor.putBoolean("simRemovalSwitch", false);
                    editor.apply();
                }
            }
        });

        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("notificationsSwitch", true);
                    editor.apply();
                    Toast.makeText(getActivity(), "App Notifications Disabled", Toast.LENGTH_SHORT).show();

                } else {
                    editor.putBoolean("notificationsSwitch", false);
                    editor.apply();
                    Toast.makeText(getActivity(), "App Notifications Enabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imei = view.findViewById(R.id.imei);
        manufacturer = view.findViewById(R.id.manufacturer);
        model = view.findViewById(R.id.model);
        apiLevel = view.findViewById(R.id.api);
        androidVersion = view.findViewById(R.id.osVersion);
        simSerial = view.findViewById(R.id.simSerial);
        simOperator = view.findViewById(R.id.simOperator);
        osName = view.findViewById(R.id.osName);
        imsi = view.findViewById(R.id.imsi);

        getDeviceInfo();

    }

    private void getDeviceInfo() {
        TelephonyManager manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }

        String SimSerialNumber = manager.getSimSerialNumber();
        String IMEI = manager.getDeviceId();
        String IMSI = manager.getSubscriberId();
        String Operator = manager.getNetworkOperatorName();

        if (IMSI == null) {
            IMSI = "No SIM Found";
            SimSerialNumber = "No SIM Found";
            Operator = "No SIM Found";
        }

        imei.append(IMEI);
        manufacturer.append(Build.MANUFACTURER);
        model.append(android.os.Build.MODEL);
        osName.append(Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName());
        apiLevel.append(String.valueOf(android.os.Build.VERSION.SDK_INT));
        androidVersion.append(Build.VERSION.RELEASE);
        simSerial.append(SimSerialNumber);
        simOperator.append(Operator);
        imsi.append(IMSI);
    }

    private void checkDeviceAdmin() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getActivity().getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName demoDeviceAdmin = new ComponentName(getActivity(), AdminReceiver.class);

        boolean isAdminActive = devicePolicyManager.isAdminActive(demoDeviceAdmin);

        if (!isAdminActive) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());

            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("Mobile Anti Theft at Risk");
            builder.setContentText("Administrative permissions are required for the app to function properly");
            builder.setAutoCancel(true);

            Intent resultIntent = new Intent(getActivity(), BaseActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
            stackBuilder.addParentStack(BaseActivity.class);

            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            playNotificationSound();

            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());
        } else {
            if (sharedPreferences.getBoolean("AntiTheftControl", false)) {
            }
        }

    }

    private void playNotificationSound() {
        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(getActivity(), defaultRingtoneUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            mediaPlayer.start();
        } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    private void checkLocationPermissions() {
        boolean gps_enabled = false, network_enabled = false;

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ignored) {
        }
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {
        }
        if (!gps_enabled && !network_enabled) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());

            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("Mobile Anti Theft at Risk");
            builder.setContentText("Location permissions are required for the app to function properly");
            builder.setAutoCancel(true);

            Intent resultIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
            stackBuilder.addParentStack(BaseActivity.class);

            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            playNotificationSound();

            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(2, builder.build());
        } else {
            if (sharedPreferences.getBoolean("AntiTheftControl", false)) {
            }

        }
    }

    private void registerIMSI() {

        TelephonyManager manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }
        String IMSI = manager.getSubscriberId();

        if (IMSI == null) {
            IMSI = "No SIM Found";
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("IMSI", IMSI);
        editor.apply();
    }

    private void checkProtectionStatus() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("AntiTheftControl", false)) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());

            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("Mobile Anti Theft at Risk");
            builder.setContentText("Please enable Anti Theft Protection");
            builder.setAutoCancel(true);

            Intent resultIntent = new Intent(getActivity(), BaseActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
            stackBuilder.addParentStack(BaseActivity.class);

            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            playNotificationSound();

            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(3, builder.build());
        }
    }


}