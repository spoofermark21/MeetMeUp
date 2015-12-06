package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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

    private final int SELECT_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initUI();
        initBtnEvents();

    }

    EditText txtFirstname, txtLastname,
            txtCurrentLocation, txtEmailAddress, txtContactNumber,
            txtUsername, txtPassword, txtRepeatPassword;

    DatePicker dateBirth;
    RadioGroup rdGender;
    RadioButton rdMale, rdFemale;

    Button btnRegister, btnUpload;

    public void initBtnEvents() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
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

                    int gender = rdGender.getCheckedRadioButtonId();
                    final RadioButton selectedGender = (RadioButton) findViewById(gender);

                    user.setGender(selectedGender.getText().toString().charAt(0));

                    //for testing
                    Log.d("Gender", user.getGender() + "");

                    user.setBirthDate(dateBirth.getYear() + "-" +
                            dateBirth.getMonth() + "-" + dateBirth.getDayOfMonth());

                    txtRepeatPassword.setError(null);


                    if (!isFieldsEmpty()) {
                        if (txtPassword.getText().toString()
                                .equals(txtRepeatPassword.getText().toString())) {

                            if (!rdFemale.isChecked() && !rdMale.isChecked()) {
                                Interactions.showError("Please select gender", RegistrationActivity.this);
                            } else {
                                if (txtPassword.getText().toString().length() > 6) {
                                    new RegisterUser().execute(user.getUsername(),
                                            user.getPassword(), user.getFirstName(),
                                            user.getLastName(), user.getBirthDate(),
                                            "19", user.getGender() + "", user.getCurrentLocation(),
                                            user.getEmailAddress(), user.getContactNumber(), "mark.png");
                                } else {
                                    txtPassword.setError("Password minimum length is 6");
                                }
                            }
                        } else {
                            txtRepeatPassword.setError("Password does not match");
                        }
                    } else {
                        Interactions.showError("Please fill up the form correctly.", RegistrationActivity.this);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }); // end of register onclick event

        btnUpload.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

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

                String gender = user[6] == "Female" ? "F" : "M";

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

                Log.d("Gender", gender + "" + gender.length());

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        REGISTER_URL, "POST", params);

                Log.d("Register...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("User found!", json.toString());


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
                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                    RegistrationActivity.this.finish();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    } // end of thread


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
        btnUpload = (Button) findViewById(R.id.btn_upload);
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

} // end of class
