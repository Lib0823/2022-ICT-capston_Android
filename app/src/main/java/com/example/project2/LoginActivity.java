package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    Button btnJoin;

    //String sql;
    //Cursor cursor;

    private DatabaseReference mDatabaseReference;   // 파이어베이스 실시간 DB
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase mFirebaseDB;
    private DatabaseReference mDatabaseRef = mFirebaseDB.getInstance().getReference();
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); // 방금 로그인 성공한 유저의 정보를 가져오는 객체
    private UserAccount account = new UserAccount();        // 입력전용 객체
    private String date3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();


        idEditText = (EditText) findViewById(R.id.idEditText);
        pwEditText = (EditText) findViewById(R.id.pwEditText);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnJoin = (Button) findViewById(R.id.btnJoin);

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
                            //인텐트 생성 및 호출
                            //String inputId = idEditText.getText().toString();

                            // DB저장 날짜 가져오기
                            dateRead(); // date1

                            // 현재 날짜 가져오기
                            long now = System.currentTimeMillis();
                            Date date = new Date(now);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
                            String date2 = sdf.format(date);

                            if(date3 != date2){
                                Map<String, Object> taskMap1 = new HashMap<String, Object>();
                                taskMap1.put("date", date2);
                                Map<String, Object> taskMap2 = new HashMap<String, Object>();
                                taskMap2.put("run", 0);
                                mDatabaseRef.child("project").child(firebaseUser.getUid()).updateChildren(taskMap1);
                                mDatabaseRef.child("project").child(firebaseUser.getUid()).updateChildren(taskMap2);
                            }
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            //.putExtra("text", inputId);
                            startActivity(intent);
                            finish();
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
}