package com.example.androidfirebaseproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.naishadhparmar.zcustomcalendar.CustomCalendar;
import org.naishadhparmar.zcustomcalendar.OnDateSelectedListener;
import org.naishadhparmar.zcustomcalendar.OnNavigationButtonClickedListener;
import org.naishadhparmar.zcustomcalendar.Property;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;


public class EmployerAttendance extends AppCompatActivity implements OnNavigationButtonClickedListener {

    // Firebase
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fuser = fAuth.getCurrentUser();
    // Attendance
    String[] months = {"null", "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};
    Date date2 = new Date();
    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    ArrayList<String> presentDays = new ArrayList<>();
    HashMap<Integer, Object> mapDateToDesc = new HashMap<>();
    HashMap<Object, Property> mapDescToProp = new HashMap<>();
    // Calendar settings
    Property propPresent = new Property();
    Property propDefault = new Property();
    Property propUnavailable = new Property();
    Property propHoliday = new Property();
    CustomCalendar customCalendar;
    //UI
    CircleImageView userProfilePic;
    TextView userName, userEmail,navCompany;
    TextView attendancePercentage, userProg, tv_presetnDays, tv_absentDays;
    String employeeUid;
    // Navigation Drawer
    NavigationView navView;
    DrawerLayout drawer;
    Toolbar toolbar;
    TextView navFirstName;
    TextView navEmail;
    CircleImageView navProfilePic;
    //NavigationImage
    String navProfilePicUri, navEmailAddress, navUserName, navUserCompany;
    String employeeProfilePic, employeeEmail, employeeName;

    private static final DecimalFormat dfZero = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_attendance);
        customCalendar = (CustomCalendar) findViewById(R.id.custom_calendar);
        attendancePercentage = findViewById(R.id.attendancePercent);
        userProg = findViewById(R.id.userProgress);
        tv_presetnDays = findViewById(R.id.TV_presentDays);
        tv_absentDays = findViewById(R.id.TV_absentDays);
        // Navigation Drawer
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


        userProfilePic = findViewById(R.id.imageViewProfilePic);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        // Calendar Properties
        //Present
        propPresent.layoutResource = R.layout.present_view;
        propPresent.dateTextViewResource = R.id.present_datetextview;
        mapDescToProp.put("present", propPresent);
        //Default
        propDefault.layoutResource = R.layout.default_view;
        propDefault.dateTextViewResource = R.id.default_datetextview;
        mapDescToProp.put("default", propDefault);

        propUnavailable.layoutResource = R.layout.unavailable_view;
        //You can leave the text view field blank. Custom calendar won't try to set a date on such views
        propUnavailable.enable = false;
        mapDescToProp.put("unavailable", propUnavailable);
        propHoliday.layoutResource = R.layout.holiday_view;
        propHoliday.dateTextViewResource = R.id.holiday_datetextview;
        mapDescToProp.put("holiday", propHoliday);

        Intent intentReceiver = getIntent();
        employeeUid = intentReceiver.getStringExtra("Uid");
        if (!TextUtils.isEmpty(employeeUid))
        {   Log.d("EmployeeUID", "Employee UID is " + employeeUid);
        }

        // Setting Profile Image in NavigationDrawer
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            navUserName = extras.getString("userName");
            navEmailAddress = extras.getString("userEmail");
            navProfilePicUri = extras.getString("userProfilePicUri");
            navUserCompany = extras.getString("userCompanyName");
        }
        Picasso.get().load(navProfilePicUri).into(navProfilePic);
        navEmail.setText(navEmailAddress);
        navFirstName.setText(navUserName);
        navCompany.setText(navUserCompany);

        // Setting user data
        setEmployeeData();

        // Drawer Menu
        navView.setCheckedItem(R.id.navHome);
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
                finish();
            }
            DrawerLayout drawer = findViewById(R.id.drawerLayoutMainMenu);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
        // Getting year and month
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        //get current present days from firebase and set as present
        getDates(year, month);

        customCalendar.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(View view, Calendar selectedDate, Object desc) {
                Snackbar.make(customCalendar, selectedDate.get(Calendar.DAY_OF_MONTH) + " selected", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getDates(int year, int month)
    {
        String strMon = String.valueOf(month);
        String strYear = String.valueOf(year);
        // Checking the right items
        Log.d("Year","Year is: "+ strYear);
        Log.d("Month","Month is: "+ strMon);

        // Getting month name from month array
        String monthName = months[Integer.parseInt(strMon)];
        Log.d("Time","Time is: "+ monthName);
        // Setting month name and year as firebase document name ex: nov, jan

        // Creating documents reference for user in Firabase database
        Task<QuerySnapshot> docReference = fStore.collection("Users").document(employeeUid)
                .collection("Attendance").document(strYear).collection(monthName).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                if (document.exists()) {
                                    String clockIn = document.getString("ClockIn");
                                    String clockOut = document.getString("ClockOut");
                                    String clockDate = document.getString("Date");
                                    String clockDay = document.getString("Day");
                                    Log.d("getDates", "Date: " + clockDate + " ClockIn: " + clockIn + " ClockOut: "+ clockOut + " Day: " + clockDay);
                                    presentDays.add(clockDay);
                                } else {
                                    Log.d("getDates", "Failed to get Clock Data ", task.getException());
                                }
                            }
                            // Getting user's present days
                            getDays(presentDays);
                        }
                    }
                });
    }

    // Setting employee 's data
    void setEmployeeData()
    {
        DocumentReference docRef = fStore.collection("Users").document(employeeUid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        employeeName =  document.getString("Firstname") + " " + document.getString("Lastname");
                        employeeEmail = document.getString("Email");
                        employeeProfilePic = document.getString("ProfileURL");
                        // Set data
                        Picasso.get().load(employeeProfilePic).into(userProfilePic);
                        userEmail.setText(employeeEmail);
                        userName.setText(employeeName);
                    }
                } else {
                    Log.d("setEmployeeData", "Retrieving data failed with ", task.getException());
                }
            }
        });
    }




    public void getNewDates(int year, int month)
    {
        String strMon = String.valueOf(month);
        String strYear = String.valueOf(year);
        // Checking the right items
        Log.d("Year","Year is: "+ strYear);
        Log.d("Month","Month is: "+ strMon);

        // Getting month name from month array
        String monthName = months[Integer.parseInt(strMon)];
        Log.d("Time","Time is: "+ monthName);
        // Setting month name and year as firebase document name ex: nov, jan

        // Creating documents reference for user in Firabase database
        Task<QuerySnapshot> docReference = fStore.collection("Users").document(employeeUid)
                .collection("Attendance").document(strYear).collection(monthName).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                if (document.exists()) {
                                    String clockIn = document.getString("ClockIn");
                                    String clockOut = document.getString("ClockOut");
                                    String clockDate = document.getString("Date");
                                    String clockDay = document.getString("Day");
                                    Log.d("User Data", "Date: " + clockDate + " ClockIn: " + clockIn + " ClockOut: "+ clockOut + " Day: " + clockDay);
                                    presentDays.add(clockDay);
                                } else {
                                    Log.d("User Data", "Failed to get Clock Data ", task.getException());
                                }
                            }
                            Log.d("CHCEKING", "CHECKIGN DONE DONER DONE DONE ============================ ");
                            getDays(presentDays);
                        }
                    }
                });
    }

    public void getDays(ArrayList<String> days)
    {
        int weekendDays = 0;
        Map<Integer, String> allDays = new HashMap<>();
        mapDateToDesc.clear();
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        int mon = (cal.get(Calendar.MONTH)+1);
        Log.d("Month", "Month :" + mon);
        do {
            // get the day of the week for the current day
            int day = cal.get(Calendar.DAY_OF_WEEK);
            // check if it is a Saturday or Sunday
            if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
                // print the day - but you could add them to a list or whatever
                int d = cal.get(Calendar.DAY_OF_MONTH);
                allDays.put(d, "holiday");
                Log.d("Weekend Days", "Days :" + d);
            }
            // advance to the next day
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }  while ((cal.get(Calendar.MONTH)+1) == mon);
        // stop when we reach the start of the next month

        Log.d("Days Method", "Getting Days Array in method ");
        for (int i=0; i<days.size();i++)
        {
            Log.d("Days Array", "Days in array:" + i + " " + days.get(i));
            int dayInList = Integer.parseInt(days.get(i));
            allDays.put(dayInList, "present");
            Log.d("Present Days", "Employee was present on this date: " + dayInList);
        }
        for (Map.Entry<Integer, String> i : allDays.entrySet())
        {
            mapDateToDesc.put(i.getKey(), i.getValue());
            if (i.getValue() == "holiday")
            {
                weekendDays++;
            }
        }

        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        Log.d("Days In Month", "Total Days :" + daysInMonth);
        // Setting calendar data
        setPercentage(days, daysInMonth, weekendDays);
        customCalendar.setMapDescToProp(mapDescToProp);
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, this);
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT,  this);
        customCalendar.setDate(calendar, mapDateToDesc);
    }

    public void setPercentage(ArrayList<String> daysPresent, int daysInMonth, int weekendDays)
    {

        int workingDays  = daysInMonth - weekendDays;
        Log.d("Working Days", "Wokrkignggggg  is : " + weekendDays );
        Log.d("Working Days", "Wokrkignggggg  is : " + workingDays );
        Log.d("Month Days", "Monthtt  is : " + daysInMonth );
        int present = presentDays.size();
        int absent = (daysInMonth-weekendDays)-present;
        Log.d("Present Days", "Presentttttttttttttttt  is : " + present );
        Log.d("Absent Days", "Absenttttttttttttttt is : " + absent );

        float a = presentDays.size();
        Log.d("Array SIze", "Array size is : " + a );
        tv_presetnDays.setText(String.valueOf(present));
        tv_absentDays.setText(String.valueOf(absent));

        // Setting attendance display percentage
        float per = (float)((a/workingDays)*100);
        String percentage = String.valueOf(dfZero.format(per));
        Log.d("Array SIze", "Array size is : " + percentage );
        attendancePercentage.setText(percentage+"%");

        if (per < 75)
        {
            attendancePercentage.setTextColor(Color.parseColor("#e4815c"));
            userProg.setText("Average");
            userProg.setTextColor(Color.parseColor("#e4815c"));
        }
        if (per < 25)
        {
            attendancePercentage.setTextColor(Color.RED);
            userProg.setText("Poor");
            userProg.setTextColor(Color.RED);
        } else {
            attendancePercentage.setTextColor(Color.parseColor("#50C878"));
            userProg.setText("Good");
            userProg.setTextColor(Color.parseColor("#50C878"));
        }
    }

    @Override
    public Map<Integer, Object>[] onNavigationButtonClicked(int whichButton, Calendar newMonth) {
        Map<Integer, Object>[] arr = new Map[2];

        //Getting current date instances
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int currentDate = calendar.get(Calendar.DAY_OF_MONTH);

        String strMon = String.valueOf(month);
        String strYear = String.valueOf(year);

        // Checking the right items
        Log.d("Year Next","Year is: "+ strYear);
        Log.d("Month Next","Month is: "+ strMon);

        switch(newMonth.get(Calendar.MONTH)) {
            case Calendar.JANUARY:
                month = 1;
                presentDays.clear();
                calendar.set(year,Calendar.JANUARY,currentDate);
                getNewDates(year, month);
                break;
            case Calendar.FEBRUARY:
                month = 2;
                presentDays.clear();
                calendar.set(year,Calendar.FEBRUARY,currentDate);
                getNewDates(year, month);
                break;
            case Calendar.MARCH:
                month = 3;
                presentDays.clear();
                calendar.set(year,Calendar.MARCH,currentDate);
                getNewDates(year, month);
                break;
            case Calendar.APRIL:
                month = 4;
                presentDays.clear();
                calendar.set(year,Calendar.APRIL,currentDate);
                getNewDates(year, month);
                break;
            case Calendar.MAY:
                month = 5;
                calendar.set(year,Calendar.MAY,currentDate);
                getNewDates(year, month);
                break;
            case Calendar.JUNE:
                month = 6;
                presentDays.clear();
                calendar.set(year,Calendar.JUNE,currentDate);
                getNewDates(year, month);
                break;
            case Calendar.JULY:
                month = 7;
                presentDays.clear();
                calendar.set(year,Calendar.JULY,currentDate);
                getNewDates(year, month);
                break;
            case Calendar.AUGUST:
                month = 8;
                presentDays.clear();
                calendar.set(year,Calendar.AUGUST,currentDate);
                getNewDates(year, month);
                break;
            case Calendar.SEPTEMBER:
                month = 9;
                presentDays.clear();
                calendar.set(year,Calendar.SEPTEMBER,currentDate);
                getNewDates(year, month);
                break;
            case Calendar.OCTOBER:
                presentDays.clear();
                calendar.set(year,Calendar.OCTOBER,currentDate);
                month = 10;
                getNewDates(year, month);
                break;
            case Calendar.NOVEMBER:
                month = 11;
                presentDays.clear();
                calendar.set(year,Calendar.NOVEMBER,currentDate);
                getNewDates(year, month);
                break;
            case Calendar.DECEMBER:
                month = 12;
                presentDays.clear();
                calendar.set(year,Calendar.DECEMBER,currentDate);
                getNewDates(year, month);
                break;
        }
        return arr;
    }

 /* For Reference
  arr[0] = new HashMap<>(); //This is the map linking a date to its description
                arr[0].put(3, "unavailable");
                arr[0].put(6, "holiday");
                arr[0].put(21, "unavailable");
                arr[0].put(24, "holiday");
                arr[1] = null; //Optional: This is the map linking a date to its tag. */
    //break;

}