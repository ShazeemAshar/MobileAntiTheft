package activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kevalpatel.passcodeview.PinView;
import com.kevalpatel.passcodeview.indicators.CircleIndicator;
import com.kevalpatel.passcodeview.interfaces.AuthenticationListener;
import com.kevalpatel.passcodeview.keys.RoundKey;

import pk.encodersolutions.mobileantitheft.BaseActivity;
import pk.encodersolutions.mobileantitheft.R;

import static helpers.Constants.PREFERENCES;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences preferences;

    private PinView mPinView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initPinLock();
        setListeners();

    }

    private void setListeners() {
        mPinView.setAuthenticationListener(new AuthenticationListener() {
            @Override
            public void onAuthenticationSuccessful() {
                startActivity(new Intent(LoginActivity.this, BaseActivity.class));
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
            }
        });

    }

    private void initPinLock() {

        mPinView = findViewById(R.id.pin_view);
        mPinView.setCorrectPin(getCorrectPin());

        mPinView.setKey(new RoundKey.Builder(mPinView)
                .setKeyPadding(R.dimen.key_padding)
                .setKeyStrokeColorResource(R.color.colorPrimary)
                .setKeyStrokeWidth(R.dimen.key_stroke_width)
                .setKeyTextColorResource(R.color.colorPrimary)
                .setKeyTextSize(R.dimen.key_text_size)
                .build());

        mPinView.setIndicator(new CircleIndicator.Builder(mPinView)
                .setIndicatorRadius(R.dimen.indicator_radius)
                .setIndicatorFilledColorResource(R.color.colorPrimary)
                .setIndicatorStrokeColorResource(R.color.colorPrimary)
                .setIndicatorStrokeWidth(R.dimen.indicator_stroke_width)
                .build());

    }

    private int[] getCorrectPin() {
        preferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        String tempPin = preferences.getString("PIN", "");

        int[] pin = new int[tempPin.length()];

        for (int i = 0; i < tempPin.length(); i++) {
            pin[i] = Character.digit(tempPin.charAt(i), 10);
        }
        return pin;

    }
}