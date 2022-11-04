package com.example.project2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {
    private TextView tv_title, tv_date, tv_name, tv_views, tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tv_title = findViewById(R.id.tv_title);
        tv_date = findViewById(R.id.tv_date);
        tv_name = findViewById(R.id.tv_name);
        tv_content = findViewById(R.id.tv_content);

        Intent intent = getIntent(); /*데이터 수신*/
        String name = intent.getExtras().getString("name");
        String content = intent.getExtras().getString("content");
        String title = intent.getExtras().getString("title");
        String date = intent.getExtras().getString("date");

        tv_name.setText(name);
        tv_content.setText(content);
        tv_title.setText(title);
        tv_date.setText(date);
    }
}