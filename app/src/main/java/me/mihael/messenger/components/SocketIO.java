package me.mihael.messenger.components;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketIO {

    private static volatile SocketIO instance;
    private String url;
    private Socket socket;
    private boolean connected = false;
    private String uniqueId;
    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static SocketIO getInstance() {
        if (instance == null) {
            instance = new SocketIO();
        }
        return instance;
    }

    public void connectLogin(final SimpleEvent success, final SimpleEvent failure) {

        try {
            IO.Options opts = new IO.Options();
            opts.query = "uniqueId=" + this.uniqueId;

            socket = IO.socket(this.url, opts);
        } catch (URISyntaxException e) {
            Log.d("s.io", e.getMessage());
            failure.call(e.getMessage());
            return;
        }

        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(!SocketIO.this.connected) {
                    socket.disconnect();
                    failure.call("Can not connect to server.");
                }
            }
        }).on("login", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject obj = (JSONObject) args[0];
                    if (obj.getString("status").equals("success")) {
                        success.call(null);
                    }
                } catch(JSONException e) {
                    failure.call(e.getMessage());
                }
            }
        });

        socket.connect();

    }

    public void connectRegister(final SimpleEvent success, final SimpleEvent failure) {
        try {
            socket = IO.socket(this.url);
        } catch (URISyntaxException e) {
            Log.d("s.io", e.getMessage());
            failure.call(e.getMessage());
            return;
        }

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
            }
        }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(!SocketIO.this.connected) {
                    socket.disconnect();
                    failure.call("Can not connect to server.");
                }
            }
        }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("s.io", "error event :(");
            }
        }).on("login", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject obj = (JSONObject) args[0];
                    if (obj.getString("status").equals("success")) {
                        SocketIO.this.uniqueId = obj.getString("uniqueId");
                        success.call(SocketIO.this.uniqueId);
                    }
                } catch(JSONException e) {
                    failure.call(e.getMessage());
                }
            }
        });

        socket.connect();
    }

}
