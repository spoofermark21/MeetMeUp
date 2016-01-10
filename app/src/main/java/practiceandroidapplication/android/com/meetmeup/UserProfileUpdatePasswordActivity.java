package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.Interactions;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class UserProfileUpdatePasswordActivity extends AppCompatActivity {

    private static final String UPDATE_USER_URL = Network.forDeploymentIp + "user_update.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;

    EditText txtUsername, txtOldPassword, txtNewPassword, txtRepeatPassword;

    Button btnUpdate;

    User currentUser = Sessions.getSessionsInstance().currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_update_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfileUpdatePasswordActivity.this, UserProfileUpdateActivity.class));
                finish();
            }
        });

        initUI();

        txtUsername.setText("Username: " + currentUser.getUsername());
    }

    public void initUI(){

        txtUsername = (EditText) findViewById(R.id.txt_username);
        txtUsername.setEnabled(false);

        txtOldPassword = (EditText) findViewById(R.id.txt_old_password);
        txtNewPassword = (EditText) findViewById(R.id.txt_new_password);
        txtRepeatPassword = (EditText) findViewById(R.id.txt_repeat_password);

        btnUpdate = (Button) findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (formValidation()) {
                    new UpdateUser().execute(txtNewPassword.getText().toString());
                }
            }
        });
    }

    public boolean formValidation() {

        boolean isReadyToSave = true;


        String oldPassword = txtOldPassword.getText().toString();

        if (oldPassword.equals("")) {
            txtOldPassword.setError("Old password is required.");
            isReadyToSave = false;
        } else {
            txtOldPassword.setError(null);
            Log.d("Password (Update)", currentUser.getPassword());
            if (!txtOldPassword.getText().toString()
                    .equals(currentUser.getPassword())) {
                txtOldPassword.setError("Password is incorrect."); //need to define grammar.
                isReadyToSave = false;
            }
        }

        String password = txtNewPassword.getText().toString();

        if (password.equals("")) {
            txtNewPassword.setError("Password is required.");
            isReadyToSave = false;
        } else {
            txtNewPassword.setError(null);

            if (password.length() < 6) {
                txtNewPassword.setError("Password must be 6 characters length."); //need to define grammar.
                isReadyToSave = false;
            }
        }

        String repeatPassword = txtRepeatPassword.getText().toString();

        if (repeatPassword.equals("")) {
            txtRepeatPassword.setError("Password is required.");
            isReadyToSave = false;
        } else {
            txtRepeatPassword.setError(null);

            if (repeatPassword.length() < 6) {
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


        return isReadyToSave;
    }

    public void onBackPressed() {
        startActivity(new Intent(UserProfileUpdatePasswordActivity.this, UserProfileUpdateActivity.class));
        finish();
    }

    /*
        thread
     */

    class UpdateUser extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserProfileUpdatePasswordActivity.this, R.style.progress);
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
                params.add(new BasicNameValuePair("password", user[0]));
                params.add(new BasicNameValuePair("update_type", "password"));

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
                    Interactions.showError(message, UserProfileUpdatePasswordActivity.this);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    } // end of thread create user
}
