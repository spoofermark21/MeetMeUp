package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import practiceandroidapplication.android.com.meetmeup.Entity.ListLocations;
import practiceandroidapplication.android.com.meetmeup.Entity.ListNationalities;
import practiceandroidapplication.android.com.meetmeup.Entity.Location;
import practiceandroidapplication.android.com.meetmeup.Entity.Meetups;
import practiceandroidapplication.android.com.meetmeup.Entity.Nationality;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Preference;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.Interactions;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class EditMeetupActivity extends AppCompatActivity {

    private static final String UPDATE_MEETUPS_URL = Network.forDeploymentIp + "meetups_update.php";
    private static final String RETRIEVE_MEETUPS_URL = Network.forDeploymentIp + "meetups_retrieve.php";
    private static final String INSERT_PREF_URL = Network.forDeploymentIp + "pref_natio_save.php";
    private static final String INSERT_PREF_URL1 = Network.forDeploymentIp + "pref_natio_save1.php";
    private static final String DELETE_PREF_URL = Network.forDeploymentIp + "delete_pref_natio.php";

    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;

    EditText txtSubjects, txtDetails, txtLocation, txtStartAge,
            txtEndAge;

    Spinner spnGender, spnLocation;

    Button btnSetMapLocation, btnSetPreferredNationalities;

    ScrollView scrollView;

    Sessions sessions = Sessions.getSessionsInstance();

    User currentUser = Sessions.getSessionsInstance().currentUser;

    String location;

    List<Nationality> listOfNationalities = new ArrayList<>();

    Meetups meetups;
    Meetups updateMeetups;
    String randomKey = "";

    private boolean hasSetNationalities = false;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.only_save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
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

        return super.onOptionsItemSelected(item);
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

        spnLocation = (Spinner) findViewById(R.id.spn_location);

        btnSetMapLocation = (Button) findViewById(R.id.btn_set_map);
        btnSetMapLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                if (!txtLocation.getText().toString().trim().equals("")) {
                    Intent intent = new Intent(EditMeetupActivity.this, MapsActivity.class);
                    intent.putExtra("TYPE", "creation");

                    if (!spnLocation.getSelectedItem().toString().contains("Cebu")) {
                        intent.putExtra("LOCATION", txtLocation.getText().toString() + ", "
                                + spnLocation.getSelectedItem().toString() + ", Cebu");
                    } else {
                        intent.putExtra("LOCATION", txtLocation.getText().toString()
                                + ", " + spnLocation.getSelectedItem().toString());
                    }
                    startActivity(intent);
                    txtLocation.setError(null);
                } else {
                    txtLocation.setError("Please set a location first");
                }


            }
        });

        btnSetPreferredNationalities = (Button) findViewById(R.id.btn_set_preference);
        btnSetPreferredNationalities.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                checkNationalities();
            }
        });

        loadGender();
        loadLocations();
        spnLocation.setSelection(16);
    }

    public void checkNationalities() {
        final ArrayList selectedNationalities = new ArrayList();

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select preferred nationalities")
                .setMultiChoiceItems(ListNationalities.loadNationalitesSequence(), null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            selectedNationalities.add(indexSelected);
                        } else if (selectedNationalities.contains(indexSelected)) {
                            selectedNationalities.remove(Integer.valueOf(indexSelected));
                        }
                        //to be finished @ after school
                        //preferredNationalities = String.join(",",selectedNationalities);
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        hasSetNationalities = true;
                        for (int i = 0; i < selectedNationalities.size(); i++) {
                            listOfNationalities.add(
                                    new Nationality(Integer.parseInt(selectedNationalities.get(i).toString()) + 1));

                            Log.d("nationality", i + " " + Integer.parseInt(selectedNationalities.get(i).toString()) + 1);

                            location += "('" + Integer.parseInt(selectedNationalities.get(i).toString()) +"','M')";
                            Log.d("location", location);
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        selectedNationalities.clear();
                        hasSetNationalities = false;
                    }
                }).create();
        dialog.show();
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

    public void loadLocations() {
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner_layout, ListLocations.getInstanceListLocations().loadLocations());
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spnLocation.setAdapter(adapter);
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

                    String loc = jUserObject.getString("location");

                    for(Location location : ListLocations.getInstanceListLocations().locations) {
                       loc = loc.replaceAll(", " + location.getLocation(), "");
                    }

                    meetups = new Meetups(jUserObject.getString("subject"),
                            jUserObject.getString("details"), jUserObject.getInt("posted_by"), loc,
                            new Preference(Integer.parseInt(jUserObject.getString("pref_start_age")),
                                    Integer.parseInt(jUserObject.getString("pref_end_age")),
                                    jUserObject.getString("pref_gender").charAt(0)));

                    meetups.setKey("key");

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

                    txtStartAge.setText(meetups.getPreference().getStartAge() + "");
                    txtEndAge.setText(meetups.getPreference().getEndAge() + "");

                    if (meetups.getPreference().getGender() == 'M')
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

                params.add(new BasicNameValuePair("lattitude", Sessions.currentLocationLatitude + ""));
                params.add(new BasicNameValuePair("longtitude", Sessions.currentLocationLongtitude + ""));

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
                    new DeletePrefNatio("").execute();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class InsertPrefNationalities extends AsyncTask<String, String, String> {

        String natioId;

        public InsertPrefNationalities(String natioId) {
            this.natioId = natioId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditMeetupActivity.this, R.style.progress);
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... meetupInfo) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();

                Log.d("USER_ID (user)", currentUser.getId() + "");

                params.add(new BasicNameValuePair("meetup_id", meetupId + ""));
                params.add(new BasicNameValuePair("natio_id", this.natioId));
                params.add(new BasicNameValuePair("natio_type", "M"));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        INSERT_PREF_URL1, "POST", params);

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
                    Log.d("Pref", "Success");
                } else {
                    //Toast.makeText(getBaseContext(), "Unsuccessful", Toast.LENGTH_LONG).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class DeletePrefNatio extends AsyncTask<String, String, String> {

        String id;

        public DeletePrefNatio(String id) {
            this.id = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditMeetupActivity.this, R.style.progress);
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... meetupInfo) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();

                Log.d("USER_ID (user)", currentUser.getId() + "");

                params.add(new BasicNameValuePair("id", meetupId));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        DELETE_PREF_URL, "POST", params);

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
                    Log.d("Pref", "Success");

                    for(Nationality nationality : listOfNationalities) {
                        new InsertPrefNationalities(nationality.getId() + "").execute();
                    }

                    new Thread() {
                        public void run() {
                            try {
                                sleep(100);
                                startActivity(new Intent(EditMeetupActivity.this, MeetupsActivity.class));
                                finish();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }.start();
                } else {
                    //Toast.makeText(getBaseContext(), "Unsuccessful", Toast.LENGTH_LONG).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
