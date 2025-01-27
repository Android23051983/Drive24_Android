package com.example.drive24;

import java.util.Date;

public class ClientCars {
    private int id;
    private String first_name;
    private String last_name;
    private String email;
    private String phone;
    private String role;
    private int car_id;
    private String car_model;
    private String car_number;
    private float car_cost;
    private String city;
    private String country;
    private Date date_start;
    private Date date_end;
    private String status;

    public ClientCars() {

    }

    public ClientCars(int id, String first_name, String last_name, String email, String phone, String role, int car_id, String car_model, String car_number, float car_cost, String city, String country, Date date_start, Date date_end, String status) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.car_id = car_id;
        this.car_model = car_model;
        this.car_number = car_number;
        this.car_cost = car_cost;
        this.city = city;
        this.country = country;
        this.date_start = date_start;
        this.date_end = date_end;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getCar_id() {
        return car_id;
    }

    public void setCar_id(int car_id) {
        this.car_id = car_id;
    }

    public String getCar_model() {
        return car_model;
    }

    public void setCar_model(String car_model) {
        this.car_model = car_model;
    }

    public String getCar_number() {
        return car_number;
    }

    public void setCar_number(String car_number) {
        this.car_number = car_number;
    }

    public float getCar_cost() {
        return car_cost;
    }

    public void setCar_cost(float car_cost) {
        this.car_cost = car_cost;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getDate_start() {
        return date_start;
    }

    public void setDate_start(Date date_start) {
        this.date_start = date_start;
    }

    public Date getDate_end() {
        return date_end;
    }

    public void setDate_end(Date date_end) {
        this.date_end = date_end;
    }

    public String geStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
