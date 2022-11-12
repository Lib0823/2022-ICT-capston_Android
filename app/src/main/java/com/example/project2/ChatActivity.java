package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    private Handler mHandler;
    InetAddress serverAddr;
    Socket socket;
    PrintWriter sendWriter;
    private String ip = "172.20.10.2";
    private int port = 7000;

    TextView textView;
    String UserID;
    Button connectbutton;
    Button chatbutton;
    TextView chatView;
    EditText message;
    String sendmsg;
    String read;

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 데이터베이스 연동
    private DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); // 방금 로그인 성공한 유저의 정보를 가져오는 객체


    @Override
    protected void onStop() {
        Log.d("onStop","onStop함수 실행됨..");
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mHandler = new Handler();
        textView = (TextView) findViewById(R.id.textView);
        chatView = (TextView) findViewById(R.id.chatView);
        message = (EditText) findViewById(R.id.message);
        final UserAccount[] userInfo = {new UserAccount()};

        // 현재 사용자 ID가져오기
        mDatabaseRef.child("project").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                UserID = "아이디 에러";
            }

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo[0] = snapshot.getValue(UserAccount.class);
                if(userInfo[0] == null || userInfo[0].getId() == null || userInfo[0].getId().length() == 0 || userInfo[0].equals(null))
                    UserID = "아이디 에러";
                else {
                    UserID = userInfo[0].getId();
                    textView.setText(UserID);
                }
            }
        });

        chatbutton = (Button) findViewById(R.id.chatbutton);

        // 메인 Thread() - 소켓 세팅, read/update
        new Thread() {
            public void run() {
                try {
                    serverAddr = InetAddress.getByName(ip);
                    socket = new Socket(serverAddr, port);
                    sendWriter = new PrintWriter(socket.getOutputStream());
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while(true){
                        read = input.readLine();
                        Log.d("읽어온 값", read);
                        //System.out.println("TTTTTTTT"+read);
                        if(read!=null){
                            mHandler.post(new BattleActivity.msgUpdate(read));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } }}.start();

        chatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmsg = message.getText().toString();

                // 버튼 Thread() - 자신이 보낸 메세지 출력
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Log.d("정보1 아이디", UserID);
                            Log.d("정보1 메시지", sendmsg);
                            sendWriter.println(UserID +">"+ sendmsg+"\n");
                            Log.d("printWriter", "println성공");
                            sendWriter.flush();
                            message.setText("");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

}