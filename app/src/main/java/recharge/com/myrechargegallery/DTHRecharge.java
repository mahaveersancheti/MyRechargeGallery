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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

public class DTHRecharge extends Fragment {

    View view;
    PrefManager prefManager;
    Spinner spOperator;
    ArrayList<String> alOperators;
    EditText etPhone, etAmount;
    Button btnRecharge;
    TextView tvError;
    ArrayList<Operator> alOp, alOffers, alPlans, alAddOnPlans;

    GPSTracker gps;
    double latitude = 0.0;
    double longitude = 0.0;
    String number = "", op = "", amt = "", operatorStr = "";
    Dialog dialog;

    public static DTHRecharge newInstance() {
        DTHRecharge fragment = new DTHRecharge();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dth_recharge_fragment, container, false);
        DrawerActivity.setTitle("DTH Recharge");

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

        etAmount.setText(amt);
        etPhone.setText(number);

        loadOperators();

        btnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_LONG).show();
                String number = etPhone.getText().toString();
                String amount = etAmount.getText().toString();
                String operator = spOperator.getSelectedItem().toString().trim();

                String errorMsg = "";
                String numberPattern = "[789][0-9]{9}";
                boolean flag = true;
                operator = operator.toLowerCase();
                if(operator.contains("dish")) {
                    if(!(number.startsWith("0") && number.length()==11)) {
                        flag = false;
                        errorMsg = "Number must start with 0 and length is 11 digits.";
                        etPhone.setError("Number must start with 0 and length is 11 digits.");
                    }
                } else if(operator.contains("airtel")) {
                    if(!(number.startsWith("3") && number.length()==10)) {
                        flag = false;
                        errorMsg = "Number must start with 3 and length is 10 digits.";
                        etPhone.setError("Number must start with 3 and length is 10 digits.");
                    }
                } else if(operator.contains("tata")) {
                    if(!((number.startsWith("10") || number.startsWith("11") || number.startsWith("12") || number.startsWith("13") || number.startsWith("14") || number.startsWith("15") || number.startsWith("16") || number.startsWith("17")) && number.length()==10)) {
                        flag = false;
                        errorMsg = "Number must start with either 10 or 11 or 12 or 13 and length is 10 digits.";
                        etPhone.setError("Number must start with either 10 or 11 or 12 or 13 and length is 10 digits.");
                    }
                }
                if (amount.trim().length()==0) {
                    flag = false;
                    errorMsg = "Invalid Amount";
                    etAmount.setError("Invalid Number");
                }
//                if(!number.matches(numberPattern)) {
//                    flag = false;
//                    etPhone.setError("Invalid value");
//                }
                String op = spOperator.getSelectedItem().toString();
                if(!op.trim().equalsIgnoreCase("Select Operator")) {
                    if(flag) {
                        if(prefManager.getBalance()<Integer.parseInt(amount)) {
                            etAmount.setError("Insufficient Balance");
                            return;
                        }
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
                        Toast.makeText(getActivity(), "" + errorMsg, Toast.LENGTH_LONG).show();
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

        Button btnCustInfo = (Button) view.findViewById(R.id.btnCustInfo);
        Button btnPlans = (Button) view.findViewById(R.id.btnPlans);
        Button btnHeavyRefresh = (Button) view.findViewById(R.id.btnHeavyRefresh);
        Button btnCheckROffer = (Button) view.findViewById(R.id.btnCheckROffer);

        btnCustInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String operator = spOperator.getSelectedItem().toString().trim();
                String number = etPhone.getText().toString().trim();
                String numberPattern = "[6789][0-9]{9}";

                if(!operator.trim().equalsIgnoreCase("Select Operator")) {
                    String operatorCode = "";
                    if(validateNumber(operator, number)) {
//                        Airteldth, TataSky, Videocon, Sundirect, Dishtv
                        operator = operator.toLowerCase();
                        boolean f = true;
                        if(operator.contains("tata")) {
                            operatorStr = "TataSky";
                        } else if(operator.contains("videocon")) {
                            operatorStr = "Videocon";
                        } else if(operator.contains("airtel")) {
                            operatorStr = "Airteldth";
                        } else if(operator.contains("sun")) {
                            operatorStr = "Sundirect";
                        } else if(operator.contains("dish")) {
                            operatorStr = "Dishtv";
                        } else {
                            f = false;
                            Toast.makeText(getActivity(), "Sorry, service not available for this operator.", Toast.LENGTH_LONG).show();
                        }
                        if(f)
                            checkCustomerInfo();

                    } else {
                        etPhone.setError("Invalid Number");
                    }
                } else {
                    Toast.makeText(getActivity(), "Please select operator first.", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnPlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                String operator = spOperator.getSelectedItem().toString().trim();
                String number = etPhone.getText().toString().trim();
                String numberPattern = "[6789][0-9]{9}";

                if(!operator.trim().equalsIgnoreCase("Select Operator")) {
                    String operatorCode = "";
                    if(validateNumber(operator, number)) {
//                        Airtel dth, Dish TV, Tata Sky, Sun Direct, Videocon
                        operator = operator.toLowerCase();
                        boolean f = true;
                        if(operator.contains("tata")) {
                            operatorStr = "Tata Sky";
                        } else if(operator.contains("videocon")) {
                            operatorStr = "Videocon";
                        } else if(operator.contains("airtel")) {
                            operatorStr = "Airtel dth";
                        } else if(operator.contains("sun")) {
                            operatorStr = "Sun Direct";
                        } else if(operator.contains("dish")) {
                            operatorStr = "Dish TV";
                        } else {
                            f = false;
                            Toast.makeText(getActivity(), "Sorry, service not available for this operator.", Toast.LENGTH_LONG).show();
                        }
                        if(f)
                            DTHPlans();

                    } else {
                        etPhone.setError("Invalid value");
                    }
                } else {
                    Toast.makeText(getActivity(), "Please select operator first.", Toast.LENGTH_LONG).show();
                }


            }
        });
        btnHeavyRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                String operator = spOperator.getSelectedItem().toString().trim();
                String number = etPhone.getText().toString().trim();
                String numberPattern = "[6789][0-9]{9}";

                if(!operator.trim().equalsIgnoreCase("Select Operator")) {
                    String operatorCode = "";
                    if(validateNumber(operator, number)) {
//                        Airteldth, TataSky, Videocon, Sundirect, Dishtv
                        operator = operator.toLowerCase();
                        boolean f = true;
                        if(operator.contains("tata")) {
                            operatorStr = "TataSky";
                        } else if(operator.contains("videocon")) {
                            operatorStr = "Videocon";
                        } else if(operator.contains("airtel")) {
                            operatorStr = "Airteldth";
                        } else if(operator.contains("sun")) {
                            operatorStr = "Sundirect";
                        } else if(operator.contains("dish")) {
                            operatorStr = "Dishtv";
                        } else {
                            f = false;
                            Toast.makeText(getActivity(), "Sorry, service not available for this operator.", Toast.LENGTH_LONG).show();
                        }
                        if(f)
                            DTHRHeavyRefresh();

                    } else {
                        etPhone.setError("Invalid value");
                    }
                } else {
                    Toast.makeText(getActivity(), "Please select operator first.", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnCheckROffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String operator = spOperator.getSelectedItem().toString().trim();
                String number = etPhone.getText().toString().trim();
                String numberPattern = "[6789][0-9]{9}";

                if(!operator.trim().equalsIgnoreCase("Select Operator")) {
                    String operatorCode = "";
                    if(validateNumber(operator, number)) {
//                        AirtelDTH, Sundirect
                        operator = operator.toLowerCase();
                        boolean f = true;
                        if(operator.contains("airtel")) {
                            operatorStr = "AirtelDTH";
                        } else if(operator.contains("sun")) {
                            operatorStr = "Sundirect";
                        } else {
                            f = false;
                            Toast.makeText(getActivity(), "Sorry, service not available for this operator.", Toast.LENGTH_LONG).show();
                        }
                        if(f)
                            DTHROffer();

                    } else {
                        etPhone.setError("Invalid value");
                    }
                } else {
                    Toast.makeText(getActivity(), "Please select operator first.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    public boolean validateNumber(String operator, String number) {
        boolean flag = true;
        operator = operator.toLowerCase();
        if(operator.contains("dish")) {
            if(!(number.startsWith("0") && number.length()==11)) {
                flag = false;
//                errorMsg = "Number must start with 0 and length is 11 digits.";
                etPhone.setError("Number must start with 0 and length is 11 digits.");
            }
        } else if(operator.contains("airtel")) {
            if(!(number.startsWith("3") && number.length()==10)) {
                flag = false;
//                errorMsg = "Number must start with 3 and length is 10 digits.";
                etPhone.setError("Number must start with 3 and length is 10 digits.");
            }
        } else if(operator.contains("tata")) {
            if(!((number.startsWith("10") || number.startsWith("11") || number.startsWith("12") || number.startsWith("13")) && number.length()==10)) {
                flag = false;
//                errorMsg = "Number must start with either 10 or 11 or 12 or 13 and length is 10 digits.";
                etPhone.setError("Number must start with either 10 or 11 or 12 or 13 and length is 10 digits.");
            }
        }
        return flag;
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
                params.put("type", "dth");
                params.put("fromAccount", prefManager.getUserId());
                params.put("token", prefManager.getToken());
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
                params.put("transactionType", "dth");
                params.put("imei", prefManager.getImei());
                return params;
            }
        };
//        int socketTimeout = 30000; // 30 seconds. You can change it
//        //RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,-1,1);
//        stringRequest.setRetryPolicy(policy);
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
        String id, name, status, note, balance, nextRechargeDate, planName, monthlyRecharge;
        String month1 = "", month3 = "", month6 = "", year1 = "";

        public String getMonth1() {
            return month1;
        }

        public void setMonth1(String month1) {
            this.month1 = month1;
        }

        public String getMonth3() {
            return month3;
        }

        public void setMonth3(String month3) {
            this.month3 = month3;
        }

        public String getMonth6() {
            return month6;
        }

        public void setMonth6(String month6) {
            this.month6 = month6;
        }

        public String getYear1() {
            return year1;
        }

        public void setYear1(String year1) {
            this.year1 = year1;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getNextRechargeDate() {
            return nextRechargeDate;
        }

        public void setNextRechargeDate(String nextRechargeDate) {
            this.nextRechargeDate = nextRechargeDate;
        }

        public String getPlanName() {
            return planName;
        }

        public void setPlanName(String planName) {
            this.planName = planName;
        }

        public String getMonthlyRecharge() {
            return monthlyRecharge;
        }

        public void setMonthlyRecharge(String monthlyRecharge) {
            this.monthlyRecharge = monthlyRecharge;
        }

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
                holder.tvOfferDesc = (TextView) convertView.findViewById(R.id.tvOfferDesc);
                holder.btnAmt = (Button) convertView.findViewById(R.id.btnAmt);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvOfferDesc.setText(group.getNote());
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
        TextView tvOfferDesc;
        Button btnAmt;
    }

    public void checkCustomerInfo() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "curl.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject1 = new JSONObject(response);
                    Object record = jsonObject1.getJSONArray("records");
//                    if(record instanceof JSONObject) {
//                        JSONObject jRecord = jsonObject1.getJSONObject("records");
//                    } else if(record instanceof JSONArray) {
                        JSONArray jsonArray = jsonObject1.getJSONArray("records");
                        String message = "";
                        for(int i=0;i<jsonArray.length();i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Operator obj = new Operator();
                            obj.setName(jsonObject.getString("customerName"));
                            obj.setBalance(jsonObject.getString("Balance"));
                            obj.setNextRechargeDate(jsonObject.getString("NextRechargeDate"));
                            obj.setPlanName(jsonObject.getString("planname"));
                            obj.setStatus(jsonObject.getString("status"));
                            obj.setMonthlyRecharge(jsonObject.getString("MonthlyRecharge"));
                            message = "Name: " + jsonObject.getString("customerName") + "\n"
                                    + "Balance: " + jsonObject.getString("Balance") + "\n"
                                    + "Recharge Date: " + jsonObject.getString("NextRechargeDate") + "\n"
                                    + "Plan: " + jsonObject.getString("planname") + "\n"
                                    + "Monthly Recharge: " + jsonObject.getString("MonthlyRecharge") + "\n"
                                    + "Status: " + jsonObject.getString("status");
                        }
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                        alertDialogBuilder.setTitle("Customer Details");
                        alertDialogBuilder.setMessage(message);
                        alertDialogBuilder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
//                    }

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Please check number", Toast.LENGTH_LONG).show();
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
                params.put("method", "DTHCustomerInfo");
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

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.list_view_layout);

        ListView listView = (ListView) dialog.findViewById(R.id.lv);
        listView.setAdapter(new ItemAdapter(getActivity(), alOffers));
        dialog.show();
    }

    public void DTHPlans() {
        alPlans = new ArrayList<>();
        alAddOnPlans = new ArrayList<>();
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "curl.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject1 = new JSONObject(response);
                    JSONObject jRecords = jsonObject1.getJSONObject("records");
                    JSONArray jPlan = jRecords.getJSONArray("Plan");
                    for(int i=0;i<jPlan.length();i++) {
                        JSONObject jsonObject = jPlan.getJSONObject(i);
                        Operator obj = new Operator();
                        obj.setName(jsonObject.getString("plan_name"));
                        obj.setNote(jsonObject.getString("desc"));

                        JSONObject rs = jsonObject.getJSONObject("rs");
                        if(!rs.isNull("1 MONTHS"))
                            obj.setMonth1(rs.getString("1 MONTHS"));
                        if(!rs.isNull("3 MONTHS"))
                            obj.setMonth3(rs.getString("3 MONTHS"));
                        if(!rs.isNull("6 MONTHS"))
                            obj.setMonth6(rs.getString("6 MONTHS"));
                        if(!rs.isNull("1 YEAR"))
                            obj.setYear1(rs.getString("1 YEAR"));

                        alPlans.add(obj);
                    }

                    JSONArray jAddOn = jRecords.getJSONArray("Add-On Pack");
                    for(int i=0;i<jAddOn.length();i++) {
                        JSONObject jsonObject = jAddOn.getJSONObject(i);
                        Operator obj = new Operator();
                        obj.setName(jsonObject.getString("plan_name"));
                        obj.setNote(jsonObject.getString("desc"));

                        JSONObject rs = jsonObject.getJSONObject("rs");
                        if(!rs.isNull("1 MONTHS"))
                            obj.setMonth1(rs.getString("1 MONTHS"));
                        if(!rs.isNull("3 MONTHS"))
                            obj.setMonth3(rs.getString("3 MONTHS"));
                        if(!rs.isNull("6 MONTHS"))
                            obj.setMonth6(rs.getString("6 MONTHS"));
                        if(!rs.isNull("1 YEAR"))
                            obj.setYear1(rs.getString("1 YEAR"));

                        alAddOnPlans.add(obj);
                    }
                    showListPlans(alPlans);
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
                params.put("method", "DTHPlans");
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

    public void DTHROffer() {
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
                params.put("method", "DTHROffer");
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

    public void DTHRHeavyRefresh() {
        alOffers = new ArrayList<>();
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "curl.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject1 = new JSONObject(response);
                    JSONObject jRecords = jsonObject1.getJSONObject("records");
                    String message = "Name: " + jRecords.getString("customerName") + "\n"
                            + "Description: " + jRecords.getString("desc");
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                    alertDialogBuilder.setTitle("Customer Details");
                    alertDialogBuilder.setMessage(message);
                    alertDialogBuilder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

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
                params.put("method", "DTHRHeavyRefresh");
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


    public void showListPlans(ArrayList<Operator> alPlans) {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.list_view_layout);

        ListView listView = (ListView) dialog.findViewById(R.id.lv);
        listView.setAdapter(new ItemAdapterPlan(getActivity(), alPlans));
        dialog.show();
    }

    class ItemAdapterPlan extends BaseAdapter {
        Context context;
        private LayoutInflater mInflater;
        private ArrayList<Operator> arrayList;// = new ArrayList<Place>();

        public ItemAdapterPlan(Context context, ArrayList<Operator> arrayList) {
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
            final ViewHolderPlan holder;
            final Operator group = arrayList.get(position);
            if (convertView == null) {
                holder = new ViewHolderPlan();
                convertView = mInflater.inflate(R.layout.item_list_offer_plans, null);
                holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                holder.tvOfferDesc = (TextView) convertView.findViewById(R.id.tvOfferDesc);
                holder.btn1Month = (Button) convertView.findViewById(R.id.btn1Month);
                holder.btn3Month = (Button) convertView.findViewById(R.id.btn3Month);
                holder.btn6Month = (Button) convertView.findViewById(R.id.btn6Month);
                holder.btn1Year = (Button) convertView.findViewById(R.id.btn1Year);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolderPlan) convertView.getTag();
            }
            holder.tvName.setText(group.getName());
            holder.tvOfferDesc.setText(group.getNote());
            holder.btn1Month.setText(group.getMonth1() + "");
            holder.btn1Month.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etAmount.setText(holder.btn1Month.getText().toString() + "");
                    dialog.dismiss();
                }
            });
            holder.btn3Month.setText(group.getMonth3() + "");
            holder.btn3Month.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etAmount.setText(holder.btn3Month.getText().toString() + "");
                    dialog.dismiss();
                }
            });
            holder.btn6Month.setText(group.getMonth6() + "");
            holder.btn6Month.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etAmount.setText(holder.btn6Month.getText().toString() + "");
                    dialog.dismiss();
                }
            });
            holder.btn1Year.setText(group.getYear1() + "");
            holder.btn1Year.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etAmount.setText(holder.btn1Year.getText().toString() + "");
                    dialog.dismiss();
                }
            });

            return convertView;
        }
    }

    class ViewHolderPlan {
        TextView tvName, tvOfferDesc;
        Button btn1Month, btn3Month, btn6Month, btn1Year;
    }
}
