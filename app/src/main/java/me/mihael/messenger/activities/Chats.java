package me.mihael.messenger.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import me.mihael.messenger.R;
import me.mihael.messenger.components.Logger;
import me.mihael.messenger.components.LoggerFactory;
import me.mihael.messenger.components.Stun;
import me.mihael.messenger.components.UDP;


public class Chats extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Logger.Observer {

    @Override
    public void onLogEntry(String message) {
        Log.d("stun", message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Master Password");
//        final EditText input = new EditText(this);
//        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//        builder.setView(input);
//
//        builder.show();

//        Tcp.getInstance().startServer();
        /*Realm r = RDB.getInstance().getRealm();
        if(r != null) {
            RDB.getInstance().getRealm().beginTransaction();
            Contact c = RDB.getInstance().getRealm().createObject(Contact.class);
            c.setNickname("John222");
            c.setContactPublicKey(Crypto.getInstance().getMasterPassword());
            c.setMyPrivateKey(Crypto.getInstance().getMasterPassword());
            c.setMyPublicKey(Crypto.getInstance().getMasterPassword());
            RDB.getInstance().getRealm().copyToRealm(c);
            RDB.getInstance().getRealm().commitTransaction();
        }*/

        //Stun.getInstance().doDiscovery();
        //new StunDiscovery().execute();

        if(!Stun.getInstance().getDiscovered()) {
            Stun.getInstance().setLocalPort(UDP.getInstance().startServer());
            new StunDiscovery().execute();
        }

    }

    private class StunDiscovery extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... args) {
            Stun.getInstance().doDiscovery();
            return null;
        }

        protected void onPostExecute(Void feed) {

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.chats, menu);
        //return true;
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.app_settings) {
            Intent i = new Intent(this, Settings.class);
            startActivity(i);
        } else if(id == R.id.contacts) {
            Intent i = new Intent(this, Contacts.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            int contactId = data.getExtras().getInt("contactId", -1);
            if (contactId != -1) {
                Intent i = new Intent(this, Chat.class);
                i.putExtra("contactId", contactId);
                startActivity(i);
            }
        } catch (Exception e) {}
    }

    public void selectContactForMessage(View v) {
        Intent i = new Intent(this, Contacts.class);
        i.putExtra("message", true);
        startActivityForResult(i, 0);
    }
}
