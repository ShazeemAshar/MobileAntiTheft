package activities;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androiddoctors.mobileantitheft.R;
import helpers.SessionManager;
import receivers.AdminReceiver;

public class AdministratorPermission extends AppCompatActivity {

    Button settingsBtn;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator_permission);

        setTitle("Activate Admin");
        sessionManager = new SessionManager(this);

        settingsBtn = (Button) findViewById(R.id.settingsBtn);

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getAdministrativePrivileges();

            }
        });


    }

    private void getAdministrativePrivileges(){
        String description = "Please Activate Device Administrator Mode ";
        ComponentName mComponentName;
        mComponentName = new ComponentName(AdministratorPermission.this, AdminReceiver.class);

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
                Intent intent = new Intent(AdministratorPermission.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(getApplicationContext(), "Failed to register as Admin", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
