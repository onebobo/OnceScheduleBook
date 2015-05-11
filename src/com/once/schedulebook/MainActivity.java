package com.once.schedulebook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	private Button mAddBtn;
	private Button mExBtn;
	private ListView mCourseList;
	private ArrayList<Course> mCourses;
	private SimpleAdapter mListAdapter;
	public static final String CS_KEY = "MCS";
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mAddBtn = (Button) findViewById(R.id.add_course);
		mExBtn = (Button) findViewById(R.id.ex);
		mCourseList = (ListView) findViewById(R.id.course_list);

		mCourses = (ArrayList<Course>) Methods.readObject(this, CS_KEY);
		if (mCourses == null) {
			mCourses = new ArrayList<Course>();
			new AlertDialog.Builder(this)
					.setTitle("欢迎使用一次性课表")
					.setMessage("这个程序员很累，已经写不动欢迎界面了。。\n总之任何建议请联系onebobo@hust.edu.cn")
					.setPositiveButton("碉堡了", null).show();
		}

		getData();
		mListAdapter = new SimpleAdapter(this, list,
				android.R.layout.simple_list_item_2, new String[] { "title",
						"description" }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		mCourseList.setAdapter(mListAdapter);
		mCourseList.setOnItemClickListener(mClickListener);

		mAddBtn.setOnClickListener(addListener);
		mExBtn.setOnClickListener(exListener);
		// Log.i("sss", "onCreate");
	}

	protected void onPause() {
		super.onPause();
		Methods.saveObject(this, CS_KEY, mCourses);
	}

	private OnClickListener exListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent itt = new Intent();
			itt.putExtra("css", mCourses);
			itt.setClass(MainActivity.this, Export2Cal.class);
			startActivity(itt);
		}
	};

	private OnClickListener addListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			Intent itt = new Intent();
			itt.putExtra("cs", new Course());
			itt.setClass(MainActivity.this, CourseAdder.class);
			startActivityForResult(itt, 1024);
		}
	};

	// requestCode>=0表示要修改第几项课程,1024表示新增课程
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("rqcode", requestCode + "");
		Log.d("rzcode", resultCode + "");
		if (resultCode == 32) {
			Course getted = (Course) data.getSerializableExtra("cs");
			Log.d("adder", getted.Classinfo.size() + "");
			if (requestCode == 1024)
				mCourses.add(getted);
			else
				mCourses.set(requestCode, getted);
			getData();
			mListAdapter.notifyDataSetChanged();
		}
	}

	int n;

	private OnItemClickListener mClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			n = arg2;
			if (mCourses.size() != 0) {
				new AlertDialog.Builder(MainActivity.this)
						.setTitle(mCourses.get(n).CourseName)
						.setMessage("天哪你要对我做什么？")
						.setPositiveButton("修改",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										// TODO Auto-generated method stub
										Intent itt = new Intent();
										itt.putExtra("cs", mCourses.get(n));
										// itt.putExtra("modify",n);
										itt.setClass(MainActivity.this,
												CourseAdder.class);
										startActivityForResult(itt, n);
									}
								})
						.setNegativeButton("删除",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										// TODO Auto-generated method stub
										mCourses.remove(n);
										getData();
										mListAdapter.notifyDataSetChanged();
									}
								}).show();
			}
		}
	};

	private List<Map<String, Object>> getData() {
		list.clear();
		if (mCourses.size() == 0) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", "你还木有添加课程哦");
			map.put("description", "么么哒～～");
			list.add(map);
			return list;
		}
		for (int i = 0; i < mCourses.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title",
					mCourses.get(i).CourseName + "   "
							+ mCourses.get(i).CourseAddr);
			String des = "";
			for (int j = 0; j < 7; j++) {
				// Log.d("gettedclassinfosize",mCourses.get(i).Classinfo.size()+"");
				if (mCourses.get(i).Classinfo.get(j) != null) {
					des = des
							+ mCourses.get(i).Classinfo.get(j).getDayStr(true);
				}
			}
			map.put("description", des);
			list.add(map);
		}
		return list;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			new AlertDialog.Builder(this)
			.setTitle("你可以通过以下方式联系到作者哦")
			.setMessage("qq：24770818\n邮箱：onebobo@hust.edu.cn\n")
			.setPositiveButton("碉堡了", null).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
