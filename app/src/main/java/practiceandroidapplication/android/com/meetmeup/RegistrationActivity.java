package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.Entity.ListLocations;
import practiceandroidapplication.android.com.meetmeup.Entity.ListNationalities;
import practiceandroidapplication.android.com.meetmeup.Entity.Location;
import practiceandroidapplication.android.com.meetmeup.Entity.Nationality;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.Interactions;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class RegistrationActivity extends AppCompatActivity {

    private static final String REGISTER_URL = Network.forDeploymentIp + "user_save.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;

    EditText txtFirstname, txtLastname,
            txtCurrentLocation, txtEmailAddress, txtContactNumber,
            txtUsername, txtPassword, txtRepeatPassword;

    DatePicker dateBirth;
    RadioGroup rdGender;
    RadioButton rdMale, rdFemale;


    Spinner spnNationality, spnLocation;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //show some toolbar button
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initUI();
        //load nationalities options
        loadNationalities();

        //load locations options
        loadLocations();
    }

    /*
        functions
     */

    public void initUI() {
        txtFirstname = (EditText) findViewById(R.id.txt_firstname);
        txtLastname = (EditText) findViewById(R.id.txt_lastname);
        //txtCurrentLocation = (EditText) findViewById(R.id.txt_current_location);
        txtEmailAddress = (EditText) findViewById(R.id.txt_email_address);
        txtContactNumber = (EditText) findViewById(R.id.txt_contact_number);
        txtUsername = (EditText) findViewById(R.id.txt_username);
        txtPassword = (EditText) findViewById(R.id.txt_password);
        txtRepeatPassword = (EditText) findViewById(R.id.txt_repeat_password);

        dateBirth = (DatePicker) findViewById(R.id.date_birth);

        rdGender = (RadioGroup) findViewById(R.id.rd_gender);
        rdMale = (RadioButton) findViewById(R.id.rd_male);
        rdFemale = (RadioButton) findViewById(R.id.rd_female);
        btnRegister = (Button) findViewById(R.id.btn_register);
        spnNationality = (Spinner) findViewById(R.id.spn_natio);
        spnLocation = (Spinner) findViewById(R.id.spn_location);

        btnRegister.setVisibility(View.GONE);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if(formValidation()) {

                        //for debugging purposes...
                        Log.d("Birthdate", dateBirth.getYear() + "-" +
                                dateBirth.getMonth() + "-" + dateBirth.getDayOfMonth());


                        User user = new User();
                        user.setFirstName(txtFirstname.getText().toString());
                        user.setLastName(txtLastname.getText().toString());
                        //Log.d("Location (register)", txtCurrentLocation.getText().toString());
                        //user.setCurrentLocation(txtCurrentLocation.getText().toString());
                        //user.setCurrentLocation(spnLocation.getSelectedItem().toString());
                        //user.setLocation(spnLocation.getSelectedItemPosition());

                        user.setEmailAddress(txtEmailAddress.getText().toString());
                        user.setContactNumber(txtContactNumber.getText().toString());
                        user.setUsername(txtUsername.getText().toString());
                        user.setPassword(txtPassword.getText().toString());
                        user.setNationality(new Nationality(spnNationality.getSelectedItemPosition() + 1,
                                spnNationality.getSelectedItem().toString()));

                        final RadioButton selectedGender = (RadioButton)
                                findViewById(rdGender.getCheckedRadioButtonId());

                        user.setGender(selectedGender.getText().toString().charAt(0));

                        //for testing
                        Log.d("Gender", user.getGender() + "");

                        user.setBirthDate(dateBirth.getYear() + "-" +
                                dateBirth.getMonth() + "-" + dateBirth.getDayOfMonth());

                        new RegisterUser().execute(user.getUsername(),
                                user.getPassword(), user.getFirstName(),
                                user.getLastName(), user.getBirthDate(),
                                user.getNationality().getId() + "", user.getGender() + "", user.getCurrentLocation() + "",
                                user.getEmailAddress(), user.getContactNumber(), "");
                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }); // end of register onclick event

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if(formValidation()) {

                        //for debugging purposes...
                        Log.d("Birthdate", dateBirth.getYear() + "-" +
                                dateBirth.getMonth() + "-" + dateBirth.getDayOfMonth());


                        User user = new User();
                        user.setFirstName(txtFirstname.getText().toString());
                        user.setLastName(txtLastname.getText().toString());
                        //Log.d("Location (register)", txtCurrentLocation.getText().toString());
                        //user.setCurrentLocation(txtCurrentLocation.getText().toString());
                        user.setLocation(new Location(spnLocation.getSelectedItemPosition() + 1,
                                spnLocation.getSelectedItem().toString()));

                        user.setCurrentLocation(spnLocation.getSelectedItem().toString());
                        user.setEmailAddress(txtEmailAddress.getText().toString());
                        user.setContactNumber(txtContactNumber.getText().toString());
                        user.setUsername(txtUsername.getText().toString());
                        user.setPassword(txtPassword.getText().toString());
                        user.setNationality(new Nationality(spnNationality.getSelectedItemPosition() + 1,
                                spnNationality.getSelectedItem().toString()));

                        final RadioButton selectedGender = (RadioButton)
                                findViewById(rdGender.getCheckedRadioButtonId());

                        user.setGender(selectedGender.getText().toString().charAt(0));

                        //for testing
                        Log.d("Gender", user.getGender() + "");

                        user.setBirthDate(dateBirth.getYear() + "-" +
                                dateBirth.getMonth() + "-" + dateBirth.getDayOfMonth());

                        new RegisterUser().execute(user.getUsername(),
                                user.getPassword(), user.getFirstName(),
                                user.getLastName(), user.getBirthDate(),
                                user.getNationality().getId() + "", user.getGender() + "", user.getLocation().getId() + "",
                                user.getEmailAddress(), user.getContactNumber(), "");
                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        });
    }

    public void loadNationalities(){
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner_layout, ListNationalities.getInstanceListNationalities()
                .loadNationalities());
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spnNationality.setAdapter(adapter);
    }

    public void loadLocations(){
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner_layout, ListLocations.getInstanceListLocations().loadLocations());
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spnLocation.setAdapter(adapter);
    }





    public boolean isFieldsEmpty() {
        return (txtFirstname.getText().toString().equals("") ||
                txtLastname.getText().toString().equals("") ||
                //txtCurrentLocation.getText().toString().equals("") ||
                txtEmailAddress.getText().toString().equals("") ||
                txtContactNumber.getText().toString().equals("") ||
                txtUsername.getText().toString().equals("") ||
                txtPassword.getText().toString().equals("") ||
                txtRepeatPassword.getText().toString().equals(""));
    }

    public boolean formValidation() {

        boolean isReadyToSave = true;

        if(txtFirstname.getText().toString().equals("")) {
            txtFirstname.setError("First name is required.");
            isReadyToSave = false;
        } else
            txtFirstname.setError(null);

        if(txtLastname.getText().toString().equals("")) {
            txtLastname.setError("Last name is required.");
            isReadyToSave = false;
        }
        else
            txtLastname.setError(null);

        /*if(txtCurrentLocation.getText().toString().equals("")) {
            txtCurrentLocation.setError("Current location is required.");
            isReadyToSave = false;
        } else
            txtCurrentLocation.setError(null);*/

        String emailAddress = txtEmailAddress.getText().toString();

        if (emailAddress.equals("")) {
            txtEmailAddress.setError("Email address is required.");
            isReadyToSave = false;
        }else {
            txtEmailAddress.setError(null);

            if ((!emailAddress.contains("@") || !emailAddress.contains(".com")) ||
                    emailAddress.contains("@.com") || emailAddress.indexOf("@") == 0 ||
                    emailAddress.indexOf(".com") == 0) {
                txtEmailAddress.setError("Not a valid email address");
                isReadyToSave = false;
            }
        }

        if(txtContactNumber.getText().toString().equals("")) {
            txtContactNumber.setError("Contact number is required.");
            isReadyToSave = false;
        } else
            txtContactNumber.setError(null);

        String username = txtUsername.getText().toString();

        if(username.equals("")) {
            txtUsername.setError("Username is required.");
            isReadyToSave = false;
        } else {
            txtUsername.setError(null);

            if(username.length() < 6) {
                txtUsername.setError("Username must be 6 characters length."); //need to define grammar.
                isReadyToSave = false;
            }
        }

        String password = txtPassword.getText().toString();

        if(password.equals("")) {
            txtPassword.setError("Password is required.");
            isReadyToSave = false;
        } else {
            txtPassword.setError(null);

            if (password.length() < 6) {
                txtPassword.setError("Password must be 6 characters length."); //need to define grammar.
                isReadyToSave = false;
            }
        }

        String repeatPassword = txtRepeatPassword.getText().toString();

        if(repeatPassword.equals("")) {
            txtRepeatPassword.setError("Password is required.");
            isReadyToSave = false;
        } else {
            txtRepeatPassword.setError(null);

            if(repeatPassword.length() < 6) {
                txtRepeatPassword.setError("Password must be 6 characters length."); //need to define grammar.
                isReadyToSave = false;
            }
        }

        if (!password.equals(repeatPassword)) {
            txtRepeatPassword.setError("Password does not match.");
            isReadyToSave = false;
        } else
            txtRepeatPassword.setError(null);

        //final RadioButton selectedGender = (RadioButton) findViewById(rdGender.getCheckedRadioButtonId();

        if(isReadyToSave) {
            if (!rdMale.isChecked() && !rdFemale.isChecked()) {
                Interactions.showError("Please select gender.", RegistrationActivity.this);
                isReadyToSave = false;
            } else {
                isReadyToSave = true;
            }
        }

        /*
        if(isReadyToSave) {
            Date birthdate;
            //if()
        }*/


        //error trap birthdate must be 18 years old to register...

        return isReadyToSave;
    }

    public boolean isLegalAge () {
        return true;
    }

    @Override
    public void onBackPressed() {
        //startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
        finish();
    }

    /*
        thread
     */

    class RegisterUser extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegistrationActivity.this, R.style.progress);
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

                //String gender = user[6] == "Female" ? "F" : "M";

                params.add(new BasicNameValuePair("username", user[0]));
                params.add(new BasicNameValuePair("password", user[1]));
                params.add(new BasicNameValuePair("first_name", user[2]));
                params.add(new BasicNameValuePair("last_name", user[3]));
                params.add(new BasicNameValuePair("birth_date", user[4]));
                params.add(new BasicNameValuePair("natio_id", user[5]));
                params.add(new BasicNameValuePair("gender", user[6]));
                params.add(new BasicNameValuePair("current_location", user[7]));
                params.add(new BasicNameValuePair("email_address", user[8]));
                params.add(new BasicNameValuePair("contact_number", user[9]));
                params.add(new BasicNameValuePair("file_name", user[10]));

                Log.d("Gender", user[6] + "" + user[6].length());

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        REGISTER_URL, "POST", params);

                Log.d("Register...", json.toString());

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
                    Toast.makeText(RegistrationActivity.this, message + "!", Toast.LENGTH_SHORT).show();

                    new Thread(){
                        public void run() {
                            try {
                                sleep(1000);
                                finish();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }.start();
                    //Interactions.showError(message + "!", RegistrationActivity.this);
                    /*startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                    RegistrationActivity.this.finish();*/
                } else if(message.equals("Username is already taken. Please try again.")) {
                    Interactions.showError(message, RegistrationActivity.this);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    } // end of thread create user



} // end of class