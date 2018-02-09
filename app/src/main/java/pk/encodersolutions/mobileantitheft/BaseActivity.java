package pk.encodersolutions.mobileantitheft;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import activities.AboutActivity;
import databases.SQLiteHandler;
import fragments.Commands;
import fragments.Home;
import fragments.HowTo;
import fragments.PrivacyPolicy;
import fragments.Profile;

import static com.android.volley.Request.Method.POST;
import static helpers.Constants.BASE_URL;
import static helpers.Constants.PREFERENCES;
import static utilities.utils.showToast;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvName, tvEmail;
    SharedPreferences sharedPreferences;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        tvName = headerView.findViewById(R.id.tvName);
        tvEmail = headerView.findViewById(R.id.tvEmail);

        SQLiteHandler sqLiteHandler = new SQLiteHandler(BaseActivity.this);
        Cursor cursor = sqLiteHandler.getUserData();
        cursor.moveToFirst();

        tvName.setText(cursor.getString(1));
        tvEmail.setText(cursor.getString(2));

        getFragmentManager().beginTransaction().replace(R.id.content_base, new Home()).commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        Fragment fragment = null;

        switch (id) {
            case R.id.home:
                fragment = new Home();
                break;
            case R.id.accountInfo:
                fragment = new Profile();
                break;
            case R.id.commands:
                fragment = new Commands();
                break;
            case R.id.howToUse:
                fragment = new HowTo();
                break;
            case R.id.faq:
                if (checkInternetConnectivity()) {
                    Uri uri = Uri.parse(BASE_URL + "/#faq");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                break;
            case R.id.report:
                reportProblem();
                break;
            case R.id.privacyPolicy:
                fragment = new PrivacyPolicy();
                break;
            case R.id.visitWebsite:
                if (checkInternetConnectivity()) {
                    Uri uri = Uri.parse(BASE_URL);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                break;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                break;

        }
        if (fragment != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content_base, fragment);
            transaction.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean checkInternetConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null) {
            showToast(BaseActivity.this, "No Internet Connection");
            return false;
        } else {
            return true;
        }
    }

    private void reportProblem() {
        if (checkInternetConnectivity()) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(BaseActivity.this);
            dialog.setTitle("Report a Problem");
            dialog.setMessage("Please write and report your problem, we will try to fix the problem as soon as possible.");

            final EditText problemInput = new EditText(BaseActivity.this);
            problemInput.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
            dialog.setView(problemInput);

            dialog.setPositiveButton("Report", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    final ProgressDialog progressDialog = new ProgressDialog(BaseActivity.this);
                    progressDialog.setMessage("Reporting Problem");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    String url = BASE_URL + "/problemReport.php";
                    StringRequest request = new StringRequest(POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("success")) {
                                progressDialog.cancel();
                                showToast(BaseActivity.this, "Your problem has been reported successfully");
                            } else {
                                progressDialog.cancel();
                                showToast(BaseActivity.this, "Failed to report the problem");
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.cancel();
                            showToast(BaseActivity.this, "Failed to report the problem");
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            String dateTime = DateFormat.getDateTimeInstance().format(new Date());
                            SQLiteHandler sqLiteHandler = new SQLiteHandler(BaseActivity.this);
                            Cursor cursor = sqLiteHandler.getUserData();
                            cursor.moveToFirst();
                            String email = cursor.getString(cursor.getColumnIndex("email"));

                            Map<String, String> params = new HashMap<>();
                            params.put("Problem", problemInput.getText().toString());
                            params.put("DateTime", dateTime);
                            params.put("Email", email);
                            return params;
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(BaseActivity.this);
                    requestQueue.add(request);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        if (sharedPreferences.getBoolean("IconSwitch", false)) {
            PackageManager p = getPackageManager();
            ComponentName componentName = new ComponentName(this, pk.encodersolutions.mobileantitheft.IntroActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
