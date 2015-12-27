package practiceandroidapplication.android.com.meetmeup.Handles;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import practiceandroidapplication.android.com.meetmeup.LoginActivity;
import practiceandroidapplication.android.com.meetmeup.R;

/**
 * Created by sibimark on 30/11/2015.
 */
public class Interactions {

    public static void showError(String error, final Activity activity) {

        try {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(activity);
            dlgAlert.setMessage(error);
            dlgAlert.setTitle(null);
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
            dlgAlert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
