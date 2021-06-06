package com.emretopcu.reflexive.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.emretopcu.reflexive.R;
import com.emretopcu.reflexive.interfaces.Interface_Classic_Menu;
import com.emretopcu.reflexive.presenters.Presenter_Classic_Menu;

public class Activity_Classic_Menu extends AppCompatActivity implements Interface_Classic_Menu {

    private MediaPlayer mediaPlayerIntro;
    private MediaPlayer mediaPlayerGame;
    private Button buttonAudioEnabled;
    private Button buttonAudioDisabled;
    private RecyclerViewAdapter_Classic_Menu adapter;
    private RecyclerView recyclerView_classic_menu;
    private LinearLayoutManager layoutManager;

    private Presenter_Classic_Menu presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_classic_menu);

        mediaPlayerIntro = Activity_Initial.mediaPlayerIntro;
        mediaPlayerGame = Activity_Initial.mediaPlayerGame;

        buttonAudioEnabled = findViewById(R.id.button_classic_menu_audio_on);
        buttonAudioDisabled = findViewById(R.id.button_classic_menu_audio_off);

        recyclerView_classic_menu = findViewById(R.id.recyclerView_classic_menu);
        recyclerView_classic_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);

        presenter = new Presenter_Classic_Menu(getApplicationContext(), this);

        buttonAudioEnabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onAudioDisabled();
            }
        });

        buttonAudioDisabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onAudioEnabled();
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

    public void onButtonClickedAtPosition(int selectedPosition){
        presenter.onLevelSelectedAtPosition(selectedPosition);
    }

    @Override
    public void setAudioEnabled() {
        buttonAudioEnabled.setVisibility(View.VISIBLE);
        buttonAudioDisabled.setVisibility(View.INVISIBLE);
        if(!mediaPlayerIntro.isPlaying()){
            mediaPlayerIntro.start();
        }
        if(mediaPlayerGame.isPlaying()){
            mediaPlayerGame.pause();
        }
    }

    @Override
    public void setAudioDisabled() {
        buttonAudioEnabled.setVisibility(View.INVISIBLE);
        buttonAudioDisabled.setVisibility(View.VISIBLE);
        if(mediaPlayerIntro.isPlaying()){
            mediaPlayerIntro.pause();
        }
        if(mediaPlayerGame.isPlaying()){
            mediaPlayerGame.pause();
        }
    }

    @Override
    public void mute() {
        if(mediaPlayerIntro.isPlaying()){
            mediaPlayerIntro.pause();
        }
    }

    @Override
    public void showLevels(int maxUnlockedPosition) {
        adapter = new RecyclerViewAdapter_Classic_Menu(maxUnlockedPosition,this);
        recyclerView_classic_menu.setLayoutManager(layoutManager);
        recyclerView_classic_menu.setAdapter(adapter);
        layoutManager.scrollToPositionWithOffset(maxUnlockedPosition, 15);
    }

    @Override
    public void openGame() {
        Intent i = new Intent(getApplicationContext(), Activity_Classic_Game.class);
        startActivity(i);
    }

    @Override
    public void openMain() {
        Intent i = new Intent(getApplicationContext(), Activity_Main.class);
        startActivity(i);
    }
}