package com.example.androidfirebaseproject;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PayrollAdapter extends RecyclerView.Adapter<PayrollAdapter.MyViewHolder>  {

    private Context context;
    private ArrayList<String> employeeNameList;
    private ArrayList<String> employeeAmountList;
    private ArrayList<String> employeeImageList;



    public PayrollAdapter(Context context, ArrayList<String> employeeNameList, ArrayList<String> employeeImageList,ArrayList<String> employeeAmountList) {
        this.context = context;
        this.employeeNameList = employeeNameList;
        this.employeeImageList = employeeImageList;
        this.employeeAmountList = employeeAmountList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view =  inflater.inflate(R.layout.payroll_history_row,parent,false);
        return new MyViewHolder(view);
    }




    @Override
    public int getItemCount() {
        return employeeNameList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_employeeName;
        TextView tv_empAmount;
        ImageView iv_employeeImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_employeeName = itemView.findViewById(R.id.tv_employeeListName);
            iv_employeeImage = itemView.findViewById(R.id.iv_employeeImageList);
            tv_empAmount = itemView.findViewById(R.id.amountListName);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_employeeName.setText(employeeNameList.get(position));
        holder.tv_empAmount.setText(employeeAmountList.get(position));
        Picasso.get().load(employeeImageList.get(position)).resize(50,50).centerCrop().into( holder.iv_employeeImage);


    }
}

