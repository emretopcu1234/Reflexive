package com.emretopcu.reflexive.presenters;

import android.content.Context;

import com.emretopcu.reflexive.interfaces.Interface_Leaderboard;
import com.emretopcu.reflexive.models.Common_Parameters_Variables;
import com.emretopcu.reflexive.models.Leaderboard_Info;
import com.emretopcu.reflexive.models.User_Info;
import com.emretopcu.reflexive.models.User_Preferences;

public class Presenter_Leaderboard {

    private Context context;
    private Interface_Leaderboard view;

    public Presenter_Leaderboard(Context context, Interface_Leaderboard view) {
        this.context = context;
        this.view = view;
    }

    public void onActivityResumed(){
        if (User_Preferences.getInstance().isAudioEnabled()) {
            onAudioEnabled();
        } else {
            onAudioDisabled();
        }
        int numberOnLeaderboard = -1;
        for(int i = 0; i< Common_Parameters_Variables.NUMBER_OF_LEADERBOARD_USERS; i++){
            if(User_Info.getInstance().getUsername().equals(Leaderboard_Info.getInstance().getClassicLeaderboard()[i].getUsername())){
                numberOnLeaderboard = i;
                break;
            }
        }
        view.showListAccordingToMode(0, numberOnLeaderboard);
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

    public void onModeSelected(int selectedMode){
        int numberOnLeaderboard = -1;
        if(selectedMode == 0){
            for(int i = 0; i< Common_Parameters_Variables.NUMBER_OF_LEADERBOARD_USERS; i++){
                if(User_Info.getInstance().getUsername().equals(Leaderboard_Info.getInstance().getClassicLeaderboard()[i].getUsername())){
                    numberOnLeaderboard = i;
                    break;
                }
            }
        }
        else if(selectedMode == 1){
            for(int i = 0; i< Common_Parameters_Variables.NUMBER_OF_LEADERBOARD_USERS; i++){
                if(User_Info.getInstance().getUsername().equals(Leaderboard_Info.getInstance().getArcadeLeaderboard()[i].getUsername())){
                    numberOnLeaderboard = i;
                    break;
                }
            }
        }
        else{
            for(int i = 0; i< Common_Parameters_Variables.NUMBER_OF_LEADERBOARD_USERS; i++){
                if(User_Info.getInstance().getUsername().equals(Leaderboard_Info.getInstance().getSurvivalLeaderboard()[i].getUsername())){
                    numberOnLeaderboard = i;
                    break;
                }
            }
        }

        view.showListAccordingToMode(selectedMode, numberOnLeaderboard);
    }

    public void onBackPressed(){
        view.openMain();
    }
}
