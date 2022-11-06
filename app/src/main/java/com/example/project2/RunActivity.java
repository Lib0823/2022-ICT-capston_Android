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
import com.bumptech.glide.Glide;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    private String date, tmperature;
    private static int run;
    private Chronometer chrono;
    private boolean running;
    private long pauseOffset;
    private TextView distance, kcal;
    private double countKcal=0.0;
    private int result = 0;
    public static String weather121313 = "현재 날씨는 맑은 상태입니다.";
    private String idemail;
    private Integer point;


    // 날씨
    private double longitude = 37.4481;    // 인하공전 경도
    private double latitude = 126.6585;    // 인하공전 위도
    private static String weatherResult = "";     // 날씨정보
    private ImageView ivWeather;
    private TextView tvTemperatures, tvWeather;
    private String baseDate;                                    // 조회하고 싶은 날짜
    private String baseTime;                                    // 조회하고 싶은 시간
    private String weather;                                     // 날씨 결과

    //걸음수
    SensorManager sensorManager;
    Sensor stepCountSensor;
    TextView stepCount;
    Button resetButton;
    static int currentSteps = 0;

    // SQLite
    int version = 1;
    DatabaseOpenHelper helper;
    SQLiteDatabase database;

    String sql;
    Cursor cursor;

    private int m; // 시간(분)

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
        final PointInfo[] pointInfos = {new PointInfo()};

        // 현재 날짜 가져오기
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
        String date2 = sdf.format(date);

        Log.d("stepCount 정보", stepCount.getText().toString());
        int c = Integer.parseInt(stepCount.getText().toString());
        //데이터 읽기
        mDatabaseRef.child("point").child(firebaseUser.getUid()).child(date2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                point = 0;
            }

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pointInfos[0] = snapshot.getValue(PointInfo.class);
                if (pointInfos[0] == null || pointInfos[0].equals(null))
                    point = 0;
                else {
                    point = pointInfos[0].getPoint();
                    if(point==null){
                        point = 0;
                    }

                    int to = (int) total; // 총 거리
                    if(to==0) {
                        to=1;
                    }
                    Log.d("c 정보", String.valueOf(c));
                    int sumpoint = c * to;
                    Log.d("sumpoint의 정보", String.valueOf(sumpoint));
                    int resultpoint = point + sumpoint;
                    Log.d("resultpoint의 정보", String.valueOf(resultpoint));

                    Map<String, Object> taskMap2 = new HashMap<String, Object>();
                    taskMap2.put("point", resultpoint);
                    mDatabaseRef.child("point").child(firebaseUser.getUid()).child(date2).updateChildren(taskMap2);
                    Log.d("이이이이름", String.valueOf(point));
                }
            }
        });

//        // SQlite 런값 업데이트
        //Log.d("여기여기!", String.valueOf(distance));
       // point = (currentSteps * Integer.parseInt(String.valueOf(distance)));
//        database.execSQL("UPDATE Run SET run="+point+" WHERE id='"+idemail+"'");

        // ***** 파베에서 해당 사용자의 id에 (date, point)값을 계속 갱신해준다.


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

    @SuppressLint("WrongViewCast")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        //DataBase연결부분
        //helper = new DatabaseOpenHelper(RunActivity.this, DatabaseOpenHelper.tableRun, null, version);
        //database = helper.getWritableDatabase();

        // 날씨 이미지 뷰
        ivWeather = findViewById(R.id.ivWeather);
        tvTemperatures = findViewById(R.id.tvTemperatures);
        tvWeather = findViewById(R.id.tvWeather);

        Glide.with(this).load(R.mipmap.sun).into(ivWeather);

        new Thread(() -> {
            try {
                weatherResult = lookUpWeather(longitude, latitude);
                Log.d("날씨정보",weatherResult);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();

        // 날씨
        int beginIndex = weatherResult.lastIndexOf(",") + 1;
        int endIndex = weatherResult.length();
        // 혹시 모를 에러 처리하기!!
        if (beginIndex != 0) {
            Log.d("정보", String.valueOf(beginIndex));
            String temperatures = weatherResult.substring(beginIndex, endIndex);    // 기온
            String weather = weatherResult.substring(0, (beginIndex - 1));    // 날씨
            tvTemperatures.setText(temperatures);
            tvWeather.setText(weather);
            Log.d("r1의 정보", weather121313);
            Log.d("weather의 정보", weather);
            if(!weather121313.equals(weather)) {
                // 날씨에 따라 이미지 변경
                if (weather.equals("현재 날씨는 맑은 상태입니다.")) {
                    Glide.with(ivWeather).load(R.mipmap.sun).into(ivWeather);
                    weather121313 = "현재 날씨는 맑은 상태입니다.";
                    ivWeather.setImageResource(R.mipmap.sun);
                } else if (weather.equals("현재 날씨는 비가 오는 상태입니다.")) {
                    Glide.with(ivWeather).load(R.mipmap.rain).into(ivWeather);
                    ivWeather.setImageResource(R.mipmap.rain);
                    weather121313 = "현재 날씨는 비가 오는 상태입니다.";
                } else if (weather.equals("현재 날씨는 구름이 많은 상태입니다.")) {
                    Glide.with(ivWeather).load(R.mipmap.cloudy).into(ivWeather);
                    ivWeather.setImageResource(R.mipmap.cloudy);
                    weather121313 = "현재 날씨는 구름이 많은 상태입니다.";
                } else if (weather.equals("현재 날씨는 흐린 상태입니다.")) {
                    Glide.with(ivWeather).load(R.mipmap.clouds).into(ivWeather);
                    ivWeather.setImageResource(R.mipmap.clouds);
                    weather121313 = "현재 날씨는 흐린 상태입니다.";
                }
            }
        }

        // 여기까지 날씨 구하기
        
        kcal = findViewById(R.id.kcal);

        /*//데이터 읽기
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
        });*/

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
                result = 1;
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
                result = 0;
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
            if(result == 1) {
                if (event.values[0] == 1.0f) {
                    // 센서 이벤트가 발생할때 마다 걸음수 증가
                    currentSteps++;
                    stepCount.setText(String.valueOf(currentSteps));
                    countKcal = currentSteps * 0.04;
                    kcal.setText((String.format("%.2f", countKcal) + "kcal"));
                }
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

        // 날씨
        int beginIndex = weatherResult.lastIndexOf(",") + 1;
        int endIndex = weatherResult.length();
        // 혹시 모를 에러 처리하기!!
        if (beginIndex != 0) {
            Log.d("정보", String.valueOf(beginIndex));
            String temperatures = weatherResult.substring(beginIndex, endIndex);    // 기온
            String weather = weatherResult.substring(0, (beginIndex - 1));    // 날씨
            tvTemperatures.setText(temperatures);
            tvWeather.setText(weather);
            Log.d("r1의 정보", weather121313);
            Log.d("weather의 정보", weather);
            if(!weather121313.equals(weather)) {
                // 날씨에 따라 이미지 변경
                if (weather.equals("현재 날씨는 맑은 상태입니다.")) {
                    Glide.with(RunActivity.this).load(R.mipmap.sun).into(ivWeather);
                    ivWeather.setImageResource(R.mipmap.sun);
                    weather121313 = "현재 날씨는 맑은 상태입니다.";
                } else if (weather.equals("현재 날씨는 비가 오는 상태입니다.")) {
                    Glide.with(RunActivity.this).load(R.mipmap.rain).into(ivWeather);
                    ivWeather.setImageResource(R.mipmap.rain);
                    weather121313 = "현재 날씨는 비가 오는 상태입니다.";
                } else if (weather.equals("현재 날씨는 구름이 많은 상태입니다.")) {
                    Glide.with(RunActivity.this).load(R.mipmap.cloudy).into(ivWeather);
                    ivWeather.setImageResource(R.mipmap.cloudy);
                    weather121313 = "현재 날씨는 구름이 많은 상태입니다.";
                } else if (weather.equals("현재 날씨는 흐린 상태입니다.")) {
                    Glide.with(RunActivity.this).load(R.mipmap.clouds).into(ivWeather);
                    ivWeather.setImageResource(R.mipmap.clouds);
                    weather121313 = "현재 날씨는 흐린 상태입니다.";
                }
            }
        }

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
    // 날씨 구하는 메서드
    public String lookUpWeather(double dx, double dy) throws IOException, JSONException {
        // 현재 위치 필드 저장
        int ix = (int) dx;
        int iy = (int) dy;
        String nx = String.valueOf(ix);
        String ny = String.valueOf(iy);
        Log.i("날씨: 위도!!", nx);
        Log.i("날씨: 경도!!", ny);

        // 현재 날짜 구하기 (시스템 시계, 시스템 타임존)
        LocalDate date = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            date = LocalDate.now();
        }
        LocalTime time = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            time = LocalTime.now();
        }
        baseDate = String.valueOf(date).replaceAll("-", "");
        int correctionDate = Integer.parseInt(baseDate) - 1;     // 날씨 API : 매 시각 45분 이후 호출 // 오전 12시인 경우 사용

        // 시간(30분 단위로 맞추기)
        DateTimeFormatter formatter1 = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter1 = DateTimeFormatter.ofPattern("HHmm");
        }
        DateTimeFormatter formatter2 = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter2 = DateTimeFormatter.ofPattern("HH");
        }

        int itime1 = 0; // 실제 시간
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            itime1 = Integer.parseInt(time.format(formatter1));
        }
        int itime2 = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            itime2 = Integer.parseInt(time.format(formatter2)) - 1;
        }

        //  /*06시30분 발표(30분 단위)*/
        if (itime2 <= 7) {
            itime2 = 23;
            baseDate = String.valueOf(correctionDate);
            baseTime = "2100";
        } else {
            // api가 30분 단위로 업데이트
            if (itime1 % 100 >= 30) baseTime = itime2 + "30";
            else baseTime = itime2 + "00";
        }
        // 오전에는 시간이 3자리로 나옴...
        if (baseTime.length() == 3) {
            baseTime = "0" + baseTime;
        }

        String weatherResult = "현재 날씨를 확인할 수가 없어요.";

        Log.i("날씨: 입력일자!!", baseDate);
        Log.i("날씨: 입력시간!!", baseTime);

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=eWD3WU%2B78w6UiyRQFINsKmuNGrDvg3JnKDnefyrBx1jEAGOxNI%2FuFwXB5W7LgsBunL2cQz6OqBLIuJQWDES1SQ%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8")); /*‘21년 6월 28일 발표*/
        urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8")); /*06시30분 발표(30분 단위)*/
        urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); /*예보지점 X 좌표값*/
        urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); /*예보지점 Y 좌표값*/

        /*
         * GET방식으로 전송해서 파라미터 받아오기
         */
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        String result = sb.toString();

        Log.d("정보", result);
        //=======이 밑에 부터는 json에서 데이터 파싱해 오는 부분이다=====//

        // response 키를 가지고 데이터를 파싱
        JSONObject jsonObj_1 = new JSONObject(result);
        String response = jsonObj_1.getString("response");

        // response 로 부터 body 찾기
        JSONObject jsonObj_2 = new JSONObject(response);
        String body = jsonObj_2.getString("body");

        // body 로 부터 items 찾기
        JSONObject jsonObj_3 = new JSONObject(body);
        String items = jsonObj_3.getString("items");
        Log.i("ITEMS", items);

        // items로 부터 itemlist 를 받기
        JSONObject jsonObj_4 = new JSONObject(items);
        JSONArray jsonArray = jsonObj_4.getJSONArray("item");

        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObj_4 = jsonArray.getJSONObject(i);
            String fcstValue = jsonObj_4.getString("fcstValue");
            String category = jsonObj_4.getString("category");

            if (category.equals("SKY")) {
                weather = "현재 날씨는 ";
                if (fcstValue.equals("1")) {
                    weather += "맑은 상태입니다.";
                } else if (fcstValue.equals("2")) {
                    weather += "비가 오는 상태입니다.";
                } else if (fcstValue.equals("3")) {
                    weather += "구름이 많은 상태입니다.";
                } else if (fcstValue.equals("4")) {
                    weather += "흐린 상태입니다.";
                }
            }
            if (category.equals("T3H") || category.equals("T1H")) {
                tmperature = fcstValue + " ℃";
            }
            weatherResult = weather + "," + tmperature;
        }
        Log.i("리턴!!", weatherResult);
        return weatherResult;
    }

    private void read() {

        final UserAccount[] userInfo = {new UserAccount()};
        //데이터 읽기
        mDatabaseRef.child("project").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                idemail = "회원정보에러";
            }

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo[0] = snapshot.getValue(UserAccount.class);
                if(userInfo[0] == null || userInfo[0].getId() == null || userInfo[0].getId().length() == 0 || userInfo[0].equals(null))
                    idemail = "회원정보에러";
                else
                    idemail = userInfo[0].getId();
            }
        });
        /*if(userInfo[0].getName() == null || userInfo[0].getName().length() == 0)
            welcome.setText("회원정보를 불러오지 못했습니다.");
        else if (userInfo[0].getDogName().equals(""))
            tvDogName.setText(userInfo[0].getDogName());*/
    }

    // 찐 이름 가져오는 메소드
    private void readPoint() {
        final PointInfo[] pointInfos = {new PointInfo()};

        // 현재 날짜 가져오기
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
        String date2 = sdf.format(date);

        //데이터 읽기
        mDatabaseRef.child("point").child(firebaseUser.getUid()).child(date2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                point = 0;
            }

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pointInfos[0] = snapshot.getValue(PointInfo.class);
                if (pointInfos[0] == null || pointInfos[0].equals(null))
                    point = 0;
                else {
                   point = pointInfos[0].getPoint();
                   if(point==null){
                       point = 0;
                   }
                    Log.d("이이이이름", String.valueOf(point));
                }
            }
        });
    }

}