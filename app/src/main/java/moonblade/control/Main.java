package moonblade.control;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Script;
import android.util.Log;
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

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class Main extends Activity implements SensorEventListener {
    BluetoothAdapter mBluetoothAdapter;
    ImageButton brake, gas;
    ArrayList<String> devices,macid;
    int vyh = 3, vyl = -3,vyhh=5,vyll=-5;
    int directionPower;
    private float vx, vy, vz;
    boolean forward, reverse;
    SensorManager mSensorManager;
    ImageView dir;
    List<Sensor> mLight;
    Sensor acc;
    private boolean flag = false;

    private static final String TAG = "bluetooth1";

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;


    // SPP UUID service
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805f9b34fb");

    // MAC-address of Bluetooth module, its going to be assigned from paired devices
    private static String address = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_land);
//        tryToConnect();
        initialise();
        startsensor();


        brake.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(address.isEmpty()){
                    toast("Connect First");
                    return false;
                }

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
                if(address.isEmpty())
                {
                    toast("Connect First");
                    return false;
                }
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
            {
                if(vy>vyhh)
                {

                    if(directionPower!=92)
                    {
                        dir.setImageResource(R.drawable.downright);
                        directionPower=92;
                        send();
                    }

                }
                else
                {

                    if(directionPower!=91)
                    {
                        dir.setImageResource(R.drawable.downright);
                        directionPower=91;
                        send();
                    }

                }
            }

            else if (vy < vyl)
            {
                if(vy<vyll)
                {
                    if(directionPower!=72)
                    {
                        dir.setImageResource(R.drawable.downleft);
                        directionPower=72;
                        send();
                    }
                }
                else
                {
                    if(directionPower!=71)
                    {
                        dir.setImageResource(R.drawable.downleft);
                        directionPower=71;
                        send();
                    }
                }


            }
            else
            {
                if(directionPower!=81)
                {
                    dir.setImageResource(R.drawable.down);
                    directionPower=81;
                    send();
                }

            }

        } else if (forward) {

            if (vy > vyh)
            {
                if(vy>vyhh)
                {
                    if(directionPower!=32)
                    {
                        dir.setImageResource(R.drawable.upright);
                        directionPower=32;
                        send();
                    }

                }
                else
                {
                    if(directionPower!=31)
                    {
                        dir.setImageResource(R.drawable.upright);
                        directionPower=31;
                        send();
                    }
                }

            }
            else if (vy < vyl)
            {
                if(vy<vyll)
                {
                    if(directionPower!=12)
                    {
                        dir.setImageResource(R.drawable.upleft);
                        directionPower=12;
                        send();
                    }

                }
                else
                {
                    if(directionPower!=11)
                    {
                        dir.setImageResource(R.drawable.upleft);
                        directionPower=11;
                        send();
                    }

                }

            }
            else
            {
                if(directionPower!=21)
                {
                    dir.setImageResource(R.drawable.up);
                    directionPower=21;
                    send();
                }
            }


        } else {
            //left and right are disabled for now.
/*
            if (vy > vyh)
                dir.setImageResource(R.drawable.right);
            else if (vy < vyl)
                dir.setImageResource(R.drawable.left);
            else
*/
            if(directionPower!=0)
            {
                dir.setImageResource(R.drawable.stopped);
                directionPower=0;
                send();
            }

        }

    }

    public boolean tryToConnect() {

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e1) {
            errorExit("Fatal Error", "In onResume() and socket create failed: "
                    + e1.getMessage() + ".");
        }

        btAdapter.cancelDiscovery();
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "...Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error",
                        "In onResume() and unable to close socket during connection failure"
                                + e2.getMessage() + ".");
                return false;
            }
        }

        toast("connected");

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");

        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            errorExit(
                    "Fatal Error",
                    "In onResume() and output stream creation failed:"
                            + e.getMessage() + ".");
            return false;
        }
        return true;
    }

    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();
        Log.d(TAG, "...Send data: " + message + "...");
        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {

            toast("cannot");
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned
        // on
        // Emulator doesn't support Bluetooth and will return null
        if (btAdapter == null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                // Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device)
            throws IOException {
        if (Build.VERSION.SDK_INT >= 10) {
            try {
                final Method m = device.getClass().getMethod(
                        "createInsecureRfcommSocketToServiceRecord",
                        new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection", e);
            }
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    private void errorExit(String title, String message) {
        toast(title + " - " + message);
        finish();
    }



    void send()
    {
        if(directionPower==0)
            sendData("00");
        else
            sendData(String.valueOf(directionPower));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.action_connect)
        {
            connectBluetooth();
        }

        if(id==R.id.action_about)
        {
            Intent i=new Intent(Main.this,About.class);
            startActivity(i);
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
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Paired Devices");
        ListView devices = new ListView(this);
        devices.setAdapter(adapter);
        dialog.setView(devices);

/*
        dialog.setPositiveButton("Scan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                toast("disabled");
                dialog.cancel();
                scan_devices();
            }
        });
*/
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                toast((String) parent.getItemAtPosition(position));
//            toast(macid.get(position));
                address=macid.get(position);
                if(!tryToConnect())
                {
                    toast("Some Error Occured");
                }


            }
        });
        dialog.show();
    }

/*
    private void scan_devices() {
        mBluetoothAdapter.startDiscovery();
        if (mBluetoothAdapter.isEnabled()) {
            // Register the BroadcastReceiver
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter);

        }

        makeScanDialog(scanAdapter);
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
        dialog.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                if(mBluetoothAdapter.isDiscovering()){
//                    mBluetoothAdapter.cancelDiscovery();
//                }
//                scan_devices();
                makeScanDialog(scanAdapter);
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
                try {
                    scanDevices.add(device.getName() + "\n" + device.getAddress());

                }catch (Exception e)
                {
                    toast(e.toString());
                }
                scanAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, scanDevices);
            }
        }
    };
*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();
        //unregisterReceiver(mReceiver);
    }


    public ArrayList<String> getPairedDevices() {
        ArrayList<String> devices=new ArrayList<String>();
        macid=new ArrayList<String>();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
//            Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
//                Add the name and MAC address to an array adapter to show in a ListView
                devices.add(device.getName() + "\n" + device.getAddress());
                macid.add(device.getAddress());
            }
        }
        return devices;
    }

    void toast(String text)
    {
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }
}
