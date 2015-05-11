package com.once.schedulebook;

import java.io.Serializable;
import java.util.ArrayList;

public class Course implements Serializable{


	String CourseName;
	String CourseAddr;
	int Weeks;
	public ArrayList<ClassDayInfo> Classinfo;
	//public static boolean[][] table=new boolean[7][10];
	public Course(){
		Classinfo=new ArrayList<ClassDayInfo>();
		for(int i=0;i<7;i++) Classinfo.add(null);
		Weeks=0;
	}
	public boolean checkok(){
		if(Weeks==0){
			return false;
		}
		if(CourseName.isEmpty()||CourseAddr.isEmpty()){
			return false;
		}
		for(int i=0;i<=7;i++) {
			if(i==7) return false;
			if(Classinfo.get(i)!=null) {
				break;
			}
		}
		return true;
	}
	
//	public ClassDayInfo getClassInfoOn(int weekday){
//		for (int i = 0; i < Classinfo.size(); i++) {
//			if(weekday==Classinfo.get(i).WeekDay){
//				return Classinfo.get(i);
//			}
//		}
//		return null;
//	}
	public String getWeekStr(){
		String tem="第";
		int start=-1;
		boolean lasttrue=false;
		for(int i=0;i<25;i++){
			if((Weeks&(1<<i))==1<<i){
				if(!lasttrue){
					if(start!=-1) tem=tem+"、";
					start=i;
					lasttrue=true;
					continue;
				}
			}else if(lasttrue){
				if(start+1!=i) tem = tem + (start + 1) + "~" + i;
				else tem=tem+i;
				lasttrue=false;
			}
		}
		return tem+"周";
	}
}
