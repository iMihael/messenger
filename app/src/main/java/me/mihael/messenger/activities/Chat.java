package me.mihael.messenger.activities;

import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import me.mihael.messenger.R;
import me.mihael.messenger.activities.chat.ChatArrayAdapter;
import me.mihael.messenger.activities.chat.ChatMessage;
import me.mihael.messenger.models.Contact;

public class Chat extends AppCompatActivity {

    private Contact contact;
    private EditText messageText;
    private ListView listView;
    private ChatArrayAdapter chatArrayAdapter;
    private boolean side = true;

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

        setTitle(contact.getNickname());
    }

    public void sendMessage(View v) {
        if(!messageText.getText().toString().isEmpty()) {
            chatArrayAdapter.add(new ChatMessage(side, messageText.getText().toString().trim()));
            messageText.setText("");
            side = !side;
        }
    }
}
