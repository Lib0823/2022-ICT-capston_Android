package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InfoActivity extends AppCompatActivity {
    private long backBtnTime = 0;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase mFirebaseDB;
    private DatabaseReference mDatabaseRef = mFirebaseDB.getInstance().getReference();
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); // 방금 로그인 성공한 유저의 정보를 가져오는 객체
    private UserAccount[] userInfo = {new UserAccount()};   // 출력전용 객체
    private UserAccount account = new UserAccount();        // 입력전용 객체
    private ArrayList<UserAccount> arrayList;
    private String date, gender, idToken;
    private int run;


    int version = 1;
    DatabaseOpenHelper helper;
    SQLiteDatabase database;

    String sql;
    Cursor cursor;

    Button infoUpdate;
    TextView textLogout, textDelete;
    EditText idEdit, pwEdit, nameEdit, ageEdit, heightEdit, weightEdit, genderEditText;
    private BottomNavigationView bottomNavi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        /*DataBase연결부분
        helper = new DatabaseOpenHelper(InfoActivity.this, DatabaseOpenHelper.tableName, null, version);
        database = helper.getWritableDatabase();

        sql = "SELECT * FROM "+ helper.tableName + " WHERE login = '1'";
        cursor = database.rawQuery(sql, null);

        cursor.moveToNext();   // 첫번째에서 다음 레코드가 없을때까지 읽음
        String id = cursor.getString(0);
        String pw = cursor.getString(1);
        String name = cursor.getString(2);
        String age = cursor.getString(3);
        String height = cursor.getString(4);
        String weight = cursor.getString(5);
        String gender = cursor.getString(6);
        idEdit.setText(id); ageEdit.setText(age); genderEditText.setText(gender); heightEdit.setText(height);
        pwEdit.setText(pw);nameEdit.setText(name);weightEdit.setText(weight);
         */
        idEdit = findViewById(R.id.idEditText);
        pwEdit = findViewById(R.id.pwEditText);
        nameEdit = findViewById(R.id.nameEditText);
        ageEdit = findViewById(R.id.ageEditText);
        heightEdit = findViewById(R.id.heightEditText);
        weightEdit = findViewById(R.id.weightEditText);
        genderEditText = findViewById(R.id.genderEditText);
        read();


        // 이 친구의 역활 물어보기!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 아마 하단버튼?
        // 하단 네비게이션바 맞는데 역할 입니다 행님
        bottomNavi = findViewById(R.id.bottonNavi);
        bottomNavi.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        Intent intent2 = new Intent(InfoActivity.this, MainActivity.class);
                        startActivity(intent2);
                        overridePendingTransition(0, 0);
                        finish();
                        break;
                    case R.id.action_fitness:
                        Intent intent = new Intent(InfoActivity.this, FitnessActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        finish();
                        break;
                    case R.id.action_board:
                        Intent intent3 = new Intent(InfoActivity.this, BoardActivity.class);
                        startActivity(intent3);
                        overridePendingTransition(0, 0);
                        finish();
                        break;
                    case R.id.action_info:
                        break;
                }
                return true;
            }
        });

        //정보수정
        infoUpdate = (Button) findViewById(R.id.btnUpdate);
        infoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer age = Integer.parseInt(ageEdit.getText().toString()); //정수값 가져오기
                Integer height = Integer.parseInt(heightEdit.getText().toString());
                Integer weight = Integer.parseInt(weightEdit.getText().toString());
                /*database.execSQL("UPDATE Users SET " +
                        "age="+age+", height="+height+", weight="+weight+
                        " WHERE login ='1'");*/

                if(age==null || height==null || weight==null ) {
                    Toast toast = Toast.makeText(InfoActivity.this, "정보를 다시 확인해주세요.", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Map<String, Object> taskMap1 = new HashMap<String, Object>();
                    taskMap1.put("age", age);
                    mDatabaseRef.child("project").child(firebaseUser.getUid()).updateChildren(taskMap1);
                    Map<String, Object> taskMap2 = new HashMap<String, Object>();
                    taskMap2.put("height", height);
                    mDatabaseRef.child("project").child(firebaseUser.getUid()).updateChildren(taskMap2);
                    Map<String, Object> taskMap3 = new HashMap<String, Object>();
                    taskMap3.put("weight", weight);
                    mDatabaseRef.child("project").child(firebaseUser.getUid()).updateChildren(taskMap3);

                    Toast toast = Toast.makeText(InfoActivity.this, "정보가 수정되었습니다.", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });

        //회원탈퇴
        textDelete = findViewById(R.id.textDelete);
        textDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*database.execSQL("DELETE FROM Users"+
                        " WHERE login ='1'");*/
                mFirebaseAuth.getCurrentUser().delete();
                Toast toast = Toast.makeText(InfoActivity.this, "회원정보가 삭제되었습니다", Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(InfoActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //로그아웃
        textLogout = findViewById(R.id.textLogout);
        textLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(InfoActivity.this, "로그아웃 되었습니다", Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(InfoActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    // 회원정보를 불러오기 위한 메소드
    private void read() {
        ArrayList<UserAccount> arrayList = new ArrayList<>();
        final UserAccount[] userInfo = {new UserAccount()};
        //데이터 읽기
        mDatabaseRef.child("project").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo[0] = snapshot.getValue(UserAccount.class);
                //arrayList.clear();
                if(userInfo[0] == null || userInfo[0].getName() == null || userInfo[0].getName().length() == 0 || userInfo[0].equals(null)) {
                    ageEdit.setText("1");
                    heightEdit.setText("");
                    idEdit.setText("");
                    nameEdit.setText("");
                    pwEdit.setText("");
                    weightEdit.setText("");
                }
                else{
                    //for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                       // UserAccount user = snapshot1.getValue(UserAccount.class); // 만들어뒀던 유저 객체에 데이터 담기!
                        //arrayList.add(user);    // 담은 데이터 배열리스트에 저장.
                    //}
                    /*ageEdit.setText((CharSequence) arrayList.get(0));
                    date = String.valueOf( arrayList.get(1));
                    gender = String.valueOf(arrayList.get(2));
                    heightEdit.setText((CharSequence) arrayList.get(3));
                    idEdit.setText((CharSequence) arrayList.get(4));
                    idToken = String.valueOf(arrayList.get(5));
                    nameEdit.setText((CharSequence) arrayList.get(6));
                    pwEdit.setText((CharSequence) arrayList.get(7));
                    run = Integer.parseInt(String.valueOf(arrayList.get(8)));
                    weightEdit.setText((CharSequence) arrayList.get(9));

                    for(int i = 0; i < 10; i++) {
                        Log.d("DB 정보 "+i, String.valueOf(arrayList.get(i)));
                    }*/

                    //Log.d( "정보", String.valueOf(userInfo[0].getAge()));
                    //idToken = String.valueOf(userInfo[0].getIdToken());
                    date = String.valueOf(userInfo[0].getDate());
                    gender = String.valueOf(userInfo[0].getGender());
                    run = userInfo[0].getRun();

                    ageEdit.setText(userInfo[0].getAge()+"");
                    heightEdit.setText(userInfo[0].getHeight()+"");
                    idEdit.setText(userInfo[0].getId());
                    nameEdit.setText(userInfo[0].getName());
                    pwEdit.setText(userInfo[0].getPw());
                    weightEdit.setText(userInfo[0].getWeight()+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                ageEdit.setText("");
                heightEdit.setText("");
                idEdit.setText("");
                nameEdit.setText("");
                pwEdit.setText("");
                weightEdit.setText("");
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