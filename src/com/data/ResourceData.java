package com.data;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.Uri;
import android.view.View;

import com.bean.BResource;
import com.bean.Course;
import com.bean.Grade;
import com.constant.Constant;
import com.net.AsyncRequest;
import com.net.RequestParameter;
/**
 * 解析学科、年级资源数据，用于下载链接页面
 * @author <a href="mailto:udmjh0508@126.com">莫建华</a>
 * @created 2013-1-14
 * @version V-1.0
 */
public class ResourceData extends BaseData {

	/**
	 * 语文、数学、英语列表
	 */
	private Course mChineseCourse = new Course(), mMathCourse = new Course(), mEnglishCourse = new Course();
	
	@Override 
	public void startParse(RequestParameter parameter) throws Exception { 
		final String rs = AsyncRequest.openUrl(Constant.SERVER_URL +  
				"business/services!getDirectory.action", AsyncRequest.HTTP_GET, parameter);
		if(rs == null) 
			throw new NullPointerException("网络数据请求为空"); 
		JSONObject json = new JSONObject(rs);
		mStatus = json.getInt("status");
		System.out.println("state="+mStatus);
		
		if(mStatus != 1)
			throw new Exception(getErrorMessage(mStatus));
		
		JSONArray array = json.getJSONArray("list");
		int length = array.length();
		if(length == 0)
			return;
		
		BResource resource = null;
		for(int i = 0; i < length; i++) {
			resource = new BResource();
			JSONObject object = array.optJSONObject(i);
			resource.setResourceId(object.getString("resourceId"));
//			resource.setResourceUrl(Constant.SERVER_URL + object.getString("resourceUrl"));
			resource.setResourceSpace(object.getDouble("resourceSpace"));
			resource.setIspackage(object.getInt("ispackage"));
			resource.setUpdateDate(new Date(object.getJSONObject("updateDate").getLong("time")));
			resource.setResourcePrice(object.getDouble("resourcePrice"));
			resource.setResourceisactivate(object.getInt("resourceisactivate"));
			resource.setResourceRemark(object.getString("resourceRemark"));
			resource.setResourceName(object.getString("resourceName"));
			resource.setFileName(object.getString("fileName"));
			resource.setResourceNumber(object.getString("resourceNumber"));
			resource.setResourceIsold(object.getInt("resourceIsold"));
			resource.setDownload(object.getInt("isDownload"));
			// 解析课程属性
			Course course = analyzeCourse(object.getJSONObject("resourceCourse"));
			Grade grade = analyzeGrade(object.getJSONObject("resourceGrade"));
			
			setCourse(course, grade, resource);
		} // for
	}
	
	private Course analyzeCourse(JSONObject json) throws JSONException{
		Course course = new Course();
		course.setCourseId(json.getString("courseId"));
		course.setCourseName(json.getString("courseName"));
		course.setCourseCode(json.getString("courseCode"));
		course.setCourseCreatedate(new Date(json.getJSONObject("courseCreatedate").getLong("time")));
		course.setCourseRemark(json.getString("courseRemark"));
		
		return course;
	} // analyzeCourse
	
	private Grade analyzeGrade(JSONObject json) throws JSONException {
		Grade grade = new Grade();
		grade.setGradeId(json.getString("gradeId"));
		grade.setGradeName(json.getString("gradeName"));
		grade.setGradeCreatedate(new Date(json.getJSONObject("gradeCreatedate").getLong("time")));
		grade.setGradeRemark(json.getString("gradeRemark"));
		grade.setGradeIsactivate(json.getInt("gradeIsactivate"));
		
		return grade;
	} // analyzeGrade

	private void setCourse(Course course, Grade grade, BResource resource) {
		if("Chinese".equals(course.getCourseCode())) 
		{
			if(mChineseCourse.getCourseId() == null)
				mChineseCourse = course;
			
			course = mChineseCourse;
		}
		else if("Math".equals(course.getCourseCode()))
		{
			if(mMathCourse.getCourseId() == null)
				mMathCourse = course;
			
			course = mMathCourse;
		} 
		else if("English".equals(course.getCourseCode())) 
		{
			if(mEnglishCourse.getCourseId() == null) 
				mEnglishCourse = course;
			
			course = mEnglishCourse;
		}
		
		Grade grade2 = exist(course, grade);
		if(grade2 == null) {
			course.getGrades().add(grade);
			grade.getbResource().add(resource);
		}
		else {
			grade2.getbResource().add(resource);
		}
		
		
	} // setCourse
	
	private Grade exist(final Course course, final Grade grade) {
		ArrayList<Grade> list = course.getGrades();
		int len = list.size();
		for(int i = 0; i < len; i++) {
			Grade g = list.get(i);
			if(g.getGradeName().equals(grade.getGradeName())) {
				// 相等则返回
				return g;
			}
		}
		
		return null;
	} // exist
	
	public Course getChinese() {
		return mChineseCourse;
	}
	
	public Course getMath() {
		return mMathCourse;
	}
	
	public Course getEnglish() {
		return mEnglishCourse;
	}
	
}
