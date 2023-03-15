package com.utils;

import java.util.ArrayList;

import android.R.bool;
import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bean.ContentStateBean;
import com.bean.TopicBean;

public class DatabaseAdapter {
	/**
	 * 数据库名字
	 */
	public static final String DATABASE_NAME = "RecordDatabase.db";
	
	//目录表的表名
	public static final String CONTENT_TABLE		= "content";
	//目录表的各个字段名
	public static final String LIGHT_STATE			= "light_state";		//灯的状态字段
	public static final String IMPORTANT_STATE		= "important_state";	//重点复习按钮状态字段
	public static final String SECOND_STATE			= "second_state";		//次重点复习按钮状态字段
	public static final String RESTART_STATE		= "restart_state";		//重新开始按钮状态字段
	
	
	//题目状态记录表的表名
	public static final String RECORD_TABLE			= "record";
	//题目状态表中的各个字段
	public static final String FIRST_STATE			= "first_state";		//第一遍做题后的题目状态
	public static final String FINAL_STATE			= "final_state";		//最后一次做题的题目状态
	public static final String NUMBER				= "number";				//题目的编号
	public static final String PATH					= "path";				//题目所在的路径
	
	private static final int DATABASE_VERSION 		= 1;					// 数据库版本 
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private Cursor cursor;
	private static DatabaseAdapter databaseAdapter;
	
	private DatabaseAdapter(Context context){
		dbHelper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public static DatabaseAdapter getInstance(Context context){
		if (null == databaseAdapter){
			databaseAdapter = new DatabaseAdapter(context);
		}
		
		return databaseAdapter;
	}
	
	/**
	 * 打开数据库
	 */
	public void openDatabase(){
		try {
			if(db == null || !db.isOpen()){
				db = dbHelper.getWritableDatabase();
				dbHelper.createTable(db);
			}
		} catch (SQLException e) {
			if(!db.isOpen()){
				db = dbHelper.getReadableDatabase();
			}
		}
	}
	
	/**
	 * 关闭数据库
	 */
	public void closeDatabase(){
		db.close();
	}
	
	/**
	 * 向目录表中添加一条数据
	 * @param path：目录路径
	 * @param lightState：目录上灯的状态：0-未完成，没有灯。1-全部掌握，绿灯。2-最差为模糊状态，黄灯。3-最差为完全不会，红灯。
	 * @param impState：重点复习按钮的状态：0-不可用。1-可用。
	 * @param scdState：次重点复习按钮的状态：0-不可用。1-可用。
	 * @param rstState：重新开始按钮的状态：0-不可用。1-可用。
	 */
	public void add2Content(String path, int lightState, int impState, int scdState, int rstState){
		openDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(PATH, path);
		contentValues.put(LIGHT_STATE, lightState);
		contentValues.put(IMPORTANT_STATE, impState);
		contentValues.put(SECOND_STATE, scdState);
		contentValues.put(RESTART_STATE, rstState);
		db.insert(CONTENT_TABLE, null, contentValues);
		closeDatabase();
	}
	
	/**
	 * 根据路径取出显示灯的状态
	 * @param path:路径
	 * @return：字段light_state中的值，-1：表示没有以该路径作为关键字的信息
	 */
	public int getLishtState(String path){
		openDatabase();
		
		String sql = "SELECT " + LIGHT_STATE + " FROM " + CONTENT_TABLE + " WHERE " + PATH + "=\"" + path + "\";";
		cursor = db.rawQuery(sql, null);
		if (cursor.getCount() == 0){
			cursor.close();
			closeDatabase();
			return -1;
		}
		cursor.moveToLast();
		int k = cursor.getInt(0);
		
		cursor.close();
		closeDatabase();
		
		return k;
	}
	
	/**
	 * 根据路径取出重点复习按钮的状态
	 * @param path
	 * @return
	 */
	public int getImpState(String path){
		openDatabase();
		
		String sql = "SELECT " + IMPORTANT_STATE + " FROM " + CONTENT_TABLE + " WHERE " + PATH + "=\"" + path + "\";";
		cursor = db.rawQuery(sql, null);
		if (cursor.getCount() == 0){
			cursor.close();
			closeDatabase();
			return -1;
		}
		cursor.moveToLast();
		int k = cursor.getInt(0);
		
		cursor.close();
		closeDatabase();
		
		return k;
	}
	/**
	 * 根据路径取出次重点复习按钮的状态
	 * @param path
	 * @return
	 */
	public int getScdState(String path){
		openDatabase();
		
		String sql = "SELECT " + SECOND_STATE + " FROM " + CONTENT_TABLE + " WHERE " + PATH + "=\"" + path + "\";";
		cursor = db.rawQuery(sql, null);
		if (cursor.getCount() == 0){
			cursor.close();
			closeDatabase();
			return -1;
		}
		cursor.moveToLast();
		int k = cursor.getInt(0);
		
		cursor.close();
		closeDatabase();
		
		return k;
	}
	/**
	 * 根据路径取出次重新开始按钮的状态
	 * @param path
	 * @return
	 */
	public int getRstState(String path){
		openDatabase();
		
		String sql = "SELECT " + RESTART_STATE + " FROM " + CONTENT_TABLE + " WHERE " + PATH + "=\"" + path + "\";";
		cursor = db.rawQuery(sql, null);
		if (cursor.getCount() == 0){
			cursor.close();
			closeDatabase();
			return -1;
		}
		cursor.moveToLast();
		int k = cursor.getInt(0);
		
		cursor.close();
		closeDatabase();
		
		return k;
	}
	
	/**
	 * 获取一条目录下面的所有按钮以及显示灯的信息
	 * @param path
	 * @return
	 */
	public ContentStateBean getContentState(String path){
		openDatabase();
		
		String sql = "SELECT * FROM " + CONTENT_TABLE + " WHERE " + PATH + "=\"" + path + "\";";
		cursor = db.rawQuery(sql, null);
		if (cursor.getCount() == 0){
			cursor.close();
			closeDatabase();
			return null;
		}
		
		cursor.moveToLast();
		ContentStateBean bean = new ContentStateBean();
		bean.lightState = cursor.getInt(1);
		bean.importantState = cursor.getInt(2);
		bean.secondState = cursor.getInt(3);
		bean.restartState = cursor.getInt(4);
		
		cursor.close();
		closeDatabase();
		
		return bean;
	}
	
	
	/**
	 * 向record（学习记录表）中添加一条记录
	 * @param path：学习的路径
	 * @param number：题号
	 * @param firstState：第一次学习的题目状态
	 * @param finalState：最后一次学习的题目状态
	 */
	public void add2Record(String path, int number, int firstState, int finalState){
		
		if (hasSameRecord(path, number)){
			//有这条记录则删除之后在进行插入操作
			deleteRecord(path, number);
		}
		
		openDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(PATH, path);
		contentValues.put(NUMBER, number);
		contentValues.put(FIRST_STATE, firstState);
		contentValues.put(FINAL_STATE, finalState);
		
		db.insert(RECORD_TABLE, null, contentValues);
		
		closeDatabase();
	}
	
	/**
	 * 判断record表中是否有path和number作为关键字相同的记录，有则返回true
	 * @param path：
	 * @param number
	 * @return
	 */
	public boolean hasSameRecord(String path, int number){
		openDatabase();
		
		boolean result = false;
		
		String sql = "SELECT * FROM " + RECORD_TABLE + " WHERE " + PATH + "=\"" + path + "\" AND " + NUMBER + "=\"" + number + "\";";
		cursor = db.rawQuery(sql, null);
		if (cursor.getCount() == 0){
			result = false;
		}else {
			result = true;
		}
		
		cursor.close();
		closeDatabase();
		
		return result;
	}
	
	public void deleteRecord(String path, int number){
		openDatabase();
		
		String sql = "DELETE FROM " + RECORD_TABLE + " WHERE " + PATH + "=\"" + path + "\" AND " + NUMBER + "=\"" + number + "\";";
		db.execSQL(sql);
		
		closeDatabase();
	}
	
	/**
	 * 修改一个路径下面一个题号的final_state
	 * @param path：要修改题所在的路径
	 * @param number：要修改的题号
	 */
	public void alterFinalState(String path, int number, int finalState){
		openDatabase();
		
		ContentValues contentValues = new ContentValues();
		contentValues.put(FINAL_STATE, finalState);
		
		String whereString = PATH + "=\"" + path + "\" AND " + NUMBER + "=" + number;
		db.update(RECORD_TABLE, contentValues, whereString, null);
		
		closeDatabase();
	}
	
	/**
	 * 从record表中取出一个路径下面的所有题目数据
	 * @param path
	 * @return
	 */
	public ArrayList<TopicBean> getRecord(String path){
		openDatabase();
		
		String sql = "SELECT * FROM " + RECORD_TABLE + " WHERE " + PATH + "=\"" + path + "\";"; 
		cursor = db.rawQuery(sql, null);
		if (cursor.getCount() == 0){
			cursor.close();
			closeDatabase();
			return null;
		}
		
		ArrayList<TopicBean> topicBeans = new ArrayList<TopicBean>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()){
			TopicBean topicBean = new TopicBean();
			topicBean.number = cursor.getInt(1);
			topicBean.firstState = cursor.getInt(2);
			topicBean.finalState = cursor.getInt(3);
			topicBeans.add(topicBean);
			
			cursor.moveToNext();
		}
		
		cursor.close();
		closeDatabase();
		
		return topicBeans;
	}
	
	/**
	 * 取出一条路径下面一种类型的题目编号
	 * @param path:题目所在路径
	 * @param firstState：题目的类型，可以是“0-未完成，1-完全掌握，2-模糊，3-完全不会”
	 * @return
	 */
	public ArrayList<TopicBean> getOneTypeRecord(String path, int firstState){
		openDatabase();
		
		String sql = "SELECT * FROM " + RECORD_TABLE
				+ " WHERE " + PATH + "=\"" + path + "\" AND " + FIRST_STATE + "=" + firstState;
		cursor = db.rawQuery(sql, null);
		if (cursor.getCount() == 0){
			cursor.close();
			closeDatabase();
			return null;
		}
		
		ArrayList<TopicBean> topicBeans = new ArrayList<TopicBean>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()){
			TopicBean topicBean = new TopicBean();
			topicBean.number = cursor.getInt(1);
			topicBeans.add(topicBean);
			
			cursor.moveToNext();
		}
		
		cursor.close();
		closeDatabase();
		
		return topicBeans;
	}
	
	/**
	 * 删除content表和record表中一条路径下面的所有记录
	 * @param path
	 */
	public void deleteOnePathRecord(String path){
		openDatabase();
		
		String sql = "DELETE FROM " + RECORD_TABLE + " WHERE " + PATH + "=\"" + path + "\";";
		db.execSQL(sql);
		
		sql = "DELETE FROM " + CONTENT_TABLE + " WHERE " + PATH + "=\"" + path + "\";";
		db.execSQL(sql);
		
		closeDatabase();
	}
	
	/**
	 * 检查一张表中的其中一条路径是否存在
	 * @param tableName：要检测的表名
	 * @param path：要检测的值
	 * @return：true-该表中存在该值
	 */
	public boolean isValueExists(String tableName, String path){
		openDatabase();
		
		String sql = "SELECT * FROM " + tableName + " WHERE " + PATH + "=\"" + path + "\";";
		cursor = db.rawQuery(sql, null);
		
		if (cursor.getCount() == 0){
			closeDatabase();
			return false;
		}else {
			closeDatabase();
			return true;
		}
	}

	/**
	 * 函数作用：修改一个路径的目录属性
	 * void
	 * @param path2
	 * @param bean
	 */
	public void alterContentState(String path2, ContentStateBean bean) {
		openDatabase();
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(LIGHT_STATE, bean.lightState);
		contentValues.put(IMPORTANT_STATE, bean.importantState);
		contentValues.put(SECOND_STATE, bean.secondState);
		contentValues.put(RESTART_STATE, bean.restartState);
		
		String whereString = PATH + "=\"" + path2 + "\"";
		db.update(CONTENT_TABLE, contentValues, whereString, null);
		closeDatabase();
	}
}
