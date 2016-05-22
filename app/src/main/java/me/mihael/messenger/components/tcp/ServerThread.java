package me.mihael.messenger.components.tcp;

import android.util.Log;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import me.mihael.messenger.components.Tcp;

public class ServerThread implements Runnable {
    private ServerSocket serverSocket;
    private int port;

    public int getPort() {
        return port;
    }

    public void run() {
        Socket socket;
        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            Log.d("TCP", e.getMessage());
            Log.d("TPC", e.toString());
            return;
        }

        port = serverSocket.getLocalPort();
        Tcp.getInstance().setPort(port);

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
