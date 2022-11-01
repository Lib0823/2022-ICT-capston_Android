package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FitnessActivity extends AppCompatActivity {

    int version = 1;
    DatabaseOpenHelper helper;
    SQLiteDatabase database;

    String sql;
    Cursor cursor;

    private AdView mAdView;

    private BottomNavigationView bottomNavi;

    private ImageButton moveDiet, moveLeanmass, moveBulk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness);

        // 구글 광고API
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        // 바텀네비게이션
        bottomNavi = findViewById(R.id.bottonNavi);
        bottomNavi.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_home:
                        Intent intent = new Intent(FitnessActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_fitness:
                        break;
                    case R.id.action_board:
                        Intent intent2 = new Intent(FitnessActivity.this, BoardActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.action_info:
                        Intent intent3 = new Intent(FitnessActivity.this, InfoActivity.class);
                        startActivity(intent3);
                        break;
                }
                return true;
            }
        });

        //DataBase연결부분
        helper = new DatabaseOpenHelper(FitnessActivity.this, DatabaseOpenHelper.tableName, null, version);
        database = helper.getWritableDatabase();

        sql = "SELECT * FROM "+ helper.tableName + " WHERE login = '1'";
        cursor = database.rawQuery(sql, null);

        cursor.moveToNext();   // 첫번째에서 다음 레코드가 없을때까지 읽음
        String height = cursor.getString(4);   // 키
        String weight = cursor.getString(5); // 몸무게

        double height2 = Double.parseDouble(height);
        double weight2 = Double.parseDouble(weight);

        double bmi = weight2 / (height2 * height2);
        bmi *= 10000;

        String result, recommend;
        if(bmi < 18.5){
            result = "저체중";
            recommend = "벌크업";
        }else if(bmi < 22.9){
            result = "정상체중";
            recommend = "린매스업";
        }else{
            result = "과체중";
            recommend = "다이어트";
        }
        String bmi2 = String.format("%.1f", bmi);
        TextView bmiResult = findViewById(R.id.BMI);
        TextView bmiRecomm = findViewById(R.id.BMI2);
        bmiResult.setText("BMI : '"+bmi2+"'");
        bmiRecomm.setText(result+"이며, "+recommend+" 추천합니다");

        moveDiet = (ImageButton) findViewById(R.id.dietBtn);
        moveDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FitnessActivity.this, DietActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        moveLeanmass = (ImageButton) findViewById(R.id.leanmassUpBtn);
        moveLeanmass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FitnessActivity.this, LeanmassActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        moveBulk = (ImageButton) findViewById(R.id.bulkUpBtn);
        moveBulk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FitnessActivity.this, BulkActivity.class);
                startActivity(intent);
                //finish();
            }
        });


    }
}