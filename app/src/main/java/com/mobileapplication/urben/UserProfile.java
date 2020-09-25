package com.mobileapplication.urben;

public class UserProfile {

    private String userName;
    private String email;
    private int age;
    private String gender;
    private String password;
    private  String userType;

    public UserProfile(String userName, String email, int age, String gender, String password, String userType) {
        this.userName = userName;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.password = password;
        this.userType = userType;
    }

    public UserProfile() {
        this.userName = "";
        this.email = "";
        this.age = 0;
        this.gender = "";
        this.password = "";
        this.userType = "";
    }

    public void setFirstName(String name){
        userName = name;
    }


    public void setAge(int age) {
        this.age = age;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFirstName() {
        return userName;
    }


    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getGender() {
        return gender;
    }

    public String getUserType() {
        return userType;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "firstName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", password='" + password + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}
