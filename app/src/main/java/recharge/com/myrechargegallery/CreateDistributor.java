package recharge.com.myrechargegallery;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class CreateDistributor extends Fragment {

    View view;
    PrefManager prefManager;

    EditText etFullName, etShopName, etContact, etAdharNo, etPassword, etAddr, etHangout, etWhatsapp;
    String id = "", name = "", shopName = "", address = "", hangout = "", contact = "", whatsapp = "", adhar = "";
    boolean isUpdate = false;

    public static CreateDistributor newInstance() {
        CreateDistributor fragment = new CreateDistributor();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.create_distributor_fragment, container, false);
        DrawerActivity.setTitle("Create Distributor");

        prefManager = new PrefManager(getActivity());

        Bundle args = getArguments();
        if(args!=null) {
            isUpdate = true;
            id = args.getString("id");
            //Toast.makeText(getActivity(), id, Toast.LENGTH_LONG).show();
            name = args.getString("name");
            shopName = args.getString("shopName");
            contact = args.getString("contact");
            whatsapp = args.getString("whatsapp");
            hangout = args.getString("hangout");
            adhar = args.getString("adhar");
            address = args.getString("address");
        }

        etFullName = (EditText) view.findViewById(R.id.etFullName);
        etShopName = (EditText) view.findViewById(R.id.etShopName);
        etContact = (EditText) view.findViewById(R.id.etPhone);
        etAdharNo = (EditText) view.findViewById(R.id.etAdharNo);
        etPassword = (EditText) view.findViewById(R.id.etPassword);
        etAddr = (EditText) view.findViewById(R.id.etAddr);
        etHangout = (EditText) view.findViewById(R.id.etHangoutId);
        etWhatsapp = (EditText) view.findViewById(R.id.etWhatsapp);

        etFullName.setText(name);
        etShopName.setText(shopName);
        etContact.setText(contact);
        etAdharNo.setText(adhar);
        etAddr.setText(address);
        etHangout.setText(hangout);
        etWhatsapp.setText(whatsapp);

        Button btn = (Button) view.findViewById(R.id.btnReg);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        return view;
    }

    public void validate() {
        boolean flag = true;
        String namePattern = "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$";
        String contactPattern = "[6789][0-9]{9}";
        String emailPattern = "";

        String name = etFullName.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etHangout.getText().toString().trim();
        String adhar = etAdharNo.getText().toString().trim();

        if (name.trim().length()==0) {
            flag = false;
            etFullName.setError("Invalid value");
        }
        if (contact.trim().length()==0) {
            flag = false;
            etContact.setError("Invalid value");
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            flag = false;
            etHangout.setError("Invalid value");
        }
        if (adhar.trim().length()!=12) {
            flag = false;
            etAdharNo.setError("Only 12 Digit Number is Allowed.");
        }
//        if (password.trim().length()==0) {
//            flag = false;
//            etPassword.setError("Invalid value");
//        }
        if(!contact.matches(contactPattern)) {
            flag = false;
            etContact.setError("Invalid value");
        }
        if(flag) {
            if(isUpdate)
                update();
            else
                register();
        } else {
            Toast.makeText(getActivity(), "Invalid details", Toast.LENGTH_LONG).show();
        }
    }

    public void register() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "users.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        String token = jsonObject.getString("token");
                        //prefManager.setToken(token);
                        ResourceElements.showDialogOk(getActivity(), "Result", "Registered Successfully. User token is "+token);
                        Fragment selectedFragment = null;
                        selectedFragment = DistributorsList.newInstance();
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
                params.put("method", "registerDistributor");
                params.put("token", prefManager.getToken());
                params.put("fromAccount", prefManager.getUserId());
                params.put("name", etFullName.getText().toString().trim());
                params.put("contact", etContact.getText().toString().trim());
                params.put("shopName", etShopName.getText().toString().trim());
                params.put("adharNumber", etAdharNo.getText().toString().trim());
                params.put("password", etPassword.getText().toString().trim());
                params.put("address", etAddr.getText().toString().trim());
                params.put("hangout", etHangout.getText().toString().trim());
                params.put("whatsapp", etWhatsapp.getText().toString().trim());
                params.put("lat", "");
                params.put("lng", "");
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

    public void update() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "users.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        String token = jsonObject.getString("token");
                        //prefManager.setToken(token);
                        ResourceElements.showDialogOk(getActivity(), "Result", "Registered Successfully. User token is "+token);
                        Fragment selectedFragment = null;
                        selectedFragment = RetailersList.newInstance();
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
                params.put("method", "updateRetailer");
                params.put("token", prefManager.getToken());
                params.put("fromAccount", prefManager.getUserId());
                params.put("ratailerId", id);
                params.put("name", etFullName.getText().toString().trim());
                params.put("contact", etContact.getText().toString().trim());
                params.put("shopName", etShopName.getText().toString().trim());
                params.put("adharNumber", etAdharNo.getText().toString().trim());
                params.put("password", etPassword.getText().toString().trim());
                params.put("address", etAddr.getText().toString().trim());
                params.put("hangout", etHangout.getText().toString().trim());
                params.put("whatsapp", etWhatsapp.getText().toString().trim());
                params.put("lat", "");
                params.put("lng", "");
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
}
