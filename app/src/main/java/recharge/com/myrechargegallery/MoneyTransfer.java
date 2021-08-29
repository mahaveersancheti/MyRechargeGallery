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
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MoneyTransfer extends Fragment {

    View view;
    Button btnAddBeneficiary;
    Button btnSearch;
    PrefManager prefManager;
    String imei;
    EditText etSearch;
    ListView listView;
    ArrayList<Item> alItem;
    String searchString = "";

    TextView tvMobile, tvUsedAmt, tvRemainingAmt, tvLimit;
    LinearLayout llMsg;
    int remainingAmt = 0;

    GPSTracker gps;
    double latitude = 0.0;
    double longitude = 0.0;

    public static MoneyTransfer newInstance() {
        MoneyTransfer fragment = new MoneyTransfer();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.money_transfer_fragment, container, false);
        DrawerActivity.setTitle("Money Transfer");

        prefManager = new PrefManager(getActivity());

        listView = (ListView) view.findViewById(R.id.lvUserList);
        btnAddBeneficiary = (Button) view.findViewById(R.id.btnAddBeneficiary);
        btnAddBeneficiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment selectedFragment = null;
                selectedFragment = AddBeneficiaty.newInstance();
                if(selectedFragment!=null) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, selectedFragment);
                    transaction.commit();
                }
            }
        });

        llMsg = (LinearLayout) view.findViewById(R.id.llMsg);
        tvMobile = (TextView) view.findViewById(R.id.tvMobile);
        tvUsedAmt = (TextView) view.findViewById(R.id.tvUsedAmt);
        tvRemainingAmt = (TextView) view.findViewById(R.id.tvRemainingAmt);
        tvLimit = (TextView) view.findViewById(R.id.tvLimitAmt);

        //getData();

        etSearch = (EditText) view.findViewById(R.id.etSearchString);
        btnSearch = (Button) view.findViewById(R.id.btnGo);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchString = etSearch.getText().toString().trim();
                if(searchString.length()!=10) {
                    etSearch.setError("Invalid value");
                } else {
                    btnAddBeneficiary.setVisibility(View.VISIBLE);
                    getData();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final Item obj = alItem.get(i);
                String id = obj.getId();
                String name = obj.getName();
                String accountNumber = obj.getAccNumber();
                String mobile = obj.getMobile();
                String ifsc = obj.getIfsc();

                String message = "Please enter amount to transfer\nName : "+name+"\nA/C No : "+accountNumber+"\nIFSC : "+ifsc+"\nMobile : "+mobile+"\n";
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Transfer Money");
                builder.setMessage(message);
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
                input.setHint("Amount between 10-5000");
                input.setPadding(20,20,20,20);
                input.setBackgroundResource(R.drawable.btn_back);
                builder.setView(input);
                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String amount = input.getText().toString();
                        int amt = Integer.parseInt(amount);
                        obj.setAmount(amount);
                        if(amount.trim().length()>0) {
                            if(amt>0 && amt<=5000 && remainingAmt>=amt) {
                                moneyTransfer(obj);
                                //Toast.makeText(getActivity(), "Test", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "Amount can not be > 5000 or check limit", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Amount can not be empty", Toast.LENGTH_LONG).show();
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


    public void moneyTransfer(final Item item) {
        alItem = new ArrayList<>();
        //Toast.makeText(getActivity(), ""+prefManager.getBalance(), Toast.LENGTH_LONG).show();
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "money_transfer.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
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
                        //Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    } else {
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
                params.put("method", "moneyTransfer");
                params.put("token", prefManager.getToken());
                params.put("fromAccount", prefManager.getUserId());
                params.put("number", item.getAccNumber());
                params.put("amount", item.getAmount()+"");
                params.put("senderId", item.getSenderId()+"");
                params.put("beneficiaryId", item.getBeneficiaryId()+"");
                params.put("beneficiaryName", item.getBeneficiaryName()+"");
                params.put("mobile", item.getMobile()+"");
                params.put("accType", item.getAccType()+"");
                params.put("ifsc", item.getIfsc()+"");
                params.put("operator", "money_transfer");
                params.put("balance", "" + prefManager.getBalance());
                params.put("transactionType", "money_transfer");
                params.put("imei", prefManager.getImei());
                params.put("toAccount", item.getId());
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

    public void getData() {
        alItem = new ArrayList<>();
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "money_transfer.php", new Response.Listener<String>() {
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
                            Item item = new Item();
                            item.setId(jsonObject.getString("id"));
                            item.setName(jsonObject.getString("name"));
                            item.setIfsc(jsonObject.getString("ifsc"));
                            item.setMobile(jsonObject.getString("mobile"));
                            item.setAccNumber(jsonObject.getString("accountNumber"));
                            item.setAccType(jsonObject.getString("accountType"));
                            item.setBeneficiaryId(jsonObject.getString("beneficiaryId"));
                            item.setSenderId(jsonObject.getString("senderId"));
                            item.setBeneficiaryName(jsonObject.getString("beneficiaryName"));
                            alItem.add(item);
                        }
                        llMsg.setVisibility(View.VISIBLE);
                        tvMobile.setText(searchString.trim());
                        tvUsedAmt.setText(jsonObject1.getString("usedAmt"));
                        tvRemainingAmt.setText(jsonObject1.getString("remainingAmt"));
                        tvLimit.setText(jsonObject1.getString("limit"));
                        try {
                            remainingAmt = Integer.parseInt(jsonObject1.getString("remainingAmt"));
                        } catch (Exception e) {}

                    } else {
                        llMsg.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), jsonObject1.getString("message"), Toast.LENGTH_LONG).show();
                    }
                    listView.setAdapter(new ItemAdapter(getActivity(), alItem));
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
                params.put("method", "getAllBeneficiaty");
                params.put("transactionType", "money_transfer");
                params.put("token", prefManager.getToken());
                params.put("fromAccount", prefManager.getUserId());
                params.put("searchString", "" + searchString.trim());
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

    class Item {
        String id, name, mobile, beneficiaryName, accNumber, accType, ifsc, bankName, beneficiaryId, senderId, amount;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
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

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getBeneficiaryName() {
            return beneficiaryName;
        }

        public void setBeneficiaryName(String beneficiaryName) {
            this.beneficiaryName = beneficiaryName;
        }

        public String getAccNumber() {
            return accNumber;
        }

        public void setAccNumber(String accNumber) {
            this.accNumber = accNumber;
        }

        public String getAccType() {
            return accType;
        }

        public void setAccType(String accType) {
            this.accType = accType;
        }

        public String getIfsc() {
            return ifsc;
        }

        public void setIfsc(String ifsc) {
            this.ifsc = ifsc;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getBeneficiaryId() {
            return beneficiaryId;
        }

        public void setBeneficiaryId(String beneficiaryId) {
            this.beneficiaryId = beneficiaryId;
        }

        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }
    }

    class ItemAdapter extends BaseAdapter {
        Context context;
        private LayoutInflater mInflater;
        private ArrayList<Item> arrayList;// = new ArrayList<Place>();

        public ItemAdapter(Context context, ArrayList<Item> arrayList) {
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
            final Item group = arrayList.get(position);
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item_beneficiary, null);
                holder.tvId = (TextView) convertView.findViewById(R.id.tvLIBId);
                holder.tvName = (TextView) convertView.findViewById(R.id.tvLIShopName);
                holder.tvNumber = (TextView) convertView.findViewById(R.id.tvLIMobile);
                holder.tvIFSC = (TextView) convertView.findViewById(R.id.tvLIIfsc);
                holder.tvAccNo = (TextView) convertView.findViewById(R.id.tvLIAccNo);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvId.setText(group.getBeneficiaryId());
            holder.tvNumber.setText(group.getMobile());
            holder.tvIFSC.setText(group.getIfsc());
            holder.tvName.setText(group.getName());
            holder.tvAccNo.setText(group.getAccNumber());

            return convertView;
        }
    }

    class ViewHolder {
        TextView tvId, tvNumber, tvIFSC, tvName, tvAccNo;
    }
}

