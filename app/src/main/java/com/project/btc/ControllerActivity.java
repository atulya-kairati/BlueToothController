package com.project.btc;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class ControllerActivity extends AppCompatActivity {

    int flag = 1;
    Button onOffButton, btnDis, sendMsgButton;
    String address = null;
    EditText customMsg;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //static final UUID myUUID = UUID.fromString("ca347293-6578-43ee-91d8-cee0a9f73181");//Generated On uuidgenerator.net

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS);

        setContentView(R.layout.activity_controller);

        onOffButton = findViewById(R.id.onOffButton);
        btnDis = findViewById(R.id.buttonDisconnect);
        sendMsgButton = findViewById(R.id.sendMsgButton);
        customMsg = findViewById(R.id.customMsg);

        // to connect to the bluetooth Module
        ConnectBT cbt = new ConnectBT();
        cbt.execute();

        onOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (flag == 0){
                    sendSignal("1");
                    onOffButton.setText("OFF");
                    flag = 1;
                    //onOffButton.setBackgroundColor();
                }else if (flag ==1){
                    sendSignal("0");
                    onOffButton.setText("ON");
                    flag = 0;
                }
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Disconnect();
            }
        });


        sendMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal(customMsg.getText().toString());
                customMsg.setText("");
            }
        });

    }

    ////****************************
    public void onOff(View v){

        if (flag == 0){
            sendSignal("1");
            flag = 1;
            onOffButton.setBackgroundColor(6);
        }else if (flag == 1){

            sendSignal("0");
            flag = 0;
            onOffButton.setBackgroundColor(5);
        }

    }
///*********************************** useless
    @Override
    public void onBackPressed() {
//        if ( btSocket!=null ) {
//            try {
//                btSocket.close();
//                Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
//            } catch(IOException e) {
//                msg("Error");
//            }
//        }
//
//        finish();
        Toast.makeText(this, "Press Disconect to go back.", Toast.LENGTH_SHORT).show();
    }



    private void sendSignal (String number ) {
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write(number.getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void Disconnect () {
        if ( btSocket!=null ) {
            try {
                btSocket.close();
                Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
            } catch(IOException e) {
                msg("Error");
            }
        }

        finish();
    }

    private void msg (String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected  void onPreExecute () {
            progress = ProgressDialog.show(ControllerActivity.this, "Connecting...", "Please Wait!!!");
        }

        @Override
        protected Void doInBackground (Void... devices) {
            try {
                if ( btSocket==null || !isBtConnected ) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed.Try again.");
                finish();
            } else {
                msg("Connected");
                isBtConnected = true;
            }

            progress.dismiss();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////// EXPERIMENTAL

//    private  final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
//
//        @Override
//        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//
//            String intentAction;
//            if (newState == BluetoothProfile.STATE_CONNECTED) {
//                intentAction = ACTION_GATT_CONNECTED;
//                mConnectionState = STATE_CONNECTED;
//                boolean rssiStatus = mBluetoothGatt.readRemoteRssi();
//                broadcastUpdate(intentAction);
//                // Attempts to discover services after successful connection.
//                Log.i(">>>>>>", "Attempting to start service discovery:" +
//                        mBluetoothGatt.discoverServices());
//            }
//
//        }
//    };
}

