package recharge.com.myrechargegallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import recharge.com.myrechargegallery.databinding.ActivityWebBinding;

public class WebActivity extends AppCompatActivity {

    ActivityWebBinding binding;
    WebViewClient webViewClient;
    PrefManager prefManager;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Wallet Balance");
        setResult(RESULT_OK);
        prefManager = new PrefManager(this);
        if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

//        webViewClient = new WebViewClient(){
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                Log.d("veer url",url);
//                if (url.contains("client_txn_id")){
//                    finish();
//                }
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                if (url.contains("client_txn_id")){
//                    finish();
//                }
//            }
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                if (url.startsWith("upi:")) {    //To allow link which starts with upi://
//                    Intent intent = new Intent(Intent.ACTION_VIEW);  // To show app chooser
//                    intent.setData(Uri.parse(url));
//                    startActivity(intent);
//                    return true;
//                }
//                view.loadUrl(url);
//                return true;
//            }
//
//        };

        initWebView();
        binding.webView.loadUrl(prefManager.getPaymentUrl());



    }
    @SuppressLint({ "SetJavaScriptEnabled" })
    private void initWebView() {
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.setWebChromeClient(new WebChromeClient());
        binding.webView.addJavascriptInterface(new WebviewInterface(), "Interface");
    }

    public class WebviewInterface {
        @JavascriptInterface
        public void errorResponse() {
            // this function is called when Transaction in Already Done or Any other Issue.
            Toast.makeText(WebActivity.this, "Transaction Error.", Toast.LENGTH_SHORT).show();
            // Close the Webview.
            finish();
        }

        @JavascriptInterface
        public void paymentResponse(String client_txn_id, String txn_id) {
            Log.i("WebActivity", txn_id);
            Log.i("WebActivity", client_txn_id);
            // this function is called when payment is done (success, scanning ,timeout or cancel by user).
            // You must call the check order status API in server and get update about payment.
            // 🚫 Do not Call UPIGateway API in Android App Directly.
            Toast.makeText(WebActivity.this, "Payment has been Done.", Toast.LENGTH_SHORT).show();
            // Close the Webview.
            finish();
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}