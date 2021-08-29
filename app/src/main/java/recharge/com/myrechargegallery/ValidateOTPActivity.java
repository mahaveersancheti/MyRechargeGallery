package recharge.com.myrechargegallery;

import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ValidateOTPActivity extends AppCompatActivity {

    public static EditText etOtp;
    PrefManager prefManager;
    String otp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_otp);

        prefManager = new PrefManager(this);

        otp = getIntent().getStringExtra("otp");

        etOtp = (EditText) findViewById(R.id.etOtp);
    }

    public void verify(View v) {
        String otpStr = etOtp.getText().toString().trim();
        if(otpStr.length()==0) {
            etOtp.setError("Invalid Value");
        } else {
            if(otpStr.equals(otp)) {
                prefManager.setIsLogin(true);
                Intent intent = new Intent(getApplicationContext(), PinActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Wrong OTP", Toast.LENGTH_LONG).show();
            }
        }
    }
}
