package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class SetPreferenceActivity extends AppCompatActivity {

    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    private static final String PREFERENCE_URL = Network.forDeploymentIp + "user_save.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";


    EditText txtMinAge, txtMaxAge, txtLocation;

    RadioGroup rdGender;
    RadioButton rdMale, rdFemale, rdBoth;

    Toolbar toolBar;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_preference);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initUI();
    }

    public void initUI(){
        txtMinAge = (EditText) findViewById(R.id.txtMinAge);
        txtMaxAge = (EditText) findViewById(R.id.txtMaxAge);
        txtLocation = (EditText) findViewById(R.id.txtLocation);
        rdGender = (RadioGroup) findViewById(R.id.rd_gender);
        rdMale = (RadioButton) findViewById(R.id.rd_male);
        rdFemale = (RadioButton) findViewById(R.id.rd_female);
        rdBoth = (RadioButton) findViewById(R.id.rd_both);
    }

    class PreferenceUser extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SetPreferenceActivity.this, R.style.progress);
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... user) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair("min_age", user[0]));
                params.add(new BasicNameValuePair("max_age", user[1]));
                params.add(new BasicNameValuePair("gender", user[2]));
                params.add(new BasicNameValuePair("location", user[3]));

                Log.d("Gender", user[6] + "" + user[6].length());

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        PREFERENCE_URL, "POST", params);

                Log.d("Saving...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());
                    return json.getString(TAG_RESPONSE);
                } else {
                    Log.d("Login failed!", json.getString(TAG_RESPONSE));
                    return json.getString(TAG_RESPONSE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

}
