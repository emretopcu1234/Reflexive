package com.emretopcu.reflexive.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.emretopcu.reflexive.R;
import com.emretopcu.reflexive.fragments.Fragment_4x4;
import com.emretopcu.reflexive.fragments.Fragment_5x5;
import com.emretopcu.reflexive.fragments.Fragment_6x6;
import com.emretopcu.reflexive.interfaces.Interface_Classic_Game;
import com.emretopcu.reflexive.interfaces.Interface_Fragment;
import com.emretopcu.reflexive.interfaces.Interface_General_Game_Activity;
import com.emretopcu.reflexive.presenters.Presenter_Classic_Game;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import javax.annotation.Nullable;

public class Activity_Classic_Game extends AppCompatActivity implements Interface_Classic_Game, Interface_General_Game_Activity {

    private MediaPlayer mediaPlayerIntro;
    private MediaPlayer mediaPlayerGame;
    private MediaPlayer mediaPlayerRight_0;
    private MediaPlayer mediaPlayerRight_1;
    private MediaPlayer mediaPlayerRight_2;
    private MediaPlayer mediaPlayerWrong_0;
    private MediaPlayer mediaPlayerWrong_1;
    private MediaPlayer mediaPlayerWrong_2;
    private Button buttonPlay;
    private Button buttonPause;
    private Button buttonQuestion;
    private Button buttonAudioEnabled;
    private Button buttonAudioDisabled;
    private TextView textViewBest;
    private TextView textViewTarget;
    private TextView textViewScore;
    private TextView textViewTime;
    private TextView textViewPaused_1;
    private TextView textViewPaused_2;
    private AlertDialog.Builder builderHowToPlay;
    private AlertDialog.Builder builderCountToStart;
    private AlertDialog.Builder builderEndGameWithBest;
    private AlertDialog.Builder builderEndGameWithoutBest;
    private View viewHowToPlayDialog;
    private View viewCountToStartDialog;
    private View viewEndGameWithBest;
    private View viewEndGameWithoutBest;
    private AlertDialog alertDialogHowToPlay;
    private AlertDialog alertDialogCountToStart;
    private AlertDialog alertDialogEndGameWithBest;
    private AlertDialog alertDialogEndGameWithoutBest;

    private AdView adView;
    private AdRequest adRequestBanner;

    private InterstitialAd interstitialAd;
    private AdRequest adRequestInterstitial;

    private Interface_Fragment fragment;
    private Presenter_Classic_Game presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_classic_game);

        adView = findViewById(R.id.adView_classic);
        adRequestBanner = new AdRequest.Builder().build();
        adView.loadAd(adRequestBanner);

        adRequestInterstitial = new AdRequest.Builder().build();

        mediaPlayerIntro = Activity_Initial.mediaPlayerIntro;
        mediaPlayerGame = Activity_Initial.mediaPlayerGame;
        mediaPlayerRight_0 = Activity_Initial.mediaPlayerRight_0;
        mediaPlayerRight_1 = Activity_Initial.mediaPlayerRight_1;
        mediaPlayerRight_2 = Activity_Initial.mediaPlayerRight_2;
        mediaPlayerWrong_0 = Activity_Initial.mediaPlayerWrong_0;
        mediaPlayerWrong_1 = Activity_Initial.mediaPlayerWrong_1;
        mediaPlayerWrong_2 = Activity_Initial.mediaPlayerWrong_2;

        buttonPlay = findViewById(R.id.button_classic_game_play);
        buttonPause = findViewById(R.id.button_classic_game_pause);
        buttonQuestion = findViewById(R.id.button_classic_game_question);
        buttonAudioEnabled = findViewById(R.id.button_classic_game_audio_on);
        buttonAudioDisabled = findViewById(R.id.button_classic_game_audio_off);

        textViewBest = findViewById(R.id.textView_classic_game_best);
        textViewTarget = findViewById(R.id.textView_classic_game_target);
        textViewScore = findViewById(R.id.textView_classic_game_score);
        textViewTime = findViewById(R.id.textView_classic_game_time);
        textViewPaused_1 = findViewById(R.id.textView_classic_game_paused_1);
        textViewPaused_2 = findViewById(R.id.textView_classic_game_paused_2);

        builderHowToPlay = new AlertDialog.Builder(this);
        viewHowToPlayDialog = this.getLayoutInflater().inflate(R.layout.dialog_how_to_play_classic, null);
        builderHowToPlay.setView(viewHowToPlayDialog);
        alertDialogHowToPlay = builderHowToPlay.create();
        alertDialogHowToPlay.setCancelable(false);

        builderCountToStart = new AlertDialog.Builder(this);
        viewCountToStartDialog = this.getLayoutInflater().inflate(R.layout.dialog_count_to_start, null);
        builderCountToStart.setView(viewCountToStartDialog);
        alertDialogCountToStart = builderCountToStart.create();
        alertDialogCountToStart.setCancelable(false);
        alertDialogCountToStart.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        builderEndGameWithBest = new AlertDialog.Builder(this);
        viewEndGameWithBest = this.getLayoutInflater().inflate(R.layout.dialog_end_game_with_best, null);
        builderEndGameWithBest.setView(viewEndGameWithBest);
        alertDialogEndGameWithBest = builderEndGameWithBest.create();
        alertDialogEndGameWithBest.setCancelable(false);

        builderEndGameWithoutBest = new AlertDialog.Builder(this);
        viewEndGameWithoutBest = this.getLayoutInflater().inflate(R.layout.dialog_end_game_without_best, null);
        builderEndGameWithoutBest.setView(viewEndGameWithoutBest);
        alertDialogEndGameWithoutBest = builderEndGameWithoutBest.create();
        alertDialogEndGameWithoutBest.setCancelable(false);

        presenter = new Presenter_Classic_Game(getApplicationContext(), this);

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
        if(interstitialAd == null) {
            interstitialAd = new InterstitialAd() {
                @NonNull
                @Override
                public String getAdUnitId() {
                    return null;
                }

                @Override
                public void show(@NonNull Activity activity) {
                }

                @Override
                public void setFullScreenContentCallback(@Nullable FullScreenContentCallback fullScreenContentCallback) {
                }

                @Nullable
                @Override
                public FullScreenContentCallback getFullScreenContentCallback() {
                    return null;
                }

                @Override
                public void setImmersiveMode(boolean b) {
                }

                @Nullable
                @Override
                public ResponseInfo getResponseInfo() {
                    return null;
                }

                @Override
                public void setOnPaidEventListener(@Nullable OnPaidEventListener onPaidEventListener) {
                }

                @Nullable
                @Override
                public OnPaidEventListener getOnPaidEventListener() {
                    return null;
                }
            };
            interstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequestInterstitial, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    Activity_Classic_Game.this.interstitialAd = interstitialAd;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    interstitialAd = null;
                }
            });
        }
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
    public void setFragment(int fragmentType) {
        if (fragmentType == 0) {
            fragment = new Fragment_4x4(this);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_classic_game, (Fragment) fragment, null)
                    .commit();
        }
        else if (fragmentType == 1) {
            fragment = new Fragment_5x5(this);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_classic_game, (Fragment) fragment, null)
                    .commit();
        }
        else{
            fragment = new Fragment_6x6(this);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_classic_game, (Fragment) fragment, null)
                    .commit();
        }
    }

    @Override
    public void setAudioEnabled() {
        buttonAudioEnabled.setVisibility(View.VISIBLE);
        buttonAudioDisabled.setVisibility(View.INVISIBLE);
        if(!mediaPlayerGame.isPlaying()){
            mediaPlayerGame.start();
        }
    }

    @Override
    public void setAudioDisabled() {
        buttonAudioEnabled.setVisibility(View.INVISIBLE);
        buttonAudioDisabled.setVisibility(View.VISIBLE);
        if(mediaPlayerGame.isPlaying()){
            mediaPlayerGame.pause();
        }
    }

    @Override
    public void mute() {
        if(mediaPlayerGame.isPlaying()){
            mediaPlayerGame.pause();
        }
    }

    @Override
    public void setBest(String best) {
        textViewBest.setText(best);
    }

    @Override
    public void setTarget(String target) {
        textViewTarget.setText(target);
    }

    @Override
    public void setScoreColorDefault() {
        textViewScore.setTextColor(-1979711488);   // getcurrenttextcolor yapıp bu sayıyı buldum.
    }

    @Override
    public void setScoreColorGreen() {
        if(textViewScore.getCurrentTextColor() != getResources().getColor(R.color.green)){
            textViewScore.setTextColor(getResources().getColor(R.color.green));
        }
    }

    @Override
    public void setScore(String score) {
        textViewScore.setText(score);
    }

    @Override
    public void setTime(String time) {
        textViewTime.setText(time);
    }

    @Override
    public void setPause() {
        buttonPlay.setVisibility(View.INVISIBLE);
        buttonPause.setVisibility(View.VISIBLE);
        textViewPaused_1.setVisibility(View.INVISIBLE);
        textViewPaused_2.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setPlay() {
        buttonPlay.setVisibility(View.VISIBLE);
        buttonPause.setVisibility(View.INVISIBLE);
        textViewPaused_1.setVisibility(View.VISIBLE);
        textViewPaused_2.setVisibility(View.VISIBLE);
    }

    @Override
    public void openHowToPlay() {
        Button buttonOk = viewHowToPlayDialog.findViewById(R.id.button_how_to_play_classic_ok);
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
    public void setButtonColor(int buttonIndex, int colorType) {
        fragment.setButtonColor(buttonIndex,colorType);
    }

    @Override
    public void setButtonsVisible() {
        fragment.setButtonsVisible();
    }

    @Override
    public void setButtonsInvisible() {
        fragment.setButtonsInvisible();
    }

    @Override
    public void playRight(int index) {
        switch (index) {
            case 0:
                mediaPlayerRight_0.start();
                break;
            case 1:
                mediaPlayerRight_1.start();
                break;
            case 2:
                mediaPlayerRight_2.start();
                break;
        }
    }

    @Override
    public void playWrong(int index) {
        switch (index) {
            case 0:
                mediaPlayerWrong_0.start();
                break;
            case 1:
                mediaPlayerWrong_1.start();
                break;
            case 2:
                mediaPlayerWrong_2.start();
                break;
        }
    }

    @Override
    public void openEndGame(boolean isBest, int score) {
        if(isBest){
            TextView textViewScoreValue = viewEndGameWithBest.findViewById(R.id.textView_end_game_with_best_score_value);
            textViewScoreValue.setText(Integer.toString(score));
            Button buttonOk = viewEndGameWithBest.findViewById(R.id.button_end_game_with_best_ok);
            buttonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.onEndGameDismissRequested();
                }
            });
            alertDialogEndGameWithBest.show();
        }
        else{
            TextView textViewScoreValue = viewEndGameWithoutBest.findViewById(R.id.textView_end_game_without_best_score_value);
            textViewScoreValue.setText(Integer.toString(score));
            Button buttonOk = viewEndGameWithoutBest.findViewById(R.id.button_end_game_without_best_ok);
            buttonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.onEndGameDismissRequested();
                }
            });
            alertDialogEndGameWithoutBest.show();
        }
    }

    @Override
    public void dismissEndGame(boolean isBest) {
        if(isBest){
            alertDialogEndGameWithBest.dismiss();
        }
        else{
            alertDialogEndGameWithoutBest.dismiss();
        }
    }

    @Override
    public void openClassicMenu() {
        Intent i = new Intent(getApplicationContext(), Activity_Classic_Menu.class);
        startActivity(i);
    }

    @Override
    public void onButtonClicked(int buttonIndex) {
        presenter.onButtonClicked(buttonIndex);
    }

    @Override
    public void showInterstitialAd() {
        if (interstitialAd != null) {
            interstitialAd.show(Activity_Classic_Game.this);
        }
    }
}