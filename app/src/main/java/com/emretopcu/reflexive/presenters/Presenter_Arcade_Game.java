package com.emretopcu.reflexive.presenters;

import android.content.Context;
import android.os.CountDownTimer;

import com.emretopcu.reflexive.R;
import com.emretopcu.reflexive.interfaces.Interface_Arcade_Game;
import com.emretopcu.reflexive.models.Common_Parameters;
import com.emretopcu.reflexive.models.User_Preferences;

public class Presenter_Arcade_Game {

    private Context context;
    private Interface_Arcade_Game view;
    private final String baseBest;
    private final String baseTarget;
    private final String baseScore;
    private boolean isPlayActive;

    public Presenter_Arcade_Game(Context context, Interface_Arcade_Game view) {
        this.context = context;
        this.view = view;
        baseBest = context.getString(R.string.arcade_game_best);
        baseTarget = context.getString(R.string.arcade_game_target);
        baseScore = context.getString(R.string.arcade_game_score);
    }

    public void onActivityResumed(){
        view.setFragment(Common_Parameters.FRAGMENT_TYPE_ARCADE[0]);    // oyunda ilerlendikçe index artacak.
        if(User_Preferences.getInstance().isAudioEnabled()){
            onAudioEnabled();
        }
        else{
            onAudioDisabled();
        }
        isPlayActive = true;
        view.setPause();
        view.setBest(baseBest + User_Preferences.getInstance().getArcadeBest());
        view.setTarget(baseTarget + Integer.toString(Common_Parameters.TARGET_ARCADE[0]));  // oyunda ilerlendikçe index artacak.
        view.setScoreColorDefault();
        view.setScore(baseScore + "0");
        view.setTime(Integer.toString(Common_Parameters.TIME_ARCADE[0]));   // oyunda ilerlendikçe index artacak.
        if(User_Preferences.getInstance().isArcadeFirstEntrance()){
            view.openHowToPlay();
        }
        else{
            countToStart();
        }
    }

    public void onPlayClicked(){
        isPlayActive = true;
        view.setPause();
    }

    public void onPauseClicked(){
        isPlayActive = false;
        view.setPlay();
    }

    public void onQuestionClicked(){
        view.setPlay();
        view.openHowToPlay();
    }

    public void onQuestionDismissRequested(){
        view.dismissHowToPlay();
        if(User_Preferences.getInstance().isArcadeFirstEntrance()){
            User_Preferences.getInstance().setArcadeFirstEntrance(false);
            countToStart();
        }
        if(isPlayActive){
            view.setPause();
        }
        else{
            view.setPlay();
        }
    }

    public void onAudioEnabled() {
        User_Preferences.getInstance().setAudioEnabled(true);
        view.setAudioEnabled();
    }

    public void onAudioDisabled() {
        User_Preferences.getInstance().setAudioEnabled(false);
        view.setAudioDisabled();
    }

    private void countToStart(){
        view.openCountToStart();
        new CountDownTimer(3000, 100){
            // 3000, 1000 yapınca onTick'in içine 2 kere giriyor.
            // 3.sefer girmesi için 4000, 1000 yapmak gerekiyor, bu kez de 1 saniye fazladan bekleniyor.
            int remainingSecond = 3;
            int count = 0;
            @Override
            public void onTick(long millisUntilFinished) {
                if(count%10 == 0){
                    view.editCountToStart(remainingSecond);
                    remainingSecond--;
                }
                count++;
            }

            @Override
            public void onFinish() {
                view.dismissCountToStart();
            }
        }.start();
    }

}

