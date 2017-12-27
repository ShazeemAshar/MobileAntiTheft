package activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androiddoctors.mobileantitheft.BaseActivity;
import androiddoctors.mobileantitheft.R;
import databases.SQLiteHandler;

public class ProfileActivity extends BaseActivity {

    TextView name,email,password,pin,phone,emergencyContact1,emergencyContact2,emergencyContact3;
    ImageView editIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_profile, null, false);
        drawerLayout.addView(contentView, 0);

        setTitle("Profile");

        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        password = (TextView) findViewById(R.id.password);
        pin = (TextView) findViewById(R.id.pin);
        phone = (TextView) findViewById(R.id.phone);
        emergencyContact1 = (TextView) findViewById(R.id.emergencyContact1);
        emergencyContact2 = (TextView) findViewById(R.id.emergencyContact2);
        emergencyContact3 = (TextView) findViewById(R.id.emergencyContact3);
        editIcon = (ImageView) findViewById(R.id.edit);


        SQLiteHandler sqLiteHandler = new SQLiteHandler(ProfileActivity.this);
        Cursor cursor = sqLiteHandler.getUserData();
        cursor.moveToFirst();

        name.append(cursor.getString(1));
        email.append(cursor.getString(2));
        password.append(cursor.getString(3));
        pin.append(cursor.getString(4));
        phone.append(cursor.getString(5));
        emergencyContact1.append(cursor.getString(6));
        emergencyContact2.append(cursor.getString(7));
        emergencyContact3.append(cursor.getString(8));


        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{"Change PIN", "Change Phone No"};

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent5 = new Intent(ProfileActivity.this, PinRegistration.class);
                                intent5.putExtra("Flag", "ChangePIN");
                                startActivity(intent5);
                                finish();
                                break;
                            case 1:
                                Intent intent6 = new Intent(ProfileActivity.this, PhoneNumberRegistration.class);
                                intent6.putExtra("Flag", "ChangePhoneNo");
                                startActivity(intent6);
                                finish();
                                break;
                        }
                    }
                });
                builder.show();
            }
        });

    }
    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
            startActivity(new Intent(this,HomeActivity.class));
        }

    }
}
