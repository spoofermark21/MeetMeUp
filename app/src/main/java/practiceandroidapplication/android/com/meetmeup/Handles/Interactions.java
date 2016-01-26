package practiceandroidapplication.android.com.meetmeup.Handles;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by sibimark on 30/11/2015.
 */
public class Interactions {

    public static boolean isOk = false;

    public static void showError(String error, final Activity activity) {

        try {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(activity);
            dlgAlert.setMessage(error);
            dlgAlert.setTitle(null);
            dlgAlert.setCancelable(true);
            dlgAlert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            isOk = true;
                        }
                    });

            dlgAlert.create().show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean showQuestion(String message, final Activity activity) {

        try {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(activity);
            dlgAlert.setMessage("Are you sure to disable this event?");
            dlgAlert.setTitle("Warning!");
            dlgAlert.setCancelable(true);
            dlgAlert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            isOk = true;
                            Toast.makeText(activity, isOk + "!", Toast.LENGTH_SHORT).show();
                        }
                    });

            dlgAlert.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(EditEventsActivity.this, "False" + "!", Toast.LENGTH_SHORT).show();
                            isOk = false;
                        }
                    });

            dlgAlert.create().show();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isOk;
    }

    public static String generateString(Random rng, String characters, int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }


}
