package recharge.com.myrechargegallery;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    EditText etFullName, etShopName, etContact, etAdharNo, etPassword, etAddr, etHangout, etWhatsapp;
    PrefManager prefManager;
    String imei;

    // GPSTracker class
    GPSTracker gps;
    double latitude = 0.0;
    double longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        prefManager = new PrefManager(this);

        etFullName = (EditText) findViewById(R.id.etFullName);
        etShopName = (EditText) findViewById(R.id.etShopName);
        etContact = (EditText) findViewById(R.id.etPhone);
        etAdharNo = (EditText) findViewById(R.id.etAdharNo);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etAddr = (EditText) findViewById(R.id.etAddr);
        etHangout = (EditText) findViewById(R.id.etHangoutId);
        etWhatsapp = (EditText) findViewById(R.id.etWhatsapp);

        // create class object
        gps = new GPSTracker(RegistrationActivity.this);

        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

        // check if GPS enabled
        if(gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }


        getAllPermissions();
        readImei();
    }

    public void submit(View v) {
        validate();
    }

    public void validate() {
        boolean flag = true;
        String namePattern = "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$";
        String contactPattern = "[789][0-9]{9}";
        String emailPattern = "";

        String name = etFullName.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.trim().length()==0) {
            flag = false;
            etFullName.setError("Invalid value");
        }
        if (contact.trim().length()==0) {
            flag = false;
            etContact.setError("Invalid value");
        }
        if (password.trim().length()==0) {
            flag = false;
            etPassword.setError("Invalid value");
        }
        if(!contact.matches(contactPattern)) {
            flag = false;
            etContact.setError("Invalid value");
        }
        if(flag) {
            register();
        } else {
            Toast.makeText(getApplicationContext(), "Invalid details", Toast.LENGTH_LONG).show();
        }
    }

    public void register() {
        final ProgressDialog progressDialog = ProgressDialog.show(RegistrationActivity.this, "Loading", "Please Wait..", true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "api.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        prefManager.setUserId(jsonObject.getString("id"));
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("method", "registerHomeUser");
                params.put("name", etFullName.getText().toString().trim());
                params.put("contact", etContact.getText().toString().trim());
                params.put("shopName", etShopName.getText().toString().trim());
                params.put("adharNumber", etAdharNo.getText().toString().trim());
                params.put("password", etPassword.getText().toString().trim());
                params.put("imei", prefManager.getImei());
                params.put("lat", "");
                params.put("lng", "");
                return params;
            }
        };
        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    public void getAllPermissions() {
        String[] PERMISSIONS = new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECEIVE_SMS, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_SMS};
        ActivityCompat.requestPermissions(this, PERMISSIONS, 10);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case 10:
                if (hasAllPermissionsGranted(grantResults)) {
                    // Permission Granted
                    //loadSubmit();
                    if (grantResults[1] == 0) {
//                        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
//                        imei = telephonyManager.getDeviceId();
                        imei = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                        //Toast.makeText(getApplicationContext(), imei + "", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "You have to allow permission.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {
                    // Permission Denied
                    //ResourceElements.showDialogOk(MainActivity.this, "Alert", "You have to accept all permission to use applicaiton");
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
                if (ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this,
                        android.Manifest.permission.READ_PHONE_STATE)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE},
                            10);
                } else {
                    // No explanation needed, we can request the permission.

                    requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE},
                            10);

                    // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE},
                        10);
            }
        } else {
//            TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
//            imei = telephonyManager.getDeviceId();
            imei = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            //Toast.makeText(getApplicationContext(), imei + "", Toast.LENGTH_LONG).show();

        }
    }
}
