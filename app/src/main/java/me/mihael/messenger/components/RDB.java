package me.mihael.messenger.components;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import android.content.Context;
import android.util.Log;

public class RDB {

    private static volatile RDB instance;
    private Context context;
    private Realm realm;

    public static RDB getInstance() {
        if (instance == null) {
            instance = new RDB();
        }
        return instance;
    }

    public void setContext(Context c) {
        this.context = c;
    }

    public Realm getRealm() {
        if(this.realm == null) {
            byte [] pwd = Crypto.getInstance().getMasterPassword();
            if(pwd != null) {
                try {
                    RealmConfiguration conf = new RealmConfiguration.Builder(this.context)
                            .encryptionKey(pwd)
                            .build();
                    Realm.setDefaultConfiguration(conf);
                    this.realm = Realm.getInstance(conf);
                } catch (Exception e) {
                    Log.d("REALM", e.getMessage());
                }
            }
        }

        return this.realm;
    }
}
