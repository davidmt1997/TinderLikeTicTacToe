package com.example.tinder_liketictactoe;

//references:
//https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/
//https://developer.android.com/reference/android/location/Location
//http://www.coderzheaven.com/2017/05/01/client-server-programming-in-android-send-message-to-the-client-and-back/
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;

public class MainActivity extends AppCompatActivity implements LocationListener{

    private EditText usernameField, passwordField;
    private DataInputStream dis;
    private DataOutputStream dos;
    private String username;
    private String password;
    private Socket socket;
    protected LocationManager locationManager;
    private double latitude;
    private double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    //called when the user presses the login button
    public void login(View view) {
        username = usernameField.getText().toString();
        password = passwordField.getText().toString();

        startConnection();
    }

    //called when the user presses the sign in button
    public void signup(View view){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    //if flag = 0 login
    //if flag = 1 sign in
    public void startConnection() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    socket = SocketHandler.getSocket();
                    dos = SocketHandler.getDos();
                    dis = SocketHandler.getDis();
                    while(true){
                    //sending 0 means we are doing log in
                        String toSend = "0" + " " + username + " " +password + " " + latitude + " " + longitude;
                        dos.writeUTF(toSend);
                        Log.d("sending", toSend);

                        String received = dis.readUTF();
                        Log.d("received", received);
                        if(received.equals("error")){
                            final String msg = "Wrong username or password";

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(MainActivity.this).setTitle("Error in login").
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
                            Intent intent = new Intent(MainActivity.this, PlayScreenActivity.class);
                            intent.putExtra("username", received);
                            startActivity(intent);
                            dos.writeUTF("quit");
                            break;
                        }
                    }
                    SocketHandler.closeConnection();
                } catch(IOException e){
                    e.printStackTrace();
                }
            }

        });

        thread.start();

    }
}