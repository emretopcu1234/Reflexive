package com.emretopcu.reflexive.models;

public class User_Info {

    private static User_Info INSTANCE = new User_Info();
    private String username;
    private int classicBest;
    private int arcadeBest;
    private int survivalBest;

    public static User_Info getInstance(){
        return INSTANCE;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getClassicBest() {
        return classicBest;
    }

    public void setClassicBest(int classicBest) {
        this.classicBest = classicBest;
    }

    public int getArcadeBest() {
        return arcadeBest;
    }

    public void setArcadeBest(int arcadeBest) {
        this.arcadeBest = arcadeBest;
    }

    public int getSurvivalBest() {
        return survivalBest;
    }

    public void setSurvivalBest(int survivalBest) {
        this.survivalBest = survivalBest;
    }

}

