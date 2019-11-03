package com.example.security_and_file_access;

public class RegisterModel {
    public String id;
    public String password;
    public String date;
    public String encodedPassword;
    public RegisterModel(String id, String password, String date, String encodedPassword){
        this.id = id;
        this.password = password;
        this.date = date;
        this.encodedPassword = encodedPassword;
    }
}
