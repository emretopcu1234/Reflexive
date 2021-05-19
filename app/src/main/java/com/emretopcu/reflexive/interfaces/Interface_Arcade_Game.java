package com.emretopcu.reflexive.interfaces;

public interface Interface_Arcade_Game {
    void setFragment(int fragmentType);
    void setAudioEnabled();
    void setAudioDisabled();
    void setBest(String best);
    void setTarget(String target);
    void setScore(String score);
    void setScoreColorDefault();
    void setScoreColorGreen();
    void setTime(String time);
    void setPause();
    void setPlay();
    void openHowToPlay();
    void dismissHowToPlay();
    void openCountToStart();
    void editCountToStart(int second);
    void dismissCountToStart();
}

