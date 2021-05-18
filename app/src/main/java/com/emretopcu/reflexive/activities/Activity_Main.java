package com.emretopcu.reflexive.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.emretopcu.reflexive.R;
import com.emretopcu.reflexive.interfaces.Interface_Main;
import com.emretopcu.reflexive.presenters.Presenter_Main;

public class Activity_Main extends AppCompatActivity implements Interface_Main {

    private MediaPlayer mediaPlayerIntro;
    private MediaPlayer mediaPlayerGame;
    private Button buttonAudioEnabled;
    private Button buttonAudioDisabled;
    private Button buttonEditUsername;
    private TextView textViewUsername;
    private TextView textViewClassicBest;
    private TextView textViewArcadeBest;
    private TextView textViewSurvivalBest;
    private Button buttonClassic;
    private Button buttonArcade;
    private Button buttonSurvival;
    private Button buttonLeaderboard;
    private AlertDialog.Builder builder;
    private View viewEditUsernameDialog;
    private AlertDialog alertDialog;

    private Presenter_Main presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        mediaPlayerIntro = Activity_Initial.mediaPlayerIntro;
        mediaPlayerGame = Activity_Initial.mediaPlayerGame;

        buttonAudioEnabled = findViewById(R.id.button_main_audio_on);
        buttonAudioDisabled = findViewById(R.id.button_main_audio_off);
        buttonEditUsername = findViewById(R.id.button_main_edit);

        textViewUsername = findViewById(R.id.textView_main_username);
        textViewClassicBest = findViewById(R.id.textView_main_classicRecordValue);
        textViewArcadeBest = findViewById(R.id.textView_main_arcadeRecordValue);
        textViewSurvivalBest = findViewById(R.id.textView_main_survivalRecordValue);

        buttonClassic = findViewById(R.id.button_main_classic);
        buttonArcade = findViewById(R.id.button_main_arcade);
        buttonSurvival = findViewById(R.id.button_main_survival);
        buttonLeaderboard = findViewById(R.id.button_main_leaderboard);

        builder = new AlertDialog.Builder(this);
        viewEditUsernameDialog = this.getLayoutInflater().inflate(R.layout.dialog_edit_username, null);
        builder.setView(viewEditUsernameDialog);
        alertDialog = builder.create();
        alertDialog.setCancelable(false);

        presenter = new Presenter_Main(getApplicationContext(), this);

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

        buttonEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onUsernameEditStarted();
            }
        });

        buttonClassic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onClassicClicked();
            }
        });

        buttonArcade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onArcadeClicked();
            }
        });

        buttonSurvival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSurvivalClicked();
            }
        });

        buttonLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onLeaderboardClicked();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onActivityResumed();
    }

    @Override
    public void openUsernameDialog(String username){
        EditText editTextNewUsername = viewEditUsernameDialog.findViewById(R.id.edit_text_username_dialog_username);
        editTextNewUsername.setText(username);
        editTextNewUsername.setSelection(editTextNewUsername.getText().length());
        TextView textViewWarning = viewEditUsernameDialog.findViewById(R.id.textView_username_dialog_warning);
        textViewWarning.setText(null);
        textViewWarning.setVisibility(View.INVISIBLE);
        Button buttonOk = viewEditUsernameDialog.findViewById(R.id.button_username_dialog_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = editTextNewUsername.getText().toString().toUpperCase();
                if(newUsername.length() == 0){
                    textViewWarning.setText(R.string.username_dialog_warning_2);
                    textViewWarning.setVisibility(View.VISIBLE);
                }
                else if(newUsername.equals(username)){
                    alertDialog.dismiss();
                }
                else{
                    textViewWarning.setText(null);
                    textViewWarning.setVisibility(View.INVISIBLE);
                    presenter.onUsernameEditRequested(newUsername);
                }
            }
        });
        Button buttonCancel = viewEditUsernameDialog.findViewById(R.id.button_username_dialog_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void dismissUsernameDialog() {
        alertDialog.dismiss();
    }

    @Override
    public void showWarningOnUsernameDialog() {
        TextView textViewWarning = viewEditUsernameDialog.findViewById(R.id.textView_username_dialog_warning);
        textViewWarning.setText(R.string.username_dialog_warning_1);
        textViewWarning.setVisibility(View.VISIBLE);
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
    public void setUsername(String username) {
        textViewUsername.setText(username);
    }

    @Override
    public void setClassicBest(String classicBest) {
        textViewClassicBest.setText(classicBest);
    }

    @Override
    public void setArcadeBest(String arcadeBest) {
        textViewArcadeBest.setText(arcadeBest);
    }

    @Override
    public void setSurvivalBest(String survivalBest) {
        textViewSurvivalBest.setText(survivalBest);
    }

    @Override
    public void openClassic() {
        Intent i = new Intent(getApplicationContext(), Activity_Classic_Menu.class);
        startActivity(i);
    }

    @Override
    public void openArcade() {
        Intent i = new Intent(getApplicationContext(), Activity_Arcade_Game.class);
        startActivity(i);
    }

    @Override
    public void openSurvival() {
        Intent i = new Intent(getApplicationContext(), Activity_Survival_Game.class);
        startActivity(i);
    }

    @Override
    public void openLeaderboard() {
        Intent i = new Intent(getApplicationContext(), Activity_Leaderboard.class);
        startActivity(i);
    }

    @Override
    public void showDatabaseErrorToast() {
        Toast.makeText(this, getResources().getString(R.string.main_toast_database_error), Toast.LENGTH_SHORT).show();
    }
}