package com.example.project2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class FindPWActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText editTextUserEmail;
    private Button btn_sendEmail; //, btn_moveLogin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);

        // 로그인화면 이미지 gif
        ImageView intro = (ImageView)findViewById(R.id.introImage);
        Glide.with(this).load(R.mipmap.intro_mobile_).into(intro);

        editTextUserEmail = (EditText) findViewById(R.id.et_email);
        btn_sendEmail = (Button) findViewById(R.id.btn_sendEmail);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        btn_sendEmail.setOnClickListener(this);

        /*// 로그인 화면으로 이동 버튼 수행 동작
        btn_moveLogin = findViewById(R.id.btn_moveLogin);
        btn_moveLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 마이페이지 화면으로 이동
                finish();
                Intent intent = new Intent(FindPWActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });*/
    }

    @Override
    public void onClick(View view) {
        String strEmail = editTextUserEmail.getText().toString();
        if(strEmail.equals("")){
            Toast.makeText(FindPWActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            if (view == btn_sendEmail) {
                progressDialog.setMessage("처리중입니다. 잠시 기다려 주세요...");
                progressDialog.show();

                //비밀번호 재설정 이메일 보내기
                String emailAddress = editTextUserEmail.getText().toString().trim();
                firebaseAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(FindPWActivity.this, "이메일을 보냈습니다.", Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        } else {
                            Toast.makeText(FindPWActivity.this, "메일 보내기 실패!", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
            }
        }
    }
}