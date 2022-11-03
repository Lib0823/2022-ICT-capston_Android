package com.example.project2;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.StrictMath.atan2;
import static java.lang.StrictMath.sin;
import static java.lang.StrictMath.sqrt;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RunActivity extends AppCompatActivity implements SensorEventListener, TMapGpsManager.onLocationChangedCallback {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase mFirebaseDB;
    private DatabaseReference mDatabaseRef = mFirebaseDB.getInstance().getReference();
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); // 방금 로그인 성공한 유저의 정보를 가져오는 객체
    private UserAccount[] userInfo = {new UserAccount()};   // 출력전용 객체
    private UserAccount account = new UserAccount();        // 입력전용 객체
    private String date;
    private static int run;
    private Chronometer chrono;
    private boolean running;
    private long pauseOffset;
    private TextView distance, kcal;
    private double countKcal=0.0;


    //걸음수
    SensorManager sensorManager;
    Sensor stepCountSensor;
    TextView stepCount;
    Button resetButton;
    int currentSteps = 0;

    int m; // 시간(분)

    double[] lon = new double[1000];
    double[] lat = new double[1000];
    int count= 0;
    double total = 0; // 총 거리

    Button startBtn, stopBtn;

    String API_Key = "l7xx307e334d60fa48ea83d967f7e14d88bb";

    // T Map View
    TMapView tMapView = null;

    // T Map GPS
    TMapGpsManager tMapGPS = null;

    // 멀티터치 이벤트
    private double touch_interval_X = 0; // X 터치 간격
    private double touch_interval_Y = 0; // Y 터치 간격

    ArrayList<TMapPoint> alTMapPoint = new ArrayList<TMapPoint>();

    @Override
    public void onBackPressed() { // back키 이벤트

        int sum = run + m;
        Map<String, Object> taskMap1 = new HashMap<String, Object>();
        taskMap1.put("run", sum);
        mDatabaseRef.child("project").child(firebaseUser.getUid()).updateChildren(taskMap1);

        // 현재 날짜 가져오기
        long now = System.currentTimeMillis();
        Date date1 = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
        date = sdf.format(date1);

        Map<String, Object> taskMap2 = new HashMap<String, Object>();
        taskMap2.put("date", date);
        mDatabaseRef.child("project").child(firebaseUser.getUid()).updateChildren(taskMap2);

        chrono.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
        chrono.stop();
        running = false;
        currentSteps = 0;
        stepCount.setText(String.valueOf(currentSteps));

        Intent intent = new Intent(RunActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        kcal = findViewById(R.id.kcal);

        //데이터 읽기
        mDatabaseRef.child("project").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                run = 0;
            }

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo[0] = snapshot.getValue(UserAccount.class);
                if(userInfo[0] == null ||  userInfo[0].equals(null))
                    run = 0;
                else {
                    run = userInfo[0].getRun();
                }
            }
        });

        //걸음수
        stepCount = findViewById(R.id.stepCount);
        // 활동 퍼미션 체크
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){

            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        // 걸음 센서 연결
        // * 옵션
        // - TYPE_STEP_DETECTOR:  리턴 값이 무조건 1, 앱이 종료되면 다시 0부터 시작
        // - TYPE_STEP_COUNTER : 앱 종료와 관계없이 계속 기존의 값을 가지고 있다가 1씩 증가한 값을 리턴
        //
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        // 디바이스에 걸음 센서의 존재 여부 체크
        if (stepCountSensor == null) {
            Toast.makeText(this, "No Step Sensor", Toast.LENGTH_SHORT).show();
        }

        // T Map View
        tMapView = new TMapView(this);

        // API Key
        tMapView.setSKTMapApiKey(API_Key);

        // Initial Setting
        tMapView.setZoomLevel(16);
        tMapView.setIconVisibility(true);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        // T Map View Using Linear Layout
        LinearLayout linearLayoutTmap = findViewById(R.id.linearLayoutTmap);
        linearLayoutTmap.addView(tMapView);

        // Request For GPS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // GPS using T Map
        tMapGPS = new TMapGpsManager(this);

        // Initial Setting
        tMapGPS.setMinTime(100);    // 일정 시간마다 리셋
        tMapGPS.setMinDistance(1);  // 일정 거리마다 리셋
        //tMapGPS.setProvider(tMapGPS.NETWORK_PROVIDER); //네트워크
        tMapGPS.setProvider(tMapGPS.GPS_PROVIDER);       //GPS

        // 화면중심을 단말의 현재위치로 이동
        tMapView.setTrackingMode(true);
        tMapView.setSightVisible(true);

        tMapGPS.OpenGps();


        //시간
        chrono = findViewById(R.id.chrono);
        chrono.setFormat("%s");

        startBtn = findViewById(R.id.startBtn);
        stopBtn = findViewById(R.id.stopBtn);

        //시작버튼
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!running){
                    chrono.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                    chrono.start();
                    running = true;
                }
            }
        });

        //정지버튼
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chrono.stop();
                pauseOffset = SystemClock.elapsedRealtime() - chrono.getBase();
                running = false;
            }
        });

        chrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
            @Override
            public void onChronometerTick(Chronometer cArg) {
                long time = SystemClock.elapsedRealtime() - cArg.getBase();
                int h   = (int)(time /3600000);
                m = (int)(time - h*3600000)/60000;
                int s= (int)(time - h*3600000- m*60000)/1000 ;
                String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0"+m: m+"";
                String ss = s < 10 ? "0"+s: s+"";
                chrono.setText(hh+":"+mm+":"+ss);
            }
        });

    }

    //걸음수
    public void onStart() {
        super.onStart();
        if(stepCountSensor !=null) {
            // 센서 속도 설정
            // * 옵션
            // - SENSOR_DELAY_NORMAL: 20,000 초 딜레이
            // - SENSOR_DELAY_UI: 6,000 초 딜레이
            // - SENSOR_DELAY_GAME: 20,000 초 딜레이
            // - SENSOR_DELAY_FASTEST: 딜레이 없음
            //
            sensorManager.registerListener((SensorEventListener) this,stepCountSensor,SensorManager.SENSOR_DELAY_FASTEST);
        }
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        // 걸음 센서 이벤트 발생시
        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){

            if(event.values[0]==1.0f){
                // 센서 이벤트가 발생할때 마다 걸음수 증가
                currentSteps++;
                stepCount.setText(String.valueOf(currentSteps));
                countKcal = currentSteps * 0.04;
                kcal.setText((String.format("%.2f", countKcal)+"kcal"));
            }

        }

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    // 지속적으로 위치를 받아와 설정해줌
    @Override
    public void onLocationChange(Location location) {
        tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        tMapView.setCenterPoint(location.getLongitude(), location.getLatitude());
        double Longitude = location.getLongitude(); //경도
        double Latitude = location.getLatitude();   //위도
        alTMapPoint.add( new TMapPoint(Latitude, Longitude)); //가져온 경도,위도를 Point에 추가

        TMapPolyLine tMapPolyLine = new TMapPolyLine();
        tMapPolyLine.setLineColor(Color.RED);
        tMapPolyLine.setLineWidth(10);
        for( int i=0; i<alTMapPoint.size(); i++ ) {
            tMapPolyLine.addLinePoint( alTMapPoint.get(i) );
        }
        tMapView.addTMapPolyLine("Line", tMapPolyLine); // point값을 polyLine로 그림

        // 거리계산 식
        if(count == 0){
            lon[0] = Longitude;
            lat[0] = Latitude;
            lon[1] = Longitude;
            lat[1] = Latitude;
        }else{
            lon[count] = Longitude;     // count로 매번 포인트마다 위도/경도를 대입
            lat[count] = Latitude;
            double d2r = (Math.PI / 180D);
            double dlong = (lon[count] - lon[count-1]) * d2r;
            double dlat = (lat[count] - lat[count-1]) * d2r;
            double a = pow(sin(dlat/2.0), 2) + cos(lat[count-1]*d2r) * cos(lat[count]*d2r) * pow(sin(dlong/2.0), 2);
            double c = 2 * atan2(sqrt(a), sqrt(1-a));
            double d = 6367 * c;

            total += d;
            distance = findViewById(R.id.distance);
            distance.setText((String.format("%.2f", total)+"km"));    // km단위로 거리 출력
        }
        count++;

    }

    // 멀티터치(zoom in/out) 함수
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: // 싱글 터치
                break;
            case MotionEvent.ACTION_MOVE: // 터치 후 이동 시
                if (event.getPointerCount() == 2) { // 터치 손가락 2개일 때
                    double now_interval_X = (double) abs(event.getX(0) - event.getX(1)); // 두 손가락 X좌표 차이 절대값
                    double now_interval_Y = (double) abs(event.getY(0) - event.getY(1)); // 두 손가락 Y좌표 차이 절대값
                    if (touch_interval_X < now_interval_X && touch_interval_Y < now_interval_Y) { // 이전 값과 비교
                        // 확대 기능
                        tMapView.MapZoomIn();
                    }
                    if (touch_interval_X > now_interval_X && touch_interval_Y >
                            now_interval_Y) {
                        // 축소 기능
                        tMapView.MapZoomOut();
                    }
                    touch_interval_X = (double) abs(event.getX(0) - event.getX(1));
                    touch_interval_Y = (double) abs(event.getY(0) - event.getY(1));
                }
                break;
        }
            return super.onTouchEvent(event);
    }

}