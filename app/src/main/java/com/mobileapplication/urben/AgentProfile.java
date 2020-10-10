package com.mobileapplication.urben;

public class AgentProfile extends UserProfile{

    private String counterName;
    private double counterLat;
    private double counterLong;
    private int totalBusApproved;
    private boolean busArrived;

    public AgentProfile(String userName, String email){
        super();
        super.setName(userName);
        super.setEmail(email);
        busArrived = false;
    }

    @Override
    public String toString() {
        return "AgentProfile{" +
                "counterName='" + counterName + '\'' +
                ", counterLat=" + counterLat +
                ", counterLong=" + counterLong +
                ", totalBusApproved=" + totalBusApproved +
                '}';
    }

    public boolean isBusArrived() {
        return busArrived;
    }

    public void setBusArrived(boolean busArrived) {
        this.busArrived = busArrived;
    }

    public double getCounterLat() {
        return counterLat;
    }

    public double getCounterLong() {
        return counterLong;
    }

    public int getTotalBusApproved() {
        return totalBusApproved;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterLat(double counterLat) {
        this.counterLat = counterLat;
    }

    public void setCounterLong(double counterLong) {
        this.counterLong = counterLong;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public void setTotalBusApproved(int totalBusApproved) {
        this.totalBusApproved = totalBusApproved;
    }

    public AgentProfile() {
        super();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public void setAge(int age) {
        super.setAge(age);
    }

    @Override
    public void setEmail(String email) {
        super.setEmail(email);
    }

    @Override
    public void setGender(String gender) {
        super.setGender(gender);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public int getAge() {
        return super.getAge();
    }

    @Override
    public String getEmail() {
        return super.getEmail();
    }

    @Override
    public String getGender() {
        return super.getGender();
    }
}
