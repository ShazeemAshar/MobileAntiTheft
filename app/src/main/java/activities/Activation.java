package activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import adapters.ActivationAdapter;
import androiddoctors.mobileantitheft.R;
import helpers.SessionManager;

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

        if (sessionManager.isLoggedIn()){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

        listView = findViewById(R.id.listView);

        String[] title = {"SMS Setup","Web Setup","Allow Permissions"};
        String[] description = {"Required to remotely lock the phone and delete the data",
                "Required to recover data and locate the thief",
                "Administrative permissions are required for the app to function properly"};

        ActivationAdapter adapter = new ActivationAdapter(this,title,description);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = null;

                if (position == 0){
                    intent = new Intent(Activation.this,SmsSetup.class);
                }
                else if (position == 1){
                    intent = new Intent(Activation.this,WebSetup.class);
                }
                else if (position == 2){
                    intent = new Intent(Activation.this,AdministratorPermission.class);
                }

                startActivity(intent);
                finish();

            }
        });

    }
}
