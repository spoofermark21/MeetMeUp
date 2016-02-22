package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import practiceandroidapplication.android.com.meetmeup.Entity.ListLocations;
import practiceandroidapplication.android.com.meetmeup.Entity.ListNationalities;
import practiceandroidapplication.android.com.meetmeup.Entity.Location;
import practiceandroidapplication.android.com.meetmeup.Entity.Nationality;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.Interactions;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;
import practiceandroidapplication.android.com.meetmeup.Helpers.ImageHelper;

public class UserProfileUpdateActivity extends AppCompatActivity {


    private static final String RETRIEVE_USER_URL = Network.forDeploymentIp + "user_retrieve.php";

    private static final String UPDATE_USER_URL = Network.forDeploymentIp + "user_update.php";
    private static final String DISABLE_USER_URL = Network.forDeploymentIp + "user_disable.php";
    private static final String PRIVACY_USER_URL = Network.forDeploymentIp + "user_privacy.php";

    private static final String UPLOAD_IMAGE_URL = Network.forDeploymentIp + "image_upload.php";

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

    Switch disableUser, privacyFlag;

    Toolbar toolBar;

    Spinner spnNationality, spnLocation;
    ImageView imgUser;
    Button btnUpdate, btnUpload;

    User fetchUser = new User();
    User updateUser = new User();
    User currentUser = Sessions.getSessionsInstance().currentUser;

    private static int RESULT_LOAD_IMG = 1;
    String encodedImage, fileName, imgDecodableString;

    boolean isNewImage = false;
    boolean hasSetNationalities = false;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_profile_update);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initUI();
        loadNationalities();
        loadLocations();

        Log.d("Current user id", currentUser.getId() + "");

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
        } else if(id == R.id.action_save) {
            save();
        }

        return super.onOptionsItemSelected(item);
    }


    /*
        functions
     */

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
                        //location = "("
                        for(int i=0; i < selectedNationalities.size(); i++) {
                            location += selectedNationalities.get(i).toString();
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
        //txtCurrentLocation = (EditText) findViewById(R.id.txt_current_location);

        spnLocation = (Spinner) findViewById(R.id.spn_location);

        txtEmailAddress = (EditText) findViewById(R.id.txt_email_address);
        txtContactNumber = (EditText) findViewById(R.id.txt_contact_number);

        dateBirth = (DatePicker) findViewById(R.id.date_birth);

        rdGender = (RadioGroup) findViewById(R.id.rd_gender);
        rdMale = (RadioButton) findViewById(R.id.rd_male);
        rdFemale = (RadioButton) findViewById(R.id.rd_female);
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

        imgUser = (ImageView) findViewById(R.id.img_user);
        imgUser.setBackgroundColor(Color.parseColor("#E6E9ED"));

        disableUser = (Switch) findViewById(R.id.disable_user);
        disableUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(UserProfileUpdateActivity.this);
                    dlgAlert.setMessage("Are you sure to disable your account permanently?");
                    dlgAlert.setTitle("Warning!");
                    dlgAlert.setCancelable(true);
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Toast.makeText(UserProfileUpdateActivity.this,"Disabled", Toast.LENGTH_SHORT).show();
                                    new DisableUser().execute();
                                }
                            });

                    dlgAlert.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    disableUser.setChecked(false);
                                }
                            });

                    dlgAlert.setOnCancelListener(new DialogInterface.OnCancelListener() {

                        public void onCancel(DialogInterface dialog) {
                            disableUser.setChecked(false);
                        }
                    });

                    dlgAlert.create().show();


                }
            }
        });



        privacyFlag = (Switch) findViewById(R.id.privacy_flag);
        privacyFlag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(UserProfileUpdateActivity.this);
                    dlgAlert.setMessage("The following will be kept private: Mobile number and Email add. Are you sure to proceed?");
                    dlgAlert.setTitle("Warning!");
                    dlgAlert.setCancelable(true);
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Toast.makeText(UserProfileUpdateActivity.this,"Disabled", Toast.LENGTH_SHORT).show();
                                    new PrivacyEnable("N").execute();
                                }
                            });

                    dlgAlert.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    privacyFlag.setChecked(false);
                                }
                            });

                    dlgAlert.setOnCancelListener(new DialogInterface.OnCancelListener() {

                        public void onCancel(DialogInterface dialog) {
                            privacyFlag.setChecked(false);
                        }
                    });
                    dlgAlert.create().show();
                } else if (isChecked){
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(UserProfileUpdateActivity.this);
                    dlgAlert.setMessage("The following will be visible: Mobile number and Email add. Are you sure to proceed?");
                    dlgAlert.setTitle("Warning!");
                    dlgAlert.setCancelable(true);
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Toast.makeText(UserProfileUpdateActivity.this,"Disabled", Toast.LENGTH_SHORT).show();
                                    new PrivacyEnable("Y").execute();
                                }
                            });

                    dlgAlert.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    privacyFlag.setChecked(true);
                                }
                            });

                    dlgAlert.setOnCancelListener(new DialogInterface.OnCancelListener() {

                        public void onCancel(DialogInterface dialog) {
                            privacyFlag.setChecked(true);
                        }
                    });


                    dlgAlert.setOnCancelListener(new DialogInterface.OnCancelListener() {

                        public void onCancel(DialogInterface dialog) {
                            privacyFlag.setChecked(false);
                        }
                    });
                    dlgAlert.create().show();
                }
            }
        });

        btnUpload = (Button) findViewById(R.id.btn_image);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });


    } // end of initUI



    protected void Quit() {
        super.finish();
    }

    public void save(){
        try {

            if (formValidation()) {

                //for debugging purposes...
                Log.d("Birthdate", dateBirth.getYear() + "-" +
                        dateBirth.getMonth() + "-" + dateBirth.getDayOfMonth());

                updateUser.setFirstName(txtFirstname.getText().toString());
                updateUser.setLastName(txtLastname.getText().toString());
                //updateUser.setCurrentLocation(txtCurrentLocation.getText().toString());
                //updateUser.setCurrentLocation(spnLocation.getSelectedItem().toString());
                updateUser.setLocation(new Location(spnLocation.getSelectedItemPosition() + 1));

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
                        (dateBirth.getMonth() + 1) + "-" + dateBirth.getDayOfMonth());

                new UpdateUser().execute(updateUser.getFirstName(),
                        updateUser.getLastName(), updateUser.getBirthDate(),
                        updateUser.getNationality().getId() + "", updateUser.getGender() + "", updateUser.getLocation().getId() + "",
                        updateUser.getEmailAddress(), updateUser.getContactNumber());
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadLocations(){
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner_layout, ListLocations.getInstanceListLocations().loadLocations());
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spnLocation.setAdapter(adapter);
    }

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

        /*if (txtCurrentLocation.getText().toString().equals("")) {
            txtCurrentLocation.setError("Current location is required.");
            isReadyToSave = false;
        } else
            txtCurrentLocation.setError(null);*/

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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                // Set the Image in ImageView after decoding the String
                imgUser.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

                // set flag to true is new image
                isNewImage = true;

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    public void onBackPressed() {
        startActivity(new Intent(UserProfileUpdateActivity.this, UserProfileActivity.class));
        finish();
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
                List<NameValuePair> params = new ArrayList<>();

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
                    //currentUser.setCurrentLocation(jUserObject.getString("current_location"));

                    currentUser.setLocation(new Location(jUserObject.getInt("current_location")));

                    currentUser.setEmailAddress(jUserObject.getString("email_address"));
                    currentUser.setContactNumber(jUserObject.getString("contact_number"));
                    currentUser.setUserImage(jUserObject.getString("user_image"));

                    currentUser.setPrivacyFlag(jUserObject.getString("privacy_flag").charAt(0));

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

                    //txtCurrentLocation.setText(currentUser.getCurrentLocation());
                    spnLocation.setSelection(currentUser.getLocation().getId() - 1);

                    txtEmailAddress.setText(currentUser.getEmailAddress());
                    txtContactNumber.setText(currentUser.getContactNumber());

                    String Str = new String(currentUser.getBirthDate());
                    String date[] = Str.split("-", 3);

                    dateBirth.updateDate(Integer.parseInt(date[0]),
                            Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]));

                    //.showError(currentUser.getPrivacyFlag() + "", UserProfileUpdateActivity.this);

                    if(currentUser.getPrivacyFlag() == 'Y') {
                        privacyFlag.setChecked(false);
                    } else {
                        privacyFlag.setChecked(true);
                    }

                    try {
                        if(!currentUser.getUserImage().equals("null") && !currentUser.getUserImage().equals("")) {
                            new DownloadUserImage(currentUser.getUserImage() + ".JPG").execute();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


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


                if(isNewImage) {
                    fileName = Interactions.generateString(new Random(),"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", 10);
                    params.add(new BasicNameValuePair("user_image", fileName));
                }

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
                    //Interactions.showError(message, UserProfileUpdateActivity.this);
                    //new RetrieveUser().execute();

                    //set image then upload to server
                    if(isNewImage) {
                        Bitmap image = ((BitmapDrawable) imgUser.getDrawable()).getBitmap();
                        new UploadUserImage(image, fileName,"users").execute();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }


    class DisableUser extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserProfileUpdateActivity.this, R.style.progress);
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
                List<NameValuePair> params = new ArrayList<>();

                //params.add(new BasicNameValuePair("id", currentUser.getId() + ""));
                params.add(new BasicNameValuePair("id", currentUser.getId() + ""));
                Log.d("User id", currentUser.getId() + "");

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        DISABLE_USER_URL, "POST", params);

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
                    // do something
                    //Toast.makeText(UserProfileUpdateActivity.this, "Successful!", Toast.LENGTH_LONG).show();
                    //System.exit(0);
                    //int p = android.os.Process.myPid();
                    //android.os.Process.killProcess(p);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    class PrivacyEnable extends AsyncTask<String, String, String> {

        String type;

        public PrivacyEnable(String type) {
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserProfileUpdateActivity.this, R.style.progress);
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
                List<NameValuePair> params = new ArrayList<>();

                params.add(new BasicNameValuePair("id", currentUser.getId() + ""));
                params.add(new BasicNameValuePair("type", type + ""));
                Log.d("User id", currentUser.getId() + "");

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        PRIVACY_USER_URL, "POST", params);

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
                    // do something
                    Toast.makeText(UserProfileUpdateActivity.this, "Successful!", Toast.LENGTH_LONG).show();
                    //System.exit(0);
                    //currentUser.setPrivacyFlag('N');
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    private class UploadUserImage extends AsyncTask<String, String, String> {

        Bitmap image;
        String filename;
        String directory;

        public UploadUserImage(Bitmap image, String filename, String directory) {
            this.image = image;
            this.filename = filename;
            this.directory = directory;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserProfileUpdateActivity.this, R.style.progress);
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            pDialog.setMessage("Uploading image...");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... string) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

                encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                params.add(new BasicNameValuePair("image", encodedImage));
                params.add(new BasicNameValuePair("name", filename));
                params.add(new BasicNameValuePair("directory", directory));

                //HttpParams httpParams = getHttpRequestParams();
                //HttpClient

                Log.d("Image...", encodedImage);


                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        UPLOAD_IMAGE_URL, "POST", params);

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
                Toast.makeText(UserProfileUpdateActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class DownloadUserImage extends AsyncTask<Void, Void, Bitmap> {

        String filename;

        public DownloadUserImage(String filename) {
            this.filename = filename;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                final String USER_IMAGE_URL = Network.forDeploymentIp + "meetmeup/uploads/users/" + this.filename;

                Log.d("Image", USER_IMAGE_URL);

                URLConnection connection = new URL(USER_IMAGE_URL).openConnection();
                connection.setConnectTimeout(1000 * 30);

                return BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        protected void onPostExecute(Bitmap bitmap) {
            //pDialog.dismiss();
            try {
                if(bitmap!=null) {
                    imgUser.setImageBitmap(bitmap);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


}
