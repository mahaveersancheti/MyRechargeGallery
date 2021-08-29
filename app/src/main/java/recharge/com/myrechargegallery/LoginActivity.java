package recharge.com.myrechargegallery;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    TextView tvForgetPwd, tvRegister, tvJoinUs;
    EditText etUnm, etPwd, etToken;
    CheckBox cbRemember;

    PrefManager prefManager;
    //DataBaseWrapper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefManager = new PrefManager(this);

        readImei();

        etUnm = (EditText) findViewById(R.id.etLoginUnm);
        etPwd = (EditText) findViewById(R.id.etLoginPwd);
        cbRemember = (CheckBox) findViewById(R.id.cbLogin);
        tvJoinUs = (TextView) findViewById(R.id.tvJoinus);

        tvForgetPwd = (TextView) findViewById(R.id.tvLoginForgetPwd);
        tvRegister = (TextView) findViewById(R.id.tvLoginRegister);
        etToken = (EditText) findViewById(R.id.etToken);

        String token = prefManager.getToken();
        if(token.length()>0) {
            etToken.setText(token);
            etToken.setVisibility(View.GONE);
        }

        etPwd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (etPwd.getRight() - etPwd.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        //Toast.makeText(getApplicationContext(), "Called", Toast.LENGTH_LONG).show();
                        etPwd.setInputType(InputType.TYPE_CLASS_TEXT);

                        return true;
                    }
                }
                return false;
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(intent);
            }
        });

        tvForgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
                startActivity(intent);

//                String message = "Your password will be reset and new password will be sent to your register number.";
//                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                builder.setTitle("Reset Password");
//                builder.setMessage(message);
//                final EditText input = new EditText(LoginActivity.this);
//                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_PHONE);
//                input.setHint("Mobile Number");
//                input.setPadding(20,20,20,20);
//                input.setBackgroundResource(R.drawable.btn_back);
//                builder.setView(input);
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String contact = input.getText().toString();
//                        resetPassword(contact);
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                builder.show();
            }
        });

        tvJoinUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:7758889888"));
                startActivity(intent);
            }
        });

    }

    public void login(View v) {
        String username = etUnm.getText().toString().trim();
        String password = etPwd.getText().toString().trim();
        String token = etToken.getText().toString().trim();

        boolean flag = true;
        if(username.length()==0) {
            flag = false;
            etUnm.setError("Invalid value");
        }
        if(password.length()==0) {
            flag = false;
            etPwd.setError("Invalid value");
        }
        if(token.length()==0) {
            flag = false;
            etToken.setError("Invalid value");
        }
        if(flag) {
            authenticate();
        } else {
            Toast.makeText(getApplicationContext(), "Invalid details.", Toast.LENGTH_LONG).show();
        }
    }

    public void authenticate() {
        Log.d("veer", Config.JSON_URL + "users.php");
        final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "users.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
                        //Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
//                        if(cbRemember.isChecked()) {
//                            prefManager.setIsLogin(true);
//                        }
                        prefManager.setToken(etToken.getText().toString().trim());
                        prefManager.setUserId(jsonObject.getString("id"));
                        prefManager.setUserType(jsonObject.getString("userType"));
                        prefManager.setName(jsonObject.getString("name"));
                        prefManager.setShopName(jsonObject.getString("shopName"));
                        //Intent intent = new Intent(getApplicationContext(), DrawerActivity.class);
                        //Toast.makeText(getApplicationContext(), "Calling Validate OTP", Toast.LENGTH_LONG).show();
                        //validateOtp(jsonObject.getString("otp"));
                        Intent intent = new Intent(getApplicationContext(), ValidateOTPActivity.class);
                        intent.putExtra("otp", jsonObject.getString("otp"));
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.d("veer", "" + e.getMessage());
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("veer", "" + error.getMessage());
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("method", "login");
                params.put("token", etToken.getText().toString().trim());
                params.put("username", etUnm.getText().toString().trim());
                params.put("password", etPwd.getText().toString().trim());
                params.put("imei", prefManager.getImei());
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

    public void validateOtp(final String otp) {
        Toast.makeText(getApplicationContext(), "Validate OTP", Toast.LENGTH_LONG).show();

        String message = "Please enter OTP.";
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Validate OTP");
        builder.setMessage(message);
        final EditText input = new EditText(LoginActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_PHONE);
        input.setHint("Enter OTP");
        input.setPadding(20,20,20,20);
        input.setBackgroundResource(R.drawable.btn_back);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String otpText = input.getText().toString().trim();
                if(otp.equals(otpText)) {
                    if(cbRemember.isChecked()) {
                        prefManager.setIsLogin(true);
                    }
                    Intent intent = new Intent(getApplicationContext(), PinActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "OTP not matched", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

//        final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Loading", "Please Wait..", true);
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "users.php", new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d("veer", "response" + response);
//                try {
//                    progressDialog.dismiss();
//                    JSONObject jsonObject = new JSONObject(response);
//                    if(jsonObject.getBoolean("ack")) {
//                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(getApplicationContext(), PinActivity.class);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
//                    }
//                } catch (JSONException e) {
//                    progressDialog.dismiss();
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                progressDialog.dismiss();
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("method", "verifyOtp");
//                params.put("otp", otp.trim());
//                params.put("imei", prefManager.getImei());
//                params.put("token", prefManager.getToken());
//                params.put("fromAccount", prefManager.getUserId());
//                return params;
//            }
//        };
//        int socketTimeout = 30000; // 30 seconds. You can change it
//        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
//                0,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        stringRequest.setRetryPolicy(policy);
//        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//        requestQueue.add(stringRequest);
//        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
//            @Override
//            public void onRequestFinished(Request<Object> request) {
//                requestQueue.getCache().clear();
//            }
//        });
    }

    public void resetPassword(final String contact) {
        final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "users.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        prefManager.clearPreference();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
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
                params.put("method", "resetPwd");
                params.put("mobile", contact.trim());
                params.put("imei", prefManager.getImei());
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

    private void readImei() {
        if (Build.VERSION.SDK_INT >= 23) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
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
//                TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
//                String imei = telephonyManager.getDeviceId();
                String imei = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                //Toast.makeText(getApplicationContext(), imei + "", Toast.LENGTH_LONG).show();
                prefManager.setImei(imei);
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
