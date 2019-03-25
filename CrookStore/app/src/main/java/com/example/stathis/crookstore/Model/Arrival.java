package com.example.stathis.crookstore.Model;

public class Arrival {

    private int user_id;
    private String email;
    private String firstname;
    private String lastname;
    private String time_remain;

    public Arrival(int user_id, String email, String firstname, String lastname, String time_remain) {
        this.user_id = user_id;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.time_remain = time_remain;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getTime_remain() {
        return time_remain;
    }

    public void setTime_remain(String time_remain) {
        this.time_remain = time_remain;
    }
}
