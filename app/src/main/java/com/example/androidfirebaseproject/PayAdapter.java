package com.example.androidfirebaseproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PayAdapter extends RecyclerView.Adapter<PayAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> payHistoryList;
    private Activity activity;
    ArrayList<String> weekList;
    ArrayList<String> imageList;

    public PayAdapter(Context context,ArrayList<String> imageList, ArrayList<String> payHistoryList, ArrayList<String> weekList, Activity activity)
    {
        this.context = context;
        this.payHistoryList = payHistoryList;
        this.activity = activity;
        this.weekList = weekList;
        this.imageList = imageList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view =  inflater.inflate(R.layout.employee_pay_history_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return payHistoryList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_week;
        TextView tv_amount;
        ImageView iv_displayImage;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_week = itemView.findViewById(R.id.weekListName);
            tv_amount = itemView.findViewById(R.id.amountListName);
            iv_displayImage = itemView.findViewById(R.id.iv_employeeImageList);

        }
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tv_week.setText(weekList.get(position));
        holder.tv_amount.setText(payHistoryList.get(position));
        if (imageList.size()>0){
        if (imageList.get(position) != null)
        {
            Picasso.get().load(imageList.get(position)).resize(50,50).centerCrop().into( holder.iv_displayImage);
        }}

    }
}