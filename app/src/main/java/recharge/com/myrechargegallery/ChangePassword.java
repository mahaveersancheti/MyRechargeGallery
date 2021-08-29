package recharge.com.myrechargegallery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
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

public class ChangePassword extends Fragment {

    View view;
    PrefManager prefManager;

    EditText etOldPwd, etNewPwd, etConfirmPwd;
    Button btn;

    public static ChangePassword newInstance() {
        ChangePassword fragment = new ChangePassword();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.change_password_fragment, container, false);
        DrawerActivity.setTitle("Change Password");

        prefManager = new PrefManager(getActivity());

        etOldPwd = (EditText) view.findViewById(R.id.etOldPwd);
        etNewPwd = (EditText) view.findViewById(R.id.etNewPwd);
        etConfirmPwd = (EditText) view.findViewById(R.id.etConfirmPwd);

        btn = (Button) view.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean flag = true;
                String namePattern = "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$";
                String contactPattern = "[789][0-9]{9}";
                String emailPattern = "";

                String oldPwd = etOldPwd.getText().toString().trim();
                String newPwd = etNewPwd.getText().toString().trim();
                String confirmPwd = etConfirmPwd.getText().toString().trim();

                if (oldPwd.trim().length()==0) {
                    flag = false;
                    etOldPwd.setError("Invalid value");
                }
                if (newPwd.trim().length()==0) {
                    flag = false;
                    etNewPwd.setError("Invalid value");
                }
                if (confirmPwd.trim().length()==0) {
                    flag = false;
                    etConfirmPwd.setError("Invalid value");
                }
                if(!newPwd.equals(confirmPwd)) {
                    flag = false;
                    etConfirmPwd.setError("Invalid value");
                    Toast.makeText(getActivity(), "Comfirm password is not matched.", Toast.LENGTH_LONG).show();
                }
                if(flag) {
                    changePwd();
                } else {
                    //Toast.makeText(getActivity(), "Invalid details", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;

    }

    public void changePwd() {
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
                        prefManager.clearPreference();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().finish();
//                        Fragment selectedFragment = null;
//                        selectedFragment = HomeFragment.newInstance();
//                        if(selectedFragment!=null) {
//                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                            transaction.replace(R.id.container, selectedFragment);
//                            transaction.commit();
//                        }
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
                params.put("method", "changePwd");
                params.put("token", prefManager.getToken());
                params.put("oldPwd", etOldPwd.getText().toString().trim());
                params.put("newPwd", etNewPwd.getText().toString().trim());
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
}