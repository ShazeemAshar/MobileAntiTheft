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
import android.widget.Toast;

import androiddoctors.mobileantitheft.R;
import databases.SQLiteHandler;

import static helpers.Constants.PREFERENCES;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{


    TextView screen;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    private void initViews() {
        screen = findViewById(R.id.screen);

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

        if (currentScreen.length() == 4) {
            preferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            if (preferences.getString("PIN", "").equals(screen.getText().toString())) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Invalid PIN", Toast.LENGTH_SHORT).show();
            }
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