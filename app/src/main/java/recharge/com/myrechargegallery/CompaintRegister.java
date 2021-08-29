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

public class CompaintRegister extends Fragment {

    View view;
    PrefManager prefManager;
    ArrayList<Item> alItem;
    ListView listView;
    Button btnGo;
    TextView tvFrmDate, tvToDate;
    String frmDate = "", toDate = "";
    String transactionId = "", operator = "", amount = "", complaintId = "";

    public static CompaintRegister newInstance() {
        CompaintRegister fragment = new CompaintRegister();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.complaint_register_fragment, container, false);
        DrawerActivity.setTitle("Complaint");

        prefManager = new PrefManager(getActivity());

        listView = (ListView) view.findViewById(R.id.lvComplaints);
        tvToDate = (TextView) view.findViewById(R.id.tvToDate);
        tvFrmDate = (TextView) view.findViewById(R.id.tvFrmDate);
        btnGo = (Button) view.findViewById(R.id.btnGo);

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
                if(prefManager.getUserType().equals("admin")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                    alertDialogBuilder.setTitle("Make Decision");
                    alertDialogBuilder.setMessage("Please make a decision.");
                    alertDialogBuilder.setPositiveButton("SUCCESS",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    Item item = alItem.get(index);
                                    transactionId = item.gettId();
                                    operator = item.getOperator();
                                    amount = item.getAmt();
                                    complaintId = item.getcId();
                                    //Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_LONG).show();
                                    takeActon("success");
                                }
                            });
                    alertDialogBuilder.setNegativeButton("FAIL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Item item = alItem.get(index);
                            transactionId = item.gettId();
                            operator = item.getOperator();
                            amount = item.getAmt();
                            complaintId = item.getcId();
                            //Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_LONG).show();
                            takeActon("fail");
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    //other user
                }
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
                params.put("fromAccount", prefManager.getUserId());
                params.put("transactionId", transactionId);
                params.put("operator", operator);
                params.put("amount", amount);
                params.put("from", "complaints");
                params.put("complaintId", complaintId);
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
        alItem.clear();
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
                            item.setcId(jsonObject.getString("cId"));
                            item.settId(jsonObject.getString("tId"));
                            item.setcStatus(jsonObject.getString("cStatus"));
                            item.settStatus(jsonObject.getString("transactionStatus"));
                            item.setNumber(jsonObject.getString("rechargeNumber"));
                            item.settType(jsonObject.getString("transactionType"));
                            item.setcDate(jsonObject.getString("cDate"));
                            item.setOperator(jsonObject.getString("operator"));
                            item.setAmt(jsonObject.getString("amount"));
                            item.setuId(jsonObject.getString("uId"));
                            item.setuName(jsonObject.getString("name"));
                            alItem.add(item);
                        }
                        listView.setAdapter(new ItemAdapter(getActivity(), alItem));
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
                params.put("method", "getComplaints");
                params.put("token", prefManager.getToken());
                params.put("fromAccount", prefManager.getUserId());
                params.put("imei", prefManager.getImei());
                params.put("frmDate", "" + frmDate);
                params.put("toDate", "" + toDate);
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
        String cId, tId, operator, number, amt, cStatus, tStatus, tType, cDate, uId, uName;

        public void setuId(String uId) {
            this.uId = uId;
        }

        public void setuName(String uName) {
            this.uName = uName;
        }

        public String getuId() {
            return uId;
        }

        public String getuName() {
            return uName;
        }

        public void setcId(String cId) {
            this.cId = cId;
        }

        public void settId(String tId) {
            this.tId = tId;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public void setAmt(String amt) {
            this.amt = amt;
        }

//        public void setcStatus(String cStatus) {
//            this.cStatus = cStatus;
//        }

        public void settStatus(String tStatus) {
            this.tStatus = tStatus;
        }

        public void setcStatus(String status) {
            if(status.equals("0")) {
                this.cStatus = "Pending";
            } else if(status.equals("1")) {
                this.cStatus = "Success";
            } else if(status.equals("2")) {
                this.cStatus = "Failed";
            } else if(status.equals("3")) {
                this.cStatus = "Suspend";
            } else if(status.equals("4")) {
                this.cStatus = "Refund";
            } else if(status.equals("6")) {
                this.cStatus = "Power On Del";
            } else if(status.equals("5")) {
                this.cStatus = "Aborted";
            } else if(status.equals("7")) {
                this.cStatus = "Frequent";
            } else {
                this.cStatus = "Failed";
            }
        }


        public void settType(String tType) {
            this.tType = tType;
        }

        public void setcDate(String cDate) {
            this.cDate = cDate;
        }

        public String getcId() {
            return cId;
        }

        public String gettId() {
            return tId;
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

        public String getcStatus() {
            return cStatus;
        }

        public String gettStatus() {
            return tStatus;
        }

        public String gettType() {
            return tType;
        }

        public String getcDate() {
            return cDate;
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
                holder.tvSerCharge = (TextView) convertView.findViewById(R.id.tvLISerCharge);
                holder.tvBalance = (TextView) convertView.findViewById(R.id.tvLIBalance);
                holder.tvDeductAmt = (TextView) convertView.findViewById(R.id.tvLIDeductAmt);
                holder.tvTimestamp = (TextView) convertView.findViewById(R.id.tvLITimestamp);
                holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvOperator.setText(group.getOperator());
            holder.tvNumber.setText(group.getNumber());
            holder.tvAmt.setText(group.getAmt());
            //holder.tvStatus.setText(group.getcStatus());

            String status = group.getcStatus() + "";
            if(status.equals("Pending")) {
                holder.tvStatus.setBackgroundColor(Color.YELLOW);
                //status = "Processing";
                status = "Accepted";
            } else if(status.equals("Success")) {
                holder.tvStatus.setBackgroundColor(Color.GREEN);
            } else if(status.equals("Failed")) {
                holder.tvStatus.setBackgroundColor(Color.RED);
            } else if(status.equals("Suspend")) {
                holder.tvStatus.setBackgroundColor(Color.RED);
            } else if(status.equals("Aborted")) {
                holder.tvStatus.setBackgroundColor(Color.RED);
            } else if(status.equals("Refund")) {
                holder.tvStatus.setBackgroundColor(Color.parseColor("#357EC7"));
            }
            holder.tvStatus.setText(status.toUpperCase());

            holder.tvTransactionId.setText(group.gettStatus());
            holder.tvTimestamp.setText(group.getcDate());
            holder.tvName.setText(group.getuId() + " - " + group.getuName());

            holder.tvName.setVisibility(View.VISIBLE);

            /*Glide.with(getActivity())
                    .load(group.getPath())
                    .error(R.drawable.tree2)
                    .into(holder.imgPath);*/

            return convertView;
        }
    }

    class ViewHolder {
        TextView tvName, tvOperator, tvNumber, tvAmt, tvStatus, tvTransactionId, tvCommission, tvSerCharge, tvBalance, tvDeductAmt, tvTimestamp;
    }
}
