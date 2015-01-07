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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class Main extends Activity implements SensorEventListener{
    public TextView result,result2;
    Button stop,start;
    int vxh=5,vxl=5,vyh=3,vyl=-3;
    private float vx,vy,vz;
    private TextView x,y,z;
    SensorManager mSensorManager;
    ImageView dir;
    List<Sensor> mLight;
    Sensor acc;
    private boolean flag=false;
    @Override
    public final void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        dir=(ImageView)findViewById(R.id.dir);
        setContentView(R.layout.activity_main);
        x=(TextView)findViewById(R.id.x);
        y=(TextView)findViewById(R.id.y);
        z=(TextView)findViewById(R.id.z);
        result=(TextView)findViewById(R.id.result);
        result2=(TextView)findViewById(R.id.result2);
        stop=(Button)findViewById(R.id.stop);
        start=(Button)findViewById(R.id.start);
        startsensor();
//        dir.setImageResource(R.drawable.upright);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText("stopped");
                result2.setText("stopped");
                onPause();
            }
        });
        start.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //mSensorManager.registerListener(this,acc, SensorManager.SENSOR_DELAY_NORMAL);
                if(mLight!=null)
                {

                    acc=mLight.get(0);
                    String op=mLight.get(0).getName();
                    if(acc==null)
                        Toast.makeText(getApplicationContext(), "null",Toast.LENGTH_LONG).show();
                    startsensor();
//                    Toast.makeText(getApplicationContext(),"Doesnt work, start game again",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void startsensor() {
        flag=false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(mLight!=null)
        {

            acc=mLight.get(0);
            String op=mLight.get(0).getName();
            if(acc==null)
                Toast.makeText(getApplicationContext(), "null",Toast.LENGTH_LONG).show();

            mSensorManager.registerListener(this,acc, SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    //private final SensorEventListener mSensorListener = new SensorEventListener() {
    //};

    @Override
    protected void onPause() {
        super.onPause();
        if(!flag) {
            flag=true;
            mSensorManager.unregisterListener(this);
            mSensorManager = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,acc, SensorManager.SENSOR_DELAY_NORMAL);
        flag=false;
    }

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

        if(vx>vxh)
            result.setText("forward");
        if(vx<vxl)
            result.setText("reverse");

        if(vy>vyh)
            result2.setText("right");
        else if(vy<vyl)
            result2.setText("left");
        else
            result2.setText("no direction");
/*        if(vx>vxh)
        {
            if(vy>vyh)
                dir.setImageResource(R.drawable.upright);
            else if(vy<vyl)
                dir.setImageResource(R.drawable.upleft);
            else
                dir.setImageResource(R.drawable.up);

        }
        else if(vx<vxl)
        {
            if(vy>vyh)
                dir.setImageResource(R.drawable.downright);
            else if(vy<vyl)
                dir.setImageResource(R.drawable.downleft);
            else
                dir.setImageResource(R.drawable.down);
        }
        else
        {
            if(vy>vyh)
                dir.setImageResource(R.drawable.right);
            else if(vy<vyl)
                dir.setImageResource(R.drawable.left);
            else
                dir.setImageResource(R.drawable.stopped);

        }*/

    }
}