package recharge.com.myrechargegallery.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import recharge.com.myrechargegallery.ValidateOTPActivity;

public class SmsReceiver extends BroadcastReceiver {

    //interface
    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        for(int i=0;i<pdus.length;i++) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = smsMessage.getDisplayOriginatingAddress();
            //Check the sender to filter messages which we require to read
            String messageBody = smsMessage.getMessageBody();
            if(messageBody.contains("OTP for My Recharge Gallery")) {
                Toast.makeText(context, messageBody, Toast.LENGTH_SHORT).show();
                ValidateOTPActivity.etOtp.setText(messageBody);
            } else {
                //Toast.makeText(context, "No OTP", Toast.LENGTH_SHORT).show();
            }

            //Pass the message text to interface
            //mListener.messageReceived(messageBody);
        }
    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}