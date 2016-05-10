package me.mihael.messenger.components;

import android.util.Log;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Crypto {

    private static volatile Crypto instance;

    public static Crypto getInstance() {
        Crypto localInstance = instance;
        if (localInstance == null) {
            synchronized (Crypto.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Crypto();
                }
            }
        }
        return localInstance;
    }

    private byte [] masterPassword;
    public void setMasterPassword(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            byte [] buf = str.getBytes(Charset.forName("UTF-8"));
            digest.update(buf);
            this.masterPassword = digest.digest();
        } catch(NoSuchAlgorithmException e) {
            Log.d("CRYPTO", e.getMessage());
            Log.d("CRYPTO", e.toString());
        }

    }
}
