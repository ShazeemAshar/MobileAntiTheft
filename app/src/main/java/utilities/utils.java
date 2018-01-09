package utilities;

import android.content.Context;
import android.widget.Toast;

public class utils {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
