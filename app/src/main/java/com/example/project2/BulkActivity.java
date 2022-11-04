package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BulkActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase mFirebaseDB;
    private DatabaseReference mDatabaseRef = mFirebaseDB.getInstance().getReference();
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); // 방금 로그인 성공한 유저의 정보를 가져오는 객체
    private static int height, weight, age;
    private static String gender;

    private ImageButton bf1, bf2, bf3, bf4, bf5;
    private ImageButton bs1, bs2, bs3, bs4, bs5;
    private ImageButton bb1, bb2, bb3, bb4, bb5;

    int version = 1;
    //DatabaseOpenHelper helper;
    SQLiteDatabase database;

    String sql;
    Cursor cursor;

    Double BMR, bulkup;
    TextView basicText, recomText;
    ProgressBar basicBar, recomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk);

        // imageButton
        // 운동
        bf1 = findViewById(R.id.bf1);
        bf1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=J4TMWaxPQKk"));
                        startActivity(intent);
            }
        });
        bf2 = findViewById(R.id.bf2);
        bf2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=wGVMv2pOZcg&t=94s"));
                startActivity(intent);
            }
        });
        bf3 = findViewById(R.id.bf3);
        bf3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=XCCeqDwmCFQ"));
                startActivity(intent);
            }
        });
        bf4 = findViewById(R.id.bf4);
        bf4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=szOEoAzw7YU"));
                startActivity(intent);
            }
        });
        bf5 = findViewById(R.id.bf5);
        bf5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=bD9zSgeVUkY"));
                startActivity(intent);
            }
        });
        // 식단
        bs1 = findViewById(R.id.bs1);
        bs1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=Z--DYp5dPw4"));
                startActivity(intent);
            }
        });
        bs2 = findViewById(R.id.bs2);
        bs2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=ZDwNHTDuLRs"));
                startActivity(intent);
            }
        });
        bs3 = findViewById(R.id.bs3);
        bs3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=XCCeqDwmCFQ&t=49s"));
                startActivity(intent);
            }
        });
        bs4 = findViewById(R.id.bs4);
        bs4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=d32FMg9tzEo"));
                startActivity(intent);
            }
        });
        bs5 = findViewById(R.id.bs5);
        bs5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=xkBAiu8MmlM"));
                startActivity(intent);
            }
        });
        // 보충제
        bb1 = findViewById(R.id.bb1);
        bb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=EFM2I2icWqE"));
                startActivity(intent);
            }
        });
        bb2 = findViewById(R.id.bb2);
        bb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=qhD6wucVhso"));
                startActivity(intent);
            }
        });
        bb3 = findViewById(R.id.bb3);
        bb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=sgRLOXSOqNk"));
                startActivity(intent);
            }
        });
        bb4 = findViewById(R.id.bb4);
        bb4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=Ki_1jOjbxpU"));
                startActivity(intent);
            }
        });
        bb5 = findViewById(R.id.bb5);
        bb5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=Rbqm8YBbq4Q&t=286s"));
                startActivity(intent);
            }
        });

        final UserAccount[] userInfo = {new UserAccount()};
        //데이터 읽기
        mDatabaseRef.child("project").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                weight = 0;
                height = 0;
                age = 0;
                gender = "man";
                if(gender.equals("man")){
                    BMR = (13.397*weight) + (4.799*height) - (5.677*age) + 88.362;
                }else if(gender.equals("woman")){
                    BMR =  (9.247*weight) + (3.098*height) - (4.330*age) + 447.593;
                }

                bulkup = (BMR*1.4)*1.2;

                String bulkup2 = String.format("%.0f", bulkup);
                String BMR2 = String.format("%.0f", BMR);

                basicText = findViewById(R.id.basic);
                basicText.setText("기초                                 "+
                        "                                         "+BMR2); //기초대사량
                recomText = findViewById(R.id.recommend);
                recomText.setText("권장                                 "+
                        "                                         "+bulkup2); //권장섭취량

                basicBar = findViewById(R.id.basicBar);
                basicBar.setProgress(Integer.parseInt(BMR2));
                recomBar = findViewById(R.id.recomBar);
                recomBar.setProgress(Integer.parseInt(bulkup2));
            }

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo[0] = snapshot.getValue(UserAccount.class);
                if(userInfo[0] == null || userInfo[0].equals(null)){
                    weight = 0;
                    height = 0;
                    age = 0;
                    gender = "man";
                    if(gender.equals("man")){
                        BMR = (13.397*weight) + (4.799*height) - (5.677*age) + 88.362;
                    }else if(gender.equals("woman")){
                        BMR =  (9.247*weight) + (3.098*height) - (4.330*age) + 447.593;
                    }

                    bulkup = (BMR*1.4)*1.2;

                    String bulkup2 = String.format("%.0f", bulkup);
                    String BMR2 = String.format("%.0f", BMR);

                    basicText = findViewById(R.id.basic);
                    basicText.setText("기초                                 "+
                            "                                         "+BMR2); //기초대사량
                    recomText = findViewById(R.id.recommend);
                    recomText.setText("권장                                 "+
                            "                                         "+bulkup2); //권장섭취량

                    basicBar = findViewById(R.id.basicBar);
                    basicBar.setProgress(Integer.parseInt(BMR2));
                    recomBar = findViewById(R.id.recomBar);
                    recomBar.setProgress(Integer.parseInt(bulkup2));
                }
                else {
                    weight = userInfo[0].getWeight();
                    height = userInfo[0].getHeight();
                    age = userInfo[0].getAge();
                    gender = userInfo[0].getGender();
                    if(gender.equals("man")){
                        BMR = (13.397*weight) + (4.799*height) - (5.677*age) + 88.362;
                    }else if(gender.equals("woman")){
                        BMR =  (9.247*weight) + (3.098*height) - (4.330*age) + 447.593;
                    }

                    bulkup = (BMR*1.4)*1.2;

                    String bulkup2 = String.format("%.0f", bulkup);
                    String BMR2 = String.format("%.0f", BMR);

                    basicText = findViewById(R.id.basic);
                    basicText.setText("기초                                 "+
                            "                                         "+BMR2); //기초대사량
                    recomText = findViewById(R.id.recommend);
                    recomText.setText("권장                                 "+
                            "                                         "+bulkup2); //권장섭취량

                    basicBar = findViewById(R.id.basicBar);
                    basicBar.setProgress(Integer.parseInt(BMR2));
                    recomBar = findViewById(R.id.recomBar);
                    recomBar.setProgress(Integer.parseInt(bulkup2));
                }
            }
        });
    }

    // 이름 변경을 위한 메소드
    private void read() {

        final UserAccount[] userInfo = {new UserAccount()};
        //데이터 읽기
        mDatabaseRef.child("project").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                weight = 0;
                height = 0;
                age = 0;
                gender = "man";
            }

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo[0] = snapshot.getValue(UserAccount.class);
                if(userInfo[0] == null || userInfo[0].equals(null)){
                    weight = 0;
                    height = 0;
                    age = 0;
                    gender = "man";
                }
                else {
                    weight = userInfo[0].getWeight();
                    height = userInfo[0].getHeight();
                    age = userInfo[0].getAge();
                    gender = userInfo[0].getGender();
                }
            }
        });
        /*if(userInfo[0].getName() == null || userInfo[0].getName().length() == 0)
            welcome.setText("회원정보를 불러오지 못했습니다.");
        else if (userInfo[0].getDogName().equals(""))
            tvDogName.setText(userInfo[0].getDogName());*/
    }
}