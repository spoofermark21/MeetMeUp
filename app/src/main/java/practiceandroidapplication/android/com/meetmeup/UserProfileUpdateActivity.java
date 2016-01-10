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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.Entity.ListNationalities;
import practiceandroidapplication.android.com.meetmeup.Entity.Nationality;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.Interactions;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class UserProfileUpdateActivity extends AppCompatActivity {


    private static final String RETRIEVE_USER_URL = Network.forDeploymentIp + "user_retrieve.php";
    private static final String UPDATE_USER_URL = Network.forDeploymentIp + "user_update.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;


    EditText txtFirstname, txtLastname,
            txtCurrentLocation, txtEmailAddress, txtContactNumber;

    DatePicker dateBirth;
    RadioGroup rdGender;
    RadioButton rdMale, rdFemale;

    ScrollView scrollView;

    Toolbar toolBar;

    Spinner spnNationality;
    Button btnUpdate;

    User fetchUser = new User();
    User updateUser = new User();
    User currentUser = Sessions.getSessionsInstance().currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_profile_update);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initUI();
        loadNationalities();

        try {
            new RetrieveUser().execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile_update_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_password) {
            startActivity(new Intent(UserProfileUpdateActivity.this, UserProfileUpdatePasswordActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    /*
        functions
     */

    public void loadNationalities() {
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner_layout, ListNationalities.getInstanceListNationalities()
                .loadNationalities());
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spnNationality.setAdapter(adapter);
    } // end of LoadNationalities

    public void initUI() {

        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.setVisibility(View.INVISIBLE);

        txtFirstname = (EditText) findViewById(R.id.txt_firstname);
        txtLastname = (EditText) findViewById(R.id.txt_lastname);
        txtCurrentLocation = (EditText) findViewById(R.id.txt_current_location);
        txtEmailAddress = (EditText) findViewById(R.id.txt_email_address);
        txtContactNumber = (EditText) findViewById(R.id.txt_contact_number);

        dateBirth = (DatePicker) findViewById(R.id.date_birth);

        rdGender = (RadioGroup) findViewById(R.id.rd_gender);
        rdMale = (RadioButton) findViewById(R.id.rd_male);
        rdFemale = (RadioButton) findViewById(R.id.rd_female);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        spnNationality = (Spinner) findViewById(R.id.spn_natio);

        //show some toolbar button
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfileUpdateActivity.this, UserProfileActivity.class));
                finish();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (formValidation()) {

                        //for debugging purposes...
                        Log.d("Birthdate", dateBirth.getYear() + "-" +
                                dateBirth.getMonth() + "-" + dateBirth.getDayOfMonth());

                        updateUser.setFirstName(txtFirstname.getText().toString());
                        updateUser.setLastName(txtLastname.getText().toString());
                        updateUser.setCurrentLocation(txtCurrentLocation.getText().toString());
                        updateUser.setEmailAddress(txtEmailAddress.getText().toString());
                        updateUser.setContactNumber(txtContactNumber.getText().toString());
                        updateUser.setNationality(new Nationality(spnNationality.getSelectedItemPosition() + 1,
                                spnNationality.getSelectedItem().toString()));

                        final RadioButton selectedGender = (RadioButton)
                                findViewById(rdGender.getCheckedRadioButtonId());

                        updateUser.setGender(selectedGender.getText().toString().charAt(0));

                        //for testing
                        Log.d("Gender", updateUser.getGender() + "");

                        updateUser.setBirthDate(dateBirth.getYear() + "-" +
                                dateBirth.getMonth() + "-" + dateBirth.getDayOfMonth());

                        new UpdateUser().execute(updateUser.getFirstName(),
                                updateUser.getLastName(), updateUser.getBirthDate(),
                                updateUser.getNationality().getId() + "", updateUser.getGender() + "", updateUser.getCurrentLocation(),
                                updateUser.getEmailAddress(), updateUser.getContactNumber(), "mark.png");
                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }); // end of update onclick event

    } // end of initUI


    public boolean formValidation() {

        boolean isReadyToSave = true;

        if (txtFirstname.getText().toString().equals("")) {
            txtFirstname.setError("First name is required.");
            isReadyToSave = false;
        } else
            txtFirstname.setError(null);

        if (txtLastname.getText().toString().equals("")) {
            txtLastname.setError("Last name is required.");
            isReadyToSave = false;
        } else
            txtLastname.setError(null);

        if (txtCurrentLocation.getText().toString().equals("")) {
            txtCurrentLocation.setError("Current location is required.");
            isReadyToSave = false;
        } else
            txtCurrentLocation.setError(null);

        String emailAddress = txtEmailAddress.getText().toString();

        if (emailAddress.equals("")) {
            txtEmailAddress.setError("Email address is required.");
            isReadyToSave = false;
        } else {
            txtEmailAddress.setError(null);

            if ((!emailAddress.contains("@") || !emailAddress.contains(".com")) ||
                    emailAddress.contains("@.com") || emailAddress.indexOf("@") == 0 ||
                    emailAddress.indexOf(".com") == 0) {
                txtEmailAddress.setError("Not a valid email address");
                isReadyToSave = false;
            }
        }

        if (txtContactNumber.getText().toString().equals("")) {
            txtContactNumber.setError("Contact number is required.");
            isReadyToSave = false;
        } else
            txtContactNumber.setError(null);

        if (isReadyToSave) {
            if (!rdMale.isChecked() && !rdFemale.isChecked()) {
                Interactions.showError("Please select gender.", UserProfileUpdateActivity.this);
                isReadyToSave = false;
            } else {
                isReadyToSave = true;
            }
        }

        //error trap birthdate must be 18 years old to register...

        return isReadyToSave;
    }

    /*
        thread
     */

    class RetrieveUser extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserProfileUpdateActivity.this, R.style.progress);
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

                params.add(new BasicNameValuePair("user_info", "current_user"));
                Log.d("USER_ID (user)", currentUser.getId() + "");
                params.add(new BasicNameValuePair("user_id", currentUser.getId() + ""));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        RETRIEVE_USER_URL, "POST", params);

                Log.d("Fetching...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

                    JSONArray jUserArray = json.getJSONArray("user");
                    JSONObject jUserObject = jUserArray.getJSONObject(0);

                    //fetchUser.setId(jUserObject.getInt("id"));
                    currentUser.setFirstName(jUserObject.getString("first_name"));
                    currentUser.setLastName(jUserObject.getString("last_name"));
                    currentUser.setBirthDate(jUserObject.getString("bdate"));
                    currentUser.setNationality(new Nationality(jUserObject.getInt("natio_id")));
                    currentUser.setGender(jUserObject.getString("gender").charAt(0));
                    currentUser.setCurrentLocation(jUserObject.getString("current_location"));
                    currentUser.setEmailAddress(jUserObject.getString("email_address"));
                    currentUser.setContactNumber(jUserObject.getString("contact_number"));

                    currentUser.setUsername(jUserObject.getString("username"));
                    currentUser.setPassword(jUserObject.getString("password"));

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

                    txtFirstname.setText(currentUser.getFirstName());
                    txtLastname.setText(currentUser.getLastName());
                    //dateBirth.init();
                    spnNationality.setSelection(currentUser.getNationality().getId() - 1);

                    char gender = currentUser.getGender();

                    if (gender == 'M')
                        rdMale.setChecked(true);
                    else
                        rdFemale.setChecked(true);

                    txtCurrentLocation.setText(currentUser.getCurrentLocation());
                    txtEmailAddress.setText(currentUser.getEmailAddress());
                    txtContactNumber.setText(currentUser.getContactNumber());

                    String Str = new String(currentUser.getBirthDate());
                    String date[] = Str.split("-", 3);

                    dateBirth.updateDate(Integer.parseInt(date[0]),
                            Integer.parseInt(date[1]), Integer.parseInt(date[2]));

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    } // end of thread retrieve user

    class UpdateUser extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserProfileUpdateActivity.this, R.style.progress);
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

                params.add(new BasicNameValuePair("id", currentUser.getId() + ""));
                params.add(new BasicNameValuePair("first_name", user[0]));
                params.add(new BasicNameValuePair("last_name", user[1]));
                params.add(new BasicNameValuePair("birth_date", user[2]));
                params.add(new BasicNameValuePair("natio_id", user[3]));
                params.add(new BasicNameValuePair("gender", user[4]));
                params.add(new BasicNameValuePair("current_location", user[5]));
                params.add(new BasicNameValuePair("email_address", user[6]));
                params.add(new BasicNameValuePair("contact_number", user[7]));

                params.add(new BasicNameValuePair("update_type", "profile"));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        UPDATE_USER_URL, "POST", params);

                Log.d("Updating...", json.toString());

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
                    Interactions.showError(message, UserProfileUpdateActivity.this);
                    //new RetrieveUser().execute();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    } // end of thread create user


    public void onBackPressed() {
        startActivity(new Intent(UserProfileUpdateActivity.this, UserProfileActivity.class));
        finish();
    }

}
