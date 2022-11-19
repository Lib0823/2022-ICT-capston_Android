package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
    private EditText idEditText;
    private EditText pwEditText;
    private Button btnLogin;
    private TextView btnJoin, btnFindPw;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();                              // 파이어베이스 데이터베이스 연동
    private DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();                           // 방금 로그인 성공한 유저의 정보를 가져오는 객체
    private String date3;
    private String comDate="날짜 에러";
    private int run = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 로그인화면 이미지 gif
        ImageView intro = (ImageView)findViewById(R.id.introImage);
        Glide.with(this).load(R.mipmap.intro_mobile_).into(intro);

        // xml 객체 초기화
        idEditText = (EditText) findViewById(R.id.idEditText);
        pwEditText = (EditText) findViewById(R.id.pwEditText);
        btnLogin = findViewById(R.id.btnLogin);
        btnJoin = findViewById(R.id.btnJoin);
        btnFindPw = findViewById(R.id.btnFindPw);

        // Request For GPS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // 로그인 버튼
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String id = idEditText.getText().toString();
                String pw = pwEditText.getText().toString();
                
                //아이디와 비밀번호를 입력해주세요.
                if(id.length() == 0 || pw.length() == 0) {
                    Toast toast = Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                    // 로그인 시도
                    mFirebaseAuth.signInWithEmailAndPassword(id, pw).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // 로그인 성공 시.
                            if (task.isSuccessful()) {
                                if (firebaseUser != null) {
                                    // 이용자 정보 가져오는 객체
                                    final UserAccount[] userInfo = {new UserAccount()};
                                    mDatabaseRef.child("project").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            userInfo[0] = snapshot.getValue(UserAccount.class);
                                            if (userInfo[0] == null || userInfo[0].getDate() == null || userInfo[0].getDate().length() == 0 || userInfo[0].equals(null))
                                                date3 = "날짜 에러";
                                            else {
                                                date3 = userInfo[0].getDate();

                                                // 현재 날짜 가져오기
                                                long now = System.currentTimeMillis();
                                                Date date = new Date(now);
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
                                                String date2 = sdf.format(date);

                                                // run값 가져오기
                                                mDatabaseRef.child("project").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                                                        run = 0;
                                                    }

                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        userInfo[0] = snapshot.getValue(UserAccount.class);
                                                        if (userInfo[0] == null || userInfo[0].equals(null))
                                                            run = 0;
                                                        else {
                                                            run = userInfo[0].getRun();

                                                            if (run == 1) {
                                                                //데이터 읽기
                                                                mDatabaseRef.child("battle").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        // 매치일 비교 후, 매치일이 지난 경우 -> 런값 0으로 변경
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

                                                            // 저장되있던 날짜와 현재 날짜가 다르다면 실행
                                                            if (!date3.equals(date2)) {
                                                                Map<String, Object> taskMap1 = new HashMap<String, Object>();
                                                                taskMap1.put("date", date2);
                                                                mDatabaseRef.child("project").child(firebaseUser.getUid()).updateChildren(taskMap1);

                                                                // 만약 당일 처음으로 방문한 이용자라면 포인트 0으로 리셋.
                                                                PointInfo pointInfo = new PointInfo();
                                                                int p = 0;
                                                                pointInfo.setPoint(p);

                                                                // setValue : DB 하위주소(UserAccount)에 정보를 삽입함. (2022-10-21 이수)
                                                                mDatabaseRef.child("point").child(firebaseUser.getUid()).child(date2).setValue(pointInfo);
                                                            }

                                                            // 로그인 처리 완료... 화면전환(고생이 많네...)
                                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
                                } else {
                                    Toast toast = Toast.makeText(LoginActivity.this, "서버의 전원이 꺼져있습니다. 서버를 확인해주세요.", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            } else {
                                Toast toast = Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다. 정보를 확인해주세요.", Toast.LENGTH_SHORT);
                                toast.show();
                        }
                    }
                });
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
}