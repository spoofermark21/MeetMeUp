package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpConnection;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParamBean;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import practiceandroidapplication.android.com.meetmeup.Entity.Group;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Preference;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.Interactions;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class CreateGroupActivity extends AppCompatActivity {

    private static final String CREATE_GROUP_URL = Network.forDeploymentIp + "group_save.php";
    private static final String UPLOAD_IMAGE_URL = Network.forDeploymentIp + "image_upload.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    Toolbar toolBar;
    EditText txtGroupName, txtDetails;
    Button btnImage, btnSave;
    ImageView imgGroup;

    User currentUser = Sessions.getSessionsInstance().currentUser;

    Group createGroup;

    //to avoid multiple save
    private boolean isClickSave = false;
    private boolean isNewImage = false;

    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    String encodedImage;

    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateGroupActivity.this, GroupActivity.class));
                finish();
            }
        });

        initUI();

        //Log.d("Random", Interactions.generateString(new Random(), "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", 10));
        //sample download image 22/01/2016
        //new DownloadGroupImage("file.JPG").execute();

    }

    public void initUI() {
        txtGroupName = (EditText) findViewById(R.id.txt_group_name);
        txtDetails = (EditText) findViewById(R.id.txt_details);

        imgGroup = (ImageView) findViewById(R.id.img_group);

        btnImage = (Button) findViewById(R.id.btn_image);
        btnImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });

        btnSave = (Button) findViewById(R.id.btn_create);
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (validateForm() && !isClickSave) {

                    createGroup = new Group(txtGroupName.getText().toString(),
                            txtDetails.getText().toString(), currentUser.getId());

                    new CreateGroup().execute(createGroup.getGroupName(), createGroup.getDetails(),
                            createGroup.getCreatedBy() + "");
                    //boolean flag to avoid multiple creation
                    isClickSave = true;
                }

            }
        });
    }

    public boolean validateForm() {
        boolean isReadyToSave = true;

        if (txtGroupName.getText().toString().equals("")) {
            txtGroupName.setError("Group name is required.");
            isReadyToSave = false;
        } else
            txtGroupName.setError(null);

        if (txtDetails.getText().toString().equals("")) {
            txtDetails.setError("Details is required.");
            isReadyToSave = false;
        } else
            txtDetails.setError(null);

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
                imgGroup.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

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
        startActivity(new Intent(CreateGroupActivity.this, GroupActivity.class));
        finish();
    }

    /*
        thread
     */

    private class UploadGroupImage extends AsyncTask<String, String, String> {

        Bitmap image;
        String filename;
        String directory;

        public UploadGroupImage(Bitmap image, String filename, String directory) {
            this.image = image;
            this.filename = filename;
            this.directory = directory;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateGroupActivity.this, R.style.progress);
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
                params.add(new BasicNameValuePair("directory", "groups"));
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
                Toast.makeText(CreateGroupActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class DownloadGroupImage extends AsyncTask<Void, Void, Bitmap> {

        String filename;

        public DownloadGroupImage(String filename) {
            this.filename = filename;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateGroupActivity.this, R.style.progress);
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            //pDialog.setMessage("Downloading image...");
            pDialog.show();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                final String GROUP_IMAGE_URL = Network.forDeploymentIp + "meetmeup/uploads/groups/" + this.filename;

                Log.d("Image", GROUP_IMAGE_URL);

                URLConnection connection = new URL(GROUP_IMAGE_URL).openConnection();
                connection.setConnectTimeout(1000 * 30);

                return BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        protected void onPostExecute(Bitmap bitmap) {
            pDialog.dismiss();
            try {
                if(bitmap!=null) {
                    imgGroup.setImageBitmap(bitmap);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private HttpParams getHttpRequestParams() {
        HttpParams httpRequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams, 1000 * 30);
        HttpConnectionParams.setSoTimeout(httpRequestParams, 1000 * 30);
        return httpRequestParams;
    }

    private class CreateGroup extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateGroupActivity.this, R.style.progress);
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... groupInfo) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                Log.d("USER_ID (user)", currentUser.getId() + "");

                //group image file name
                if(isNewImage) {
                    fileName = Interactions.generateString(new Random(),"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", 10);
                } else {
                    fileName = "";
                }

                params.add(new BasicNameValuePair("group_name", groupInfo[0]));
                params.add(new BasicNameValuePair("details", groupInfo[1]));
                params.add(new BasicNameValuePair("user_id", groupInfo[2]));
                params.add(new BasicNameValuePair("file_name", fileName));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        CREATE_GROUP_URL, "POST", params);

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
                    //upload image to server
                    if(isNewImage) {
                        Bitmap image = ((BitmapDrawable) imgGroup.getDrawable()).getBitmap();
                        new UploadGroupImage(image, fileName, "groups").execute();
                    }


                    Toast.makeText(CreateGroupActivity.this, message + "!", Toast.LENGTH_SHORT).show();
                    /*new Thread() {
                        public void run() {
                            try {
                                sleep(100);
                                finish();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }.start();*/
                } else {
                    isClickSave = false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
