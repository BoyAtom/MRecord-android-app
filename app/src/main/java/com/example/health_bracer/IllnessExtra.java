package com.example.health_bracer;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class IllnessExtra extends AppCompatActivity {

    private TextView IllnessName;
    private TextView IllnessDescription;
    private TextView IllnessSupport;

    String name, desc, supp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.magnify_extra);

        IllnessName = findViewById(R.id.illnessName);
        IllnessDescription = findViewById(R.id.illnessDescription);
        IllnessSupport = findViewById(R.id.illnessSupport);

        getData();
        setData();

    }

    private void getData() {
        if (getIntent().hasExtra("name") && getIntent().hasExtra("desc") && getIntent().hasExtra("supp")){

            name = getIntent().getStringExtra("name");
            desc = getIntent().getStringExtra("desc");
            supp = getIntent().getStringExtra("supp");

        }
    }

    private void setData() {

        IllnessName.setText(name);
        IllnessDescription.setText(desc);
        IllnessSupport.setText(supp);

    }

}
