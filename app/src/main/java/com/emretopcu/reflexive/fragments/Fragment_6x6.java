package com.emretopcu.reflexive.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.emretopcu.reflexive.R;
import com.emretopcu.reflexive.interfaces.Interface_Fragment;
import com.emretopcu.reflexive.interfaces.Interface_General_Game_Activity;

public class Fragment_6x6 extends Fragment implements Interface_Fragment {

    private Button[] buttons;
    private Interface_General_Game_Activity activity;

    public Fragment_6x6(Interface_General_Game_Activity activity) {
        super(R.layout.fragment_6x6);
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_6x6, container, false);
        buttons = new Button[36];
        for(int i=0; i<buttons.length; i++){
            String buttonID = "button_fragment_6x6_" + i;
            int resID = getResources().getIdentifier(buttonID, "id", getActivity().getPackageName());
            buttons[i] = ((Button) view.findViewById(resID));
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(int i=0; i<buttons.length; i++){
                        String buttonID = "button_fragment_4x4_" + i;
                        int resID = getResources().getIdentifier(buttonID, "id", getActivity().getPackageName());
                        if(v.getId() == resID){
                            activity.onButtonClicked(i);
                            break;
                        }
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void setButtonColor(int buttonIndex, int colorType) {
        switch (colorType){
            case 0:
                buttons[buttonIndex].setBackground(getActivity().getDrawable(R.drawable.background_button_game_6x6_default));
                buttons[buttonIndex].setEnabled(false);
                break;
            case 1:
                buttons[buttonIndex].setBackground(getActivity().getDrawable(R.drawable.background_button_game_6x6_green));
                buttons[buttonIndex].setEnabled(true);
                break;
            case 2:
                buttons[buttonIndex].setBackground(getActivity().getDrawable(R.drawable.background_button_game_6x6_yellow));
                buttons[buttonIndex].setEnabled(true);
                break;
            case 3:
                buttons[buttonIndex].setBackground(getActivity().getDrawable(R.drawable.background_button_game_6x6_red));
                buttons[buttonIndex].setEnabled(true);
                break;
        }
    }

    @Override
    public void setButtonEnabled(int buttonIndex) {
        buttons[buttonIndex].setEnabled(true);
    }

    @Override
    public void setAllButtonsDisabled() {
        for(int i=0; i<buttons.length; i++){
            buttons[i].setEnabled(false);
        }
    }
}