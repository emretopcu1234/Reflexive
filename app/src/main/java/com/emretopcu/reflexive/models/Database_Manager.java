package com.emretopcu.reflexive.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.emretopcu.reflexive.presenters.Presenter_First_Entrance;
import com.emretopcu.reflexive.presenters.Presenter_Main;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Database_Manager {

    private static Database_Manager INSTANCE = new Database_Manager();
    private FirebaseFirestore db;
    private CollectionReference dbRef;

    private Database_Manager(){
        db = FirebaseFirestore.getInstance();
        dbRef = db.collection("users");
    }

    public static Database_Manager getInstance(){
        return INSTANCE;
    }

    public void requestUsernameEdit(Presenter_Main presenter, String newUsername) {
        dbRef.document(newUsername).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        presenter.onUsernameEditResponded(false, newUsername);
                    }
                    else {
                        Map<String, Object> data = new HashMap<>();
                        data.put("classic_best", User_Info.getInstance().getClassicBest());
                        data.put("arcade_best", User_Info.getInstance().getArcadeBest());
                        data.put("survival_best", User_Info.getInstance().getSurvivalBest());
                        dbRef.document(newUsername).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dbRef.document(User_Info.getInstance().getUsername()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        User_Info.getInstance().setUsername(newUsername);
                                        presenter.onUsernameEditResponded(true, newUsername);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        presenter.onUsernameEditResponded(false, newUsername);
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                presenter.onUsernameEditResponded(false, newUsername);
                            }
                        });
                    }
                }
                else {
                    presenter.onUsernameEditResponded(false, newUsername);
                }
            }
        });
    }

    public void requestUsernameAdd(Presenter_First_Entrance presenter, String newUsername) {
        dbRef.document(newUsername).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        presenter.onUsernameAddResponded(false, newUsername);
                    }
                    else {
                        Map<String, Object> data = new HashMap<>();
                        data.put("classic_best", User_Info.getInstance().getClassicBest());
                        data.put("arcade_best", User_Info.getInstance().getArcadeBest());
                        data.put("survival_best", User_Info.getInstance().getSurvivalBest());
                        dbRef.document(newUsername).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                User_Info.getInstance().setUsername(newUsername);
                                presenter.onUsernameAddResponded(true, newUsername);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                presenter.onUsernameAddResponded(false, newUsername);
                            }
                        });
                    }
                }
                else {
                    presenter.onUsernameAddResponded(false, newUsername);
                }
            }
        });
    }

    public void getLeaderboardInfo(){
        dbRef.orderBy("classic_best", Query.Direction.DESCENDING).limit(Common_Parameters.NUMBER_OF_LEADERBOARD_USERS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(int i=0; i<task.getResult().size(); i++) {
                    Leaderboard_Info_Object_Type data = new Leaderboard_Info_Object_Type(
                            task.getResult().getDocuments().get(i).getId(),
                            Integer.parseInt(task.getResult().getDocuments().get(i).get("classic_best").toString()));
                    Leaderboard_Info.getInstance().getClassicLeaderboard()[i] = data;
                }
            }
        });
        dbRef.orderBy("arcade_best", Query.Direction.DESCENDING).limit(Common_Parameters.NUMBER_OF_LEADERBOARD_USERS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(int i=0; i<task.getResult().size(); i++) {
                    Leaderboard_Info_Object_Type data = new Leaderboard_Info_Object_Type(
                            task.getResult().getDocuments().get(i).getId(),
                            Integer.parseInt(task.getResult().getDocuments().get(i).get("arcade_best").toString()));
                    Leaderboard_Info.getInstance().getArcadeLeaderboard()[i] = data;
                }
            }
        });
        dbRef.orderBy("survival_best", Query.Direction.DESCENDING).limit(Common_Parameters.NUMBER_OF_LEADERBOARD_USERS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(int i=0; i<task.getResult().size(); i++) {
                    Leaderboard_Info_Object_Type data = new Leaderboard_Info_Object_Type(
                            task.getResult().getDocuments().get(i).getId(),
                            Integer.parseInt(task.getResult().getDocuments().get(i).get("survival_best").toString()));
                    Leaderboard_Info.getInstance().getSurvivalLeaderboard()[i] = data;
                }
            }
        });
    }

}
















// UNUTMAMAK İÇİN!!!
//        dbRef.orderBy("classic_best").limit(3).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                for(int i=0; i<task.getResult().size(); i++) {
//                    Log.d("Firestore", "order " + task.getResult().getDocuments().get(i).get("arcade_best").toString());
//                }
//            }
//        });