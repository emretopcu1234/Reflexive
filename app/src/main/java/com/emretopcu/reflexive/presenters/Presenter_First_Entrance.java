package com.emretopcu.reflexive.presenters;

import android.content.Context;

import com.emretopcu.reflexive.interfaces.Interface_First_Entrance;
import com.emretopcu.reflexive.models.Database_Manager;
import com.emretopcu.reflexive.models.User_Preferences;

public class Presenter_First_Entrance {

    private Context context;
    private Interface_First_Entrance view;

    public Presenter_First_Entrance(Context context, Interface_First_Entrance view) {
        this.context = context;
        this.view = view;
    }

    public void onActivityResumed(){
        view.openUsernameDialog();
    }

    public void onUsernameAddRequested(String newUsername){
        Database_Manager.getInstance().requestUsernameAdd(this, newUsername);
    }

    public void onUsernameAddResponded(boolean isEditAccepted, String newUsername){
        if(isEditAccepted){
            User_Preferences.getInstance().setUsername(newUsername);
            view.dismissUsernameDialog();
        }
        else{
            view.showWarningOnUsernameDialog();
        }
    }
}
