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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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

    private ProgressBar pBar;

    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pBar = (ProgressBar) findViewById(R.id.progressBar);

        if(isConnectedToInternet()) {
            new ListOfNationalities().execute();
        } else {
            Log.d("Status", "Not connected");
            Interactions.showError("Please check your internet connection.", SplashActivity.this);
            finish();
        }

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
        protected String doInBackground(String... info) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();
                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        NATIONALITIES_URL, "POST", params);

                Log.d("Fetching...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Successful!", json.toString());

                    JSONArray nationalities = json.getJSONArray("nationality");

                    Log.d("nationalities", nationalities.toString());

                    //save to singleton object ListNationality
                    for (int i = 0; i < nationalities.length(); i++) {
                        JSONObject jsonObject = nationalities.getJSONObject(i);
                        ListNationalities listNationalities = ListNationalities.getInstanceListNationalities();
                        listNationalities.nationalities.add(
                                new Nationality(jsonObject.getInt("id"),
                                        jsonObject.getString("nationality")));

                        Log.d(jsonObject.getInt("id") + "",
                                jsonObject.getString("nationality"));
                    }

                    return json.getString(TAG_RESPONSE);
                } else {
                    Log.d("Fetching failed!", json.getString(TAG_RESPONSE));
                    return json.getString(TAG_RESPONSE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String message) {

            try {
                pBar.setVisibility(View.GONE);
                if (message.equals("Successful")) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                    finish();
                } else {
                    if(isConnectedToInternet()) {
                        Interactions.showError("Please check your internet connection.", SplashActivity.this);
                    } else {
                        Interactions.showError("Something went wrong. Sorry.", SplashActivity.this);
                    }
                    finish();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    } // end of ListNationalities Thread

    //check internet connection
    public boolean isConnectedToInternet() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

    }

}

/*
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

                    /*for debugging
        ListNationalities listNationalities = ListNationalities.getInstanceListNationalities();
        for(Nationality list : listNationalities.nationalities) {
        Log.d(list.getId() + "", list.getNationality());
        }

        return json.getString(TAG_RESPONSE);
        } else {
        Log.d("Fetching failed!", json.getString(TAG_RESPONSE));
        return json.getString(TAG_RESPONSE);
        }


         String type = "select";
            String query = "SELECT%20*%20FROM%20nationalities";

            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(Network.forFinalDeployment + "?type=" + type + "&get_query=" + query);

            try {
                HttpResponse response = client.execute(httpGet);
                //response.
                Log.d("Response", response.toString());

                //JSONObject json = jsonParser.getJSONFromUrl(Network.forFinalDeployment + "?type=" + type + "&query=" + query);
                //Log.d("Json", json.toString());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("type", "select"));
                params.add(new BasicNameValuePair("get_query", "SELECT * FROM nationalities"));

                JSONObject json = jsonParser.makeHttpRequest(
                        Network.forDeploymentIp + "/transactions.php", "POST", params);

*/