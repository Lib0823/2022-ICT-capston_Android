package com.example.project2;

// 회원가입 사용자 계정 정보 모델 클래스
public class UserAccount {
    private String idToken;     // 파이어베이스 고유 아이디 토큰정보
    private String id;
    private String pw;
    private String name;
    private int age;
    private int height;
    private int weight;
    private String gender;
    private int run;
    private String date;

    private int pedometer;

    public UserAccount() {
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getRun() {
        return run;
    }

    public void setRun(int run) {
        this.run = run;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPedometer(int i) {
        return pedometer;
    }

    public void setPedometer(int pedometer) {
        this.pedometer = pedometer;
    }
}
