package com.example.project2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

public class BattleActivity extends AppCompatActivity {

    ListView list;
    ArrayList data;
    ArrayAdapter adapter;
    EditText battleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        // 리스트 생성
        list = findViewById(R.id.battleList);
        data = new ArrayList<>();
        adapter = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1, data);
        list.setAdapter(adapter);
        list.setSelection(adapter.getCount() - 1);

        data.add("leeSoo님이 대결을 신청하였습니다!");
        adapter.notifyDataSetChanged();
        list.setSelection(adapter.getCount() - 1);

        // Spinner
        Spinner battleSpinner = (Spinner)findViewById(R.id.battleSpinner);
        ArrayAdapter battleAdapter = ArrayAdapter.createFromResource(this,
                R.array.battleDay, android.R.layout.simple_spinner_item);
        battleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        battleSpinner.setAdapter(battleAdapter);

        battleId = findViewById(R.id.battleId);

    }
}