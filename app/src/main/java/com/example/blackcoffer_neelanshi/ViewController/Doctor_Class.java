package com.example.blackcoffer_neelanshi.ViewController;

public class Doctor_Class {
    private String Name, Hospital, Location, Specialization, From_time, To_time, Gender;
    private int Fees;
    private Long Contact;

    public Doctor_Class() {}

    public String getName() {
        return Name;
    }

    public String getGender() { return Gender; }

    public String getHospital() {
        return Hospital;
    }

    public String getSpecialization() {
        return Specialization;
    }

    public String getLocation(){ return Location; }

    public String getFrom_time() {
        return From_time;
    }

    public String getTo_time() {
        return To_time;
    }

    public int getFees() {
        return Fees;
    }

    public Long getContact() {
        return Contact;
    }
}
