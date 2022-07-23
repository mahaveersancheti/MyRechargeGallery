package recharge.com.myrechargegallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
        webViewClient = new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("veer url",url);
                if (url.contains("client_txn_id")){
                    finish();
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.contains("client_txn_id")){
                    finish();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("upi:")) {    //To allow link which starts with upi://
                    Intent intent = new Intent(Intent.ACTION_VIEW);  // To show app chooser
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                view.loadUrl(url);
                return true;
            }

        };

        binding.webView.loadUrl(prefManager.getPaymentUrl());
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        binding.webView.setWebViewClient(webViewClient);


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}