package com.once.schedulebook;

import java.io.Serializable;

public class ClassDayInfo implements Serializable{
	public int ClassNums;
	public int WeekDay;
	//public String Addr;

	public ClassDayInfo(int weekday, int classnums) {
		WeekDay = weekday;
		ClassNums = classnums;
	}

	/**
     * 参数为true时返回星期几第几节课，为false时只返回第几节课
     */
	public String getDayStr(boolean showWeekDay) {	
		String tem;
		if (showWeekDay) {
			tem = Methods.wkds[WeekDay] + " 第";
		} else {
			tem = "第";
		}
		int start = -1;
		boolean lasttrue = false;
		for (int i = 0; i < 12; i++) {
			if ((ClassNums & (1 << i)) == 1 << i) {
				if (!lasttrue) {
					if (start != -1)
						tem = tem + "、";
					start = i;
					lasttrue = true;
					continue;
				}
			} else if (lasttrue ) {
				if(start+1!=i) tem = tem + (start + 1) + "~" + i;
				else tem=tem+i;
				lasttrue = false;
			}
		}
		return tem + "节课 ";
	}
}
