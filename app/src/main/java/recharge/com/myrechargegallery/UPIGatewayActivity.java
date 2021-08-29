package recharge.com.myrechargegallery;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;

public class UPIGatewayActivity extends AppCompatActivity {

    EditText etxt_upi, etxt_amount;
    Button btn_pay;
    PrefManager prefManager;
    int GOOGLE_PAY_REQUEST_CODE = 123;

    //https://developers.google.com/pay/api/android/guides/setup
    //https://developers.google.com/pay/india/api/android/in-app-payments

    public boolean isUpiValid(String text){
        return text.matches("^[\\w-]+@\\w+$");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_p_i_gateway);

        prefManager = new PrefManager(this);

        etxt_amount = findViewById(R.id.etxt_amount);
        etxt_upi = findViewById(R.id.etxt_upi);
        btn_pay = findViewById(R.id.btn_pay);

        etxt_amount.setText(1 + "");

        etxt_upi.setText("7758889888@upi");

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final String key = "1910486f-daa9-449d-8ac3-0878b0b22798";
//                // Get the Key from https://upigateway.com/user/api_credentials
//                if(!isUpiValid(etxt_upi.getText().toString().trim())){
//                    Toast.makeText(getApplicationContext(), "Invalid UPI", Toast.LENGTH_LONG).show();
//                    return ;
//                }

                String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
//                int GOOGLE_PAY_REQUEST_CODE = 123;
                Uri uri = new Uri.Builder()
                            .scheme("upi")
                            .authority("pay")
                            .appendQueryParameter("pa", "manojjain514439@gmail.com")  // merchant email
                            .appendQueryParameter("pn", "my recharge gallery") //my recharge gallery     // merchant name
                            .appendQueryParameter("mc", "BCR2DN6TT7W4DBZI")   // merchant code
                            .appendQueryParameter("tr", "001")  // transaction ref id
                            .appendQueryParameter("tn", "testing")    // transaction note
                            .appendQueryParameter("am", "1")    // amount
                            .appendQueryParameter("cu", "INR")
                            .appendQueryParameter("url", "http://myrechargegallery.net/callback_urls/upi_gateway.php")    // transaction url
                            .build();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
                startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);

//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setPackage("com.android.chrome");
//                startActivity(intent);

                // third party integration
//                Uri.Builder builder = new Uri.Builder();
//                builder.scheme("https")
//                        .authority("upigateway.com")
//                        .appendPath("gateway")
//                        .appendPath("android")
//                        .appendQueryParameter("key", key)
//                        .appendQueryParameter("client_vpa", etxt_upi.getText().toString())
//                        .appendQueryParameter("client_txn_id", UUID.randomUUID().toString())
//                        .appendQueryParameter("amount", etxt_amount.getText().toString())
//                        // Amount Can also be hidden if your product price is fix
//                        .appendQueryParameter("p_info", "RECHARGE")
//                        .appendQueryParameter("client_name", "Manoj") // Set Client Name.
//                        .appendQueryParameter("client_email", "manojjain514439@gmail.com")
//                        .appendQueryParameter("client_mobile", "9595055559")
//                        .appendQueryParameter("udf1", "1")
//                        // udf var is used to store the variable data and
//                        // get same in callback response. ex. user_id, product_id
//                        .appendQueryParameter("udf2", prefManager.getUserId() + "")
//                        .appendQueryParameter("udf3", "1")
//                        .appendQueryParameter("redirect_url", "http://myrechargegallery.net/callback_urls/upi_gateway.php");
//
//                Intent intent = new Intent(Intent.ACTION_VIEW, builder.build());
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setPackage("com.android.chrome");
//                try {
//                    getApplicationContext().startActivity(intent);
//                } catch (ActivityNotFoundException ex) {
//                    // Chrome browser presumably not installed so allow user to choose instead
//                    intent.setPackage(null);
//                    getApplicationContext().startActivity(intent);
//                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_PAY_REQUEST_CODE) {
            // Process based on the data in response.
            Log.d("veer result", data.getStringExtra("Status"));
        }
    }
}