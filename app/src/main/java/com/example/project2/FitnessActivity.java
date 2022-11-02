package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FitnessActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase mFirebaseDB;
    private DatabaseReference mDatabaseRef = mFirebaseDB.getInstance().getReference();
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); // 방금 로그인 성공한 유저의 정보를 가져오는 객체
    private static int height, weight;

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

        final UserAccount[] userInfo = {new UserAccount()};
        //데이터 읽기
        mDatabaseRef.child("project").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                weight = 0;
                height = 0;
                Log.d("정보1", "333333");
                double height2 = height;
                double weight2 = weight;

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
            }

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo[0] = snapshot.getValue(UserAccount.class);
                if(userInfo[0] == null || userInfo[0].equals(null)){
                    weight = 0;
                    height = 0;
                    Log.d("정보1", "2222222");
                    double height2 = height;
                    double weight2 = weight;

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
                }
                else {
                    weight = userInfo[0].getWeight();
                    height = userInfo[0].getHeight();
                    Log.d("정보1", "11111111111111111111111111111111323123123131232");
                    Log.d("키1", String.valueOf(weight));
                    Log.d("키2", String.valueOf(height));
                    double height2 = height;
                    double weight2 = weight;

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
                }
            }
        });
        Log.d("키3", String.valueOf(weight));
        Log.d("키4", String.valueOf(height));
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
                        finish();
                        break;
                    case R.id.action_fitness:
                        break;
                    case R.id.action_board:
                        Intent intent2 = new Intent(FitnessActivity.this, BoardActivity.class);
                        startActivity(intent2);
                        finish();
                        break;
                    case R.id.action_info:
                        Intent intent3 = new Intent(FitnessActivity.this, InfoActivity.class);
                        startActivity(intent3);
                        finish();
                        break;
                }
                return true;
            }
        });


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

    // 이름 변경을 위한 메소드
    private String read() {

        final UserAccount[] userInfo = {new UserAccount()};
        //데이터 읽기
        mDatabaseRef.child("project").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                weight = 0;
                height = 0;
                Log.d("정보1", "333333");
            }

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo[0] = snapshot.getValue(UserAccount.class);
                if(userInfo[0] == null || userInfo[0].equals(null)){
                    weight = 0;
                    height = 0;
                    Log.d("정보1", "2222222");
                }
                else {
                    weight = userInfo[0].getWeight();
                    height = userInfo[0].getHeight();
                    Log.d("정보1", "11111111111111111111111111111111323123123131232");
                    Log.d("키1", String.valueOf(weight));
                    Log.d("키2", String.valueOf(height));
                }
            }
        });
        /*if(userInfo[0].getName() == null || userInfo[0].getName().length() == 0)
            welcome.setText("회원정보를 불러오지 못했습니다.");
        else if (userInfo[0].getDogName().equals(""))
            tvDogName.setText(userInfo[0].getDogName());*/
        return weight+","+height;
    }
}