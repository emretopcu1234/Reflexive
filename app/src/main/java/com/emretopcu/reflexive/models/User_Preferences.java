package com.emretopcu.reflexive.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.emretopcu.reflexive.activities.Activity_Initial;

public class User_Preferences {

    private static User_Preferences INSTANCE = new User_Preferences();
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor prefsEditor;

    public static void initialize(Activity_Initial activityInitial){
        prefs = activityInitial.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        prefsEditor = prefs.edit();
    }

    public static User_Preferences getInstance(){
        return INSTANCE;
    }

    public String getUsername() {
        return prefs.getString("username","TEMPFORFIRSTENTRANCE");
    }

    public void setUsername(String username) {
        prefsEditor.putString("username", username);
        prefsEditor.commit();
    }

    public String getClassicBest() {
        return prefs.getString("classic_best","0");
    }

    public void setClassicBest(String classicBest) {
        prefsEditor.putString("classic_best", classicBest);
        prefsEditor.commit();
    }

    public String getArcadeBest() {
        return prefs.getString("arcade_best","0");
    }

    public void setArcadeBest(String arcadeBest) {
        prefsEditor.putString("arcade_best", arcadeBest);
        prefsEditor.commit();
    }

    public String getSurvivalBest() {
        return prefs.getString("survival_best","0");
    }

    public void setSurvivalBest(String survivalBest) {
        prefsEditor.putString("survival_best", survivalBest);
        prefsEditor.commit();
    }

    public boolean isAudioEnabled() {
        return prefs.getBoolean("is_audio_enabled",true);
    }

    public void setAudioEnabled(boolean isAudioEnabled) {
        prefsEditor.putBoolean("is_audio_enabled", isAudioEnabled);
        prefsEditor.commit();
    }

    public String getClassicBestLevel(int currentLevel){
        return prefs.getString("classic_best_level_" + currentLevel, "0");
    }

    public void setClassicBestLevel(int currentLevel, String best){
        prefsEditor.putString("classic_best_level_" + currentLevel, best);
        prefsEditor.commit();
    }

    public int getMaxUnlockedClassicLevel(){
        return prefs.getInt("max_unlocked_classic_level", 1);
    }

    public void setMaxUnlockedClassicLevel(int maxUnlockedClassicLevel){
        prefsEditor.putInt("max_unlocked_classic_level", maxUnlockedClassicLevel);
        prefsEditor.commit();
    }

    public boolean isClassicFirstEntrance(){
        return prefs.getBoolean("is_classic_first_entrance", true);
    }

    public void setClassicFirstEntrance(boolean isClassicFirstEntrance){
        prefsEditor.putBoolean("is_classic_first_entrance", isClassicFirstEntrance);
        prefsEditor.commit();
    }

    public boolean isArcadeFirstEntrance(){
        return prefs.getBoolean("is_arcade_first_entrance", true);
    }

    public void setArcadeFirstEntrance(boolean isArcadeFirstEntrance){
        prefsEditor.putBoolean("is_arcade_first_entrance", isArcadeFirstEntrance);
        prefsEditor.commit();
    }

    public boolean isSurvivalFirstEntrance(){
        return prefs.getBoolean("is_survival_first_entrance", true);
    }

    public void setSurvivalFirstEntrance(boolean isSurvivalFirstEntrance){
        prefsEditor.putBoolean("is_survival_first_entrance", isSurvivalFirstEntrance);
        prefsEditor.commit();
    }
}
