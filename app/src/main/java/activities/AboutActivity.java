package activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import pk.encodersolutions.mobileantitheft.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initViews();
    }

    private void initViews() {
        TextView textView = findViewById(R.id.contactUs);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(AboutActivity.this);
                alert.setTitle("Contact us");
                alert.setMessage("Please leave us an email at SaddaButt@gmail.com, we will get back to you as soon as possible. You can " +
                        "visit our website MobileAntiTheft.000webhostapp.com");

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert.show();
            }
        });
    }
}
