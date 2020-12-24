package com.example.health_bracer;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class MainMenu extends AppCompatActivity {

    Button vDurka;
    String helpNumber;
    ArrayList<String> illnessName;
    ArrayList<String> illnessDesc;
    ArrayList<String> illnessHelp;
    public EditText tokenText;
    public String NFCstring;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDb;
    public String illnessId;


    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Intent intent = new Intent(MainMenu.this, FindedIllnesses.class);
            intent.putExtra("ids", result);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void callToDurka(View view) {
        String dial = "tel:" + this.helpNumber;
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
    }

    public void openMap(View view) {
        Intent intent = new Intent(MainMenu.this, YandexMap.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void openProfile(View view) {
        Intent intent = new Intent(MainMenu.this, Profile.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void openMagnify(View view) {
        Intent intent = new Intent(MainMenu.this, Magnify.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void openMoreMenu(View view) {
        Intent intent = new Intent(MainMenu.this, MoreMenu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void sendID(View view) {

        String userID = this.tokenText.getText().toString();
        String url = "http://mrecord.dqpig.ml/api/octopus/getrecord?id="+userID;
        RequestTask requiem = new RequestTask();
        requiem.execute(url);

    }

    @Override
    protected void onNewIntent(Intent intent){
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            Parcelable mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);  // get the detected tag
            Parcelable[] msgs =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefRecord firstRecord = ((NdefMessage)msgs[0]).getRecords()[0];
            byte[] payload = firstRecord.getPayload();
            int payloadLength = payload.length;
            int langLength = payload[0];
            int textLength = payloadLength - langLength - 1;
            byte[] text = new byte[textLength];
            System.arraycopy(payload, 1+langLength, text, 0, textLength);
            NFCstring = new String(text);

            Toast.makeText(this, this.getString(R.string.ok_detection)+this.NFCstring, Toast.LENGTH_LONG).show();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        this.mDBHelper = new DBHelper(this);
        this.vDurka = findViewById(R.id.vDurku);
        this.tokenText = findViewById(R.id.userTokenID);
        this.helpNumber = "112";
        this.illnessId = "";

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }

        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);

    }
}
