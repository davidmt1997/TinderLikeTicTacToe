package com.example.tinder_liketictactoe;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SignupActivity extends AppCompatActivity implements LocationListener {

    private EditText usernameField, passwordField;
    private static DataInputStream dis;
    private static DataOutputStream dos;
    private String username;
    private String password;
    private Socket socket;
    protected LocationManager locationManager;
    private double latitude;
    private double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameField = (EditText) findViewById(R.id.usernameText);
        passwordField = (EditText) findViewById(R.id.passwordText);

        //get the location of the client
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //check if the user allows access to the location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        //Log.d("position", latitude + " " + longitude);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }


    public void signin(View view){
        username = usernameField.getText().toString();
        password = passwordField.getText().toString();

        startConnection();
    }

    //if flag = 0 login
    //if flag = 1 sign in
    public void startConnection(){
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                try{
                    socket = SocketHandler.getSocket();
                    dos = SocketHandler.getDos();
                    dis = SocketHandler.getDis();
                    while(true){
                        //Log.d("position", latitude + " " + longitude);
                        //if sending 1 means we are doing sign in
                        String toSend = "1" + " " + username + " " +password + " " + latitude + " " + longitude;
                        dos.writeUTF(toSend);
                        Log.d("sending:", toSend);
                        String received = dis.readUTF();
                        Log.d("received", received);
                        if(received.equals("error")){
                            final String msg = "Username already exists";

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(SignupActivity.this).setTitle("Error in signin").
                                            setMessage(msg).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {

                                        }
                                    }).show();
                                }
                            });
                            break;
                        }
                        else {
                            //start a new activity, the login was successful
                            Intent intent = new Intent(SignupActivity.this, PlayScreenActivity.class);
                            intent.putExtra("username", received);
                            startActivity(intent);
                            break;
                        }
                    }
                } catch(IOException e){
                    e.printStackTrace();
                }

            }
        });

        thread.start();

    }
}