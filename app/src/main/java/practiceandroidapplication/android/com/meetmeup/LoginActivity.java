package practiceandroidapplication.android.com.meetmeup;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import practiceandroidapplication.android.com.meetmeup.Entity.User;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

    TextView txtUsername;
    TextView txtPassword;

    Button btnLogin;

    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    private static final String LOGIN_URL = "http://192.168.1.201/meetmeup/user_login.php";

    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    private User user;

    private String username;
    private String password;


    //flag
    private boolean isReadyToSave = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUsername = (TextView) findViewById(R.id.txt_username);
        txtPassword = (TextView) findViewById(R.id.txt_password);
        btnLogin = (Button) findViewById(R.id.btn_login);


        initBtnEvents();
    }

    public void initBtnEvents(){
        btnLogin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (txtUsername.getText().toString().length() != 0 && txtPassword.getText().toString().length() != 0) {
                    //User user = new User(txtUsername.getText().toString(),
                                        //txtPassword.getText().toString());
                    try{
                        //user.setUsername(txtUsername.getText().toString());
                        //user.setPassword(txtPassword.getText().toString());
                        new User(txtUsername.getText().toString(),
                                txtPassword.getText().toString());

                        //username = txtUsername.getText().toString();
                        //password = txtPassword.getText().toString();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    new LoginUser().execute();

                } else {
                    txtUsername.setError("Fill up this field.");
                    txtPassword.setError("Fill up this field.");
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
                params.add(new BasicNameValuePair("username", "naidy"));
                params.add(new BasicNameValuePair("password", "naidy"));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", params);

                Log.d("Login attempt", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("User found!", json.toString());

                    //Intent intent = new Intent(this, NewsfeedActivity.class);
                    Intent intent = new Intent("android.intent.action.NEWSFEED");
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
