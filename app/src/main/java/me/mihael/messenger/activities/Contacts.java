package me.mihael.messenger.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.Map;

import io.realm.RealmResults;
import me.mihael.messenger.R;
import me.mihael.messenger.components.RDB;
import me.mihael.messenger.components.SimpleEvent;
import me.mihael.messenger.components.SocketIO;
import me.mihael.messenger.models.Contact;

public class Contacts extends AppCompatActivity {

    ListView list;
    FloatingActionButton fab;

    private RealmResults<Contact> contacts;
    private boolean forMessage = false;
    private boolean receiveStatuses = false;
    private Map<String, Boolean> contactStatuses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        list = (ListView)findViewById(R.id.listView);
        fab = (FloatingActionButton)findViewById(R.id.fab);

        listContacts();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(forMessage) {
                    Intent intent = new Intent();
                    intent.putExtra("contactId", contacts.get(position).getId());
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Intent i = new Intent(Contacts.this, AddContact.class);
                    i.putExtra("contactId", contacts.get(position).getId());
                    startActivityForResult(i, 0);
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            forMessage = extras.getBoolean("message", false);
            if(forMessage) {
                fab.setVisibility(View.INVISIBLE);
            }
        }

        SocketIO.getInstance().setContactUpdateEvent(new SimpleEvent() {
            @Override
            public void call(Object o) {
                JSONObject contact = (JSONObject)o;

                try {
                    contactStatuses.put(contact.getString("uniqueId"), contact.getString("status").equals("online"));
                } catch (Exception e) {}

                Contacts.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listContacts();
                    }
                });
            }
        });

    }

    private void listContacts() {
        //TODO: implement pagination
        contacts = RDB.getInstance().getRealm().where(Contact.class).findAll();
        String [] uniqueIds = new String[contacts.size()];
        String [] values = new String[contacts.size()];
        for(int i=0;i<contacts.size();i++) {
            values[i] = contacts.get(i).getNickname();
            if(receiveStatuses) {
                values[i] += contactStatuses.get(contacts.get(i).getUniqueId()) ? " (online)" : " (offline)";
            }
            uniqueIds[i] = contacts.get(i).getUniqueId();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
        list.setAdapter(adapter);

        if(!receiveStatuses) {
            SocketIO.getInstance().getStatuses(uniqueIds, new SimpleEvent() {
                @Override
                public void call(Object o) {
                    receiveStatuses = true;
                    contactStatuses = (Map<String, Boolean>)o;
                    Contacts.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listContacts();
                        }
                    });

                }
            });
        }
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
