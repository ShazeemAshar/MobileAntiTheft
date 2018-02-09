package activities;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import adapters.ActivationAdapter;
import pk.encodersolutions.mobileantitheft.BaseActivity;
import pk.encodersolutions.mobileantitheft.R;
import helpers.SessionManager;
import receivers.AdminReceiver;

public class Activation extends AppCompatActivity {

    ListView listView;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);

        initViews();
    }

    private void initViews() {
        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        listView = findViewById(R.id.listView);

        String[] title = {"SMS Setup", "Web Setup", "Allow Permissions"};
        String[] description = {"Required to remotely lock the phone and delete the data",
                "Required to recover data and locate the thief",
                "Administrative permissions are required for the app to function properly"};

        ActivationAdapter adapter = new ActivationAdapter(this, title, description);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = null;

                switch (position) {
                    case 0:
                        intent = new Intent(Activation.this, PhoneNumberRegistration.class);
                        break;
                    case 1:
                        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                        if (networkInfo == null) {
                            Toast.makeText(Activation.this, "Please check that you are connected to the Internet and try again", Toast.LENGTH_SHORT).show();
                            break;
                        } else {
                            intent = new Intent(Activation.this, WebRegistration.class);
                            break;
                        }
                    case 2:
                        getAdministrativePrivileges();
                        break;
                }

                if (intent!=null){
                    startActivity(intent);
                    finish();
                }

            }
        });

    }

    private void getAdministrativePrivileges(){
        String description = "Please Activate Device Administrator Mode ";
        ComponentName mComponentName;
        mComponentName = new ComponentName(Activation.this, AdminReceiver.class);

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,description);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                sessionManager.setLogin(true);
                Intent intent = new Intent(Activation.this, BaseActivity.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(getApplicationContext(), "Failed to register as Admin", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
