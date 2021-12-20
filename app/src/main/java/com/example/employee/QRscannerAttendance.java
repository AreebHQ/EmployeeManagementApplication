package com.example.employee;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.androidfirebaseproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.common.InputImage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRscannerAttendance extends AppCompatActivity{
    private static final int CAMERA_PERMISSION_CODE = 223;
    private static final int READ_STORAGE_PERMISSION_CODE = 144;
    private static final int WRITE_STORAGE_PERMISSION_CODE = 144;
    BarcodeScanner scanner;
    ActivityResultLauncher<Intent> cameraLauncher;
    ActivityResultLauncher<Intent> galleryLauncher;
    Button btn_scan;
    InputImage inputImage;

    public  static TextView tv_ssid;


    String []months = {"null","jan","feb","mar","apr","may","jun","jul","aug","sep","oct","nov","dec"};
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fuser = fAuth.getCurrentUser();
    boolean isClockIn = false;

    // Timer Time
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
    // Clock In time
    DateFormat checkDay = new SimpleDateFormat("dd");
    DateFormat checkMonth = new SimpleDateFormat("MM");
    DateFormat checkYear = new SimpleDateFormat("yyyy");
    DateFormat checkTime = new SimpleDateFormat("K:mm a");
    Date date2 = new Date();
    // Getting Month Year and Time separately
    String strMon;
    String strYear;
    String strTime;
    String strDay;
    TextView attnStatus;
    String monthName;
    String companyCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner_attendance);
        btn_scan = findViewById(R.id.btnScan);
        tv_ssid = findViewById(R.id.tvSSID);
        attnStatus = findViewById(R.id.attendanceStatus);

        Intent intentReceiver = getIntent();
        isClockIn = intentReceiver.getBooleanExtra("clockInStatus",false);

        // Getting Month Year and Time separately
        strMon = checkMonth.format(date2);
        strYear = checkYear.format(date2);
        strTime = checkTime.format(date2);
        strDay = checkDay.format(date2);
        monthName = months[Integer.parseInt(strMon)];
        getCompanyCode();

        if(isClockIn)
        {
            attnStatus.setText("Clocked In");
            attnStatus.setTextColor(Color.GREEN);
            Toast.makeText(QRscannerAttendance.this, "You Are Already Clocked-In", Toast.LENGTH_LONG).show();
        } else {
            Log.d("Timer Data", "No Start Time Found");
            isClockIn = false;
            attnStatus.setText("Clocked Out");
            attnStatus.setTextColor(Color.RED);
        }


       /*

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                // handle pictures from gallery
                Intent data = result.getData();
                try{
                    inputImage = InputImage.fromFilePath(QRscannerAttendance.this,data.getData());
                    processQRImage();
                } catch (Exception e){
                    Log.d("Image Error", "onActivityResult: " + e.getMessage());
                }
            }
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                // handle pictures from camera
                Intent data = result.getData();
                try{
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    inputImage = InputImage.fromBitmap(photo, 0);
                    inputImage = InputImage.fromFilePath(QRscannerAttendance.this,data.getData());
                    processQRImage();

                } catch (Exception e){
                    Log.d("Image Error", "onActivityResult: " + e.getMessage());
                }
            }
        });
*/

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isClockIn) {
                    Toast.makeText(QRscannerAttendance.this, "Error! Already Clocked In", Toast.LENGTH_LONG).show();
                } else {

                    Intent intent = new Intent(getApplicationContext(),scannerView.class);
                    intent.putExtra("companyCode", companyCode);
                    startActivity(intent);
                }
            }
        });
    }

    // Getting employees company code to validate wiht the QR code result
    private void getCompanyCode() {

        Task<DocumentSnapshot> docRef = fStore.collection("Users").document(fuser.getUid()).get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String compCode = document.getString("CompanyCode");
                        Log.d("CompanyCode Found: ", compCode);
                        if (!TextUtils.isEmpty(compCode)) {
                            companyCode = compCode;
                        }
                    }
                } else {
                    Log.d("CompanyCode", "Company Code Failed with ", task.getException());
                }
            }
        });
    }

}