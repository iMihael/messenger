package me.mihael.messenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences settings = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        boolean registered = settings.getBoolean("registered", false);

        if(!registered) {
            Intent registerIntent = new Intent(this, Register.class);
            startActivity(registerIntent);
        }
    }
}
