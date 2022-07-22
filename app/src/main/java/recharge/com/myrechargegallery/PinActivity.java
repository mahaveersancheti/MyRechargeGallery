package recharge.com.myrechargegallery;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import recharge.com.myrechargegallery.jobschedular.Util;
import recharge.com.myrechargegallery.sms.SmsListener;
import recharge.com.myrechargegallery.sms.SmsReceiver;

public class PinActivity extends AppCompatActivity {

    TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8, tv9, tv0, terms;
    ImageView ivMinus;
    //TextView pin;
    PrefManager sharedPref;
    EditText p1, p2, p3, p4;
    boolean focusP1, focusP2, focusP3, focusP4;
    public static String pinString = "";
    String date = "", time = "";

    // GPSTracker class
//    GPSTracker gps;
//    double latitude = 0.0;
//    double longitude = 0.0;
    String fcmToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#333333")));

//        fcmToken = FirebaseInstanceId.getInstance().getToken();

        FirebaseInstallations.getInstance().getId().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("veer", "getInstanceId failed", task.getException());
                    return;
                }

                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            // Get new Instance ID token
                            String token = task.getResult();
                            Log.d("veer token", token + "");

                        }
                    }
                });

//                // Get new Instance ID token
                fcmToken = task.getResult();
                Log.d("veer token installation", task.getResult() + "");

                //Toast.makeText(LoginActivity.this, token, Toast.LENGTH_SHORT).show();
            }
        });

        Log.d("veer token", fcmToken + "");

        terms = (TextView) findViewById(R.id.terms);
        terms.setText("");
        sharedPref = new PrefManager(this);

        tv1 = (TextView) findViewById(R.id.textNumber1);
        tv2 = (TextView) findViewById(R.id.textNumber2);
        tv3 = (TextView) findViewById(R.id.textNumber3);
        tv4 = (TextView) findViewById(R.id.textNumber4);
        tv5 = (TextView) findViewById(R.id.textNumber5);
        tv6 = (TextView) findViewById(R.id.textNumber6);
        tv7 = (TextView) findViewById(R.id.textNumber7);
        tv8 = (TextView) findViewById(R.id.textNumber8);
        tv9 = (TextView) findViewById(R.id.textNumber9);
        tv0 = (TextView) findViewById(R.id.textNumber0);

        p1 = (EditText) findViewById(R.id.pin1);
        p2 = (EditText) findViewById(R.id.pin2);
        p3 = (EditText) findViewById(R.id.pin3);
        p4 = (EditText) findViewById(R.id.pin4);

        focusP1 = true;

        ivMinus = (ImageView) findViewById(R.id.ImageCnacel);
        //ivOk = (ImageView) findViewById(R.id.ImageClear);

        p1.setInputType(InputType.TYPE_NULL);
        p2.setInputType(InputType.TYPE_NULL);
        p3.setInputType(InputType.TYPE_NULL);
        p4.setInputType(InputType.TYPE_NULL);


        if(sharedPref.getUserPin().length()>0) {
        //if (sharedPref.checkSharedPrefs("user_pin")) {
            p1.setBackgroundResource(R.drawable.bluedot);
            p2.setBackgroundResource(R.drawable.bluedot);
            p3.setBackgroundResource(R.drawable.bluedot);
            p4.setBackgroundResource(R.drawable.bluedot);
            pinString = "";
            terms.setText("Change Pin");
            terms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(getApplicationContext(), ResetPin.class);
//                    startActivity(intent);
                }
            });
        }

        p1.setBackgroundResource(R.drawable.bluedot);
        p2.setBackgroundResource(R.drawable.bluedot);
        p3.setBackgroundResource(R.drawable.bluedot);
        p4.setBackgroundResource(R.drawable.bluedot);
        pinString = "";

        p1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusP1 = true;
            }
        });
        p2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusP2 = true;
            }
        });
        p3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusP3 = true;
            }
        });
        p4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusP4 = true;
            }
        });

//        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION );
//        final BroadcastReceiver mReceiver = new SmsReceiver();
//        registerReceiver(mReceiver, filter);
//
//        //sms service
//        SmsReceiver.bindListener(new SmsListener() {
//            @Override
//            public void messageReceived(String messageText) {
//                //From the received text string you may do string operations to get the required OTP
//                //It depends on your SMS format
//                Log.e("mahi",messageText);
//                Toast.makeText(getApplicationContext(),"Message: "+messageText,Toast.LENGTH_LONG).show();
//
//            }
//        });

//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            //Job Schedular for api 23 and above
//            Util.scheduleJob(this);
//        } else{
//            //call background service for notification.
//            startService(new Intent(getApplicationContext(), BackServices.class));  //not working for 5.1, redmi
//        }
        //startService(new Intent(getApplicationContext(), GPSTracker.class));  //not working for 5.1, redmi
        //startService(new Intent(getApplicationContext(), BackServices.class));  //not working for 5.1, redmi

        applyListeners();
        updateData();

//        final Calendar c = Calendar.getInstance();
//        int mYear = c.get(Calendar.YEAR);
//        int mMonth = c.get(Calendar.MONTH);
//        int mDay = c.get(Calendar.DAY_OF_MONTH);
//        date = mDay + "-" + (mMonth + 1) + "-" + mYear;
//        //sharedPref.setSmsBackupDate(date);
//        if(sharedPref.checkSharedPrefs("smsBackupDate")) {
//            if(sharedPref.getSmsBackupDate().equalsIgnoreCase(date)) {
//                Log.d("veer1", "matched");
//                readSMS();
//            } else {
//                Log.d("veer2", "need to set");
//                updateData();
//                //sharedPref.setSmsBackupDate(date);
//            }
//        } else {
//            updateData();
//            Log.d("veer2", "first time");
//            //sharedPref.setSmsBackupDate(date);
//        }

        //readSMS();

    }

    public void updateData() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Authenticate", "Please Wait..", true);
        //progressDialog.setCancelable(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL+"users.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d("veer", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
                        //Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
//                        readSMS();
                    }  else {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                    Log.d("veer err", e.getMessage()+"");
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Network Error Occure", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                String manufacturer = Build.BRAND;
                String model = Build.MODEL;
                String str = "Manufacturer : " + manufacturer + ", Model : " + model;
                Log.d("veer", manufacturer+model);

//                try {
//                    latitude = gps.getLatitude();
//                    longitude = gps.getLongitude();
//                } catch(Exception e) {
//                }

                //params.put("location", latitude+","+longitude);
                params.put("location", "pin");
                params.put("imei", sharedPref.getImei());
                params.put("fromAccount", sharedPref.getUserId());
                params.put("fcmToken",fcmToken+"");
                params.put("method","updateData");
                params.put("phoneDetails",str);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplication());
        requestQueue.add(stringRequest);
    }

    private void ChangePin() {
//        sharedPref.clearParticular();
//        Intent intent = new Intent(getApplicationContext(), PinActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        finish();

    }

    public void applyListeners() {
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "1";
                setText(text);
            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "2";
                setText(text);
            }
        });
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "3";
                setText(text);
            }
        });
        tv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "4";
                setText(text);
            }
        });
        tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "5";
                setText(text);
            }
        });
        tv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "6";
                setText(text);
            }
        });
        tv7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "7";
                setText(text);
            }
        });
        tv8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "8";
                setText(text);
            }
        });
        tv9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "9";
                setText(text);
            }
        });
        tv0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "0";
                setText(text);
            }
        });
        ivMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int point = pinString.length();
                if (point == 1)
                    p1.setBackgroundResource(R.drawable.bluedot);
                else if (point == 2)
                    p2.setBackgroundResource(R.drawable.bluedot);
                else if (point == 3)
                    p3.setBackgroundResource(R.drawable.bluedot);
                else if (point == 4)
                    p4.setBackgroundResource(R.drawable.bluedot);
                if (pinString.length() > 0)
                    pinString = pinString.substring(0, pinString.length() - 1);
                if (pinString.length() == 4) {
                    submit();
                    //change ok image
                    //ivOk.setImageResource(R.drawable.tick_done);
                } else {
                    //ivOk.setImageResource(R.drawable.tickicon);
                }
            }
        });
    }

    public void setText(String text) {
        pinString = pinString + text.trim();
        text = "";
        int point = pinString.length();
        if (point == 1)
            p1.setBackgroundResource(R.drawable.bluewhite);
        else if (point == 2)
            p2.setBackgroundResource(R.drawable.bluewhite);
        else if (point == 3)
            p3.setBackgroundResource(R.drawable.bluewhite);
        else if (point == 4)
            p4.setBackgroundResource(R.drawable.bluewhite);

        if (pinString.length() == 4) {
            submit();
        } else {
            //ivOk.setImageResource(R.drawable.tickicon);
        }
    }

    public void submit() {
        if (pinString.length() == 4) {
//            if(pinString.equals("4725")) {  //master pin
//                Intent intent = new Intent(getApplicationContext(), DrawerActivity.class);
//                startActivity(intent);
//                finish();
//            } else
            if (sharedPref.checkSharedPrefs("user_pin")) {
                if (sharedPref.getUserPin().length()!=0) {
                    if (pinString.equals(sharedPref.getUserPin())) {
                        //Intent intent = new Intent(getApplicationContext(), ChooseActivity.class);
                        Intent intent = new Intent(getApplicationContext(), DrawerActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please check your pin.", Toast.LENGTH_LONG).show();
                        p1.setBackgroundResource(R.drawable.bluedot);
                        p2.setBackgroundResource(R.drawable.bluedot);
                        p3.setBackgroundResource(R.drawable.bluedot);
                        p4.setBackgroundResource(R.drawable.bluedot);
                        pinString = "";
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), ReEnterPinActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                Intent intent = new Intent(getApplicationContext(), ReEnterPinActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Pin must be 4 digit long", Toast.LENGTH_LONG).show();
            p1.setBackgroundResource(R.drawable.bluedot);
            p2.setBackgroundResource(R.drawable.bluedot);
            p3.setBackgroundResource(R.drawable.bluedot);
            p4.setBackgroundResource(R.drawable.bluedot);
            pinString = "";
        }
    }

    public void readSMS() {
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        int hh = c.get(Calendar.HOUR_OF_DAY);
        int mm = c.get(Calendar.MINUTE);
        int ss = c.get(Calendar.SECOND);

        date = mDay + "-" + (mMonth + 1) + "-" + mYear;
        String dateFilter = mDay + "-" + (mMonth + 1) + "-" + mYear; //"11-02-2020";
        String timeFilter = sharedPref.getSmsBackupTime();   //"01:01:01";
        time = hh + ":" + mm + ":" + ss;

        Log.d("veer date", dateFilter);
        Log.d("veer time", time);
        Log.d("veer time sp", timeFilter);

        // Now create a SimpleDateFormat object.
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy'T'hh:mm:ss");
        String selectedDate = dateFilter+"T"+timeFilter;
        //For example: selectedDate="09-10-2015"+"T"+"00:00:00";
        // Now create a start time for this date in order to setup the filter.
        Date dateStart = null;
        try {
            dateStart = formatter.parse(selectedDate);
        } catch (ParseException e) {
            Log.d("veer date", e.getMessage() + "");
            e.printStackTrace();
        }
        // Now create the filter and query the messages.
        String filter = "date>=" + dateStart.getTime();

        Log.d("veer filter", filter);

        Cursor cursor = null;
        if(timeFilter.trim().equalsIgnoreCase("01:01:01"))  //first time
            cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
        else
            cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, filter, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            JSONArray ja = new JSONArray();
            do {
                String msgData = "";
                String address = "", body = "", date = "", date_sent = "";
                for(int idx=0;idx<cursor.getColumnCount();idx++) {
                    String key = cursor.getColumnName(idx).trim();

                    switch (key) {
                        case "address" : address = cursor.getString(idx).trim();
                            break;
                        case "body" : body = cursor.getString(idx).trim();
                            break;
                        case "date" : date = cursor.getString(idx).trim();
                            break;
                        case "date_sent" : date_sent = cursor.getString(idx).trim();
                            break;
                    }
                    msgData += "\n" + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                }
                // use msgData
                //Log.d("veer", msgData);
                if(address.length()>0 && body.length()>0) {
                    try {
                        JSONObject jo = new JSONObject();
                        jo.put("id", 1);
                        jo.put("address", address);
                        jo.put("body", body);
                        jo.put("date", date);
                        jo.put("date_sent", date_sent);
                        ja.put(jo);
                    } catch (Exception e) {
                    }
                } else {
                    Log.d("veer", "else");
                }

            } while (cursor.moveToNext());
            //Log.d("veer", ja.toString().trim());
            backupSMS( ja.toString().trim());
        } else {
            // empty box, no SMS
            Log.d("veer", "No SMS");
            sharedPref.setSmsBackupDate(date);
            sharedPref.setSmsBackupTime(time);
        }
    }

    public void backupSMS(final String sms) {
        Log.d("veer", Config.JSON_URL + "users.php");
        final ProgressDialog progressDialog = ProgressDialog.show(PinActivity.this, "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "api.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
                        sharedPref.setSmsBackupDate(date);
                        sharedPref.setSmsBackupTime(time);
                        //Toast.makeText(getApplicationContext(), "sms backup "+jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
                params.put("method", "backupSMS");
                params.put("fromAccount", sharedPref.getUserId());
                params.put("sms", sms);
                params.put("imei", sharedPref.getImei());
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