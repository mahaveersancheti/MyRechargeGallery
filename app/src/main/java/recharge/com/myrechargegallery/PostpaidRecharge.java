package recharge.com.myrechargegallery;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PostpaidRecharge extends Fragment {

    View view;
    PrefManager prefManager;
    Spinner spOperator;
    ArrayList<String> alOperators;
    EditText etPhone, etAmount;
    Button btnRecharge;
    GPSTracker gps;
    double latitude = 0.0;
    double longitude = 0.0;
    TextView tvError;
    ArrayList<Operator> alOp;
    String number = "", op = "", amt = "";

    public static PostpaidRecharge newInstance() {
        PostpaidRecharge fragment = new PostpaidRecharge();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.postpaid_recharge_fragment, container, false);
        DrawerActivity.setTitle("Postpaid Recharge");

        if (getArguments() != null) {
            number = getArguments().getString("number");
            op = getArguments().getString("op");
            amt = getArguments().getString("amt");
        }

        prefManager = new PrefManager(getActivity());

        spOperator = (Spinner) view.findViewById(R.id.spOperator);
        etPhone = (EditText) view.findViewById(R.id.etPhone);
        etAmount = (EditText) view.findViewById(R.id.etAmount);
        btnRecharge = (Button) view.findViewById(R.id.btnRecharge);
        tvError = (TextView) view.findViewById(R.id.tvError);

        etAmount.setText(amt + "");
        etPhone.setText(number);

        loadOperators();

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
                        spOperator.setBackgroundResource(R.drawable.btn_back);
                        String message = "Operator : " + operator + "\n\n"
                                + "Number : " + number + "\n\n"
                                + "Amount : " + amount;
                        //ResourceElements.showDialog(getActivity(), "Recharge Details", message);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
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
                                        //Toast.makeText(context, "You clicked yes button", Toast.LENGTH_LONG).show();
                                        btnRecharge.setTextColor(getResources().getColor(R.color.themeColor));
                                        btnRecharge.setEnabled(false);
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
        //alOperators.add("Select Operator");
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
                params.put("type", "postpaid");
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
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "transactions.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
                        etAmount.setText("");
                        etPhone.setText("");
                        spOperator.setSelection(0);
                        tvError.setText(jsonObject.getString("message"));
                        ResourceElements.showDialogOk(getActivity(), "Recharge Result", jsonObject.getString("message"));
//                        Fragment selectedFragment = null;
//                        selectedFragment = HomeFragment.newInstance();
//                        if(selectedFragment!=null) {
//                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                            transaction.replace(R.id.container, selectedFragment);
//                            transaction.commit();
//                        }
                        //Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    } else {
                        etAmount.setText("");
                        etPhone.setText("");
                        spOperator.setSelection(0);
                        tvError.setText(jsonObject.getString("message"));
                        ResourceElements.showDialogOk(getActivity(), "Recharge Result", jsonObject.getString("message"));
                        //Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
//                        Fragment selectedFragment = null;
//                        selectedFragment = HomeFragment.newInstance();
//                        if(selectedFragment!=null) {
//                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                            transaction.replace(R.id.container, selectedFragment);
//                            transaction.commit();
//                        }
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
                params.put("location", latitude+","+longitude);
                params.put("method", "prepaidRecharge");
                params.put("token", prefManager.getToken());
                params.put("fromAccount", prefManager.getUserId());
                params.put("number", etPhone.getText().toString());
                params.put("amount", etAmount.getText().toString());
                params.put("operator", spOperator.getSelectedItem().toString());
                params.put("balance", "" + prefManager.getBalance());
                params.put("transactionType", "postpaid");
                params.put("imei", prefManager.getImei());
                return params;
            }
        };
        int socketTimeout = 30000; // 30 seconds. You can change it
        //RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,-1,1);
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
        String id, name, status, note;

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
}
