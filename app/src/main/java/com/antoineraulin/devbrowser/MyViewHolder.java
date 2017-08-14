package com.antoineraulin.devbrowser;

/**
 * Created by antoi on 12/08/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MyViewHolder extends RecyclerView.ViewHolder{

    private TextView fileViewView;
    private TextView timeTextView;

    //itemView est la vue correspondante à 1 cellule
    public MyViewHolder(View itemView) {
        super(itemView);

        //c'est ici que l'on fait nos findView

        fileViewView = (TextView) itemView.findViewById(R.id.textName);
        timeTextView = (TextView) itemView.findViewById(R.id.textTime);
    }

    //puis ajouter une fonction pour remplir la cellule en fonction d'un MyObject
    public void bind(final MyObject myObject){
        fileViewView.setText(myObject.getFile());
        timeTextView.setText(myObject.getTimeMilli()+"ms");
    }
}