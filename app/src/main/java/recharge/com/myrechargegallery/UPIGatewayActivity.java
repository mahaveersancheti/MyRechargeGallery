package recharge.com.myrechargegallery;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import recharge.com.myrechargegallery.databinding.ActivityUPIGatewayBinding;
import recharge.com.myrechargegallery.dialog.DialogFailed;
import recharge.com.myrechargegallery.dialog.DialogSuccess;

public class UPIGatewayActivity extends AppCompatActivity {
    ActivityUPIGatewayBinding binding;
    EditText etxt_upi, etxt_amount;
    Button btn_pay;
    PrefManager prefManager;
    int GOOGLE_PAY_REQUEST_CODE = 123;
    int WEB_ACTIVITY_RESULT = 1009;
    String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    String transactionId = "";
    //https://developers.google.com/pay/api/android/guides/setup
    //https://developers.google.com/pay/india/api/android/in-app-payments


    public boolean isUpiValid(String text) {
        return text.matches("^[\\w-]+@\\w+$");
    }

    private String getRandomString(final int sizeOfRandomString, String characters) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(characters.charAt(random.nextInt(characters.length())));
        return sb.toString();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUPIGatewayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        setContentView(R.layout.activity_u_p_i_gateway);
        getSupportActionBar().setTitle("Add Wallet Balance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefManager = new PrefManager(this);

        etxt_amount = findViewById(R.id.etxt_amount);
        etxt_upi = findViewById(R.id.etxt_upi);
        btn_pay = findViewById(R.id.btn_pay);
        binding.etxtName.setText(prefManager.getName());
        binding.etxtName.setEnabled(false);
        binding.crdOrder.setVisibility(View.GONE);

//        checkOrder();

        if (prefManager.getIsPaymentPreviousOrder()) {
            binding.crdOrder.setVisibility(View.VISIBLE);
            binding.llDetails.setVisibility(View.GONE);
            binding.txtName.setText(prefManager.getName());
            binding.txtEmail.setText(prefManager.getPaymentEmail());
            binding.txtMobile.setText(prefManager.getPaymentMobile());
            binding.txtAmount.setText("\u20B9 " + prefManager.getPaymentAmount());
            binding.txtTXNId.setText(prefManager.getPaymentClientTXNId());

            binding.btnOrderPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(UPIGatewayActivity.this, WebActivity.class);
                    startActivityForResult(browserIntent,WEB_ACTIVITY_RESULT);


                }
            });
            checkOrder();
        }


//        long tsLong = System.currentTimeMillis() / 1000;
//        String ts = Long.toString(tsLong);
//        transactionId = getRandomString(10, UUID.randomUUID().toString().replace("-", "")) + getRandomString(5, ts);

//        etxt_amount.setText("");

//        etxt_upi.setText("7758889888@upi");
//        etxt_upi.setText("7758889888@upi");
        etxt_upi.setText("7758889888@okbizaxis");
//
//        btn_pay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                final String key = "1910486f-daa9-449d-8ac3-0878b0b22798";
////                // Get the Key from https://upigateway.com/user/api_credentials
////                if(!isUpiValid(etxt_upi.getText().toString().trim())){
////                    Toast.makeText(getApplicationContext(), "Invalid UPI", Toast.LENGTH_LONG).show();
////                    return ;
////                }
//
////                String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
////                int GOOGLE_PAY_REQUEST_CODE = 123;
//
//                if (etxt_amount.getText().toString().isEmpty())
//                {
//                    Toast.makeText(UPIGatewayActivity.this, "Please enter amount", Toast.LENGTH_SHORT).show();
//                    return;
//                }else if (etxt_upi.getText().toString().isEmpty()){
//                    Toast.makeText(UPIGatewayActivity.this, "Please enter UPI ID", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//
//
//                Uri uri = new Uri.Builder()
//                            .scheme("upi")
//                            .authority("pay")
////                            .appendQueryParameter("pa", "manojjain514439@gmail.com")  // merchant email
////                            .appendQueryParameter("pa", "7758889888@okbizaxis")  // merchant vpa or upi id
////                            .appendQueryParameter("pa", etxt_upi.getText().toString())  // merchant vpa or upi id
//                            .appendQueryParameter("pa", "gpay-11195213124@okbizaxis")  // merchant vpa or upi id
//                            .appendQueryParameter("pn", "Navakaar Enterprise") //my recharge gallery     // merchant name
////                            .appendQueryParameter("mc", "BCR2DN6TT7W4DBZI")   // merchant code
////                            .appendQueryParameter("mc", "BCR2DN4TXSMOBKS6")   // merchant code from screenshot sent by mahaveer
//                            .appendQueryParameter("mc", "BCR2DN6T2X4L3JKL")   // merchant code
//                            .appendQueryParameter("tr",transactionId.toUpperCase())  // transaction ref id
//                            .appendQueryParameter("tn", transactionId.toUpperCase())    // transaction note
//                            .appendQueryParameter("am", etxt_amount.getText().toString())    // amount
//                            .appendQueryParameter("cu", "INR")
//                            .appendQueryParameter("url", "http://myrechargegallery.net/callback_urls/upi_gateway.php")    // transaction url
//                            .build();
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(uri);
//                intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
//                startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);
//
////                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
////                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                intent.setPackage("com.android.chrome");
////                startActivity(intent);
//
//                // third party integration
////                Uri.Builder builder = new Uri.Builder();
////                builder.scheme("https")
////                        .authority("upigateway.com")
////                        .appendPath("gateway")
////                        .appendPath("android")
////                        .appendQueryParameter("key", key)
////                        .appendQueryParameter("client_vpa", etxt_upi.getText().toString())
////                        .appendQueryParameter("client_txn_id", UUID.randomUUID().toString())
////                        .appendQueryParameter("amount", etxt_amount.getText().toString())
////                        // Amount Can also be hidden if your product price is fix
////                        .appendQueryParameter("p_info", "RECHARGE")
////                        .appendQueryParameter("client_name", "Manoj") // Set Client Name.
////                        .appendQueryParameter("client_email", "manojjain514439@gmail.com")
////                        .appendQueryParameter("client_mobile", "9595055559")
////                        .appendQueryParameter("udf1", "1")
////                        // udf var is used to store the variable data and
////                        // get same in callback response. ex. user_id, product_id
////                        .appendQueryParameter("udf2", prefManager.getUserId() + "")
////                        .appendQueryParameter("udf3", "1")
////                        .appendQueryParameter("redirect_url", "http://myrechargegallery.net/callback_urls/upi_gateway.php");
////
////                Intent intent = new Intent(Intent.ACTION_VIEW, builder.build());
////                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                intent.setPackage("com.android.chrome");
////                try {
////                    getApplicationContext().startActivity(intent);
////                } catch (ActivityNotFoundException ex) {
////                    // Chrome browser presumably not installed so allow user to choose instead
////                    intent.setPackage(null);
////                    getApplicationContext().startActivity(intent);
////                }
//            }
//        });


        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()){
                    createOrder();
                }

            }
        });

    }


    private boolean validation() {
        if (binding.etxtName.getText().toString().isEmpty()) {
            binding.etxtName.setError("Name required");
            return false;
        } else if (binding.etxtEmail.getText().toString().isEmpty()) {
            binding.etxtEmail.setError("Email required");
            return false;
        } else if (binding.etxtMobile.getText().toString().isEmpty()) {
            binding.etxtMobile.setError("Mobile required");
            return false;
        } else if (binding.etxtAmount.getText().toString().isEmpty()) {
            binding.etxtAmount.setError("Amount required");
            return false;
        }


        return true;
    }

    private void createOrder() {
        String key = "86ec0681-d523-495f-bff8-8a9b0761a7f7";
        long tsLong = System.currentTimeMillis() / 1000;
        String ts = Long.toString(tsLong);
        transactionId = getRandomString(10, UUID.randomUUID().toString().replace("-", "")) + getRandomString(5, ts);
        prefManager.setPaymentClientTXNId(transactionId);

        try {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String formatedDate = dateFormat.format(date);
            prefManager.setPaymentOrderDate(formatedDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = "https://merchant.upigateway.com/api/create_order";

        JSONObject object = new JSONObject();
        try {
            object.put("key", key);
            object.put("client_txn_id", transactionId);
            object.put("amount", binding.etxtAmount.getText());
            object.put("p_info", "Payment by " + binding.etxtName.getText());
            object.put("customer_name", binding.etxtName.getText());
            object.put("customer_email", binding.etxtEmail.getText());
            object.put("customer_mobile", binding.etxtMobile.getText());
            object.put("redirect_url", "http://myrechargegallery.net");
            object.put("udf1", "xyz");
            object.put("udf2", "xyz");
            object.put("udf3", "xyz");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog progressDialog = ProgressDialog.show(UPIGatewayActivity.this, "Loading", "Please Wait..", true);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    if (response.getBoolean("status")) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        prefManager.setPaymentOrderId(jsonObject.getString("order_id"));
                        prefManager.setPaymentUrl(jsonObject.getString("payment_url"));
                        prefManager.setPaymentAmount(binding.etxtAmount.getText().toString());
                        prefManager.setPaymentMobile(binding.etxtMobile.getText().toString());
                        prefManager.setPaymentEmail(binding.etxtEmail.getText().toString());
                        prefManager.setIsPaymentPreviousOrder(true);

                        binding.llDetails.setVisibility(View.GONE);
                        binding.crdOrder.setVisibility(View.VISIBLE);

                        binding.txtName.setText(prefManager.getName());
                        binding.txtEmail.setText(prefManager.getPaymentEmail());
                        binding.txtMobile.setText(prefManager.getPaymentMobile());
                        binding.txtAmount.setText("\u20B9 " + prefManager.getPaymentAmount());
                        binding.txtTXNId.setText(prefManager.getPaymentClientTXNId());

                        binding.btnOrderPay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent browserIntent = new Intent(UPIGatewayActivity.this, WebActivity.class);
                                startActivityForResult(browserIntent,WEB_ACTIVITY_RESULT);
                            }
                        });

                        logUPIPayments();
                    } else {
                        Toast.makeText(UPIGatewayActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
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
        });

        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        final RequestQueue requestQueue = Volley.newRequestQueue(UPIGatewayActivity.this);
        requestQueue.add(jsonObjectRequest);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }



    private void checkOrder() {
        String key = "86ec0681-d523-495f-bff8-8a9b0761a7f7";
        String url = "https://merchant.upigateway.com/api/check_order_status";

        JSONObject object = new JSONObject();
        try {
            object.put("key", key);
            object.put("client_txn_id", prefManager.getPaymentClientTXNId());
            object.put("txn_date", prefManager.getPaymentOrderDate());

//            Log.d("parshu",object.toString()+" "+prefManager.getPaymentUrl());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog progressDialog = ProgressDialog.show(UPIGatewayActivity.this, "Checking orders", "Please Wait..", true);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    if (response.getBoolean("status")) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                            fundTransferUPIPayments();
                            //show default details
                            prefManager.setIsPaymentPreviousOrder(false);
                            binding.llDetails.setVisibility(View.VISIBLE);
                            binding.crdOrder.setVisibility(View.GONE);
                            setDialog(true,jsonObject.getString("remark"));
                        } else if (jsonObject.getString("status").equalsIgnoreCase("failure")) {

                            Toast.makeText(UPIGatewayActivity.this, jsonObject.getString("remark"), Toast.LENGTH_SHORT).show();
                            prefManager.setIsPaymentPreviousOrder(false);
                            binding.llDetails.setVisibility(View.VISIBLE);
                            binding.crdOrder.setVisibility(View.GONE);
                            setDialog(false,jsonObject.getString("remark"));
//                            binding.etxtMobile.setText("");
//                            binding.etxtEmail.setText("");
                            binding.etxtAmount.setText("");
                        } else {
                            if (!jsonObject.getString("remark").isEmpty())
                                Toast.makeText(UPIGatewayActivity.this, jsonObject.getString("remark"), Toast.LENGTH_SHORT).show();
                            //show order details
                            binding.llDetails.setVisibility(View.GONE);
                            binding.crdOrder.setVisibility(View.VISIBLE);
                        }

                    } else {
                        prefManager.setIsPaymentPreviousOrder(false);
                        binding.llDetails.setVisibility(View.VISIBLE);
                        binding.crdOrder.setVisibility(View.GONE);
                        Toast.makeText(UPIGatewayActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
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
        });

        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        final RequestQueue requestQueue = Volley.newRequestQueue(UPIGatewayActivity.this);
        requestQueue.add(jsonObjectRequest);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (prefManager.getIsPaymentPreviousOrder())
//            checkOrder();
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDialog(boolean isSuccess, String message){
        if (isSuccess) {
            DialogSuccess dialogSuccess = new DialogSuccess(UPIGatewayActivity.this);
            dialogSuccess.binding.txtMsg.setText("Transaction Success");
            dialogSuccess.binding.btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogSuccess.dismiss();
                    finish();
                }
            });
            dialogSuccess.show();
        }else {
            DialogFailed dialogFailed = new DialogFailed(UPIGatewayActivity.this);
            dialogFailed.binding.txtMsg.setText(message);
            dialogFailed.binding.btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogFailed.dismiss();
                    finish();
                }
            });
            dialogFailed.show();
        }
    }

    public void logUPIPayments() {
//        final ProgressDialog progressDialog = ProgressDialog.show(UPIGatewayActivity.this, "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "transactions.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
//                    progressDialog.dismiss();
                    JSONObject jsonObject1 = new JSONObject(response);
                    if(jsonObject1.getBoolean("ack")) {
                        prefManager.setPaymentLogId(jsonObject1.getString("id"));

                    } else {
                        Toast.makeText(UPIGatewayActivity.this, jsonObject1.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
//                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("method", "logUPIPaymentRequest");
                params.put("userType", prefManager.getUserType());
                params.put("token", prefManager.getToken());
                params.put("fromAccount", prefManager.getUserId());
                params.put("imei", prefManager.getImei());
                params.put("amount", prefManager.getPaymentAmount());
//                params.put("transactionId", prefManager.getPaymentClientTXNId());
//                params.put("status", status);
                return params;
            }
        };
        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        final RequestQueue requestQueue = Volley.newRequestQueue(UPIGatewayActivity.this);
        requestQueue.add(stringRequest);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }


    public void updateLogUPIPayments() {
//        final ProgressDialog progressDialog = ProgressDialog.show(UPIGatewayActivity.this, "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "transactions.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
//                    progressDialog.dismiss();
                    JSONObject jsonObject1 = new JSONObject(response);
                    if(jsonObject1.getBoolean("ack")) {
                        prefManager.setPaymentLogId(jsonObject1.getString("id"));

                    } else {
                        Toast.makeText(UPIGatewayActivity.this, jsonObject1.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
//                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("method", "updateUPIPaymentRequest");
                params.put("userType", prefManager.getUserType());
                params.put("token", prefManager.getToken());
                params.put("fromAccount", prefManager.getUserId());
                params.put("imei", prefManager.getImei());
                params.put("amount", prefManager.getPaymentAmount());
                params.put("transactionId", prefManager.getPaymentClientTXNId());
                params.put("id", prefManager.getPaymentLogId());
                return params;
            }
        };
        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        final RequestQueue requestQueue = Volley.newRequestQueue(UPIGatewayActivity.this);
        requestQueue.add(stringRequest);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }



    public void fundTransferUPIPayments() {
//        final ProgressDialog progressDialog = ProgressDialog.show(UPIGatewayActivity.this, "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "transactions.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
//                    progressDialog.dismiss();
                    JSONObject jsonObject1 = new JSONObject(response);
                    if(jsonObject1.getBoolean("ack")) {
                        updateLogUPIPayments();
                    } else {

                        Toast.makeText(UPIGatewayActivity.this, jsonObject1.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
//                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("method", "fundTransferForUPI");
                params.put("userType", prefManager.getUserType());
                params.put("token", prefManager.getToken());
                params.put("fromAccount", "1");
                params.put("toAccount", prefManager.getUserId());
                params.put("imei", prefManager.getImei());
                params.put("amount", prefManager.getPaymentAmount());
                params.put("transactionId", prefManager.getPaymentClientTXNId());

                return params;
            }
        };
        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        final RequestQueue requestQueue = Volley.newRequestQueue(UPIGatewayActivity.this);
        requestQueue.add(stringRequest);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_PAY_REQUEST_CODE) {
            // Process based on the data in response.
            Log.d("veer result", data.getStringExtra("Status"));
        }

        if (requestCode == WEB_ACTIVITY_RESULT) {
            checkOrder();
        }
    }
}