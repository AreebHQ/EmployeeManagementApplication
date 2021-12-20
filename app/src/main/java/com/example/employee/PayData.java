package com.example.employee;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidfirebaseproject.LoginPageOriginal;
import com.example.androidfirebaseproject.PayAdapter;
import com.example.androidfirebaseproject.PayrollAdapter;
import com.example.androidfirebaseproject.R;
import com.example.androidfirebaseproject.WeeklyPayroll;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class PayData extends AppCompatActivity {

    DateFormat checkMonth = new SimpleDateFormat("MM");
    DateFormat checkYear = new SimpleDateFormat("yyyy");
    DateFormat  dateFormat = new SimpleDateFormat("MM-dd-yyyy");
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fuser = fAuth.getCurrentUser();
    String []months = {"null","jan","feb","mar","apr","may","jun","jul","aug","sep","oct","nov","dec"};
    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    ArrayList<String> weekDays = new ArrayList<>();
    ArrayList<String> dailyHours = new ArrayList<>();
    ArrayList<String> prevWeeksTotal = new ArrayList<>();
    ArrayList<String> oneWeekList = new ArrayList<>();
    Map<String,String> dateHours = new HashMap<>();
    ArrayList<String> imageList = new ArrayList<>();
    float payRate;
    int week = 0;
    int totalMinutes = 0;
    int totalHours = 0;
    int overtimeHours = 0;
    int overtimeMins = 0;
    double amount = 0.0;
    String hireDate;
    String strAmount;
    Button  btn_previous, btn_next;
    TextView tv_payrate, tv_amount, tv_hours, tv_overTimeRate, tv_payDate, tv_overtimeHours;
    RecyclerView recyclerView;
    PayAdapter payAdapter;
    ProgressBar pb_overtime, pb_hours, pb_earned;
    ProgressBar pb_mainPbar;

    // Navigation Drawer
    NavigationView navView;
    DrawerLayout drawer;
    Toolbar toolbar;
    TextView navFirstName;
    TextView navEmail;
    CircleImageView navProfilePic;
    String navProfilePicUri, navEmailAddress, navUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_data);
        tv_amount = findViewById(R.id.tv_amount);
        tv_hours = findViewById(R.id.tv_hours);
        tv_payrate = findViewById(R.id.tv_payrate);
        btn_previous = findViewById(R.id.btn_previous);
        recyclerView = findViewById(R.id.recycleView_pay);
        btn_next = findViewById(R.id.btn_calculate);
        tv_payDate = findViewById(R.id.tv_payDate);
        tv_overTimeRate = findViewById(R.id.tv_overtimeRate);
        tv_overtimeHours = findViewById(R.id.tv_overtimeHours);
        pb_overtime = findViewById(R.id.progress_bar2);
        pb_hours = findViewById(R.id.progress_bar3);
        pb_earned = findViewById(R.id.progress_bar1);
        pb_mainPbar = findViewById(R.id.pb_pbMainbar);

        navView = findViewById(R.id.nav_view_main_menu);
        drawer = findViewById(R.id.drawerLayoutMainMenu);
        toolbar = findViewById(R.id.app_Bar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24);
        View header = navView.getHeaderView(0);
        navFirstName = header.findViewById(R.id.nav_header_first_name);
        navEmail = header.findViewById(R.id.nav_header_email);
        navProfilePic = header.findViewById(R.id.nav_header_picture);


        // Setting NavigationDrawer info
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            navUserName = extras.getString("thisName");
            navEmailAddress = extras.getString("thisEmail");
            navProfilePicUri = extras.getString("ProfilePicUri");
        }
        Picasso.get().load(navProfilePicUri).into(navProfilePic);
        navEmail.setText(navEmailAddress);
        navFirstName.setText(navUserName);

        navView.setCheckedItem(R.id.navEmpPay);
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navHome) {
                Intent intent = new Intent(getApplicationContext(), EmployeeLanding.class);
                intent.putExtra("thisName", navUserName);
                intent.putExtra("thisEmail", navEmailAddress);
                intent.putExtra("ProfilePicUri", navProfilePicUri);
                startActivity(intent);
            } else if (id == R.id.navEmpPay) {
                Intent intent = new Intent(getApplicationContext(), PayData.class);
                intent.putExtra("thisName", navUserName);
                intent.putExtra("thisEmail", navEmailAddress);
                intent.putExtra("ProfilePicUri", navProfilePicUri);
                startActivity(intent);
            } else if (id == R.id.navEmpProfile) {
                Intent intent = new Intent(getApplicationContext(), EmployeeProfile.class);
                intent.putExtra("thisName", navUserName);
                intent.putExtra("thisEmail", navEmailAddress);
                intent.putExtra("ProfilePicUri", navProfilePicUri);
                startActivity(intent);
            } else if (id == R.id.navEmpAttendance) {
                Intent intent = new Intent(getApplicationContext(), EmployeeAttendance.class);
                intent.putExtra("thisName", navUserName);
                intent.putExtra("thisEmail", navEmailAddress);
                intent.putExtra("ProfilePicUri", navProfilePicUri);
                startActivity(intent);
            } else if (id == R.id.navEmpClock) {
                Intent intent = new Intent(getApplicationContext(), EmployeeClock.class);
                intent.putExtra("thisName", navUserName);
                intent.putExtra("thisEmail", navEmailAddress);
                intent.putExtra("ProfilePicUri", navProfilePicUri);
                startActivity(intent);
            } else if (id == R.id.navEmpSetting) {
                Intent intent = new Intent(getApplicationContext(), EmployeeSettings.class);
                intent.putExtra("thisName", navUserName);
                intent.putExtra("thisEmail", navEmailAddress);
                intent.putExtra("ProfilePicUri", navProfilePicUri);
                startActivity(intent);
            } else if (id == R.id.navSignOut) {
                // Signing user out from app
                FirebaseAuth.getInstance().signOut();
                // Calling login activity
                startActivity(new Intent(getApplicationContext(), LoginPageOriginal.class));
                finish();
            }
            DrawerLayout drawer = findViewById(R.id.drawerLayoutMainMenu);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        //Calling current week's hours status
        getPayRate();

        // Setting display data after 1 second
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getCurrentWeekHours();
                pb_mainPbar.setVisibility(View.INVISIBLE);

            }
        }, 1000);


        // Setting recycler view with weekly payroll
        getRecyclerWeekList(this);

        // button to navigate trough weekly data
        btn_next.setOnClickListener(v->{  getWeeklyHours(+1); });
        btn_previous.setOnClickListener(v->{  getWeeklyHours(-1); });

    }


    private void getCurrentWeekHours() {

        Date refDate = new Date();
        int firstDayOfWeek = Calendar.getInstance().getFirstDayOfWeek();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(refDate);
        calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
        Date[] daysOfWeek = new Date[7];

        for (int i = 0; i < 7; i++) {

            Date systemTime = calendar.getTime();
            String currentDate = dateFormat.format(systemTime);
            weekDays.add(currentDate);
            daysOfWeek[i] = calendar.getTime();
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        }

        // Setting last day of week as pay date
        String payDate =  dateFormat.format(daysOfWeek[6]);
        tv_payDate.setText(payDate);
        // Getting Month Year and Time separately
        String strMon= checkMonth.format(refDate);
        String strYear = checkYear.format(refDate);
        // Getting month name from month array
        String monthName = months[Integer.parseInt(strMon)];

        for (int i=0; i<weekDays.size(); i++) {
            // Creating documents reference for user in Firabase database
            Task<DocumentSnapshot> docReference = fStore.collection("Users").document(fuser.getUid())
                    .collection("Attendance").document(strYear).collection(monthName).document(weekDays.get(i))
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String hours = document.getString("Hours");
                                    String date = document.getString("Date");
                                    if (TextUtils.isEmpty(hours))
                                    {
                                        hours = "00:00";
                                    }
                                     dailyHours.add(hours);
                                }
                            }
                            getHours();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                       Toast.makeText(getApplicationContext(),"Data not found",Toast.LENGTH_LONG).show();
                        }
                    });
        }

    }


    public void getRecyclerWeekList(Context context)
    {
        Date newDate = new Date();
        // Getting Year
        String strYear = checkYear.format(newDate);

        // Creating documents reference for user in Firabase database
        Task<QuerySnapshot> docReference = fStore.collection("Users").document(fuser.getUid())
                .collection("Payroll").document(strYear).collection("Week").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String weekNumber = document.getString("Week");

                                if (!TextUtils.isEmpty(weekNumber)) {
                                    oneWeekList.add(weekNumber);
                                }
                                String amount = document.getString("Amount");
                                if (!TextUtils.isEmpty(amount)) {
                                    prevWeeksTotal.add(amount);
                                }
                            }
                        }  else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                        // Calling recycler adapter
                        payAdapter = new PayAdapter(context,imageList, prevWeeksTotal,oneWeekList,PayData.this);
                        recyclerView.setAdapter(payAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    }
                });
    }




    public void getWeeklyHours(int num)
    {
        int backDays = 0;
            Date refDate = new Date();
            int month =  calendar.get(Calendar.MONTH)+1;
            int firstDayOfWeek = Calendar.getInstance().getFirstDayOfWeek();
            Calendar prevCalander = Calendar.getInstance();
            prevCalander.setTime(refDate);

            // setting week
             week = week + num;

            prevCalander.add(Calendar.WEEK_OF_YEAR, week);
            int currentMonth = prevCalander.get(Calendar.MONTH) + 1;
            Date prevDate = prevCalander.getTime();

            prevCalander.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
            Date[] daysOfWeek = new Date[7];
            backDays = (int) ((refDate.getTime() - prevDate.getTime())
                    / (1000 * 60 * 60 * 24));

            if (backDays < 60) {
                weekDays.clear();
                dailyHours.clear();

                for (int i = 0; i < 7; i++) {

                    Date systemTime = prevCalander.getTime();
                    String currentDate = dateFormat.format(systemTime);
                    String currentDay = checkMonth.format(systemTime);
                    weekDays.add(currentDate);
                    daysOfWeek[i] = prevCalander.getTime();
                    prevCalander.add(Calendar.DAY_OF_MONTH, 1);
                    String strMon= checkMonth.format(systemTime).toString();
                    String strYear = checkYear.format(systemTime).toString();
                    // Getting month name from month array
                    String monthName = months[Integer.parseInt(strMon)];

                    // Creating documents reference for user in Firabase database
                    Task<DocumentSnapshot> docReference = fStore.collection("Users").document(fuser.getUid())
                            .collection("Attendance").document(strYear).collection(monthName).document(weekDays.get(i))
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            String hours = document.getString("Hours");
                                            String date = document.getString("Date");
                                            Log.d("Hours Found", "Hours Found for: " + date + " are: " + hours);

                                            // dateHours.put(date,hours);
                                            if (!TextUtils.isEmpty(hours)) {
                                                dateHours.put(date, hours);
                                                dailyHours.add(hours);
                                                Log.d("Hours Added", "Hours Added for: " + date + " are: " + hours);
                                            }
                                        }
                                    }
                                    getHours();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Day", "Day Not Found" + "  " + e);
                                }
                            });
                }

            } else {
                Log.d("DATE-DATE", "Back Days more than 90 : " + backDays);
                Toast.makeText(getApplicationContext(),"Error: You have reached to date before hire date", Toast.LENGTH_LONG).show();

            }
    }


    public void getPayRate()
    {
        // Creating documents reference for user in Firabase database
        Task<DocumentSnapshot> docReference = fStore.collection("Users").document(fuser.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String payrate = document.getString("PayRate");
                                if (!TextUtils.isEmpty(payrate)){
                                payRate = Float.parseFloat(payrate);
                                tv_payrate.setText(payrate);
                                    tv_overTimeRate.setText((String.valueOf(payRate*1.5)));
                                }
                                String hiredate = document.getString("HireDate");
                                Log.d("getPayRate", "Employee Hire Date: " +  hiredate);
                                if (!TextUtils.isEmpty(hiredate)){
                                    hireDate = hiredate;
                                }
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("getPayRate", "Failure" );
                    }
                });
    }



    public void getHours()
    {
        List<Integer> minutesList = new ArrayList<>();
        List<Integer> hoursList = new ArrayList<>();
        int checkHours = 0;
        int checkMins = 0;

        for (int i=0; i<dailyHours.size(); i++)
        {
            String[] disect = dailyHours.get(i).split(":");
            hoursList.add(Integer.parseInt(disect[0]));
            minutesList.add(Integer.parseInt(disect[1]));

        }

        for (int h : hoursList)
        {
            checkHours = checkHours + h;
        }
        for (int m : minutesList)
        {
            checkMins = checkMins + m;
        }

        totalHours = checkMins / 60;
        totalHours = totalHours+checkHours;
        totalMinutes = Math.abs(checkMins) % 60;

        //Checking overtime
        if (totalHours >= 40)
        {
            overtimeHours = totalHours-40;
            totalHours = totalHours - overtimeHours;
            overtimeMins = totalMinutes;
            totalMinutes = 0;
        } else {
            overtimeMins = 0; overtimeHours = 0;
        }

        tv_hours.setText(totalHours+":"+totalMinutes);
        tv_overtimeHours.setText(overtimeHours+":"+overtimeMins);

        Log.d("getHours", "Calling setAmount ");
        setAmount();
    }


    public void setAmount()
    {
        amount = (payRate*totalHours) + ((payRate*totalMinutes)/100);
        if(overtimeHours > 0 || overtimeMins > 0)
        {
           amount = amount + ((overtimeHours*payRate)*1.5) + (((payRate*totalMinutes)*1.5)/100);
        }
        strAmount  =  new DecimalFormat("#,###.##").format(amount);
        tv_amount.setText("$"+strAmount);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            drawer.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}