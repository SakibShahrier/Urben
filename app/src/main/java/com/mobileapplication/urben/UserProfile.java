package com.mobileapplication.urben;

public class UserProfile {

    private String userName;
    private String email;
    private int age;
    private String gender;

    public UserProfile(String userName, String email, int age, String gender) {
        this.userName = userName;
        this.email = email;
        this.age = age;
        this.gender = gender;
    }

    public UserProfile() {
        this.userName = "";
        this.email = "";
        this.age = 0;
        this.gender = "";
    }

    public void setName(String name){
        userName = name;
    }


    public void setAge(int age) {
        this.age = age;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getName() {
        return userName;
    }


    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "name='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                '}';
    }
}
