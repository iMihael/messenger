package me.mihael.messenger.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import me.mihael.messenger.R;
import me.mihael.messenger.components.Crypto;

public class Register extends AppCompatActivity {

    EditText pwd;
    EditText confirm;
    EditText nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        pwd = (EditText)findViewById(R.id.editText2);
        confirm = (EditText)findViewById(R.id.editText3);
        nickname = (EditText)findViewById(R.id.editTextNickname);
    }

    @Override
    public void onBackPressed() {

    }

    public void createPassword(View v) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
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

        Crypto.getInstance().setMasterPassword(pwd.getText().toString());

        SharedPreferences settings = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("registered", true);
        editor.putString("nickname", nickname.getText().toString());
        editor.apply();

        Intent chatsInt = new Intent(this, Chats.class);
        startActivity(chatsInt);
        finish();
    }
}
