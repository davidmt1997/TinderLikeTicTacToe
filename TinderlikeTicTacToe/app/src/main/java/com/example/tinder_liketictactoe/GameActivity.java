package com.example.tinder_liketictactoe;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class GameActivity extends AppCompatActivity {

    private BView boardView;
    private Engine gameEngine;
    private static Socket socket;
    private static DataInputStream dis;
    private static DataOutputStream dos;
    public static final int SERVERPORT = 3004;
    public static final String SERVER_IP = "192.168.1.7";
    //public  static final String SERVER_IP = "35.24.59.253";
    private static int x;
    private static  int y;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        boardView = (BView) findViewById(R.id.board);
        gameEngine = new Engine();
        boardView.setGameEngine(gameEngine);
        boardView.setMainActivity(this);
    }

    public static int getx(){
        return x;
    }

    public static int gety(){
        return y;
    }

    public static void setX(int posX){
        x = posX;
    }

    public static void setY(int posY){
        y = posY;
    }

    public void gameEnded(int c){
        String msg = (c==-1) ? "Game Ended in Tie" : "Game ended " + c + " win.";

        new AlertDialog.Builder(this).setTitle("Tic Tac Toe").
                setMessage(msg).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                newGame();
            }
        }).show();
    }

    private void newGame(){
        gameEngine.newGame();
        boardView.invalidate();
    }

    public static void sendMove(final int x, final int y){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(SERVER_IP, SERVERPORT);
                    dis = new DataInputStream(socket.getInputStream());
                    dos = new DataOutputStream(socket.getOutputStream());
                    while(true){

                        Log.d("move", x + " " + y);
                        if(x != -1 && y != -1) {
                            Log.d("sending", "move " + x + " " + y);
                            dos.writeUTF("move " + x + " " + y);
                        }
                        break;
                    }
                    socket.close();
                    dis.close();
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public static void readMove(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(SERVER_IP, SERVERPORT);
                    dis = new DataInputStream(socket.getInputStream());
                    dos = new DataOutputStream(socket.getOutputStream());
                    while (true) {
                        String received = dis.readUTF();
                        Log.d("received", received);
                        String[] data = received.split(" ");
                        if(data[0].equals("move")){
                            //BView.makeAMove(Integer.parseInt(data[1]), Integer.parseInt(data[2]));
                        }
                        else if(data[0].equals("error")){
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
