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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ElectricityBillPayment extends Fragment {

    View view;
    PrefManager prefManager;

    EditText etConsumerNumber;
    Button btnGo, btnPay;
    TextView tvResult, tvConNo, tvName, tvPC, tvBU, tvPayable, tvAmt, tvDueDate;
    Consumer consumer = null;
    Spinner spOperator;
    ArrayList<String> alOperator, opCodes;

    GPSTracker gps;
    double latitude = 0.0;
    double longitude = 0.0;

    public static ElectricityBillPayment newInstance() {
        ElectricityBillPayment fragment = new ElectricityBillPayment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.electricity_bill_fragment, container, false);
        DrawerActivity.setTitle("Electric Bill");

        prefManager = new PrefManager(getActivity());

        etConsumerNumber = (EditText) view.findViewById(R.id.etConsumerNumber);
        btnGo = (Button) view.findViewById(R.id.btnGo);
        btnPay = (Button) view.findViewById(R.id.btnPay);
        tvResult = (TextView) view.findViewById(R.id.tvResult);

        tvConNo = (TextView) view.findViewById(R.id.tvConsumerNo);
        tvName = (TextView) view.findViewById(R.id.tvConsumerName);
        tvPC = (TextView) view.findViewById(R.id.tvProcessingCycle);
        tvBU = (TextView) view.findViewById(R.id.tvBillingUnit);
        tvPayable = (TextView) view.findViewById(R.id.tvPayableAmt);
        tvAmt = (TextView) view.findViewById(R.id.tvBillingAmt);
        tvDueDate = (TextView) view.findViewById(R.id.tvBillingDue);
        spOperator = (Spinner) view.findViewById(R.id.spOperator);
        alOperator = new ArrayList<>();
        opCodes = new ArrayList<>();
        alOperator.add("MSEB");opCodes.add("50");
        alOperator.add("AJMER VIDYUT VITRAN NIGAM – R.J");opCodes.add("70");
        alOperator.add("APDCL - ASSAM");opCodes.add("71");
        alOperator.add("BEST UNDERTAKING - MUMBAI");opCodes.add("72");
        alOperator.add("BSES RAJDHANI - DELHI");opCodes.add("73");
        alOperator.add("BSES YAMUNA - DELHI");opCodes.add("74");
        alOperator.add("CESC - WEST BENGAL");opCodes.add("75");
        alOperator.add("CSPDCL – CHHATTISGARH");opCodes.add("76");
        alOperator.add("JAIPUR VIDYUT VITRAN NIGAM – R.J");opCodes.add("77");
        alOperator.add("JAMSHEDPUR UTILITIES & SERVICES");opCodes.add("78");
        alOperator.add("JODHPUR VIDYUT VITRAN NIGAM – R.J");opCodes.add("79");
        alOperator.add("MADHYA KSHETRA VITARAN - M.P");opCodes.add("80");
        alOperator.add("NOIDA POWER - NOIDA");opCodes.add("81");
        alOperator.add("PASCHIM KSHETRA VITARAN - M.P");opCodes.add("82");
        alOperator.add("TATA POWER - DELHI");opCodes.add("84");
        alOperator.add("TORRENT POWER");opCodes.add("85");
        alOperator.add("TSECL - TRIPURA");opCodes.add("86");
        alOperator.add("BESCOM – BENGALURU");opCodes.add("87");
        alOperator.add("INDIA POWER");opCodes.add("88");
        spOperator.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.tv, alOperator));
        spOperator.setVisibility(View.GONE);

        //etConsumerNumber.setText("049016304467");

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String consumerNumber = etConsumerNumber.getText().toString().trim();
                if(Config.hasConnection(getActivity())) {
                    if(consumerNumber.length()>0)
                        getBillDetails();
                    else
                        etConsumerNumber.setText("Invalid value");
                } else {
                    Toast.makeText(getActivity(), "Internet connection problem", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Please confirm your decision.";
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Electricity Bill Payment");
                builder.setMessage(message);
//                final EditText input = new EditText(getActivity());
//                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
//                input.setHint("Amount");
//                input.setPadding(20,20,20,20);
//                input.setBackgroundResource(R.drawable.btn_back);
//                builder.setView(input);
                builder.setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        makePayment();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            }
        });

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


    public void getBillDetails() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "money_transfer.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    btnPay.setVisibility(View.VISIBLE);
                    spOperator.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    String str = "";
                    str += "\nStatus : " + jsonObject.getString("Status");
                    str += "\nConsumer No. : " + jsonObject.getString("consumerNumber");
                    str += "\nName : " + jsonObject.getString("ConsumerName");
                    str += "\nBilling Unit : " + jsonObject.getString("billingUnit");
                    str += "\nProcessing Cycle : " + jsonObject.getString("processiongCycle");
                    str += "\nBilling Due : " + jsonObject.getString("billDueDate");
                    str += "\nBilling Amount : " + jsonObject.getString("billAmounts");
                    str += "\nPayable Amount : " + jsonObject.getString("AmountToPay");
                    //tvResult.setText(str);

                    tvConNo.setText(jsonObject.getString("consumerNumber"));
                    tvName.setText(jsonObject.getString("ConsumerName"));
                    tvPC.setText(jsonObject.getString("processiongCycle"));
                    tvBU.setText(jsonObject.getString("billingUnit"));
                    tvPayable.setText(jsonObject.getString("AmountToPay"));
                    tvAmt.setText(jsonObject.getString("billAmounts"));
                    tvDueDate.setText(jsonObject.getString("billDueDate"));

                    consumer = new Consumer();
                    consumer.setName(jsonObject.getString("ConsumerName"));
                    consumer.setNumber(jsonObject.getString("consumerNumber"));
                    consumer.setBillUnit(jsonObject.getString("billingUnit"));
                    consumer.setProcessingCycle(jsonObject.getString("processiongCycle"));
                    consumer.setAmount(jsonObject.getString("AmountToPay"));
                    consumer.setType(jsonObject.getString("ConnectionType"));

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
                params.put("method", "getElecticityBillDetails");
                params.put("token", prefManager.getToken());
                params.put("fromAccount", prefManager.getUserId());
                params.put("consumerNumber", etConsumerNumber.getText().toString());
                params.put("balance", "" + prefManager.getBalance());
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

    public void makePayment() {

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "money_transfer.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    btnPay.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
                        //Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        String message = jsonObject.getString("message") + "\n\nTransaction Id : " + jsonObject.getString("transactionId");
                        ResourceElements.showDialogOk(getActivity(), "Transaction Result", message);
                        prefManager.setBalance(Float.parseFloat(jsonObject.getString("balance")));
                        Fragment selectedFragment = null;
                        selectedFragment = HomeFragment.newInstance();
                        if(selectedFragment!=null) {
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.container, selectedFragment);
                            transaction.commit();
                        }
                    } else {
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
                params.put("method", "electricBill");
                params.put("token", prefManager.getToken());
                params.put("fromAccount", prefManager.getUserId());
                params.put("balance", "" + prefManager.getBalance());
                params.put("transactionType", "electricity");
                params.put("imei", prefManager.getImei());
                params.put("number", consumer.getNumber());
                params.put("name", consumer.getName());
                params.put("pc", consumer.getProcessingCycle());
                params.put("bu", consumer.getBillUnit());
                params.put("type", consumer.getType());
                params.put("amount", consumer.getAmount());
                params.put("operator", opCodes.get(spOperator.getSelectedItemPosition()));
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

    class Consumer {
        String name, number, amount, billUnit, processingCycle, operator, type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getBillUnit() {
            return billUnit;
        }

        public void setBillUnit(String billUnit) {
            this.billUnit = billUnit;
        }

        public String getProcessingCycle() {
            return processingCycle;
        }

        public void setProcessingCycle(String processingCycle) {
            if(processingCycle.trim().length()==1)
                this.processingCycle = "0"+processingCycle;
            else
                this.processingCycle = processingCycle;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}

