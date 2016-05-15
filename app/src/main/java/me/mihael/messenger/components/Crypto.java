package me.mihael.messenger.components;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import me.mihael.messenger.models.Contact;

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

    public KeyPair getKeyPairFromContact(Contact c) {
        X509EncodedKeySpec x509KeySpecPublic = new X509EncodedKeySpec(c.getMyPublicKey());
        PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(c.getMyPrivateKey());

        try {
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PublicKey publicKey = fact.generatePublic(x509KeySpecPublic);
            PrivateKey privateKey = fact.generatePrivate(privateSpec);
            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            return null;
        }
    }

    public String exportPublicKey(KeyPair pair) {
        byte [] buf = pair.getPublic().getEncoded();
        return new String(Base64.encode(buf, Base64.DEFAULT));
    }

    public byte [] importPublicKey(String str) {
        return Base64.decode(str, Base64.DEFAULT);
    }
}
