package androiddoctors.mobileantitheft;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.android.volley.Request.Method.POST;

public class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected ActionBarDrawerToggle mDrawerToggle;
    TextView name,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view) ;

        View headerView = navigationView.getHeaderView(0);

        name = (TextView) headerView.findViewById(R.id.nameHeader);
        email = (TextView) headerView.findViewById(R.id.emailHeader);

        SQLiteHandler sqLiteHandler = new SQLiteHandler(BaseActivity.this);
        Cursor cursor = sqLiteHandler.getUserData();
        cursor.moveToFirst();

        name.setText(cursor.getString(1));
        email.setText(cursor.getString(2));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId()==R.id.home){
                    startActivity(new Intent(BaseActivity.this,HomeActivity.class));
                    finish();
                }
                if (item.getItemId()==R.id.accountInfo){
                    startActivity(new Intent(BaseActivity.this,ProfileActivity.class));
                    finish();
                }
                if (item.getItemId()==R.id.deviceInfo){
                    Intent intent2 = new Intent(BaseActivity.this, DeviceInfoActivity.class);
                    startActivity(intent2);
                    finish();
                }
                if (item.getItemId()==R.id.commands){
                    Intent intent = new Intent(BaseActivity.this, Commands.class);
                    startActivity(intent);
                    finish();
                }
                if (item.getItemId()==R.id.howToUse){
                    Intent intent1 = new Intent(BaseActivity.this, HowToActivity.class);
                    startActivity(intent1);
                    finish();
                }
                if (item.getItemId()==R.id.report){
                    if (checkInternetConnectivity()) {
                        drawerLayout.closeDrawer(GravityCompat.START);

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

                                String url = "http://mobileantitheft.uphero.com/problemReport.php";
                                StringRequest request = new StringRequest(POST, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if (response.equals("success")) {
                                            progressDialog.cancel();
                                            Toast.makeText(BaseActivity.this, "Your problem has been reported successfully", Toast.LENGTH_LONG).show();
                                        } else {
                                            progressDialog.cancel();
                                            Toast.makeText(BaseActivity.this, "Failed to report the problem", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progressDialog.cancel();
                                        Toast.makeText(BaseActivity.this, "Failed to report the problem", Toast.LENGTH_LONG).show();
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
                if (item.getItemId()==R.id.privacyPolicy){
                    Intent intent4 = new Intent(BaseActivity.this, PrivacyPolicyActivity.class);
                    startActivity(intent4);
                    finish();
                }
                if (item.getItemId()==R.id.about){
                    Intent intent3 = new Intent(BaseActivity.this, AboutActivity.class);
                    startActivity(intent3);
                    finish();
                }
                if (item.getItemId()==R.id.visitWebsite){
                    if (checkInternetConnectivity()){
                        Uri uri = Uri.parse("http://mobileantitheft.uphero.com/");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        finish();
                    }

                }
                if (item.getItemId()==R.id.faq){
                    if (checkInternetConnectivity()){
                        Uri uri = Uri.parse("http://mobileantitheft.uphero.com/#faq");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        finish();
                    }

                }
                return false;
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(getTitle());
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getSupportActionBar().setTitle(getString(R.string.menu_name));
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }
        };


        drawerLayout.addDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerToggle.syncState();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private boolean checkInternetConnectivity(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null){
            Toast.makeText(BaseActivity.this, "No Internet Connection",Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }
}