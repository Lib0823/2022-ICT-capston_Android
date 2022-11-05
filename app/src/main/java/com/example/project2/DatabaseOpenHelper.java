package com.example.project2;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    
    public static final String tableRun = "Run";

    public DatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("tag","db 생성_db가 없을때만 최초로 실행함");
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
    }

    public void createTable(SQLiteDatabase db){
        String sql = "CREATE TABLE " + tableRun + "(date text, id text, point int)";
        try {
            db.execSQL(sql);
        }catch (SQLException e){
        }
    }

    public void insertRun(SQLiteDatabase db, String date, String id, int point){
        Log.i("tag","게시판 등록했을때 실행함");
        db.beginTransaction();
        try {
            String sql2 = "INSERT INTO " + tableRun + "(date, id, point)" + "values('"+ date +"', '"+ id +"', "+ point+")";
            db.execSQL(sql2);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
    }


}
