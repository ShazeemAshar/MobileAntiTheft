package activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import pk.encodersolutions.mobileantitheft.R;
import databases.SQLiteHandler;

import static databases.SQLiteHandler.KEY_EC1;
import static databases.SQLiteHandler.KEY_EC2;
import static databases.SQLiteHandler.KEY_EC3;
import static databases.SQLiteHandler.KEY_PHONE;
import static databases.SQLiteHandler.KEY_PIN;
import static helpers.Constants.BASE_URL;
import static utilities.utils.showToast;

public class WebRegistration extends AppCompatActivity {

    EditText Name, Email, Password, ConfirmPassword;
    Button signupBtn;
    SQLiteHandler sqLiteHandler;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_registration);

        initViews();

    }

    private void initViews() {
        Name = findViewById(R.id.fullName);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        ConfirmPassword = findViewById(R.id.confirmPassword);
        signupBtn = findViewById(R.id.signup);


        sqLiteHandler = new SQLiteHandler(this);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateCredentials(v);
            }
        });
    }

    public void signUp() {
        final ProgressDialog progressDialog = new ProgressDialog(WebRegistration.this);
        progressDialog.setMessage("Signing Up");
        progressDialog.setCancelable(false);
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(WebRegistration.this);
        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL + "/register.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                switch (response) {
                    case "success":
                        progressDialog.cancel();
                        //Toast.makeText(WebRegistration.this,"User Registered", Toast.LENGTH_SHORT).show();

                        sqLiteHandler.insertWebAccountInfo(Name.getText().toString(), Email.getText().toString(), Password.getText().toString());


                        sharedPreferences = getSharedPreferences("ActivationPref", MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        editor.putBoolean("Step2", false);
                        editor.putBoolean("Step3", true);
                        editor.apply();

                        Intent intent = new Intent(WebRegistration.this, Activation.class);
                        startActivity(intent);
                        finish();
                        break;
                    case "Duplicate":
                        progressDialog.cancel();
                        Email.setError("Email already registered!");
                        Email.setText(null);
                        showToast(WebRegistration.this, "Email already registered!");
                        break;
                    default:
                        progressDialog.cancel();
                        showToast(WebRegistration.this, "Registration Failed");
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                showToast(WebRegistration.this, error.toString());
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() {

                TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 2);
                    }
                }
                String SimSerialNumber = manager.getSimSerialNumber();
                String SimNumber = manager.getLine1Number();
                String IMEI = manager.getDeviceId();
                String IMSI = manager.getSubscriberId();
                String Operator = manager.getNetworkOperatorName();

                if (IMSI == null) {
                    IMSI = "No SIM Found";
                    SimSerialNumber = "No SIM Found";
                    SimNumber = "No SIM Found";
                    Operator = "No SIM Found";
                }

                SQLiteHandler sqLiteHandler = new SQLiteHandler(WebRegistration.this);
                Cursor cursor = sqLiteHandler.getUserData();
                cursor.moveToFirst();

                Map<String, String> params = new HashMap<>();

                params.put("Mobile", "Android");
                params.put("Name", Name.getText().toString());
                params.put("Email", Email.getText().toString());
                params.put("Password", Password.getText().toString());
                params.put("PIN", cursor.getString(cursor.getColumnIndex(KEY_PIN)));
                params.put("Phone", cursor.getString(cursor.getColumnIndex(KEY_PHONE)));
                params.put("EmergencyContact1", cursor.getString(cursor.getColumnIndex(KEY_EC1)));
                params.put("EmergencyContact2", cursor.getString(cursor.getColumnIndex(KEY_EC2)));
                params.put("EmergencyContact3", cursor.getString(cursor.getColumnIndex(KEY_EC3)));

                params.put("IMEI", IMEI);
                params.put("Manufacturer", Build.MANUFACTURER);
                params.put("Model", Build.MODEL);
                params.put("OS", Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName());
                params.put("ApiLevel", String.valueOf(Build.VERSION.SDK_INT));
                params.put("Version", Build.VERSION.RELEASE);
                params.put("SimSerial", SimSerialNumber);
                params.put("SimNo", SimNumber);
                params.put("SimOperator", Operator);
                params.put("IMSI", IMSI);

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }


    public void validateCredentials(View view) {
        if (Name.getText().toString().equals("")) {
            Name.requestFocus();
            Name.setError("Name is required!");
        } else if (Name.getText().toString().trim().length() == 0) {
            Name.requestFocus();
            Name.setError("Name is required!");
        } else if (Name.getText().toString().startsWith(" ") || Name.getText().toString().endsWith(" ")) {
            Name.requestFocus();
            Name.setError("Please enter a valid Name!");
        } else if (Name.getText().toString().length() < 3) {
            Name.requestFocus();
            Name.setError("Enter your full name!");
        } else if (Email.getText().toString().equals("")) {
            Email.requestFocus();
            Email.setError("Email is required!");
        } else if (Password.getText().toString().equals("")) {
            Password.requestFocus();
            Password.setError("Password is required!");
        } else if (Password.getText().toString().length() < 8) {
            Password.requestFocus();
            Password.setText(null);
            Password.setError("Password must be at least 8 characters long");
        } else if (ConfirmPassword.getText().toString().equals("")) {
            ConfirmPassword.requestFocus();
            ConfirmPassword.setError("Please Confirm Password");
        } else if (!Password.getText().toString().equals(ConfirmPassword.getText().toString())) {
            Password.setText(null);
            ConfirmPassword.setText(null);
           showToast(WebRegistration.this, "Passwords do not match");
        } else if (!isValidEmail(Email.getText().toString())) {
            Email.requestFocus();
            Email.setError("Invalid Email!");
            Email.setText(null);
        } else {
            if (!checkInternetConnectivity()) {
                showToast(this, "Please check that you are connected to the Internet and try again");
            } else {
                signUp();
            }
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private boolean checkInternetConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return networkInfo != null;
    }
}

