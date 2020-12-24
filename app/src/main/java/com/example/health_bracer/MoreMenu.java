package com.example.health_bracer;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MoreMenu extends AppCompatActivity {

    String helpNumber;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void callToDurka(View view) {
        String dial = "tel:" + this.helpNumber;
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
    }

    public void openMap(View view) {
        Intent intent = new Intent(MoreMenu.this, YandexMap.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void openProfile(View view) {
        Intent intent = new Intent(MoreMenu.this, Profile.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void openMagnify(View view) {
        Intent intent = new Intent(MoreMenu.this, Magnify.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void openMoreMenu(View view) {
        Intent intent = new Intent(MoreMenu.this, MoreMenu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void openAbout(View view) {
        Intent intent = new Intent(MoreMenu.this, About.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_layout);
        this.helpNumber = "112";
    }

}
