package com.bean;

import java.util.ArrayList;
import java.util.Date;

/**
 * 学科实体bean
 * @author <a href="mailto:udmjh0508@126.com">莫建华</a>
 * @created 2013-1-14
 * @version V-1.0
 */
public class Course {
	private String courseId;
	private String courseName;
	private String courseCode;
	private Date courseCreatedate;
	private String courseRemark;
	private ArrayList<Grade> grades = new ArrayList<Grade>();
	public String getCourseId() {
		return courseId;
	}
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public String getCourseCode() {
		return courseCode;
	}
	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}
	public Date getCourseCreatedate() {
		return courseCreatedate;
	}
	public void setCourseCreatedate(Date courseCreatedate) {
		this.courseCreatedate = courseCreatedate;
	}
	public String getCourseRemark() {
		return courseRemark;
	}
	public void setCourseRemark(String courseRemark) {
		this.courseRemark = courseRemark;
	}
	public ArrayList<Grade> getGrades() {
		return grades;
	}
	public void setGrades(ArrayList<Grade> grades) {
		this.grades = grades;
	}
}
