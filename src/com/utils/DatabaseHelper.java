package com.utils;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable(db);
	}
	
	public void createTable(SQLiteDatabase db){
		//创建目录表
		String sql = "CREATE TABLE IF NOT EXISTS " + DatabaseAdapter.CONTENT_TABLE + "("
				+ DatabaseAdapter.PATH + " STRING PRIMARY KEY,"
				+ DatabaseAdapter.LIGHT_STATE + " INTEGER,"
				+ DatabaseAdapter.IMPORTANT_STATE + " INTEGER,"
				+ DatabaseAdapter.SECOND_STATE + " INTEGER,"
				+ DatabaseAdapter.RESTART_STATE + " INTEGER);";
		db.execSQL(sql);
		
		//创建学习记录表
		sql = "CREATE TABLE IF NOT EXISTS " + DatabaseAdapter.RECORD_TABLE + "("
				+ DatabaseAdapter.PATH + " STRING,"
				+ DatabaseAdapter.NUMBER + " INTEGER,"
				+ DatabaseAdapter.FIRST_STATE + " INTEGER,"
				+ DatabaseAdapter.FINAL_STATE + " INTEGER);";
		db.execSQL(sql);
	}

	/**
	 *  更新数据库
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DatabaseAdapter.CONTENT_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DatabaseAdapter.RECORD_TABLE);
		
		onCreate(db);
	}

}
