package me.mihael.messenger.activities;

import android.Manifest;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;

import java.security.KeyPair;
import me.mihael.messenger.R;
import me.mihael.messenger.activities.fragments.ContactFragment;
import me.mihael.messenger.components.Crypto;
import me.mihael.messenger.models.Contact;

public class AddContact extends AppCompatActivity {

    EditText nickname;
    ImageView barCode;
    Button saveBtn;

    ProgressDialog progress;

    private ContactFragment contactFragment;
    private String contactPublicKey;
    private KeyPair myKeyPairForContact;

    private int contactId = -1;
    private Contact editContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        barCode = (ImageView)findViewById(R.id.imageView2);
        saveBtn = (Button)findViewById(R.id.button4);
        nickname = (EditText)findViewById(R.id.editText4);

        FragmentManager fm = getFragmentManager();
        contactFragment = (ContactFragment)fm.findFragmentByTag("contact");

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            this.contactId = extras.getInt("contactId", -1);
        }

        if(this.contactId != -1) {
            this.setTitle("Edit Contact");
            //TODO: get data from realm
            editContact = Contact.findById(this.contactId);
        } else {
            if (contactFragment == null) {
                progress = ProgressDialog.show(this, "", "Generating RSA keypair for contact...", true, false);
                new GenerateKeyTask().execute();
            } else {
                myKeyPairForContact = contactFragment.getPair();
                barCode.setImageBitmap(contactFragment.getBitmap());
                if (contactFragment.getContactPublicKey() != null) {
                    contactPublicKey = contactFragment.getContactPublicKey();
                    saveBtn.setEnabled(true);
                }
            }
        }
    }

    private class GenerateKeyTask extends AsyncTask<Void, Void, KeyPair> {
        protected KeyPair doInBackground(Void... params) {
            return Crypto.getInstance().generateKeyPair();
        }

        protected void onPostExecute(KeyPair kp) {
            myKeyPairForContact = kp;
            String qrStr = Crypto.getInstance().exportPublicKey(kp);
            SharedPreferences settings = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
            String nickname = settings.getString("nickname", "Anon");
            qrStr += nickname;

            Bitmap bm = genQrCode(qrStr);
            barCode.setImageBitmap(bm);

            contactFragment = new ContactFragment();
            getFragmentManager().beginTransaction().add(contactFragment, "contact").commit();
            contactFragment.setPair(kp);
            contactFragment.setBitmap(bm);

            progress.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchScanner();
        }
    }

    public void doScanQr(View v) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 0);
        } else if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchScanner();
        }
    }

    private void launchScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    private Bitmap genQrCode(String str) {
        QRCodeWriter w = new QRCodeWriter();
        try {
            BitMatrix bm = w.encode(str, BarcodeFormat.QR_CODE, 500, 500);
            Bitmap bmap = Bitmap.createBitmap(500, 500, Bitmap.Config.RGB_565);
            for (int i = 0; i < 500; i++) {
                for (int j = 0; j < 500; j++) {
                    bmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK: Color.WHITE);
                }
            }


            return bmap;
        } catch(Exception e) {
            return null;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Failure!");

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            // handle scan result
            String result = scanResult.getContents();
            if(result != null) {
                if(result.length() < 398) {

                    b.setMessage("Wrong public key.");
                    b.show();
                } else {
                    contactPublicKey = result.substring(0, 398);

                    if(Contact.contactExists(contactPublicKey)) {
                        b.setMessage("Contact already exists.");
                        b.show();
                        return;
                    }

                    String nick = result.substring(398);
                    contactFragment.setContactPublicKey(contactPublicKey);
                    saveBtn.setEnabled(true);
                    nickname.setText(nick);
                }
            }
        }
    }

    public void doSaveContact(View v) {
        if(nickname.getText().toString().isEmpty()) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Failure!");
            b.setMessage("Nickname can not be empty.");
            b.show();
            return;
        }

        if(this.contactId != -1) {
            //TODO: update contact
        } else {
            Contact.addContact(nickname.getText().toString(), contactPublicKey, myKeyPairForContact);
        }

        setResult(0);
        finish();
    }

    public void doRegenerateKeyPair(View v) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Sure?");
        b.setMessage("Are you sure you want to regenerate keypair for this contact ?");
        b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progress = ProgressDialog.show(AddContact.this, "", "Generating RSA keypair for contact...", true, false);
                new GenerateKeyTask().execute();
            }
        });
        b.setNegativeButton("No", null);
        b.show();
    }
}
