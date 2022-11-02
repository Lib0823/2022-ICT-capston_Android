package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BoardActivity extends AppCompatActivity {

    private ListView list;
    private List<String> data;
    private ArrayAdapter<String> adapter;
    private BottomNavigationView bottomNavi, boardNavi;
    private Button contentBtn, searchBtn;
    private TextView contentText, searchText, boardTitle;
    private String id, contentId, content, field = "free", search;
    private final BoardInfo[] boardInfo = {new BoardInfo()};
    private BoardInfo bi;
    final static String[] name1 = {"회원"};
    private static String na = "";

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseRef = mFirebaseDB.getInstance().getReference();
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); // 방금 로그인 성공한 유저의 정보를 가져오는 객체

    int version = 1;
    //DatabaseOpenHelper helperBoard, helperUser;
    //SQLiteDatabase databaseBoard, databaseUser;

    //String sql;
    //Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        na = readName();
        //DB
        /*helperBoard = new DatabaseOpenHelper(BoardActivity.this, DatabaseOpenHelper.tableNameBoard, null, version);
        helperUser = new DatabaseOpenHelper(BoardActivity.this, DatabaseOpenHelper.tableName, null, version);
        databaseBoard = helperBoard.getWritableDatabase();
        databaseUser = helperUser.getWritableDatabase();

        sql = "SELECT id FROM "+ helperUser.tableName + " WHERE login = '1'";
        cursor = databaseUser.rawQuery(sql, null);
        cursor.moveToNext();   // 첫번째에서 다음 레코드가 없을때까지 읽음
       id = cursor.getString(0); */

        // 리스트 생성
        list = findViewById(R.id.list);
        data = new ArrayList<>();
        adapter = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1, data);
        list.setAdapter(adapter);
        list.setSelection(adapter.getCount() - 1);

        //sql = "SELECT id, content FROM "+ helperBoard.tableNameBoard + " WHERE field = '"+ field +"'";
        //cursor = databaseBoard.rawQuery(sql, null);

        /*while(cursor.moveToNext()){
            contentId = cursor.getString(0);
            content = cursor.getString(1);
            data.add(contentId+ " : " +content);
        }*/
        adapter.notifyDataSetChanged();
        list.setSelection(adapter.getCount() - 1);

        boardTitle = findViewById(R.id.boardTitle);

        field = "free";
        read(field);
        boardTitle.setText("자유 게시판");

        // Field변경 시 저장
        boardNavi = findViewById(R.id.boardNavi);
        boardNavi.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.free:
                        field = "free";
                        read(field);
                        boardTitle.setText("자유 게시판");
                        break;
                    case R.id.diet:
                        field = "diet";
                        read(field);
                        boardTitle.setText("다이어트 게시판");
                        break;
                    case R.id.lean:
                        field = "lean";
                        read(field);
                        boardTitle.setText("린매스업 게시판");
                        break;
                    case R.id.bulk:
                        field = "bulk";
                        read(field);
                        boardTitle.setText("벌크업 게시판");
                        break;
                }
                return true;
            }
        });

        contentText = findViewById(R.id.contentText);
        contentText.requestFocus();

        //등록버튼 클릭 시
        contentBtn = findViewById(R.id.contentBtn);
        contentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content = contentText.getText().toString();
                if(content.contains("ㅅㅂ") || content.contains("ㅂㅅ") || content.contains("ㅗ") || content.contains("fuck")){
                    // 비속어 필터링
                    Toast toast = Toast.makeText(BoardActivity.this, "비속어는 등록 할 수 없습니다.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                bi = new BoardInfo(readName(), content);
                contentText.setText("");
                // setValue : DB 하위주소(UserAccount)에 정보를 삽입함. (2022-10-21 이수)
                mDatabaseRef.child("board").child(field).push().setValue(bi);
                Toast.makeText(BoardActivity.this, "내용이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                read(field);
            }
        });

        searchBtn = findViewById(R.id.searchBtn);
        searchText = findViewById(R.id.searchText);

        //검색버튼 클릭 시
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search = searchText.getText().toString();
                data.clear();

                //데이터 읽기
                mDatabaseRef.child("board").child(field).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boardInfo[0] = snapshot.getValue(BoardInfo.class);
                        data.clear();

                        for(DataSnapshot ss : snapshot.getChildren()){
                            BoardInfo boardInfo1 = ss.getValue(BoardInfo.class);
                            contentId = boardInfo1 .getName();
                            content = boardInfo1 .getContent();
                            if(content.contains(search))
                                data.add(contentId + " : " + content);
                        }

                        adapter.notifyDataSetChanged();
                        list.setSelection(adapter.getCount() - 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                        data.clear();
                        contentId = " ";
                        content = " ";
                        data.add(contentId + content);
                        boardTitle.setText("게시글을 불러올 수가 없습니다.");
                    }
                });
            }
        });

        // 바텀 네비게이션
        bottomNavi = findViewById(R.id.bottonNavi);
        bottomNavi.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_home:
                        Intent intent = new Intent(BoardActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.action_fitness:
                        Intent intent2 = new Intent(BoardActivity.this, FitnessActivity.class);
                        startActivity(intent2);
                        finish();
                        break;
                    case R.id.action_board:
                        break;
                    case R.id.action_info:
                        Intent intent3 = new Intent(BoardActivity.this, InfoActivity.class);
                        startActivity(intent3);
                        finish();
                        break;
                }
                return true;
            }
        });

    }
    // 게시판분류를 찾는 메서드 (게시판 분류)
    private void read(String division) {

        //데이터 읽기
        mDatabaseRef.child("board").child(division).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boardInfo[0] = snapshot.getValue(BoardInfo.class);
                data.clear();

                for(DataSnapshot ss : snapshot.getChildren()){
                    BoardInfo boardInfo1 = ss.getValue(BoardInfo.class);
                    contentId = boardInfo1.getName();
                    content = boardInfo1.getContent();
                    data.add(contentId + " : " + content);
                }

                adapter.notifyDataSetChanged();
                list.setSelection(adapter.getCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                data.clear();
                boardTitle.setText("게시글을 불러올 수가 없습니다.");
            }
        });
    }
    // 찐 이름 가져오는 메소드
    private String readName() {
        final UserAccount[] userInfo = {new UserAccount()};
        //데이터 읽기
        mDatabaseRef.child("project").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                name1[0] = "회원";
            }

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo[0] = snapshot.getValue(UserAccount.class);
                if (userInfo[0] == null || userInfo[0].getName() == null || userInfo[0].getName().length() == 0 || userInfo[0].equals(null))
                    name1[0] = "회원";
                else {
                    name1[0] = userInfo[0].getName();
                    Log.d("이이이이름", name1[0]);
                }
            }
        });
        Log.d("이이이이름111111111", name1[0]);
        return name1[0];
    }
}