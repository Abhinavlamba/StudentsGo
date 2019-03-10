package com.travis.android.studentsgo.model;

public class Chat
{
    private String name;
    private String phone;
    private String uid;
    private String body;

    public Chat(String name, String phone, String uid, String body) {
        this.name = name;
        this.phone = phone;
        this.uid = uid;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getUid() {
        return uid;
    }

    public String getBody() {
        return body;
    }
}
