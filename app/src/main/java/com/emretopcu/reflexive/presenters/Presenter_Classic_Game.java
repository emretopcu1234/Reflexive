package com.emretopcu.reflexive.presenters;

import android.content.Context;
import android.os.CountDownTimer;

import com.emretopcu.reflexive.R;
import com.emretopcu.reflexive.interfaces.Interface_Classic_Game;
import com.emretopcu.reflexive.models.Common_Parameters;
import com.emretopcu.reflexive.models.User_Preferences;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Presenter_Classic_Game {

    private Context context;
    private Interface_Classic_Game view;
    private final String baseBest;
    private final String baseTarget;
    private final String baseScore;

    private final Object lock = new Object();
    private boolean isPlayActive;   // howtoplay'e basılmadan önce oyun playde miydi pauseda mıydı anlamak için
    private boolean isPaused;       // herhangi bir t anında oyun playde mi, pauseda mı anlamak için
    private AtomicInteger remainingTime;
    private AtomicInteger remainingMillis;    // sadece timefield'ını doldurmak için.
    // eğer remainingTime'da tutulsaydı, sensitivity hep 1000'e bölünmesi gereken sayılardan seçilmek zorunda kalırdı.

    private AtomicInteger[][] buttonIndicators;
    private int gameSize;
    private AtomicInteger buttonFireSequence;

    private CountDownTimer uiWorkerTime;
    private CountDownTimer uiWorkerButton;
    private ExecutorService serviceGameLogic;
    private ExecutorService serviceTimeField;

    private AtomicBoolean isLastPressedGreen;     // sari rengin nasıl davranacağını anlamak için
    private AtomicBoolean isAnyPressed;   // herhangi bir butona basılmadan sarı renk çıkmaması için
    private int score;


    public Presenter_Classic_Game(Context context, Interface_Classic_Game view) {
        this.context = context;
        this.view = view;
        baseBest = context.getString(R.string.classic_game_best);
        baseTarget = context.getString(R.string.classic_game_target);
        baseScore = context.getString(R.string.classic_game_score);
    }

    public void onActivityResumed(){
        view.setFragment(Common_Parameters.FRAGMENT_TYPE_CLASSIC[Common_Parameters.CURRENT_CLASSIC_LEVEL-1]);
        if(User_Preferences.getInstance().isAudioEnabled()){
            onAudioEnabled();
        }
        else{
            onAudioDisabled();
        }
        isPlayActive = true;
        isPaused = false;
        view.setPause();
        view.setBest(baseBest + User_Preferences.getInstance().getClassicBestLevel(Common_Parameters.CURRENT_CLASSIC_LEVEL));
        view.setTarget(baseTarget + Common_Parameters.TARGET_CLASSIC[Common_Parameters.CURRENT_CLASSIC_LEVEL-1]);
        view.setScore(baseScore + "0");
        view.setTime(Integer.toString(Common_Parameters.TIME_CLASSIC[Common_Parameters.CURRENT_CLASSIC_LEVEL-1]));
        if(User_Preferences.getInstance().isClassicFirstEntrance()){
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
        synchronized (lock){
            lock.notifyAll();
        }
    }

    public void onPauseClicked(){
        isPlayActive = false;
        isPaused = true;
        view.setPlay();
    }

    public void onQuestionClicked(){
        isPaused = true;
        view.setPlay();
        view.openHowToPlay();
    }

    public void onQuestionDismissRequested(){
        isPaused = !isPlayActive;
        view.dismissHowToPlay();
        if(User_Preferences.getInstance().isClassicFirstEntrance()){
            User_Preferences.getInstance().setClassicFirstEntrance(false);
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
    }

    public void onAudioDisabled() {
        User_Preferences.getInstance().setAudioEnabled(false);
        view.setAudioDisabled();
    }

    private void countToStart() {
        view.openCountToStart();
        new CountDownTimer(3000, 100){
            // 3000, 1000 yapınca onTick'in içine 2 kere giriyor.
            // 3.sefer girmesi için 4000, 1000 yapmak gerekiyor, bu kez de 1 saniye fazladan bekleniyor.
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
        switch (Common_Parameters.FRAGMENT_TYPE_CLASSIC[Common_Parameters.CURRENT_CLASSIC_LEVEL-1]){
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

        isLastPressedGreen = new AtomicBoolean();
        isAnyPressed = new AtomicBoolean();
        isAnyPressed.set(false);
        score = 0;

        remainingTime = new AtomicInteger();
        remainingMillis = new AtomicInteger();
        remainingTime.set(Common_Parameters.TIME_CLASSIC[Common_Parameters.CURRENT_CLASSIC_LEVEL-1] * 1000);   // ms
        remainingMillis.set(Common_Parameters.TIME_CLASSIC[Common_Parameters.CURRENT_CLASSIC_LEVEL-1] * 1000); // ms

        // normal timer kullanamıyoruz, çünkü farklı bir thread'e geçiyor,
        // farklı thread'den de ui componentlarına erişemiyoruz.
        // ama countdowntimer main thread'den devam ediyor.
        // o yüzden countdowntimer kullanılıyor, süre dolunca yeniden başlıyor.
        uiWorkerTime = new CountDownTimer(Common_Parameters.COUNT_DOWN_LENGTH, 500) {
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
                }
            }

            @Override
            public void onFinish() {
                // if(isPlayActive) ekleyemeyiz, oyun pause edildiğinde buraya gelmiş olabilir.
                // zaten oyun bittiğinde her türlü burasi stop edilecek.
                view.setTime(Integer.toString(remainingMillis.get()/1000));
                this.start();
            }
        }.start();

        uiWorkerButton = new CountDownTimer(Common_Parameters.COUNT_DOWN_LENGTH, Common_Parameters.SENSITIVITY_UI) {
            @Override
            public void onTick(long millisUntilFinished) {
                for(int i=0; i<gameSize; i++){
                    if(buttonIndicators[i][0].get() != 0){
                        view.setButtonColor(i,buttonIndicators[i][0].get());
                    }
                    if(buttonIndicators[i][1].get() == ((buttonFireSequence.get()) % (Common_Parameters.CLASSIC_NUMBER_OF_FIRING_BUTTONS[Common_Parameters.CURRENT_CLASSIC_LEVEL-1]+1))){
                        if(buttonIndicators[i][0].get() != 0){  // kullanıcı tıkladıysa 0'lanmış olabilir.
                            buttonIndicators[i][0].set(0);
                            view.setButtonColor(i,0);
                        }
                    }
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
                int randomColorIndicator = -1;   // 0-3 green, 4-5 yellow, 6-9 red
                Random random = new Random();

                boolean isYellowAllowed = false;
                while(remainingTime.get() > 0){
                    if(!isPaused){
                        candidateCell = random.nextInt(gameSize);
                        while(buttonIndicators[candidateCell][0].get() != 0){
                            candidateCell = random.nextInt(gameSize);
                        }
                        randomColorIndicator = random.nextInt(Common_Parameters.CLASSIC_TOTAL_LIMIT[Common_Parameters.CURRENT_CLASSIC_LEVEL]-1);
                        while(!isYellowAllowed){
                            if(isAnyPressed.get()){
                                isYellowAllowed = true;
                            }
                            if(!(randomColorIndicator >= Common_Parameters.CLASSIC_GREEN_LIMIT[Common_Parameters.CURRENT_CLASSIC_LEVEL]-1
                                && randomColorIndicator < Common_Parameters.CLASSIC_YELLOW_LIMIT[Common_Parameters.CURRENT_CLASSIC_LEVEL]-1)){
                                break;
                            }
                            randomColorIndicator = random.nextInt(Common_Parameters.CLASSIC_TOTAL_LIMIT[Common_Parameters.CURRENT_CLASSIC_LEVEL]-1);
                        }

                        if(randomColorIndicator < Common_Parameters.CLASSIC_GREEN_LIMIT[Common_Parameters.CURRENT_CLASSIC_LEVEL]-1){
                            buttonIndicators[candidateCell][0].set(1);
                        }
                        else if(randomColorIndicator < Common_Parameters.CLASSIC_YELLOW_LIMIT[Common_Parameters.CURRENT_CLASSIC_LEVEL]-1){
                            buttonIndicators[candidateCell][0].set(2);
                        }
                        else{
                            buttonIndicators[candidateCell][0].set(3);
                        }
                        if(buttonFireSequence.get() > Common_Parameters.CLASSIC_NUMBER_OF_FIRING_BUTTONS[Common_Parameters.CURRENT_CLASSIC_LEVEL-1]){
                            buttonFireSequence.set(0);
                        }
                        buttonIndicators[candidateCell][1].set(buttonFireSequence.get());
                        buttonFireSequence.set(buttonFireSequence.get() + 1);

                        try {
                            Thread.sleep(Common_Parameters.CLASSIC_FIRE_INTERVAL[Common_Parameters.CURRENT_CLASSIC_LEVEL-1]);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        remainingTime.set(remainingTime.get() - Common_Parameters.CLASSIC_FIRE_INTERVAL[Common_Parameters.CURRENT_CLASSIC_LEVEL-1]);
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
                while(remainingMillis.get() > 0){
                    if(!isPaused) {
                        try {
                            Thread.sleep(Common_Parameters.SENSITIVITY_TIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        remainingMillis.set(remainingMillis.get()- Common_Parameters.SENSITIVITY_TIME);
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
                uiWorkerTime.cancel();
                uiWorkerButton.cancel();
                // TODO arcade kısmında direkt olarak cancel etmek yerine,
                // hedefe ulaşılma durumuna göre remainingmillis'i güncelle.
                // hedefe ulaşılamadıysa cancel et.
            }
        });
    }

    public void onBackPressed(){
        uiWorkerTime.cancel();
        uiWorkerButton.cancel();
        serviceGameLogic.shutdownNow();
        serviceTimeField.shutdownNow();

        view.openClassicMenu();
    }

    public void onButtonClicked(int buttonIndex){
        if(buttonIndicators[buttonIndex][0].get() == 1){
            buttonIndicators[buttonIndex][0].set(0);
            if(!isAnyPressed.get()){
                isAnyPressed.set(true);
            }
            score++;
            isLastPressedGreen.set(true);
            view.setScore(baseScore + Integer.toString(score));
        }
        else if(buttonIndicators[buttonIndex][0].get() == 2){
            buttonIndicators[buttonIndex][0].set(0);
            // isanypressed kısmına gerek yok. zaten sarı yandıysa önceden true'ya çekilmiş demektir.
            if(isLastPressedGreen.get()){
                score++;
                view.setScore(baseScore + Integer.toString(score));
            }
            else{
                remainingMillis.set(remainingMillis.get() - 1000);
                remainingTime.set(remainingTime.get() - 1000);
                isLastPressedGreen.set(false);
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
        }
        view.setButtonColor(buttonIndex,0);
    }

    // TODO play pause'da fragmenttaki işlemleri yapmayı unutma.
    // TODO ses efektlerini hallet.

}
