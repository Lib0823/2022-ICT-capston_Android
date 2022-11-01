package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.shape.CornerFamily;

public class MainActivity extends AppCompatActivity {  //hi ~~~ main

    public ImageButton moveCal, run;

    int version = 1;
    DatabaseOpenHelper helper;
    SQLiteDatabase database;

    String sql;
    Cursor cursor;

    private BottomNavigationView bottomNavi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DataBase연결부분
        helper = new DatabaseOpenHelper(MainActivity.this, DatabaseOpenHelper.tableName, null, version);
        database = helper.getWritableDatabase();



        sql = "SELECT * FROM "+ helper.tableName + " WHERE login = '1'";
        cursor = database.rawQuery(sql, null);
        cursor.moveToNext();   // 첫번째에서 다음 레코드가 없을때까지 읽음
        String name = cursor.getString(2);

        TextView welcome = findViewById(R.id.welcomeMessage);
        welcome.setText(name + "\ntoday's workout");

        bottomNavi = findViewById(R.id.bottonNavi);
        bottomNavi.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_home:
                        break;
                    case R.id.action_fitness:
                        Intent intent = new Intent(MainActivity.this, FitnessActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_board:
                        Intent intent2 = new Intent(MainActivity.this, BoardActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.action_info:
                        Intent intent3 = new Intent(MainActivity.this, InfoActivity.class);
                        startActivity(intent3);
                        break;
                }
                return true;
            }
        });


        // 러닝
        run = findViewById(R.id.runBtn);
        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RunActivity.class);
                startActivity(intent);
            }
        });


        CheckTypesTask task = new CheckTypesTask();
        // 운동일지
        moveCal = (ImageButton) findViewById(R.id.moveCal);
        moveCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.execute();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //String inputId = idEditText.getText().toString();
                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        // 러닝 프로그래스바
        sql = "SELECT * FROM "+ helper.tableName + " WHERE login = '1'";
        cursor = database.rawQuery(sql, null);
        cursor.moveToNext();
        int run = Integer.parseInt(cursor.getString(7));

        ProgressBar progress = (ProgressBar) findViewById(R.id.progress) ;
        progress.setProgress(run) ;
    }
    private class CheckTypesTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(
                MainActivity.this);

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            asyncDialog.setMessage("운동일지를 불러오는중...");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                for (int i = 0; i < 5; i++) {
                    asyncDialog.setProgress(i * 20);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }
    }

}