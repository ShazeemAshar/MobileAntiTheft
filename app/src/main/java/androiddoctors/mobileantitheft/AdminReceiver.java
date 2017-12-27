package androiddoctors.mobileantitheft;

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class AdminReceiver extends DeviceAdminReceiver {

    private static int count = 0;

    public static class Controller extends Activity {
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        super.onPasswordFailed(context, intent);
        count++;
        if (count==1){
            Toast.makeText(context, "Invalid Password", Toast.LENGTH_LONG).show();
            count = 0;

            if (checkInternetConnectivity(context)) {
                context.startService(new Intent(context,ActionHandlerService.class).putExtra("Key", "Find Mobile"));
                context.startService(new Intent(context,CameraService.class));
            }

        }
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        super.onPasswordSucceeded(context, intent);
    }

    private boolean checkInternetConnectivity(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null;
    }
}
