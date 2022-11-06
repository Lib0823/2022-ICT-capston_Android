package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    //int version = 1;
    //DatabaseOpenHelper helper;
    //SQLiteDatabase database;

    EditText idEditText;
    EditText pwEditText;
    Button btnLogin;
    TextView btnJoin;
    TextView btnFindPw;

    // SQLite
    int version = 1;
    DatabaseOpenHelper helper;
    SQLiteDatabase database;

    String sql;
    Cursor cursor;

    private String userId;
    private DatabaseReference mDatabaseReference;   // 파이어베이스 실시간 DB
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase mFirebaseDB;
    private DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); // 방금 로그인 성공한 유저의 정보를 가져오는 객체
    private UserAccount account = new UserAccount();        // 입력전용 객체
    private String date3;
    private String startDay;
    private String matchDay;
    private String battleid;
    private String comDate="날짜 에러";
    private int run = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //DataBase연결부분
        helper = new DatabaseOpenHelper(LoginActivity.this, DatabaseOpenHelper.tableRun, null, version);
        database = helper.getWritableDatabase();


        idEditText = (EditText) findViewById(R.id.idEditText);
        pwEditText = (EditText) findViewById(R.id.pwEditText);

        btnLogin = findViewById(R.id.btnLogin);
        btnJoin = findViewById(R.id.btnJoin);
        btnFindPw = findViewById(R.id.btnFindPw);
        //helper = new DatabaseOpenHelper(LoginActivity.this, DatabaseOpenHelper.tableName, null, version);
        //database = helper.getWritableDatabase();

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String id = idEditText.getText().toString();
                String pw = pwEditText.getText().toString();

                if(id.length() == 0 || pw.length() == 0) {
                    //아이디와 비밀번호를 입력해주세요.
                    Toast toast = Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                mFirebaseAuth.signInWithEmailAndPassword(id, pw).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            // 로그인 성공 시.

                            final UserAccount[] userInfo = {new UserAccount()};
                            //데이터 읽기
                            mDatabaseRef.child("project").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    userInfo[0] = snapshot.getValue(UserAccount.class);
                                    if(userInfo[0] == null || userInfo[0].getDate() == null || userInfo[0].getDate().length() == 0 || userInfo[0].equals(null))
                                        date3 = "날짜 에러";
                                    else {
                                        date3 = userInfo[0].getDate();
                                        // 현재 날짜 가져오기
                                        long now = System.currentTimeMillis();
                                        Date date = new Date(now);
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
                                        String date2 = sdf.format(date);

                                        SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
                                        int dayday = Integer.parseInt(sdfDay.format(date));

                                        final UserAccount[] userInfo = {new UserAccount()};
                                        //데이터 읽기
                                        mDatabaseRef.child("project").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                                                run = 0;
                                            }

                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                userInfo[0] = snapshot.getValue(UserAccount.class);
                                                if(userInfo[0] == null || userInfo[0].equals(null))
                                                    run = 0;
                                                else {
                                                    run = userInfo[0].getRun();
                                                    if(run == 1) {
                                                        //데이터 읽기
                                                        mDatabaseRef.child("battle").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                // 매치일 비교 후 런값 변경
                                                                BattleInfo battleInfos1 = snapshot.getValue(BattleInfo.class);
                                                                if (battleInfos1 == null || battleInfos1.equals(null))
                                                                    comDate = "날짜 에러";
                                                                else {
                                                                    comDate = battleInfos1.getMatchDay();
                                                                    int compare = comDate.compareTo(date2);
                                                                    // "today가 date보다 큽니다.(date < today)"
                                                                    if (compare < 0) {
                                                                        Map<String, Object> taskMap = new HashMap<String, Object>();
                                                                        taskMap.put("run", 0);
                                                                        mDatabaseRef.child("project").child(firebaseUser.getUid()).updateChildren(taskMap);
                                                                    }
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                                                                comDate = "날짜 에러";
                                                            }
                                                        });
                                                    }
                                                    Log.d("date2", date2);
                                                    Log.d("date3", date3);
                                                    if(!date3.equals(date2)){ // 저장되있던 날짜와 현재 날짜가 다르다면 실행
                                                        Map<String, Object> taskMap1 = new HashMap<String, Object>();
                                                        taskMap1.put("date", date2);

                                                        mDatabaseRef.child("project").child(firebaseUser.getUid()).updateChildren(taskMap1);


                                                        PointInfo pointInfo = new PointInfo();
                                                        int p = 0;
                                                        pointInfo.setPoint(p);

                                                        // setValue : DB 하위주소(UserAccount)에 정보를 삽입함. (2022-10-21 이수)
                                                        mDatabaseRef.child("point").child(firebaseUser.getUid()).child(date2).setValue(pointInfo);

//                                //Run값 저장
//                                int point = 0;
//                                helper.insertRun(database, date2, id, point);
                                                    }
                                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                                    //.putExtra("text", inputId);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        });

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                                    date3 = "날짜 에러";
                                }

                            });

                            //String resultDay = startDay.substring(8);    // 기온
                            //Log.d("제발", resultDay);
//                            int sumday = Integer.parseInt(resultDay) + matchDay;
//                            if(dayday >= sumday) { // 대결일이 지났다면
//                                // 본인 아이디의 ?일 동안의 포인트값을 가져오고
//                                int myTotalPoint = 0;
//                                for(int i=0;i<matchDay;i++){
//                                    sql = "SELECT point FROM "+helper.tableRun+" WHERE date='"+(startDay+i)+"' AND id='"+userId+"'";
//                                    cursor = database.rawQuery(sql, null);
//                                    cursor.moveToNext();
//                                    myTotalPoint += Integer.parseInt(cursor.getString(0));
//                                }
//
//                                // (본인 아이디와 같은 대결아이디)를 가진 상대 아이디의 ?일 동안의 포인트값을 가져와서
//                                idbattleread();
//                                //상대 아이디 = battleid
//                                int opTotalPoint = 0;
//                                for(int i=0;i<matchDay;i++){
//                                    sql = "SELECT point FROM "+helper.tableRun+" WHERE date='"+(startDay+i)+"' AND id='"+battleid+"'";
//                                    cursor = database.rawQuery(sql, null);
//                                    cursor.moveToNext();
//                                    opTotalPoint += Integer.parseInt(cursor.getString(0));
//                                }
//
//                                // 비교하여 자신이 더 크면 승리, 작으면 패배
//                            }
//                            idread();
//                            battleread();

                        } else {
                            Toast toast = Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다. 정보를 확인해주세요.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });

                /*sql = "SELECT id FROM "+ helper.tableName + " WHERE id = '" + id + "'";
                cursor = database.rawQuery(sql, null);

                if(cursor.getCount() != 1){
                    //아이디가 틀렸습니다.
                    Toast toast = Toast.makeText(LoginActivity.this, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                sql = "SELECT pw FROM "+ helper.tableName + " WHERE id = '" + id + "'";
                cursor = database.rawQuery(sql, null);

                cursor.moveToNext();
                if(!pw.equals(cursor.getString(0))){
                    //비밀번호가 틀렸습니다.
                    Toast toast = Toast.makeText(LoginActivity.this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    //로그인성공
                    Toast toast = Toast.makeText(LoginActivity.this, "로그인성공", Toast.LENGTH_SHORT);
                    toast.show();
                    //Run값 초기화
                    database.execSQL("UPDATE Users SET " +
                            "run=0 WHERE id ='" + id + "'");
                    //login값 초기화 / 세팅
                    database.execSQL("UPDATE Users SET " +
                            "login='0' WHERE NOT id ='" + id + "'");
                    database.execSQL("UPDATE Users SET " +
                            "login='1' WHERE id ='" + id + "'");
                    //인텐트 생성 및 호출
                    String inputId = idEditText.getText().toString();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("text", inputId);
                    startActivity(intent);
                    finish();
                }
                cursor.close();*/

            }
        });

        btnJoin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //회원가입 버튼 클릭
                Toast toast = Toast.makeText(LoginActivity.this, "회원가입 화면으로 이동", Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(getApplicationContext(),JoinActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        btnFindPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //비밀번호 재설정 버튼 클릭
                Toast toast = Toast.makeText(LoginActivity.this, "비밀번호 재설정 화면으로 이동", Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(LoginActivity.this,FindPWActivity.class);
                startActivity(intent);
            }
        });

    }
    // 날짜 확인을 위한 메소드
    private void dateRead() {
        final UserAccount[] userInfo = {new UserAccount()};
        //데이터 읽기
        mDatabaseRef.child("project").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo[0] = snapshot.getValue(UserAccount.class);
                if(userInfo[0] == null || userInfo[0].getDate() == null || userInfo[0].getDate().length() == 0 || userInfo[0].equals(null))
                    date3 = "날짜 에러";
                else
                    date3 = userInfo[0].getDate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                date3 = "날짜 에러";
            }
        });
        /*if(userInfo[0].getName() == null || userInfo[0].getName().length() == 0)
            welcome.setText("회원정보를 불러오지 못했습니다.");
        else if (userInfo[0].getDogName().equals(""))
            tvDogName.setText(userInfo[0].getDogName());*/
    }

    // 이름 변경을 위한 메소드
    private void idread() {

        final UserAccount[] userInfo = {new UserAccount()};
        //데이터 읽기
        mDatabaseRef.child("project").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                userId = "아이디 에러";
            }

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo[0] = snapshot.getValue(UserAccount.class);
                if(userInfo[0] == null || userInfo[0].getId() == null || userInfo[0].getId().length() == 0 || userInfo[0].equals(null))
                    userId = "아이디 에러";
                else
                    userId = userInfo[0].getId();
            }
        });
        /*if(userInfo[0].getName() == null || userInfo[0].getName().length() == 0)
            welcome.setText("회원정보를 불러오지 못했습니다.");
        else if (userInfo[0].getDogName().equals(""))
            tvDogName.setText(userInfo[0].getDogName());*/
    }

    // 이름 변경을 위한 메소드
    private void battleread() {

        final BattleInfo[] battleInfos = {new BattleInfo()};
        //데이터 읽기
        mDatabaseRef.child("battle").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출

            }

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                battleInfos[0] = snapshot.getValue(BattleInfo.class);
                if(battleInfos[0] != null || !battleInfos[0].equals(null)) {
                    // startDay, matchDay
                    startDay = battleInfos[0].getStartDay();
                    matchDay = battleInfos[0].getMatchDay();
                }
            }
        });
        /*if(userInfo[0].getName() == null || userInfo[0].getName().length() == 0)
            welcome.setText("회원정보를 불러오지 못했습니다.");
        else if (userInfo[0].getDogName().equals(""))
            tvDogName.setText(userInfo[0].getDogName());*/
    }

    // 이름 변경을 위한 메소드
    private void idbattleread() {

        final BattleInfo[] battleInfos = {new BattleInfo()};
        //데이터 읽기
        mDatabaseRef.child("battle").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출

            }

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                battleInfos[0] = snapshot.getValue(BattleInfo.class);
                if(battleInfos[0] != null || !battleInfos[0].equals(null)) {
                    // startDay, matchDay
                    battleid = battleInfos[0].getOpid();
                }
            }
        });
        /*if(userInfo[0].getName() == null || userInfo[0].getName().length() == 0)
            welcome.setText("회원정보를 불러오지 못했습니다.");
        else if (userInfo[0].getDogName().equals(""))
            tvDogName.setText(userInfo[0].getDogName());*/
    }
}