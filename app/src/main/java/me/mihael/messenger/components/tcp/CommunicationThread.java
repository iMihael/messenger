package me.mihael.messenger.components.tcp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class CommunicationThread implements Runnable {
    private Socket clientSocket;
    private BufferedReader input;

    public CommunicationThread(Socket cSocket) {
        this.clientSocket = cSocket;
        try {
            this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        } catch(IOException e) {
            Log.d("TCP", e.getMessage());
            Log.d("TCP", e.toString());
        }
    }


    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                String read = input.readLine();
                if(read != null && !read.isEmpty()) {
                    Log.i("NET", read);
                } else if(read == null) {
                    //TODO: delete client
                    return;
                }
            } catch (IOException e) {
                Log.d("TCP", e.getMessage());
                Log.d("TCP", e.toString());
            }
        }
    }
}