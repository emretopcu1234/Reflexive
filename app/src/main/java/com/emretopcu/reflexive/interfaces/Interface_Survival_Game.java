package com.emretopcu.reflexive.interfaces;

public interface Interface_Survival_Game {
    void setFragment(int fragmentType);
    void setAudioEnabled();
    void setAudioDisabled();
    void setBest(String best);
    void setScore(String score);
    void setChances(int remainingChances);
    void setPause();
    void setPlay();
    void openHowToPlay();
    void dismissHowToPlay();
    void openCountToStart();
    void editCountToStart(int second);
    void dismissCountToStart();
    void setButtonColor(int buttonIndex, int colorType);
    void setButtonsVisible();
    void setButtonsInvisible();
    void playRight(int index);
    void playWrong(int index);
    void openEndGame(boolean isBest, int score);
    void dismissEndGame(boolean isBest);
    void openMain();
}
