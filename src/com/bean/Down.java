package com.bean;

/**
 * 本地保存记录数据信息实体
 * @author <a href="mailto:udmjh0508@126.com">莫建华</a>
 * @created 2013-1-30
 * @version V-1.0
 */
public final class Down {
	private String course;
	private String grade;
	private int isPackage;
	private String resourceName;
	private String resourceId;
	
	public Down() {
		
	}
	
	public Down(String course, String grade, int isPackage,
			String resourceName, String resourceId) {
		super();
		this.course = course;
		this.grade = grade;
		this.isPackage = isPackage;
		this.resourceName = resourceName;
		this.resourceId = resourceId;
	}
	
	public String getCourse() {
		return course;
	}
	public void setCourse(String course) {
		this.course = course;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public int getIsPackage() {
		return isPackage;
	}
	public void setIsPackage(int isPackage) {
		this.isPackage = isPackage;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
}
