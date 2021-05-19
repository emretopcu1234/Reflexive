package com.emretopcu.reflexive.models;

public class Common_Parameters {
    public static final int NUMBER_OF_CLASSIC_LEVELS = 20;
    public static final int NUMBER_OF_LEADERBOARD_USERS = 20;
    public static final int NUMBER_OF_SURVIVAL_CHANCES = 3;

    public static final int[] TARGET_CLASSIC = {10,15,30,40,50,60,70,80,90,100,100,100,100,100,100,100,100,100,100,100};
    public static final  int[] TIME_CLASSIC = {20,25,50,50,60,40,45,45,60,30,20,50,15,30,45,30,20,20,15,10};
    public static final int[] FRAGMENT_TYPE_CLASSIC = {0,0,0,1,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2}; // 0->4x4, 1->5x5, 2->6x6
    public static int CURRENT_CLASSIC_LEVEL;    // uygulama esnasında, hangi classic levelına tıklanırsa.

    public static final int[] TARGET_ARCADE = {10,20,30};   // TODO devamı doldurulacak.
    public static final int[] TIME_ARCADE = {25,20,30};     // TODO devamı doldurulacak.
    public static final int[] FRAGMENT_TYPE_ARCADE = {1,0,1};    // TODO devamı doldurulacak.

    public static final int[] FRAGMENT_TYPE_SURVIVAL = {2,0,1};  // TODO devamı doldurulacak.

    public static final int SENSITIVITY_TIME = 100; // ms
    public static final int SENSITIVITY_UI = 50;    // ms
    public static final int COUNT_DOWN_LENGTH = 100000; // ms

    public static final int[] CLASSIC_FIRE_INTERVAL = {500,400,125,100,75};    // ms  TODO devamı doldurulacak.
    public static final int[] CLASSIC_NUMBER_OF_FIRING_BUTTONS = {5,4,5,5,6};   // TODO devamı doldurulacak.

    public static final int[] CLASSIC_GREEN_LIMIT = {4,4,4,4};  // TODO devamı doldurulacak.
    public static final int[] CLASSIC_YELLOW_LIMIT = {6,6,6,6}; // TODO devamı doldurulacak.
    public static final int[] CLASSIC_TOTAL_LIMIT = {10,10,10,10};  // TODO devamı doldurulacak.

}

