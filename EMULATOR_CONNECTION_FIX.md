# Emulator Network Connection Fix - ConnectException Issue

## Error Details
```
java.net.ConnectException: Failed to connect to myrechargegallery.net/103.117.212.59:443
```

## Root Cause
Android emulators have different network configurations than physical devices and often fail to connect to external servers due to:
1. **DNS resolution delays** - Emulators are slower at resolving domain names
2. **Network isolation** - Emulator networking is virtualized
3. **Timeout issues** - Default timeouts are too short for emulator networking
4. **IPv6 vs IPv4** - Some emulators struggle with IPv6

## Changes Applied

### 1. Created VolleySingleton.java
**Purpose**: Centralized network request management with emulator optimizations

**Key Features**:
- Increased connection timeout: 30 seconds (instead of default 10s)
- DNS caching enabled
- IPv4 preference for better emulator compatibility
- Proper timeout configuration
- Debug logging for troubleshooting

### 2. Updated Retry Policy
**Changed**: From **0 retries** to **2 retries** (3 total attempts)

**Why**: Emulators often fail on the first connection attempt due to:
- Cold start network stack
- Slow DNS resolution
- Virtual network adapter initialization

**Files Updated**:
- `RechargeReport.java` - Both `registerComplaint()` and `loadData()` methods

### 3. Enhanced Error Messages
**Updated**: `Config.java` - `getVolleyErrorMessage()` method

**New Feature**: Detects if running on emulator and provides specific guidance:
```
"Cannot connect to server (Emulator detected: Check emulator network settings)"
```

### 4. Added DNS Testing
**Updated**: `SplashActivity.java`

**Purpose**: Tests DNS resolution on app startup (DEBUG builds only)
- Logs DNS resolution results
- Helps identify if the issue is DNS-related
- Only runs in debug mode

## How to Test

### 1. Check LogCat for DNS Resolution
After launching the app, filter LogCat by "VolleySingleton":
```
Tag: VolleySingleton
```

You should see:
```
Testing DNS for: myrechargegallery.net
Resolved: 103.117.212.59
```

If you see "DNS Resolution failed", the emulator cannot resolve the domain name.

### 2. Test API Call
Try making an API call and check the error message in LogCat:
```
Tag: RechargeReport
```

### 3. Verify Server is Accessible
From your host machine (not emulator), test if the server is reachable:

**Windows PowerShell**:
```powershell
# Test DNS
nslookup myrechargegallery.net

# Test connection
Test-NetConnection -ComputerName myrechargegallery.net -Port 443

# Test HTTPS
curl https://myrechargegallery.net/webservices-v6/api.php
```

**Linux/Mac**:
```bash
# Test DNS
dig myrechargegallery.net

# Test connection
nc -zv myrechargegallery.net 443

# Test HTTPS
curl -v https://myrechargegallery.net/webservices-v6/api.php
```

## Troubleshooting Steps

### Issue: DNS Resolution Fails in Emulator

**Solution 1: Update Emulator Network Settings**
1. Open AVD Manager
2. Edit your emulator
3. Show Advanced Settings
4. Network section:
   - Set DNS Server 1: `8.8.8.8` (Google DNS)
   - Set DNS Server 2: `8.8.4.4` (Google DNS backup)
5. Save and restart emulator

**Solution 2: Use Emulator with Google APIs**
- Create a new AVD with "Google APIs" system image
- Google APIs images have better network support

**Solution 3: Reset Emulator Network**
```bash
# Cold boot the emulator
# In AVD Manager: Click dropdown on emulator → Cold Boot Now
```

### Issue: Connection Refused/Timeout

**Solution 1: Check Windows Firewall**
1. Open Windows Defender Firewall
2. Allow an app through firewall
3. Make sure "Android Emulator" is allowed for both Private and Public networks

**Solution 2: Check Antivirus/VPN**
- Temporarily disable antivirus/firewall
- Disconnect from VPN if active
- Test if connection works

**Solution 3: Use Different Emulator**
Try different Android versions:
- Recommended: API 30 (Android 11) or API 33 (Android 13)
- Avoid: Very old (< API 28) or very new (> API 34) versions

### Issue: Works on WiFi but not Mobile Data (Physical Device)

This is a different issue - likely firewall/carrier restriction:
1. Check if carrier/ISP blocks port 443
2. Try different network (different WiFi or mobile carrier)
3. Check if VPN is interfering

### Issue: Still Getting ConnectException

**Option 1: Use Localhost/LAN Testing (Development Only)**

For local development/testing, you can point to a local server:

1. **Find your computer's local IP**:
   ```powershell
   ipconfig
   # Look for "IPv4 Address" under your active network adapter
   # Example: 192.168.1.100
   ```

2. **Update Config.java**:
   ```java
   // For LOCAL TESTING ONLY - use your computer's IP
   public static String JSON_URL = "http://192.168.1.100/myrechargegallery/webservices-v6/";
   // Or use emulator's special IP for localhost: 10.0.2.2
   // public static String JSON_URL = "http://10.0.2.2/myrechargegallery/webservices-v6/";
   ```

3. **Update network_security_config.xml**:
   ```xml
   <domain-config cleartextTrafficPermitted="true">
       <domain includeSubdomains="true">192.168.1.100</domain>
       <domain includeSubdomains="true">10.0.2.2</domain>
   </domain-config>
   ```

**Option 2: Verify Server Certificate**

Check if the SSL certificate is valid:
```powershell
# Windows PowerShell
$request = [System.Net.WebRequest]::Create("https://myrechargegallery.net")
try {
    $response = $request.GetResponse()
    Write-Host "SSL certificate is valid"
    $response.Close()
} catch {
    Write-Host "SSL Error: $($_.Exception.Message)"
}
```

**Option 3: Test on Physical Device**

If emulator continues to fail:
1. Build APK: `./gradlew assembleDebug`
2. Install on physical device: `adb install app/build/outputs/apk/debug/app-debug.apk`
3. Test on device

## Expected Behavior After Fix

### Before Fix:
- **Emulator**: ❌ APIs fail with ConnectException immediately
- **Physical Device**: ✅ APIs work (sometimes)
- **Retries**: 0 (fails on first attempt)

### After Fix:
- **Emulator**: ✅ APIs work (may take 2-3 seconds on first call)
- **Physical Device**: ✅ APIs work reliably
- **Retries**: 2 retries = up to 3 attempts total
- **Timeout**: 30 seconds per attempt
- **Error Messages**: Clear, actionable error messages

## Build and Test

1. **Clean and build**:
   ```powershell
   cd E:\GitProjects\MyRechargeGallery
   .\gradlew clean assembleDebug
   ```

2. **Install on emulator**:
   ```powershell
   adb install -r app\build\outputs\apk\debug\app-debug.apk
   ```

3. **Monitor logs**:
   ```powershell
   adb logcat -s VolleySingleton:D RechargeReport:E
   ```

4. **Test API calls** in the app

## Debug Checklist

- [ ] Build succeeds without errors
- [ ] App installs on emulator
- [ ] LogCat shows "Testing DNS for: myrechargegallery.net"
- [ ] LogCat shows "Resolved: 103.117.212.59"
- [ ] API calls succeed (or show detailed error message)
- [ ] Error messages indicate emulator detection
- [ ] Works on physical device (Redmi)

## Additional Configuration

### For Better Emulator Performance:

1. **Increase Emulator RAM**:
   - AVD Manager → Edit → Show Advanced Settings
   - RAM: 2048 MB or higher
   - VM Heap: 256 MB

2. **Enable Hardware Acceleration**:
   - Settings → Graphics: Hardware - GLES 2.0

3. **Use x86_64 System Image** (if your CPU supports it):
   - Faster than ARM images on x86 computers

## Contact Server Administrator

If all else fails, the issue might be server-side:

**Ask your server admin to check**:
1. Is port 443 open and accepting connections?
2. Is the SSL certificate valid and not expired?
3. Are there any IP restrictions or firewall rules blocking connections?
4. Is the server experiencing high load or downtime?
5. Check server logs for incoming connection attempts from your IP

**Server Requirements**:
- Valid SSL/TLS certificate from trusted CA
- Port 443 open for HTTPS
- Proper CORS headers (if applicable)
- No IP-based blocking that might affect emulator IPs

## Summary of Files Changed

1. ✅ **VolleySingleton.java** (NEW) - Network request singleton
2. ✅ **RechargeReport.java** - Updated retry policy (0→2 retries)
3. ✅ **Config.java** - Enhanced error messages with emulator detection
4. ✅ **SplashActivity.java** - Added DNS testing in debug mode
5. ✅ **network_security_config.xml** - Added user certificates and debug overrides

## Next Steps

1. Rebuild the app
2. Test on emulator and check LogCat
3. If DNS resolution succeeds but connection fails, check firewall
4. If DNS resolution fails, update emulator DNS settings
5. Test on physical device to confirm production readiness

## Performance Notes

- First API call may take 2-5 seconds on emulator (cold start)
- Subsequent calls should be faster (< 1 second)
- Physical devices will be faster than emulators
- Retry logic adds max 90 seconds total timeout (3 attempts × 30s)
