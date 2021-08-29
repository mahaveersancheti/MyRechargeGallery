package recharge.com.myrechargegallery;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddBeneficiaty extends Fragment {

    View view;
    EditText etFullName, etBeneficiaryName, etContact, etAccNumber, etIfsc, etBankName;
    PrefManager prefManager;
    //String imei;
    Spinner spAccType;
    ArrayList<String> alAccType;
    String OTP = "", senderId = "", beneficiaryId = "", contact = "", id = "";


    public static AddBeneficiaty newInstance() {
        AddBeneficiaty fragment = new AddBeneficiaty();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.create_beneficiary_fragment, container, false);
        DrawerActivity.setTitle("Add Beneficiary");

        prefManager = new PrefManager(getActivity());

        etFullName = (EditText) view.findViewById(R.id.etFullName);
        etContact = (EditText) view.findViewById(R.id.etPhone);
        etBeneficiaryName = (EditText) view.findViewById(R.id.etBeneficiaryName);
        etAccNumber = (EditText) view.findViewById(R.id.etAccNumber);
        etIfsc = (EditText) view.findViewById(R.id.etIfsc);
        etBankName = (EditText) view.findViewById(R.id.etBankName);
        spAccType = (Spinner) view.findViewById(R.id.spAccType);

        Button btn = (Button) view.findViewById(R.id.btnReg);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        alAccType = new ArrayList<>();
        alAccType.add("Savings");
        alAccType.add("Current");

        spAccType.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.tv, alAccType));

        return view;
    }

    public void validate() {
        boolean flag = true;
        String namePattern = "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$";
        String contactPattern = "[6789][0-9]{9}";

        String name = etFullName.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String beneficiaryName = etBeneficiaryName.getText().toString().trim();
        String accNumber = etAccNumber.getText().toString().trim();
        String ifsc = etIfsc.getText().toString().trim();
        String bankName = etBankName.getText().toString().trim();

        if (name.trim().length()==0) {
            flag = false;
            etFullName.setError("Invalid value");
        }
        if (contact.trim().length()==0) {
            flag = false;
            etContact.setError("Invalid value");
        }
        if (beneficiaryName.trim().length()==0) {
            flag = false;
            etBeneficiaryName.setError("Only 12 Digit Number is Allowed.");
        }
        if (accNumber.trim().length()==0) {
            flag = false;
            etAccNumber.setError("Invalid value");
        }
        if (ifsc.trim().length()==0) {
            flag = false;
            etIfsc.setError("Invalid value");
        }
        if (bankName.trim().length()==0) {
            flag = false;
            etBankName.setError("Invalid value");
        }
        if(!contact.matches(contactPattern)) {
            flag = false;
            etContact.setError("Invalid value");
        }
        if(flag) {
            register();
        } else {
            Toast.makeText(getActivity(), "Invalid details", Toast.LENGTH_LONG).show();
        }
    }

    public void register() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "money_transfer.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
//                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
//                        JSONObject jsonObject2 = jsonObject1.getJSONObject("Body");
//                        senderId = jsonObject2.getString("SenderID");
//                        beneficiaryId = jsonObject2.getString("BeneficiaryID");
                        contact = jsonObject.getString("contact");
                        senderId = jsonObject.getString("senderId");
                        beneficiaryId = jsonObject.getString("beneficiaryId");
                        id = jsonObject.getString("id");

                        String message = "Please enter otp received on beneficiary number.";
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Verify OTP");
                        builder.setMessage(message);
                        final EditText input = new EditText(getActivity());
                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_PHONE);
                        input.setHint("OTP");
                        input.setPadding(20,20,20,20);
                        input.setBackgroundResource(R.drawable.btn_back);
                        builder.setView(input);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                OTP = input.getText().toString();
                                verifyOtp();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();

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
                params.put("method", "addBeneficiaty");
                params.put("token", prefManager.getToken());
                params.put("fromAccount", prefManager.getUserId());
                params.put("name", etFullName.getText().toString().trim());
                params.put("contact", etContact.getText().toString().trim());
                params.put("beneficiaryName", etBeneficiaryName.getText().toString().trim());
                params.put("accountNumber", etAccNumber.getText().toString().trim());
                params.put("ifsc", etIfsc.getText().toString().trim());
                params.put("accountType", spAccType.getSelectedItem().toString().trim());
                params.put("bankName", etBankName.getText().toString().trim());
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

    public void verifyOtp() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "money_transfer.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
                params.put("method", "verifyBeneficiary");
                params.put("token", prefManager.getToken());
                params.put("fromAccount", prefManager.getUserId());
                params.put("imei", prefManager.getImei());
                params.put("contact", contact);
                params.put("beneficiaryId", beneficiaryId);
                params.put("id", id);
                params.put("senderId", senderId);
                params.put("OTP", OTP);
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
