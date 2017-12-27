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
import android.widget.Toast;

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
        setTitle("Phone No Registration");

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
                } else if (phone.getText().length() < 11 || phone.getText().length() > 13) {
                    phone.requestFocus();
                    phone.setError("Invalid Phone Number");
                } else if (phone.getText().toString().startsWith("032") && phone.getText().length() > 11 || phone.getText().toString().startsWith("030") && phone.getText().length() > 11 || phone.getText().toString().startsWith("034") && phone.getText().length() > 11 || phone.getText().toString().startsWith("031") && phone.getText().length() > 11 || phone.getText().toString().startsWith("033") && phone.getText().length() > 11 || phone.getText().toString().startsWith("+923") && phone.getText().length() < 13) {
                    phone.requestFocus();
                    phone.setError("Invalid Phone Number");
                } else if (!phone.getText().toString().startsWith("032") && !phone.getText().toString().startsWith("030") && !phone.getText().toString().startsWith("034") && !phone.getText().toString().startsWith("031") && !phone.getText().toString().startsWith("033") && !phone.getText().toString().startsWith("+923")) {
                    phone.requestFocus();
                    phone.setError("Invalid Phone Number");
                } else if (ec1.getText().toString().equals("")) {
                    ec1.requestFocus();
                    ec1.setError("Emergency Contact is required!");
                } else if (ec1.getText().length() < 11 || ec1.getText().length() > 13) {
                    ec1.requestFocus();
                    ec1.setError("Invalid Phone Number");
                } else if (ec1.getText().toString().startsWith("032") && ec1.getText().length() > 11 || ec1.getText().toString().startsWith("030") && ec1.getText().length() > 11 || ec1.getText().toString().startsWith("034") && ec1.getText().length() > 11 || ec1.getText().toString().startsWith("031") && ec1.getText().length() > 11 || ec1.getText().toString().startsWith("033") && ec1.getText().length() > 11 || ec1.getText().toString().startsWith("+923") && ec1.getText().length() < 13) {
                    ec1.requestFocus();
                    ec1.setError("Invalid Phone Number");
                } else if (!ec1.getText().toString().startsWith("032") && !ec1.getText().toString().startsWith("030") && !ec1.getText().toString().startsWith("034") && !ec1.getText().toString().startsWith("031") && !ec1.getText().toString().startsWith("033") && !ec1.getText().toString().startsWith("+923")) {
                    ec1.requestFocus();
                    ec1.setError("Invalid Phone Number");
                } else if (ec2.getText().toString().equals("")) {
                    ec2.requestFocus();
                    ec2.setError("Emergency Contact is required!");
                } else if (ec2.getText().length() < 11 || ec2.getText().length() > 13) {
                    ec2.requestFocus();
                    ec2.setError("Invalid Phone Number");
                } else if (ec2.getText().toString().startsWith("032") && ec2.getText().length() > 11 || ec2.getText().toString().startsWith("030") && ec2.getText().length() > 11 || ec2.getText().toString().startsWith("034") && ec2.getText().length() > 11 || ec2.getText().toString().startsWith("031") && ec2.getText().length() > 11 || ec2.getText().toString().startsWith("033") && ec2.getText().length() > 11 || ec2.getText().toString().startsWith("+923") && ec2.getText().length() < 13) {
                    ec2.requestFocus();
                    ec2.setError("Invalid Phone Number");
                } else if (!ec2.getText().toString().startsWith("032") && !ec2.getText().toString().startsWith("030") && !ec2.getText().toString().startsWith("034") && !ec2.getText().toString().startsWith("031") && !ec2.getText().toString().startsWith("033") && !ec2.getText().toString().startsWith("+923")) {
                    ec2.requestFocus();
                    ec2.setError("Invalid Phone Number");
                } else if (ec3.getText().toString().equals("")) {
                    ec3.requestFocus();
                    ec3.setError("Emergency Contact is required!");
                } else if (ec3.getText().length() < 11 || ec3.getText().length() > 13) {
                    ec3.requestFocus();
                    ec3.setError("Invalid Phone Number");
                } else if (ec3.getText().toString().startsWith("032") && ec3.getText().length() > 11 || ec3.getText().toString().startsWith("030") && ec3.getText().length() > 11 || ec3.getText().toString().startsWith("034") && ec3.getText().length() > 11 || ec3.getText().toString().startsWith("031") && ec3.getText().length() > 11 || ec3.getText().toString().startsWith("033") && ec3.getText().length() > 11 || ec3.getText().toString().startsWith("+923") && ec3.getText().length() < 13) {
                    ec3.requestFocus();
                    ec3.setError("Invalid Phone Number");
                } else if (!ec3.getText().toString().startsWith("032") && !ec3.getText().toString().startsWith("030") && !ec3.getText().toString().startsWith("034") && !ec3.getText().toString().startsWith("031") && !ec3.getText().toString().startsWith("033") && !ec3.getText().toString().startsWith("+923")) {
                    ec3.requestFocus();
                    ec3.setError("Invalid Phone Number");
                } else {
                    String temp = phone.getText().toString();
                    temp = temp.replaceAll("\\s", "");
                    phone.setText(temp.replaceAll("^0+(?!$)", "+92"));

                    temp = ec1.getText().toString();
                    temp = temp.replaceAll("\\s", "");
                    ec1.setText(temp.replaceAll("^0+(?!$)", "+92"));

                    temp = ec2.getText().toString();
                    temp = temp.replaceAll("\\s", "");
                    ec2.setText(temp.replaceAll("^0+(?!$)", "+92"));

                    temp = ec3.getText().toString();
                    temp = temp.replaceAll("\\s", "");
                    ec3.setText(temp.replaceAll("^0+(?!$)", "+92"));


                    sqLiteHandler.insertPhoneNum(phone.getText().toString());
                    sqLiteHandler.insertEmergencyContacts(ec1.getText().toString(), ec2.getText().toString(), ec3.getText().toString());

                    if (getIntent().getExtras() != null && getIntent().getStringExtra("Flag").equals("ChangePhoneNo")) {
                        Intent intent = new Intent(PhoneNumberRegistration.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(PhoneNumberRegistration.this, PinSetup.class);
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
        } else {
            Toast.makeText(PhoneNumberRegistration.this, "Failed to select!", Toast.LENGTH_LONG).show();
        }
    }
}
