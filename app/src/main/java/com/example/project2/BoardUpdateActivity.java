package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class BoardUpdateActivity extends AppCompatActivity {
    private TextView tv_date, tv_name;
    private EditText et_content, et_title;
    private Button btn_update;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseRef = mFirebaseDB.getInstance().getReference();
    private DatabaseReference mDatabaseRef1 = FirebaseDatabase.getInstance().getReference();      // 파이어베이스 DB에 저장시킬 상위 주소위치
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); // 방금 로그인 성공한 유저의 정보를 가져오는 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boardupdate);

        et_title = findViewById(R.id.et_title);
        tv_date = findViewById(R.id.tv_date);
        tv_name = findViewById(R.id.tv_name);
        et_content = findViewById(R.id.et_content);

        Intent intent = getIntent(); /*데이터 수신*/
        String name = intent.getExtras().getString("name");
        String content = intent.getExtras().getString("content");
        String title = intent.getExtras().getString("title");
        String date = intent.getExtras().getString("date");
        String idToken = intent.getExtras().getString("idToken");
        String field = intent.getExtras().getString("field");

        tv_name.setText(name);
        tv_date.setText(date);
        et_title.setText(title);
        et_content.setText(content);

        btn_update = findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = String.valueOf(et_title.getText());
                String content = String.valueOf(et_content.getText());
                Map<String, Object> taskMap1 = new HashMap<String, Object>();
                taskMap1.put("title", title);
                mDatabaseRef.child("board").child(field).getParent().updateChildren(taskMap1);

                Map<String, Object> taskMap2 = new HashMap<String, Object>();
                taskMap2.put("content", content);
                mDatabaseRef.child("board").child(field).getParent().updateChildren(taskMap2);

                Toast toast = Toast.makeText(BoardUpdateActivity.this, "게시물 수정이 완료되었습니다.", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        });

    }

}