package com.emretopcu.reflexive.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.emretopcu.reflexive.R;
import com.emretopcu.reflexive.interfaces.Interface_First_Entrance;
import com.emretopcu.reflexive.models.Database_Manager;
import com.emretopcu.reflexive.presenters.Presenter_First_Entrance;

public class Activity_First_Entrance extends AppCompatActivity implements Interface_First_Entrance {

    private AlertDialog.Builder builder;
    private View viewEditUsernameDialog;
    private AlertDialog alertDialog;

    private Presenter_First_Entrance presenter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_first_entrance);

        builder = new AlertDialog.Builder(this);
        viewEditUsernameDialog = this.getLayoutInflater().inflate(R.layout.dialog_enter_username, null);
        builder.setView(viewEditUsernameDialog);
        alertDialog = builder.create();

        presenter = new Presenter_First_Entrance(getApplicationContext(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onActivityResumed();
    }

    @Override
    public void openUsernameDialog() {
        EditText editTextNewUsername = viewEditUsernameDialog.findViewById(R.id.edit_text_username_dialog_username);
        editTextNewUsername.setSelection(editTextNewUsername.getText().length());
        TextView textViewWarning = viewEditUsernameDialog.findViewById(R.id.textView_username_dialog_warning);
        Button buttonOk = viewEditUsernameDialog.findViewById(R.id.button_username_dialog_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = editTextNewUsername.getText().toString().toUpperCase();
                if(newUsername.length() == 0){
                    textViewWarning.setText(R.string.username_dialog_warning_2);
                    textViewWarning.setVisibility(View.VISIBLE);
                }
                else{
                    presenter.onUsernameAddRequested(newUsername);
                }
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    public void dismissUsernameDialog() {
        alertDialog.dismiss();
        Database_Manager.getInstance().getLeaderboardInfo();
        Intent i = new Intent(getApplicationContext(), Activity_Main.class);
        startActivity(i);
    }

    @Override
    public void showWarningOnUsernameDialog() {
        TextView textViewWarning = viewEditUsernameDialog.findViewById(R.id.textView_username_dialog_warning);
        textViewWarning.setText(R.string.username_dialog_warning_1);
        textViewWarning.setVisibility(View.VISIBLE);
    }
}