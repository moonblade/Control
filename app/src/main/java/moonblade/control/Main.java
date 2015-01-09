package moonblade.control;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class Main extends Activity implements SensorEventListener{
    public TextView result,result2;
    BluetoothAdapter mBluetoothAdapter;
    ImageButton brake,gas;
    int vxh=5,vxl=-1,vyh=3,vyl=-3;
    private float vx,vy,vz;
    private TextView x,y,z;
    boolean forward,reverse;
    SensorManager mSensorManager;
    ImageView dir;
    List<Sensor> mLight;
    Sensor acc;

    private boolean flag=false;
    @Override
    public final void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_land);
        brake=(ImageButton)findViewById(R.id.brake);
        gas=(ImageButton)findViewById(R.id.gas);
        dir=(ImageView)findViewById(R.id.dir);
        startsensor();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();



        brake.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        brake.setImageResource(R.drawable.brakepressed);
                        reverse=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        brake.setImageResource(R.drawable.brake);
                        reverse=false;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        brake.setImageResource(R.drawable.brake);
                        reverse=false;
                        break;
                }
                return false;
            }
        });
        gas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        gas.setImageResource(R.drawable.gaspressed);
                        forward=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        gas.setImageResource(R.drawable.gas);
                        forward=false;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        gas.setImageResource(R.drawable.gas);
                        forward=false;
                        break;
                }
                return false;
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
        startsensor();
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

        if(reverse)
        {
            if(vy>vyh)
                dir.setImageResource(R.drawable.downright);
            else if(vy<vyl)
                dir.setImageResource(R.drawable.downleft);
            else
                dir.setImageResource(R.drawable.down);
        }
        else if(forward)
        {
            if(vy>vyh)
                dir.setImageResource(R.drawable.upright);
            else if(vy<vyl)
                dir.setImageResource(R.drawable.upleft);
            else
                dir.setImageResource(R.drawable.up);
        }
        else
        {
/*
            if (vy > vyh)
                dir.setImageResource(R.drawable.right);
            else if (vy < vyl)
                dir.setImageResource(R.drawable.left);
            else
*/
            dir.setImageResource(R.drawable.stopped);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);

        return true;

    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        final int id=item.getItemId();

        if(id==R.id.action_connect);
        {
            connectBluetooth();
        }
        return true;
    }

    private void connectBluetooth()
    {
        if(mBluetoothAdapter==null)
        {
            Toast.makeText(getApplicationContext(),"No Bluetooth! App will exit.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!mBluetoothAdapter.isEnabled())
        {
            Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            int REQUEST_ENABLE_BT=1;

            startActivityForResult(intent, REQUEST_ENABLE_BT);
            if(REQUEST_ENABLE_BT==RESULT_CANCELED)
            {
                Toast.makeText(getApplicationContext(),"Cancelled",Toast.LENGTH_SHORT).show();
            }
        }

//        side note for future
//        Optionally, your application can also listen for the ACTION_STATE_CHANGED broadcast Intent, which the system will broadcast whenever the Bluetooth state has changed. This broadcast contains the extra fields EXTRA_STATE and EXTRA_PREVIOUS_STATE, containing the new and old Bluetooth states, respectively. Possible values for these extra fields are STATE_TURNING_ON, STATE_ON, STATE_TURNING_OFF, and STATE_OFF. Listening for this broadcast can be useful to detect changes made to the Bluetooth state while your app is running.

    }

    private void makedialog(ArrayAdapter mArrayAdapter)
    {
        Dialog dialog=new Dialog(getApplication());
        dialog.setTitle("Paired Devices");
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.listview);
        ListView devices;
        devices=(ListView)findViewById(R.id.lvname);
//        devices.setAdapter(mArrayAdapter);
        dialog.show();
    }


}
