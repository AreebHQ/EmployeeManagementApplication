package com.example.employee;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.androidfirebaseproject.LoginPageOriginal;
import com.example.androidfirebaseproject.R;
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
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import org.naishadhparmar.zcustomcalendar.CustomCalendar;
import org.naishadhparmar.zcustomcalendar.OnNavigationButtonClickedListener;
import org.naishadhparmar.zcustomcalendar.Property;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmployeeClock extends AppCompatActivity implements OnNavigationButtonClickedListener {

    // UI
    ProgressBar prog;
    TextView timer_text;
    boolean breakStarted = false;
    boolean clockInStarted = false;
    Button clockPunch1;
    Button clockInOut;
    ImageView fingerPrintAttn;
    ImageView QRcodeView;
    // Timer
    Timer timer;
    TimerTask timerTask;
    String savedTimerTime;
    String savedTimerStopTime;
    String lastPunchDate;
    boolean clockInStatus = false;
    boolean timePunchDisable = false;
    public static String timerTime;
    // Firebase
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fuser = fAuth.getCurrentUser();
    // Calendar
    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    CustomCalendar customCalendar;
    HashMap<Integer, Object> mapDateToDesc = new HashMap<>();
    HashMap<Object, Property> mapDescToProp = new HashMap<>();
    ArrayList<String> presentDays = new ArrayList<>();
    Property propPresent = new Property();
    Property propDefault = new Property();
    Property propUnavailable = new Property();
    Property propHoliday = new Property();
    String []months = {"null","jan","feb","mar","apr","may","jun","jul","aug","sep","oct","nov","dec"};

    //Navigation
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
        setContentView(R.layout.activity_employee_clock);
        prog = findViewById(R.id.progress_bar2);
        clockPunch1 = findViewById(R.id.button_clockpunches);
        clockInOut = findViewById(R.id.btn_clockInOut);
        timer_text = findViewById(R.id.timerText);
        fingerPrintAttn = findViewById(R.id.imageV_fingerPrintAttn);
        QRcodeView = findViewById(R.id.imageV_QRCodeAttn);
        customCalendar = (CustomCalendar) findViewById(R.id.custom_calendar);
        prog.setProgress(100);


        // Setting Navigation Drawer
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


        // Property present
        propPresent.layoutResource = R.layout.present_view;
        propPresent.dateTextViewResource = R.id.present_datetextview;
        mapDescToProp.put("present", propPresent);

        // Property default
        propDefault.layoutResource = R.layout.default_view;
        propDefault.dateTextViewResource = R.id.default_datetextview;
        mapDescToProp.put("default", propDefault);

        // Property unavailable
        propUnavailable.layoutResource = R.layout.unavailable_view;
        //You can leave the text view field blank. Custom calendar won't try to set a date on such views
        propUnavailable.enable = false;
        mapDescToProp.put("unavailable", propUnavailable);

        //   Property holiday
        propHoliday.layoutResource = R.layout.holiday_view;
        propHoliday.dateTextViewResource = R.id.holiday_datetextview;
        mapDescToProp.put("holiday", propHoliday);

        // Setting timer
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        timer = new Timer();

        // Setting Profile Image in NavigationDrawer
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            navUserName = extras.getString("thisName");
            navEmailAddress = extras.getString("thisEmail");
            navProfilePicUri = extras.getString("ProfilePicUri");
        }
        Picasso.get().load(navProfilePicUri).into(navProfilePic);
        navEmail.setText(navEmailAddress);
        navFirstName.setText(navUserName);

        navView.setCheckedItem(R.id.navEmpClock);
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

        //get current month's "present" days from firebase and set as present
        getDates(year,month);

        QRcodeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QRscannerAttendance.class);
                intent.putExtra("clockInStatus", clockInStatus);
                intent.putExtra("timePunchDisable", timePunchDisable);
                startActivity(intent);
            }
        });

        fingerPrintAttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), FingerprintAttendance.class);
                intent.putExtra("clockInStatus", clockInStatus);
                intent.putExtra("timePunchDisable", timePunchDisable);
                startActivity(intent);
            }
        });

        // Clock in and out button
        clockInOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timePunchDisable) {
                    if (clockInStatus) {
                        Toast.makeText(getApplicationContext(), "You Have been Clocked In! Press it for long to Clock-out", Toast.LENGTH_LONG).show();
                    } else {
                        clockInStatus = true;
                        clockInOut.setText("Stop");
                        clockInOut.setBackgroundColor(getResources().getColor(R.color.Orange));
                        clockIn();
                        Toast.makeText(getApplicationContext(), "You Are Clocked-In", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Time Punches Detected!! \n Contact Your Administrator!", Toast.LENGTH_LONG).show();
                }
            }
        });

        clockInOut.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!timePunchDisable) {

                    if (clockInStatus) {
                        clockInStatus = false;
                        clockInOut.setText("Start");
                        clockInOut.setBackgroundColor(getResources().getColor(R.color.LimeGreen));
                        clockOut();
                        Toast.makeText(getApplicationContext(), "You Are Clocked-Out", Toast.LENGTH_LONG).show();
                        return true;
                    } else {
                        Toast.makeText(getApplicationContext(), "You Are Already Clocked-Out", Toast.LENGTH_LONG).show();
                        return true;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Time Punches Detected!! \n Contact Your Administrator!", Toast.LENGTH_LONG).show();
                    return true;
                }

            }
        });

        // Break time button
        clockPunch1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clockOut();
            }
        });
    }

    public void getDates(int year, int month)
    {
        String strMon = String.valueOf(month);
        String strYear = String.valueOf(year);
        // Checking the right items
        Log.d("Year","Year is: "+ strYear);
        Log.d("Month","Month is: "+ strMon);
        // Setting month name and year as firebase document name ex: nov, jan
        // Getting month name from month array
        String monthName = months[Integer.parseInt(strMon)];
        Log.d("Month","Month is: "+ monthName);

        // Creating documents reference for user in Firabase database
        Task<QuerySnapshot> docReference = fStore.collection("Users").document(fuser.getUid())
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
                                    String timerStart = document.getString("TimerStart");
                                    String timerStop = document.getString("TimerStop");
                                    savedTimerTime = timerStart;
                                    savedTimerStopTime = timerStop;
                                    lastPunchDate = clockDate;
                                    Log.d("User Data", "Date: " + clockDate + " ClockIn: " + clockIn + " ClockOut: "+ clockOut + " Day: " + clockDay + " TimerStart: "+ timerStart);
                                    presentDays.add(clockDay);

                                } else {
                                    Log.d("User Data", "Failed to get Clock Data ", task.getException());
                                }
                            }
                            Log.d("getDates", "Finished Getting Dates");
                            // Get days to set up in calendar
                            getDays(presentDays);

                            // Check dates for current date and last clock punch date
                            if (!checkDate(lastPunchDate)) {
                                Log.d("TimerSavedTime", "Last Punch Date Is Not Same");
                                if (savedTimerStopTime == null)
                                {
                                    Log.d("SettingMissingTime", "Stop Time Missing - Setting Missing Time");
                                    setMissingPunch(lastPunchDate);
                                }
                            }
                            if (  savedTimerTime != null && checkDate(lastPunchDate)) {
                                Log.d("TimerStartTime", "Timer Start Time Is Not Null");

                                if ( savedTimerStopTime == null) {
                                    Log.d("TimerStopTime", "Timer Stop Time Is Null");
                                    clockInStatus = true;
                                    clockInOut.setText("Stop");
                                    clockInOut.setBackgroundColor(getResources().getColor(R.color.Orange));
                                    setTimer(strYear, monthName);
                                }
                                else {
                                    Log.d("TimerStopTime", "Timer Stop Time Is Not Null");
                                    timePunchDisable = true;
                                }
                            }
                        }
                    }
                });
    }


    public boolean checkDate(String strDate)
    {
        Date date2 = new Date();
        // Getting date ex: 11-22-2021
        DateFormat  dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String currentDate = dateFormat.format(date2).toString();

        if (strDate.equals(currentDate))
        { Log.d("checkDate", "Date is Same");
            return true;
        }
        Log.d("checkDate", "Date is Different");
        return false;

    }


    public void setMissingPunch(String strDate) {

        String[] date = strDate.split("-");
        String strMon = date[0];
        String strDay = date[1];
        String strYear = date[2];
        Log.d("setMissingPunch", "Getting setter data: ");
        // Checking the right items
        Log.d("Year", "Year is: " + strYear);
        Log.d("Month", "Month is: " + strMon);
        Log.d("Day", "Day is: " + strDay);
        Log.d("Date", "Date is: " + strDate);

        // Getting month name from month array
        String monthName = months[Integer.parseInt(strMon)];
        Log.d("Time", "Time is: " + monthName);

        // Creating documents reference for user in Firabase database
        DocumentReference docReference = fStore.collection("Users").document(fuser.getUid())
                .collection("Attendance").document(strYear).collection(monthName).document(strDate);

        //Storing user info in map
        Map<String, Object> punchInfo = new HashMap<>();
        punchInfo.put("ClockOut", "6:00 PM");
        punchInfo.put("TimerStop", "06:00:00 PM");

        // Saving / Merging data with the existing clock in punch
        docReference.set(punchInfo, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Successful", " Successfully Saved Missing Time");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Failure", "NOT SAVED=======================");
            }
        });
    }



    public void setTimer(String strYear, String monthName)
    {
        Date date2 = new Date();
        // Getting date ex: 11-22-2021
        DateFormat  dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String date = dateFormat.format(date2).toString();

        DocumentReference docRef = fStore.collection("Users").document(fuser.getUid())
                .collection("Attendance").document(strYear).collection(monthName).document(date);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String timerStart = document.getString("TimerStart");
                        Log.d("Timer Data", "Timer Start Time: " + timerStart );

                        // In case of missing timer time - ask user to clock in again
                        if (TextUtils.isEmpty(timerStart))
                        {
                            Toast.makeText(getApplicationContext(),"No Time Found! Please clock in again!",Toast.LENGTH_LONG).show();

                        } else {
                            // if timer has already started - time is there in database
                            savedTimerTime = timerStart;
                            startTimer(savedTimerTime);
                        }

                    } else {
                        Log.d("Timer Data", "No Start Timer Time Found");
                    }
                } else {
                    Log.d("Timer Data Date", "Failed with ", task.getException());
                }
            }
        });

    }

    private String formatTime(int seconds, int minutes, int hours) {

        return String.format("%02d",hours) + " : " + String.format("%02d",minutes);

    }


    private void startTimer(String savedTimerTime) {
        Log.d("Timer Setter", "Setting timer for: " + savedTimerTime );


        timerTask = new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Log.d("Timer Setter", "Setting timer for: " + savedTimerTime);
                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
                            Date systemDate = Calendar.getInstance().getTime();
                            String myDate = sdf.format(systemDate);


                            Date Date1 = sdf.parse(myDate);
                            Date Date2 = sdf.parse(savedTimerTime);
                            long millse = Date1.getTime() - Date2.getTime();

                            long mills = Math.abs(millse);
                            int hours = (int) (mills / (1000 * 60 * 60));
                            int minutes = (int) (mills / (1000 * 60)) % 60;
                            int seconds = (int) (mills / 1000) % 60;
                            // String timerTime = formatTime(seconds, minutes, hours);
                            timerTime = formatTime(seconds, minutes, hours);
                            timer_text.setText(timerTime);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
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
        Task<QuerySnapshot> docReference = fStore.collection("Users").document(fuser.getUid())
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
        mapDateToDesc.clear();

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        //getWeekends();
        int mon = (cal.get(Calendar.MONTH)+1);
        Log.d("Month", "Month :" + mon);
        do {
            // get the day of the week for the current day
            int day = cal.get(Calendar.DAY_OF_WEEK);
            // check if it is a Saturday or Sunday
            if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
                // print the day - but you could add them to a list or whatever
                //   System.out.println(calendar.get(Calendar.DAY_OF_MONTH));
                int d = cal.get(Calendar.DAY_OF_MONTH);
                mapDateToDesc.put(d, "holiday");

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
            Log.d("Present Days", "Employee was present on this date: " + dayInList);
            mapDateToDesc.put(dayInList, "present");
        }
        // Setting calendar properties
        customCalendar.setMapDescToProp(mapDescToProp);
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, this);
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT,  this);
        customCalendar.setDate(calendar, mapDateToDesc);
    }


    // pushing clock in time into firebase database
    public void clockIn()
    {
        // Timer Time
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
        Date systemTime = Calendar.getInstance().getTime();
        String timerStartTime = sdf.format(systemTime);

        // Clock In time
        DateFormat checkDay = new SimpleDateFormat("dd");
        DateFormat checkMonth = new SimpleDateFormat("MM");
        DateFormat checkYear = new SimpleDateFormat("yyyy");
        DateFormat checkTime = new SimpleDateFormat("K:mm a");
        Date date2 = new Date();

        // Getting Month Year and Time separately
        String strMon= checkMonth.format(date2).toString();
        String strYear = checkYear.format(date2).toString();
        String strTime = checkTime.format(date2).toString();
        String strDay = checkDay.format(date2).toString();

        // Checking the right items
        Log.d("Year","Year is: "+ strYear);
        Log.d("Month","Month is: "+ strMon);
        Log.d("Time","Time is: "+ strTime);
        Log.d("Day","Time is: "+ strDay);

        // Getting date ex: 11-22-2021
        DateFormat  dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String date = dateFormat.format(date2).toString();

        // Getting month name from month array
        String monthName = months[Integer.parseInt(strMon)];
        // Setting month name and year as firebase document name ex: nov2021, jan2021

        // Creating documents reference for user in Firabase database
        DocumentReference docReference = fStore.collection("Users").document(fuser.getUid())
                .collection("Attendance").document(strYear).collection(monthName).document(date);

        //Storing user info in map
        Map<String, Object> punchInfo = new HashMap<>();
        punchInfo.put("Date", date);
        punchInfo.put("ClockIn",strTime);
        punchInfo.put("Day",strDay);
        punchInfo.put("TimerStart",timerStartTime);

        //Start Timer
        startTimer(timerStartTime);

        docReference.set(punchInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                clockInStatus = true;
                Log.d("Successful","Saved Date: "+ date + " Time: "+ strTime);
                Log.d("Successful","Saved Time: "+ date + " Time: "+ timerStartTime);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Failure","NOT SAVED=======================");
            }
        });
    }

    // pushing clock out time in firebase database
    public void clockOut()
    {
        // Timer Time
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
        Date systemTime = Calendar.getInstance().getTime();
        String timerStopTime = sdf.format(systemTime);
        timerTask.cancel();

        DateFormat checkMonth = new SimpleDateFormat("MM");
        DateFormat checkYear = new SimpleDateFormat("yyyy");
        DateFormat checkTime = new SimpleDateFormat("K:mm a");
        Date date2 = new Date();
        // Getting Month Year and Time separately
        String strMon= checkMonth.format(date2).toString();
        String strYear = checkYear.format(date2).toString();
        String strTime = checkTime.format(date2).toString();
        // Checking the right items
        Log.d("Year","Year is: "+ strYear);
        Log.d("Month","Month is: "+ strMon);
        Log.d("Time","Time is: "+ strTime);

        // Getting date ex: 11-22-2021
        DateFormat  dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String date = dateFormat.format(date2).toString();

        // Getting month name from month array
        String monthName = months[Integer.parseInt(strMon)];
        // Setting month name and year as firebase document name ex: nov2021, jan2021
        String collcName = strYear;
        String hours = timerTime.replaceAll("\\s+", "");

        // Creating documents reference for user in Firabase database
        DocumentReference docReference = fStore.collection("Users").document(fuser.getUid())
                .collection("Attendance").document(collcName).collection(monthName).document(date);

        //Storing user info in map
        Map<String, Object> punchInfo = new HashMap<>();
        punchInfo.put("ClockOut",strTime);
        punchInfo.put("TimerStop",timerStopTime);
        punchInfo.put("Hours",hours);
        // Saving / Merging data with the existing clock in punch
        docReference.set(punchInfo, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                timePunchDisable = true;
                Log.d("Successful","Saved Date: "+ date + " Time: "+ strTime);
                Log.d("Successful","Saved Time: "+ date + " Time: "+ timerStopTime);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Failure","NOT SAVED=======================");
            }
        });
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

}