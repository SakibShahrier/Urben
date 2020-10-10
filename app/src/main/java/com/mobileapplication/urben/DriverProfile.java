package com.mobileapplication.urben;

public class DriverProfile extends UserProfile {

    private String busNumber;
    private double revenue;
    private double withdraw;
    private String routeName;
    private String serviceType;

    public DriverProfile(String userName, String email, String serviceType, String routeName){
        super();
        super.setName(userName);
        super.setEmail(email);
        this.busNumber = "None";
        this. revenue = 0.00;
        this.withdraw = 0.00;
        this.serviceType = serviceType;
        this.routeName = routeName;
    }

    public DriverProfile() {
        super();
        busNumber = "None";
        revenue = 0.00;
        withdraw = 0.00;
        serviceType = "None";
        routeName = "None";
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getRouteName() {
        return routeName;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public void setEmail(String email) {
        super.setEmail(email);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String getEmail() {
        return super.getEmail();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public double getRevenue() {
        return revenue;
    }

    public double getWithdraw() {
        return withdraw;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public void setWithdraw(double withdraw) {
        this.withdraw = withdraw;
    }

}
