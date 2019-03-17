package com.example.witsdaily;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class addPost extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        TextView t = findViewById(R.id.titleValue);
        String title = getIntent().getExtras().getString("title");
        t.setText(title);
    }
}
