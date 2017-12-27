package androiddoctors.mobileantitheft;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class SmsSetup extends AppCompatActivity {

    Button setupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_setup);

        setTitle("SMS Setup");

        setupBtn = (Button) findViewById(R.id.setupBtn);

        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SmsSetup.this, PhoneNumberRegistration.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
