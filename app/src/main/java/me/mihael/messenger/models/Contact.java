package me.mihael.messenger.models;

import java.security.KeyPair;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Required;
import me.mihael.messenger.components.RDB;

public class Contact extends RealmObject {

    @Required
    private String nickname;
    private byte [] contactPublicKey;
    private byte [] myPublicKey;
    private byte [] myPrivateKey;
    private int id;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static void addContact(String _nickname, byte [] _contactPublicKey, KeyPair myKeyPairForContact) {
        Realm r = RDB.getInstance().getRealm();

        int newId = 1;
        Number n = r.where(Contact.class).max("id");
        if(n != null) {
            newId = n.intValue() + 1;
        }

        r.beginTransaction();
        Contact newContact = r.createObject(Contact.class);

        newContact.setNickname(_nickname);
        newContact.setMyPrivateKey(myKeyPairForContact.getPrivate().getEncoded());
        newContact.setMyPublicKey(myKeyPairForContact.getPublic().getEncoded());
        newContact.setContactPublicKey(_contactPublicKey);
        newContact.setId(newId);

        r.copyToRealm(newContact);
        r.commitTransaction();
    }
}
