package activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androiddoctors.mobileantitheft.R;
import databases.SQLiteHandler;

import static helpers.Constants.PREFERENCES;

public class PinRegistration extends AppCompatActivity implements View.OnClickListener{

    TextView screen;
    SQLiteHandler sqLiteHandler;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_registration);

        initViews();
    }

    private void initViews() {
        screen =  findViewById(R.id.screen);
        sqLiteHandler = new SQLiteHandler(this);


        int idList[] = {R.id.num0, R.id.num1, R.id.num2, R.id.num3, R.id.num4, R.id.num5, R.id.num6, R.id.num7, R.id.num8,
                R.id.num9};

        for (int id : idList) {
            View v = findViewById(id);
            v.setOnClickListener(this);
        }
    }


    @Override
    public void onClick(View v) {

        String currentScreen = screen.getText().toString();
        currentScreen += ((Button) v).getText().toString();
        screen.setText(currentScreen);

        if (currentScreen.length()==4){

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Your PIN : "+currentScreen);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sqLiteHandler.insertPin((String) screen.getText());

                    sharedPreferences = getSharedPreferences("ActivationPref",MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("Step1",false);
                    editor.putBoolean("Step2",true);
                    editor.apply();

                    sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putString("PIN",screen.getText().toString());
                    editor.apply();

                    if (getIntent().getExtras() != null && getIntent().getStringExtra("Flag").equals("ChangePIN")){
                        Intent intent = new Intent(PinRegistration.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Intent intent = new Intent(PinRegistration.this, Activation.class);
                        startActivity(intent);
                        finish();
                    }


                }
            });
            alert.setCancelable(false);
            alert.show();

        }

    }

    public void onDelete(View v) {

        String updateScreen = screen.getText().toString();
        if (updateScreen.equals("Error")) {
            screen.setText("");
        } else if (!updateScreen.equals("")) {
            updateScreen = updateScreen.substring(0, updateScreen.length() - 1);
            screen.setText(String.valueOf(updateScreen));
        }
    }
}
