package com.example.attendencecheck;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ViewAttendanceRecord extends Activity {

    private static final int STORAGE_CODE = 1000;
    ArrayList<AttendanceBean> attendanceBeanList;
    private ListView listView ;
    private ArrayAdapter<String> listAdapter;
    Button report;

    DBAdapter dbAdapter = new DBAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recordlist);

        listView = (ListView) findViewById(R.id.recordlistview);
        final ArrayList<String> attendanceList = new ArrayList<String>();
        attendanceList.add("View All Student Attendence");

        attendanceBeanList = ((ApplicationContext) ViewAttendanceRecord.this.getApplicationContext()).getAttendanceBeanList();

        for (AttendanceBean attendanceBean : attendanceBeanList) {
            String users = "";
            if (attendanceBean.getAttendance_session_id() != 0) {
                DBAdapter dbAdapter = new DBAdapter(ViewAttendanceRecord.this);
                StudentBean studentBean = dbAdapter.getStudentById(attendanceBean.getAttendance_student_id());
                users = attendanceBean.getAttendance_student_id() + ".     " + studentBean.getStudent_firstname() + "," + studentBean.getStudent_lastname() + "                  " + attendanceBean.getAttendance_status();
            }
            else
                {
                users = attendanceBean.getAttendance_status();
            }

            attendanceList.add(users);
            Log.d("users: ", users);

        }

        listAdapter = new ArrayAdapter<String>(this, R.layout.activity_view_attendance_record, R.id.labelAttendanceAllStudent, attendanceList);
        listView.setAdapter(listAdapter);


        report = (Button) findViewById(R.id.generatereport);
        report.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions, STORAGE_CODE);
                    } else {
                        savePdf();
                    }
                } else {
                    savePdf();
                }
            }
        });


    }


    private void savePdf() {




        Document mDoc = new Document();

        String mFileName = new SimpleDateFormat("yyyy_MMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        String mFilePath = Environment.getExternalStorageDirectory() + "/" + mFileName + ".pdf";

        try {

            PdfWriter.getInstance(mDoc, new FileOutputStream(mFilePath));
            mDoc.open();
            final ArrayList<String> attendanceList = new ArrayList<String>();
            for (AttendanceBean attendanceBean : attendanceBeanList) {
                String users = "";
                if (attendanceBean.getAttendance_session_id() != 0) {
                    DBAdapter dbAdapter = new DBAdapter(ViewAttendanceRecord.this);
                    StudentBean studentBean = dbAdapter.getStudentById(attendanceBean.getAttendance_student_id());
                    users = attendanceBean.getAttendance_student_id() + ".     " + studentBean.getStudent_firstname() + "," + studentBean.getStudent_lastname() + "                  " + attendanceBean.getAttendance_status();
                    mDoc.add(new Paragraph("ID    "+attendanceBean.getAttendance_student_id()));
                    mDoc.add(new Paragraph("Name     "+studentBean.getStudent_firstname()+","+studentBean.getStudent_lastname()));
                    mDoc.add(new Paragraph("\tStatus     "+attendanceBean.getAttendance_status()));

                } else {
                    users = attendanceBean.getAttendance_status();
                }

                attendanceList.add(users);



            }

            mDoc.close();
            Toast.makeText(this, mFileName + ".pdf\nis saved to\n" + mFilePath, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    savePdf();


                } else {
                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }









    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
