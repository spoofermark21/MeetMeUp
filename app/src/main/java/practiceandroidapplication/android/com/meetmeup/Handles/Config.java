package practiceandroidapplication.android.com.meetmeup.Handles;

import practiceandroidapplication.android.com.meetmeup.Entity.Network;

/**
 * Created by sibimark on 03/01/2016.
 */
public class Config {

    // File upload url (replace the ip with your server address)
    public static final String FILE_UPLOAD_URL = Network.forDeploymentIp + "meetmeup/fileUpload.php";

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";

}
