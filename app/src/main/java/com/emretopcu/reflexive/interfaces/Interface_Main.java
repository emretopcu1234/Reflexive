package com.emretopcu.reflexive.interfaces;

public interface Interface_Main {
    void setAudioEnabled();
    void setAudioDisabled();
    void mute();
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
    void setArcadeButtonColor(int colorType);
    void setSurvivalButtonColor(int colorType);
    void showArcadeToast();
    void showSurvivalToast();
}
