package com.emretopcu.reflexive.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.emretopcu.reflexive.R;
import com.emretopcu.reflexive.fragments.Fragment_4x4;
import com.emretopcu.reflexive.fragments.Fragment_5x5;
import com.emretopcu.reflexive.fragments.Fragment_6x6;
import com.emretopcu.reflexive.interfaces.Interface_Fragment;
import com.emretopcu.reflexive.interfaces.Interface_General_Game_Activity;
import com.emretopcu.reflexive.interfaces.Interface_Survival_Game;
import com.emretopcu.reflexive.models.Common_Parameters;
import com.emretopcu.reflexive.presenters.Presenter_Survival_Game;

public class Activity_Survival_Game extends AppCompatActivity implements Interface_Survival_Game, Interface_General_Game_Activity {

    private MediaPlayer mediaPlayerIntro;
    private MediaPlayer mediaPlayerGame;
    private Button buttonPlay;
    private Button buttonPause;
    private Button buttonQuestion;
    private Button buttonAudioEnabled;
    private Button buttonAudioDisabled;
    private TextView textViewBest;
    private TextView textViewScore;
    private Button[] buttonChances;
    private AlertDialog.Builder builderHowToPlay;
    private AlertDialog.Builder builderCountToStart;
    private View viewHowToPlayDialog;
    private View viewCountToStartDialog;
    private AlertDialog alertDialogHowToPlay;
    private AlertDialog alertDialogCountToStart;

    private Interface_Fragment fragment;
    private Presenter_Survival_Game presenter;

    private boolean isAudioEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_survival_game);

        mediaPlayerIntro = Activity_Initial.mediaPlayerIntro;
        mediaPlayerGame = Activity_Initial.mediaPlayerGame;

        buttonPlay = findViewById(R.id.button_survival_game_play);
        buttonPause = findViewById(R.id.button_survival_game_pause);
        buttonQuestion = findViewById(R.id.button_survival_game_question);
        buttonAudioEnabled = findViewById(R.id.button_survival_game_audio_on);
        buttonAudioDisabled = findViewById(R.id.button_survival_game_audio_off);

        textViewBest = findViewById(R.id.textView_survival_game_best);
        textViewScore = findViewById(R.id.textView_survival_game_score);

        buttonChances = new Button[Common_Parameters.NUMBER_OF_SURVIVAL_CHANCES];
        buttonChances[0] = findViewById(R.id.button_survival_game_life0);
        buttonChances[1] = findViewById(R.id.button_survival_game_life1);
        buttonChances[2] = findViewById(R.id.button_survival_game_life2);
        for(int i=0; i<buttonChances.length; i++){
            buttonChances[i].setEnabled(false);
        }

        builderHowToPlay = new AlertDialog.Builder(this);
        viewHowToPlayDialog = this.getLayoutInflater().inflate(R.layout.dialog_how_to_play_survival, null);
        builderHowToPlay.setView(viewHowToPlayDialog);
        alertDialogHowToPlay = builderHowToPlay.create();
        alertDialogHowToPlay.setCancelable(false);

        builderCountToStart = new AlertDialog.Builder(this);
        viewCountToStartDialog = this.getLayoutInflater().inflate(R.layout.dialog_count_to_start, null);
        builderCountToStart.setView(viewCountToStartDialog);
        alertDialogCountToStart = builderCountToStart.create();
        alertDialogCountToStart.setCancelable(false);
        alertDialogCountToStart.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        presenter = new Presenter_Survival_Game(getApplicationContext(), this);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPlayClicked();
            }
        });

        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPauseClicked();
            }
        });

        buttonQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onQuestionClicked();
            }
        });

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
        if(mediaPlayerIntro.isPlaying()){
            mediaPlayerIntro.pause();
        }
        presenter.onActivityResumed();
    }

    @Override
    public void setFragment(int fragmentType) {
        if (fragmentType == 0) {
            fragment = new Fragment_4x4(this);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_survival_game, (Fragment) fragment, null)
                    .commit();
        }
        else if (fragmentType == 1) {
            fragment = new Fragment_5x5(this);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_survival_game, (Fragment) fragment, null)
                    .commit();
        }
        else{
            fragment = new Fragment_6x6(this);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_survival_game, (Fragment) fragment, null)
                    .commit();
        }
    }

    @Override
    public void setAudioEnabled() {
        buttonAudioEnabled.setVisibility(View.VISIBLE);
        buttonAudioDisabled.setVisibility(View.INVISIBLE);
        isAudioEnabled = true;
        if(!mediaPlayerGame.isPlaying()){
            mediaPlayerGame.start();
        }
    }

    @Override
    public void setAudioDisabled() {
        buttonAudioEnabled.setVisibility(View.INVISIBLE);
        buttonAudioDisabled.setVisibility(View.VISIBLE);
        isAudioEnabled = false;
        if(mediaPlayerGame.isPlaying()){
            mediaPlayerGame.pause();
        }
    }

    @Override
    public void setBest(String best) {
        textViewBest.setText(best);
    }

    @Override
    public void setScore(String score) {
        textViewScore.setText(score);
    }

    @Override
    public void setChances(int remainingChances) {
        for(int i=remainingChances; i<buttonChances.length; i++){
            buttonChances[i].setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setPause() {
        buttonPlay.setVisibility(View.INVISIBLE);
        buttonPause.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPlay() {
        buttonPlay.setVisibility(View.VISIBLE);
        buttonPause.setVisibility(View.INVISIBLE);
    }

    @Override
    public void openHowToPlay() {
        Button buttonOk = viewHowToPlayDialog.findViewById(R.id.button_how_to_play_survival_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onQuestionDismissRequested();
            }
        });
        alertDialogHowToPlay.show();
    }

    @Override
    public void dismissHowToPlay() {
        alertDialogHowToPlay.dismiss();
    }

    @Override
    public void openCountToStart() {
        alertDialogCountToStart.show();
    }

    @Override
    public void editCountToStart(int second) {
        TextView textViewTimeToStart = viewCountToStartDialog.findViewById(R.id.textView_time_to_start);
        textViewTimeToStart.setText(Integer.toString(second));
    }

    @Override
    public void dismissCountToStart() {
        alertDialogCountToStart.dismiss();
    }

    @Override
    public void onButtonClicked(int buttonIndex) {

    }
}