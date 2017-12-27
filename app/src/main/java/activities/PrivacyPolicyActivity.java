package activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;

import androiddoctors.mobileantitheft.BaseActivity;
import androiddoctors.mobileantitheft.R;

public class PrivacyPolicyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_privacy_policy, null, false);
        drawerLayout.addView(contentView, 0);

        setTitle("Privacy Policy");
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
