package recharge.com.myrechargegallery;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.Map;

public class CommissionChart  extends Fragment {

    View view;
    PrefManager prefManager;
    LinearLayout llTerms, llCustomerCare;
    TextView tvCustomerCare, tvTerms;

    ArrayList<Item> alItem;
    ListView listView;

    public static CommissionChart newInstance() {
        CommissionChart fragment = new CommissionChart();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.commission_chart_fragment, container, false);
        DrawerActivity.setTitle("Commission Chart");

        prefManager = new PrefManager(getActivity());

        llTerms = (LinearLayout) view.findViewById(R.id.llTerms);
        llCustomerCare = (LinearLayout) view.findViewById(R.id.llCustomerCare);
        tvCustomerCare = (TextView) view.findViewById(R.id.tvCustomerCare);
        tvTerms = (TextView) view.findViewById(R.id.tvTerms);
        tvCustomerCare.setTextColor(getResources().getColor(R.color.textColor));

        tvTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llCustomerCare.setVisibility(View.GONE);
                llTerms.setVisibility(View.VISIBLE);
                tvTerms.setTextColor(getResources().getColor(R.color.textColor));
                tvCustomerCare.setTextColor(getResources().getColor(R.color.white));
            }
        });
        tvCustomerCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llCustomerCare.setVisibility(View.VISIBLE);
                llTerms.setVisibility(View.GONE);
                tvCustomerCare.setTextColor(getResources().getColor(R.color.textColor));
                tvTerms.setTextColor(getResources().getColor(R.color.white));
            }
        });

        return view;
    }

    public void loadData() {
        alItem = new ArrayList<>();
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);
        progressDialog.setCancelable(true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "api.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONArray jsonArray = new JSONArray(response);
                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Item item = new Item();
                        item.setId(jsonObject.getString("id"));
                        item.setOperatorName(jsonObject.getString("operatorName"));
                        item.setInCommission(jsonObject.getString("incommission"));
                        item.setOutCommission(jsonObject.getString("outcommission"));
                        item.setInServiceCharge(jsonObject.getString("inservicecharge"));
                        item.setOutServiceCharge(jsonObject.getString("outservicecharge"));
                        alItem.add(item);
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
                params.put("method", "getCommissionChart");
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

    class Item {
        String id, operatorName, inCommission, outCommission, inServiceCharge, outServiceCharge;

        public String getId() {
            return id;
        }

        public String getOperatorName() {
            return operatorName;
        }

        public String getInCommission() {
            return inCommission;
        }

        public String getOutCommission() {
            return outCommission;
        }

        public String getInServiceCharge() {
            return inServiceCharge;
        }

        public String getOutServiceCharge() {
            return outServiceCharge;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setOperatorName(String operatorName) {
            this.operatorName = operatorName;
        }

        public void setInCommission(String inCommission) {
            this.inCommission = inCommission;
        }

        public void setOutCommission(String outCommission) {
            this.outCommission = outCommission;
        }

        public void setInServiceCharge(String inServiceCharge) {
            this.inServiceCharge = inServiceCharge;
        }

        public void setOutServiceCharge(String outServiceCharge) {
            this.outServiceCharge = outServiceCharge;
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
            holder.tvOperator.setText(group.getOperatorName());

            return convertView;
        }
    }

    class ViewHolder {
        TextView tvName, tvOperator, tvNumber, tvAmt, tvStatus, tvTransactionId, tvCommission, tvSerCharge, tvBalance, tvDeductAmt, tvTimestamp;
    }
}
