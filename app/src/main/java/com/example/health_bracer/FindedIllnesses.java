package com.example.health_bracer;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class FindedIllnesses extends AppCompatActivity {

    ArrayList<String> IllnessName;
    ArrayList<String> IllnessDesc;
    ArrayList<String> IllnessSupp;

    String ids;

    View magnify_extra;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.magnify_layout);

        this.mDBHelper = new DBHelper(this);
        this.magnify_extra = getLayoutInflater().inflate(R.layout.magnify_extra, null);

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

        String data = "Nothing where";

        data = getData();

        if (data != "Nothing where") {

            findInDB(getIllness(data));

        }
    }

    private String getData() {
        if (getIntent().hasExtra("ids")) {

            ids = getIntent().getStringExtra("ids");

        }
        return ids;
    }

    private void findInDB (String id[]) {

        IllnessName = new ArrayList<>(id.length);
        IllnessDesc = new ArrayList<>(id.length);
        IllnessSupp = new ArrayList<>(id.length);

        Cursor cursor     = mDb.rawQuery("SELECT id FROM illness", null);
        Cursor cursorName = mDb.rawQuery("SELECT name FROM illness", null);
        Cursor cursorDesc = mDb.rawQuery("SELECT description FROM illness", null);
        Cursor cursorHelp = mDb.rawQuery("SELECT assistance FROM illness", null);

        cursor.moveToFirst();
        cursorName.moveToFirst();
        cursorDesc.moveToFirst();
        cursorHelp.moveToFirst();

        for (int i = 2; i < id.length; i++) {
            while (!cursor.isAfterLast()) {
                //Теперь тут нет костылей. Всё работает как и надо
                String answer = cursor.getString(0);

                if (id[i].equals(answer)) {
                    cursorName.moveToPosition(cursor.getPosition());
                    IllnessName.add(cursorName.getString(0));

                    cursorDesc.moveToPosition(cursor.getPosition());
                    IllnessDesc.add(cursorDesc.getString(0));

                    cursorHelp.moveToPosition(cursor.getPosition());
                    IllnessSupp.add(cursorHelp.getString(0));
                }
                cursor.moveToNext();
            }
            cursor.moveToFirst();
        }

        initRecyclerView();

    }

    private String[] getIllness (String message) {
        String Sym = "{}[]\"";
        message = message.replace(':', ' ');
        message = message.replace(',', ' ');
        for (char c : Sym.toCharArray()) {
            message = message.replace(c, ' ');
        }
        message = message.replace("  ", " ");
        message = message.replace("  ", " ");
        String[] words = message.split(" ");

        return words;
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(IllnessName, IllnessDesc, IllnessSupp, magnify_extra, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void callToDurka(View view) {
        String dial = "tel:" + 112;
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
    }

    public void openMap(View view) {
        Intent intent = new Intent(FindedIllnesses.this, YandexMap.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void openProfile(View view) {
        Intent intent = new Intent(FindedIllnesses.this, Profile.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void openMagnify(View view) {
        Intent intent = new Intent(FindedIllnesses.this, Magnify.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void openMoreMenu(View view) {
        Intent intent = new Intent(FindedIllnesses.this, MoreMenu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

}
