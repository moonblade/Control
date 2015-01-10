package moonblade.control;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class Main extends Activity implements SensorEventListener {
    BluetoothAdapter mBluetoothAdapter;
    ImageButton brake, gas;
    ArrayList<String> devices;
    int vyh = 3, vyl = -3;
    private float vx, vy, vz;
    boolean forward, reverse;
    SensorManager mSensorManager;
    ImageView dir;
    List<Sensor> mLight;
    Sensor acc;
    private boolean flag = false;

    @Override
    public final void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_land);

        initialise();
        startsensor();


        brake.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        brake.setImageResource(R.drawable.brakepressed);
                        reverse = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        brake.setImageResource(R.drawable.brake);
                        reverse = false;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        brake.setImageResource(R.drawable.brake);
                        reverse = false;
                        break;
                }
                return false;
            }
        });

        gas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        gas.setImageResource(R.drawable.gaspressed);
                        forward = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        gas.setImageResource(R.drawable.gas);
                        forward = false;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        gas.setImageResource(R.drawable.gas);
                        forward = false;
                        break;
                }
                return false;
            }
        });
    }

    private void initialise() {
        brake = (ImageButton) findViewById(R.id.brake);
        gas = (ImageButton) findViewById(R.id.gas);
        dir = (ImageView) findViewById(R.id.dir);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        devices = new ArrayList<String>();
    }


    private void startsensor() {
        flag = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (mLight != null) {

            acc = mLight.get(0);
            String op = mLight.get(0).getName();
            if (acc == null)
                toast("null");
            mSensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!flag) {
            flag = true;
            mSensorManager.unregisterListener(this);
            mSensorManager = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startsensor();
        flag = false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        //get sensor x,y,z values
        vx = event.values[0];
        vy = event.values[1];
        vz = event.values[2];

        //setting the image based on direction and keypress
        if (reverse) {
            if (vy > vyh)
                dir.setImageResource(R.drawable.downright);
            else if (vy < vyl)
                dir.setImageResource(R.drawable.downleft);
            else
                dir.setImageResource(R.drawable.down);
        } else if (forward) {
            if (vy > vyh)
                dir.setImageResource(R.drawable.upright);
            else if (vy < vyl)
                dir.setImageResource(R.drawable.upleft);
            else
                dir.setImageResource(R.drawable.up);
        } else {
            //left and right are disabled for now.
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.action_connect) ;
        {
            connectBluetooth();
        }
        return true;
    }

    private void connectBluetooth() {
        if (hasbluetooth()) {
            turnBluetoothOn();
        }

        ArrayList<String> devices = getPairedDevices();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, devices);
        makedialog(adapter);
    }


    private void turnBluetoothOn() {
        //checking if bluetooth enabled, and enabling if not
        if (!mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            int REQUEST_ENABLE_BT = 1;

            startActivityForResult(intent, REQUEST_ENABLE_BT);
            if (REQUEST_ENABLE_BT == RESULT_CANCELED) {
                toast("cancelled");
            }
        }

        if (mBluetoothAdapter.isEnabled()) {
            // Register the BroadcastReceiver
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter);

        }
    }

    private boolean hasbluetooth() {
        //checking if bluetooth exists
        if (mBluetoothAdapter == null) {
            toast("Bluetooth not supported!");
            return false;
        }
        return true;
    }

    private void makedialog(ArrayAdapter<String> adapter) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Paired Devices");
        ListView devices = new ListView(this);
        devices.setAdapter(adapter);
        dialog.setView(devices);

        dialog.setPositiveButton("Scan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                toast("disabled");
                dialog.cancel();
                scan_devices();
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toast((String) parent.getItemAtPosition(position));
            }
        });
        dialog.show();
    }

    private void scan_devices() {
        mBluetoothAdapter.startDiscovery();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, devices);
        makeScanDialog(adapter);
    }

    private void makeScanDialog(ArrayAdapter<String> adapter) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Devices");
        ListView devices = new ListView(this);
        devices.setAdapter(adapter);
        dialog.setView(devices);
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toast((String) parent.getItemAtPosition(position));
            }
        });
        dialog.show();
    }

    //    Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                Add the name and address to an array adapter to show in a ListView
                devices.add(device.getName() + "\n" + device.getAddress());
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.cancelDiscovery();
        unregisterReceiver(mReceiver);
    }


    public ArrayList<String> getPairedDevices() {
        ArrayList<String> devices=new ArrayList<String>();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
//            Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
//                Add the name and MAC address to an array adapter to show in a ListView
                devices.add(device.getName() + "\n" + device.getAddress());
            }
        }
        return devices;
    }

    void toast(String text)
    {
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }
}
