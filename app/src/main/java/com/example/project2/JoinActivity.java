package com.example.project2;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JoinActivity extends AppCompatActivity {

    EditText idEditText, pwEditText, nameEditText, ageEditText, heightEditText, weightEditText;
    Button btnJoin, btnBack;
    RadioGroup radioGender;
    String gender = "man";
    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증
    private DatabaseReference mDatabaseRef;   // 파이어베이스 실시간 DB
    private String date;

    int version = 1;
    //DatabaseOpenHelper helper;
    //SQLiteDatabase database;

    String sql;
    Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();      // 파이어베이스 DB에 저장시킬 상위 주소위치
        idEditText = (EditText) findViewById(R.id.idEditText);
        pwEditText = (EditText) findViewById(R.id.pwEditText);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        ageEditText = (EditText) findViewById(R.id.ageEditText);
        heightEditText = (EditText) findViewById(R.id.heightEditText);
        weightEditText = (EditText) findViewById(R.id.weightEditText);
        radioGender = (RadioGroup) findViewById(R.id.radioGender);
        radioGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int check) {
                if(check == R.id.man){
                    gender = "man";
                }
                if(check == R.id.woman){
                    gender = "woman";
                }
            }
        });

        btnJoin = (Button) findViewById(R.id.btnJoin);
        btnBack = (Button) findViewById(R.id.btnBack);

        //helper = new DatabaseOpenHelper(JoinActivity.this, DatabaseOpenHelper.tableName, null, version);
        //database = helper.getWritableDatabase();

        btnJoin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                /*// 항목 미 입력시 선언부에서 오류가 발생하기 때문에 미리 확인해줘야함

                if(idEditText.getText().length() == 0 || pwEditText.getText().length() == 0 || nameEditText.getText().length() == 0 ||
                        ageEditText.getText().length() == 0 || heightEditText.getText().length() == 0 || weightEditText.getText().length() == 0) {
                    //아이디, 비밀번호, 이름, 나이, 키, 몸무게를 모두 입력해야 합니다.
                    Toast toast = Toast.makeText(JoinActivity.this, "모든 항목을 입력해야 합니다.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                if(idEditText.getText().length() < 5) {
                    //아이디와 5자 이상 입력해야 합니다.
                    Toast toast = Toast.makeText(JoinActivity.this, "아이디를 5자 이상 입력해주세요.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                if(pwEditText.getText().length() < 7) {
                    //비밀번호는 7자 이상 입력해야 합니다.
                    Toast toast = Toast.makeText(JoinActivity.this, "비밀번호를 7자 이상 입력해주세요.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                if(!pwEditText.getText().toString().contains("@") && !pwEditText.getText().toString().contains("#")
                && !pwEditText.getText().toString().contains("%") && !pwEditText.getText().toString().contains("$")) {
                    //비밀번호는 특수문자를 포함해야 합니다.
                    Toast toast = Toast.makeText(JoinActivity.this, "비밀번호는 특수문자를 포함해야 합니다.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }*/

                String id = idEditText.getText().toString();
                String pw = pwEditText.getText().toString();
                String name = nameEditText.getText().toString();
                int age = Integer.parseInt(ageEditText.getText().toString());
                int height = Integer.parseInt(heightEditText.getText().toString());
                int weight = Integer.parseInt(weightEditText.getText().toString());

                // 현재 날짜 가져오기
                long now = System.currentTimeMillis();
                Date date1 = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
                date = sdf.format(date1);
                //int run = 0;
                int run = 0;
                Log.d("id", id);
                Log.d("pw", pw);
                Log.d("name", name);
                Log.d("age", id);
                Log.d("height", pw);
                Log.d("id", id);
                // 회원가입 처리 시작
                // Firebase Auth 진행
                mFirebaseAuth.createUserWithEmailAndPassword(id, pw).addOnCompleteListener(JoinActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("정보", "지나감");
                        // 회원가입 성공 시
                        if (task.isSuccessful()) {
                            // 회원가입 성공 시.
                            Log.d("여기", "저기");
                            // 방금 로그인 성공한 유저의 정보를 가져오는 객체
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            // 회원 정보 입력
                            UserAccount account = new UserAccount();

                            // DB 삽입 정보
                            account.setIdToken(firebaseUser.getUid());
                            account.setId(firebaseUser.getEmail());
                            account.setPw(pw);
                            account.setName(name);
                            account.setAge(age);
                            account.setHeight(height);
                            account.setWeight(weight);
                            account.setRun(run);
                            account.setDate(date);
                            account.setGender(gender);
                            account.getPedometer(0);

                            // setValue : DB 하위주소(UserAccount)에 정보를 삽입함. (2022-10-21 이수)
                            mDatabaseRef.child("project").child(firebaseUser.getUid()).setValue(account);

                            Toast toast = Toast.makeText(JoinActivity.this, "가입이 완료되었습니다. 로그인을 해주세요.", Toast.LENGTH_SHORT);
                            toast.show();
                            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // 회원가입 실패 시.
                            Toast toast = Toast.makeText(JoinActivity.this, "존재하는 아이디입니다.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });

                /*sql = "SELECT id FROM "+ helper.tableName + " WHERE id = '" + id + "'";
                cursor = database.rawQuery(sql, null);

                if(cursor.getCount() != 0){
                    //존재하는 아이디입니다.
                    Toast toast = Toast.makeText(JoinActivity.this, "존재하는 아이디입니다.", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    helper.insertUser(database,id, pw, name, age, height, weight, gender, run, login);
                    Toast toast = Toast.makeText(JoinActivity.this, "가입이 완료되었습니다. 로그인을 해주세요.", Toast.LENGTH_SHORT);
                    toast.show();
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                    finish();
                }*/
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //취소버튼 클릭
                Toast toast = Toast.makeText(JoinActivity.this, "회원가입 취소", Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}