package com.emretopcu.reflexive.presenters;

import android.content.Context;

import com.emretopcu.reflexive.interfaces.Interface_Classic_Menu;
import com.emretopcu.reflexive.models.Common_Parameters_Variables;
import com.emretopcu.reflexive.models.User_Preferences;

public class Presenter_Classic_Menu {

    private Context context;
    private Interface_Classic_Menu view;

    public Presenter_Classic_Menu(Context context, Interface_Classic_Menu view){
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
        view.showLevels(User_Preferences.getInstance().getMaxUnlockedClassicLevel() -1);  // level x = position x-1.
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

    public void onLevelSelectedAtPosition(int selectedPosition){
        Common_Parameters_Variables.CURRENT_CLASSIC_LEVEL = selectedPosition + 1;
        view.openGame();
    }

    public void onBackPressed(){
        view.openMain();
    }

}
