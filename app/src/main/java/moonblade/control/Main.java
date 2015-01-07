package moonblade.control;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class Main extends Activity{
    public TextView result;
    Button update;
    private float vx,vy,vz;
    private TextView x,y,z;
    SensorManager mSensorManager;
    List<Sensor> mLight;
    Sensor acc;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x=(TextView)findViewById(R.id.x);
        y=(TextView)findViewById(R.id.y);
        z=(TextView)findViewById(R.id.z);
        result=(TextView)findViewById(R.id.result);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(mLight!=null)
        {

            acc=mLight.get(0);
            String op=mLight.get(0).getName();
            if(acc==null)
                Toast.makeText(getApplicationContext(), "null",Toast.LENGTH_LONG).show();

            mSensorManager.registerListener(mSensorListener,acc, SensorManager.SENSOR_DELAY_NORMAL);
        }




    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {



        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}



        @Override
        public final void onSensorChanged(SensorEvent event) {
            // The light sensor returns a single value.
            // Many sensors return 3 values, one for each axis.
            //Toast.makeText(getApplicationContext(), "in", Toast.LENGTH_LONG).show();

            vx=event.values[0];
            vy=event.values[1];
            vz=event.values[2];

            x.setText("Moved in X : "+vx);
            y.setText("Moved in Y : "+vy);
            z.setText("Moved in Z : "+vz);

            // Do something with this sensor value.






        }



    };

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener,acc, SensorManager.SENSOR_DELAY_GAME);
    }
}
