package recharge.com.myrechargegallery;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import java.util.HashMap;
import java.util.Map;

public class RetailersList extends Fragment {

    View view;
    PrefManager prefManager;
    ListView listView;
    ArrayList<Item> alItem, alDistributors;
    ArrayList<String> alDistributorName;
    Button btnAddNew;
    String retailerId = "";

    EditText etSearch;
    Button btnSearch;

    public static RetailersList newInstance() {
        RetailersList fragment = new RetailersList();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);
        DrawerActivity.setTitle("Retailers List");

        prefManager = new PrefManager(getActivity());

        etSearch = (EditText) view.findViewById(R.id.etSearchString);
        btnSearch = (Button) view.findViewById(R.id.btnGo);

        btnAddNew = (Button) view.findViewById(R.id.btnAddNew);
        btnAddNew.setVisibility(View.VISIBLE);
        listView = (ListView) view.findViewById(R.id.lvUserList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String userId = prefManager.getUserId().trim();
                final int index = i;
                if(userId.equalsIgnoreCase("1")) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("Select Option");
                    //alertDialogBuilder.setMessage(message);
                    alertDialogBuilder.setPositiveButton("Change Dist",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    retailerId = alItem.get(index).getId();
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("Select Distributor");
                                    View view1 = getLayoutInflater().inflate(R.layout.list_view_layout, null);
                                    ListView lv = (ListView) view1.findViewById(R.id.lv);
                                    lv.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.tv, alDistributorName));
                                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            builder.setCancelable(true);
                                            String name = alDistributorName.get(i);
                                            final String id = alDistributors.get(i).getId();

                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                                            builder1.setTitle("Make Decision");
                                            builder1.setMessage("Id : " + id + "\nName : " + name);

                                            builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    updateDistributor(id);
                                                    //Toast.makeText(getActivity(), "Coming Soon", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                            builder1.show();

                                        }
                                    });
                                    builder.setView(view1);
                                    builder.show();
                                }
                            });
                    alertDialogBuilder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Item obj = alItem.get(index);
                            Bundle args = new Bundle();
                            args.putString("id", obj.getId());
                            args.putString("name", obj.getName());
                            args.putString("shopName", obj.getShopName());
                            args.putString("address", obj.getAddress());
                            args.putString("whatsapp", obj.getWhatsapp());
                            args.putString("hangout", obj.getHandgout());
                            args.putString("contact", obj.getNumber());
                            args.putString("adhar", obj.getAdhar());

                            Fragment selectedFragment = null;
                            selectedFragment = CreateRetailer.newInstance();
                            if(selectedFragment!=null) {
                                selectedFragment.setArguments(args);
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.container, selectedFragment);
                                transaction.commit();
                            }
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

//                    retailerId = alItem.get(i).getId();
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                    builder.setTitle("Select Distributor");
//                    View view1 = getLayoutInflater().inflate(R.layout.list_view_layout, null);
//                    ListView lv = (ListView) view1.findViewById(R.id.lv);
//                    lv.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.tv, alDistributorName));
//                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                            builder.setCancelable(true);
//                            String name = alDistributorName.get(i);
//                            final String id = alDistributors.get(i).getId();
//
//                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
//                            builder1.setTitle("Make Decision");
//                            builder1.setMessage("Id : " + id + "\nName : " + name);
//
//                            builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    updateDistributor(id);
//                                    //Toast.makeText(getActivity(), "Coming Soon", Toast.LENGTH_LONG).show();
//                                }
//                            });
//                            builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                }
//                            });
//                            builder1.show();
//
//                        }
//                    });
//                    builder.setView(view1);
//                    builder.show();


                }
            }
        });

        getData();

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment selectedFragment = null;
                selectedFragment = CreateRetailer.newInstance();
                if(selectedFragment!=null) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, selectedFragment);
                    transaction.commit();
                }
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchString = etSearch.getText().toString().trim();
                getData();
//                if(searchString.trim().length()>0) {
//
//                } else {
//                    etSearch.setError("Can not be null");
//                }
            }
        });

        return view;
    }

    public void getData() {
        alItem = new ArrayList<>();
        alDistributorName = new ArrayList<>();
        alDistributors = new ArrayList<>();
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
                        for(int i=0;i<jsonArray.length();i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Item item = new Item();
                            item.setId(jsonObject.getString("id"));
                            item.setShopName(jsonObject.getString("shopName"));
                            item.setBalance(jsonObject.getString("balance"));
                            item.setNumber(jsonObject.getString("contact"));
                            item.setDistributorId(jsonObject.getString("distributorId"));
                            item.setName(jsonObject.getString("name"));
                            item.setAdhar(jsonObject.getString("adharNumber"));
                            item.setAddress(jsonObject.getString("address"));
                            item.setHandgout(jsonObject.getString("hangoutId"));
                            item.setWhatsapp(jsonObject.getString("whatsapp"));
                            alItem.add(item);

                        }
                        listView.setAdapter(new ItemAdapter(getActivity(), alItem));

                        JSONArray jsonArray1 = jsonObject1.getJSONArray("distributors");
                        for(int i=0;i<jsonArray1.length();i++) {
                            JSONObject jsonObject = jsonArray1.getJSONObject(i);
                            alDistributorName.add(jsonObject.getString("id") + " : " +jsonObject.getString("name"));
                            Item item = new Item();
                            item.setId(jsonObject.getString("id"));
                            item.setName(jsonObject.getString("name"));
                            alDistributors.add(item);
                        }
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
                params.put("method", "allRetailers");
                params.put("token", prefManager.getToken());
                params.put("fromAccount", prefManager.getUserId());
                params.put("searchString", "" + etSearch.getText().toString().trim());
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
        String id, name, balance, number, distributorId, shopName, address, handgout, whatsapp, adhar;

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getHandgout() {
            return handgout;
        }

        public void setHandgout(String handgout) {
            this.handgout = handgout;
        }

        public String getWhatsapp() {
            return whatsapp;
        }

        public void setWhatsapp(String whatsapp) {
            this.whatsapp = whatsapp;
        }

        public String getAdhar() {
            return adhar;
        }

        public void setAdhar(String adhar) {
            this.adhar = adhar;
        }

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
                convertView = mInflater.inflate(R.layout.list_item_user, null);
                holder.tvId = (TextView) convertView.findViewById(R.id.tvLIId);
                holder.tvName = (TextView) convertView.findViewById(R.id.tvLIShopName);
                holder.tvNumber = (TextView) convertView.findViewById(R.id.tvLIMobile);
                holder.tvBalance = (TextView) convertView.findViewById(R.id.tvLIBalance);
                holder.tvDistributorId = (TextView) convertView.findViewById(R.id.tvLIDistributorId);
                holder.imResetImei = (ImageView) convertView.findViewById(R.id.imResetImei);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvId.setText(group.getId());
            holder.tvNumber.setText(group.getNumber());
            holder.tvBalance.setText(group.getBalance());
            holder.tvName.setText(group.getShopName());
            holder.tvDistributorId.setText(group.getDistributorId());
            holder.imResetImei.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resetIMEI(group.getId());
                }
            });

            return convertView;
        }
    }

    class ViewHolder {
        TextView tvId, tvNumber, tvBalance, tvName, tvDistributorId;
        ImageView imResetImei;
    }

    public void updateDistributor(final String distributorId) {
        alItem = new ArrayList<>();
        alDistributorName = new ArrayList<>();
        alDistributors = new ArrayList<>();
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "users.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject1 = new JSONObject(response);
                    if(jsonObject1.getBoolean("ack")) {
                        Toast.makeText(getActivity(), jsonObject1.getString("message"), Toast.LENGTH_LONG).show();
                        Fragment selectedFragment = null;
                        selectedFragment = RechargeReport.newInstance();
                        if(selectedFragment!=null) {
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.container, selectedFragment);
                            transaction.commit();
                        }
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
                params.put("method", "updateDistributorId");
                params.put("token", prefManager.getToken());
                params.put("fromAccount", prefManager.getUserId());
                params.put("distributorId", distributorId);
                params.put("retailerId", retailerId);
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

    public void resetIMEI(final String id) {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait..", true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL + "users.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", "response" + response);
                try {
                    progressDialog.dismiss();

                    JSONObject jsonObject1 = new JSONObject(response);
                    if(jsonObject1.getBoolean("ack")) {
                        Toast.makeText(getActivity(), jsonObject1.getString("message"), Toast.LENGTH_LONG).show();
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
                params.put("method", "resetImei");
                params.put("token", prefManager.getToken());
                params.put("fromAccount", prefManager.getUserId());
                params.put("userId", id);
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
