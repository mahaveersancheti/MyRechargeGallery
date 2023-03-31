package recharge.com.myrechargegallery;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class ValidateOTPActivity extends AppCompatActivity {

    public static EditText etOtp;
    PrefManager prefManager;
    String otp = "";
    String password = "";
    TextView tvTime;
    Button btnResend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_otp);

        prefManager = new PrefManager(this);

        otp = getIntent().getStringExtra("otp");
        password = getIntent().getStringExtra("password");

        etOtp = (EditText) findViewById(R.id.etOtp);
        tvTime = (TextView) findViewById(R.id.tvTime);
        btnResend = (Button) findViewById(R.id.resend);
//        692394
//        462073
        setTimer();
    }

    private void  setTimer(){
        btnResend.setVisibility(View.GONE);
        new CountDownTimer(60 * 1000, 1000) {
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText("Resend OTP: " + millisUntilFinished / 1000);
                // logic to set the EditText could go here
            }

            public void onFinish() {
                tvTime.setVisibility(View.GONE);
                btnResend.setVisibility(View.VISIBLE);
            }

        }.start();
    }

    public void verify(View v) {
        String otpStr = etOtp.getText().toString().trim();
        if(otpStr.length()==0) {
            etOtp.setError("Invalid Value");
        } else {
            if(otpStr.equals(otp)) {
                prefManager.setIsLogin(true);
                Intent intent = new Intent(getApplicationContext(), PinActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Wrong OTP", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void authenticate(View view) {
        Log.d("veer", Config.JSON_URL + "users.php");
        final ProgressDialog progressDialog = ProgressDialog.show(ValidateOTPActivity.this, "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "users.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
//                        prefManager.setToken(etToken.getText().toString().trim());
                        prefManager.setUserId(jsonObject.getString("id"));
                        prefManager.setUserType(jsonObject.getString("userType"));
                        prefManager.setName(jsonObject.getString("name"));
                        prefManager.setShopName(jsonObject.getString("shopName"));
                        //Intent intent = new Intent(getApplicationContext(), DrawerActivity.class);
                        //Toast.makeText(getApplicationContext(), "Calling Validate OTP", Toast.LENGTH_LONG).show();
                        //validateOtp(jsonObject.getString("otp"));

                        otp = jsonObject.getString("otp");
                        setTimer();
//                        Intent intent = new Intent(getApplicationContext(), ValidateOTPActivity.class);
//                        intent.putExtra("otp", jsonObject.getString("otp"));
//                        intent.putExtra("password", etPwd.getText().toString().trim());
//                        startActivity(intent);
//                        finish();
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
                params.put("token", prefManager.getToken());
                params.put("username", prefManager.getPhone());
                params.put("password", password);
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



    public void sendOtp(View v) {
        final ProgressDialog progressDialog = ProgressDialog.show(ValidateOTPActivity.this, "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "users.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {

                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
                params.put("method", "sendOTP");
                params.put("imei", prefManager.getImei());
                params.put("contact", prefManager.getPhone());
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


}
