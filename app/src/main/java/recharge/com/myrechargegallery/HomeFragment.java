package recharge.com.myrechargegallery;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    int imgs[] = {R.drawable.prepaid, R.drawable.postpaid, R.drawable.dth1, R.drawable.electricity, R.drawable.ewallet, R.drawable.fund, R.drawable.trade_alert, R.drawable.seller, R.drawable.distributor, R.drawable.home_icon, R.drawable.home_icon};
    String titlesAdmin[] = {"Prepaid\nRecharge", "Postpaid Bill\nPayment", "DTH\nRecharge", "Electricity\nBill", "Money\nTransfer", "Fund Transfer\nand Revert", "Recharge Report\n and Statement", "Create\nRetailer", "Create\nDistributor", "Recharge\nComplaints", "Suspense\nRecharge"};
    ArrayList<Item> alItemsAdmin;

    int imgsDistributors[] = {R.drawable.prepaid, R.drawable.postpaid, R.drawable.dth1, R.drawable.electricity, R.drawable.ewallet, R.drawable.fund, //R.drawable.trade_alert,
            R.drawable.seller, R.drawable.commission};
    String titlesDistributor[] = {"Prepaid\nRecharge", "Postpaid\nRecharge", "DTH\nRecharge", "Electricity\nBill", "Money\nTransfer", "Fund\nTransfer", //"Recharge\nReport",
            "Create\nRetailer", "Information"};
    ArrayList<Item> alItemsDistributor;

    int imgsRetailers[] = {R.drawable.prepaid, R.drawable.postpaid, R.drawable.dth1, R.drawable.electricity, R.drawable.fund,  R.drawable.commission};
    String titlesRetailers[] = {"Prepaid\nRecharge", "Postpaid\nRecharge", "DTH\nRecharge", "Electricity\nBill", "Money\nTransfer",  "Information"};
    ArrayList<Item> alItemsRetailers;


    int imgsRetailersTop[] = {R.drawable.ewallet, R.drawable.trade_alert};
    String titlesRetailersTop[] = {"Add Wallet\nBalance", "Recharge\nReport"};
    ArrayList<Item> alItemsRetailersTop;


    int imgsHome[] = {R.drawable.prepaid, R.drawable.postpaid, R.drawable.dth1, R.drawable.electricity, R.drawable.fund, R.drawable.trade_alert, R.drawable.home_icon};
    String titlesHome[] = {"Prepaid\nRecharge", "Postpaid\nRecharge", "DTH\nRecharge", "Electricity\nBill", "Money\nTransfer", "Recharge\nReport", "Purchase\nBalance"};
    ArrayList<Item> alItemsHome;

    View view;
    GridView gridView,gridTopView;
    CardView crdTopView;
    TextView tvGreeting, tvName, tvLastLogin, tvRefer, tvReferId, tvLogout, tvNotice;

    PrefManager prefManager;
    String version = "";

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment, container, false);
        DrawerActivity.setTitle("Home");

        prefManager = new PrefManager(getActivity());

        tvGreeting = (TextView) view.findViewById(R.id.tvDrawerGreeting);
        tvName = (TextView) view.findViewById(R.id.tvDrawerName);
        tvLastLogin = (TextView) view.findViewById(R.id.tvDrawerLastLogin);
        tvRefer = (TextView) view.findViewById(R.id.tvDrawerRefer);
        tvReferId = (TextView) view.findViewById(R.id.tvDrawerReferId);
        tvLogout = (TextView) view.findViewById(R.id.tvDrawerLogout);

        tvNotice = (TextView) view.findViewById(R.id.tvDrawerNotice);
        tvNotice.setSelected(true);  // Set focus to the textview

//        Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
//                "fonts/dev.ttf");
//        tvNotice.setTypeface(face);

        tvReferId.setText("Balance : " + prefManager.getBalance());

        tvName.setText(prefManager.getName());
        //tvReferId.setText(prefManager.getBalance());
        //tvReferId.setText(" Your Referral Id " + prefManager.getReferralCode());

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            tvGreeting.setText("Good Morning");
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            tvGreeting.setText("Good Afternoon");
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            tvGreeting.setText("Good Evening");
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            tvGreeting.setText("Good Night");
        }

        gridView = (GridView) view.findViewById(R.id.gvHome);
        gridTopView = (GridView) view.findViewById(R.id.gvHomeTopRow);
        crdTopView = (CardView) view.findViewById(R.id.crdTopRow);
        crdTopView.setVisibility(View.GONE);
        String userType = prefManager.getUserType();
        //Toast.makeText(getActivity(), "" + userType, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getActivity(), "User Type : " + userType, Toast.LENGTH_LONG).show();
        if (userType.trim().equalsIgnoreCase("admin")) {
            //Toast.makeText(getActivity(), "admin", Toast.LENGTH_SHORT).show();
            alItemsAdmin = new ArrayList<>();
            for (int i = 0; i < titlesAdmin.length; i++) {
                Item obj = new Item();
                obj.setImgId(imgs[i]);
                obj.setTitle(titlesAdmin[i]);
                alItemsAdmin.add(obj);
            }
            ItemAdapter groupAdapter = new ItemAdapter(getActivity(), alItemsAdmin);
            gridView.setAdapter(groupAdapter);
        } else if (userType.trim().equalsIgnoreCase("distributor")) {
            //Toast.makeText(getActivity(), "distributor", Toast.LENGTH_SHORT).show();
            crdTopView.setVisibility(View.VISIBLE);
            alItemsRetailers = new ArrayList<>();
            alItemsRetailersTop = new ArrayList<>();
            for (int i = 0; i < titlesRetailersTop.length; i++) {
                Item obj = new Item();
                obj.setImgId(imgsRetailersTop[i]);
                obj.setTitle(titlesRetailersTop[i]);
                alItemsRetailersTop.add(obj);
            }
            ItemAdapter groupAdapterTop = new ItemAdapter(getActivity(), alItemsRetailersTop);
            gridTopView.setAdapter(groupAdapterTop);

            alItemsDistributor = new ArrayList<>();
            for (int i = 0; i < titlesDistributor.length; i++) {
                Item obj = new Item();
                obj.setImgId(imgsDistributors[i]);
                obj.setTitle(titlesDistributor[i]);
                alItemsDistributor.add(obj);
            }
            ItemAdapter groupAdapter = new ItemAdapter(getActivity(), alItemsDistributor);
            gridView.setAdapter(groupAdapter);
        } else if (userType.trim().equalsIgnoreCase("retailer")) {
            //Toast.makeText(getActivity(), "retailser", Toast.LENGTH_SHORT).show();
            crdTopView.setVisibility(View.VISIBLE);
            alItemsRetailers = new ArrayList<>();
            alItemsRetailersTop = new ArrayList<>();
            for (int i = 0; i < titlesRetailersTop.length; i++) {
                Item obj = new Item();
                obj.setImgId(imgsRetailersTop[i]);
                obj.setTitle(titlesRetailersTop[i]);
                alItemsRetailersTop.add(obj);
            }
            ItemAdapter groupAdapterTop = new ItemAdapter(getActivity(), alItemsRetailersTop);
            gridTopView.setAdapter(groupAdapterTop);

            for (int i = 0; i < titlesRetailers.length; i++) {
                Item obj = new Item();
                obj.setImgId(imgsRetailers[i]);
                obj.setTitle(titlesRetailers[i]);
                alItemsRetailers.add(obj);
            }
            ItemAdapter groupAdapter = new ItemAdapter(getActivity(), alItemsRetailers);
            gridView.setAdapter(groupAdapter);
        } else if (userType.trim().equalsIgnoreCase("home")) {
            //Toast.makeText(getActivity(), "home", Toast.LENGTH_SHORT).show();
            alItemsHome = new ArrayList<>();
            for (int i = 0; i < titlesHome.length; i++) {
                Item obj = new Item();
                obj.setImgId(imgsHome[i]);
                obj.setTitle(titlesHome[i]);
                alItemsHome.add(obj);
            }
            ItemAdapter groupAdapter = new ItemAdapter(getActivity(), alItemsHome);
            gridView.setAdapter(groupAdapter);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String userType = prefManager.getUserType();
                if (userType.trim().equalsIgnoreCase("admin")) {
                    adminOptions(i);
                } else if (userType.trim().equalsIgnoreCase("distributor")) {
                    distributorOptions(i);
                } else if (userType.trim().equalsIgnoreCase("retailer")) {
                    retailerOptions(i);
                } else if (userType.trim().equalsIgnoreCase("home")) {
                    homeOptions(i);
                }
            }
        });


        gridTopView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String userType = prefManager.getUserType();
//              if (userType.trim().equalsIgnoreCase("retailer")) {
//                    retailerOptionsTop(i);
//                }

              retailerOptionsTop(i);
            }
        });

        if (Config.hasConnection(getActivity())) {
            getHomeData();
        } else {
            String message = "Internet connection problem. Please check your internet connection then try again.";
            //ResourceElements.showDialogOk(getActivity(), "Internet Problem", message);
            Toast.makeText(getActivity(), "Internet connection problem", Toast.LENGTH_LONG).show();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
            alertDialogBuilder.setTitle("Internet Problem");
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            getActivity().finish();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        return view;
    }

    public void validateVersion(String version) {
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        if (!version.trim().equalsIgnoreCase(versionName)) {
            String message = "App update is available. Please update app to use further.";
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
            alertDialogBuilder.setTitle("App Update");
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setPositiveButton("Update",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialogBuilder.setCancelable(false);
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
    }

    public void getHomeData() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "api.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");
                    if (result.equals("success")) {
                        String status = jsonObject.getString("status");
                        if (status.equals("1")) {
                            tvLogout.setText("Id : " + jsonObject.getString("id"));
                            tvRefer.setText("Today's Sale : " + jsonObject.getString("todaySale") + "");
                            tvReferId.setText("Balance : " + jsonObject.getString("balance"));
                            DrawerActivity.itemBalance.setTitle("Rs. " + jsonObject.getString("balance"));
                            //prefManager.setBalance(Float.parseFloat(jsonObject.getString("balance").trim()));
                            tvName.setText(jsonObject.getString("name"));
                            prefManager.setName(jsonObject.getString("name"));
                            prefManager.setSchemeUrl(jsonObject.getString("schemeUrl"));
                            if (jsonObject.getString("balance").trim().length() > 0)
                                prefManager.setBalance(Float.parseFloat(jsonObject.getString("balance")));

                            if ((jsonObject.getString("Mobile") + "").equalsIgnoreCase("1"))
                                prefManager.setPrepaid(true);
                            else
                                prefManager.setPrepaid(false);

                            if ((jsonObject.getString("PostPaid") + "").equalsIgnoreCase("1"))
                                prefManager.setPostpaid(true);
                            else
                                prefManager.setPostpaid(false);

                            if ((jsonObject.getString("DTH") + "").equalsIgnoreCase("1"))
                                prefManager.setDth(true);
                            else
                                prefManager.setDth(false);

                            if ((jsonObject.getString("Gas") + "").equalsIgnoreCase("1"))
                                prefManager.setGas(true);
                            else
                                prefManager.setGas(false);
                            if ((jsonObject.getString("Electricity") + "").equalsIgnoreCase("1"))
                                prefManager.setElectricity(true);
                            else
                                prefManager.setElectricity(false);
                            if ((jsonObject.getString("MoneyTransfer") + "").equalsIgnoreCase("1"))
                                prefManager.setMoneyTransfer(true);
                            else
                                prefManager.setMoneyTransfer(false);
                        } else if (status.equals("0")) {
                            Toast.makeText(getActivity(), "Your account is not yet active.", Toast.LENGTH_LONG).show();
                            prefManager.clearPreference();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        } else if (status.equals("2")) {
                            Toast.makeText(getActivity(), "Your account is deactivated.", Toast.LENGTH_LONG).show();
                            prefManager.clearPreference();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        }
                        version = jsonObject.getString("version");
                        validateVersion(version);


                        //set UPI
                        try {
                            prefManager.setIsUPIAllowed(jsonObject.getString("UPI").equalsIgnoreCase("1"));
                        } catch (Exception e) {
                            Log.d("veer err", e.getMessage());
                        }
                    } else {
                        Toast.makeText(getActivity(), "Your account not found.", Toast.LENGTH_LONG).show();
                        prefManager.clearPreference();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().finish();
                    }
                    String value = "";
                    try {
                        byte ptext[] = jsonObject.getString("title").getBytes("ISO-8859-1");
                        value = new String(ptext, "UTF-8");
                        //tvNotice.setText(value);
                    } catch (Exception e) {
                        Log.d("veer", e.getMessage() + "");
                    }
                    //tvNotice.setText(jsonObject.getString("title"));
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        tvNotice.setText(Html.fromHtml(value + "", Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        tvNotice.setText(Html.fromHtml(value + ""));
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
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //Log.d("veer home", prefManager.getToken() + prefManager.getImei() + "," + prefManager.getUserId());
                params.put("method", "getHomeData");
                params.put("token", prefManager.getToken());
                params.put("imei", prefManager.getImei());
                //params.put("id", "" + prefManager.getUserId());
                params.put("fromAccount", prefManager.getUserId());
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

    public void adminOptions(int index) {
        Fragment selectedFragment = null;
        switch (index) {
            case 0:
                if (prefManager.getPrepaid())
                    selectedFragment = PrepaidRecharge.newInstance();
                else
                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
                break;
            case 1:
                if (prefManager.getPostpaid())
                    selectedFragment = PostpaidRecharge.newInstance();
                else
                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
                break;
            case 2:
                if (prefManager.getDth())
                    selectedFragment = DTHRecharge.newInstance();
                else
                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
                break;
            case 3:
                if (prefManager.getElectricity())
                    selectedFragment = ElectricityBillPayment.newInstance();
                else
                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
                break;
//            case 4 :
//                if(prefManager.getGas())
//                    selectedFragment = GasBillPayment.newInstance();
//                else
//                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
//                break;
            case 4:
                //money transfer
                if (prefManager.getMoneyTransfer())
                    selectedFragment = MoneyTransfer.newInstance();
                else
                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
                break;
            case 5:
                //fund transfer
                selectedFragment = FundTransfer.newInstance();
                break;
            case 6:
                //recharge report
                selectedFragment = RechargeReport.newInstance();
                break;
            case 7:
                //create retailer
                selectedFragment = RetailersList.newInstance();
                break;
            case 8:
                //create distributor
                selectedFragment = DistributorsList.newInstance();
                break;
            case 9:
                selectedFragment = CompaintRegister.newInstance();
                break;
            case 10:
                selectedFragment = SuspendListFragment.newInstance();
                break;
        }
        if (selectedFragment != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, selectedFragment);
            transaction.commit();
        }
    }

    public void distributorOptions(int index) {
        Fragment selectedFragment = null;
        switch (index) {
            case 0:
                if (prefManager.getPrepaid())
                    selectedFragment = PrepaidRecharge.newInstance();
                else
                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
                break;
            case 1:
                if (prefManager.getPostpaid())
                    selectedFragment = PostpaidRecharge.newInstance();
                else
                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
                break;
            case 2:
                if (prefManager.getDth())
                    selectedFragment = DTHRecharge.newInstance();
                else
                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
                break;
            case 3:
                //Toast.makeText(getActivity(), prefManager.getElectricity() + "", Toast.LENGTH_LONG).show();
                if (prefManager.getElectricity())
                    selectedFragment = ElectricityBillPayment.newInstance();
                else
                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
                break;
//            case 4 :
//                if(prefManager.getGas())
//                    selectedFragment = GasBillPayment.newInstance();
//                else
//                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
//                break;
            case 4:
                //money transfer
                if (prefManager.getMoneyTransfer())
                    selectedFragment = MoneyTransfer.newInstance();
                else
                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
                break;
            case 5:
                //fund transfer
                selectedFragment = FundTransfer.newInstance();
                break;
//            case 6:
//                //recharge report
//                selectedFragment = RechargeReport.newInstance();
//                break;
            case 6:
                //create retailer
                selectedFragment = RetailersList.newInstance();
                break;
//            case 8 :
//                //create distributor
//                selectedFragment = CompaintRegister.newInstance();
//                break;
            case 7:
                //Toast.makeText(getActivity(), "Done", Toast.LENGTH_LONG).show();
                selectedFragment = CommissionChart.newInstance();
                break;
        }
        if (selectedFragment != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, selectedFragment);
            transaction.commit();
        }
    }


    public void retailerOptionsTop(int index) {
        Fragment selectedFragment = null;
        switch (index) {
            case 0:
                //add wallet balance
                if (prefManager.isUPIAllowed()) {
                Intent intent = new Intent(getActivity(), UPIGatewayActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(getActivity(), "You are not authorized\nPlease contact to admin", Toast.LENGTH_LONG).show();
            }
                break;
            case 1:
                //recharge report
                selectedFragment = RechargeReport.newInstance();
                break;
        }
        if (selectedFragment != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, selectedFragment);
            transaction.commit();
        }
    }


    public void retailerOptions(int index) {
        Fragment selectedFragment = null;
        switch (index) {
            case 0:
                if (prefManager.getPrepaid())
                    selectedFragment = PrepaidRecharge.newInstance();
                else
                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
                break;
            case 1:
                if (prefManager.getPostpaid())
                    selectedFragment = PostpaidRecharge.newInstance();
                else
                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
                break;
            case 2:
                if (prefManager.getDth())
                    selectedFragment = DTHRecharge.newInstance();
                else
                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
                break;
            case 3:
                if (prefManager.getElectricity())
                    selectedFragment = ElectricityBillPayment.newInstance();
                else
                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
                break;
//            case 4 :
//                if(prefManager.getGas())
//                    selectedFragment = GasBillPayment.newInstance();
//                else
//                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
//                break;
            case 4:
                //money transfer
                if (prefManager.getMoneyTransfer())
                    selectedFragment = MoneyTransfer.newInstance();
                else
                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
                break;
//            case 6:
//                //recharge report
//                selectedFragment = RechargeReport.newInstance();
//                break;
//            case 6 :
//                selectedFragment = CompaintRegister.newInstance();
//                break;
            case 5:
                selectedFragment = CommissionChart.newInstance();
                break;
//            case 7:
////                Intent intent = new Intent(getActivity(), UPIGatewayActivity.class);
////                startActivity(intent);
////                Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_LONG).show();
//                if (prefManager.isUPIAllowed()) {
//                    Intent intent = new Intent(getActivity(), UPIGatewayActivity.class);
//                    startActivity(intent);
//                }else{
//                                    Toast.makeText(getActivity(), "You are not authorized/nPlease contact to admin", Toast.LENGTH_LONG).show();
//                }
//                break;
        }
        if (selectedFragment != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, selectedFragment);
            transaction.commit();
        }
    }

    public void homeOptions(int index) {
        Fragment selectedFragment = null;
        switch (index) {
            case 0:
                if (prefManager.getPrepaid())
                    selectedFragment = PrepaidRecharge.newInstance();
                else
                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
                break;
            case 1:
                if (prefManager.getPostpaid())
                    selectedFragment = PostpaidRecharge.newInstance();
                else
                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
                break;
            case 2:
                if (prefManager.getDth())
                    selectedFragment = DTHRecharge.newInstance();
                else
                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
                break;
            case 3:
                if (prefManager.getElectricity())
                    selectedFragment = ElectricityBillPayment.newInstance();
                else
                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
                break;
//            case 4 :
//                if(prefManager.getGas())
//                    selectedFragment = GasBillPayment.newInstance();
//                else
//                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
//                break;

            case 4:
                //money transfer
                if (prefManager.getMoneyTransfer())
                    selectedFragment = MoneyTransfer.newInstance();
                else
                    Toast.makeText(getActivity(), "You are not authorized.", Toast.LENGTH_LONG).show();
                break;
            case 5:
                selectedFragment = RechargeReport.newInstance();
                break;
            case 6:
                //request balance
                break;
            case 7:
                //recharge report
                //selectedFragment = CompaintRegister.newInstance();
                break;
        }
        if (selectedFragment != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, selectedFragment);
            transaction.commit();
        }
    }

    class Item {
        int imgId;
        String title;

        public int getImgId() {
            return imgId;
        }

        public String getTitle() {
            return title;
        }

        public void setImgId(int imgId) {
            this.imgId = imgId;
        }

        public void setTitle(String title) {
            this.title = title;
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
                convertView = mInflater.inflate(R.layout.grid_item, null);
                holder.tvName = (TextView) convertView.findViewById(R.id.tvGridItem);
                holder.imgPath = (ImageView) convertView.findViewById(R.id.ivGridItem);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvName.setText(group.getTitle());
            holder.imgPath.setImageResource(group.getImgId());

            /*Glide.with(getActivity())
                    .load(group.getPath())
                    .error(R.drawable.tree2)
                    .into(holder.imgPath);*/

            return convertView;
        }
    }

    class ViewHolder {
        TextView tvName;
        ImageView imgPath;
    }

}
