package me.mihael.messenger.components;

import android.util.Log;

import java.net.DatagramSocket;

import me.mihael.messenger.components.udp.DatagramReceiver;

public class UDP {
    private boolean started = false;
    private static UDP instance;
    private DatagramSocket socket;
    private int port = -1;
    private DatagramReceiver receiver;

    public static UDP getInstance() {
        if(instance == null) {
            instance = new UDP();
        }

        return instance;
    }

    public int startServer() {
        if(!started) {
            try {
                socket = new DatagramSocket(0);
                port = socket.getLocalPort();
                receiver = new DatagramReceiver(socket);
                receiver.start();
                started = true;
            } catch (Exception e) {
                Log.d("UDP", e.getMessage());
            }
        }

        return port;
    }

    public int getPort() {
        return port;
    }
}
