package com.spectators.hungerfree;

/**
 * Created by Naveen on 1/15/2018.
 */

public class Profile {
    private String email;
    private String name;
    private String mobile;

    public Profile(){
    }

    public Profile(String email, String name, String mobile){
        this.email = email;
        this.name = name;
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String toString(){
        return email + " " + name + " " + mobile;
    }
}
