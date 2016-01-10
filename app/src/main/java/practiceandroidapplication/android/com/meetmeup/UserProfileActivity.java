package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.Entity.Nationality;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class UserProfileActivity extends AppCompatActivity {

    private static final String RETRIEVE_USER_URL = Network.forDeploymentIp + "user_retrieve.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;

    TextView lblFullName,lblGender, lblNationality,
            lblLocation, lblMobile, lblEmailAdd, lblBirthdate;

    LinearLayout linearProfile;

    Sessions sessions = Sessions.getSessionsInstance();
    User currentUser = Sessions.getSessionsInstance().currentUser;
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initUI();
        try {
            linearProfile.setVisibility(View.INVISIBLE);
            new RetrieveUser().execute();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_edit){
            startActivity(new Intent(UserProfileActivity.this, UserProfileUpdateActivity.class));
            finish();
        } else if (id == R.id.action_preferrence ){
            startActivity(new Intent(UserProfileActivity.this, SetPreferenceActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /*
        functions
     */

    public void initUI(){

        lblBirthdate = (TextView) findViewById(R.id.lbl_age);
        lblFullName = (TextView) findViewById(R.id.lbl_user_fullname);
        lblGender = (TextView) findViewById(R.id.lbl_gender);
        lblNationality = (TextView) findViewById(R.id.lbl_nationality);
        lblLocation = (TextView) findViewById(R.id.lbl_address);
        lblMobile = (TextView) findViewById(R.id.lbl_mobile);
        lblEmailAdd = (TextView) findViewById(R.id.lbl_email);

        linearProfile = (LinearLayout) findViewById(R.id.linear_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });

    }


    /*
        thread
     */

    class RetrieveUser extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserProfileActivity.this, R.style.progress);
            pDialog.setCancelable(false);
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

                Sessions sessions = Sessions.getSessionsInstance();

                params.add(new BasicNameValuePair("user_info", "current_user"));
                Log.d("USER_ID (userProfile)", sessions.currentUser.getId() + "");
                params.add(new BasicNameValuePair("user_id", sessions.currentUser.getId() + ""));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        RETRIEVE_USER_URL, "POST", params);

                Log.d("Fetching...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

                    JSONArray jUserArray = json.getJSONArray("user");
                    JSONObject jUserObject = jUserArray.getJSONObject(0);

                    currentUser.setId(jUserObject.getInt("id"));

                    Log.d("Fullname (user)", jUserObject.getString("first_name")
                            + " " + jUserObject.getString("last_name"));

                    currentUser.setFirstName(jUserObject.getString("first_name"));
                    currentUser.setLastName(jUserObject.getString("last_name"));
                    currentUser.setBirthDate(jUserObject.getString("bdate"));
                    currentUser.setNationality(new Nationality(jUserObject.getInt("natio_id")));
                    currentUser.setGender(jUserObject.getString("gender").charAt(0));
                    currentUser.setCurrentLocation(jUserObject.getString("current_location"));
                    currentUser.setEmailAddress(jUserObject.getString("email_address"));
                    currentUser.setContactNumber(jUserObject.getString("contact_number"));
                    currentUser.setPrivacyFlag(jUserObject.getString("active_flag").charAt(0));

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
                if(message.equals("Successful")) {

                    linearProfile.setVisibility(View.VISIBLE);

                    lblFullName.setText(currentUser.getFirstName() + " "
                            + currentUser.getLastName());
                    lblBirthdate.setText(currentUser.getBirthDate() + "");

                    String gender = currentUser.getGender() == 'M' ? "Male" : "Female";

                    lblGender.setText(gender);
                    lblNationality.setText(currentUser.getNationality().getNatioNalityName());
                    lblLocation.setText(currentUser.getCurrentLocation());
                    lblMobile.setText(currentUser.getContactNumber());
                    lblEmailAdd.setText(currentUser.getEmailAddress());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    } // end of retrieve create user

    public void onBackPressed() {
        finish();
    }

}
