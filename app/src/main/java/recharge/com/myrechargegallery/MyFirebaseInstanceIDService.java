package recharge.com.myrechargegallery;

import android.annotation.SuppressLint;
import android.util.Log;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseIIDService";

    public void onTokenRefresh() {

        //Getting registration token
//        String refreshedToken = FirebaseMessaging.getInstance().getToken();
        final String[] refreshedToken = {""};

        FirebaseInstallations.getInstance().getId().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("veer", "getInstanceId failed", task.getException());
                    return;
                }

                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            // Get new Instance ID token
                            String token = task.getResult();
                            Log.d("veer token", token + "");

                        }
                    }
                });

//                // Get new Instance ID token
                refreshedToken[0] = task.getResult();
                Log.d("veer token installation", task.getResult() + "");

                //Toast.makeText(LoginActivity.this, token, Toast.LENGTH_SHORT).show();
            }
        });

       // sendRegistrationToServer(refreshedToken);
        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken[0]);

    }
}
