package com.emretopcu.reflexive.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.emretopcu.reflexive.R;
import com.emretopcu.reflexive.interfaces.Interface_Leaderboard;
import com.emretopcu.reflexive.presenters.Presenter_Leaderboard;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import javax.annotation.Nullable;

public class Activity_Leaderboard extends AppCompatActivity implements Interface_Leaderboard {

    private MediaPlayer mediaPlayerIntro;

    private RecyclerViewAdapter_Leaderboard adapter;
    private RecyclerView recyclerView_leaderboard;
    private LinearLayoutManager layoutManager;

    private Button buttonClassic;
    private Button buttonArcade;
    private Button buttonSurvival;

    private Presenter_Leaderboard presenter;

    private AdView adView;
    private AdRequest adRequestBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_leaderboard);

        adView = findViewById(R.id.adView_leaderboard);
        adRequestBanner = new AdRequest.Builder().build();
        adView.loadAd(adRequestBanner);

        mediaPlayerIntro = Activity_Initial.mediaPlayerIntro;

        recyclerView_leaderboard = findViewById(R.id.recyclerView_leaderboard);
        recyclerView_leaderboard.addItemDecoration(new DividerItemDecoration(recyclerView_leaderboard.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView_leaderboard.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);

        buttonClassic = findViewById(R.id.button_leaderboard_classic);
        buttonArcade = findViewById(R.id.button_leaderboard_arcade);
        buttonSurvival = findViewById(R.id.button_leaderboard_survival);

        presenter = new Presenter_Leaderboard(getApplicationContext(), this);

        buttonClassic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onModeSelected(0);
            }
        });

        buttonArcade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onModeSelected(1);
            }
        });

        buttonSurvival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onModeSelected(2);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onActivityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onActivityPaused();
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @Override
    public void setAudioEnabled() {
        if(!mediaPlayerIntro.isPlaying()){
            mediaPlayerIntro.start();
        }
    }

    @Override
    public void setAudioDisabled() {
        if(mediaPlayerIntro.isPlaying()){
            mediaPlayerIntro.pause();
        }
    }

    @Override
    public void mute() {
        if(mediaPlayerIntro.isPlaying()){
            mediaPlayerIntro.pause();
        }
    }

    @Override
    public void showListAccordingToMode(int selectedMode, int numberOnLeaderboard) {
        if(selectedMode == 0){
            buttonClassic.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            buttonArcade.setBackgroundColor(getResources().getColor(R.color.intermediate_gray));
            buttonSurvival.setBackgroundColor(getResources().getColor(R.color.intermediate_gray));
        }
        else if(selectedMode == 1){
            buttonClassic.setBackgroundColor(getResources().getColor(R.color.intermediate_gray));
            buttonArcade.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            buttonSurvival.setBackgroundColor(getResources().getColor(R.color.intermediate_gray));
        }
        else{
            buttonClassic.setBackgroundColor(getResources().getColor(R.color.intermediate_gray));
            buttonArcade.setBackgroundColor(getResources().getColor(R.color.intermediate_gray));
            buttonSurvival.setBackgroundColor(getResources().getColor(R.color.dark_gray));
        }

        adapter = new RecyclerViewAdapter_Leaderboard(this, selectedMode, numberOnLeaderboard);
        recyclerView_leaderboard.setLayoutManager(layoutManager);
        recyclerView_leaderboard.setAdapter(adapter);
    }

    @Override
    public void openMain() {
        Intent i = new Intent(getApplicationContext(), Activity_Main.class);
        startActivity(i);
    }
}