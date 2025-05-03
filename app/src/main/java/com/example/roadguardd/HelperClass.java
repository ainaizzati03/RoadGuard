package com.example.roadguardd;

public class HelperClass {
    private String IC, name, phone, password;

    public HelperClass() {
    }

    public HelperClass(String IC, String name, String phone, String password) {
        this.IC = IC;
        this.name = name;
        this.phone = phone;
        this.password = password;
    }

    public String getIC() {
        return IC;
    }

    public void setIC(String IC) {
        this.IC = IC;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
