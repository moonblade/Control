package moonblade.control;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import moonblade.control.R;

public class MainSplashScreen extends Activity {
    private int delay=3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_splash_screen);
        Thread background = new Thread() {
            public void run() {

                try {
                    sleep(delay*1000);
                   Intent i=new Intent(getBaseContext(),Main.class);
                    startActivity(i);
                    finish();

                } catch (Exception e) {

                }
            }
        };
        background.start();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_splash_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
