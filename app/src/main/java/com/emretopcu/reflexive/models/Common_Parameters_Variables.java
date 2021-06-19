package com.emretopcu.reflexive.models;

public class Common_Parameters_Variables {
    public static final int NUMBER_OF_CLASSIC_LEVELS = 20;
    public static final int NUMBER_OF_ARCADE_LEVELS = 20;
    public static final int NUMBER_OF_SURVIVAL_LEVELS = 20;
    public static final int NUMBER_OF_LEADERBOARD_USERS = 20;
    public static final int NUMBER_OF_SURVIVAL_CHANCES = 3;

    public static final int ARCADE_UNLOCK_LEVEL = 6;
    public static final int SURVIVAL_UNLOCK_LEVEL = 11;

    public static final int AD_TIME_INTERVAL = 75000;  // ms

    public static final int SENSITIVITY_TIME = 100; // ms
    public static final int SENSITIVITY_UI = 50;    // ms
    public static final int COUNT_DOWN_LENGTH = 100000; // ms

    public static final int[] CLASSIC_TARGET = {10,50,100,125,50,250,300,400,450,250,500,600,700,750,900,1000,1100,1250,1300,2000};
    public static final int[] CLASSIC_TIME = {15,20,20,20,10,30,30,35,35,15,30,30,40,40,40,40,40,40,35,45};
    public static final int[] CLASSIC_FRAGMENT_TYPE = {0,0,0,0,0,1,1,1,1,1,2,2,2,2,2,2,2,2,2,2}; // 0->4x4, 1->5x5, 2->6x6
    public static final int[] CLASSIC_FIRE_INTERVAL = {600,550,500,465,450,450,400,400,350,350,400,375,350,325,300,300,300,300,300,300};    // ms
    public static final int[] CLASSIC_NUMBER_OF_FIRING_BUTTONS = {4,4,4,4,4,5,5,5,5,5,6,6,6,6,6,6,6,6,6,6};
    public static final int[] CLASSIC_GREEN_LIMIT = {5,5,5,5,5,5,5,5,5,4,5,5,5,5,4,4,4,4,4,5};
    public static final int[] CLASSIC_YELLOW_LIMIT = {8,8,8,7,7,7,7,7,7,6,7,7,7,6,6,5,6,6,6,7};
    public static final int[] CLASSIC_TOTAL_LIMIT = {10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10};

    public static final int[] ARCADE_TARGET = {10,50,150,275,325,575,900,1250,1750,2000,2500,3000,3750,4500,5500,6500,7500,8750,10000,12500};   // son indis onemsiz aslinda cunku "hedef -" gorunecek
    public static final int[] ARCADE_TIME = {15,20,20,20,10,30,30,35,35,15,30,30,40,40,40,40,40,40,35,45};
    public static final int[] ARCADE_FRAGMENT_TYPE = {0,0,0,0,0,1,1,1,1,1,2,2,2,2,2,2,2,2,2,2}; // 0->4x4, 1->5x5, 2->6x6
    public static final int[] ARCADE_FIRE_INTERVAL = {600,550,500,465,450,450,400,400,350,350,400,375,350,325,300,300,300,300,300,300};    // ms
    public static final int[] ARCADE_NUMBER_OF_FIRING_BUTTONS = {4,4,4,4,4,5,5,5,5,5,6,6,6,6,6,6,6,6,6,6};
    public static final int[] ARCADE_GREEN_LIMIT = {5,5,5,5,5,5,5,5,5,4,5,5,5,5,4,4,4,4,4,5};
    public static final int[] ARCADE_YELLOW_LIMIT = {8,8,8,7,7,7,7,7,7,6,7,7,7,6,6,5,6,6,6,7};
    public static final int[] ARCADE_TOTAL_LIMIT = {10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10};

    public static final int[] SURVIVAL_TIME = {10,10,10,15,15,15,20,20,20,25,25,25,30,30,30,30,30,30,30,30};
    public static final int[] SURVIVAL_FRAGMENT_TYPE = {0,0,0,0,0,1,1,1,1,1,2,2,2,2,2,2,2,2,2,2};
    public static final int[] SURVIVAL_FIRE_INTERVAL = {1250,1200,1100,1000,900,850,800,500,475,450,400,375,350,300,300,275,250,250,225,200};    // ms
    public static final int[] SURVIVAL_NUMBER_OF_FIRING_BUTTONS = {1,1,1,1,1,1,1,2,2,2,2,2,2,2,2,2,2,3,3,3};
    public static final int[] SURVIVAL_GREEN_LIMIT = {4,4,4,5,5,5,5,6,6,6,6,6,6,7,7,7,7,7,7,7};
    public static final int[] SURVIVAL_YELLOW_LIMIT = {6,6,6,6,6,7,7,7,7,8,8,8,8,8,8,8,9,9,9,9};
    public static final int[] SURVIVAL_TOTAL_LIMIT = {10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10};

    public static long LAST_AD_TIME;
    public static int CURRENT_CLASSIC_LEVEL;
    public static boolean IS_NEW_LEADERBOARD_VISITED;   // eger kullanıcı kendi rekorunu kırarsa ve bu rekor genel sıralamada da ilk 20'deyse
    // kullanıcı leaderboard'a tıkladığında veritabanından bilgiler tekrar okunmalı. ama rekordan sonra kullanıcı leaderboard'a girdiyse,
    // sonrasında 2.defa girmek isterse bir daha veritabanından okunmasına gerek yok.
    // ya da kullanıcı adını değiştirdiyse yine benzer şekilde veritabanından tekrar bilgiler okunmalı.
    // niye boyle bir degisken kullanmadan her rekorda veritabanından okuma yapmıyoruz?
    // cunku kullanıcı leaderboard'u görmek istemiyorsa bosuna firebase okuma sayisini artırmamak icin
}