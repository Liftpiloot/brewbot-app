package com.example.brewbot;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Date;

public class User{
    private String token;
    private int age;
    private String gender;
    private String username;
    private String email;
    private String recentTapMachine;
    private ArrayList<int[]> pastWeekStats;
    private Date lastBeer;
    private int profile;

    private static User instance;

    private User() {
    }


    public static User getInstance() {
        if (instance == null){
            instance = new User();}
        return instance;
    }

    public ArrayList<int[]> getPastWeekStats() {
        return pastWeekStats;
    }

    public void setPastWeekStats(ArrayList<int[]> pastWeekStats) {
        this.pastWeekStats = pastWeekStats;
    }

    public int[] getPastWeekTypes(){
        int pils = 0;
        int special = 0;
        int zero = 0;
        for (int[] day: pastWeekStats){
            pils += day[1];
            special += day[2];
            zero += day[3];
        }
        return new int[]{pils, special, zero};
    }

    public Object[] getProfile() {
        switch (profile) {
            case 0:
                return new Object[]{"Responsible drinker!", 0};
            case 1:
                return new Object[]{"Casual drinker", 1};
            case 2:
                return new Object[]{"Alcoholic :(", 2};
            case 3:
                return new Object[]{"Special beer lover", 3};
            default:
                return new Object[]{"none", -1};
        }
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRecentTapMachine() {
        return recentTapMachine;
    }

    public void setRecentTapMachine(String recentTapMachine) {
        this.recentTapMachine = recentTapMachine;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
