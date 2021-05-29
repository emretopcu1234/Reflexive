package com.emretopcu.reflexive.presenters;

import android.content.Context;
import android.os.CountDownTimer;

import com.emretopcu.reflexive.R;
import com.emretopcu.reflexive.interfaces.Interface_Survival_Game;
import com.emretopcu.reflexive.models.Common_Parameters;
import com.emretopcu.reflexive.models.User_Preferences;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Presenter_Survival_Game {
    private Context context;
    private Interface_Survival_Game view;
    private final String baseBest;
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

    private CountDownTimer uiWorkerAllFields;
    private CountDownTimer uiWorkerButton;
    private ExecutorService serviceGameLogic;
    private ExecutorService serviceTimeField;

    private AtomicBoolean isLastPressedGreen;
    private AtomicBoolean isAnyPressed;
    private int score;
    private int remainingChances;
    private boolean isBest;
    private int gameDifficultyIndex;
    private AtomicInteger currentFragment;

    public Presenter_Survival_Game(Context context, Interface_Survival_Game view) {
        this.context = context;
        this.view = view;
        baseBest = context.getString(R.string.survival_game_best);
        baseScore = context.getString(R.string.survival_game_score);
    }

    public void onActivityResumed(){
        view.setFragment(Common_Parameters.SURVIVAL_FRAGMENT_TYPE[0]);    // oyunda ilerlendikçe index artacak.
        if(User_Preferences.getInstance().isAudioEnabled()){
            onAudioEnabled();
        }
        else{
            onAudioDisabled();
        }
        gameDifficultyIndex = 0;    // TODO oyun arkaplana alınıp yeniden gelirse vb bu duruma bir çözüm gerekebilir.
        currentFragment = new AtomicInteger();
        currentFragment.set(Common_Parameters.SURVIVAL_FRAGMENT_TYPE[gameDifficultyIndex]);
        isPlayActive = true;
        isPaused = false;
        view.setPause();
        view.setBest(baseBest + User_Preferences.getInstance().getSurvivalBest());
        view.setScore(baseScore + "0");
        view.setChances(Common_Parameters.NUMBER_OF_SURVIVAL_CHANCES);
        if(User_Preferences.getInstance().isSurvivalFirstEntrance()){
            view.openHowToPlay();
        }
        else{
            countToStart();
        }
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
    }

    public void onQuestionClicked(){
        isPaused = true;
        view.setPlay();
        view.openHowToPlay();
    }

    public void onQuestionDismissRequested(){
        isPaused = !isPlayActive;
        view.dismissHowToPlay();
        if(User_Preferences.getInstance().isSurvivalFirstEntrance()){
            User_Preferences.getInstance().setSurvivalFirstEntrance(false);
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
        switch (Common_Parameters.SURVIVAL_FRAGMENT_TYPE[gameDifficultyIndex]){
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
        isLastPressedGreen.set(false);
        isAnyPressed = new AtomicBoolean();
        isAnyPressed.set(false);
        score = 0;
        remainingChances = Common_Parameters.NUMBER_OF_SURVIVAL_CHANCES;
        mediaIndexRight = 0;
        mediaIndexWrong = 0;

        remainingTime = new AtomicInteger();
        remainingMillis = new AtomicInteger();
        remainingTime.set(Common_Parameters.SURVIVAL_TIME[gameDifficultyIndex] * 1000);   // ms
        remainingMillis.set(Common_Parameters.SURVIVAL_TIME[gameDifficultyIndex] * 1000); // ms

        // normal timer kullanamıyoruz, çünkü farklı bir thread'e geçiyor,
        // farklı thread'den de ui componentlarına erişemiyoruz.
        // ama countdowntimer main thread'den devam ediyor.
        // o yüzden countdowntimer kullanılıyor, süre dolunca yeniden başlıyor.
        uiWorkerAllFields = new CountDownTimer(Common_Parameters.COUNT_DOWN_LENGTH, 500) {
            // millisInFuture kısmının pek bir önemi yok.
            // o süre dolduğunda oyun devam ediyorsa timer otomatik olarak yeniden başlayacak.
            // ama her yeniden başlamada 1 sn falan atlama yaptığı için mümkün olduğunca yüksek yapmakta fayda var.
            // intervalı da 1000 yerine 500 yapma sebebi, bazen sayım yaparken 22'den 20'ye vb düşebiliyor,
            // (süre olarak 2 sn geçse bile timefield'a bazen yansımayabiliyor).
            // o yüzden timefield da yakalasın diye double check amaçlı 500 ms yapıldı.
            @Override
            public void onTick(long millisUntilFinished) {
                if(!isPaused){
                    if(currentFragment.get() != Common_Parameters.SURVIVAL_FRAGMENT_TYPE[gameDifficultyIndex]){
                        currentFragment.set(Common_Parameters.SURVIVAL_FRAGMENT_TYPE[gameDifficultyIndex]);
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
                }
            }

            @Override
            public void onFinish() {
                // if(isPlayActive) ekleyemeyiz, oyun pause edildiğinde buraya gelmiş olabilir.
                // zaten oyun bittiğinde her türlü burasi stop edilecek.
                if(!isPaused) {
                    if(currentFragment.get() != Common_Parameters.SURVIVAL_FRAGMENT_TYPE[gameDifficultyIndex]){
                        currentFragment.set(Common_Parameters.SURVIVAL_FRAGMENT_TYPE[gameDifficultyIndex]);
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
                }
                this.start();
            }
        }.start();

        uiWorkerButton = new CountDownTimer(Common_Parameters.COUNT_DOWN_LENGTH, Common_Parameters.SENSITIVITY_UI) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(!isFinished){
                    for(int i=0; i<gameSize; i++){
                        if(buttonIndicators[i][0].get() != 0){
                            view.setButtonColor(i,buttonIndicators[i][0].get());
                        }
                        if(buttonIndicators[i][1].get() == ((buttonFireSequence.get()) % (Common_Parameters.SURVIVAL_NUMBER_OF_FIRING_BUTTONS[gameDifficultyIndex]+1))){
                            if(buttonIndicators[i][0].get() != 0){  // kullanıcı tıkladıysa 0'lanmış olabilir.
                                if(buttonIndicators[i][0].get() == 1 || (buttonIndicators[i][0].get() == 2 && isLastPressedGreen.get())){
                                    remainingChances--;
                                    view.setChances(remainingChances);
                                    if(isAudioEnabled){
                                        view.playWrong(mediaIndexWrong);
                                        mediaIndexWrong++;
                                        if(mediaIndexWrong == 3){
                                            mediaIndexWrong = 0;
                                        }
                                    }
                                    if(remainingChances == 0){
                                        isFinished = true;
                                        remainingTime.set(-1);
                                        remainingMillis.set(-1);
                                    }
                                }
                                buttonIndicators[i][0].set(0);
                                view.setButtonColor(i,0);
                            }
                        }
                    }
                }
                else{
                    if(score > Integer.parseInt(User_Preferences.getInstance().getSurvivalBest())){
                        isBest = true;
                        User_Preferences.getInstance().setSurvivalBest(Integer.toString(score));
                    }
                    else{
                        isBest = false;
                    }
                    view.openEndGame(isBest, score);
                    uiWorkerAllFields.cancel();
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
                        randomColorIndicator = random.nextInt(Common_Parameters.SURVIVAL_TOTAL_LIMIT[gameDifficultyIndex]);
                        while(!isYellowAllowed){
                            if(isAnyPressed.get()){
                                isYellowAllowed = true;
                            }
                            if(!(randomColorIndicator >= Common_Parameters.SURVIVAL_GREEN_LIMIT[gameDifficultyIndex]
                                    && randomColorIndicator < Common_Parameters.SURVIVAL_YELLOW_LIMIT[gameDifficultyIndex])){
                                break;
                            }
                            randomColorIndicator = random.nextInt(Common_Parameters.SURVIVAL_TOTAL_LIMIT[gameDifficultyIndex]);
                        }

                        if(randomColorIndicator < Common_Parameters.SURVIVAL_GREEN_LIMIT[gameDifficultyIndex]){
                            buttonIndicators[candidateCell][0].set(1);
                        }
                        else if(randomColorIndicator < Common_Parameters.SURVIVAL_YELLOW_LIMIT[gameDifficultyIndex]){
                            buttonIndicators[candidateCell][0].set(2);
                        }
                        else{
                            buttonIndicators[candidateCell][0].set(3);
                        }
                        if(buttonFireSequence.get() > Common_Parameters.SURVIVAL_NUMBER_OF_FIRING_BUTTONS[gameDifficultyIndex]){
                            buttonFireSequence.set(0);
                        }
                        buttonIndicators[candidateCell][1].set(buttonFireSequence.get());
                        buttonFireSequence.set(buttonFireSequence.get() + 1);

                        try {
                            Thread.sleep(Common_Parameters.SURVIVAL_FIRE_INTERVAL[gameDifficultyIndex]);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        remainingTime.set(remainingTime.get() - Common_Parameters.SURVIVAL_FIRE_INTERVAL[gameDifficultyIndex]);
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
                for(int i=0; i<Common_Parameters.NUMBER_OF_SURVIVAL_LEVELS; i++){
                    if(isFinished){
                        break;
                    }
                    while(remainingMillis.get() >= 0){
                        if(!isPaused) {
                            try {
                                Thread.sleep(Common_Parameters.SENSITIVITY_TIME);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            remainingMillis.set(remainingMillis.get() - Common_Parameters.SENSITIVITY_TIME);
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
                    if(gameDifficultyIndex != Common_Parameters.NUMBER_OF_SURVIVAL_LEVELS-1){
                        gameDifficultyIndex++;
                    }
                    remainingTime.set(Common_Parameters.SURVIVAL_TIME[gameDifficultyIndex] * 1000);
                    remainingMillis.set(Common_Parameters.SURVIVAL_TIME[gameDifficultyIndex] * 1000);
                }
            }
        });
    }

    public void onBackPressed(){
        uiWorkerAllFields.cancel();
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
            score++;
            isLastPressedGreen.set(true);
            view.setScore(baseScore + score);
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
                score++;
                view.setScore(baseScore + score);
                if(isAudioEnabled){
                    view.playRight(mediaIndexRight);
                    mediaIndexRight++;
                    if(mediaIndexRight == 3){
                        mediaIndexRight = 0;
                    }
                }
            }
            else{
                isLastPressedGreen.set(false);
                remainingChances--;
                view.setChances(remainingChances);
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
            isLastPressedGreen.set(false);
            remainingChances--;
            view.setChances(remainingChances);
            if(isAudioEnabled){
                view.playWrong(mediaIndexWrong);
                mediaIndexWrong++;
                if(mediaIndexWrong == 3){
                    mediaIndexWrong = 0;
                }
            }
        }
        view.setButtonColor(buttonIndex,0);
        if(remainingChances == 0){
            isFinished = true;
            remainingTime.set(-1);
            remainingMillis.set(-1);
        }
    }

    public void onEndGameDismissRequested(){
        view.dismissEndGame(isBest);
        view.openMain();
    }
}