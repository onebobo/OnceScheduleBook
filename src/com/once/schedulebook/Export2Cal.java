package com.once.schedulebook;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class Export2Cal extends Activity {
	private Button WeekSlcBtn, DelBtn, ExportBtn, BackBtn;
	private ArrayList<Course> mCourses;
	private int[] FirstWeekDate = new int[3];
	private boolean fwset=false;
	private int[][] timetable = new int[][] { { 8, 0 }, { 8, 55 }, { 10, 10 },
			{ 11, 5 }, { 14, 0 }, { 14, 50 }, { 15, 55 }, { 16, 45 },
			{ 18, 30 }, { 19, 20 }, { 20, 15 }, { 21, 05 } };
	private int[][] timetable2 = new int[][] { { 8, 0 }, { 8, 55 }, { 10, 10 },
			{ 11, 5 }, { 14, 30 }, { 15, 20 }, { 16, 25 }, { 17, 15 },
			{ 18, 50 }, { 19, 40 }, { 20, 35 }, { 21, 25 } };
	private Calendar FirstWeekMonday = Calendar.getInstance();
	//private static final int daymils = 86400000;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xpot2cal);
		WeekSlcBtn = (Button) findViewById(R.id.weekslc);
		DelBtn=(Button) findViewById(R.id.del);
		ExportBtn = (Button) findViewById(R.id.ex);
		BackBtn = (Button) findViewById(R.id.back);

		
		
		Intent it=this.getIntent();
		mCourses=(ArrayList<Course>) it.getSerializableExtra("css");
		Log.d("mCourses.size",mCourses.size()+"");
		
		WeekSlcBtn.setOnClickListener(weekSlc);
		ExportBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Export2Cal.this.export();
			}
		});
		DelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Export2Cal.this.delevent();
			}
		});
		BackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Export2Cal.this.finish();
			}
		});
	}

	private OnClickListener weekSlc = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			new DatePickerDialog(Export2Cal.this,
					new DatePickerDialog.OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {

							FirstWeekMonday.set(year, monthOfYear, dayOfMonth);
							int wkd = FirstWeekMonday.get(Calendar.DAY_OF_WEEK)
									- Calendar.SUNDAY;
							// long time = FirstWeekMonday.getTimeInMillis();
							// FirstWeekMonday.setTimeInMillis(time - wkd
							// * daymils);

							FirstWeekMonday.set(year, monthOfYear, dayOfMonth
									- wkd);

							FirstWeekDate[0] = FirstWeekMonday
									.get(Calendar.YEAR);
							FirstWeekDate[1] = FirstWeekMonday
									.get(Calendar.MONTH);
							FirstWeekDate[2] = FirstWeekMonday
									.get(Calendar.DAY_OF_MONTH);

							WeekSlcBtn.setText("第一周的周日是" + FirstWeekDate[0]
									+ "-" + (FirstWeekDate[1] + 1) + "-"
									+ FirstWeekDate[2]);
							fwset=true;
							// FirstWeekDate[0] = year;
							// FirstWeekDate[1] = monthOfYear;
							// FirstWeekDate[2] = dayOfMonth;
						}
					}, 2015, 2, 1).show();
		}
	};
	
	private void delevent(){
		ContentResolver cr = getContentResolver();
		cr.delete(Events.CONTENT_URI, Events.DESCRIPTION + "= ?",
				new String[] { "byOnce" });
		new AlertDialog.Builder(Export2Cal.this).setTitle("删除成功").setMessage("系统日历中没有自动添加的课程了！")
		.setPositiveButton("碉堡了", null).show();
	}

	private void export() {
		// TODO 需要增加检查设置完整、合理的语句
		if (mCourses.size()==0) {
			//Log.e("sss", "没有课程传入");
			new AlertDialog.Builder(Export2Cal.this).setTitle("导出失败").setMessage("未检测到课程信息")
			.setPositiveButton("酱紫哦", null).show();
			return;
		}
		if(!fwset){
			new AlertDialog.Builder(Export2Cal.this).setTitle("导出失败").setMessage("没有设置第一周")
			.setPositiveButton("酱紫哦", null).show();
			return;
		}
		
		Iterator<Course> it = mCourses.iterator();
		while (it.hasNext()) {
			Course cs = it.next();
			for (int week = 0; week < 25; week++) {
				if ((cs.Weeks & 1 << week) == 0)
					continue;

				for (int i = 0; i < 7; i++) {
					ClassDayInfo csd = cs.Classinfo.get(i);
					if (csd == null)
						continue;
					int rdate = week * 7 + i;
					for (int j = 0; j < 12; j++) {
						if ((csd.ClassNums & 1 << j) != 0) {
							Calendar beginTime = Calendar.getInstance();
							beginTime.set(FirstWeekDate[0], FirstWeekDate[1], FirstWeekDate[2]+ rdate);
							int month=beginTime.get(Calendar.MONTH);
							if(month<4||month>8){
								beginTime.set(Calendar.HOUR_OF_DAY, timetable[j][0]);
								beginTime.set(Calendar.MINUTE, timetable[j][1]);
							}else{
								beginTime.set(Calendar.HOUR_OF_DAY, timetable2[j][0]);
								beginTime.set(Calendar.MINUTE, timetable2[j][1]);
							}
							addEvent(Methods.classnums[j]+" "+cs.CourseName, cs.CourseAddr, beginTime);
						}
					}
				}
			}
		}
		new AlertDialog.Builder(Export2Cal.this).setTitle("导出成功").setMessage("您的课程安排已加入系统日历！")
		.setPositiveButton("碉堡了", null).show();
		// new Calendar().set
	}

	private void addEvent(String eventName, String eventLocation, Calendar beginTime) {
		long calId = 1;
		long startMillis = 0;
		long endMillis = 0;
//		Calendar beginTime = Calendar.getInstance();
//		beginTime.set(FirstWeekDate[0], FirstWeekDate[1], FirstWeekDate[2]
//				+ rdate, eventBeginTime[0], eventBeginTime[1]); 
		startMillis = beginTime.getTimeInMillis();
		// Calendar endTime = Calendar.getInstance();
		endMillis = startMillis + 2700000;

		ContentResolver cr = getContentResolver(); // 添加新event，步骤是固定的
		ContentValues values = new ContentValues();
		values.put(Events.DTSTART, startMillis);
		values.put(Events.DTEND, endMillis);
		values.put(Events.TITLE, eventName);
		values.put(Events.EVENT_LOCATION, eventLocation);
		values.put(Events.DESCRIPTION, "byOnce");
		values.put(Events.CALENDAR_ID, calId);
		values.put(Events.EVENT_TIMEZONE, "GMT+8");
		Uri uri = cr.insert(Events.CONTENT_URI, values);
		Long myEventsId = Long.parseLong(uri.getLastPathSegment()); // 获取刚才添加的event的Id

		Log.d("sucess",
				"插入成功！,id=" + myEventsId + "\n" + uri.getLastPathSegment()
						+ "\n" + uri.getAuthority());

		// new AlertDialog.Builder(this).setMessage("插入成功！" + "\n" +
		// uri.getLastPathSegment() + "\n"
		// + uri.getAuthority()).show();
	}
}
