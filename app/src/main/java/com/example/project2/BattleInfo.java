package com.example.project2;

public class BattleInfo {
    private String userId;
    private String battleId;
    private String matchDay;
    private String startDay;
    private String opid;
    private String opToken;

    public BattleInfo() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBattleId() {
        return battleId;
    }

    public void setBattleId(String battleId) {
        this.battleId = battleId;
    }

    public String getMatchDay() {
        return matchDay;
    }

    public void setMatchDay(String matchDay) {
        this.matchDay = matchDay;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getOpid() {
        return opid;
    }

    public void setOpid(String opid) {
        this.opid = opid;
    }

    public String getOpToken() {
        return opToken;
    }

    public void setOpToken(String opToken) {
        this.opToken = opToken;
    }
}
