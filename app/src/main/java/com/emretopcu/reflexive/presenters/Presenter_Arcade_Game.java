package com.emretopcu.reflexive.presenters;

import android.content.Context;
import android.os.CountDownTimer;

import com.emretopcu.reflexive.R;
import com.emretopcu.reflexive.interfaces.Interface_Arcade_Game;
import com.emretopcu.reflexive.models.Common_Parameters_Variables;
import com.emretopcu.reflexive.models.Database_Manager;
import com.emretopcu.reflexive.models.User_Preferences;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Presenter_Arcade_Game {

    private Context context;
    private Interface_Arcade_Game view;
    private final String baseBest;
    private final String baseTarget;
    private final String baseScore;

    private boolean isAudioEnabled;
    private int mediaIndexRight;
    private int mediaIndexWrong;

    private final Object lock = new Object();
    private boolean isPlayActive;
    private boolean isPaused;
    private boolean isFinished;
    private AtomicInteger remainingTime;
    private AtomicInteger remainingMillis;

    private AtomicInteger[][] buttonIndicators;
    private int gameSize;
    private AtomicInteger buttonFireSequence;

    private CountDownTimer uiWorkerLastSecondsField;
    private CountDownTimer uiWorkerAllFields;
    private CountDownTimer uiWorkerButton;
    private ExecutorService serviceGameLogic;
    private ExecutorService serviceTimeField;

    private AtomicBoolean isLastPressedGreen;
    private AtomicBoolean isAnyPressed;
    private int score;
    private boolean isBest;
    private int gameDifficultyIndex;
    private AtomicInteger currentFragment;

    private boolean isLastLevelStarted;
    private boolean isLastSecondsVisible;

    private boolean isExitedOnPurpose = true;    // geri tusuna basılırsa true, diger durumlarda false

    public Presenter_Arcade_Game(Context context, Interface_Arcade_Game view) {
        this.context = context;
        this.view = view;
        baseBest = context.getString(R.string.arcade_game_best);
        baseTarget = context.getString(R.string.arcade_game_target);
        baseScore = context.getString(R.string.arcade_game_score);
    }

    public void onActivityResumed(){
        if(User_Preferences.getInstance().isAudioEnabled()){
            onAudioEnabled();
        }
        else{
            onAudioDisabled();
        }
        if(isExitedOnPurpose){
            isExitedOnPurpose = false;
            isPlayActive = true;
            isPaused = false;
            gameDifficultyIndex = 0;
            view.setFragment(Common_Parameters_Variables.ARCADE_FRAGMENT_TYPE[gameDifficultyIndex]);    // oyunda ilerlendikçe index artacak.
            currentFragment = new AtomicInteger();
            currentFragment.set(Common_Parameters_Variables.ARCADE_FRAGMENT_TYPE[gameDifficultyIndex]);
            isLastLevelStarted = false;
            isLastSecondsVisible = false;
            view.setPause();
            view.setLastSecondsVisible(false);
            view.setBest(baseBest + User_Preferences.getInstance().getArcadeBest());
            view.setTarget(baseTarget + Common_Parameters_Variables.ARCADE_TARGET[gameDifficultyIndex]);
            view.setScoreColorDefault();
            view.setScore(baseScore + "0");
            view.setTime(Integer.toString(Common_Parameters_Variables.ARCADE_TIME[gameDifficultyIndex]));
            if(User_Preferences.getInstance().isArcadeFirstEntrance()){
                view.openHowToPlay();
            }
            else{
                countToStart();
            }
        }
        else{
            onPauseClicked();
        }
    }

    public void onActivityPaused(){
        isPaused = true;
        view.mute();
    }

    public void onPlayClicked(){
        isPlayActive = true;
        isPaused = false;
        view.setPause();
        view.setButtonsVisible();
        synchronized (lock){
            lock.notifyAll();
        }
    }

    public void onPauseClicked(){
        isPlayActive = false;
        isPaused = true;
        view.setPlay();
        view.setButtonsInvisible();
        view.setLastSecondsVisible(false);
    }

    public void onQuestionClicked(){
        isPaused = true;
        view.setPlay();
        view.openHowToPlay();
    }

    public void onQuestionDismissRequested(){
        isPaused = !isPlayActive;
        view.dismissHowToPlay();
        if(User_Preferences.getInstance().isArcadeFirstEntrance()){
            User_Preferences.getInstance().setArcadeFirstEntrance(false);
            countToStart();
        }
        if(isPlayActive){
            view.setPause();
            synchronized (lock){
                lock.notifyAll();
            }
        }
        else{
            view.setPlay();
        }
    }

    public void onAudioEnabled() {
        User_Preferences.getInstance().setAudioEnabled(true);
        view.setAudioEnabled();
        isAudioEnabled = true;
    }

    public void onAudioDisabled() {
        User_Preferences.getInstance().setAudioEnabled(false);
        view.setAudioDisabled();
        isAudioEnabled = false;
    }

    private void countToStart(){
        view.openCountToStart();
        new CountDownTimer(3000, 100){
            int remainingSecond = 3;
            int count = 0;
            @Override
            public void onTick(long millisUntilFinished) {
                if(count%10 == 0){
                    view.editCountToStart(remainingSecond);
                    remainingSecond--;
                }
                count++;
            }

            @Override
            public void onFinish() {
                view.dismissCountToStart();
                startGame();
            }
        }.start();
    }

    private void startGame(){
        switch (Common_Parameters_Variables.ARCADE_FRAGMENT_TYPE[gameDifficultyIndex]){
            case 0:
                gameSize = 16;
                break;
            case 1:
                gameSize = 25;
                break;
            case 2:
                gameSize = 36;
                break;
            default:
                gameSize = 0;
        }
        buttonIndicators = new AtomicInteger[gameSize][2];
        for(int i=0; i<gameSize; i++){
            buttonIndicators[i][0] = new AtomicInteger(0);     // 0 default, 1 green, 2 yellow, 3 red
            buttonIndicators[i][1] = new AtomicInteger(-1);     // buttonfiresequence değerleri
        }
        buttonFireSequence = new AtomicInteger();

        isFinished = false;
        isLastPressedGreen = new AtomicBoolean();
        isAnyPressed = new AtomicBoolean();
        isAnyPressed.set(false);
        score = 0;
        mediaIndexRight = 0;
        mediaIndexWrong = 0;

        remainingTime = new AtomicInteger();
        remainingMillis = new AtomicInteger();
        remainingTime.set(Common_Parameters_Variables.ARCADE_TIME[gameDifficultyIndex] * 1000);   // ms
        remainingMillis.set(Common_Parameters_Variables.ARCADE_TIME[gameDifficultyIndex] * 1000); // ms

        uiWorkerLastSecondsField = new CountDownTimer(Common_Parameters_Variables.COUNT_DOWN_LENGTH,500){
            @Override
            public void onTick(long millisUntilFinished) {
                if(!isPaused){
                    isLastSecondsVisible = !isLastSecondsVisible;
                    view.setLastSecondsVisible(isLastSecondsVisible);
                }
            }

            @Override
            public void onFinish() {
                if(!isPaused){
                    isLastSecondsVisible = !isLastSecondsVisible;
                    view.setLastSecondsVisible(isLastSecondsVisible);
                }
                this.start();
            }
        };

        // normal timer kullanamıyoruz, çünkü farklı bir thread'e geçiyor,
        // farklı thread'den de ui componentlarına erişemiyoruz.
        // ama countdowntimer main thread'den devam ediyor.
        // o yüzden countdowntimer kullanılıyor, süre dolunca yeniden başlıyor.
        uiWorkerAllFields = new CountDownTimer(Common_Parameters_Variables.COUNT_DOWN_LENGTH, 500) {
            // millisInFuture kısmının pek bir önemi yok.
            // o süre dolduğunda oyun devam ediyorsa timer otomatik olarak yeniden başlayacak.
            // ama her yeniden başlamada 1 sn falan atlama yaptığı için mümkün olduğunca yüksek yapmakta fayda var.
            // intervalı da 1000 yerine 500 yapma sebebi, bazen sayım yaparken 22'den 20'ye vb düşebiliyor,
            // (süre olarak 2 sn geçse bile timefield'a bazen yansımayabiliyor).
            // o yüzden timefield da yakalasın diye double check amaçlı 500 ms yapıldı.
            @Override
            public void onTick(long millisUntilFinished) {
                if(!isPaused){
                    view.setTime(Integer.toString(remainingMillis.get()/1000));
                    if(currentFragment.get() != Common_Parameters_Variables.ARCADE_FRAGMENT_TYPE[gameDifficultyIndex]){
                        currentFragment.set(Common_Parameters_Variables.ARCADE_FRAGMENT_TYPE[gameDifficultyIndex]);
                        switch (currentFragment.get()){
                            case 0:
                                gameSize = 16;
                                break;
                            case 1:
                                gameSize = 25;
                                break;
                            case 2:
                                gameSize = 36;
                                break;
                            default:
                                gameSize = 0;
                        }
                        buttonIndicators = new AtomicInteger[gameSize][2];
                        for(int i=0; i<gameSize; i++){
                            buttonIndicators[i][0] = new AtomicInteger(0);
                            buttonIndicators[i][1] = new AtomicInteger(-1);
                        }
                        view.setFragment(currentFragment.get());
                    }
                    if(gameDifficultyIndex != Common_Parameters_Variables.NUMBER_OF_ARCADE_LEVELS-1){
                        view.setTarget(baseTarget + Common_Parameters_Variables.ARCADE_TARGET[gameDifficultyIndex]);
                        if(score >= Common_Parameters_Variables.ARCADE_TARGET[gameDifficultyIndex]){
                            view.setScoreColorGreen();
                        }
                        else{
                            view.setScoreColorDefault();
                        }
                    }
                    else{
                        view.setTarget(baseTarget + "-");
                        view.setScoreColorDefault();
                        if(!isLastLevelStarted){
                            uiWorkerLastSecondsField.start();
                            isLastLevelStarted = true;
                        }
                    }
                }
            }

            @Override
            public void onFinish() {
                // if(isPlayActive) ekleyemeyiz, oyun pause edildiğinde buraya gelmiş olabilir.
                // zaten oyun bittiğinde her türlü burasi stop edilecek.
                if(!isPaused) {
                    view.setTime(Integer.toString(remainingMillis.get()/1000));
                    if(currentFragment.get() != Common_Parameters_Variables.ARCADE_FRAGMENT_TYPE[gameDifficultyIndex]){
                        currentFragment.set(Common_Parameters_Variables.ARCADE_FRAGMENT_TYPE[gameDifficultyIndex]);
                        switch (currentFragment.get()){
                            case 0:
                                gameSize = 16;
                                break;
                            case 1:
                                gameSize = 25;
                                break;
                            case 2:
                                gameSize = 36;
                                break;
                            default:
                                gameSize = 0;
                        }
                        buttonIndicators = new AtomicInteger[gameSize][2];
                        for(int i=0; i<gameSize; i++){
                            buttonIndicators[i][0] = new AtomicInteger(0);
                            buttonIndicators[i][1] = new AtomicInteger(-1);
                        }
                        view.setFragment(currentFragment.get());
                    }
                    if(gameDifficultyIndex != Common_Parameters_Variables.NUMBER_OF_ARCADE_LEVELS-1){
                        view.setTarget(baseTarget + Common_Parameters_Variables.ARCADE_TARGET[gameDifficultyIndex]);
                        if(score >= Common_Parameters_Variables.ARCADE_TARGET[gameDifficultyIndex]){
                            view.setScoreColorGreen();
                        }
                        else{
                            view.setScoreColorDefault();
                        }
                    }
                    else{
                        view.setTarget(baseTarget + "-");
                        view.setScoreColorDefault();
                        if(!isLastLevelStarted){
                            uiWorkerLastSecondsField.start();
                            isLastLevelStarted = true;
                        }
                    }
                }
                this.start();
            }
        }.start();

        uiWorkerButton = new CountDownTimer(Common_Parameters_Variables.COUNT_DOWN_LENGTH, Common_Parameters_Variables.SENSITIVITY_UI) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(!isFinished){
                    for(int i=0; i<gameSize; i++){
                        if(buttonIndicators[i][0].get() != 0){
                            view.setButtonColor(i,buttonIndicators[i][0].get());
                        }
                        if(buttonIndicators[i][1].get() == ((buttonFireSequence.get()) % (Common_Parameters_Variables.ARCADE_NUMBER_OF_FIRING_BUTTONS[gameDifficultyIndex]+1))){
                            if(buttonIndicators[i][0].get() != 0){  // kullanıcı tıkladıysa 0'lanmış olabilir.
                                buttonIndicators[i][0].set(0);
                                view.setButtonColor(i,0);
                            }
                        }
                    }
                }
                else{
                    if(score > Integer.parseInt(User_Preferences.getInstance().getArcadeBest())){
                        isBest = true;
                        User_Preferences.getInstance().setArcadeBest(Integer.toString(score));
                        Database_Manager.getInstance().updateUserScoreArcade(score);
                    }
                    else{
                        isBest = false;
                    }
                    view.openEndGame(isBest, score);
                    uiWorkerAllFields.cancel();
                    uiWorkerLastSecondsField.cancel();
                    this.cancel();
                }
            }

            @Override
            public void onFinish() {
                this.start();
            }
        }.start();

        serviceGameLogic = Executors.newSingleThreadExecutor();
        serviceGameLogic.execute(new Runnable() {
            @Override
            public void run() {
                buttonFireSequence.set(0);
                int candidateCell;
                int randomColorIndicator;   // 0-3 green, 4-5 yellow, 6-9 red
                Random random = new Random();

                boolean isYellowAllowed = false;
                while(remainingTime.get() != -1){
                    if(!isPaused){
                        candidateCell = random.nextInt(gameSize);
                        while(buttonIndicators[candidateCell][0].get() != 0){
                            candidateCell = random.nextInt(gameSize);
                        }
                        randomColorIndicator = random.nextInt(Common_Parameters_Variables.ARCADE_TOTAL_LIMIT[gameDifficultyIndex]);
                        while(!isYellowAllowed){
                            if(isAnyPressed.get()){
                                isYellowAllowed = true;
                            }
                            if(!(randomColorIndicator >= Common_Parameters_Variables.ARCADE_GREEN_LIMIT[gameDifficultyIndex]
                                    && randomColorIndicator < Common_Parameters_Variables.ARCADE_YELLOW_LIMIT[gameDifficultyIndex])){
                                break;
                            }
                            randomColorIndicator = random.nextInt(Common_Parameters_Variables.ARCADE_TOTAL_LIMIT[gameDifficultyIndex]);
                        }

                        if(randomColorIndicator < Common_Parameters_Variables.ARCADE_GREEN_LIMIT[gameDifficultyIndex]){
                            buttonIndicators[candidateCell][0].set(1);
                        }
                        else if(randomColorIndicator < Common_Parameters_Variables.ARCADE_YELLOW_LIMIT[gameDifficultyIndex]){
                            buttonIndicators[candidateCell][0].set(2);
                        }
                        else{
                            buttonIndicators[candidateCell][0].set(3);
                        }
                        if(buttonFireSequence.get() > Common_Parameters_Variables.ARCADE_NUMBER_OF_FIRING_BUTTONS[gameDifficultyIndex]){
                            buttonFireSequence.set(0);
                        }
                        buttonIndicators[candidateCell][1].set(buttonFireSequence.get());
                        buttonFireSequence.set(buttonFireSequence.get() + 1);

                        try {
                            Thread.sleep(Common_Parameters_Variables.ARCADE_FIRE_INTERVAL[gameDifficultyIndex]);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        remainingTime.set(remainingTime.get() - Common_Parameters_Variables.ARCADE_FIRE_INTERVAL[gameDifficultyIndex]);
                    }
                    else{
                        synchronized (lock){
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        serviceTimeField = Executors.newSingleThreadExecutor();
        serviceTimeField.execute(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i< Common_Parameters_Variables.NUMBER_OF_ARCADE_LEVELS; i++){
                    if(isFinished){
                        break;
                    }
                    while(remainingMillis.get() >= 0){
                        if(!isPaused) {
                            try {
                                Thread.sleep(Common_Parameters_Variables.SENSITIVITY_TIME);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            remainingMillis.set(remainingMillis.get() - Common_Parameters_Variables.SENSITIVITY_TIME);
                        }
                        else{
                            synchronized (lock){
                                try {
                                    lock.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    if(gameDifficultyIndex == Common_Parameters_Variables.NUMBER_OF_ARCADE_LEVELS-1){
                        isFinished = true;
                        remainingTime.set(-1);
                        remainingMillis.set(-1);
                    }
                    else{
                        if(score < Common_Parameters_Variables.ARCADE_TARGET[gameDifficultyIndex]){
                            isFinished = true;
                            remainingTime.set(-1);
                            remainingMillis.set(-1);
                        }
                        else{
                            gameDifficultyIndex++;
                            remainingTime.set(Common_Parameters_Variables.ARCADE_TIME[gameDifficultyIndex] * 1000);
                            remainingMillis.set(Common_Parameters_Variables.ARCADE_TIME[gameDifficultyIndex] * 1000);
                        }
                    }
                }
            }
        });
    }

    public void onBackPressed(){
        isPlayActive = true;
        isPaused = false;
        isExitedOnPurpose = true;
        uiWorkerAllFields.cancel();
        uiWorkerLastSecondsField.cancel();
        uiWorkerButton.cancel();
        serviceGameLogic.shutdownNow();
        serviceTimeField.shutdownNow();
        view.openMain();
    }

    public void onButtonClicked(int buttonIndex){
        if(buttonIndicators[buttonIndex][0].get() == 1){
            buttonIndicators[buttonIndex][0].set(0);
            if(!isAnyPressed.get()){
                isAnyPressed.set(true);
            }
            score += gameDifficultyIndex+1;
            isLastPressedGreen.set(true);
            view.setScore(baseScore + score);
            if(score >= Common_Parameters_Variables.ARCADE_TARGET[gameDifficultyIndex] && !isLastLevelStarted){
                view.setScoreColorGreen();
            }
            if(isAudioEnabled){
                view.playRight(mediaIndexRight);
                mediaIndexRight++;
                if(mediaIndexRight == 3){
                    mediaIndexRight = 0;
                }
            }
        }
        else if(buttonIndicators[buttonIndex][0].get() == 2){
            buttonIndicators[buttonIndex][0].set(0);
            if(isLastPressedGreen.get()){
                score += gameDifficultyIndex+1;
                view.setScore(baseScore + score);
                if(score >= Common_Parameters_Variables.ARCADE_TARGET[gameDifficultyIndex] && !isLastLevelStarted){
                    view.setScoreColorGreen();
                }
                if(isAudioEnabled){
                    view.playRight(mediaIndexRight);
                    mediaIndexRight++;
                    if(mediaIndexRight == 3){
                        mediaIndexRight = 0;
                    }
                }
            }
            else{
                remainingMillis.set(remainingMillis.get() - 1000);
                remainingTime.set(remainingTime.get() - 1000);
                isLastPressedGreen.set(false);
                if(isAudioEnabled){
                    view.playWrong(mediaIndexWrong);
                    mediaIndexWrong++;
                    if(mediaIndexWrong == 3){
                        mediaIndexWrong = 0;
                    }
                }
            }
        }
        else if(buttonIndicators[buttonIndex][0].get() == 3){
            buttonIndicators[buttonIndex][0].set(0);
            if(!isAnyPressed.get()){
                isAnyPressed.set(true);
            }
            remainingMillis.set(remainingMillis.get() - 1000);
            remainingTime.set(remainingTime.get() - 1000);
            isLastPressedGreen.set(false);
            if(isAudioEnabled){
                view.playWrong(mediaIndexWrong);
                mediaIndexWrong++;
                if(mediaIndexWrong == 3){
                    mediaIndexWrong = 0;
                }
            }
        }
        view.setButtonColor(buttonIndex,0);
    }

    public void onEndGameDismissRequested(){
        view.dismissEndGame(isBest);
        view.openMain();
        if(System.currentTimeMillis() - Common_Parameters_Variables.LAST_AD_TIME >= Common_Parameters_Variables.AD_TIME_INTERVAL){
            Common_Parameters_Variables.LAST_AD_TIME = System.currentTimeMillis();
            view.showInterstitialAd();
        }
    }
}