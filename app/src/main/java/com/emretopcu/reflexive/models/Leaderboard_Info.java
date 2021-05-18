package com.emretopcu.reflexive.models;

public class Leaderboard_Info {

    private static Leaderboard_Info INSTANCE = new Leaderboard_Info();
    private Leaderboard_Info_Object_Type[] classicLeaderboard;
    private Leaderboard_Info_Object_Type[] arcadeLeaderboard;
    private Leaderboard_Info_Object_Type[] survivalLeaderboard;


    private Leaderboard_Info(){
        classicLeaderboard = new Leaderboard_Info_Object_Type[Common_Parameters.NUMBER_OF_LEADERBOARD_USERS];
        arcadeLeaderboard = new Leaderboard_Info_Object_Type[Common_Parameters.NUMBER_OF_LEADERBOARD_USERS];
        survivalLeaderboard = new Leaderboard_Info_Object_Type[Common_Parameters.NUMBER_OF_LEADERBOARD_USERS];
        for(int i=0; i<Common_Parameters.NUMBER_OF_LEADERBOARD_USERS; i++){
            classicLeaderboard[i] = new Leaderboard_Info_Object_Type("",-1);
            arcadeLeaderboard[i] = new Leaderboard_Info_Object_Type("",-1);
            survivalLeaderboard[i] = new Leaderboard_Info_Object_Type("",-1);
        }
    }

    public static Leaderboard_Info getInstance(){
        return INSTANCE;
    }

    public Leaderboard_Info_Object_Type[] getClassicLeaderboard() {
        return classicLeaderboard;
    }

    public Leaderboard_Info_Object_Type[] getArcadeLeaderboard() {
        return arcadeLeaderboard;
    }

    public Leaderboard_Info_Object_Type[] getSurvivalLeaderboard() {
        return survivalLeaderboard;
    }
}