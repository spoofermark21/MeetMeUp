package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.Entity.ListNationalities;
import practiceandroidapplication.android.com.meetmeup.Entity.Meetups;
import practiceandroidapplication.android.com.meetmeup.Entity.Nationality;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Preference;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.Interactions;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class SetPreferenceActivity extends AppCompatActivity {

    private static final String PREFERENCE_URL = Network.forDeploymentIp + "user_preference_update.php";
    private static final String RETRIEVE_PREFERENCE_URL = Network.forDeploymentIp + "user_preference_retrieve.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    Spinner spnGender;

    Toolbar toolBar;
    Button btnSave;

    EditText txtMinAge, txtMaxAge, txtLocation;

    LinearLayout listOfNationalities;
    ScrollView scrollView;

    Preference preference = new Preference();
    User currentUser = Sessions.getSessionsInstance().currentUser;
    Preference currentPreference = Sessions.getSessionsInstance().currentPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_preference);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SetPreferenceActivity.this, UserProfileActivity.class));
                finish();
            }
        });

        initUI();
        btnEvents();
        new RetrievePreference().execute();

    }

    public void initUI() {

        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.setVisibility(View.INVISIBLE);

        txtMinAge = (EditText) findViewById(R.id.txt_min_age);
        txtMaxAge = (EditText) findViewById(R.id.txt_max_age);
        txtLocation = (EditText) findViewById(R.id.txt_location);
        spnGender = (Spinner) findViewById(R.id.spn_gender);
        loadGender();

        listOfNationalities = (LinearLayout) findViewById(R.id.linear_nationalities);
        //listOfNationalities.setVisibility(View.GONE);
        displayMeetups();

        btnSave = (Button) findViewById(R.id.btn_pref);
    }

    public void loadGender() {
        List<String> listGender = new ArrayList<>();

        listGender.add("Male");
        listGender.add("Female");
        listGender.add("Both");

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner_layout, listGender);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spnGender.setAdapter(adapter);
    }

    public void btnEvents() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (formValidation()) {
                    preference.setStartAge(Integer.parseInt(txtMinAge.getText().toString()));
                    preference.setEndAge(Integer.parseInt(txtMaxAge.getText().toString()));
                    preference.setGender(spnGender.getSelectedItem().toString().charAt(0));
                    preference.setLocation(txtLocation.getText().toString());

                    new PreferenceUser().execute(preference.getStartAge() + "", preference.getEndAge() + "",
                            preference.getGender() + "", preference.getLocation());
                }

            }
        });
    }

    public boolean formValidation() {

        boolean isReadyToSave = true;

        if (preference.getStartAge() > preference.getEndAge()) {
            Interactions.showError("Minimum age must not be greater than maximum.", SetPreferenceActivity.this);
            isReadyToSave = false;
        }

        if ((!txtMinAge.getText().toString().equals("") &&
                txtMaxAge.getText().toString().equals("")) ||
                !txtMinAge.getText().toString().equals("") &&
                        txtMaxAge.getText().toString().equals("")) {
            Interactions.showError("Minimum or Maximum must be specified.", SetPreferenceActivity.this);
            isReadyToSave = false;
        }

        return isReadyToSave;
    }

    public void displayMeetups() {
        for (Nationality nationality : ListNationalities.getInstanceListNationalities().nationalities) {

            LinearLayout recordOfNationalities = new LinearLayout(this);
            recordOfNationalities.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            recordOfNationalities.setOrientation(LinearLayout.VERTICAL);
            recordOfNationalities.setPadding(10, 10, 10, 10);

            Log.d("nationalities", nationality.getNatioNalityName());
            recordOfNationalities.setTag(nationality.getId());

            final CheckBox meetupCheckBox = new CheckBox(this);
            meetupCheckBox.setText(nationality.getNatioNalityName());
            meetupCheckBox.setTextSize(15);
            meetupCheckBox.setTextColor(Color.BLACK);

            recordOfNationalities.addView(meetupCheckBox);


            listOfNationalities.addView(recordOfNationalities);
        }
    }
    /*
        thread
     */

    class PreferenceUser extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SetPreferenceActivity.this, R.style.progress);
            pDialog.setCancelable(true);
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

                params.add(new BasicNameValuePair("id", currentUser.getId() + ""));
                params.add(new BasicNameValuePair("min_age", user[0]));
                params.add(new BasicNameValuePair("max_age", user[1]));
                params.add(new BasicNameValuePair("gender", user[2]));
                params.add(new BasicNameValuePair("location", user[3]));

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

        protected void onPostExecute(String message) {
            pDialog.dismiss();
            try {
                if (message.equals("Successful")) {
                    Interactions.showError(message, SetPreferenceActivity.this);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    } // end of set preference thread

    class RetrievePreference extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SetPreferenceActivity.this, R.style.progress);
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... userInfo) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                Log.d("USER_ID (userProfile)", currentUser.getId() + "");
                params.add(new BasicNameValuePair("user_id", currentUser.getId() + ""));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        RETRIEVE_PREFERENCE_URL, "POST", params);

                Log.d("Fetching...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

                    JSONArray jUserArray = json.getJSONArray("user");
                    JSONObject jUserObject = jUserArray.getJSONObject(0);

                    currentPreference.setStartAge(Integer.parseInt(jUserObject.getString("min_age")));
                    currentPreference.setEndAge(Integer.parseInt(jUserObject.getString("max_age")));
                    currentPreference.setGender(jUserObject.getString("gender").charAt(0));
                    currentPreference.setLocation(jUserObject.getString("pref_location"));

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


        protected void onPostExecute(String message) {
            pDialog.dismiss();
            try {
                if (message.equals("Successful")) {

                    scrollView.setVisibility(View.VISIBLE);

                    txtMinAge.setText(currentPreference.getStartAge() + "");
                    txtMaxAge.setText(currentPreference.getEndAge() + "");

                    if (currentPreference.getGender() == 'M')
                        spnGender.setSelection(0);
                    else if (currentPreference.getGender() == 'F')
                        spnGender.setSelection(1);
                    else
                        spnGender.setSelection(2);

                    txtLocation.setText(currentPreference.getLocation());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    } // end of thread create user

    public void onBackPressed() {
        startActivity(new Intent(SetPreferenceActivity.this, UserProfileActivity.class));
        finish();
    }
}
