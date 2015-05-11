package com.once.schedulebook;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class CourseAdder extends Activity {
	private Course mCourse;
	private EditText mCourseNameEdit;
	private EditText mCourseAddrEdit;
	private Button mWeekSelectButton;
	private Button mConfirmButton;
	private Button mCancleButton;
	// private Switch mRemindSwitch;
	private ListView mClassDayList;
	// ArrayList<ClassDayInfo> mClassinfo;
	private ArrayAdapter<String> mClassDayAdapter;

	// int mod;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("sss", "r0");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.course_add);
		mCourseNameEdit = (EditText) findViewById(R.id.course_name);
		mCourseAddrEdit = (EditText) findViewById(R.id.course_addr);
		mWeekSelectButton = (Button) findViewById(R.id.week_select);
		// mRemindSwitch = (Switch) findViewById(R.id.reminder);
		mClassDayList = (ListView) findViewById(R.id.classday_list);
		mConfirmButton = (Button) findViewById(R.id.confirm);
		mCancleButton = (Button) findViewById(R.id.ccl);
		// 传入数据

		// for (int i = 0; i < 7; i++) data.add(Methods.wkds[i] + "没有课，真好");
		// mod = itt.getIntExtra("modify", -1);
		// if (mod !=-1)

		// else
		// mCourse=new Course();
		Intent itt = this.getIntent();
		mCourse = (Course) itt.getSerializableExtra("cs");
		// Log.d("adder",mCourse.Classinfo.size()+"");
		if (mCourse.CourseName != null) {
			mCourseNameEdit.setText(mCourse.CourseName);
			mCourseAddrEdit.setText(mCourse.CourseAddr);
			mWeekSelectButton.setText(mCourse.getWeekStr());
		}

		mWeekSelectButton.setOnClickListener(WeekSelect);

		ClassDayData();
		mClassDayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, data);
		mClassDayList.setAdapter(mClassDayAdapter);

		mClassDayList.setOnItemClickListener(mClassDayListClick);

		mConfirmButton.setOnClickListener(Confirm);
		mCancleButton.setOnClickListener(Cancle);
	}

	// 各种adapter、listener
	int selected;
	int n;

	// 取消 resultCode=1时MainActivity不执行操作
	private OnClickListener Cancle = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			setResult(RESULT_CANCELED, null);
			finish();
		}
	};
	// 确认
	private OnClickListener Confirm = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			mCourse.CourseName = mCourseNameEdit.getText().toString();
			mCourse.CourseAddr = mCourseAddrEdit.getText().toString();
			if (mCourse.checkok()) {
				Intent itt = new Intent();
				itt.putExtra("cs", mCourse);
				//Log.d("sentclassinfosize", mCourse.Classinfo.size() + "");
				// itt.putExtra("modify",mod);
				setResult(32, itt);
				finish();
			} else {
				new AlertDialog.Builder(CourseAdder.this).setTitle("这是一个错误信息")
						.setMessage("你好像没有设置完全").setPositiveButton("酱紫哦", null)
						.show();
			}
		}
	};

	private OnItemClickListener mClassDayListClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			n = arg2;
			if (mCourse.Classinfo.get(n) == null)
				selected = 0;
			else
				selected = mCourse.Classinfo.get(n).ClassNums;
			new AlertDialog.Builder(CourseAdder.this)
					.setTitle("请选择第几节课")
					.setMultiChoiceItems(Methods.classnums,
							Methods.int2bool(selected, 12), MutiListener)
					.setPositiveButton("确定", confirm)
					.setNegativeButton("取消", null).show();
			// TODO 需要增加快速删除功能
		}
	};
	DialogInterface.OnClickListener confirm = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface arg0, int arg1) {

			if (mCourse.Classinfo.get(n) != null) {
				if (selected != mCourse.Classinfo.get(n).ClassNums)
					if (selected == 0)
						mCourse.Classinfo.set(n, null);
					else
						mCourse.Classinfo.get(n).ClassNums = selected;
			} else if (selected != 0)
				mCourse.Classinfo.set(n, new ClassDayInfo(n, selected));
			ClassDayData();
			mClassDayAdapter.notifyDataSetChanged();

			// mClassDayList.notify();
			// 有没有更好的办法？？？？
			// mClassDayAdapter=new ArrayAdapter<String>(CourseAdder.this,
			// android.R.layout.simple_expandable_list_item_1, ClassDayData());
			// mClassDayList.setAdapter(mClassDayAdapter);
		}
	};

	// 生成星期几第几节有课的列表
	List<String> data = new ArrayList<String>();

	private List<String> ClassDayData() {
		data.clear();
		for (int i = 0; i < 7; i++) {
			if (mCourse.Classinfo.get(i) != null) {
				data.add(mCourse.Classinfo.get(i).getDayStr(true));
			} else {
				data.add(Methods.wkds[i] + "没有这门课 \\^o^/");
			}
		}
		return data;
	}

	// {
	// List<String> data = new ArrayList<String>();
	// if(mCourse!=null){
	// for (int i = 0; i < 7; i++) {
	// if (mCourse.Classinfo.get(i) == null){
	// data.add(Methods.wkds[i] + "没有课，真好");
	// }
	// else{
	// data.add(mCourse.Classinfo.get(i).getDayStr(true));
	// }
	// }
	// //Log.v("0 0","data added");
	// }
	// return data;
	//
	// }

	OnMultiChoiceClickListener MutiListener = new OnMultiChoiceClickListener() {
		@Override
		public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
			selected = (selected ^ (1 << arg1));
		}

	};
	// 选择周次按钮
	private OnClickListener WeekSelect = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			int weeks = mCourse.Weeks;
			selected = weeks;

			// AlertDialog.Builder WeekSelectBuilder =
			new AlertDialog.Builder(CourseAdder.this)
					.setTitle("请选择上课周次")
					.setMultiChoiceItems(Methods.weeks(),
							Methods.int2bool(weeks, 25), MutiListener)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									mCourse.Weeks = selected;
									mWeekSelectButton.setText(mCourse
											.getWeekStr());
								}
							}).setNegativeButton("取消", null).show();
		}

	};
}
