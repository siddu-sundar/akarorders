package com.akarcontrols.akarorders;

public class UserHelperClass {

    String customer_name, model, capacity, accuracy, quantity, loadcell, description, brand, stamping, delivery;
    String phone_number;
    String reference;
    String intime;
    String company_name;
    String status;
    String date;
    String reason;


    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getPerson_name() {
        return person_name;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    String person_name;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getIntime() {
        return intime;
    }

    public void setIntime(String intime) {
        this.intime = intime;
    }
    public String getCustomer_name() {
        return customer_name;
    }

    public UserHelperClass() {
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getLoadcell() {
        return loadcell;
    }

    public void setLoadcell(String loadcell) {
        this.loadcell = loadcell;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone_number() {
        return phone_number+"";
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getStamping() {
        return stamping;
    }

    public void setStamping(String stamping) {
        this.stamping = stamping;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public UserHelperClass(String customer_name, String model, String capacity, String accuracy, String delivery,
                           String quantity, String loadcell, String description, String phone_number, String brand, String stamping, String reference, String status, String date) {
        this.customer_name = customer_name;
        this.model = model;
        this.capacity = capacity;
        this.date = date;
        this.accuracy = accuracy;
        this.reference = reference;
        this.status = status;
        this.delivery = delivery;
        this.quantity = quantity;
        this.loadcell = loadcell;
        this.description = description;
        this.phone_number = phone_number;
        this.brand = brand;
        this.stamping = stamping;
    }
}
