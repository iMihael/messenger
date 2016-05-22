package me.mihael.messenger.components;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import me.mihael.messenger.models.Contact;

public class SocketIO {

    private static volatile SocketIO instance;
    private String url;
    private Socket socket;
    private boolean connected = false;
    private String uniqueId;
    private String nickname;

    private SimpleEvent contactUpdateEvent;

    public void setContactUpdateEvent(SimpleEvent contactUpdateEvent) {
        this.contactUpdateEvent = contactUpdateEvent;
    }

    public void deleteContactUpdateEvent() {
        this.contactUpdateEvent = null;
    }

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

    private Map<String, Boolean> convertStatuses(Object... args) {
        try {
            JSONArray contacts = (JSONArray) args[0];
            Map<String, Boolean> contactStatuses = new HashMap<>();
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject cont = contacts.getJSONObject(i);
                contactStatuses.put(cont.getString("id"), cont.getString("status").equals("online"));
            }

            return contactStatuses;
        } catch (Exception e) {
            return null;
        }
    }

    public void getStatuses(String [] ids, final SimpleEvent success) {
        final JSONArray jarray = new JSONArray();
        for(int i=0;i<ids.length;i++) {
            jarray.put(ids[i]);
        }

        socket.once("statuses", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.emit("subscribeToContacts", jarray);
                success.call(convertStatuses(args));
            }
        });

        socket.emit("getStatuses", jarray);
    }

    public void getContactIP(final String uniqueId, final SimpleEvent success) {
        socket.once("sendIPForContact", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject) args[0];
                try {
                    if (obj.getString("status") != null && obj.getString("status").equals("success")) {
                        String localIP = obj.getString("localIP");
                        Contact c = Contact.findByUniqueId(uniqueId, true);
                        localIP = Crypto.getInstance().decryptOnContactMyPrivate(c, localIP);
                        success.call(localIP);
                    }
                } catch (Exception e) {

                }
            }
        });

        socket.emit("getContactIP", uniqueId);
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

        socket.once(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.disconnect();
                failure.call("Can not connect to server.");
            }
        }).once("login", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    SocketIO.this.subscribeSocket();
                    socket.off(Socket.EVENT_CONNECT_ERROR);
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

    private void subscribeSocket() {
        socket.on("contactUpdate", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(SocketIO.this.contactUpdateEvent != null) {
                    try {
                        JSONObject contact = (JSONObject) args[0];
                        SocketIO.this.contactUpdateEvent.call(contact);
                    } catch (Exception e) {
                        Log.d("contactUpdate", e.getMessage());
                    }
                }
            }
        })
        .on(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("s.io", "error event :(");
            }
        })
        .on("getContactIP", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject) args[0];
                try {
                    String uniqueId = obj.getString("forContact");
                    //TODO: fix thread mistake
                    Contact contact = Contact.findByUniqueId(uniqueId, true);
                    if(contact != null) {
                        String ip = Utils.getIPAddress(true);
                        ip = Crypto.getInstance().encryptOnContactPublic(contact, ip);
                        JSONObject jObject = new JSONObject();
                        jObject.put("forContact", uniqueId);
                        jObject.put("localIP", ip);
                        socket.emit("sendIPForContact", jObject);
                    }
                } catch (Exception e) {
                    Log.d("s.io", e.getMessage());
                }
            }
        });
    }

    public void connectRegister(final SimpleEvent success, final SimpleEvent failure) {
        try {
            socket = IO.socket(this.url);
        } catch (URISyntaxException e) {
            Log.d("s.io", e.getMessage());
            failure.call(e.getMessage());
            return;
        }

        socket.once(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.disconnect();
                failure.call("Can not connect to server.");
            }
        }).once("login", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    socket.off(Socket.EVENT_CONNECT_ERROR);
                    SocketIO.this.subscribeSocket();
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
