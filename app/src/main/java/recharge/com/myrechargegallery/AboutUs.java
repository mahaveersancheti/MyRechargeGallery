package recharge.com.myrechargegallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AboutUs extends Fragment {

    View view;
    PrefManager prefManager;

    TextView tvSlogan, evEmail, tvWebsite, tvContact, tvAddr,tvVer, tvWa;
    ImageView ivLogo;
    String email, website, contact,Ver;

    public static AboutUs newInstance() {
        AboutUs fragment = new AboutUs();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.about_us, container, false);
        DrawerActivity.setTitle("About Us");

        prefManager = new PrefManager(getActivity());

        tvSlogan = (TextView) view.findViewById(R.id.tvSlogan);
        evEmail = (TextView) view.findViewById(R.id.evEmail);
        tvWebsite = (TextView) view.findViewById(R.id.tvWebsite);
        tvContact = (TextView) view.findViewById(R.id.tvContact);
        tvWa = (TextView) view.findViewById(R.id.tvContact1);
        tvAddr = (TextView) view.findViewById(R.id.tvAddr);
        ivLogo = (ImageView) view.findViewById(R.id.ivLogo);
        //tvVer =(TextView) view.findViewById(R.id.Ver);

        tvSlogan.setText("Ver No.: " + BuildConfig.VERSION_NAME);
        //+"\nVer Code: "+BuildConfig.VERSION_CODE

//        Glide.with(getActivity())
//                .load(mySharedPreference.getLogoUrl())
//                .into(ivLogo);

        email = evEmail.getText().toString().trim();
        website = tvWebsite.getText().toString().trim();
        contact = tvContact.getText().toString().trim();

        evEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", email, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                startActivity(Intent.createChooser(emailIntent, "Send email.."));
            }
        });
        tvContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact));
                startActivity(intent);
//                getPermission();
            }
        });
        tvWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(website.startsWith("http")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                    startActivity(browserIntent);
                } else {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+website));
                    startActivity(browserIntent);
                }
            }
        });

        tvWa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.setPackage("com.whatsapp");
                intent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
                try {
                    getActivity().startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "Whatsapp have not been installed.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    public void getPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.CALL_PHONE)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE},
                            1);
                } else {
                    // No explanation needed, we can request the permission.

                    requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE},
                            1);

                    // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE},
                        1);
            }
        } else {
            //get permission
            //contact = contact.trim().replaceAll(" ", "");
            //String mobile = contact.substring(contact.length() - 10);
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact));
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1) {
            if (grantResults[0] == 0) {
                //get permission
                //contact = contact.trim().replaceAll(" ", "");
                //String mobile = contact.substring(contact.length() - 10);
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact));
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "You have to allow permission.", Toast.LENGTH_LONG).show();
                //getActivity().finish();
            }
        }
    }
}
