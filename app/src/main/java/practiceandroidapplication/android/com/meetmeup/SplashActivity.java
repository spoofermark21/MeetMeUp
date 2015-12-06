package practiceandroidapplication.android.com.meetmeup;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //loading meetmeup in 2 seconds

        new Thread("i") {
            public void run() {
                try{
                    sleep(1000);
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    SplashActivity.this.finish();
                } catch (InterruptedException ex) {
                    Log.d("InterruptedException", "@Intent");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.start();

    }

}
