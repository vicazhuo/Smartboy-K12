package com.bean;

import java.util.ArrayList;
import java.util.Date;


public class Grade {
	private String gradeId;
	private String gradeName;
	private Date gradeCreatedate = new Date();
	private String gradeRemark;
	private int gradeIsactivate=1;
	private ArrayList<BResource> bResource = new ArrayList<BResource>();
	public String getGradeId() {
		return gradeId;
	}
	public void setGradeId(String gradeId) {
		this.gradeId = gradeId;
	}
	public String getGradeName() {
		return gradeName;
	}
	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}
	public Date getGradeCreatedate() {
		return gradeCreatedate;
	}
	public void setGradeCreatedate(Date gradeCreatedate) {
		this.gradeCreatedate = gradeCreatedate;
	}
	public String getGradeRemark() {
		return gradeRemark;
	}
	public void setGradeRemark(String gradeRemark) {
		this.gradeRemark = gradeRemark;
	}
	public int getGradeIsactivate() {
		return gradeIsactivate;
	}
	public void setGradeIsactivate(int gradeIsactivate) {
		this.gradeIsactivate = gradeIsactivate;
	}
	public ArrayList<BResource> getbResource() {
		return bResource;
	}
	public void setbResource(ArrayList<BResource> bResource) {
		this.bResource = bResource;
	}
}
