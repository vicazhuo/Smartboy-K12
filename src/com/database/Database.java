package com.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bean.Down;
import com.constant.Constant;
import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.tools.SimpleCrypto;

public class Database {
	private String fileName = "/download.db";
	private File file = new File(Constant.DATABASE_PATH + fileName);
	private SQLiteDatabase db;

	public Database() {
		File catalog = new File(Constant.DATABASE_PATH);
		if (!catalog.exists())
			catalog.mkdirs();
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		db = SQLiteDatabase.openOrCreateDatabase(file, null);
		createTable();
	}

	public void createTable() {
		int aa = 0;
		Cursor cursor = db
				.rawQuery(
						"SELECT count(*) FROM sqlite_master WHERE type='table' AND name='resource'",
						null);
		while (cursor.moveToNext()) {
			aa = cursor.getInt(0);
		}

		cursor.close();
		if (aa == 1) {
			return;
		}

		String sql = "CREATE TABLE resource(id INTEGER PRIMARY KEY AUTOINCREMENT, course VARCHAR(20) NOT NULL,"
				+ "grade VARCHAR(20) NOT NULL, ispackage INT NOT NULL,resourceName VARCHAR(20) NOT NULL, resourceId VARCHAR(20) NOT NULL)";
		db.execSQL(sql);

	} // createTable

	// TODO 放入
	public void addResource(final Down resource) {
		String seed = KingPadStudyActivity.getAndroidId();
		try {
			String cource_encode = SimpleCrypto.encrypt(seed, resource.getCourse());
			String grade_encode = SimpleCrypto.encrypt(seed, resource.getGrade());
			String resource_encode = SimpleCrypto.encrypt(seed, resource.getResourceName());
			String id_encode = SimpleCrypto.encrypt(seed, resource.getResourceId());
			String sql = "INSERT INTO resource(course,grade,ispackage,resourceName,resourceId) VALUES('"
					+ cource_encode
					+ "','"
					+ grade_encode
					+ "',"
					+ resource.getIsPackage()
					+ ",'"
					+ resource_encode
					+ "','" + id_encode + "')";
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	// TODO 取出
	public HashMap<String, HashMap<String, ArrayList<String>>> query() {
		HashMap<String, HashMap<String, ArrayList<String>>> hashMap = new HashMap<String, HashMap<String, ArrayList<String>>>();
		Cursor cursor = db.rawQuery("SELECT * FROM resource", null);
		String seed = KingPadStudyActivity.getAndroidId();
		String cource_decode ;
		String grade_decode ;
		String resource_decode ;
		String id_decode;
		while (cursor.moveToNext()) {
			Down down = new Down();
			try {
				cource_decode = SimpleCrypto.decrypt(seed, cursor.getString(cursor.getColumnIndex("course")));
				grade_decode = SimpleCrypto.decrypt(seed, cursor.getString(cursor.getColumnIndex("grade")));
				resource_decode  = SimpleCrypto.decrypt(seed, cursor.getString(cursor.getColumnIndex("resourceName")));
				id_decode = SimpleCrypto.decrypt(seed, cursor.getString(cursor.getColumnIndex("resourceId")));
				down.setCourse(cource_decode);
				down.setGrade(grade_decode);
				down.setIsPackage(cursor.getInt(cursor.getColumnIndex("ispackage")));
				down.setResourceName(resource_decode);
				down.setResourceId(id_decode);
			} catch (Exception e) {
				System.out.println("有一个资源DB记录是非法复制的。。");
				continue;
			}
			if (down.getIsPackage() == 1)
				continue;

			HashMap<String, ArrayList<String>> map = hashMap.get(down
					.getCourse());
			if (map == null) {
				map = new HashMap<String, ArrayList<String>>();
				hashMap.put(down.getCourse(), map);
			}

			ArrayList<String> list = map.get(down.getGrade());
			if (list == null) {
				list = new ArrayList<String>(4);
				map.put(down.getGrade(), list);
			}

			int i = 0, len = list.size();
			for (i = 0; i <= len; i++) {
				if (i == len) {
					list.add(down.getResourceName());
					break;
				}

				if (down.getResourceName().equals(list.get(i)))
					break;
			}

			// if(i != len || i == 0)
			// list.add(down.getResourceName());
		}
		cursor.close();

		return hashMap;
	}

	// public HashMap<String, HashMap<String, Down>> query() {
	//
	// // HashMap<Course, HashMap<Grade, Down>>
	// HashMap<String, HashMap<String, Down>> hashMap = new HashMap<String,
	// HashMap<String,Down>>();
	//
	//
	// Cursor cursor = db.rawQuery("SELECT * FROM resource", null);
	// while(cursor.moveToNext()) {
	// Down down = new Down();
	// down.setCourse(cursor.getString(cursor.getColumnIndex("course")));
	// down.setGrade(cursor.getString(cursor.getColumnIndex("grade")));
	// down.setIsPackage(cursor.getInt(cursor.getColumnIndex("ispackage")));
	// down.setResourceName(cursor.getString(cursor.getColumnIndex("resourceName")));
	// down.setResourceId(cursor.getString(cursor.getColumnIndex("resourceId")));
	//
	// HashMap<String, Down> map = hashMap.get(down.getCourse());
	// if(map == null) {
	// map = new HashMap<String, Down>();
	// hashMap.put(down.getCourse(), map);
	// }
	//
	// Down down2 = map.get(down.getGrade());
	// if(down2 == null)
	// map.put(down.getGrade(), down2);
	//
	// } // while
	//
	// cursor.close();
	//
	// return hashMap;
	// }

	public void close() {
		db.close();
	}
}
