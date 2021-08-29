package recharge.com.myrechargegallery;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ReEnterPinActivity extends AppCompatActivity {

    TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8, tv9, tv0, terms;
    ImageView ivMinus;
    //TextView pin;
    PrefManager sharedPref;
    EditText p1, p2, p3, p4;
    boolean focusP1, focusP2, focusP3, focusP4;
    String pinString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_enter_pin);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#333333")));

        sharedPref = new PrefManager(this);

     /*   if(sharedPref.checkSharedPrefs("contact")){
            terms="";
        }*/

        tv1 = (TextView) findViewById(R.id.textNumber1);
        tv2 = (TextView) findViewById(R.id.textNumber2);
        tv3 = (TextView) findViewById(R.id.textNumber3);
        tv4 = (TextView) findViewById(R.id.textNumber4);
        tv5 = (TextView) findViewById(R.id.textNumber5);
        tv6 = (TextView) findViewById(R.id.textNumber6);
        tv7 = (TextView) findViewById(R.id.textNumber7);
        tv8 = (TextView) findViewById(R.id.textNumber8);
        tv9 = (TextView) findViewById(R.id.textNumber9);
        tv0 = (TextView) findViewById(R.id.textNumber0);

        p1 = (EditText) findViewById(R.id.pin1);
        p2 = (EditText) findViewById(R.id.pin2);
        p3 = (EditText) findViewById(R.id.pin3);
        p4 = (EditText) findViewById(R.id.pin4);

        focusP1 = true;

        ivMinus = (ImageView) findViewById(R.id.ImageCnacel);

        p1.setInputType(InputType.TYPE_NULL);
        p2.setInputType(InputType.TYPE_NULL);
        p3.setInputType(InputType.TYPE_NULL);
        p4.setInputType(InputType.TYPE_NULL);

        p1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusP1 = true;
            }
        });
        p2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusP2 = true;
            }
        });
        p3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusP3 = true;
            }
        });
        p4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusP4 = true;
            }
        });

        applyListeners();
    }

    public void applyListeners() {
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "1";
                setText(text);
            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "2";
                setText(text);
            }
        });
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "3";
                setText(text);
            }
        });
        tv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "4";
                setText(text);
            }
        });
        tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "5";
                setText(text);
            }
        });
        tv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "6";
                setText(text);
            }
        });
        tv7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "7";
                setText(text);
            }
        });
        tv8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "8";
                setText(text);
            }
        });
        tv9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "9";
                setText(text);
            }
        });
        tv0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "0";
                setText(text);
            }
        });
        ivMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int point = pinString.length();
                if (point == 1)
                    p1.setBackgroundResource(R.drawable.bluedot);
                else if (point == 2)
                    p2.setBackgroundResource(R.drawable.bluedot);
                else if (point == 3)
                    p3.setBackgroundResource(R.drawable.bluedot);
                else if (point == 4)
                    p4.setBackgroundResource(R.drawable.bluedot);

                if (pinString.length() > 0)
                    pinString = pinString.substring(0, pinString.length() - 1);

                if (pinString.length() == 4) {
                    submit();
                } else {
                    //ivOk.setImageResource(R.drawable.tickicon);
                }
            }
        });
    }

    public void setText(String text) {

        pinString = pinString + text.trim();
        text = "";

        int point = pinString.length();
        if (point == 1)
            p1.setBackgroundResource(R.drawable.bluewhite);
        else if (point == 2)
            p2.setBackgroundResource(R.drawable.bluewhite);
        else if (point == 3)
            p3.setBackgroundResource(R.drawable.bluewhite);
        else if (point == 4)
            p4.setBackgroundResource(R.drawable.bluewhite);


        if (pinString.length() == 4) {
            submit();
        } /*else {
            ivOk.setImageResource(R.drawable.tickicon);
        }*/
    }
    public void submit() {
        if (pinString.length() == 4) {
            if (PinActivity.pinString.equals(pinString)) {
                sharedPref.setUserPin(pinString);
                Intent intent = new Intent(getApplicationContext(), DrawerActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Pin Not Match", Toast.LENGTH_LONG).show();
                p1.setBackgroundResource(R.drawable.bluedot);
                p2.setBackgroundResource(R.drawable.bluedot);
                p3.setBackgroundResource(R.drawable.bluedot);
                p4.setBackgroundResource(R.drawable.bluedot);
                pinString = "";
            }
        } else {
            Toast.makeText(getApplicationContext(), "Pin must be 4 digit long", Toast.LENGTH_LONG).show();
            p1.setBackgroundResource(R.drawable.bluedot);
            p2.setBackgroundResource(R.drawable.bluedot);
            p3.setBackgroundResource(R.drawable.bluedot);
            p4.setBackgroundResource(R.drawable.bluedot);
            pinString = "";
        }
    }
}

