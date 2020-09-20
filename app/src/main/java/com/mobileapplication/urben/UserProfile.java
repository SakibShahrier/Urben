package com.mobileapplication.urben;

public class UserProfile {

    private String firstName;
    private String lastName;
    private String email;
    private int age;
    private String gender;
    private String password;
    private  String userType;

    public UserProfile(String firstName, String lastName, String email, int age, String gender, String password, String userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.password = password;
        this.userType = userType;
    }

    public UserProfile() {
        this.firstName = "";
        this.lastName = "";
        this.email = "";
        this.age = 0;
        this.gender = "";
        this.password = "";
        this.userType = "";
    }

    public void setFirstName(String name){
        firstName = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
        return firstName;
    }

    public String getLastName() {
        return lastName;
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
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", password='" + password + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}
