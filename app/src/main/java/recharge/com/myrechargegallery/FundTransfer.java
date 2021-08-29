package recharge.com.myrechargegallery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class FundTransfer extends Fragment {

    View view;
    EditText etUserId, etAmout, etDesc;
    PrefManager prefManager;
    //Spinner spUser;
    //ArrayList<Item> alItem;
    //ArrayList<String> alUsers;
    TextView tvName;
    Button btnGo;
    float reverseBalance = 0;
    String shopName = "";

    public static FundTransfer newInstance() {
        FundTransfer fragment = new FundTransfer();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fund_transfer_fragment, container, false);
        DrawerActivity.setTitle("Fund Transfer");

        prefManager = new PrefManager(getActivity());
        //Toast.makeText(getActivity(), prefManager.getBalance()+"", Toast.LENGTH_LONG).show();

        etUserId = (EditText) view.findViewById(R.id.etUserId);
        etAmout = (EditText) view.findViewById(R.id.etAmount);
        etDesc = (EditText) view.findViewById(R.id.etDesc);
        //spUser = (Spinner) view.findViewById(R.id.spUser);
        tvName = (TextView) view.findViewById(R.id.tvUserName);
        btnGo = (Button) view.findViewById(R.id.btnGo);
        Button btnTransfer = (Button) view.findViewById(R.id.btnTransfer);
        Button btnRevert = (Button) view.findViewById(R.id.btnRevert);
        Button btnTransactions = (Button) view.findViewById(R.id.btnTransactions);

        if(prefManager.getUserType().equals("admin")) {
            btnRevert.setVisibility(View.VISIBLE);
            etDesc.setVisibility(View.VISIBLE);
        } else {
            btnRevert.setVisibility(View.GONE);
            etDesc.setVisibility(View.GONE);
        }

        btnTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment selectedFragment = null;
                selectedFragment = FundTransferReport.newInstance();
                if(selectedFragment!=null) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, selectedFragment);
                    transaction.commit();
                }
            }
        });

        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = etAmout.getText().toString().trim();
                String userId = etUserId.getText().toString().trim();
                boolean flag = true;
                if (amount.trim().length()==0) {
                    etAmout.setError("Invalid value");
                    flag = false;
                } else {
                    float balance = prefManager.getBalance();
                    float amt = Float.parseFloat(etAmout.getText().toString().trim());
                    if(balance<amt) {
                        etAmout.setError("Invalid value");
                        flag = false;
                        etAmout.setError("Invalid value");
                        Toast.makeText(getActivity(), "You do not have enough balance.", Toast.LENGTH_LONG).show();
                    }
                }
                if (userId.trim().length()==0) {
                    etUserId.setError("Invalid value");
                    flag = false;
                }
                if (flag) {
                    fundTransfer();
                }
            }
        });
        btnRevert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = etAmout.getText().toString().trim();
                String userId = etUserId.getText().toString().trim();
                boolean flag = true;
                if (amount.trim().length()==0) {
                    etAmout.setError("Invalid value");
                    flag = false;
                } else {
                    //float balance = prefManager.getBalance();
                    float amt = Float.parseFloat(etAmout.getText().toString().trim());
                    if(reverseBalance<amt) {
                        flag = false;
                        etAmout.setError("Invalid value");
                        Toast.makeText(getActivity(), "Balance is not enough.", Toast.LENGTH_LONG).show();
                    }
                }
                if (userId.trim().length()==0) {
                    etUserId.setError("Invalid value");
                    flag = false;
                }
                if (flag) {
                    fundReverse();
                }
            }
        });

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
//                tvName.setText("Mahavir Sancheti");
//                tvName.setVisibility(View.VISIBLE);
            }
        });
        tvName.setVisibility(View.GONE);
        return view;
    }

    public void fundTransfer() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "transactions.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
                        prefManager.setBalance(Float.parseFloat(jsonObject.getString("balance").trim()));
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        String msg = "Transfer Successfully.\nAmount : "+etAmout.getText().toString().trim()+"\nShopname : "+ shopName +
                                "\nTransaction Id : " + jsonObject.getString("id");
                        ResourceElements.showDialogOk(getActivity(), "Result", msg);
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
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("method", "fundTransfer");
                params.put("token", prefManager.getToken());
                params.put("fromAccount", "" + prefManager.getUserId());
                params.put("toAccount", "" + etUserId.getText().toString().trim());
                params.put("amount", "" + etAmout.getText().toString().trim());
                params.put("balance", "" + prefManager.getBalance());
                params.put("imei", prefManager.getImei());
                params.put("message", etDesc.getText().toString().trim() + "");
                return params;
            }
        };
        int socketTimeout = 30000; // 30 seconds. You can change it
        //RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
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

    public void fundReverse() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "transactions.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
                        prefManager.setBalance(Float.parseFloat(jsonObject.getString("balance").trim()));
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        String msg = "Revert Successfully.\nAmount : "+etAmout.getText().toString().trim()+"\nShopname : "+ shopName +
                                "\nTransaction Id : " + jsonObject.getString("id");
                        ResourceElements.showDialogOk(getActivity(), "Result", msg);
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
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("method", "fundRevert");
                params.put("token", prefManager.getToken());
                params.put("fromAccount", "" + prefManager.getUserId());
                params.put("toAccount", "" + etUserId.getText().toString().trim());
                params.put("amount", "" + etAmout.getText().toString().trim());
                params.put("balanceTo", "" + reverseBalance);
                params.put("balanceFrom", "" + prefManager.getBalance());
                params.put("imei", prefManager.getImei());
                params.put("message", etDesc.getText().toString().trim() + "");
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

    public void getData() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "users.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject1 = new JSONObject(response);
                    if(jsonObject1.getBoolean("ack")) {
                        JSONArray jsonArray = jsonObject1.getJSONArray("result");

                        //JSONArray jsonArray = new JSONArray(response);
                        if(jsonArray.length()>0) {
                            for(int i=0;i<jsonArray.length();i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Item item = new Item();
                                item.setId(jsonObject.getString("id"));
                                item.setName(jsonObject.getString("shopName"));
                                item.setBalance(jsonObject.getString("balance"));
                                item.setNumber(jsonObject.getString("contact"));
                                item.setDistributorId(jsonObject.getString("distributorId"));
                                shopName = item.getName();
                                String str = "";
                                if(prefManager.getUserType().equals("admin")) {
                                    str = "Shopname : " + item.getName() + "\n" +
                                            "Opening Bal : " + jsonObject1.getString("openingBalance") + "\n" +
                                            "Sale : " + jsonObject1.getString("sale") + "\n" +
                                            "Balance : " + item.getBalance() + "\n" +
                                            "Mobile : " + jsonObject.getString("contact");
                                } else {
                                    str = "Shopname : " + item.getName() + "\n" +
                                            "Balance : " + item.getBalance() + "\n" +
                                            "Mobile : " + jsonObject.getString("contact");
                                }

                                tvName.setText("" + str);
                                tvName.setVisibility(View.VISIBLE);

                                reverseBalance = Float.parseFloat(item.getBalance());
                            }
                        } else {
                            etAmout.setText("");
                            etUserId.setText("");
                            tvName.setText("Account not found");
                            tvName.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Toast.makeText(getActivity(), jsonObject1.getString("message"), Toast.LENGTH_LONG).show();
                    }
                    tvName.requestFocus();
                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
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
                params.put("method", "getUserDetails");
                params.put("token", prefManager.getToken());
                params.put("fromAccount", prefManager.getUserId());
                params.put("id", "" + etUserId.getText().toString());
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
        String id, name, balance, number, distributorId;

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public void setDistributorId(String distributorId) {
            this.distributorId = distributorId;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getBalance() {
            return balance;
        }

        public String getNumber() {
            return number;
        }

        public String getDistributorId() {
            return distributorId;
        }
    }

}
