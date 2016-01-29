package practiceandroidapplication.android.com.meetmeup.Entity;

/**
 * Created by sibimark on 27/11/2015.
 */
public class Network {

    private static final String testing = "http://192.168.186.1/";
    private static final String deployment = "http://192.168.254.106/";
    private static final String teradyne = "http://172.17.154.233/";

    private static final String webhosting = "http://meetmeup.site11.com/";
    public static final String forDeploymentIp = deployment + "meetmeup/";
    public static final String forFinalDeployment = testing + "meetmeup/transactions.php";


    //testing "http://192.168.1.201/meetmeup/"
    //deployment "http://192.168.254.118/meetmeup/" teradyne
    //deployment "http://192.168.254.104/meetmeup/ sibi residence
    //deployment "http://192.168.0.107/meetmeup/ sibi residence
    // yuson 192.168.0.100
    // aize 192.168.254.109
}
