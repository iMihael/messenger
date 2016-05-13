package me.mihael.messenger.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;

import java.security.KeyPair;
import me.mihael.messenger.R;
import me.mihael.messenger.components.Crypto;

public class AddContact extends AppCompatActivity {

    ImageView barCode;
    Button saveBtn;
    KeyPair myKeyPairForContact;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        barCode = (ImageView)findViewById(R.id.imageView2);
        saveBtn = (Button)findViewById(R.id.button4);

        progress = ProgressDialog.show(this, "", "Generating RSA keypair for contact...", true, false);
        new GenerateKeyTask().execute();
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
            showQrCode(qrStr);
            progress.dismiss();
        }
    }

    public void doScanQr(View v) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    private void showQrCode(String str) {
        QRCodeWriter w = new QRCodeWriter();
        try {
            BitMatrix bm = w.encode(str, BarcodeFormat.QR_CODE, 500, 500);
            Bitmap bmap = Bitmap.createBitmap(500, 500, Bitmap.Config.RGB_565);
            for (int i = 0; i < 500; i++) {
                for (int j = 0; j < 500; j++) {
                    bmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK: Color.WHITE);
                }
            }

            barCode.setImageBitmap(bmap);

        } catch(Exception e) {}
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            // handle scan result
            String result = scanResult.getContents();
            Log.i("QR", result);
        }
    }
}
