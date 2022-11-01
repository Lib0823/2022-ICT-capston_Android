package com.example.project2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BulkActivity extends AppCompatActivity {

    int version = 1;
    DatabaseOpenHelper helper;
    SQLiteDatabase database;

    String sql;
    Cursor cursor;

    Double BMR, bulkup;
    TextView basicText, recomText;
    ProgressBar basicBar, recomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk);

        //DataBase연결부분
        helper = new DatabaseOpenHelper(BulkActivity.this, DatabaseOpenHelper.tableName, null, version);
        database = helper.getWritableDatabase();

        sql = "SELECT * FROM "+ helper.tableName + " WHERE login = '1'";
        cursor = database.rawQuery(sql, null);

        cursor.moveToNext();   // 첫번째에서 다음 레코드가 없을때까지 읽음
        String age = cursor.getString(3);   //키
        String height = cursor.getString(4);   //키
        String weight = cursor.getString(5);   //몸무게
        String gender = cursor.getString(6);   //성별
        int weight2 = Integer.parseInt(weight);
        int height2 = Integer.parseInt(height);
        int age2 = Integer.parseInt(age);

        if(gender.equals("man")){
            BMR = (13.397*weight2) + (4.799*height2) - (5.677*age2) + 88.362;
        }else if(gender.equals("woman")){
            BMR =  (9.247*weight2) + (3.098*height2) - (4.330*age2) + 447.593;
        }

        bulkup = (BMR*1.4)*1.2;

        String bulkup2 = String.format("%.0f", bulkup);
        String BMR2 = String.format("%.0f", BMR);

        basicText = findViewById(R.id.basic);
        basicText.setText("기초                                 "+
                "                                         "+BMR2); //기초대사량
        recomText = findViewById(R.id.recommend);
        recomText.setText("권장                                 "+
                "                                         "+bulkup2); //권장섭취량

        basicBar = findViewById(R.id.basicBar);
        basicBar.setProgress(Integer.parseInt(BMR2));
        recomBar = findViewById(R.id.recomBar);
        recomBar.setProgress(Integer.parseInt(bulkup2));
    }
}