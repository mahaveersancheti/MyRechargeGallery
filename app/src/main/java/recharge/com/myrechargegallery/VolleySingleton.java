package recharge.com.myrechargegallery;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

/**
 * Singleton class for managing Volley RequestQueue
 * Optimized for emulator and physical device compatibility
 * Handles DNS resolution issues common in Android emulators
 */
public class VolleySingleton {
    private static VolleySingleton instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private VolleySingleton(Context context) {
        ctx = context.getApplicationContext();
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // Create RequestQueue with custom HurlStack for better network handling
            // This helps with emulator networking issues
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext(), 
                new HurlStack() {
                    @Override
                    protected HttpURLConnection createConnection(URL url) throws IOException {
                        HttpURLConnection connection = super.createConnection(url);
                        
                        // Set timeouts - important for emulators with slow network
                        connection.setConnectTimeout(30000); // 30 seconds to connect
                        connection.setReadTimeout(30000);    // 30 seconds to read
                        
                        // Enable DNS caching for better performance
                        System.setProperty("networkaddress.cache.ttl", "60");
                        System.setProperty("networkaddress.cache.negative.ttl", "10");
                        
                        // Prefer IPv4 over IPv6 for better emulator compatibility
                        System.setProperty("java.net.preferIPv4Stack", "true");
                        
                        // Log the connection for debugging
                        Log.d("VolleySingleton", "Connecting to: " + url.toString());
                        
                        return connection;
                    }
                });
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        // Add default retry policy if not set
        if (req.getRetryPolicy() == null || req.getRetryPolicy() instanceof DefaultRetryPolicy) {
            // 30 seconds timeout, 2 retries, exponential backoff
            req.setRetryPolicy(new DefaultRetryPolicy(
                30000,  // 30 second timeout
                2,      // 2 retry attempts (total 3 tries)
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // 1.0 backoff multiplier
            ));
        }
        getRequestQueue().add(req);
    }

    /**
     * Test DNS resolution for debugging emulator network issues
     */
    public static void testDNSResolution(final String hostname) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("VolleySingleton", "Testing DNS for: " + hostname);
                    InetAddress[] addresses = InetAddress.getAllByName(hostname);
                    for (InetAddress addr : addresses) {
                        Log.d("VolleySingleton", "Resolved: " + addr.getHostAddress());
                    }
                } catch (Exception e) {
                    Log.e("VolleySingleton", "DNS Resolution failed: " + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Clear the request queue cache
     */
    public void clearCache() {
        if (requestQueue != null) {
            requestQueue.getCache().clear();
        }
    }
}
