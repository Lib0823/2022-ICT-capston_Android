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

public class LeanmassActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase mFirebaseDB;
    private DatabaseReference mDatabaseRef = mFirebaseDB.getInstance().getReference();
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); // 방금 로그인 성공한 유저의 정보를 가져오는 객체
    private static int height, weight, age;
    private static String gender;

    private ImageButton lf1, lf2, lf3, lf4, lf5;
    private ImageButton ls1, ls2, ls3, ls4, ls5;
    private ImageButton lb1, lb2, lb3, lb4, lb5;

    int version = 1;
    DatabaseOpenHelper helper;
    SQLiteDatabase database;

    String sql;
    Cursor cursor;

    Double BMR, leanmass;
    TextView basicText, recomText;
    ProgressBar basicBar, recomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leanmass);

        // imageButton
        // 운동
        lf1 = findViewById(R.id.lf1);
        lf1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=tPVzsTr7ULw"));
                startActivity(intent);
            }
        });
        lf2 = findViewById(R.id.lf2);
        lf2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=V-Pg-MrVBeE"));
                startActivity(intent);
            }
        });
        lf3 = findViewById(R.id.lf3);
        lf3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=wbAZY5HPsbc"));
                startActivity(intent);
            }
        });
        lf4 = findViewById(R.id.lf4);
        lf4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=H4AZO-KiAPo"));
                startActivity(intent);
            }
        });
        lf5 = findViewById(R.id.lf5);
        lf5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=gTofe6nrSwA"));
                startActivity(intent);
            }
        });
        // 식단
        ls1 = findViewById(R.id.ls1);
        ls1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=WHqi90lNeX8&t=147s"));
                startActivity(intent);
            }
        });
        ls2 = findViewById(R.id.ls2);
        ls2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=w4rkqonvCSA"));
                startActivity(intent);
            }
        });
        ls3 = findViewById(R.id.ls3);
        ls3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=MWH8g6hCiWM"));
                startActivity(intent);
            }
        });
        ls4 = findViewById(R.id.ls4);
        ls4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=ApobDUjr7xc"));
                startActivity(intent);
            }
        });
        ls5 = findViewById(R.id.ls5);
        ls5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=aJWMx6NbmO0&t=282s"));
                startActivity(intent);
            }
        });
        // 보충제
        lb1 = findViewById(R.id.lb1);
        lb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=k8KUr8LzaaA"));
                startActivity(intent);
            }
        });
        lb2 = findViewById(R.id.lb2);
        lb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=Dj3kYRr5zDo&t=779s"));
                startActivity(intent);
            }
        });
        lb3 = findViewById(R.id.lb3);
        lb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=RpZ6RGd0JHU"));
                startActivity(intent);
            }
        });
        lb4 = findViewById(R.id.lb4);
        lb4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=TulXF8GiReA"));
                startActivity(intent);
            }
        });
        lb5 = findViewById(R.id.lb5);
        lb5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=AGcu3d1-suw"));
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

                leanmass = BMR*1.4;

                String leanmass2 = String.format("%.0f", leanmass);
                String BMR2 = String.format("%.0f", BMR);

                basicText = findViewById(R.id.basic);
                basicText.setText("기초                                 "+
                        "                                         "+BMR2); //기초대사량
                recomText = findViewById(R.id.recommend);
                recomText.setText("권장                                 "+
                        "                                         "+leanmass2); //권장섭취량

                basicBar = findViewById(R.id.basicBar);
                basicBar.setProgress(Integer.parseInt(BMR2));
                recomBar = findViewById(R.id.recomBar);
                recomBar.setProgress(Integer.parseInt(leanmass2));
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
                    if(gender.equals("man")){
                        BMR = (13.397*weight) + (4.799*height) - (5.677*age) + 88.362;
                    }else if(gender.equals("woman")){
                        BMR =  (9.247*weight) + (3.098*height) - (4.330*age) + 447.593;
                    }

                    leanmass = BMR*1.4;

                    String leanmass2 = String.format("%.0f", leanmass);
                    String BMR2 = String.format("%.0f", BMR);

                    basicText = findViewById(R.id.basic);
                    basicText.setText("기초                                 "+
                            "                                         "+BMR2); //기초대사량
                    recomText = findViewById(R.id.recommend);
                    recomText.setText("권장                                 "+
                            "                                         "+leanmass2); //권장섭취량

                    basicBar = findViewById(R.id.basicBar);
                    basicBar.setProgress(Integer.parseInt(BMR2));
                    recomBar = findViewById(R.id.recomBar);
                    recomBar.setProgress(Integer.parseInt(leanmass2));
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