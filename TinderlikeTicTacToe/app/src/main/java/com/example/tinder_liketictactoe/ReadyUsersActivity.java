package com.example.tinder_liketictactoe;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ReadyUsersActivity extends AppCompatActivity {

    private Handler handler;
    private LinearLayout users;
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready_users);

        users = findViewById(R.id.users);
        handler = new Handler();
        // Get the transferred data from source activity.
        Intent intent = getIntent();
        String message = intent.getStringExtra("data");
        String received = message;
        String []receivedData = received.split(" ");
        username = receivedData[0];
        TextView textView = (TextView)findViewById(R.id.username);
        textView.setText(receivedData[0]);
        TextView numTrophies = findViewById(R.id.numTrophies);
        numTrophies.setText(receivedData[1]);

        getUsers();

    }

    public TextView textView(String message) {
        if (null == message || message.trim().isEmpty()) {
            message = "<Empty Message>";
        }
        TextView tv = new TextView(this);

        tv.setText(message);
        tv.setTextSize(20);
        tv.setPadding(0, 5, 0, 0);
        return tv;
    }

    public void showMessage(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                users.addView(textView(message));
            }
        });
    }

    public void getUsers(){

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    socket = SocketHandler.getSocket();
                    dos = SocketHandler.getDos();
                    dis = SocketHandler.getDis();
                    while (true) {
                        dos.writeUTF("users");
                        final String received = dis.readUTF();
                        Log.d("received", received);
                        final String[] array = received.split(",");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for(int i = 0; i < array.length; i += 2){
                                    Log.d("array", array[i]);
                                    if(username.equals(array[i].substring(0, username.length()))){
                                        Log.d("skiping", array[i]);
                                        continue;
                                    }
                                    showMessage(array[i]);
                                }
                            }
                        });
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
