package recharge.com.myrechargegallery;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PaymentRequest extends Fragment {

    View view;
    PrefManager prefManager;

    public static PaymentRequest newInstance() {
        PaymentRequest fragment = new PaymentRequest();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.payment_request_fragment, container, false);
        DrawerActivity.setTitle("Payment Request");

        prefManager = new PrefManager(getActivity());

        return view;
    }
}
