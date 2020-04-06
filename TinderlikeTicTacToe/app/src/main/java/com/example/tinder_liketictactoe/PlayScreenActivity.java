package com.example.tinder_liketictactoe;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class PlayScreenActivity extends AppCompatActivity {

    private String username;
    private static Socket socket;
    private static DataInputStream dis;
    private static DataOutputStream dos;
    private String received;
    private static String trophies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_screen);

        // Get the transferred data from source activity.
        Intent intent = getIntent();
        String message = intent.getStringExtra("username");
        received = message;
        String []receivedData = received.split(" ");
        TextView textView = (TextView)findViewById(R.id.username);
        textView.setText(receivedData[0]);
        username = receivedData[0];

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            shutConnection();
            Log.d(this.getClass().getName(), "back button pressed");
        }
        return super.onKeyDown(keyCode, event);
    }

    public void practice(View view){
        new AlertDialog.Builder(PlayScreenActivity.this)
                .setTitle(R.string.pick_mode)
                .setItems(R.array.modes_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        if (which == 0) {
                            Intent intent = new Intent(PlayScreenActivity.this, TicTacToe.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(PlayScreenActivity.this, MinMaxActivity.class);
                            startActivity(intent);
                        }
                    }
                }).show();
    }

    public void play(View view){
        userReady();
        /*
        Intent intent = new Intent(PlayScreenActivity.this, ReadyUsersActivity.class);
        intent.putExtra("data", username + " " + trophies);
        startActivity(intent);*/
    }

    public void leaderboard(View view){
        getLeaderboard();
    }

    public void shutConnection(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = SocketHandler.getSocket();
                    dos = SocketHandler.getDos();
                    dis = SocketHandler.getDis();
                    while (true) {
                        dos.writeUTF("quit " + username);
                        System.out.println("Closing this connection : " + socket);
                        socket.close();
                        System.out.println("Connection closed");
                        break;
                    }
                    SocketHandler.closeConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void getLeaderboard(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = SocketHandler.getSocket();
                    dos = SocketHandler.getDos();
                    dis = SocketHandler.getDis();
                    while (true) {
                        String tosend = "leaderboard";
                        dos.writeUTF(tosend);
                        Log.d("sending:", tosend);
                        String received = dis.readUTF();
                        Intent intent = new Intent(PlayScreenActivity.this, ShowLeaderboardActivity.class);
                        intent.putExtra("result", received);
                        startActivity(intent);
                        break;
                    }
                    dos.writeUTF("quit");
                    SocketHandler.closeConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public static void updateDB(final String result){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = SocketHandler.getSocket();
                    dos = SocketHandler.getDos();
                    dis = SocketHandler.getDis();
                    dos.writeUTF(result);
                    String received = dis.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void userReady(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = SocketHandler.getSocket();
                    dos = SocketHandler.getDos();
                    dis = SocketHandler.getDis();
                    while (true) {
                        String tosend = "ready " + username;
                        dos.writeUTF(tosend);

                        try {
                            Thread.sleep(2000);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                        String received = dis.readUTF();
                        Log.d("received", received);
                        if(received.equals("outside")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(PlayScreenActivity.this)
                                            .setTitle(R.string.server_msg)
                                            .setMessage(R.string.too_far)
                                            .show();
                                }
                            });
                        }
                        if(received.equals("okay")){
                            Intent intent = new Intent(PlayScreenActivity.this, GameActivity.class);
                            startActivity(intent);
                        } else if(received.equals("notnow")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(PlayScreenActivity.this)
                                            .setTitle(R.string.server_msg)
                                            .setMessage(R.string.not_now)
                                            .show();
                                }
                            });
                        } else if(received.equals("busy")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(PlayScreenActivity.this)
                                            .setTitle(R.string.server_msg)
                                            .setMessage(R.string.try_again)
                                            .show();
                                }
                            });
                        }
                        break;
                    }
                    dos.writeUTF("quit");
                    SocketHandler.closeConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
