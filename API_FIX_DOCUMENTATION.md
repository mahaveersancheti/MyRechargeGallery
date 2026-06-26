# API Network Issue Fix - Emulator vs Physical Device

## Problem Summary
APIs work on Redmi devices but fail on Android emulators due to SSL certificate validation issues.

## Root Cause
The `network_security_config.xml` was configured to only trust system certificates. Android emulators have stricter certificate validation than some physical devices, causing HTTPS requests to fail.

## Changes Made

### 1. Updated `network_security_config.xml`
**File**: `app/src/main/res/xml/network_security_config.xml`

**Changes**:
- Added `<certificates src="user" />` to trust user-installed certificates
- Added `<debug-overrides>` section for debug builds
- This allows emulators to work while maintaining security in release builds

### 2. Added Error Logging Helper
**File**: `app/src/main/java/recharge/com/myrechargegallery/Config.java`

**Added method**: `getVolleyErrorMessage(VolleyError error)`
- Provides detailed error messages for SSL, timeout, and network errors
- Helps identify the exact cause of API failures
- Distinguishes between SSL errors, timeouts, server errors, etc.

### 3. Updated Error Handling Example
**File**: `app/src/main/java/recharge/com/myrechargegallery/RechargeReport.java`

**Changes**:
- Updated one API call to use the new error logging
- Shows detailed error messages to users and logs
- Template for updating other API calls

## How to Test

1. **Clean and Rebuild**:
   ```bash
   ./gradlew clean build
   ```

2. **Test on Emulator**:
   - Run the app on an Android emulator
   - Try making API calls
   - Check LogCat for detailed error messages (filter: "RechargeReport")

3. **Test on Physical Device**:
   - Install and test on Redmi or other physical devices
   - Verify existing functionality still works

## Additional Solutions (If Issue Persists)

### Option 1: Check SSL Certificate Validity
If errors continue, check the SSL certificate for `myrechargegallery.net`:
```bash
openssl s_client -connect myrechargegallery.net:443 -servername myrechargegallery.net
```

Issues to look for:
- Expired certificate
- Self-signed certificate
- Incomplete certificate chain
- Wrong Common Name (CN) or Subject Alternative Name (SAN)

### Option 2: Temporary Workaround (Development Only)
If you need immediate testing and can't fix the certificate:

Create a custom `HurlStack` that bypasses SSL validation:
```java
// WARNING: Only use for development testing!
// NEVER use in production builds!

public class UnsafeHurlStack extends HurlStack {
    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection connection = super.createConnection(url);
        if (connection instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
            try {
                // Trust all certificates (INSECURE!)
                TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
                };
                
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new SecureRandom());
                httpsConnection.setSSLSocketFactory(sc.getSocketFactory());
                httpsConnection.setHostnameVerifier((hostname, session) -> true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}
```

### Option 3: Use HTTP for Local Testing
For local API testing only, you can temporarily switch to HTTP:

In `Config.java`:
```java
// For local testing only
public static String JSON_URL = "http://192.168.1.12/myrechargegallery/webservices-v6/";
```

And update `network_security_config.xml`:
```xml
<domain-config cleartextTrafficPermitted="true">
    <domain includeSubdomains="true">192.168.1.12</domain>
</domain-config>
```

### Option 4: Update Server SSL Certificate
**Best long-term solution**: Ensure your server has a valid SSL certificate from a trusted Certificate Authority (CA):

1. Use Let's Encrypt (free): https://letsencrypt.org/
2. Use a commercial CA certificate
3. Ensure certificate chain is complete
4. Test with: https://www.ssllabs.com/ssltest/

## Recommended Action Plan

1. ✅ **First**: Test with the current changes (already applied)
2. If still failing, check LogCat for specific error message
3. Based on error:
   - "SSL Certificate Error" → Fix server certificate (Option 4)
   - "Request timeout" → Check server performance/network
   - "Cannot reach server" → Check network connectivity
   - "HTTP 404/500" → Check API endpoint configuration

## Security Notes

- The `<debug-overrides>` section only applies to debug builds
- Release builds (signed APKs) maintain strict security
- User certificates are allowed but system validation still occurs
- For production, always use valid SSL certificates from trusted CAs

## Apply Error Logging to Other API Calls

Update other files similarly to `RechargeReport.java`:

**Before**:
```java
public void onErrorResponse(VolleyError error) {
    Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_LONG).show();
}
```

**After**:
```java
public void onErrorResponse(VolleyError error) {
    String errorMessage = Config.getVolleyErrorMessage(error);
    Log.e("YourClass", "API Error: " + errorMessage);
    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
}
```

Files to update:
- `CreateDistributor.java`
- `ChangePassword.java`
- `CompaintRegister.java`
- `BackServices.java`
- `GasBillPayment.java`
- `CommissionChart.java`
- And any other files making API calls

## Contact & Support

If issues persist after these fixes:
1. Check LogCat for the specific error message
2. Verify server SSL certificate is valid and not expired
3. Test on multiple emulator versions (API 28, 30, 33)
4. Test with device internet vs emulator proxy settings
