package com.example.attendencecheck;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewAttendanceByFacultyActivity extends Activity {


	Button sendMail;
	ArrayList<AttendanceBean> attendanceBeanList;
	private ListView listView ;  
	private ArrayAdapter<String> listAdapter;

	DBAdapter dbAdapter = new DBAdapter(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.__listview_main);

		listView=(ListView)findViewById(R.id.listview);
		final ArrayList<String> attendanceList = new ArrayList<String>();
		attendanceList.add("Id | StudentName |  Status");

		attendanceBeanList=((ApplicationContext)ViewAttendanceByFacultyActivity.this.getApplicationContext()).getAttendanceBeanList();

		for(AttendanceBean attendanceBean : attendanceBeanList)
		{
			String users = "";
			if(attendanceBean.getAttendance_session_id() != 0)
			{
				DBAdapter dbAdapter = new DBAdapter(ViewAttendanceByFacultyActivity.this);
				StudentBean studentBean =dbAdapter.getStudentById(attendanceBean.getAttendance_student_id());
				users = attendanceBean.getAttendance_student_id()+".     "+studentBean.getStudent_firstname()+","+studentBean.getStudent_lastname()+"                  "+attendanceBean.getAttendance_status();
			}
			else
			{
				users = attendanceBean.getAttendance_status();
			}
			
			attendanceList.add(users);
			Log.d("users: ", users); 

		}

		listAdapter = new ArrayAdapter<String>(this, R.layout.view_attendance_list, R.id.labelAttendance, attendanceList);
		listView.setAdapter( listAdapter ); 

		/*listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {



				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewAttendanceByFacultyActivity.this);

				alertDialogBuilder.setTitle(getTitle()+"decision");
				alertDialogBuilder.setMessage("Are you sure?");

				alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {

						facultyList.remove(position);
						listAdapter.notifyDataSetChanged();
						listAdapter.notifyDataSetInvalidated();   

						dbAdapter.deleteFaculty(facultyBeanList.get(position).getFaculty_id());
						facultyBeanList=dbAdapter.getAllFaculty();

						for(FacultyBean facultyBean : facultyBeanList)
						{
							String users = " FirstName: " + facultyBean.getFaculty_firstname()+"\nLastname:"+facultyBean.getFaculty_lastname();
							facultyList.add(users);
							Log.d("users: ", users); 

						}
						
					}
					
				});
				alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// cancel the alert box and put a Toast to the user
						dialog.cancel();
						Toast.makeText(getApplicationContext(), "You choose cancel", 
								Toast.LENGTH_LONG).show();
					}
				});

				AlertDialog alertDialog = alertDialogBuilder.create();
				// show alert
				alertDialog.show();





				return false;
			}
		});
*/

		sendMail = (Button) findViewById(R.id.sendMail);
		sendMail.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int count=0;

				String[] TO={""};
				String[] CC = {""};
				String email="";
				attendanceBeanList = ((ApplicationContext) ViewAttendanceByFacultyActivity.this.getApplicationContext()).getAttendanceBeanList();

				for (AttendanceBean attendanceBean : attendanceBeanList) {
					if (attendanceBean.getAttendance_session_id() != 0) {
						String atr="p";
						if(attendanceBean.getAttendance_status().equalsIgnoreCase(atr)!=true){
							count=count+1;
							DBAdapter dbAdapter = new DBAdapter(ViewAttendanceByFacultyActivity.this);
							StudentBean studentBean = dbAdapter.getStudentById(attendanceBean.getAttendance_student_id());
							email=email+","+studentBean.getStudent_email();
						}
					}
				}


				TO[0]=email;
				if(TO[0]!=""){
					Intent emailIntent = new Intent(Intent.ACTION_SEND);

					emailIntent.setData(Uri.parse("mailto:"));
					emailIntent.setType("text/plain");
					emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
					emailIntent.putExtra(Intent.EXTRA_CC, CC);
					emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Absent");
					emailIntent.putExtra(Intent.EXTRA_TEXT, "You were absent today. Please be regular and continue attending as soon as possible");

					try {
						startActivity(Intent.createChooser(emailIntent, "Send mail..."));
						finish();

					} catch (android.content.ActivityNotFoundException ex) {
					}}else{
					Toast.makeText(getApplicationContext(), "None of students are absent", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}











	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
