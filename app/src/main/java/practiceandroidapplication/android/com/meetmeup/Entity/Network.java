package practiceandroidapplication.android.com.meetmeup.Entity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by sibimark on 27/11/2015.
 */
public class Network {

    //testing "http://192.168.1.201/meetmeup/"
    //deployment "http://192.168.254.118/meetmeup/" teradyne
    //deployment "http://192.168.254.104/meetmeup/ sibi residence
    //deployment "http://192.168.0.107/meetmeup/ sibi residence
    // yuson 192.168.0.100
    // aize 192.168.254.109

    private static final String testing = "http://192.168.186.1/";
    private static final String deployment = "http://192.168.254.106/";
    private static final String teradyne = "http://172.17.154.233/";

    private static final String webhosting = "http://meetmeup.site11.com/";
    public static final String forDeploymentIp = teradyne + "meetmeup/";
    public static final String forFinalDeployment = testing + "meetmeup/transactions.php";

}
