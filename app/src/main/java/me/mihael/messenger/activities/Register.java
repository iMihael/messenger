package me.mihael.messenger.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import me.mihael.messenger.R;
import me.mihael.messenger.components.Crypto;
import me.mihael.messenger.components.SimpleEvent;
import me.mihael.messenger.components.SocketIO;

public class Register extends AppCompatActivity {

    EditText pwd;
    EditText confirm;
    EditText nickname;
    EditText server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        pwd = (EditText)findViewById(R.id.editText2);
        confirm = (EditText)findViewById(R.id.editText3);
        nickname = (EditText)findViewById(R.id.editTextNickname);
        server = (EditText)findViewById(R.id.editText5);
    }

    @Override
    public void onBackPressed() {

    }

    public void createPassword(View v) {

        final AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Failure!");

        if(nickname.getText().toString().isEmpty()) {
            b.setMessage("Nickname can not be empty.");
            b.show();
            return;
        }

        if(pwd.getText().toString().isEmpty()) {
            b.setMessage("Password can not be empty.");
            b.show();
            return;
        }

        if(!pwd.getText().toString().equals(confirm.getText().toString())) {
            b.setMessage("Passwords do not match.");
            b.show();
            return;
        }

        if(server.getText().toString().isEmpty()) {
            b.setMessage("Server url can not be empty.");
            b.show();
            return;
        }

        SocketIO.getInstance().setUrl(server.getText().toString());
        SocketIO.getInstance().connectRegister(new SimpleEvent() {
            @Override
            public void call(Object o) {
                String uniqueId = o.toString();
                SocketIO.getInstance().setConnected(true);
                Crypto.getInstance().setMasterPassword(pwd.getText().toString());

                SharedPreferences settings = Register.this.getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("registered", true);
                editor.putString("nickname",  Crypto.getInstance().aesEncrypt( nickname.getText().toString()));
                editor.putString("uniqueId", Crypto.getInstance().aesEncrypt( uniqueId ));
                editor.putString("url", Crypto.getInstance().aesEncrypt( SocketIO.getInstance().getUrl() ));
                editor.apply();

                Register.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent chatsInt = new Intent(Register.this, Chats.class);
                        startActivity(chatsInt);
                        finish();
                    }
                });
            }
        }, new SimpleEvent() {
            @Override
            public void call(Object o) {
                Register.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        b.setMessage("Connection error.");
                        b.show();
                    }
                });
            }
        });


    }
}
