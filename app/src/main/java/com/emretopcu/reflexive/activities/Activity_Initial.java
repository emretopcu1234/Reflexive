package com.emretopcu.reflexive.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.emretopcu.reflexive.R;
import com.emretopcu.reflexive.models.Common_Parameters_Variables;
import com.emretopcu.reflexive.models.Database_Manager;
import com.emretopcu.reflexive.models.User_Info;
import com.emretopcu.reflexive.models.User_Preferences;
import com.google.android.gms.ads.MobileAds;

public class Activity_Initial extends AppCompatActivity {
    
    // TODO uygulamayı playstore'a yükleyince admob'da bunu belirt.

    
    public static MediaPlayer mediaPlayerIntro;
    public static MediaPlayer mediaPlayerGame;
    public static MediaPlayer mediaPlayerRight_0;
    public static MediaPlayer mediaPlayerRight_1;
    public static MediaPlayer mediaPlayerRight_2;
    public static MediaPlayer mediaPlayerWrong_0;
    public static MediaPlayer mediaPlayerWrong_1;
    public static MediaPlayer mediaPlayerWrong_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MobileAds.initialize(this);
        User_Preferences.initialize(this);

        mediaPlayerIntro = MediaPlayer.create(getApplicationContext(), R.raw.intro);
        mediaPlayerIntro.setLooping(true);

        mediaPlayerGame = MediaPlayer.create(getApplicationContext(), R.raw.game);
        mediaPlayerGame.setLooping(true);

        mediaPlayerRight_0 = MediaPlayer.create(getApplicationContext(), R.raw.right);
        mediaPlayerRight_0.setLooping(false);

        mediaPlayerRight_1 = MediaPlayer.create(getApplicationContext(), R.raw.right);
        mediaPlayerRight_1.setLooping(false);

        mediaPlayerRight_2 = MediaPlayer.create(getApplicationContext(), R.raw.right);
        mediaPlayerRight_2.setLooping(false);

        mediaPlayerWrong_0 = MediaPlayer.create(getApplicationContext(), R.raw.wrong);
        mediaPlayerWrong_0.setLooping(false);

        mediaPlayerWrong_1 = MediaPlayer.create(getApplicationContext(), R.raw.wrong);
        mediaPlayerWrong_1.setLooping(false);

        mediaPlayerWrong_2 = MediaPlayer.create(getApplicationContext(), R.raw.wrong);
        mediaPlayerWrong_2.setLooping(false);

        Common_Parameters_Variables.LAST_AD_TIME = System.currentTimeMillis();

        if(User_Preferences.getInstance().getUsername().equals("TEMPFORFIRSTENTRANCE")){
            User_Info.getInstance().setUsername("TEMPFORFIRSTENTRANCE");
            User_Info.getInstance().setClassicBest(0);
            User_Info.getInstance().setArcadeBest(0);
            User_Info.getInstance().setSurvivalBest(0);
            Intent i = new Intent(getApplicationContext(), Activity_First_Entrance.class);
            startActivity(i);
        }
        else{
            Database_Manager.getInstance().getLeaderboardInfo();
            Common_Parameters_Variables.IS_NEW_LEADERBOARD_VISITED = true;  // visit edilmese bile veritabanından bilgiler alındı.
            User_Info.getInstance().setUsername(User_Preferences.getInstance().getUsername());
            User_Info.getInstance().setClassicBest(Integer.parseInt(User_Preferences.getInstance().getClassicBest()));
            User_Info.getInstance().setArcadeBest(Integer.parseInt(User_Preferences.getInstance().getArcadeBest()));
            User_Info.getInstance().setSurvivalBest(Integer.parseInt(User_Preferences.getInstance().getSurvivalBest()));
            Intent i = new Intent(getApplicationContext(), Activity_Main.class);
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        // nothing  (zaten herhangi bir layout olmadığı için buraya bile düşmeyecek.)
    }
}