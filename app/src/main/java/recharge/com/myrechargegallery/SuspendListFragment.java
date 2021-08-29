package recharge.com.myrechargegallery;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SuspendListFragment  extends Fragment {

    View view;
    PrefManager prefManager;

    ArrayList<Item> alItem;
    ListView listView;
    String frmDate = "", toDate = "";
    Button btnGo;
    TextView tvFrmDate, tvToDate;
    String transactionId = "", operator = "", amount = "";
    EditText etSearchString;

    public static SuspendListFragment newInstance() {
        SuspendListFragment fragment = new SuspendListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.suspend_list_fragment, container, false);
        DrawerActivity.setTitle("Suspend List");

        prefManager = new PrefManager(getActivity());

        listView = (ListView) view.findViewById(R.id.lvSuspend);
        tvToDate = (TextView) view.findViewById(R.id.tvToDate);
        tvFrmDate = (TextView) view.findViewById(R.id.tvFrmDate);
        btnGo = (Button) view.findViewById(R.id.btnGo);
        etSearchString = (EditText) view.findViewById(R.id.etSearchString);

        tvFrmDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                frmDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                tvFrmDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        tvToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                toDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                tvToDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });

        loadData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int index = i;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                alertDialogBuilder.setTitle("Make Decision");
                alertDialogBuilder.setMessage("Please make a decision.");
                alertDialogBuilder.setPositiveButton("SUCCESS",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Item item = alItem.get(index);
                                transactionId = item.getId();
                                operator = item.getOperator();
                                amount = item.getAmt();
                                //Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_LONG).show();
                                takeActon("success");
                            }
                        });
                alertDialogBuilder.setNegativeButton("FAIL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Item item = alItem.get(index);
                        transactionId = item.getId();
                        operator = item.getOperator();
                        amount = item.getAmt();
                        //Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_LONG).show();
                        takeActon("fail");
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        return view;
    }

    public void takeActon(final String type) {
        //Toast.makeText(getActivity(), "Called", Toast.LENGTH_LONG).show();
        alItem = new ArrayList<>();
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "transactions.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        loadData();
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
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if(type.equals("fail"))
                    params.put("method", "faliedRecharge");
                else
                    params.put("method", "successRecharge");
                params.put("token", prefManager.getToken());
                //params.put("userId", prefManager.getUserId());
                params.put("fromAccount", prefManager.getUserId());
                params.put("transactionId", transactionId);
                params.put("operator", operator);
                params.put("amount", amount);
                params.put("from", "suspendList");
                params.put("complaintId", "");
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

    public void loadData() {
        //Toast.makeText(getActivity(), "Called", Toast.LENGTH_LONG).show();
        alItem = new ArrayList<>();
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "api.php", new Response.Listener<String>() {
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
                            item.setOperator(jsonObject.getString("operator"));
                            item.setNumber(jsonObject.getString("rechargeNumber"));
                            item.setAmt(jsonObject.getString("amount"));
                            item.setBalance(jsonObject.getString("availableBalance"));
                            //item.setDeductAmt(jsonObject.getString("balanceAfterTransaction"));
                            item.setStatus(jsonObject.getString("transactionStatus"));
                            item.setTransactionId(jsonObject.getString("responseTransactionId"));
                            item.setCommission(jsonObject.getString("comissionReceived"));
                            item.setSerCharge(jsonObject.getString("serviceCharge"));
                            item.setTimestamp(jsonObject.getString("timeStamp"));
                            try {
                                double bal = Double.parseDouble(jsonObject.getString("balanceAfterTransaction").trim());
                                double com = Double.parseDouble(jsonObject.getString("comissionReceived").trim());
                                double total = bal + com;
                                item.setDeductAmt(total + "");
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), e.getMessage()+"",Toast.LENGTH_LONG).show();
                            }
                            alItem.add(item);
                        }
                        listView.setAdapter(new ItemAdapter(getActivity(), alItem));
                    } else {
                        Toast.makeText(getActivity(), jsonObject1.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                    ResourceElements.showDialogOk(getActivity(), "Recharge Result", e.getMessage()+"");
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
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("method", "getSuspends");
                params.put("token", prefManager.getToken());
                params.put("fromAccount", prefManager.getUserId());
                params.put("imei", prefManager.getImei());
                params.put("frmDate", "" + frmDate);
                params.put("toDate", "" + toDate);
                params.put("searchStr", "" + etSearchString.getText().toString().trim());
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
        String id, transactionType, operator, number, amt, status, afterBalance, transactionId, commission, serCharge, balance, deductAmt, timestamp;

        public void setId(String id) {
            this.id = id;
        }

        public void setTransactionType(String transactionType) {
            this.transactionType = transactionType;
        }

        public void setAfterBalance(String afterBalance) {
            this.afterBalance = afterBalance;
        }

        public String getId() {
            return id;
        }

        public String getTransactionType() {
            return transactionType;
        }

        public String getAfterBalance() {
            return afterBalance;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getDeductAmt() {
            return deductAmt;
        }

        public void setDeductAmt(String deductAmt) {
            this.deductAmt = deductAmt;
        };

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public void setAmt(String amt) {
            this.amt = amt;
        }

        public void setStatus(String status) {
            if(status.equals("0")) {
                this.status = "Pending";
            } else if(status.equals("1")) {
                this.status = "Success";
            } else if(status.equals("2")) {
                this.status = "Failed";
            } else if(status.equals("3")) {
                this.status = "Suspend";
            } else if(status.equals("-1")) {
                this.status = "Unknown";
            } else if(status.equals("-2")) {
                this.status = "Unknown";
            }
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public void setCommission(String commission) {
            this.commission = commission;
        }

        public void setSerCharge(String serCharge) {
            this.serCharge = serCharge;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getOperator() {
            return operator;
        }

        public String getNumber() {
            return number;
        }

        public String getAmt() {
            return amt;
        }

        public String getStatus() {
            return status;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getCommission() {
            return commission;
        }

        public String getSerCharge() {
            return serCharge;
        }

        public String getBalance() {
            return balance;
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
                convertView = mInflater.inflate(R.layout.list_item_recharge_report, null);
                holder.tvOperator = (TextView) convertView.findViewById(R.id.tvLIOperator);
                holder.tvNumber = (TextView) convertView.findViewById(R.id.tvLINumber);
                holder.tvAmt = (TextView) convertView.findViewById(R.id.tvLIAmount);
                holder.tvStatus = (TextView) convertView.findViewById(R.id.tvLIStatus);
                holder.tvTransactionId = (TextView) convertView.findViewById(R.id.tvLITrasactionId);
                holder.tvCommission = (TextView) convertView.findViewById(R.id.tvLICommission);
                //holder.tvSerCharge = (TextView) convertView.findViewById(R.id.tvLISerCharge);
                holder.tvBalance = (TextView) convertView.findViewById(R.id.tvLIBalance);
                holder.tvDeductAmt = (TextView) convertView.findViewById(R.id.tvLIDeductAmt);
                holder.tvTimestamp = (TextView) convertView.findViewById(R.id.tvLITimestamp);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvOperator.setText(group.getOperator());
            holder.tvNumber.setText(group.getNumber());
            holder.tvAmt.setText(group.getAmt());

            String status = group.getStatus()+"";
            //Toast.makeText(getActivity(), status, Toast.LENGTH_SHORT).show();
            if(status.equals("Pending")) {
                holder.tvStatus.setBackgroundColor(Color.YELLOW);
            } else if(status.equals("Success")) {
                holder.tvStatus.setBackgroundColor(Color.GREEN);
            } else if(status.equals("Failed")) {
                holder.tvStatus.setBackgroundColor(Color.RED);
            } else if(status.equals("Suspend")) {
                holder.tvStatus.setBackgroundColor(Color.RED);
            }
            holder.tvStatus.setText(group.getStatus()+"");
            holder.tvTransactionId.setText(group.getTransactionId()+"");
            holder.tvCommission.setText(group.getCommission()+"");
            //holder.tvSerCharge.setText(group.getSerCharge()+"");
            //holder.tvBalance.setText(group.getBalance()+"");
            holder.tvStatus.setText(status.toUpperCase());
            double amt = Double.parseDouble(group.getAmt())-Double.parseDouble(group.getCommission());
            holder.tvBalance.setText(amt+"");
            holder.tvDeductAmt.setText(group.getDeductAmt()+"");
            holder.tvTimestamp.setText(group.getTimestamp()+"");

            /*Glide.with(getActivity())
                    .load(group.getPath())
                    .error(R.drawable.tree2)
                    .into(holder.imgPath);*/

            return convertView;
        }
    }

    class ViewHolder {
        TextView tvOperator, tvNumber, tvAmt, tvStatus, tvTransactionId, tvCommission, tvBalance, tvDeductAmt, tvTimestamp;
        //tvSerCharge
    }
}
