package com.emretopcu.reflexive.presenters;

import android.content.Context;

import com.emretopcu.reflexive.interfaces.Interface_Main;
import com.emretopcu.reflexive.models.Common_Parameters;
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
        if(User_Preferences.getInstance().isAudioEnabled() == true){
            onAudioEnabled();
        }
        else{
            onAudioDisabled();
        }
        view.setUsername(User_Preferences.getInstance().getUsername());
        view.setClassicBest(User_Preferences.getInstance().getClassicBest());
        view.setArcadeBest(User_Preferences.getInstance().getArcadeBest());
        view.setSurvivalBest(User_Preferences.getInstance().getSurvivalBest());
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
        view.openArcade();
    }

    public void onSurvivalClicked(){
        view.openSurvival();
    }

    public void onLeaderboardClicked(){
        if(Leaderboard_Info.getInstance().getClassicLeaderboard()[Common_Parameters.NUMBER_OF_LEADERBOARD_USERS-1].getBest() >= 0
            && Leaderboard_Info.getInstance().getArcadeLeaderboard()[Common_Parameters.NUMBER_OF_LEADERBOARD_USERS-1].getBest() >= 0
            && Leaderboard_Info.getInstance().getSurvivalLeaderboard()[Common_Parameters.NUMBER_OF_LEADERBOARD_USERS-1].getBest() >= 0) {
            view.openLeaderboard();
        }
        else{
            view.showDatabaseErrorToast();
        }
    }

}
