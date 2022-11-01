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

import java.util.ArrayList;
import java.util.List;

public class BoardActivity extends AppCompatActivity {

    private ListView list;
    private BottomNavigationView bottomNavi, boardNavi;
    private Button contentBtn, searchBtn;
    private TextView contentText, searchText, boardTitle;
    private String id, contentId, content, field = "free", search;

    int version = 1;
    DatabaseOpenHelper helperBoard, helperUser;
    SQLiteDatabase databaseBoard, databaseUser;

    String sql;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        //DB
        helperBoard = new DatabaseOpenHelper(BoardActivity.this, DatabaseOpenHelper.tableNameBoard, null, version);
        helperUser = new DatabaseOpenHelper(BoardActivity.this, DatabaseOpenHelper.tableName, null, version);
        databaseBoard = helperBoard.getWritableDatabase();
        databaseUser = helperUser.getWritableDatabase();

        sql = "SELECT id FROM "+ helperUser.tableName + " WHERE login = '1'";
        cursor = databaseUser.rawQuery(sql, null);
        cursor.moveToNext();   // 첫번째에서 다음 레코드가 없을때까지 읽음
        id = cursor.getString(0);

        list = findViewById(R.id.list);
        List<String> data = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1, data);
        list.setAdapter(adapter);
        list.setSelection(adapter.getCount() - 1);

        sql = "SELECT id, content FROM "+ helperBoard.tableNameBoard + " WHERE field = '"+ field +"'";
        cursor = databaseBoard.rawQuery(sql, null);

        while(cursor.moveToNext()){
            contentId = cursor.getString(0);
            content = cursor.getString(1);
            data.add(contentId+ " : " +content);
        }
        adapter.notifyDataSetChanged();
        list.setSelection(adapter.getCount() - 1);

        boardTitle = findViewById(R.id.boardTitle);
        // Field변경 시 저장
        boardNavi = findViewById(R.id.boardNavi);
        boardNavi.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.free:
                        field = "free";
                        data.clear();
                        sql = "SELECT id, content FROM "+ helperBoard.tableNameBoard + " WHERE field = '"+ field +"'";
                        cursor = databaseBoard.rawQuery(sql, null);

                        while(cursor.moveToNext()){
                            contentId = cursor.getString(0);
                            content = cursor.getString(1);
                            data.add(contentId+ " : " +content);
                        }
                        adapter.notifyDataSetChanged();
                        list.setSelection(adapter.getCount() - 1);
                        boardTitle.setText("자유 게시판");
                        break;
                    case R.id.diet:
                        field = "diet";
                        data.clear();
                        sql = "SELECT id, content FROM "+ helperBoard.tableNameBoard + " WHERE field = '"+ field +"'";
                        cursor = databaseBoard.rawQuery(sql, null);

                        while(cursor.moveToNext()){
                            contentId = cursor.getString(0);
                            content = cursor.getString(1);
                            data.add(contentId+ " : " +content);
                        }
                        adapter.notifyDataSetChanged();
                        list.setSelection(adapter.getCount() - 1);
                        boardTitle.setText("다이어트 게시판");
                        break;
                    case R.id.lean:
                        field = "lean";
                        data.clear();
                        sql = "SELECT id, content FROM "+ helperBoard.tableNameBoard + " WHERE field = '"+ field +"'";
                        cursor = databaseBoard.rawQuery(sql, null);

                        while(cursor.moveToNext()){
                            contentId = cursor.getString(0);
                            content = cursor.getString(1);
                            data.add(contentId+ " : " +content);
                        }
                        adapter.notifyDataSetChanged();
                        list.setSelection(adapter.getCount() - 1);
                        boardTitle.setText("린매스업 게시판");
                        break;
                    case R.id.bulk:
                        field = "bulk";
                        data.clear();
                        sql = "SELECT id, content FROM "+ helperBoard.tableNameBoard + " WHERE field = '"+ field +"'";
                        cursor = databaseBoard.rawQuery(sql, null);

                        while(cursor.moveToNext()){
                            contentId = cursor.getString(0);
                            content = cursor.getString(1);
                            data.add(contentId+ " : " +content);
                        }
                        adapter.notifyDataSetChanged();
                        list.setSelection(adapter.getCount() - 1);
                        boardTitle.setText("벌크업 게시판");
                        break;
                }
                return false;
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
                contentText.setText("");
                helperBoard.insertBoard(databaseBoard, id, content, field);
                data.clear();

                sql = "SELECT id, content FROM "+ helperBoard.tableNameBoard + " WHERE field = '"+ field +"'";
                cursor = databaseBoard.rawQuery(sql, null);

                while(cursor.moveToNext()){
                    contentId = cursor.getString(0);
                    content = cursor.getString(1);
                    data.add(contentId+ " : " +content);
                }
                adapter.notifyDataSetChanged();
                list.setSelection(adapter.getCount() - 1);
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

                sql = "SELECT id, content FROM "+ helperBoard.tableNameBoard + " WHERE field = '"+ field +"'" +
                        " AND content LIKE '%"+search+"%'";
                cursor = databaseBoard.rawQuery(sql, null);
                while(cursor.moveToNext()){
                    contentId = cursor.getString(0);
                    content = cursor.getString(1);
                    data.add(contentId+ " : " +content);
                }
                adapter.notifyDataSetChanged();
                list.setSelection(adapter.getCount() - 1);
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
                        break;
                    case R.id.action_fitness:
                        Intent intent2 = new Intent(BoardActivity.this, FitnessActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.action_board:
                        break;
                    case R.id.action_info:
                        Intent intent3 = new Intent(BoardActivity.this, InfoActivity.class);
                        startActivity(intent3);
                        break;
                }
                return true;
            }
        });

    }
}