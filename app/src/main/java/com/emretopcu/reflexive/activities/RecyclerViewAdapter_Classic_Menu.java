package com.emretopcu.reflexive.activities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.emretopcu.reflexive.R;
import com.emretopcu.reflexive.models.Common_Parameters;
import com.emretopcu.reflexive.presenters.Presenter_Classic_Menu;


public class RecyclerViewAdapter_Classic_Menu extends RecyclerView.Adapter<ViewHolder_CM> {

    private Context context;
    private int maxUnlockedPosition;

    public RecyclerViewAdapter_Classic_Menu(int maxUnlockedPosition, Context context) {
        this.context = context;
        this.maxUnlockedPosition = maxUnlockedPosition;
    }

    @Override
    public ViewHolder_CM onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_classic_menu, parent, false);
        ViewHolder_CM holder = new ViewHolder_Classic_Menu(v);
        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder_CM viewHolder, int position) {

        final ViewHolder_Classic_Menu holder = (ViewHolder_Classic_Menu) viewHolder;
        holder.level.setSoundEffectsEnabled(false);
        holder.level.setText(context.getString(R.string.rv_classic_menu_level) + Integer.toString(position + 1));

        if(position <= maxUnlockedPosition){
            holder.level.setEnabled(true);
            holder.level.setBackground(context.getDrawable(R.drawable.background_button_menu_dark_gray));
        }
        else{
            holder.level.setEnabled(false);
            holder.level.setBackground(context.getDrawable(R.drawable.background_button_menu_intermediate_gray));
        }
        holder.level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity_Classic_Menu)context).onButtonClickedAtPosition(position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return Common_Parameters.NUMBER_OF_CLASSIC_LEVELS;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}

class ViewHolder_CM extends RecyclerView.ViewHolder {
    public ViewHolder_CM(View v) {
        super(v);
    }
}

class ViewHolder_Classic_Menu extends ViewHolder_CM {
    public Button level;

    public ViewHolder_Classic_Menu(View view) {
        super(view);
        level = view.findViewById(R.id.button_rv_classic_menu_level);
    }
}
