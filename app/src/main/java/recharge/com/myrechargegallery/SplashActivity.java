package recharge.com.myrechargegallery;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prefManager = new PrefManager(this);

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                Log.d("veer noti done", "Key: " + key + " Value: " + value);
            }
        } else {
            Log.d("veer", "noti param");
        }

        if (Build.VERSION.SDK_INT >= 23) {
            getAllPermissions();
        } else {
            readImei();
            if(prefManager.getIsLogin()) {
                Intent intent = new Intent(getApplicationContext(), PinActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    public void getAllPermissions() {
        String[] PERMISSIONS = new String[]{Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(this, PERMISSIONS, 10);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case 10:
                if (hasAllPermissionsGranted(grantResults)) {
                    if (grantResults[1] == 0) {
//                        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
//                        String imei = telephonyManager.getDeviceId();
                        String imei = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                        prefManager.setImei(imei);
                        //Toast.makeText(getApplicationContext(), imei + "", Toast.LENGTH_LONG).show();
                        String id = prefManager.getUserId();
                        if(prefManager.getIsLogin()) {
                            Intent intent = new Intent(getApplicationContext(), PinActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "You have to allow permission.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {
                    // Permission Denied
                    //ResourceElements.showDialogOk(SplashActivity.this, "Alert", "You have to accept all permission to use applicaiton");
                    getAllPermissions();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    private void readImei() {
        if (Build.VERSION.SDK_INT >= 23) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this,
                        android.Manifest.permission.READ_PHONE_STATE)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

//                    TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
//                    String imei = telephonyManager.getDeviceId();
                    String imei = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                    //Toast.makeText(getApplicationContext(), imei + "", Toast.LENGTH_LONG).show();
                    prefManager.setImei(imei);

                    //requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE}, 1);
                } else {
                    // No explanation needed, we can request the permission.

                    requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE},
                            1);

                    // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE},
                        1);
            }
        } else {
//            TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
//            String imei = telephonyManager.getDeviceId();
            String imei = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            //Toast.makeText(getApplicationContext(), imei + "", Toast.LENGTH_LONG).show();
            prefManager.setImei(imei);

        }
    }
}
