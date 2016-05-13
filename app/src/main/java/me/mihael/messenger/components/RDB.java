package me.mihael.messenger.components;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import android.content.Context;
import android.util.Log;

public class RDB {
    private static volatile RDB instance;

    public static RDB getInstance() {
        RDB localInstance = instance;
        if (localInstance == null) {
            synchronized (RDB.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new RDB();
                }
            }
        }
        return localInstance;
    }

    private Context context;
    public void setContext(Context c) {
        this.context = c;
    }

    private Realm realm;
    public Realm getRealm() {
        if(this.realm == null) {
            byte [] pwd = Crypto.getInstance().getMasterPassword();
            if(pwd != null) {
                try {
                    RealmConfiguration conf = new RealmConfiguration.Builder(this.context)
                            .encryptionKey(pwd)
                            .build();
                    this.realm = Realm.getInstance(conf);
                } catch (Exception e) {
                    Log.d("REALM", e.getMessage());
                }
            }
        }

        return this.realm;
    }
}
