package me.mihael.messenger.components.tcp;

import android.util.Log;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread implements Runnable {
    private final int port = 6000;
    private ServerSocket serverSocket;

    public void run() {
        Socket socket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            Log.d("TCP", e.getMessage());
            Log.d("TPC", e.toString());
            return;
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                socket = serverSocket.accept();

                CommunicationThread commTh = new CommunicationThread(socket);
                Thread t = new Thread(commTh);
                //Client c = new Client(t, socket);
                //Tcp.getInstance().addClient(c);
                t.start();

            } catch (IOException e) {
                Log.d("TCP", e.getMessage());
                Log.d("TCP", e.toString());
            }
        }

    }
}
