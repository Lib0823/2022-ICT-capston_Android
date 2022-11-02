package com.example.project2;

public class BoardInfo {
    private String name;
    private String content;

    // 생성자 삭제하면 안됌!
    public BoardInfo() {}

    public BoardInfo(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
