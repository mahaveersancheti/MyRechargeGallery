package recharge.com.myrechargegallery;

import android.content.Context;
import android.content.SharedPreferences;


public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "goerecharge";
    private static final String isLogin = "isLogin";
    private static final String token = "token";
    private static final String userId = "userId";
    private static final String userType = "userType";  //admin, distributor, retailer, home
    private static final String imei = "imei";
    private static final String shopName = "shopName";
    private static final String name = "name";
    private static final String balance = "balance";

    private static final String schemeUrl = "schemeUrl";

    private static final String prepaid = "prepaid";
    private static final String postpaid = "postpaid";
    private static final String dth = "dth";
    private static final String electricity = "electricity";
    private static final String gas = "gas";
    private static final String money_transfer = "money_transfer";
    public static final String userPin = "user_pin";

    public static final String smsBackupDate = "smsBackupDate";
    public static final String smsBackupTime = "smsBackupTime";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void clearPreference() {
        editor.clear();
        editor.commit();
    }

    public boolean checkSharedPrefs(String key) {
        if (pref.contains(key)) {
            return true;
        }
        return false;
    }

    public void setUserPin(String value) {
        editor.putString(userPin, value);
        editor.commit();
    }
    public String getUserPin() {
        return pref.getString(userPin, "");
    }

    public void setSchemeUrl(String value) {
        editor.putString(schemeUrl, value);
        editor.commit();
    }
    public String getSchemeUrl() {
        return pref.getString(schemeUrl, "");
    }

    public void setUserId(String value) {
        editor.putString(userId, value);
        editor.commit();
    }
    public String getUserId() {
        return pref.getString(userId, "");
    }

    public String getUserType() {
        return pref.getString(userType, "");
    }

    public void setUserType(String value) {
        editor.putString(userType, value);
        editor.commit();
    }

    public String getImei() {
        return pref.getString(imei, "");
    }
    public void setImei(String value) {
        editor.putString(imei, value);
        editor.commit();
    }

    public String getShopName() {
        return pref.getString(shopName, "");
    }
    public void setShopName(String value) {
        editor.putString(shopName, value);
        editor.commit();
    }

    public String getName() {
        return pref.getString(name, "");
    }
    public void setName(String value) {
        editor.putString(name, value);
        editor.commit();
    }

    public float getBalance() {
        return pref.getFloat(balance, 0);
    }
    public void setBalance(float value) {
        editor.putFloat(balance, value);
        editor.commit();
    }

    public boolean getPrepaid() {
        return pref.getBoolean(prepaid, false);
    }
    public void setPrepaid(boolean value) {
        editor.putBoolean(prepaid, value);
        editor.commit();
    }

    public boolean getPostpaid() {
        return pref.getBoolean(postpaid, false);
    }
    public void setPostpaid(boolean value) {
        editor.putBoolean(postpaid, value);
        editor.commit();
    }

    public boolean getDth() {
        return pref.getBoolean(dth, false);
    }
    public void setDth(boolean value) {
        editor.putBoolean(dth, value);
        editor.commit();
    }

    public boolean getElectricity() {
        return pref.getBoolean(electricity, false);
    }
    public void setElectricity(boolean value) {
        editor.putBoolean(electricity, value);
        editor.commit();
    }

    public boolean getGas() {
        return pref.getBoolean(gas, false);
    }
    public void setGas(boolean value) {
        editor.putBoolean(gas, value);
        editor.commit();
    }

    public boolean getMoneyTransfer() {
        return pref.getBoolean(money_transfer, false);
    }
    public void setMoneyTransfer(boolean value) {
        editor.putBoolean(money_transfer, value);
        editor.commit();
    }

    public void setIsLogin(boolean value) {
        editor.putBoolean(isLogin, value);
        editor.commit();
    }
    public boolean getIsLogin() {
        return pref.getBoolean(isLogin, false);
    }

    public void setToken(String value) {
        editor.putString(token, value);
        editor.commit();
    }
    public String getToken() {
        return pref.getString(token, "");
    }

    public void setSmsBackupDate(String value) {
        editor.putString(smsBackupDate, value);
        editor.commit();
    }
    public String getSmsBackupDate() {
        return pref.getString(smsBackupDate, "");
    }

    public void setSmsBackupTime(String value) {
        editor.putString(smsBackupTime, value);
        editor.commit();
    }
    public String getSmsBackupTime() {
        return pref.getString(smsBackupTime, "01:01:01");
    }


}