package practiceandroidapplication.android.com.meetmeup;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import practiceandroidapplication.android.com.meetmeup.Entity.Nationality;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Preference;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.Interactions;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View.OnClickListener;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    // web service
    private static final String LOGIN_URL = Network.forDeploymentIp + "user_login.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    private JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    // widgets for login
    private EditText txtUsername, txtPassword;
    private Button btnLogin, btnRegister;

    private Sessions sessions = Sessions.getSessionsInstance();
    private User currentUser = sessions.currentUser;

    boolean isClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialize
        initUI();
        Log.d("Class simple name", LoginActivity.class.getSimpleName());

    }

    /*
        functions
     */

    public void initUI() {
        txtUsername = (EditText) findViewById(R.id.txt_username);
        txtPassword = (EditText) findViewById(R.id.txt_password);

        btnRegister = (Button) findViewById(R.id.btn_register);
        btnLogin = (Button) findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isFieldsEmpty()) {
                    try {
                        if (!txtUsername.getText().toString().contains(" ") &&
                                !txtPassword.getText().toString().contains(" ") &&
                                !isFieldsEmpty() && !isClick) {
                            isClick = true;
                            User user = new User(txtUsername.getText().toString(), txtPassword.getText().toString());
                            new LoginUser().execute(user.getUsername(), user.getPassword());

                        } else {
                            Interactions.showError("Username and password must not contain spaces", LoginActivity.this);
                        }
                    } catch (NullPointerException ex) {
                        Log.d("Null pointer exception", "In object user.");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        btnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }


    public boolean isFieldsEmpty() {
        if (txtUsername.getText().toString().equals(""))
            txtUsername.setError("Username is required");
        else
            txtUsername.setError(null);

        if (txtPassword.getText().toString().equals(""))
            txtPassword.setError("Password is required.");
        else
            txtPassword.setError(null);

        return txtUsername.getText().toString().equals("") ||
                txtPassword.getText().toString().equals("");
    }

    @Override
    public void onBackPressed() {
        //for animation
        //overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        //finish();
        moveTaskToBack(true);
    }

    /*
        thread
     */

    class LoginUser extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this, R.style.progress);
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... user) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();

                params.add(new BasicNameValuePair("username", user[0]));
                params.add(new BasicNameValuePair("password", user[1]));

                //save user logs
                params.add(new BasicNameValuePair("type_login", "in"));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", params);

                Log.d("Login...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("User found!", json.toString());

                    Intent intent = new Intent(LoginActivity.this, NewsfeedActivity.class);

                    JSONArray jUser = json.getJSONArray("user");
                    //JSONObject jUserObject = jUser.getJSONObject(0);

                    Log.d("User info", jUser.toString());

                    // retrieve user info
                    Log.d("USER ID (Login)", jUser.getString(0) + "");

                    currentUser.setId(Integer.parseInt(jUser.getString(0)));
                    currentUser.setFirstName(jUser.getString(1));
                    currentUser.setLastName(jUser.getString(2));
                    currentUser.setNationality(
                            new Nationality(Integer.parseInt(jUser.getString(3))));
                    currentUser.setUserImage(jUser.getString(4));

                    /*currentUser.setPreference(new Preference(jUser.getInt(10),
                            jUser.getInt(11), jUser.getString(12).charAt(0)));*/

                    startActivity(intent);
                    finish();

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
            if (message.equals("Username or Password is incorrect.")) {
                Interactions.showError("Incorrect username or password.", LoginActivity.this);
                isClick = false;
            }
        }

    } // end of thread



} // end of class