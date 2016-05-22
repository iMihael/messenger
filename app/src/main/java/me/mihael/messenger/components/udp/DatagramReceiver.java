package me.mihael.messenger.components.udp;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class DatagramReceiver extends Thread {
    private DatagramSocket socket;
    private boolean bKeepRunning = true;

    public void kill() {
        bKeepRunning = false;
    }

    public DatagramReceiver(DatagramSocket socket) {
        this.socket = socket;
    }

    public void run() {
        byte [] buf = new byte[512];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
            while (bKeepRunning) {
                socket.receive(packet);
                String msg = new String(buf, 0, buf.length);
                Log.d("UDP", msg);
            }
        } catch (Exception e) {
            Log.d("UDP", e.getMessage());
        }
    }
}
