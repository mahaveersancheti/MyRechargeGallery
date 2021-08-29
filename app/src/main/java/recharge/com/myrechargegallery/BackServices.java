package recharge.com.myrechargegallery;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BackServices extends Service {

    PrefManager myPref;
    private NotificationManager mNM;
    double lat, lng;

    public BackServices() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Log.d("veer", "from back serveri");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //Toast.makeText(getApplicationContext(), "From Service", Toast.LENGTH_LONG).show();

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.

        myPref = new PrefManager(getApplicationContext());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                while(true) {
                    try {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                            // Check Permissions Now
                        } else {
                            Log.d("veer", "location start");

                            LocationRequest mLocationRequest = LocationRequest.create();
                            mLocationRequest.setInterval(60000);
                            mLocationRequest.setFastestInterval(5000);
                            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                            LocationCallback mLocationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    if (locationResult == null) {
                                        return;
                                    }
                                    for (Location location : locationResult.getLocations()) {
                                        if (location != null) {
                                            //TODO: UI updates.
                                        }
                                    }
                                }
                            };
                            LocationServices.getFusedLocationProviderClient(getApplicationContext()).requestLocationUpdates(mLocationRequest, mLocationCallback, null);

                            // permission has been granted, continue as usual
                            FusedLocationProviderClient mFusedLocationClient;
                            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    try {
                                        if(location != null) {
                                            lat = location.getLatitude();
                                            lng = location.getLongitude();
                                            Log.d("veer", lat + ", " + lng);
                                            updateData();
                                        } else {
                                            Log.d("veer", "location is null");
                                            //Toast.makeText(getApplicationContext(), "Location is null", Toast.LENGTH_SHORT).show();
                                        }
                                    }catch(Exception e) {
                                        Log.d("veer update 17", e.getMessage() + "");
                                    }
                                }
                            });
                        }
                        Thread.sleep(1000*10);
                    } catch(Exception e) {
                    }
                }
            }
        }).start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    public void updateData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.JSON_URL+"users.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("veer", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("ack")) {
                        //Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }  else {
                        //Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                    Log.d("veer err", e.getMessage()+"");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "Network Error Occure", Toast.LENGTH_LONG).show();
                Log.d("veer err", error.getMessage()+"");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("location", lat+","+lng);
                params.put("fromAccount", myPref.getUserId());
                params.put("method","updateLocation");
                params.put("imei", myPref.getImei());
                params.put("token", myPref.getToken());

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplication());
        requestQueue.add(stringRequest);
    }


}
