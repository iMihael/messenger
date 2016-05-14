package me.mihael.messenger.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.realm.RealmResults;
import me.mihael.messenger.R;
import me.mihael.messenger.components.RDB;
import me.mihael.messenger.models.Contact;

public class Contacts extends AppCompatActivity {

    ListView list;

    private RealmResults<Contact> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        list = (ListView)findViewById(R.id.listView);

        listContacts();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(Contacts.this, AddContact.class);
                i.putExtra("contactId", contacts.get(position).getId());
                startActivityForResult(i, 0);
            }
        });
    }

    private void listContacts() {
        contacts = RDB.getInstance().getRealm().where(Contact.class).findAll();
        String [] values = new String[contacts.size()];
        for(int i=0;i<contacts.size();i++) {
            values[i] = contacts.get(i).getNickname();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
        list.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        listContacts();
    }

    public void createContact(View v) {
        Intent i = new Intent(this, AddContact.class);
        startActivityForResult(i, 0);
    }
}
