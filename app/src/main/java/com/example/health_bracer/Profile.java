package com.example.health_bracer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Profile extends AppCompatActivity {

    String salt;
    String token;
    String saltedPassword;
    String username;
    String passw;
    SharedPreferences sPref;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDb;

    public class SendRequest extends AsyncTask<String, Void, String>
    {
        int responseCode;

        @Override
        protected String doInBackground(String[] params) {

            String responseFromServer;

            try{
                String url = params[0];

                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Accept-Language", "en-US,en,q=0.5");
                String urlParameters = params[1] + "=" + params[2];

                //запись информации в поток запроса

                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                BufferedWriter writer = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    writer = new BufferedWriter(new OutputStreamWriter(wr, StandardCharsets.UTF_8));
                }
                writer.write(urlParameters);
                writer.close();
                wr.close();

                // получение информации из потока ответа

                responseCode = con.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }
                responseFromServer = response.toString();

                getSaltAndToken(responseFromServer);

            }
            catch (Exception e) {
                return e.getMessage();
            }
            return responseFromServer;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String message) {

            Type type = new TypeToken<String>(){}.getType();

            if (responseCode == 200) {
                try {
                    saltedPassword = saltPassword(passw);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                SendRequests sendPassw = new SendRequests();
                sendPassw.execute("http://mrecord.dqpig.ml/api/authorize/", "token", token, "password", saltedPassword);
            }
            else Toast.makeText(Profile.this,"Первый этап: " + responseCode, Toast.LENGTH_LONG).show();
        }

    }


    public class SendRequests extends AsyncTask<String, Void, String>
    {

        int responseCode;

        @Override
        protected String doInBackground(String[] params) {

            String responseFromServer;

            try{

                String url = params[0];

                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Accept-Language", "en-US,en,q=0.5");
                String urlParameters = params[1] + "=" + params[2] + "&" + params[3] + "=" + params[4];

                //запись информации в поток запроса

                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                BufferedWriter writer = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    writer = new BufferedWriter(new OutputStreamWriter(wr, StandardCharsets.UTF_8));
                }
                writer.write(urlParameters);
                writer.close();
                wr.close();

                // получение информации из потока ответа

                responseCode = con.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }
                responseFromServer = response.toString();

            }
            catch (Exception e) {
                return e.getMessage();
            }

            return responseFromServer;
        }

        @Override
        protected void onPostExecute(String message) {

            Type type = new TypeToken<String>(){}.getType();

            if (responseCode == 200) {
                Toast.makeText(Profile.this, "Вы успешно вошли в аккаунт", Toast.LENGTH_LONG).show();
                log_in();
            }
            else Toast.makeText(Profile.this, "Второй этап: " + responseCode, Toast.LENGTH_LONG).show();
        }

    }


    String helpNumber;
    TextView welcomeView;
    EditText login;
    EditText password;


    public void log_in() {
        sPref = getPreferences(MODE_PRIVATE);
        Editor ed = sPref.edit();
        ed.putBoolean("is_logged", true);
        ed.putString("login", username);
        ed.apply();
        Intent intent = new Intent(Profile.this, Profile.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void log_out(View view) {
        sPref = getPreferences(MODE_PRIVATE);
        Editor ed = sPref.edit();
        ed.putBoolean("is_logged", false);
        ed.putString("login", "");
        ed.apply();
        Intent intent = new Intent(Profile.this, Profile.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }


    public void getSaltAndToken(String message) {
        //Вычленяем соль и токен из входящего сообщения

        String Sym = "{}\"";
        message = message.replace(':', ' ');
        message = message.replace(',', ' ');
        for (char c : Sym.toCharArray()) {
            message = message.replace(c, ' ');
        }
        message = message.replace("  ", " ");
        String[] words = message.split(" ");
        salt = words[3];
        token = words[7];

    }


    public static String hash256(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        return bytesToHex(md.digest());
    }


    public static String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }


    @RequiresApi(api = 26)
    public String saltPassword(String password) throws NoSuchAlgorithmException {

        String toSPass = password + salt;
        return hash256(toSPass);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void callToDurka(View view)  {
        ///Звонок в дурку///
        String dial = "tel:" + this.helpNumber;
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
    }

    public void openMap(View view) {
        Intent intent = new Intent(Profile.this, YandexMap.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void openProfile(View view) {
        Intent intent = new Intent(Profile.this, Profile.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void openMagnify(View view) {
        Intent intent = new Intent(Profile.this, Magnify.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void openMoreMenu(View view) {
        Intent intent = new Intent(Profile.this, MoreMenu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @RequiresApi(api = 26)
    public void login(View view) {

        username = this.login.getText().toString();
        passw = this.password.getText().toString();

        //Send login
        SendRequest sendLogin = new SendRequest();
        sendLogin.execute("http://mrecord.dqpig.ml/api/startauthsession/", "username", username);

        this.login.setText(null);
        this.password.setText(null);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sPref = getPreferences(MODE_PRIVATE);
        Boolean is_logged = sPref.getBoolean("is_logged", false);
        String name = sPref.getString("login", "Please, use program for good.");
        super.onCreate(savedInstanceState);

        if (!is_logged) {
            setContentView(R.layout.login_layout);
        }
        else {
            setContentView(R.layout.profile_layout);
            this.welcomeView = findViewById(R.id.welcome_view);
            welcomeView.setText("Welcome, " + name);
        }
        this.helpNumber = "112";
        this.login = findViewById(R.id.login);
        this.password = findViewById(R.id.password);

    }

}
