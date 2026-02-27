package recharge.com.myrechargegallery;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class Config {
//    public static String JSON_URL = "http://192.168.1.12/myrechargegallery/webservices-v6/";
    public static String JSON_URL = "https://myrechargegallery.net/webservices-v6/";
//    public static String JSON_URL = "https://thekidzit.in/myrechargegallery/";

    /**
     * Check if device has active internet connection
     * Works with modern networks including Jio 5G with IPv6
     */
    public static boolean hasConnection(Context context) {
        if (context == null) {
            return false;
        }
        
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        
        if (cm == null) {
            return false;
        }
        
        // Use modern API for Android M (API 23) and above - works with Jio 5G
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network activeNetwork = cm.getActiveNetwork();
            if (activeNetwork == null) {
                return false;
            }
            
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(activeNetwork);
            if (capabilities == null) {
                return false;
            }
            
            // Check if network has internet capability
            // This works for WiFi, Mobile Data (4G/5G including Jio), and Ethernet
            // Note: Only check NET_CAPABILITY_INTERNET, not NET_CAPABILITY_VALIDATED
            // because VALIDATED can return false on some carriers (e.g., Jio) even
            // when the network is fully functional
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        } else {
            // Fallback for older Android versions (below API 23)
            NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiNetwork != null && wifiNetwork.isConnected()) {
                return true;
            }
            NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mobileNetwork != null && mobileNetwork.isConnected()) {
                return true;
            }
            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }
}