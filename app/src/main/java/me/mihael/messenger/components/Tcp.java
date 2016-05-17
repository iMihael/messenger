package me.mihael.messenger.components;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import me.mihael.messenger.components.tcp.Client;
import me.mihael.messenger.components.tcp.ServerThread;

public class Tcp {

    private static volatile Tcp instance;
    private boolean serverStarted = false;

    public static Tcp getInstance() {
        if (instance == null) {
            instance = new Tcp();
        }
        return instance;
    }

    private Thread serverThread;
    private List<Client> clients;

    public Tcp() {
        clients = new ArrayList<>();
    }

    public void addClient(Client c) {
        clients.add(c);
    }

    public void startServer() {
        if(!this.serverStarted) {
            this.serverThread = new Thread(new ServerThread());
            this.serverThread.start();
            this.serverStarted = true;
        }

        Log.i("TCP", "IP: " + Utils.getIPAddress(true));
    }
}
