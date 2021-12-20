package com.example.androidfirebaseproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import com.example.employee.PayData;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedingData extends AppCompatActivity {
    //Navigation
    NavigationView navView;
    DrawerLayout drawer;
    Toolbar toolbar;
    TextView navFirstName;
    TextView navEmail;
    TextView navCompany;
    CircleImageView navProfilePic;
    //NavigationImage
    String navProfilePicUri, navEmailAddress, navUserName, navUserCompany;
    //Payroll Settings
    DateFormat checkMonth = new SimpleDateFormat("MM");
    DateFormat checkYear = new SimpleDateFormat("yyyy");
    DateFormat  dateFormat = new SimpleDateFormat("MM-dd-yyyy");
    String []months = {"null","jan","feb","mar","apr","may","jun","jul","aug","sep","oct","nov","dec"};
    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    ArrayList<String> weekDays = new ArrayList<>();
    ArrayList<Float> totalEmpRegHours = new ArrayList<>();
    ArrayList<Float> totalEmpOTHours = new ArrayList<>();
    ArrayList<Double> EmpPayCheck = new ArrayList<>();
    ArrayList<String> EmpUIDList = new ArrayList<>();
    ArrayList<String> EmpPayrateList = new ArrayList<>();
    Map<String,String> empNewHours = new HashMap<>();
    float payRate;
    int week = 0;
    float totalMinutes = 0;
    int totalHours = 0;
    int overtimeHours = 0;
    float overtimeMins = 0;
    double amount = 0.0;
    String strAmount;
    String strTotalAmount;
    boolean okToFile = false;

    //Recycler
    RecyclerView recyclerView;
    PayrollAdapter payrollAdapter;
    ArrayList<String> employeeImageList = new ArrayList<>();
    ArrayList<String> employeeNamesList = new ArrayList<>();
    ArrayList<String> employeePaycheckList = new ArrayList<>();
    //Firebase
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fuser = fAuth.getCurrentUser();
    //Display
    Button btn_process, btn_continue;
    TextView tv_hours, tv_overTimeHours, tv_totalAmount, tv_totalEmployees,tv_payrollStatus, tv_lastPayrollAmt;
    ProgressBar pb_mainBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_payroll);
        tv_hours = findViewById(R.id.tv_hours);
        tv_overTimeHours = findViewById(R.id.tv_overtimeHours);
        tv_totalAmount = findViewById(R.id.tv_amount);
        btn_process = findViewById(R.id.btn_process);
        recyclerView = findViewById(R.id.recycleView_pay);
        btn_continue = findViewById(R.id.btn_continue);
        tv_hours.setVisibility(View.INVISIBLE);
        tv_overTimeHours.setVisibility(View.INVISIBLE);
        tv_totalAmount.setVisibility(View.INVISIBLE);
        tv_totalEmployees = findViewById(R.id.tv_totalEmployees);
        tv_payrollStatus = findViewById(R.id.tv_payrollStatus);
        tv_lastPayrollAmt = findViewById(R.id.tv_lastPayrollAmount);
        pb_mainBar = findViewById(R.id.pb_mainProgBar);

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
        navCompany = header.findViewById(R.id.nav_header_company_name);

        // Setting Profile Image in NavigationDrawer
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            navUserName = extras.getString("userName");
            navEmailAddress = extras.getString("userEmail");
            navProfilePicUri = extras.getString("userProfilePicUri");
           // navUserCompany = extras.getString("userCompanyName");
        }
        navUserCompany = "One Company";

        Picasso.get().load(navProfilePicUri).into(navProfilePic);
        navEmail.setText(navEmailAddress);
        navFirstName.setText(navUserName);
        navCompany.setText(navUserCompany);

        // Drawer Menu
        navView.setCheckedItem(R.id.navReports);
        navView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.navHome) {
                Intent intent = new Intent(getApplicationContext(), Admin_Homepage.class);
                intent.putExtra("userName", navUserName);
                intent.putExtra("userEmail", navEmailAddress);
                intent.putExtra("userProfilePicUri", navProfilePicUri);
                intent.putExtra("userCompanyName", navUserCompany);
                startActivity(intent);
            } else if (id == R.id.navProfile) {
                Intent intent = new Intent(getApplicationContext(), EmployerProfile.class);
                intent.putExtra("userName", navUserName);
                intent.putExtra("userEmail", navEmailAddress);
                intent.putExtra("userProfilePicUri", navProfilePicUri);
                intent.putExtra("userCompanyName", navUserCompany);
                startActivity(intent);
            } else if (id == R.id.navReports) {
                Intent intent = new Intent(getApplicationContext(), WeeklyPayroll.class);
                intent.putExtra("userName", navUserName);
                intent.putExtra("userEmail", navEmailAddress);
                intent.putExtra("userProfilePicUri", navProfilePicUri);
                intent.putExtra("userCompanyName", navUserCompany);
                startActivity(intent);
            } else if (id == R.id.navHire) {
                Intent intent = new Intent(getApplicationContext(), Hire.class);
                intent.putExtra("userName", navUserName);
                intent.putExtra("userEmail", navEmailAddress);
                intent.putExtra("userProfilePicUri", navProfilePicUri);
                intent.putExtra("userCompanyName", navUserCompany);
                startActivity(intent);
            } else if (id == R.id.navTerminate) {
                Intent intent = new Intent(getApplicationContext(), Terminate.class);
                intent.putExtra("userName", navUserName);
                intent.putExtra("userEmail", navEmailAddress);
                intent.putExtra("userProfilePicUri", navProfilePicUri);
                intent.putExtra("userCompanyName", navUserCompany);
                startActivity(intent);
            } else if (id == R.id.navEmployeeList) {
                Intent intent = new Intent(getApplicationContext(), EmployeeList.class);
                intent.putExtra("userName", navUserName);
                intent.putExtra("userEmail", navEmailAddress);
                intent.putExtra("userProfilePicUri", navProfilePicUri);
                intent.putExtra("userCompanyName", navUserCompany);
                startActivity(intent);
            } else if (id == R.id.navManage) {
                Intent intent = new Intent(getApplicationContext(), Manage.class);
                intent.putExtra("userName", navUserName);
                intent.putExtra("userEmail", navEmailAddress);
                intent.putExtra("userProfilePicUri", navProfilePicUri);
                intent.putExtra("userCompanyName", navUserCompany);
                startActivity(intent);
            } else if (id == R.id.navSetting) {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                intent.putExtra("userName", navUserName);
                intent.putExtra("userEmail", navEmailAddress);
                intent.putExtra("userProfilePicUri", navProfilePicUri);
                intent.putExtra("userCompanyName", navUserCompany);
                startActivity(intent);
            } else if (id == R.id.navSignOut) {
                // Signing user out from app
                FirebaseAuth.getInstance().signOut();
                // Calling login activity
                startActivity(new Intent(getApplicationContext(), LoginPageOriginal.class));
            }
            DrawerLayout drawer = findViewById(R.id.drawerLayoutMainMenu);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });


        fStore.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String employeeStatus = document.getString("isAdmin");
                                if (!TextUtils.isEmpty(employeeStatus) && !employeeStatus.equals("terminated") && !employeeStatus.equals("admin")) {

                                    String compName = document.getString("CompanyName");
                                    String compCode = document.getString("CompanyCode");
                                    if (TextUtils.isEmpty(compName) || TextUtils.isEmpty(compCode)) {
                                        continue;
                                    }

                                    Log.d("CompanyName ======= ", compName);
                                    Log.d("CompanyName ======= ", compCode);
                                    if (compName.equals(navUserCompany) && compCode.equals(fAuth.getCurrentUser().getUid())) {

                                        String name = document.getString("Firstname") + " " + document.getString("Lastname");
                                        String imageUrl = document.getString("ProfileURL");
                                        String empUID = document.getString("Uid");
                                        String empPayrate = document.getString("PayRate");


                                        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(empUID) || TextUtils.isEmpty(empPayrate))
                                        {
                                            Log.d("Error", "Employee Data Not Found");
                                            continue;
                                        }

                                        Log.d("TAG", document.getId() + " => " + document.getData());
                                        Log.d("TAG", name);
                                        Log.d("TAG", document.getString("Uid"));
                                        Log.d("TAG", document.getString("ProfileURL"));
                                        Log.d("TAG", empPayrate);
                                        EmpUIDList.add(empUID);
                                        employeeNamesList.add(name);
                                        EmpPayrateList.add(empPayrate);
                                        empNewHours.put(empUID, "00:00");
                                        Log.d("Added ======= ", "Employee UID and Employee Names");

                                        if (TextUtils.isEmpty(imageUrl)) {
                                            employeeImageList.add("imageUrl");
                                            Log.d("PROFILE IMAGE", "Profile Image Not Found");
                                        } else {
                                            employeeImageList.add(imageUrl);
                                        }
                                    }
                                }
                            }
                            for(int i=0; i<EmpUIDList.size(); i++) {
                                try {
                                    getCurrentWeekHours(EmpUIDList.get(i), EmpPayrateList.get(i));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });

        // Setting display data after 1 second
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                getMapHours( EmpPayrateList,  empNewHours);
                Log.d("Setting hours", "Setting display hours");
                tv_hours.setVisibility(View.VISIBLE);
                tv_overTimeHours.setVisibility(View.VISIBLE);
                tv_totalAmount.setVisibility(View.VISIBLE);
                tv_totalEmployees.setText(""+employeeNamesList.size());
                // Calling employee list adapter
                payrollAdapter = new PayrollAdapter(FeedingData.this,employeeNamesList, employeeImageList,employeePaycheckList);
                recyclerView.setAdapter(payrollAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(FeedingData.this));
                pb_mainBar.setVisibility(View.INVISIBLE);

            }
        }, 1000);

        // setting payroll status
        checkPayrollStatus();

        btn_process.setOnClickListener(v->{



            AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(v.getContext());

            // Dialogbox visuals
            confirmationDialog.setTitle("Confirm Payroll");
            confirmationDialog.setMessage("Confirm file payroll?");

            confirmationDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {


                    okToFile = true;
                    getMapHours( EmpPayrateList,  empNewHours);
                    Log.d("FilePayroll", "Calling FilePayroll");
                }
            });

            confirmationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    return;
                }
            });
            confirmationDialog.create().show();

        });


        btn_continue.setOnClickListener(v->{

           for (int i=0; i<EmpUIDList.size(); i++)
           {

               Log.d("EmpUIDList", "Calling : " + EmpUIDList.get(i));
               Log.d("employeeNamesList", "Calling : " + employeeNamesList.get(i));
               Log.d("employeePaycheckList", "Calling : " + employeePaycheckList.get(i));
               Log.d("employeePaycheckList", "Calling : " + EmpPayCheck.get(i));
               Log.d("employeeImageList", "Calling : " + employeeImageList.get(i));

           }

        });

    }


    private void getCurrentWeekHours(String uid, String payrate) throws ParseException {

        // Date refDate = new Date();
        Date refDate = dateFormat.parse("12-10-2021");
        int firstDayOfWeek = Calendar.getInstance().getFirstDayOfWeek();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(refDate);
        calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
        Date[] daysOfWeek = new Date[7];
        weekDays.clear();
        for (int i = 0; i < 7; i++) {

            Date systemTime = calendar.getTime();
            String currentDate = dateFormat.format(systemTime);
            Log.d("Date Setter", "Setting date for: " + currentDate );
            weekDays.add(currentDate);
            daysOfWeek[i] = calendar.getTime();
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        }

        // Setting last day of week as pay date
        String payDate =  dateFormat.format(daysOfWeek[6]);
        Log.d("Pay Date", "Pay Date: " + payDate );
        //tv_payDate.setText(payDate);

        // Getting Month Year and Time separately
        String strMon= checkMonth.format(refDate);
        String strYear = checkYear.format(refDate);
        Log.d("Month", "Pay Month: " + strMon );
        Log.d("Year", "Pay Year: " + strYear );
        // Getting month name from month array
        String monthName = months[Integer.parseInt(strMon)];


        //Getting weekly data for each employee
        for (int i = 0; i < weekDays.size(); i++) {

            // Creating documents reference for user in Firebase database

            int finalI = i;
            Task<DocumentSnapshot> docReference = fStore.collection("Users").document(uid)
                    .collection("Attendance").document(strYear).collection(monthName).document(weekDays.get(i))
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String hours = document.getString("Hours");

                                    if (TextUtils.isEmpty(hours)) {
                                        hours = "00:00";
                                    }
                                    //===============================================
                                    if (empNewHours.containsKey(uid))
                                    {
                                        Log.d("MEployeee UID: ",  " "+uid);
                                        String savedHours = empNewHours.get(uid);
                                        Log.d("SavedHours: ",  " "+savedHours);
                                        String[] SHours = savedHours.split(":");
                                        int savedH = (Integer.parseInt(SHours[0]));
                                        int savedM = (Integer.parseInt(SHours[1]));


                                        String[] disect = hours.split(":");
                                        int H = (Integer.parseInt(disect[0]));
                                        int M = (Integer.parseInt(disect[1]));

                                        String Hr = ""+(savedH+H)+":"+(savedM+M);
                                        Log.d("New Hours: ",  " "+ Hr);
                                        empNewHours.put(uid,Hr);
                                        Log.d("FianlI: ",  " "+ finalI);

                                    }

                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Data not found", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }




    public void getMapHours( ArrayList<String> payrateList, Map<String,String> empList)
    {
        List<Integer> minutesList = new ArrayList<>();
        List<Integer> hoursList = new ArrayList<>();
        String uid, payrate;
        int index = 0;
        EmpPayCheck.clear();
        for (Map.Entry entry : empList.entrySet())
        {   int checkHours = 0;
            int checkMins = 0;
            totalHours = 0;
            overtimeHours = 0;
            totalMinutes = 0;
            overtimeMins = 0;
            payrate = payrateList.get(index);

            index++;
            Log.d("Printing Map ",  "Employee UId: " + entry.getKey() + " Employee Hours: " + entry.getValue());
            uid =  entry.getKey().toString();
            String[] disect = entry.getValue().toString().split(":");
            hoursList.add(Integer.parseInt(disect[0]));
            minutesList.add(Integer.parseInt(disect[1]));

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

            if (totalHours >= 40)
            {
                overtimeHours = totalHours-40;
                totalHours = totalHours - overtimeHours;
                overtimeMins = totalMinutes;
                totalMinutes = 0;
            } else {
                overtimeMins = 0; overtimeHours = 0;
            }


            if (totalMinutes == 0)
            { tv_hours.setText(totalHours+":00");
            } else {
                tv_hours.setText(totalHours+":"+totalMinutes);
            }

            if (overtimeMins == 0)
            { tv_overTimeHours.setText(overtimeHours+":00");
            } else {
                tv_overTimeHours.setText(overtimeHours+":"+overtimeMins);
            }

            totalEmpRegHours.add((float)totalHours + (totalMinutes/60));
            totalEmpOTHours.add((float)overtimeHours + (overtimeMins/60));

            Log.d("totalEmpRegHours",  "Added to Employee Reg Hours: "+(float)totalHours + (totalMinutes/60));
            Log.d("totalEmpOTHours",  "Added to Employee OT Hours: "+(float)overtimeHours + (overtimeMins/60));



            Log.d("totalHours: ",  " "+totalHours);
            Log.d("totalMinutes: ",  " "+totalMinutes);
            Log.d("overtimeHours: ",  " "+overtimeHours);
            Log.d("overtimeMins: ", " "+ overtimeMins);

            hoursList.clear();
            minutesList.clear();

            Log.d("getHours", "Calling setAmount ");

            setAmount(uid,payrate);
        }
    }



    public void setDisplayAmount(String uid,String payrate)
    {
        amount = 0.0;
        payRate = Float.parseFloat(payrate);
        Log.d("Payrate: ",  ""+payRate);
        amount = (payRate*totalHours) + ((payRate*totalMinutes)/100);
        if(overtimeHours > 0 || overtimeMins > 0)
        {
            amount = amount + ((overtimeHours*payRate)*1.5) + (((payRate*totalMinutes)*1.5)/100);
        }
        strAmount  =  new DecimalFormat("#,###.##").format(amount);
        employeePaycheckList.add("$"+strAmount);
        EmpPayCheck.add(amount);
        double amnt = 0;
        for (Double d : EmpPayCheck)
        {
            amnt = amnt+d;
        }
        strTotalAmount  =  new DecimalFormat("#,###.##").format(amnt);
        Log.d("setAmount: ",  strAmount);
        tv_totalAmount.setText("$"+strTotalAmount);

    }

    public void setAmount(String uid,String payrate)
    {
        amount = 0.0;
        payRate = Float.parseFloat(payrate);
        Log.d("Payrate: ",  ""+payRate);
        amount = (payRate*totalHours) + ((payRate*totalMinutes)/100);
        if(overtimeHours > 0 || overtimeMins > 0)
        {
            amount = amount + ((overtimeHours*payRate)*1.5) + (((payRate*totalMinutes)*1.5)/100);
        }
        strAmount  =  new DecimalFormat("#,###.##").format(amount);
        employeePaycheckList.add("$"+strAmount);
        EmpPayCheck.add(amount);
        Log.d("stAmount: ",  ""+uid);
        Log.d("stAmount: ",  ""+strAmount);
        Log.d("DeciAmount: ",  ""+amount);

        if (okToFile)
        {
            setWeeklyPaycheck(uid,strAmount);
        }
        double amnt = 0;
        for (Double d : EmpPayCheck)
        {
            amnt = amnt+d;
        }
        strTotalAmount  =  new DecimalFormat("#,###.##").format(amnt);
        Log.d("setAmount: ",  strAmount);
        tv_totalAmount.setText("$"+strTotalAmount);
    }



    public void checkPayrollStatus()
    {
        Date currentDate = new Date();
        Calendar cal =  Calendar.getInstance(TimeZone.getDefault());


        String date = dateFormat.format(currentDate);
        int currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
        String strWeek = "Week " + currentWeek;
        String strYear = checkYear.format(currentDate);
        Task<DocumentSnapshot> docReference =  fStore.collection("Users").document(fAuth.getCurrentUser().getUid())
                .collection("Payroll").document(strYear).collection("Week").document(strWeek)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                tv_payrollStatus.setText("Processed");
                                tv_payrollStatus.setTextColor(Color.GREEN);
                                tv_lastPayrollAmt.setText("$"+document.getString("Amount"));

                            } else {
                                tv_payrollStatus.setText("Pending");
                                tv_lastPayrollAmt.setText("$0.00");
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Data not found", Toast.LENGTH_LONG).show();
                    }
                });
    }





    public void setWeeklyPaycheck(String uid, String strAmount)
    {
        // Getting date ex: 11-22-2021
        Date currentDate = new Date();
        Calendar cal =  Calendar.getInstance(TimeZone.getDefault());


        String date = dateFormat.format(currentDate);
        int currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
        String strWeek = "Week " + currentWeek;
        String strYear = checkYear.format(currentDate);


        // Creating documents reference for user in Firabase database
        DocumentReference docReference = fStore.collection("Users").document(uid)
                .collection("Payroll").document(strYear).collection("Week").document(strWeek);
        //Storing user info in map
        Map<String, Object> payrollInfo = new HashMap<>();
        payrollInfo.put("Date", date);
        payrollInfo.put("Amount",strAmount);
        payrollInfo.put("Week",strWeek);
        docReference.set(payrollInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(@NonNull Void unused) {
                Log.d("Successful","Saved "+ date + " Week: "+ strWeek);
                Log.d("Successful","Saved "+ date + " Amount: "+ strAmount);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Failure","PAYROLL NOT SAVED");
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


/*
    public  void feedingData()
    {
        String uid = "WVPLCVV6dfTLraHW1GrjVSl09TC2";

        for (int i=10; i<15; i++) {
            String newDate = "12-" + i + "-2021";

            Log.d("Year","Year is: "+ newDate);
            // Creating documents reference for user in Firabase database
            DocumentReference docReference = fStore.collection("Users").document(uid)
                    .collection("Attendance").document("2021").collection("dec").document(newDate);

            //Storing user info in map
            Map<String, Object> punchInfo = new HashMap<>();
            punchInfo.put("Date", newDate);
            punchInfo.put("ClockIn","08:00 AM");
            punchInfo.put("ClockOut","05:00 PM");
            punchInfo.put("Day", String.valueOf(i));
            punchInfo.put("Hours", "08:00");
            punchInfo.put("TimerStart","08:00:00 AM");
            punchInfo.put("TimerStop","05:00:00 PM");


            docReference.set(punchInfo);
        }

    }
    */

}

