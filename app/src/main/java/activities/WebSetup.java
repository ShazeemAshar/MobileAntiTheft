package activities;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androiddoctors.mobileantitheft.R;

public class WebSetup extends AppCompatActivity {

    Button setupWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_setup);
        setTitle("Web Setup");
        setupWeb = (Button)findViewById(R.id.setupWebBtn);

        setupWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo == null){
                    Toast.makeText(WebSetup.this, "Please check that you are connected to the Internet and try again", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(WebSetup.this, WebRegistration.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

    }
}
