package me.mihael.messenger.activities.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;

import java.security.KeyPair;

public class ContactFragment extends Fragment {
    private KeyPair pair;
    private Bitmap bitmap;
    private byte [] contactPublicKey;

    public byte[] getContactPublicKey() {
        return contactPublicKey;
    }

    public void setContactPublicKey(byte[] contactPublicKey) {
        this.contactPublicKey = contactPublicKey;
    }

    public KeyPair getPair() {
        return pair;
    }

    public void setPair(KeyPair pair) {
        this.pair = pair;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }
}
