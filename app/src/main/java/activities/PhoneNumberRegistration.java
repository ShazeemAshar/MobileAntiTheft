package activities;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androiddoctors.mobileantitheft.BaseActivity;
import androiddoctors.mobileantitheft.R;
import databases.SQLiteHandler;

public class PhoneNumberRegistration extends AppCompatActivity implements View.OnClickListener {

    Button registerPhoneNum;
    EditText phone, ec1, ec2, ec3;
    SQLiteHandler sqLiteHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_registration);
        initViews();

    }

    private void initViews() {

        phone =  findViewById(R.id.phoneText);
        ec1 =  findViewById(R.id.ecText1);
        ec2 =  findViewById(R.id.ecText2);
        ec3 =  findViewById(R.id.ecText3);

        int idList[] = {R.id.phoneInsertBtn, R.id.ec1InsertBtn, R.id.ec2InsertBtn, R.id.ec3InsertBtn};

        for (int id : idList) {
            View v = findViewById(id);
            v.setOnClickListener(this);
        }

        registerPhoneNum = (Button) findViewById(R.id.registerPhone);

        sqLiteHandler = new SQLiteHandler(this);

        registerPhoneNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone.getText().toString().equals("")) {
                    phone.requestFocus();
                    phone.setError("Phone No is required!");
                }  else if (ec1.getText().toString().equals("")) {
                    ec1.requestFocus();
                    ec1.setError("Emergency Contact is required!");
                } else if (ec2.getText().toString().equals("")) {
                    ec2.requestFocus();
                    ec2.setError("Emergency Contact is required!");
                } else if (ec3.getText().toString().equals("")) {
                    ec3.requestFocus();
                    ec3.setError("Emergency Contact is required!");
                } else {

                    sqLiteHandler.insertPhoneNum(phone.getText().toString());
                    sqLiteHandler.insertEmergencyContacts(ec1.getText().toString(), ec2.getText().toString(), ec3.getText().toString());

                    if (getIntent().getExtras() != null && getIntent().getStringExtra("Flag").equals("ChangePhoneNo")) {
                        Intent intent = new Intent(PhoneNumberRegistration.this, BaseActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(PhoneNumberRegistration.this, PinRegistration.class);
                        startActivity(intent);
                        finish();
                    }

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);

        switch (v.getId()) {
            case R.id.phoneInsertBtn:
                startActivityForResult(intent, 1);
                break;
            case R.id.ec1InsertBtn:
                startActivityForResult(intent, 2);
                break;
            case R.id.ec2InsertBtn:
                startActivityForResult(intent, 3);
                break;
            case R.id.ec3InsertBtn:
                startActivityForResult(intent, 4);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Cursor cursor;
            try {
                String phoneNo = null;
                Uri uri = data.getData();

                cursor = getContentResolver().query(uri, null, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();

                int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                phoneNo = cursor.getString(phoneIndex);

                switch (requestCode) {
                    case 1:
                        phone.setText(phoneNo);
                        break;
                    case 2:
                        ec1.setText(phoneNo);
                        break;
                    case 3:
                        ec2.setText(phoneNo);
                        break;
                    case 4:
                        ec3.setText(phoneNo);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}