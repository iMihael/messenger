package me.mihael.messenger.models;

import java.security.KeyPair;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.annotations.Required;
import me.mihael.messenger.components.Crypto;
import me.mihael.messenger.components.RDB;

public class Contact extends RealmObject {

    @Required
    private String nickname;
    private byte [] contactPublicKey;
    private String contactPublicKeyStr;
    private byte [] myPublicKey;
    private byte [] myPrivateKey;

    public String getContactPublicKeyStr() {
        return contactPublicKeyStr;
    }

    public void setContactPublicKeyStr(String contactPublicKeyStr) {
        this.contactPublicKeyStr = contactPublicKeyStr;
    }

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

    public static void addContact(String _nickname, String _contactPublicKey, KeyPair myKeyPairForContact) {
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
        newContact.setContactPublicKeyStr(_contactPublicKey);
        newContact.setContactPublicKey(Crypto.getInstance().importPublicKey(_contactPublicKey));
        newContact.setId(newId);

        r.copyToRealm(newContact);
        r.commitTransaction();
    }

    public static Contact findById(int id) {
        Realm r = RDB.getInstance().getRealm();
        RealmResults<Contact> result = r.where(Contact.class).equalTo("id", id).findAll();
        if(result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public static boolean contactExists(String publicKey) {
        Realm r = RDB.getInstance().getRealm();
        RealmQuery<Contact> query = r.where(Contact.class);
        query.equalTo("contactPublicKeyStr", publicKey);

        RealmResults<Contact> res = query.findAll();
        return res.size() > 0;
    }

    public void updateData(String _nickname, String _contactPublicKey, KeyPair myKeyPairForContact) {
        Realm r = RDB.getInstance().getRealm();
        r.beginTransaction();

        this.setNickname(_nickname);
        this.setMyPrivateKey(myKeyPairForContact.getPrivate().getEncoded());
        this.setMyPublicKey(myKeyPairForContact.getPublic().getEncoded());
        this.setContactPublicKeyStr(_contactPublicKey);
        this.setContactPublicKey(Crypto.getInstance().importPublicKey(_contactPublicKey));

        r.copyToRealm(this);
        r.commitTransaction();

    }

    public void delete() {
        Realm r = RDB.getInstance().getRealm();
        r.beginTransaction();
        this.deleteFromRealm();
        r.commitTransaction();
    }

    public void update() {
        Realm r = RDB.getInstance().getRealm();
        r.beginTransaction();
        r.copyToRealm(this);
        r.commitTransaction();
    }
}
