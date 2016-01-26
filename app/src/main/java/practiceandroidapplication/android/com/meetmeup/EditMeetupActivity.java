package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.Entity.Meetups;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Preference;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class EditMeetupActivity extends AppCompatActivity {

    private static final String UPDATE_MEETUPS_URL = Network.forDeploymentIp + "meetups_update.php";
    private static final String RETRIEVE_MEETUPS_URL = Network.forDeploymentIp + "meetups_retrieve.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;

    EditText txtSubjects, txtDetails, txtLocation, txtStartAge,
            txtEndAge;

    Spinner spnGender;

    Button btnUpdate;

    ScrollView scrollView;

    Sessions sessions = Sessions.getSessionsInstance();
    //List<Meetups> currentMeetups = new ArrayList<>();
    User currentUser = Sessions.getSessionsInstance().currentUser;

    Meetups meetups;
    Meetups updateMeetups;

    String meetupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meetup);

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditMeetupActivity.this, MeetupsActivity.class));
                finish();
            }
        });

        initUI();
        meetupId = getIntent().getStringExtra("MEETUPS_ID");
        new RetrieveMeetups().execute();
    }

    public void initUI() {
        txtSubjects = (EditText) findViewById(R.id.txt_subject);
        txtDetails = (EditText) findViewById(R.id.txt_details);
        txtLocation = (EditText) findViewById(R.id.txt_location);
        txtStartAge = (EditText) findViewById(R.id.txt_min_age);
        txtEndAge = (EditText) findViewById(R.id.txt_max_age);
        spnGender = (Spinner) findViewById(R.id.spn_gender);

        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.setVisibility(View.INVISIBLE);

        btnUpdate = (Button) findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (validateForm()) {

                    char gender = spnGender.getSelectedItem().toString().charAt(0);

                    try {

                        updateMeetups = new Meetups(txtSubjects.getText().toString(),
                                txtDetails.getText().toString(),
                                currentUser.getId(),
                                txtLocation.getText().toString(),
                                new Preference(Integer.parseInt(txtStartAge.getText().toString()),
                                        Integer.parseInt(txtEndAge.getText().toString()), gender));

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                    new UpdateMeetups().execute();
                }
            }
        });

        loadGender();
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

    public boolean validateForm() {
        boolean isReadyToSave = true;

        if (txtSubjects.getText().toString().equals("")) {
            txtSubjects.setError("Subject is required.");
            isReadyToSave = false;
        } else
            txtSubjects.setError(null);

        if (txtDetails.getText().toString().equals("")) {
            txtDetails.setError("Details is required.");
            isReadyToSave = false;
        } else
            txtDetails.setError(null);

        if (txtLocation.getText().toString().equals("")) {
            txtLocation.setError("Location is required.");
            isReadyToSave = false;
        } else
            txtLocation.setError(null);

        if (txtStartAge.getText().toString().equals("")) {
            txtStartAge.setError("Minimum age is required.");
            isReadyToSave = false;
        } else
            txtStartAge.setError(null);

        if (txtEndAge.getText().toString().equals("")) {
            txtEndAge.setError("Maximum age is required.");
            isReadyToSave = false;
        } else
            txtEndAge.setError(null);

        return isReadyToSave;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(EditMeetupActivity.this, MeetupsActivity.class));
        finish();
    }

    /*
        thread
     */

    class RetrieveMeetups extends AsyncTask<String, String, String> {

        String[] groups = new String[9999];
        ArrayAdapter<String> meetupAdapter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditMeetupActivity.this, R.style.progress);
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

                Log.d("USER_ID (user)", currentUser.getId() + "");

                params.add(new BasicNameValuePair("id", meetupId));
                params.add(new BasicNameValuePair("filter", "individual"));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        RETRIEVE_MEETUPS_URL, "POST", params);

                Log.d("Fetching...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

                    JSONArray jUserArray = json.getJSONArray("meetups");
                    JSONObject jUserObject;

                    //sessions.removeGroups();

                    jUserObject = jUserArray.getJSONObject(0);

                    meetups = new Meetups(jUserObject.getString("subject"),
                            jUserObject.getString("details"),jUserObject.getInt("posted_by"), jUserObject.getString("location"),
                            new Preference(Integer.parseInt(jUserObject.getString("pref_start_age")),
                                    Integer.parseInt(jUserObject.getString("pref_end_age")),
                                    jUserObject.getString("pref_gender").charAt(0)));

                    Log.d("ID:", jUserObject.getInt("id") + "");

                    return json.getString(TAG_RESPONSE);
                } else {
                    Log.d("Fetching failed...", json.getString(TAG_RESPONSE));
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

                    Toast.makeText(EditMeetupActivity.this, message + "!", Toast.LENGTH_SHORT).show();
                    //displayMeetups();
                    txtSubjects.setText(meetups.getSubject());
                    txtDetails.setText(meetups.getDetails());
                    txtLocation.setText(meetups.getLocation());

                    txtStartAge.setText(meetups.getPreference().getStartAge() +"");
                    txtEndAge.setText(meetups.getPreference().getEndAge() +"");

                    if(meetups.getPreference().getGender() == 'M')
                        spnGender.setSelection(0);
                    else if (meetups.getPreference().getGender() == 'F')
                        spnGender.setSelection(1);
                    else
                        spnGender.setSelection(2);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    class UpdateMeetups extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditMeetupActivity.this, R.style.progress);
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... meetupInfo) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                Log.d("USER_ID (user)", currentUser.getId() + "");

                params.add(new BasicNameValuePair("subject", updateMeetups.getSubject()));
                params.add(new BasicNameValuePair("details", updateMeetups.getDetails()));
                params.add(new BasicNameValuePair("location", updateMeetups.getLocation()));
                params.add(new BasicNameValuePair("start_age", updateMeetups.getPreference().getStartAge() + ""));
                params.add(new BasicNameValuePair("end_age", updateMeetups.getPreference().getEndAge() + ""));
                params.add(new BasicNameValuePair("gender", updateMeetups.getPreference().getGender() + ""));

                params.add(new BasicNameValuePair("id", meetupId));

                params.add(new BasicNameValuePair("query_type", "update"));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        UPDATE_MEETUPS_URL, "POST", params);

                Log.d("Saving...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

                    return json.getString(TAG_RESPONSE);
                } else {
                    Log.d("Fetching failed...", json.getString(TAG_RESPONSE));
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
                    Toast.makeText(EditMeetupActivity.this, message + "!", Toast.LENGTH_SHORT).show();
                    /*btnUpdate.setEnabled(false);
                    new Thread() {
                        public void run() {
                            try {
                                sleep(500);
                                startActivity(new Intent(EditMeetupActivity.this, MeetupsActivity.class));
                                finish();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }.start();*/
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


}
