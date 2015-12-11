package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.Entity.ListNationalities;
import practiceandroidapplication.android.com.meetmeup.Entity.Nationality;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.Interactions;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class RegistrationActivity extends AppCompatActivity {


    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    private static final String REGISTER_URL = Network.forDeploymentIp + "user_save.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    EditText txtFirstname, txtLastname,
            txtCurrentLocation, txtEmailAddress, txtContactNumber,
            txtUsername, txtPassword, txtRepeatPassword;

    DatePicker dateBirth;
    RadioGroup rdGender;
    RadioButton rdMale, rdFemale;

    Toolbar toolBar;

    Spinner spnNationality;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initUI();
        initBtnEvents();
        loadNationalities();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.registration_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_back){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void initBtnEvents() {
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
                        user.setCurrentLocation(txtCurrentLocation.getText().toString());
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
                                user.getNationality().getId() + "", user.getGender() + "", user.getCurrentLocation(),
                                user.getEmailAddress(), user.getContactNumber(), "mark.png");
                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }); // end of register onclick event

    }


    class RegisterUser extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegistrationActivity.this, R.style.progress);
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
                if (message.equals("Username or Password is incorrect.")) {
                    Interactions.showError(message, RegistrationActivity.this);
                } else if (message.equals("Successful")) {
                    Interactions.showError(message, RegistrationActivity.this);
                    finish();
                    /*startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                    RegistrationActivity.this.finish();*/
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    } // end of thread create user

    public void initUI() {
        txtFirstname = (EditText) findViewById(R.id.txt_firstname);
        txtLastname = (EditText) findViewById(R.id.txt_lastname);
        txtCurrentLocation = (EditText) findViewById(R.id.txt_current_location);
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

        //show some toolbar button
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        //toolBar.setLogoDescription(getResources().getString(R.string.logo_desc));
        //toolBar.setNavigationIcon(R.drawable.ic_arrow_back_black);
    }

    public void loadNationalities() {
        ListNationalities listNationalities = ListNationalities.getInstanceListNationalities();
        List<String> list = new ArrayList<>();

        for (Nationality natiolity : listNationalities.nationalities) {
            Log.d(natiolity.getId() + "", natiolity.getNationality());
            list.add(natiolity.getNationality());
        }

        ArrayAdapter<String> adapter;

        adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner_layout, list);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spnNationality.setAdapter(adapter);
    }

    public boolean isFieldsEmpty() {
        return (txtFirstname.getText().toString().equals("") ||
                txtLastname.getText().toString().equals("") ||
                txtCurrentLocation.getText().toString().equals("") ||
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

        if(txtCurrentLocation.getText().toString().equals("")) {
            txtCurrentLocation.setError("Current location is required.");
            isReadyToSave = false;
        } else
            txtCurrentLocation.setError(null);

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

            if(username.length() <= 6) {
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

            if (password.length() <= 6) {
                txtPassword.setError("Password must be 6 characters length."); //need to define grammar.
                isReadyToSave = false;
            }
        }

        String repeatPassword = txtPassword.getText().toString();

        if(repeatPassword.equals("")) {
            txtRepeatPassword.setError("Password is required.");
            isReadyToSave = false;
        } else {
            txtRepeatPassword.setError(null);

            if(repeatPassword.length() <= 6) {
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

    public boolean isValidEmail(String emailAddress) {
        return false;
    }

    public boolean isValidUsername(String username) {
        return username.length() > 6;
    }

    public boolean isValidPassword(String password) {
        return password.length() > 6;
    }



    public void onBackPressed() {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
        finish();
    }

} // end of class