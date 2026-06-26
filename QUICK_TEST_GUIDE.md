# Quick Testing Guide - ConnectException Fix

## What Was Fixed

The `ConnectException: Failed to connect to myrechargegallery.net/103.117.212.59:443` error was caused by:
1. **Zero retries** - App gave up after first failure
2. **Short timeouts** - Not enough time for emulator network initialization
3. **No network optimization** - Each request created a new RequestQueue
4. **Poor error messages** - Didn't explain the real problem

## Changes Applied

✅ **VolleySingleton.java** (NEW)
- Centralized request queue management
- 30-second connection timeout (vs default 10s)
- IPv4 preference for emulator compatibility
- DNS caching enabled
- Network logging for debugging

✅ **RechargeReport.java**
- Retry policy: 0 → **2 retries** (3 total attempts)
- Using VolleySingleton instead of creating new queues

✅ **Config.java**
- Enhanced error messages with emulator detection
- Specific guidance for ConnectException errors

✅ **SplashActivity.java**
- DNS testing on app startup (debug builds only)

✅ **network_security_config.xml**
- Added debug overrides for SSL
- User certificate support

## Test Now

### Step 1: Install on Emulator
```powershell
# Install the newly built APK
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

### Step 2: Monitor Logs
Open a new terminal and run:
```powershell
# Filter for relevant logs
adb logcat -s VolleySingleton:D RechargeReport:E SplashActivity:D
```

### Step 3: Launch App
1. Open the app on emulator
2. Check LogCat for DNS test results:
   ```
   VolleySingleton: Testing DNS for: myrechargegallery.net
   VolleySingleton: Resolved: 103.117.212.59
   ```

### Step 4: Test API Call
1. Login or navigate to a screen that makes an API call
2. Try "Recharge Report" or any other API feature
3. Watch LogCat for:
   - **Success**: Should work within 2-5 seconds
   - **Failure**: Will show detailed error message

## Expected Results

### ✅ Success Case:
```
VolleySingleton: Testing DNS for: myrechargegallery.net
VolleySingleton: Resolved: 103.117.212.59
VolleySingleton: Connecting to: https://myrechargegallery.net/webservices-v6/api.php
[API response data appears]
```

### ❌ If Still Failing:

**DNS Resolution Failed**:
```
VolleySingleton: DNS Resolution failed: UnknownHostException
```
**Fix**: Update emulator DNS settings:
1. AVD Manager → Edit emulator → Show Advanced Settings
2. DNS Server 1: `8.8.8.8`
3. DNS Server 2: `8.8.4.4`
4. Restart emulator

**Connection Still Timing Out**:
```
RechargeReport: API Error: Cannot connect to server (Emulator detected: Check emulator network settings)
```
**Fix**: 
1. Check Windows Firewall - allow Android Emulator
2. Disable VPN temporarily
3. Try different emulator (API 30 or 33)

**SSL Certificate Error**:
```
RechargeReport: API Error: SSL Certificate Error
```
**Fix**: Server certificate may be invalid - check with server admin

## Verify on Physical Device

After confirming the fix works on emulator:

1. **Build release APK** (or use debug):
   ```powershell
   .\gradlew assembleDebug
   ```

2. **Install on Redmi device**:
   - Copy `app\build\outputs\apk\debug\app-debug.apk` to phone
   - Install and test

3. **Verify APIs work** on physical device

## Troubleshooting Commands

### Check if server is accessible from your computer:
```powershell
# Test DNS
nslookup myrechargegallery.net

# Test HTTPS connection
curl https://myrechargegallery.net/webservices-v6/api.php

# Or in PowerShell
Test-NetConnection -ComputerName myrechargegallery.net -Port 443
```

### Check emulator internet:
```powershell
# From emulator terminal (via adb shell)
adb shell
ping -c 4 8.8.8.8
ping -c 4 myrechargegallery.net
curl -v https://myrechargegallery.net
```

### Clear app data if needed:
```powershell
adb shell pm clear recharge.com.myrechargegallery
```

## Performance Expectations

| Scenario | Before Fix | After Fix |
|----------|-----------|-----------|
| **Emulator - First Call** | ❌ Fails immediately | ✅ 2-5 seconds |
| **Emulator - Subsequent** | ❌ Fails immediately | ✅ < 1 second |
| **Physical Device** | ✅ Works (sometimes) | ✅ Works reliably |
| **Retry Attempts** | 1 (no retry) | 3 (2 retries) |
| **Total Timeout** | 10 seconds | 30 seconds × 3 = 90s max |

## What to Report

If the issue persists, provide these details:

1. **LogCat output** from the test (copy relevant lines)
2. **Emulator details**:
   - Android version (API level)
   - System image type (Google APIs, Play Store, etc.)
3. **Network test results**:
   - Does `nslookup myrechargegallery.net` work on host PC?
   - Does `curl https://myrechargegallery.net` work?
4. **Error message** from the app
5. **Does it work on physical device?** (Yes/No)

## Files Changed (for Reference)

- `app/src/main/java/recharge/com/myrechargegallery/VolleySingleton.java` ⭐ NEW
- `app/src/main/java/recharge/com/myrechargegallery/RechargeReport.java`
- `app/src/main/java/recharge/com/myrechargegallery/Config.java`
- `app/src/main/java/recharge/com/myrechargegallery/SplashActivity.java`
- `app/src/main/res/xml/network_security_config.xml`

## APK Location

Debug APK: `E:\GitProjects\MyRechargeGallery\app\build\outputs\apk\debug\app-debug.apk`

✅ Ready to install and test!
