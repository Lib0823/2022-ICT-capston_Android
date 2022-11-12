package com.example.project2;

public class ChatItem {
    private String content;
    private String name;
    private String time;
    private int viewType;

    public ChatItem(String content, String name , String time, int viewType) {
        this.content = content;
        this.name = name;
        this.time = time;
        this.viewType = viewType;
    }

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public int getViewType() {
        return viewType;
    }
}