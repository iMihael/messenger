package me.mihael.messenger.models;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Contact extends RealmObject {
    @Required
    private String nickname;
    private byte [] contactPublicKey;
    private byte [] myPublicKey;
    private byte [] myPrivateKey;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public byte[] getContactPublicKey() {
        return contactPublicKey;
    }

    public void setContactPublicKey(byte[] contactPublicKey) {
        this.contactPublicKey = contactPublicKey;
    }

    public byte[] getMyPublicKey() {
        return myPublicKey;
    }

    public void setMyPublicKey(byte[] myPublicKey) {
        this.myPublicKey = myPublicKey;
    }

    public byte[] getMyPrivateKey() {
        return myPrivateKey;
    }

    public void setMyPrivateKey(byte[] myPrivateKey) {
        this.myPrivateKey = myPrivateKey;
    }
}
