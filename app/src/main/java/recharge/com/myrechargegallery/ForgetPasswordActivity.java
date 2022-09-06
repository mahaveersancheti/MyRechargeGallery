package recharge.com.myrechargegallery;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class ForgetPasswordActivity extends AppCompatActivity {

    LinearLayout llOtp;
    EditText etNumber, etOtp;
    PrefManager prefManager;
    String number = "", otp = "", password = "";
    TextView tvTime;
    Button btnResend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        prefManager = new PrefManager(this);

        llOtp = (LinearLayout) findViewById(R.id.llOtp);
        etNumber = (EditText) findViewById(R.id.etNumber);
        etOtp = (EditText) findViewById(R.id.etOtp);
        tvTime = (TextView) findViewById(R.id.tvTime);
        btnResend = (Button) findViewById(R.id.resend);
        btnResend.setVisibility(View.GONE);
        llOtp.setVisibility(View.GONE);
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

    public void submit(View v) {
        number = etNumber.getText().toString().trim();
        String contactPattern = "[6789][0-9]{9}";

        boolean flag = true;
        if(number.length()==0) {
            flag = false;
            etNumber.setError("Invalid value");
        }
        if(!number.matches(contactPattern)) {
            flag = false;
            etNumber.setError("Invalid value");
        }
        if(flag) {
//            sendOtp();
            sendNewOTP();
        } else {
            Toast.makeText(getApplicationContext(), "Invalid details.", Toast.LENGTH_LONG).show();
        }
    }

    public void verify(View v) {
        String otpText = etOtp.getText().toString().trim();
        String contactPattern = "[0-9]{5}";
        boolean flag = true;
        if(otpText.length()==0) {
            flag = false;
            etOtp.setError("Invalid value");
        }
        if(flag) {

//            verifyOTP();
            verifyNewOTP();

//            if(otp.equals(otpText)) {
//                resetPassword(number);

//                String message = "\nUsername : " + number + "\nPassword : " + password + "\n";
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ForgetPasswordActivity.this, AlertDialog.THEME_HOLO_LIGHT);
//                alertDialogBuilder.setTitle("Login Details");
//                alertDialogBuilder.setMessage(message);
//                alertDialogBuilder.setPositiveButton("OK",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface arg0, int arg1) {
//                                resetPassword(number);
//                            }
//                        });
//                AlertDialog alertDialog = alertDialogBuilder.create();
//                alertDialog.show();

//            } else {
//                Toast.makeText(getApplicationContext(), "OTP not matched", Toast.LENGTH_LONG).show();
//            }
//        } else {
//            Toast.makeText(getApplicationContext(), "Invalid details.", Toast.LENGTH_LONG).show();
        }
    }

    public void verifyNewOTP(){
        final ProgressDialog progressDialog = ProgressDialog.show(ForgetPasswordActivity.this, "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "users.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getJSONObject("result").getBoolean("ack")) {
                        sendOtp();
                    }
                    Toast.makeText(getApplicationContext(), jsonObject.getJSONObject("result").getString("message"), Toast.LENGTH_LONG).show();
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
                params.put("method", "verifyNewOtp");
                params.put("otp",  etOtp.getText().toString().trim());
                params.put("contact", etNumber.getText().toString().trim());
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


    public void sendNewOTP(){
        final ProgressDialog progressDialog = ProgressDialog.show(ForgetPasswordActivity.this, "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "users.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
                        llOtp.setVisibility(View.VISIBLE);
                        etOtp.setFocusable(true);
                        number = jsonObject.getString("contact");
//                        password = jsonObject.getString("password");
                        otp = jsonObject.getString("otp");
                        setTimer();
                    }
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
                params.put("method", "sendNewOTP");
//                params.put("imei", prefManager.getImei());
                params.put("contact", number);
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

    public void sendOtp() {
        final ProgressDialog progressDialog = ProgressDialog.show(ForgetPasswordActivity.this, "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "users.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
                        llOtp.setVisibility(View.VISIBLE);
                        etOtp.setFocusable(true);
                        number = jsonObject.getString("contact");
                        password = jsonObject.getString("password");
//                        otp = jsonObject.getString("otp");
                        Intent intent = new Intent(ForgetPasswordActivity.this,LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
                params.put("contact", number);
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

    public void verifyOTP() {
        final ProgressDialog progressDialog = ProgressDialog.show(ForgetPasswordActivity.this, "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "users.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
                        resetPassword(number);
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
                params.put("method", "verifyOtp");
                params.put("imei", prefManager.getImei());
                params.put("contact", number);
                params.put("otp", etOtp.getText().toString().trim());
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

    public void resetPassword(final String contact) {
        final ProgressDialog progressDialog = ProgressDialog.show(ForgetPasswordActivity.this, "Loading", "Please Wait..", true);
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
}
