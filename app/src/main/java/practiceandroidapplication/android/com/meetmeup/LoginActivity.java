package practiceandroidapplication.android.com.meetmeup;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

    // widgets for login

    private TextView txtUsername, txtPassword;
    private Button btnLogin, btnRegister;

    //private Toolbar toolbar;

    JSONParser jsonParser = new JSONParser();
    //private ProgressDialog pDialog;

    private static final String LOGIN_URL = Network.forTestingIp + "user_login.php";

    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    //flag
    private boolean isReadyToSave = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUsername = (TextView) findViewById(R.id.txt_username);
        txtPassword = (TextView) findViewById(R.id.txt_password);

        btnRegister = (Button) findViewById(R.id.btn_register);
        btnLogin = (Button) findViewById(R.id.btn_login);

        initBtnEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    public void initBtnEvents() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!txtUsername.getText().toString().equals("") && !txtPassword.getText().toString().equals("")) {

                    try {
                        User user = new User(txtUsername.getText().toString(), txtPassword.getText().toString());
                        new LoginUser().execute(user.getUsername(), user.getPassword());

                    } catch (NullPointerException ex) {
                        Log.d("Null pointer exception", "In object user.");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                } else {
                    
                    if (txtUsername.getText().toString().equals("")) {
                        txtUsername.setError("Username is required");
                    }
                    if (txtPassword.getText().toString().equals("")) {
                        txtPassword.setError("Password is required.");
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

    class LoginUser extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Login ongoing...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();*/
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", args[0]));
                params.add(new BasicNameValuePair("password", args[1]));

                //save user logs
                params.add(new BasicNameValuePair("type_login", "in"));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", params);

                Log.d("Login attempt", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("User found!", json.toString());

                    Intent intent = new Intent(LoginActivity.this, NewsfeedActivity.class);

                    ArrayList<String> userInfo = new ArrayList<>();

                    JSONArray jUser = json.getJSONArray("user");
                    Log.d("User info", jUser.toString());

                    for (int i = 0; i < jUser.length(); i++) {
                        userInfo.add(jUser.getString(i));
                    }

                    intent.putExtra("userId", userInfo.get(0));
                    intent.putExtra("userFullName", userInfo.get(1));

                    startActivity(intent);

                    return json.getString(TAG_RESPONSE);
                } else {
                    Log.d("Login Failure!", json.getString(TAG_RESPONSE));
                    return json.getString(TAG_RESPONSE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            //pDialog.dismiss();
        }

    }

}
