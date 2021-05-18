package com.emretopcu.reflexive.interfaces;

public interface Interface_Main {
    void setAudioEnabled();
    void setAudioDisabled();
    void openUsernameDialog(String username);
    void dismissUsernameDialog();
    void showWarningOnUsernameDialog();
    void setUsername(String username);
    void setClassicBest(String classicBest);
    void setArcadeBest(String arcadeBest);
    void setSurvivalBest(String survivalBest);
    void openClassic();
    void openArcade();
    void openSurvival();
    void openLeaderboard();
    void showDatabaseErrorToast();
}
