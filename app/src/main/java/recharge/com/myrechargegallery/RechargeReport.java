package recharge.com.myrechargegallery;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RechargeReport extends Fragment {

    View view;
    PrefManager prefManager;
    ListView listView;
    ArrayList<Item> alItem;

    TextView tvFrmDate, tvToDate, tvRefresh;
    EditText etNumber;
    //ImageView ivRefresh;
    String frmDate = "", toDate = "";

    Button btnGo;

    public static RechargeReport newInstance() {
        RechargeReport fragment = new RechargeReport();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.recharge_report_fragment, container, false);
        DrawerActivity.setTitle("Recharge Report");

        prefManager = new PrefManager(getActivity());

        listView = (ListView) view.findViewById(R.id.lvReport);
        etNumber = (EditText) view.findViewById(R.id.etSearchString);
        tvFrmDate = (TextView) view.findViewById(R.id.tvFrmDate);
        tvToDate = (TextView) view.findViewById(R.id.tvToDate);
        tvRefresh = (TextView) view.findViewById(R.id.tvRefresh);
        btnGo = (Button) view.findViewById(R.id.btnGo);

        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
//        frmDate = mYear + "-" + (mMonth + 1) + "-" + mDay;
//        tvFrmDate.setText(mDay + "-" + (mMonth + 1) + "-" + mYear);

//        toDate = mYear + "-" + (mMonth + 1) + "-" + mDay;
//        tvToDate.setText(mDay + "-" + (mMonth + 1) + "-" + mYear);

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
                final Item item = alItem.get(i);
                final String id = item.getId();
                boolean flag = true;
                for(int j=0;j<alItem.size();j++) {
                    Item obj = alItem.get(j);
                    String refId = obj.getRefId();
                    if(id.equals(refId)) {
                        flag = false;
                        Toast.makeText(getActivity(), "Response is already given", Toast.LENGTH_LONG).show();
                        break;
                    } else {
                    }
                }
                if(flag) {
                    String msg = "Operator : " + item.getOperator() + "\nNumber : " + item.getNumber() + "\n";
                    String status = item.getStatus();
                    if(status.equalsIgnoreCase("pending")) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                        alertDialogBuilder.setTitle("Are you sure?");
                        alertDialogBuilder.setMessage(msg);
                        alertDialogBuilder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        registerComplaint(id);
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
                        Toast.makeText(getActivity(), "You can only complaint for pending transaction.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        return view;
    }

    public void registerComplaint(final String transactionId) {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "api.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
                        ResourceElements.showDialogOk(getActivity(), "Result", jsonObject.getString("message"));
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
                    Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("method", "registerComplaint");
                params.put("token", prefManager.getToken());
                params.put("transactionId", "" + transactionId);
                //params.put("userId", prefManager.getUserId());
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

    public void loadData() {
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
                            item.setToAccount(jsonObject.getString("toAccount"));
                            item.setFromAccount(jsonObject.getString("fromAccount"));
                            item.setId(jsonObject.getString("id"));
                            item.setOperator(jsonObject.getString("operator"));
                            item.setNumber(jsonObject.getString("rechargeNumber"));
                            item.setAmt(jsonObject.getString("amount"));
                            item.setBalance(jsonObject.getString("availableBalance"));
                            item.setToBalance(jsonObject.getString("toAvailableBalance"));
                            //item.setDeductAmt(jsonObject.getString("balanceAfterTransaction"));
                            item.setToDeductAmt(jsonObject.getString("toBalanceAfterTransaction"));
                            item.setStatus(jsonObject.getString("transactionStatus"));
                            item.setTransactionId(jsonObject.getString("responseTransactionId"));
                            item.setCommission(jsonObject.getString("comissionReceived"));
                            item.setSerCharge(jsonObject.getString("serviceCharge"));
                            item.setTimestamp(jsonObject.getString("timeStamp"));
                            item.setTransactionType(jsonObject.getString("transactionType"));
                            item.setRefId(jsonObject.getString("refTransactionId"));
                            item.setMessage(jsonObject.getString("message"));
                            try {
                                double bal = Double.parseDouble(jsonObject.getString("balanceAfterTransaction").trim());
                                double com = Double.parseDouble(jsonObject.getString("comissionReceived").trim());
                                //double total = bal + com;
                                item.setDeductAmt(bal + "");
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), e.getMessage()+"",Toast.LENGTH_LONG).show();
                            }
                            alItem.add(item);
                        }

                        tvFrmDate.setText("From Date");
                        tvToDate.setText("To Date");
                        frmDate = ""; toDate = "";
                    } else {
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
                params.put("method", "getTransactionHistory");
                params.put("token", prefManager.getToken());
                params.put("frmDate", "" + frmDate);
                params.put("toDate", "" + toDate);
                params.put("number", "" + etNumber.getText().toString().trim());
                //params.put("userId", prefManager.getUserId());
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

    class Item {
        String id, transactionType, operator, number, amt, status, afterBalance, transactionId,
                commission, serCharge, balance, deductAmt, timestamp, toAccount, refId, message,
                fromAccount, toBalance, toDeductAmt, inCommission;

        public void setFromAccount(String fromAccount) {
            this.fromAccount = fromAccount;
        }

        public void setToBalance(String toBalance) {
            this.toBalance = toBalance;
        }

        public void setToDeductAmt(String toDeductAmt) {
            this.toDeductAmt = toDeductAmt;
        }

        public String getFromAccount() {
            return fromAccount;
        }

        public String getToBalance() {
            return toBalance;
        }

        public String getToDeductAmt() {
            return toDeductAmt;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getRefId() {
            return refId;
        }

        public void setRefId(String refId) {
            this.refId = refId;
        }

        public String getToAccount() {
            return toAccount;
        }

        public void setToAccount(String toAccount) {
            this.toAccount = toAccount;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTransactionType(String transactionType) {
            this.transactionType = transactionType;
        }

        public void setAfterBalance(String afterBalance) {
            this.afterBalance = afterBalance;
        }

        public String getTransactionType() {
            return transactionType;
        }

        public String getAfterBalance() {
            return afterBalance;
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
            } else if(status.equals("4")) {
                this.status = "Refund";
            } else if(status.equals("6")) {
                this.status = "Power On Del";
            } else if(status.equals("5")) {
                this.status = "Aborted";
            } else if(status.equals("7")) {
                this.status = "Frequent";
            } else {
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
                holder.tvNumberLabel = (TextView) convertView.findViewById(R.id.tvLINumberLabel);
                holder.tvTransactionIdLabel = (TextView) convertView.findViewById(R.id.tvLITrasactionIdLabel);
                holder.ivShare = (ImageView) convertView.findViewById(R.id.ivShare);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final String transactionType = group.getTransactionType();
            if(transactionType.trim().equalsIgnoreCase("transfer")) {
                if(prefManager.getUserId().trim().equals(group.getToAccount().trim())) {
                    holder.tvDeductAmt.setText(group.getToDeductAmt());
                    holder.tvBalance.setText(group.getToBalance());
                    holder.tvTransactionIdLabel.setText("From A/C : ");
                    holder.tvTransactionId.setText(group.getTransactionId() + group.getFromAccount());
                    holder.tvOperator.setText("Received");
                    holder.tvOperator.setTextColor(Color.BLUE);
                }
                if(prefManager.getUserId().trim().equals(group.getFromAccount().trim())) {
                    holder.tvDeductAmt.setText(group.getDeductAmt());
                    holder.tvBalance.setText(group.getBalance());
                    holder.tvTransactionIdLabel.setText("To A/C : ");
                    holder.tvTransactionId.setText(group.getTransactionId() + group.getToAccount());
                    holder.tvOperator.setText(group.getOperator()+" "+group.getTransactionType());
                    holder.tvOperator.setTextColor(Color.BLUE);
                }
                holder.tvNumberLabel.setText("Message : ");
            } else if(transactionType.trim().equalsIgnoreCase("reverse")) {
                if(prefManager.getUserId().trim().equals(group.getToAccount().trim())) {
                    holder.tvDeductAmt.setText(group.getToDeductAmt());
                    holder.tvBalance.setText(group.getToBalance());
                    holder.tvTransactionIdLabel.setText("From A/C : ");
                    holder.tvTransactionId.setText(group.getTransactionId() + group.getFromAccount());
                    holder.tvOperator.setText("Reversed");
                    holder.tvOperator.setTextColor(Color.BLUE);
                }
                if(prefManager.getUserId().trim().equals(group.getFromAccount().trim())) {
                    holder.tvDeductAmt.setText(group.getDeductAmt());
                    holder.tvBalance.setText(group.getBalance());
                    holder.tvTransactionIdLabel.setText("From A/C : ");
                    holder.tvTransactionId.setText(group.getTransactionId() + group.getToAccount());
                    holder.tvOperator.setText("Reversal Received");
                    holder.tvOperator.setTextColor(Color.BLUE);
                }
                holder.tvNumberLabel.setText("Message : ");
            } else {
                //double amt = Double.parseDouble(group.getDeductAmt())+Double.parseDouble(group.getCommission())-Double.parseDouble(group.getSerCharge());
                double amt = Double.parseDouble(group.getDeductAmt());
                holder.tvDeductAmt.setText(group.getDeductAmt());
                //holder.tvDeductAmt.setText(amt + "");
                holder.tvBalance.setText(group.getBalance());
                holder.tvOperator.setText(group.getOperator()+" "+group.getTransactionType());
                holder.tvTransactionId.setText(group.getTransactionId() + group.getToAccount());
            }

            //holder.tvDeductAmt.setText(group.getDeductAmt());
            //holder.tvBalance.setText(group.getBalance());
            holder.tvNumber.setText(group.getNumber()+group.getMessage());
            holder.tvAmt.setText(group.getAmt());
            String status = group.getStatus() + "";

            if(status.equals("Pending")) {
                holder.tvStatus.setBackgroundColor(Color.YELLOW);
                //status = "Processing";
                status = "Accepted";
            } else if(status.equals("Success")) {
                holder.tvStatus.setBackgroundColor(Color.GREEN);
            } else if(status.equals("Failed")) {
                if(group.getTransactionType().equals("prepaid") || group.getTransactionType().equals("dth")) {
                    status = "Failed - Resend";
                }
                holder.tvStatus.setBackgroundColor(Color.RED);
            } else if(status.equals("Suspend")) {
                holder.tvStatus.setBackgroundColor(Color.RED);
            } else if(status.equals("Aborted")) {
                holder.tvStatus.setBackgroundColor(Color.RED);
            } else if(status.equals("Refund")) {
                holder.tvStatus.setBackgroundColor(Color.parseColor("#357EC7"));
            }
            //holder.tvStatus.setText(group.getStatus());
            holder.tvStatus.setText(status.toUpperCase());
            if(transactionType.trim().equalsIgnoreCase("money_transfer") || transactionType.trim().equalsIgnoreCase("postpaid") || transactionType.trim().equalsIgnoreCase("electricity")) {
                double amt = Double.parseDouble(group.getAmt())+Double.parseDouble(group.getCommission())+Double.parseDouble(group.getSerCharge());
                holder.tvBalance.setText(amt+"");
            } else {
                double amt = Double.parseDouble(group.getAmt())-Double.parseDouble(group.getCommission());
                holder.tvBalance.setText(amt+"");
            }

            holder.tvCommission.setText(group.getCommission());
            //holder.tvSerCharge.setText(group.getSerCharge());
            holder.tvTimestamp.setText(group.getTimestamp());

            final String str = status;
            holder.tvStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(str.equals("Failed - Resend")) {
                        String number = group.getNumber();
                        String op = group.getOperator();
                        String amt = group.getAmt();
                        String tType = group.getTransactionType();
                        if(tType.equals("prepaid")) {
                            Bundle bundle = new Bundle();
                            bundle.putString("number", "" + number);
                            bundle.putString("op", "" + op);
                            bundle.putString("amt", "" + amt);

                            Fragment selectedFragment = null;
                            selectedFragment = PrepaidRecharge.newInstance();
                            if(selectedFragment!=null) {
                                selectedFragment.setArguments(bundle);
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.container, selectedFragment);
                                transaction.commit();
                            }
                        } else if(tType.equals("dth")) {
                            Bundle bundle = new Bundle();
                            bundle.putString("number", "" + number);
                            bundle.putString("op", "" + op);
                            bundle.putString("amt", "" + amt);

                            Fragment selectedFragment = null;
                            selectedFragment = DTHRecharge.newInstance();
                            if(selectedFragment!=null) {
                                selectedFragment.setArguments(bundle);
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.container, selectedFragment);
                                transaction.commit();
                            }
                        } else if(tType.equals("postpaid")) {
                            Bundle bundle = new Bundle();
                            bundle.putString("number", "" + number);
                            bundle.putString("op", "" + op);
                            bundle.putString("amt", "" + amt);

                            Fragment selectedFragment = null;
                            selectedFragment = PostpaidRecharge.newInstance();
                            if(selectedFragment!=null) {
                                selectedFragment.setArguments(bundle);
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.container, selectedFragment);
                                transaction.commit();
                            }
                        }
                    } else if(transactionType.trim().equals("money_transfer")) {
                        String str = "Perfect";
                        //showAlert(str);
                    }
                }
            });

            holder.ivShare.setId(Integer.parseInt(group.getId()));
            holder.ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String msg = "Status : " + group.getStatus()  + "\nOperator : " + group.getOperator() + "\nNumber : " + group.getNumber() +
                            "\nAmount : " + group.getAmt() + "\nTime : " + group.getTimestamp() + "\nTnx Id : " + group.getTransactionId();

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }
            });

            return convertView;
        }
    }

    class ViewHolder {
        ImageView ivShare;
        TextView tvOperator, tvNumber, tvAmt, tvStatus, tvTransactionId, tvCommission, tvSerCharge, tvBalance, tvDeductAmt, tvTimestamp;
        TextView tvNumberLabel, tvTransactionIdLabel;
    }

    public void showAlert(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        alertDialogBuilder.setTitle("Are you sure?");
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

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
    }
}


