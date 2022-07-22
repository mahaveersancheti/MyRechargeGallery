package recharge.com.myrechargegallery;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PrepaidRecharge extends Fragment {

    int cnt = 1;
    View view;
    PrefManager prefManager;
    Spinner spOperator, spCircle;
    ArrayList<String> alOperators;
//    TextInputEditText etPhone, etAmount;
    EditText etPhone, etAmount;
    Button btnRecharge, btnCheckOffer, btnCheckROffer;
    String content = "", operatorStr = "";
    TextView tvMessage, tvError;
    ArrayList<Operator> alOp;
    GPSTracker gps;
    double latitude = 0.0;
    double longitude = 0.0;
    String number = "", op = "", amt = "";
    ArrayList<Operator> alOffers;
    ArrayList<Operator> alFrc, alCombo, alRateCutter, alSms, alRomaing, al3G, alFulltt;
    Dialog dialog;
    boolean isPlan = false;
    ListView listView;

    public static PrepaidRecharge newInstance() {
        PrepaidRecharge fragment = new PrepaidRecharge();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.prepaid_recharge_fragment, container, false);
        DrawerActivity.setTitle("Prepaid Recharge");





        if (getArguments() != null) {
            number = getArguments().getString("number");
            op = getArguments().getString("op");
            amt = getArguments().getString("amt");
        }

        prefManager = new PrefManager(getActivity());

        spCircle = (Spinner) view.findViewById(R.id.spCircle);
        spOperator = (Spinner) view.findViewById(R.id.spOperator);
        etPhone = (EditText) view.findViewById(R.id.etPhone);
        etAmount = (EditText) view.findViewById(R.id.etAmount);
        btnRecharge = (Button) view.findViewById(R.id.btnRecharge);
        btnCheckOffer = (Button) view.findViewById(R.id.btnCheckOffer);
        btnCheckROffer = (Button) view.findViewById(R.id.btnCheckROffer);
        tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        tvError = (TextView) view.findViewById(R.id.tvError);

        ArrayList<String> alCircles = new ArrayList<>();
        alCircles.add("Maharashtra Goa");alCircles.add("Andhra Pradesh Telangana");alCircles.add("Assam");alCircles.add("Bihar Jharkhand");
        alCircles.add("Chennai");alCircles.add("Delhi NCR");alCircles.add("Gujarat");
        alCircles.add("Haryana");alCircles.add("Himachal Pradesh");alCircles.add("Jammu Kashmir");
        alCircles.add("Karnataka");alCircles.add("Kerala");alCircles.add("Kolkata");
        alCircles.add("Madhya Pradesh Chhattisgarh");alCircles.add("Mumbai");
        alCircles.add("North East");alCircles.add("Orissa");alCircles.add("Punjab");
        alCircles.add("Rajasthan");alCircles.add("Tamil Nadu");alCircles.add("UP East");
        alCircles.add("UP West");alCircles.add("West Bengal");
//        alCircles.add(0,"Select Circle");
        spCircle.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.tv, alCircles));

        etAmount.setText(amt + "");
        etPhone.setText(number);

        loadOperators();

        btnCheckOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlan = true;
                String circle = spCircle.getSelectedItem().toString().trim();
                String operator = spOperator.getSelectedItem().toString().trim();
                String number = etPhone.getText().toString().trim();
                String numberPattern = "[6789][0-9]{9}";

//                Airtel, Aircel, Bsnl, Tata Docomo, Tata Indicom, Jio, Vodafone, Idea, MTS, MTNL

                if(!operator.trim().equalsIgnoreCase("Select Operator")) {
                    if(!circle.trim().equalsIgnoreCase("Select Circle")) {
                        operator = operator.toLowerCase();
                        boolean f = true;
                        if(operator.contains("idea")) {
                            operatorStr = "Idea";
                        } else if(operator.contains("vodafone")) {
                            operatorStr = "Vodafone";
                        } else if(operator.contains("airtel")) {
                            operatorStr = "Airtel";
                        } else if(operator.contains("docomo")) {
                            operatorStr = "Tata Docomo";
                        } else if(operator.contains("indicom")) {
                            operatorStr = "Tata Indicom";
                        } else if(operator.contains("bsnl")) {
                            operatorStr = "Bsnl";
                        } else if(operator.contains("aircel")) {
                            operatorStr = "Aircel";
                        } else if(operator.contains("jio")) {
                            operatorStr = "Jio";
                        } else if(operator.contains("mts")) {
                            operatorStr = "MTS";
                        } else if(operator.contains("mtnl")) {
                            operatorStr = "MTNL";
                        } else {
                            f = false;
                            Toast.makeText(getActivity(), "Sorry, service not available for this operator.", Toast.LENGTH_LONG).show();
                        }
                        if(f)
                            checkPlans();
                    } else {
                        Toast.makeText(getActivity(), "Please select circle first.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please select operator first.", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnCheckROffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPlan = false;
                String operator = spOperator.getSelectedItem().toString().trim();
                String number = etPhone.getText().toString().trim();
                String numberPattern = "[6789][0-9]{9}";

                if(!operator.trim().equalsIgnoreCase("Select Operator")) {
                    String operatorCode = "";
                    if(number.matches(numberPattern)) {

//                        Airtel, BSNL, Tata Docomo, Tata CDMA, Vodafone, Idea, Jio

                        operator = operator.toLowerCase();
                        boolean f = true;
                        if(operator.contains("idea")) {
                            operatorStr = "Idea";
                        } else if(operator.contains("vodafone")) {
                            operatorStr = "Vodafone";
                        } else if(operator.contains("airtel")) {
                            operatorStr = "Airtel";
                        } else if(operator.contains("docomo")) {
                            operatorStr = "Tata Docomo";
                        } else if(operator.contains("bsnl")) {
                            operatorStr = "BSNL";
                        } else if(operator.contains("jio")) {
                            operatorStr = "Jio";
                        } else {
                            f = false;
                            Toast.makeText(getActivity(), "Sorry, service not available for this operator.", Toast.LENGTH_LONG).show();
                        }
                        if(f)
                            checkOffer1();


//                        String url = prefManager.getSchemeUrl();
//                        operator = operator.toLowerCase();
//                        boolean f = true;
//                        if(operator.contains("idea")) {
//                            url = url.replaceAll("operatorId", "36");
//                            url = url + number;
//                        } else if(operator.contains("vodafone")) {
//                            url = url.replaceAll("operatorId", "169");
//                            url = url + number;
//                        } else if(operator.contains("airtel")) {
//                            url = url.replaceAll("operatorId", "13");
//                            url = url + number;
//                        } else if(operator.contains("docomo")) {
//                            url = url.replaceAll("operatorId", "107");
//                            url = url + number;
//                        } else {
//                            f = false;
//                            Toast.makeText(getActivity(), "Sorry, service not available for this operator.", Toast.LENGTH_LONG).show();
//                        }
//                        if(f)
//                            checkOffer(url);
                    } else {
                        etPhone.setError("Invalid value");
                    }
                } else {
                    Toast.makeText(getActivity(), "Please select operator first.", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_LONG).show();
                String number = etPhone.getText().toString();
                String amount = etAmount.getText().toString();
                String operator = spOperator.getSelectedItem().toString();

                String numberPattern = "[6789][0-9]{9}";
                boolean flag = true;
                if (number.trim().length()==0) {
                    flag = false;
                    etPhone.setError("Invalid value");
                }
                if (amount.trim().length()==0) {
                    flag = false;
                    etAmount.setError("Invalid value");
                }
                if(!number.matches(numberPattern)) {
                    flag = false;
                    etPhone.setError("Invalid value");
                }
                String op = spOperator.getSelectedItem().toString();
                if(!op.trim().equalsIgnoreCase("Select Operator")) {
                    if(flag) {
                        if(prefManager.getBalance()<Float.parseFloat(amount)) {
                            etAmount.setError("Insufficient Balance");
                            return;
                        }
                        spOperator.setBackgroundResource(R.drawable.btn_back);
                        String message = "Operator : " + operator + "\n\n"
                                + "Number : " + number + "\n\n"
                                + "Amount : " + amount;
                        //ResourceElements.showDialog(getActivity(), "Recharge Details", message);
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                        alertDialogBuilder.setTitle("Recharge Details");
                        //alertDialogBuilder.setMessage(message);

                        TextView tv = new TextView(getActivity());
                        tv.setText(message);
                        tv.setTextColor(getResources().getColor(R.color.textColor));
                        tv.setPadding(20,20,20,20);
                        tv.setTextSize(26);
                        alertDialogBuilder.setView(tv);

                        alertDialogBuilder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        btnRecharge.setTextColor(getResources().getColor(R.color.themeColor));
                                        btnRecharge.setEnabled(false);

//                                        Intent intent = new Intent(getActivity(),UPIGatewayActivity.class);
//                                        intent.putExtra("amount", etAmount.getText().toString());
//                                        startActivity(intent);

                                        //Uncomment
                                        recharge();

                                    }
                                });
                        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } else {
                        Toast.makeText(getActivity(), "Invalid details", Toast.LENGTH_LONG).show();
                    }
                } else {
                    spOperator.setBackgroundResource(R.drawable.error);
                    Toast.makeText(getActivity(), "Please select operator first.", Toast.LENGTH_LONG).show();
                }
            }
        });

//        spOperator.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String status = alOp.get(i).getStatus();
//                if(status.equals("0")) {
//                    tvError.setVisibility(View.VISIBLE);
//                    tvError.setText(alOp.get(i).getNote() + "");
//                    btnRecharge.setEnabled(false);
//                    //Toast.makeText(getActivity(), alOp.get(i).getNote() + "", Toast.LENGTH_LONG).show();
//                } else {
//                    tvError.setVisibility(View.GONE);
//                    btnRecharge.setEnabled(true);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        tvError.setVisibility(View.GONE);
        btnRecharge.setEnabled(true);

        if(isLocationEnabled(getActivity())){
            try {
                FusedLocationProviderClient mFusedLocationClient;
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Check Permissions Now
                } else {
                    // permission has been granted, continue as usual
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            try {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            } catch (Exception ee){Log.d("veer1", "" + ee.getMessage());}
                        }
                    });
                }
            } catch(Exception e) {
                Log.d("veer2", "" + e.getMessage());
            }
        } else {
            enableLocation();
        }

        return view;
    }

    public void enableLocation() {
        if (!isLocationEnabled(getActivity())) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage("Location not enabled!");
            dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    enableLocation();
                }
            });
            dialog.show();
        }

    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public void loadOperators() {
        alOperators = new ArrayList<>();
        alOp = new ArrayList<>();
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "transactions.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject1 = new JSONObject(response);
                    if(jsonObject1.getBoolean("ack")) {
                        JSONArray jsonArray = jsonObject1.getJSONArray("result");
                        //JSONArray jsonArray = new JSONArray(response);
                        for(int i=0;i<jsonArray.length();i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Operator obj = new Operator();
                            obj.setId(jsonObject.getString("id"));
                            obj.setName(jsonObject.getString("name"));
                            obj.setStatus(jsonObject.getString("status"));
                            obj.setNote(jsonObject.getString("note"));
                            alOp.add(obj);
                            alOperators.add(jsonObject.getString("name"));
                        }
                        Collections.sort(alOperators);
                        alOperators.add(0,"Select Operator");
                        spOperator.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.tv, alOperators));
                        if(op.length()>0)
                            spOperator.setSelection(alOperators.indexOf(op));
                    } else {
                        Toast.makeText(getActivity(), jsonObject1.getString("message"), Toast.LENGTH_LONG).show();
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
                params.put("method", "getOperators");
                params.put("type", "mobile");
                params.put("token", prefManager.getToken());
                params.put("fromAccount", prefManager.getUserId());
                params.put("imei", prefManager.getImei());
                return params;
            }
        };
        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    public void recharge() {
        Log.d("xxxxxx check", "called bro");
        //Toast.makeText(getActivity(), ""+prefManager.getBalance(), Toast.LENGTH_LONG).show();
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "transactions.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
                        etAmount.setText("");
                        etPhone.setText("");
                        spOperator.setSelection(0);
                        tvError.setText(jsonObject.getString("message"));
                        ResourceElements.showDialogOk(getActivity(), "Recharge Result", jsonObject.getString("message"));
                        prefManager.setBalance(Float.parseFloat(jsonObject.getString("balance")));
                        Fragment selectedFragment = null;
                        selectedFragment = HomeFragment.newInstance();
                        if(selectedFragment!=null) {
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.container, selectedFragment);
                            transaction.commit();
                        }
                        //Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    } else {
                        etAmount.setText("");
                        etPhone.setText("");
                        spOperator.setSelection(0);
                        tvError.setText(jsonObject.getString("message"));
                        ResourceElements.showDialogOk(getActivity(), "Recharge Result", jsonObject.getString("message"));
                        //Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        Fragment selectedFragment = null;
                        selectedFragment = HomeFragment.newInstance();
                        if(selectedFragment!=null) {
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.container, selectedFragment);
                            transaction.commit();
                        }
                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                String message = null;
                if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError) {
                    message = "Communication Error!";
                } else if (volleyError instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (volleyError instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (volleyError instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                message = "Please check recharge status in transaction report.";
                //Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                ResourceElements.showDialogOk(getActivity(), "Recharge Result", message+"");
                //Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                Fragment selectedFragment = null;
                selectedFragment = RechargeReport.newInstance();
                if(selectedFragment!=null) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, selectedFragment);
                    transaction.commit();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("location", latitude+","+longitude);
                params.put("method", "prepaidRecharge");
                params.put("token", prefManager.getToken());
                params.put("fromAccount", prefManager.getUserId());
                params.put("number", etPhone.getText().toString());
                params.put("amount", etAmount.getText().toString());
                params.put("operator", spOperator.getSelectedItem().toString());
                params.put("balance", "" + prefManager.getBalance());
                params.put("transactionType", "prepaid");
                params.put("imei", prefManager.getImei());
                return params;
            }
        };
        int socketTimeout = 0; // 30 seconds. You can change it
        //RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        RetryPolicy policy = new DefaultRetryPolicy(0,-1,1);
        stringRequest.setRetryPolicy(policy);

        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    class Operator {
        String id = "", name = "", status = "", note = "";

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }

    public void checkOffer(final String urlString) {

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    URLConnection urlConnection = url.openConnection();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        content = content + line;
                        Log.d("veer line", line + "");
                    }
                    Log.d("veer", content + "");
                    bufferedReader.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        String updatedString = content.replaceAll("Recharge", "");
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                        alertDialogBuilder.setTitle("Offers");

                        WebView webView = new WebView(getActivity());
                        webView.getSettings().setJavaScriptEnabled(true);
                        //webView.loadUrl(urlString);
                        webView.loadDataWithBaseURL("", updatedString, "text/html", "UTF-8", "");
                        webView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        alertDialogBuilder.setView(webView);

                        //alertDialogBuilder.setMessage(message);
                        alertDialogBuilder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        //Toast.makeText(context, "You clicked yes button", Toast.LENGTH_LONG).show();
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    }
                });
            }
        }).start();
    }

    public void checkOffer1() {
        alOffers = new ArrayList<>();
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "curl.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject1 = new JSONObject(response);
                    JSONArray jsonArray = jsonObject1.getJSONArray("records");
                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Operator obj = new Operator();
                        obj.setName(jsonObject.getString("rs"));
                        obj.setNote(jsonObject.getString("desc"));
                        alOffers.add(obj);
                    }
                    showListOffers();
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
                params.put("method", "rOfferPrepaid");
                params.put("token", prefManager.getToken());
                params.put("fromAccount", prefManager.getUserId());
                params.put("mobile", etPhone.getText().toString().trim());
                params.put("operator", operatorStr.trim());
                params.put("imei", prefManager.getImei());
                return params;
            }
        };
        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    public void showListOffers() {

        TextView tvFullTT, tvTopup, tvThreeG, tvRateCutter, tvSms, tvRomaing, tvCombo, tvFrc;

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
        dialog.setContentView(R.layout.list_view_layout);

        listView = (ListView) dialog.findViewById(R.id.lv);

        HorizontalScrollView hsv = (HorizontalScrollView) dialog.findViewById(R.id.hsv);
        if(isPlan) {
            hsv.setVisibility(View.VISIBLE);
            tvFullTT = (TextView) dialog.findViewById(R.id.tvFullTT);
            tvTopup = (TextView) dialog.findViewById(R.id.tvTopup);
            tvThreeG = (TextView) dialog.findViewById(R.id.tv3G4G);
            tvRateCutter = (TextView) dialog.findViewById(R.id.tvRateCutter);
            tvSms = (TextView) dialog.findViewById(R.id.tvSMS);
            tvRomaing = (TextView) dialog.findViewById(R.id.tvRomaing);
            tvCombo = (TextView) dialog.findViewById(R.id.tvCombo);
            tvFrc = (TextView) dialog.findViewById(R.id.tvFrc);

            tvFrc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listView.setAdapter(new ItemAdapter(getActivity(), alFrc));
                }
            });
            tvRomaing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listView.setAdapter(new ItemAdapter(getActivity(), alRomaing));
                }
            });
            tvCombo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listView.setAdapter(new ItemAdapter(getActivity(), alCombo));
                }
            });
            tvSms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listView.setAdapter(new ItemAdapter(getActivity(), alSms));
                }
            });
            tvRateCutter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listView.setAdapter(new ItemAdapter(getActivity(), alRateCutter));
                }
            });
            tvThreeG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listView.setAdapter(new ItemAdapter(getActivity(), al3G));
                }
            });
            tvTopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listView.setAdapter(new ItemAdapter(getActivity(), alOffers));
                }
            });
            tvFullTT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listView.setAdapter(new ItemAdapter(getActivity(), alFulltt));
                }
            });
        } else {
            hsv.setVisibility(View.GONE);
        }


        listView.setAdapter(new ItemAdapter(getActivity(), alOffers));
        dialog.show();
    }

    class ItemAdapter extends BaseAdapter {
        Context context;
        private LayoutInflater mInflater;
        private ArrayList<Operator> arrayList;// = new ArrayList<Place>();

        public ItemAdapter(Context context, ArrayList<Operator> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final ViewHolder holder;
            final Operator group = arrayList.get(position);
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item_offer, null);
                holder.tvValidity = (TextView) convertView.findViewById(R.id.tvValidity);
                holder.tvOfferDesc = (TextView) convertView.findViewById(R.id.tvOfferDesc);
                holder.btnAmt = (Button) convertView.findViewById(R.id.btnAmt);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvOfferDesc.setText(group.getNote());
            holder.tvValidity.setText(group.getStatus() + "");
            holder.btnAmt.setText(group.getName() + "");
            holder.btnAmt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etAmount.setText(holder.btnAmt.getText().toString() + "");
                    dialog.dismiss();
                }
            });

            return convertView;
        }
    }

    class ViewHolder {
        TextView tvOfferDesc, tvValidity;
        Button btnAmt;
    }

    public void checkPlans() {
        alFrc = new ArrayList<>();
        alCombo = new ArrayList<>();
        alRateCutter = new ArrayList<>();
        alSms = new ArrayList<>();
        alRomaing = new ArrayList<>();
        al3G = new ArrayList<>();
        alFulltt = new ArrayList<>();
        alOffers = new ArrayList<>();
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "curl.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject1 = new JSONObject(response);
                    JSONObject records = jsonObject1.getJSONObject("records");
                    if(!records.isNull("TOPUP")) {
                        JSONArray topup = records.getJSONArray("TOPUP");
                        for(int i=0;i<topup.length();i++) {
                            JSONObject jsonObject = topup.getJSONObject(i);
                            Operator obj = new Operator();
                            obj.setName(jsonObject.getString("rs"));
                            obj.setNote(jsonObject.getString("desc"));
                            obj.setStatus(jsonObject.getString("validity"));
                            alOffers.add(obj);
                        }
                    }
                    if(!records.isNull("FULLTT")) {
                        JSONArray fulltt = records.getJSONArray("FULLTT");
                        for(int i=0;i<fulltt.length();i++) {
                            JSONObject jsonObject = fulltt.getJSONObject(i);
                            Operator obj = new Operator();
                            obj.setName(jsonObject.getString("rs"));
                            obj.setNote(jsonObject.getString("desc"));
                            obj.setStatus(jsonObject.getString("validity"));
                            alFulltt.add(obj);
                        }
                    }
                    if(!records.isNull("3G/4G")) {
                        JSONArray threeG = records.getJSONArray("3G/4G");
                        for(int i=0;i<threeG.length();i++) {
                            JSONObject jsonObject = threeG.getJSONObject(i);
                            Operator obj = new Operator();
                            obj.setName(jsonObject.getString("rs"));
                            obj.setNote(jsonObject.getString("desc"));
                            obj.setStatus(jsonObject.getString("validity"));
                            al3G.add(obj);
                        }
                    }
                    if(!records.isNull("RATE CUTTER")) {
                        JSONArray rateCutter = records.getJSONArray("RATE CUTTER");
                        for(int i=0;i<rateCutter.length();i++) {
                            JSONObject jsonObject = rateCutter.getJSONObject(i);
                            Operator obj = new Operator();
                            obj.setName(jsonObject.getString("rs"));
                            obj.setNote(jsonObject.getString("desc"));
                            obj.setStatus(jsonObject.getString("validity"));
                            alRateCutter.add(obj);
                        }
                    }
                    if(!records.isNull("SMS")) {
                        JSONArray sms = records.getJSONArray("SMS");
                        for(int i=0;i<sms.length();i++) {
                            JSONObject jsonObject = sms.getJSONObject(i);
                            Operator obj = new Operator();
                            obj.setName(jsonObject.getString("rs"));
                            obj.setNote(jsonObject.getString("desc"));
                            obj.setStatus(jsonObject.getString("validity"));
                            alSms.add(obj);
                        }
                    }
                    if(!records.isNull("Romaing")) {
                        JSONArray romaing = records.getJSONArray("Romaing");
                        for(int i=0;i<romaing.length();i++) {
                            JSONObject jsonObject = romaing.getJSONObject(i);
                            Operator obj = new Operator();
                            obj.setName(jsonObject.getString("rs"));
                            obj.setNote(jsonObject.getString("desc"));
                            obj.setStatus(jsonObject.getString("validity"));
                            alRomaing.add(obj);
                        }
                    }
                    if(!records.isNull("COMBO")) {
                        JSONArray combo = records.getJSONArray("COMBO");
                        for(int i=0;i<combo.length();i++) {
                            JSONObject jsonObject = combo.getJSONObject(i);
                            Operator obj = new Operator();
                            obj.setName(jsonObject.getString("rs"));
                            obj.setNote(jsonObject.getString("desc"));
                            obj.setStatus(jsonObject.getString("validity"));
                            alCombo.add(obj);
                        }
                    }
                    if(!records.isNull("FRC")) {
                        JSONArray frc = records.getJSONArray("FRC");
                        for(int i=0;i<frc.length();i++) {
                            JSONObject jsonObject = frc.getJSONObject(i);
                            Operator obj = new Operator();
                            obj.setName(jsonObject.getString("rs"));
                            obj.setNote(jsonObject.getString("desc"));
                            obj.setStatus(jsonObject.getString("validity"));
                            alFrc.add(obj);
                        }
                    }
                    showListOffers();
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
                params.put("method", "plansPrepaid");
                params.put("token", prefManager.getToken());
                params.put("fromAccount", prefManager.getUserId());
                params.put("circle", spCircle.getSelectedItem().toString().trim());
                params.put("operator", operatorStr.trim());
                params.put("imei", prefManager.getImei());
                return params;
            }
        };
        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
}
