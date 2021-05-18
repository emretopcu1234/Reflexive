package com.emretopcu.reflexive.models;

public class Leaderboard_Info_Object_Type {

    private String username;
    private int best;

    public Leaderboard_Info_Object_Type(String username, int best) {
        this.username = username;
        this.best = best;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getBest() {
        return best;
    }

    public void setBest(int best) {
        this.best = best;
    }
}
