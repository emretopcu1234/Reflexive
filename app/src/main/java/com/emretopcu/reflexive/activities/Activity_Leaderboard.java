package com.emretopcu.reflexive.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.emretopcu.reflexive.R;
import com.emretopcu.reflexive.interfaces.Interface_Leaderboard;
import com.emretopcu.reflexive.presenters.Presenter_Leaderboard;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;

public class Activity_Leaderboard extends AppCompatActivity implements Interface_Leaderboard {

    private RecyclerViewAdapter_Leaderboard adapter;
    private RecyclerView recyclerView_leaderboard;
    private LinearLayoutManager layoutManager;

    private Button buttonClassic;
    private Button buttonArcade;
    private Button buttonSurvival;

    private Presenter_Leaderboard presenter;

    private AdView adView;
    private AdRequest adRequestBanner;
    private InterstitialAd interstitialAd;
    private AdRequest adRequestInterstitial;
    private boolean isInterstitialAdLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_leaderboard);

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





//        Button button = findViewById(R.id.button_leaderboard_classic);      // DENEME MAKSATLI
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showInterstitialAd(v);
//            }
//        });
//
//
//        adView = findViewById(R.id.adView_leaderboard);
//        adRequestBanner = new AdRequest.Builder().build();
//        adView.loadAd(adRequestBanner);
//
//
//        adRequestInterstitial = new AdRequest.Builder().build();
//        interstitialAd = new InterstitialAd() {
//            @NonNull
//            @Override
//            public String getAdUnitId() {
//                return null;
//            }
//
//            @Override
//            public void show(@NonNull Activity activity) {
//
//            }
//
//            @Override
//            public void setFullScreenContentCallback(@Nullable FullScreenContentCallback fullScreenContentCallback) {
//
//            }
//
//            @Nullable
//            @Override
//            public FullScreenContentCallback getFullScreenContentCallback() {
//                return null;
//            }
//
//            @Override
//            public void setImmersiveMode(boolean b) {
//
//            }
//
//            @Nullable
//            @Override
//            public ResponseInfo getResponseInfo() {
//                return null;
//            }
//
//            @Override
//            public void setOnPaidEventListener(@Nullable OnPaidEventListener onPaidEventListener) {
//
//            }
//
//            @Nullable
//            @Override
//            public OnPaidEventListener getOnPaidEventListener() {
//                return null;
//            }
//        };
//        isInterstitialAdLoaded = false;
//        interstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequestInterstitial, new InterstitialAdLoadCallback(){
//            @Override
//            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                Log.d("ADDDD","interstitial loaded");
//                Activity_Leaderboard.this.interstitialAd = interstitialAd;
//                isInterstitialAdLoaded = true;
//            }
//
//            @Override
//            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                Log.d("ADDDD","interstitial failed");
//                interstitialAd = null;
//            }
//        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onActivityResumed();
    }

    public void showInterstitialAd(View v){
        if (interstitialAd != null) {
            interstitialAd.show(Activity_Leaderboard.this);
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
}