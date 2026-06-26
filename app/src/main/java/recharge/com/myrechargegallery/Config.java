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
//    public static String JSON_URL = "https://10.50.37.117/webservices-v6/";
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
    
    /**
     * Get detailed error message from VolleyError for debugging
     * Helps identify SSL, network, timeout, and server errors
     */
    public static String getVolleyErrorMessage(com.android.volley.VolleyError error) {
        if (error == null) {
            return "Unknown error occurred";
        }
        
        String message = "Network Error: ";
        
        if (error.networkResponse != null) {
            // Server returned an error response
            int statusCode = error.networkResponse.statusCode;
            message += "HTTP " + statusCode;
            
            if (statusCode == 404) {
                message += " - API endpoint not found";
            } else if (statusCode >= 500) {
                message += " - Server error";
            } else if (statusCode >= 400) {
                message += " - Client error";
            }
            
            // Try to get response body
            try {
                String responseBody = new String(error.networkResponse.data, "UTF-8");
                if (responseBody.length() > 0 && responseBody.length() < 200) {
                    message += " - " + responseBody;
                }
            } catch (Exception e) {
                // Ignore
            }
        } else if (error.getCause() != null) {
            // Network or SSL error
            Throwable cause = error.getCause();
            String causeMessage = cause.getClass().getSimpleName();
            
            if (causeMessage.contains("SSLHandshake") || causeMessage.contains("SSL")) {
                message += "SSL Certificate Error - Please check network security settings";
            } else if (causeMessage.contains("UnknownHost")) {
                message += "Cannot reach server - Check internet connection";
            } else if (causeMessage.contains("Timeout") || causeMessage.contains("SocketTimeout")) {
                message += "Request timeout - Server is slow or not responding";
            } else if (causeMessage.contains("ConnectException") || causeMessage.contains("Connect")) {
                message += "Cannot connect to server";
                // Add emulator-specific guidance
                if (android.os.Build.FINGERPRINT.contains("generic") || 
                    android.os.Build.FINGERPRINT.contains("emulator") ||
                    android.os.Build.MODEL.contains("Emulator") ||
                    android.os.Build.MODEL.contains("Android SDK")) {
                    message += " (Emulator detected: Check emulator network settings)";
                } else {
                    message += " (Server may be down or unreachable)";
                }
            } else {
                message += causeMessage;
                if (cause.getMessage() != null) {
                    message += " - " + cause.getMessage();
                }
            }
        } else if (error.getMessage() != null) {
            message += error.getMessage();
        } else {
            message += "Unknown network error";
        }
        
        return message;
    }
}