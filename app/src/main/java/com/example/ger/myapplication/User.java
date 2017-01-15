package com.example.ger.myapplication;

public class User {

    private int id;

    private String nickname;

    private String google_instance_id;

    private String email_address;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGoogle_instance_id() {
        return google_instance_id;
    }

    public void setGoogle_instance_id(String google_instance_id) {
        this.google_instance_id = google_instance_id;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

}
