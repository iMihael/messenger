package me.mihael.messenger.activities;

import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.Map;

import me.mihael.messenger.R;
import me.mihael.messenger.activities.chat.ChatArrayAdapter;
import me.mihael.messenger.activities.chat.ChatMessage;
import me.mihael.messenger.components.SimpleEvent;
import me.mihael.messenger.components.SocketIO;
import me.mihael.messenger.models.Contact;

public class Chat extends AppCompatActivity {

    private Contact contact = null;
    private EditText messageText;
    private ListView listView;
    private ChatArrayAdapter chatArrayAdapter;
    private boolean side = true;
    private boolean online = false;
    private String contactUnique;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageText = (EditText)findViewById(R.id.editText6);
        listView = (ListView)findViewById(R.id.listView);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right_message);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        int contactId = getIntent().getExtras().getInt("contactId");
        contact = Contact.findById(contactId);
        contactUnique = contact.getUniqueId();

        setTitle(contact.getNickname());

        SocketIO.getInstance().getStatuses(new String[]{contact.getUniqueId()}, new SimpleEvent() {
            @Override
            public void call(Object o) {
                final Map<String, Boolean> contactStatuses = (Map<String, Boolean>)o;

                Chat.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(contactStatuses.get(contact.getUniqueId()) != null && contactStatuses.get(contact.getUniqueId())) {
                            online = true;
                            setTitle(contact.getNickname() + " (online)");
                        } else {
                            online = false;
                            setTitle(contact.getNickname() + " (offline)");
                        }
                    }
                });
            }
        });

        SocketIO.getInstance().setContactUpdateEvent(new SimpleEvent() {
            @Override
            public void call(Object o) {
                final JSONObject jContact = (JSONObject)o;
                try {
                    if(jContact.getString("uniqueId").equals(contactUnique)) {
                        online = jContact.getString("status").equals("online");
                        Chat.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (online) {
                                    setTitle(contact.getNickname() + " (online)");
                                } else {
                                    setTitle(contact.getNickname() + " (offline)");
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.d("chat", e.getMessage());
                }
            }
        });

        SocketIO.getInstance().getContactIP(contactUnique, new SimpleEvent() {
            @Override
            public void call(Object o) {

            }
        });
    }

    public void sendMessage(View v) {
        if(!messageText.getText().toString().isEmpty()) {
            chatArrayAdapter.add(new ChatMessage(side, messageText.getText().toString().trim()));
            messageText.setText("");
            side = !side;
        }
    }
}
