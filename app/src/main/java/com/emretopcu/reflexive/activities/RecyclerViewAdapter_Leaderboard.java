package com.emretopcu.reflexive.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.emretopcu.reflexive.R;
import com.emretopcu.reflexive.models.Common_Parameters_Variables;
import com.emretopcu.reflexive.models.Leaderboard_Info;

public class RecyclerViewAdapter_Leaderboard extends RecyclerView.Adapter<ViewHolder_LB> {

    private Context context;
    private int selectedMode;
    private int numberOnLeaderboard;

    public RecyclerViewAdapter_Leaderboard(Context context, int selectedMode, int numberOnLeaderboard) {
        this.context = context;
        this.selectedMode = selectedMode;
        this.numberOnLeaderboard = numberOnLeaderboard;
    }

    @Override
    public ViewHolder_LB onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_leaderboard, parent, false);
        ViewHolder_LB holder = new ViewHolder_Leaderboard(v);
        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder_LB viewHolder, int position) {
        final ViewHolder_Leaderboard holder = (ViewHolder_Leaderboard) viewHolder;

        if(selectedMode == 0){
            holder.number.setText(Integer.toString(position + 1));
            holder.username.setText(Leaderboard_Info.getInstance().getClassicLeaderboard()[position].getUsername());
            holder.score.setText(Integer.toString(Leaderboard_Info.getInstance().getClassicLeaderboard()[position].getBest()));
            if(position == numberOnLeaderboard){
                holder.number.setTextColor(context.getResources().getColor(R.color.red));
                holder.username.setTextColor(context.getResources().getColor(R.color.red));
                holder.score.setTextColor(context.getResources().getColor(R.color.red));
            }
        }
        else if(selectedMode == 1){
            holder.number.setText(Integer.toString(position + 1));
            holder.username.setText(Leaderboard_Info.getInstance().getArcadeLeaderboard()[position].getUsername());
            holder.score.setText(Integer.toString(Leaderboard_Info.getInstance().getArcadeLeaderboard()[position].getBest()));
            if(position == numberOnLeaderboard){
                holder.number.setTextColor(context.getResources().getColor(R.color.red));
                holder.username.setTextColor(context.getResources().getColor(R.color.red));
                holder.score.setTextColor(context.getResources().getColor(R.color.red));
            }
        }
        else{
            holder.number.setText(Integer.toString(position + 1));
            holder.username.setText(Leaderboard_Info.getInstance().getSurvivalLeaderboard()[position].getUsername());
            holder.score.setText(Integer.toString(Leaderboard_Info.getInstance().getSurvivalLeaderboard()[position].getBest()));
            if(position == numberOnLeaderboard){
                holder.number.setTextColor(context.getResources().getColor(R.color.red));
                holder.username.setTextColor(context.getResources().getColor(R.color.red));
                holder.score.setTextColor(context.getResources().getColor(R.color.red));
            }
        }
    }


    @Override
    public int getItemCount() {
        return Common_Parameters_Variables.NUMBER_OF_LEADERBOARD_USERS;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}

class ViewHolder_LB extends RecyclerView.ViewHolder {
    public ViewHolder_LB(View v) {
        super(v);
    }
}

class ViewHolder_Leaderboard extends ViewHolder_LB {
    public TextView number;
    public TextView username;
    public TextView score;

    public ViewHolder_Leaderboard(View view) {
        super(view);
        number = view.findViewById(R.id.textView_rv_leaderboard_number);
        username = view.findViewById(R.id.textView_rv_leaderboard_username);
        score = view.findViewById(R.id.textView_rv_leaderboard_score);
    }
}

