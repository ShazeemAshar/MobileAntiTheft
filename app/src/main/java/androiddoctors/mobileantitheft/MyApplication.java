package androiddoctors.mobileantitheft;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;


@ReportsCrashes(
        formUri = "http://mobileantitheft.uphero.com/crashReport.php",
        customReportContent = {ReportField.APP_VERSION_NAME, ReportField.PACKAGE_NAME,ReportField.ANDROID_VERSION,
                ReportField.PHONE_MODEL,ReportField.STACK_TRACE },
        httpMethod = HttpSender.Method.POST,
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
)

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }
}
