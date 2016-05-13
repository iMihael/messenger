package me.mihael.messenger.components.tcp;

import java.net.Socket;

public class Client {
    //private String nickname;
    private Thread thread;
    private Socket socket;

    public Client(Thread t, Socket s) {
        this.thread = t;
        this.socket = s;
    }
}
