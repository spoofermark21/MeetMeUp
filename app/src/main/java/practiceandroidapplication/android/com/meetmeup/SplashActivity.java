package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.Entity.ListNationalities;
import practiceandroidapplication.android.com.meetmeup.Entity.Nationality;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Handles.Interactions;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class SplashActivity extends AppCompatActivity {

    private static final String NATIONALITIES_URL = Network.forDeploymentIp + "nationality_retrieve.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();

    private ProgressDialog pDialog;

    private ProgressBar pBar;

    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pBar = (ProgressBar) findViewById(R.id.progressBar);

        new ListOfNationalities().execute();
        /*if (isConnectedToInternet()) {
            new ListOfNationalities().execute();
        } else {
            Interactions.showError("Not connected to internet. Closing...", SplashActivity.this);
            finish();
        }*/

        /*image = (ImageView) findViewById(R.id.image);
        image.setImageResource(R.drawable.scratchers);
        image.postDelayed(swapImage, 3000);*/
    }

    Runnable swapImage = new Runnable() {
        @Override
        public void run() {
            image.setImageResource(R.drawable.meetmeup);
        }
    };

    class ListOfNationalities extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... user) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        NATIONALITIES_URL, "POST", params);

                Log.d("Fetching...", json.toString());

                success = json.getInt(TAG_STATUS);


                if (success == 1) {
                    Log.d("Successful!", json.toString());

                    JSONArray jNatio = json.getJSONArray("nationality");

                    Log.d("Natio info", jNatio.toString());

                    //save to singleton object ListNationality
                    for (int i = 0; i < jNatio.length(); i++) {
                        JSONObject jsonObject = jNatio.getJSONObject(i);
                        ListNationalities listNationalities = ListNationalities.getInstanceListNationalities();
                        listNationalities.nationalities.add(
                                new Nationality(jsonObject.getInt("id"),
                                jsonObject.getString("nationality")));

                        Log.d(jsonObject.getInt("id") + "",
                                jsonObject.getString("nationality"));
                    }

                    /*//for debugging
                    ListNationalities listNationalities = ListNationalities.getInstanceListNationalities();
                    for(Nationality list : listNationalities.nationalities) {
                        Log.d(list.getId() + "", list.getNationality());
                    }*/

                    return json.getString(TAG_RESPONSE);
                } else {
                    Log.d("Fetching failed!", json.getString(TAG_RESPONSE));
                    return json.getString(TAG_RESPONSE);
                }
            } catch (Exception e) {
                //Interactions.showError(e.toString(), SplashActivity.this);
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String message) {

            try {
                if (message.equals("Successful")) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                    finish();
                    /*
                    new Thread() {
                        public void run() {
                            try {
                                sleep(5000);
                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                                pBar.setVisibility(View.GONE);
                                finish();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                finish();
                            }
                        }
                    }.start();
                    */
                } else {
                    pBar.setVisibility(View.GONE);
                    Interactions.showError("Something went wrong!", SplashActivity.this);
                    finish();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    } // end of ListNationalities Thread

    public boolean isConnectedToInternet() {

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

    }

}
