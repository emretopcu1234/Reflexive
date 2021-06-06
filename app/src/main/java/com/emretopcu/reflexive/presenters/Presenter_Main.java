package com.emretopcu.reflexive.presenters;

import android.content.Context;
import android.util.Log;

import com.emretopcu.reflexive.interfaces.Interface_Main;
import com.emretopcu.reflexive.models.Common_Parameters_Variables;
import com.emretopcu.reflexive.models.Database_Manager;
import com.emretopcu.reflexive.models.Leaderboard_Info;
import com.emretopcu.reflexive.models.User_Preferences;

public class Presenter_Main {

    private Context context;
    private Interface_Main view;

    public Presenter_Main(Context context, Interface_Main view) {
        this.context = context;
        this.view = view;
    }

    public void onActivityResumed(){
        if(User_Preferences.getInstance().isAudioEnabled()){
            onAudioEnabled();
        }
        else{
            onAudioDisabled();
        }
        view.setUsername(User_Preferences.getInstance().getUsername());
        view.setClassicBest(User_Preferences.getInstance().getClassicBest());
        view.setArcadeBest(User_Preferences.getInstance().getArcadeBest());
        view.setSurvivalBest(User_Preferences.getInstance().getSurvivalBest());
        if(User_Preferences.getInstance().getMaxUnlockedClassicLevel() < Common_Parameters_Variables.ARCADE_UNLOCK_LEVEL){
            view.setArcadeButtonColor(0);
        }
        else{
            view.setArcadeButtonColor(1);
        }
        if(User_Preferences.getInstance().getMaxUnlockedClassicLevel() < Common_Parameters_Variables.SURVIVAL_UNLOCK_LEVEL){
            view.setSurvivalButtonColor(0);
        }
        else{
            view.setSurvivalButtonColor(1);
        }
    }

    public void onActivityPaused(){
        view.mute();
    }

    public void onAudioEnabled() {
        User_Preferences.getInstance().setAudioEnabled(true);
        view.setAudioEnabled();
    }

    public void onAudioDisabled() {
        User_Preferences.getInstance().setAudioEnabled(false);
        view.setAudioDisabled();
    }

    public void onUsernameEditStarted() {
        view.openUsernameDialog(User_Preferences.getInstance().getUsername());
    }

    public void onUsernameEditRequested(String newUsername){
        Database_Manager.getInstance().requestUsernameEdit(this, newUsername);
    }

    public void onUsernameEditResponded(boolean isEditAccepted, String newUsername){
        if(isEditAccepted){
            User_Preferences.getInstance().setUsername(newUsername);
            view.dismissUsernameDialog();
            view.setUsername(newUsername);
        }
        else{
            view.showWarningOnUsernameDialog();
        }
    }

    public void onClassicClicked(){
        view.openClassic();
    }

    public void onArcadeClicked(){
        if(User_Preferences.getInstance().getMaxUnlockedClassicLevel() < Common_Parameters_Variables.ARCADE_UNLOCK_LEVEL){
            view.showArcadeToast();
        }
        else{
            view.openArcade();
        }
    }

    public void onSurvivalClicked(){
        if(User_Preferences.getInstance().getMaxUnlockedClassicLevel() < Common_Parameters_Variables.SURVIVAL_UNLOCK_LEVEL){
            view.showSurvivalToast();
        }
        else{
            view.openSurvival();
        }
    }

    public void onLeaderboardClicked(){
        if(!Common_Parameters_Variables.IS_NEW_LEADERBOARD_VISITED){
            Database_Manager.getInstance().requestLeaderboardInfo(this);
            Common_Parameters_Variables.IS_NEW_LEADERBOARD_VISITED = true;
        }
        else{
            if(Leaderboard_Info.getInstance().getClassicLeaderboard()[Common_Parameters_Variables.NUMBER_OF_LEADERBOARD_USERS-1].getBest() >= 0
                    && Leaderboard_Info.getInstance().getArcadeLeaderboard()[Common_Parameters_Variables.NUMBER_OF_LEADERBOARD_USERS-1].getBest() >= 0
                    && Leaderboard_Info.getInstance().getSurvivalLeaderboard()[Common_Parameters_Variables.NUMBER_OF_LEADERBOARD_USERS-1].getBest() >= 0) {
                view.openLeaderboard();
            }
            else{
                view.showDatabaseErrorToast();
            }
        }
    }

    public void onLeaderboardInfoResponded(){
        view.openLeaderboard();
    }

    public void onBackPressed(){
        // nothing
    }
}
