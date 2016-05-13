package me.mihael.messenger.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import io.realm.Realm;
import me.mihael.messenger.R;
import me.mihael.messenger.components.Crypto;
import me.mihael.messenger.components.RDB;


public class Login extends AppCompatActivity {

    EditText pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //RDB.getInstance().setContext(getApplicationContext());
        RDB.getInstance().setContext(this);

        SharedPreferences settings = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        boolean registered = settings.getBoolean("registered", false);

        if(!registered) {
            Intent registerIntent = new Intent(this, Register.class);
            startActivity(registerIntent);
            return;
        }

        pwd = (EditText)findViewById(R.id.editText);
    }

    public void doLogin(View v) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Failure!");

        if(pwd.getText().toString().isEmpty()) {
            b.setMessage("Password can not be empty.");
            b.show();
            return;
        }

        Crypto.getInstance().setMasterPassword(pwd.getText().toString());
        Realm r = RDB.getInstance().getRealm();
        if(r == null) {
            b.setMessage("Can not decrypt database with this password.");
            b.show();
            return;
        }

        Intent chatsInt = new Intent(this, Chats.class);
        startActivity(chatsInt);
        finish();
    }
}
