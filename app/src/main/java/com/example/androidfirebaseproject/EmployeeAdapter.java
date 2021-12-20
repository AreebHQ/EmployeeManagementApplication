package com.example.androidfirebaseproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.MyViewHolder> {

    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fuser = fAuth.getCurrentUser();
    private  Context context;
    private  ArrayList<String> employeeNameList;
    private Activity activity;
    private ArrayList<String> employeeImageList;
    String companyName;
    String employeeUid;

    public EmployeeAdapter(Context context, ArrayList<String> employeeNameList, ArrayList<String> employeeImageList,String companyName,  Activity activity) {
        this.context = context;
        this.employeeNameList = employeeNameList;
        this.activity = activity;
        this.employeeImageList = employeeImageList;
        this.companyName = companyName;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view =  inflater.inflate(R.layout.employee_list_row,parent,false);
        return new MyViewHolder(view);
    }



    @Override
    public int getItemCount() {
        return employeeNameList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView employeeName;
        ImageView employeeImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            employeeName = itemView.findViewById(R.id.employeeListName);
            employeeImage = itemView.findViewById(R.id.imageview_employeeImageList);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.employeeName.setText(employeeNameList.get(position));
        Picasso.get().load(employeeImageList.get(position)).resize(50,50).centerCrop().into( holder.employeeImage);
        String empName = employeeNameList.get(position);
        holder.employeeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting first and last name
                String []name = empName.split(" ");

                // Firebase reference
                fStore.collection("Users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String employeeStatus = document.getString("isAdmin");
                                        // Validate if user is not admin and not terminated; hence "employee"
                                        if (!TextUtils.isEmpty(employeeStatus) && !employeeStatus.equals("terminated")) {
                                            // validate company name
                                            String compName = document.getString("CompanyName");
                                            if (TextUtils.isEmpty(compName)) {
                                                continue;
                                            }
                                            Log.d("CompanyName ======= ", compName);
                                            if (compName.equals(companyName)) {
                                                String fname = document.getString("Firstname");
                                                String lname = document.getString("Lastname");
                                                Log.d("Employee First Name ", fname);
                                                Log.d("Employee Last Name", lname);
                                                if (fname.equals(name[0]) && lname.equals(name[1])) {
                                                    String uid = document.getString("Uid").trim();
                                                    employeeUid = uid;
                                                    Log.d("Employee UID", employeeUid);
                                                    Intent intent = new Intent(v.getContext(), EmployerAttendance.class);
                                                    intent.putExtra("Uid", employeeUid);
                                                    v.getContext().startActivity(intent);
                                                }
                                            }
                                        }
                                    }

                                }
                                else {

                                    Log.d("TAG", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });
    }
}
