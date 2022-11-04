package com.example.project2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private long backBtnTime = 0;

    public ImageButton moveCal, run;

    private TextView welcome;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase mFirebaseDB;
    private DatabaseReference mDatabaseRef = mFirebaseDB.getInstance().getReference();
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); // 방금 로그인 성공한 유저의 정보를 가져오는 객체
    private int run1;
    private ImageButton developer, battleBtn;

    private BottomNavigationView bottomNavi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        welcome = findViewById(R.id.welcomeMessage);

        battleBtn = findViewById(R.id.battleBtn);
        battleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BattleActivity.class);
                startActivity(intent);
            }
        });

        developer = findViewById(R.id.developer);
        developer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Copyright 2022.").setMessage("LeeSoo, LeeInbeom All rights reserved.");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        read();

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

        runRead();

        /*// 러닝 프로그래스바
        sql = "SELECT * FROM "+ helper.tableName + " WHERE login = '1'";
        cursor = database.rawQuery(sql, null);
        cursor.moveToNext();
        int run = Integer.parseInt(cursor.getString(7));*/

        ProgressBar progress = (ProgressBar) findViewById(R.id.progress) ;
        progress.setProgress(run1) ;
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
    // 이름 변경을 위한 메소드
    private void read() {

        final UserAccount[] userInfo = {new UserAccount()};
        //데이터 읽기
        mDatabaseRef.child("project").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo[0] = snapshot.getValue(UserAccount.class);
                if(userInfo[0] == null || userInfo[0].getName() == null || userInfo[0].getName().length() == 0 || userInfo[0].equals(null))
                    welcome.setText("회원정보를 불러오지 못했습니다.");
                else

                    welcome.setText(userInfo[0].getName() + "님 \nToday's Work Out");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                welcome.setText("회원정보를 불러오지 못했습니다.");
            }
        });
        /*if(userInfo[0].getName() == null || userInfo[0].getName().length() == 0)
            welcome.setText("회원정보를 불러오지 못했습니다.");
        else if (userInfo[0].getDogName().equals(""))
            tvDogName.setText(userInfo[0].getDogName());*/
    }
    // run을 위한 메소드
    private void runRead() {

        final UserAccount[] userInfo = {new UserAccount()};
        //데이터 읽기
        mDatabaseRef.child("project").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo[0] = snapshot.getValue(UserAccount.class);
                if(userInfo[0] == null || userInfo[0].equals(null))
                    run1 = 0;
                else
                    run1 = userInfo[0].getRun();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                run1 = 0;
            }
        });
        /*if(userInfo[0].getName() == null || userInfo[0].getName().length() == 0)
            welcome.setText("회원정보를 불러오지 못했습니다.");
        else if (userInfo[0].getDogName().equals(""))
            tvDogName.setText(userInfo[0].getDogName());*/
    }

    @Override
    public void onBackPressed(){
        long curTime = System.currentTimeMillis();
        long gapTime = curTime- backBtnTime;

        if(0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed();
        } else {
            backBtnTime = curTime;
            Toast.makeText(this,"한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }
}