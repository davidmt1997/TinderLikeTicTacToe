package com.example.tinder_liketictactoe;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketHandler extends Thread implements Runnable{
    private static Socket socket;
    private static DataInputStream dis;
    private static DataOutputStream dos;
    public static final int SERVERPORT = 3003;
    public static final String SERVER_IP = "192.168.1.7";
    //public static final String SERVER_IP = "35.24.59.253";

    public static synchronized Socket getSocket() throws IOException{
        socket = new Socket(SERVER_IP, SERVERPORT);
        return socket;
    }

    public static synchronized DataInputStream getDis() throws IOException{
        dis = new DataInputStream(socket.getInputStream());
        return dis;
    }

    public static synchronized DataOutputStream getDos() throws IOException{
        dos = new DataOutputStream(socket.getOutputStream());
        return dos;
    }

    public static synchronized void closeConnection() throws IOException{
        socket.close();
        dis.close();
        dos.close();
    }
}