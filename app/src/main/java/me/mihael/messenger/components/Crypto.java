package me.mihael.messenger.components;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.RSAKeyGenParameterSpec;

public class Crypto {

    private static volatile Crypto instance;

    public static Crypto getInstance() {
        if (instance == null) {
            instance = new Crypto();
        }
        return instance;
    }

    //512bit length (64 byte)
    private byte [] masterPassword;
    public void setMasterPassword(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte [] buf = str.getBytes(Charset.forName("UTF-8"));
            digest.update(buf);
            this.masterPassword = digest.digest();
        } catch(NoSuchAlgorithmException e) {
            Log.d("CRYPTO", e.getMessage());
            Log.d("CRYPTO", e.toString());
        }
    }
    public byte [] getMasterPassword() {
        return masterPassword;
    }

    public KeyPair generateKeyPair() {
        //TODO: move to separate thread
        try {
            SecureRandom random = new SecureRandom();
            RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4);
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(spec, random);
            return generator.generateKeyPair();
        } catch (Exception e) {
            Log.d("CRYPTO", e.getMessage());
            return null;
        }
    }

    public String exportPublicKey(KeyPair pair) {
        byte [] buf = pair.getPublic().getEncoded();
        return new String(Base64.encode(buf, Base64.DEFAULT));
    }
}
