package com.example.bin.weatherapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * @Author: Bhy
 * @Date: 2018/11/30
 */
public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView condPic;
    TextView skyCon;
    TextView date;
    TextView tmpMax;
    TextView tmpMin;
    private myAdapter.MyItemClickListener mMyItemClickListener;
    private static final String TAG = "MyViewHolder";


    public myViewHolder(View view, myAdapter.MyItemClickListener myItemClickListener) {
        super(view);
        this.mMyItemClickListener = myItemClickListener;
        view.setOnClickListener(this);
        try{
            //绑定空间
            condPic = view.findViewById(R.id.skyConPic_textView);
            skyCon = view.findViewById(R.id.skyCon);
            date = view.findViewById(R.id.date_textView);
            tmpMax = view.findViewById(R.id.tmp_max);
            tmpMin = view.findViewById(R.id.tmp_min);
        }catch (Exception e){
            Log.i(TAG, "MyViewHolder: find view fail here");
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (mMyItemClickListener!=null){
            mMyItemClickListener.onItemClick(view,getAdapterPosition());
        }
    }
}