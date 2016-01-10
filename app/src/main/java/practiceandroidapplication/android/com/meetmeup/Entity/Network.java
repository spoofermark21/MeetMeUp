package practiceandroidapplication.android.com.meetmeup.Entity;

/**
 * Created by sibimark on 27/11/2015.
 */
public class Network {

    //testing "http://192.168.1.201/meetmeup/"
    //deployment "http://192.168.254.118/meetmeup/" teradyne
    //deployment "http://192.168.254.116/meetmeup/ sibi residence
    //deployment "http://192.168.0.107/meetmeup/ sibi residence
    // yuson 192.168.0.100
    // aize 192.168.254.109


    private static final String testing = "http://192.168.1.201/";
    private static final String deployment = "http://192.168.254.114/";
    private static final String webhosting = "http://meetmeup.site11.com/";

    public static final String forDeploymentIp = deployment + "meetmeup/";

    public static final String imageStoragePath = forDeploymentIp + "/meetmeup/image_upload.php";

    public static final String FILE_UPLOAD_URL = "http://192.168.0.104/AndroidFileUpload/fileUpload.php";

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";

}
