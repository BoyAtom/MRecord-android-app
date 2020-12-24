package com.example.health_bracer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.util.ArrayList;

public class Magnify extends AppCompatActivity {

    private static final String TAG = "Magnify";

    String helpNumber;
    private ArrayList<String> IllnessNames = new ArrayList<>();
    private ArrayList<String> IllnessDescryptions = new ArrayList<>();
    private ArrayList<String> IllnessSupports = new ArrayList<>();

    View magnify_extra;
    private TextView illnessName;
    private TextView illnessDescription;
    private TextView illnessSupport;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDb;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void callToDurka(View view) {
        String dial = "tel:" + this.helpNumber;
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
    }

    public void openMap(View view) {
        Intent intent = new Intent(Magnify.this, YandexMap.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void openProfile(View view) {
        Intent intent = new Intent(Magnify.this, Profile.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void openMagnify(View view) {
        Intent intent = new Intent(Magnify.this, Magnify.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void openMoreMenu(View view) {
        Intent intent = new Intent(Magnify.this, MoreMenu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magnify_layout);

        magnify_extra = getLayoutInflater().inflate(R.layout.magnify_extra, null);
        this.illnessName = magnify_extra.findViewById(R.id.illnessName);
        this.illnessDescription = magnify_extra.findViewById(R.id.illnessDescription);
        this.illnessSupport = magnify_extra.findViewById(R.id.illnessSupport);

        this.mDBHelper = new DBHelper(this);
        Log.d(TAG, "onCreate: started.");
        this.helpNumber = "112";

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

        this.initBitmaps();
    }

    private void initBitmaps() {
        Cursor cursor = mDb.rawQuery("SELECT name FROM illness", null);
        cursor.moveToFirst();
        this.IllnessNames.add(cursor.getString(0));
        while (!cursor.isAfterLast()) {
            this.IllnessNames.add(cursor.getString(0));
            cursor.moveToNext();
        }

        cursor = mDb.rawQuery("SELECT description FROM illness", null);
        cursor.moveToFirst();
        this.IllnessDescryptions.add(cursor.getString(0));
        while (!cursor.isAfterLast()) {
            this.IllnessDescryptions.add(cursor.getString(0));
            cursor.moveToNext();
        }

        cursor = mDb.rawQuery("SELECT assistance FROM illness", null);
        cursor.moveToFirst();
        this.IllnessSupports.add(cursor.getString(0));
        while (!cursor.isAfterLast()) {
            this.IllnessSupports.add(cursor.getString(0));
            cursor.moveToNext();
        }

        this.initRecyclerView();
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(IllnessNames, IllnessDescryptions, IllnessSupports, magnify_extra, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

}
