package recharge.com.myrechargegallery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

/**
 * Created by Lenovo on 28/10/2017.
 */
public class ResourceElements {

    public static void showDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
                alertDialogBuilder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                //Toast.makeText(context, "You clicked yes button", Toast.LENGTH_LONG).show();
                            }
                        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public static void showDialogOk(Context context, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
        alertDialogBuilder.setTitle(title);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            alertDialogBuilder.setMessage(Html.fromHtml(message+"",Html.FROM_HTML_MODE_LEGACY));
        } else {
            alertDialogBuilder.setMessage(Html.fromHtml(message+""));
        }

        //alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(context, "You clicked yes button", Toast.LENGTH_LONG).show();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
