package androiddoctors.mobileantitheft;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class WebRegistration extends AppCompatActivity {

    TextView Name,Email,Password,ConfirmPassword;
    String SERVER_URL = "http://mobileantitheft.uphero.com";
    Button signupBtn;
    SQLiteHandler sqLiteHandler;
    String jsonURL = null;
    public static final String API_URL = "http://apilayer.net/api/check?access_key=d00a6459ba68571e2a79c26eae4979ad&email=";
    public static String SMPT_RESPONSE;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_registration);

        setTitle("Web Registration");

        Name = (EditText) findViewById(R.id.fullName);
        Email = (EditText) findViewById(R.id.email);
        Password = (EditText) findViewById(R.id.password);
        ConfirmPassword = (EditText) findViewById(R.id.confirmPassword);
        signupBtn = (Button) findViewById(R.id.signup);

        String gmail;

        try{
            Pattern gmailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
            Account[] accounts = AccountManager.get(this).getAccounts();
            for (Account account : accounts) {
                if (gmailPattern.matcher(account.name).matches()) {
                    gmail = account.name;
                    Email.setText(gmail);
                }
            }
        }catch (Exception ignored){
        }

        Cursor c = getApplication().getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        try{
            assert c != null;
            c.moveToFirst();
            if (c.getString(c.getColumnIndex("display_name"))!=null){
                Name.setText(c.getString(c.getColumnIndex("display_name")));
            }
            c.close();
        }catch (Exception ignored){
        }



        sqLiteHandler = new SQLiteHandler(this);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateCredentials(v);
            }
        });


    }

    public void signUp(){
        final ProgressDialog progressDialog = new ProgressDialog(WebRegistration.this);
        progressDialog.setMessage("Signing Up");
        progressDialog.setCancelable(false);
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(WebRegistration.this);
        StringRequest request = new StringRequest(Request.Method.POST, SERVER_URL+"/register.php", new Response.Listener<String>() {

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
                        Toast.makeText(WebRegistration.this, "Email already registered!", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        progressDialog.cancel();
                        Toast.makeText(WebRegistration.this, "Registration Failed", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(WebRegistration.this,"Failed! Please try again",Toast.LENGTH_LONG).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() {

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

                SQLiteHandler sqLiteHandler = new SQLiteHandler(WebRegistration.this);
                Cursor cursor = sqLiteHandler.getUserData();
                cursor.moveToFirst();

                Map<String, String> params = new HashMap<>();
                
                params.put("Mobile","Android");
                params.put("Name",Name.getText().toString());
                params.put("Email",Email.getText().toString());
                params.put("Password",Password.getText().toString());
                params.put("PIN",cursor.getString(4));
                params.put("Phone",cursor.getString(5));
                params.put("EmergencyContact1",cursor.getString(6));
                params.put("EmergencyContact2",cursor.getString(7));
                params.put("EmergencyContact3",cursor.getString(8));

                params.put("IMEI",IMEI);
                params.put("Manufacturer",Build.MANUFACTURER);
                params.put("Model",android.os.Build.MODEL);
                params.put("OS",Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName());
                params.put("ApiLevel",String.valueOf(android.os.Build.VERSION.SDK_INT));
                params.put("Version",Build.VERSION.RELEASE);
                params.put("SimSerial",SimSerialNumber);
                params.put("SimNo",SimNumber);
                params.put("SimOperator",Operator);
                params.put("IMSI",IMSI);

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }


    public void validateCredentials(View view){
        if (Name.getText().toString().equals("")){
            Name.requestFocus();
            Name.setError( "Name is required!" );
        }
        else if (Name.getText().toString().trim().length()==0){
            Name.requestFocus();
            Name.setError( "Name is required!" );
        }

        else if (Name.getText().toString().startsWith(" ") || Name.getText().toString().endsWith(" ")){
            Name.requestFocus();
            Name.setError( "Please enter a valid Name!" );
        }

        else if (Name.getText().toString().length()<3){
            Name.requestFocus();
            Name.setError( "Enter your full name!" );
        }
        else if (Email.getText().toString().equals("")){
            Email.requestFocus();
            Email.setError( "Email is required!" );
        }

        else if (Password.getText().toString().equals("")){
            Password.requestFocus();
            Password.setError( "Password is required!" );
        }
        else if (Password.getText().toString().length() < 8){
            Password.requestFocus();
            Password.setText(null);
            Password.setError( "Password must be at least 8 characters long" );
        }

        else if (ConfirmPassword.getText().toString().equals("")){
            ConfirmPassword.requestFocus();
            ConfirmPassword.setError("Please Confirm Password");
        }
        else if (!Password.getText().toString().equals(ConfirmPassword.getText().toString())){
            Password.setText(null);
            ConfirmPassword.setText(null);
            Toast.makeText(WebRegistration.this,"Passwords do not match",Toast.LENGTH_LONG).show();
        }
        else if (!isValidEmail(Email.getText().toString())){
            Email.requestFocus();
            Email.setError("Invalid Email!");
            Email.setText(null);
        }
        else {
            if (!checkInternetConnectivity()) {
                Toast.makeText(this, "Please check that you are connected to the Internet and try again", Toast.LENGTH_SHORT).show();
            } else {
                SMTP_CHECK();
            }
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void SMTP_CHECK(){

        final ProgressDialog progressDialog = new ProgressDialog(WebRegistration.this);
        progressDialog.setMessage("Verifying Email");
        progressDialog.setCancelable(false);
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(WebRegistration.this);

        jsonURL = API_URL +Email.getText().toString()+"&smtp=1&format=1";

        JsonObjectRequest objectRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET ,jsonURL,null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    SMPT_RESPONSE = String.valueOf(response.getBoolean("smtp_check"));
                    if (SMPT_RESPONSE.equals("true")){
                        progressDialog.cancel();
                        signUp();

                    }
                    else {
                        progressDialog.cancel();
                        Toast.makeText(WebRegistration.this,"Invalid Email",Toast.LENGTH_SHORT).show();
                        Email.setError("Invalid Email!");
                        Email.setText(null);
                    }
                    }
                catch (JSONException e) {
                    progressDialog.cancel();
                    Toast.makeText(WebRegistration.this,e.toString(),Toast.LENGTH_SHORT).show();
                    Email.setError("Invalid Email!");
                    Email.setText(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(WebRegistration.this, error.toString(),Toast.LENGTH_SHORT).show();
                Email.setError("Invalid Email!");
                Email.setText(null);
            }
        });

        objectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(objectRequest);
    }

    private boolean checkInternetConnectivity()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null;
    }
}

