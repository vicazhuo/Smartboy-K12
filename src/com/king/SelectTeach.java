package com.king;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.renderscript.Element;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.constant.Constant;
import com.kingPadStudy.tools.FileHandler;
import com.kingpad.R;

/**
 * 选择教材界面
 * @author lenovo
 *
 */
public class SelectTeach extends LinearLayout{

	/**
	 * 下拉选择框
	 */
	private Spinner mGrade, mChina, mMath, mEnglish;
	
	private LayoutInflater mInflater;
	
//	private String[] chinaString, mathString, englishString;
	private Button mSubmit;
	
	private Context context;
	
	/**
	 * 指示当前选中的关键字
	 */
	private String currentIndicate;
	
	/**
	 * 下载记录列表
	 */
	public static HashMap<String, HashMap<String, ArrayList<String>>> mHashMap;
	
	public SelectTeach(Context context) {
		super(context);
		this.context = context;
		mInflater = LayoutInflater.from(context);
		mInflater.inflate(R.layout.select_book, this, true);
		init();
	}
	
	private void init() {
		
		//list = new ArrayList<Down>();
		mHashMap = MainActivity.getDatabase().query();
		
    	mGrade = (Spinner) findViewById(R.id.spinner_grade);
    	mChina = (Spinner) findViewById(R.id.spinner_course_book_china);
    	mMath = (Spinner) findViewById(R.id.spinner_course_book_math);
    	mEnglish = (Spinner) findViewById(R.id.spinner_course_book_english);
    	
    	mSubmit = (Button) findViewById(R.id.button_submit);
    	mSubmit.setOnClickListener(listener);
    	
    	String []gradeString = {"一年级","二年级", "三年级", "四年级", "五年级", "六年级"};
    	mGrade.setAdapter(getAdapter(gradeString));
    	
    	mGrade.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				String type = (String) mGrade.getItemAtPosition(position);
				Log.e("tag", type);
				
				if(type.equals(currentIndicate))
					return;
				
				HashMap<String, ArrayList<String>> china = mHashMap.get("Chinese");
				HashMap<String, ArrayList<String>> math = mHashMap.get("Math");
				HashMap<String, ArrayList<String>> english = mHashMap.get("English");
				
				ArrayList<String> chinaList = null, mathList = null, englishList = null;
				if(china != null)
					chinaList = china.get(type);
				
				if(math != null)
					mathList = math.get(type);
				
				if(english != null)
					englishList = english.get(type);
				mChina.setAdapter(getAdapter(chinaList));
				mMath.setAdapter(getAdapter(mathList));
				mEnglish.setAdapter(getAdapter(englishList));
				currentIndicate = type;
			}
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
    	
    	// 加载本地保存的数据
    	SharedPreferences sp = getContext().getSharedPreferences(Constant.LOCAL_DATA, Activity.MODE_WORLD_WRITEABLE);
    	String selGrade = sp.getString("grade", "");
    	String selChina = sp.getString("china", "");
    	if(selChina != null && !selChina.equals(""))
    		selChina = selChina.substring(selChina.lastIndexOf('/') + 1);
    	
    	String selMath = sp.getString("math", "");
    	if(selMath != null && !selMath.equals(""))
    		selMath = selMath.substring(selMath.lastIndexOf('/') + 1);
    	
    	String selEnglish = sp.getString("english", "");
    	if(selEnglish != null && !selEnglish.equals(""))
    		selEnglish = selEnglish.substring(selEnglish.lastIndexOf('/') + 1);
    	
    	if(selGrade == null || selGrade.equals(""))
    		selGrade = "一年级";
    		
    	
    	HashMap<String, ArrayList<String>> china = mHashMap.get("Chinese");
		HashMap<String, ArrayList<String>> math = mHashMap.get("Math");
		HashMap<String, ArrayList<String>> english = mHashMap.get("English");
		
		ArrayList<String> chinaList = null, mathList = null, englishList = null;
		if(china != null)
			chinaList = china.get(selGrade);
		
		if(math != null)
			mathList = math.get(selGrade);
		
		if(english != null)
			englishList = english.get(selGrade);
		
		mChina.setAdapter(getAdapter(chinaList));
		mMath.setAdapter(getAdapter(mathList));
		mEnglish.setAdapter(getAdapter(englishList));
		
		currentIndicate = selGrade;
    	
		int a = setResource(selGrade);
		mGrade.setSelection(a);
		
		if(chinaList != null)
		for(int i = 0, len = chinaList.size(); i < len; i++) 
			if(chinaList.get(i).equals(selChina)) {
				mChina.setSelection(i);
				break;
			}
		
		if(mathList != null)
		for(int i = 0, len = mathList.size(); i < len; i++) 
			if(mathList.get(i).equals(selMath)) {
				mMath.setSelection(i);
				break;
			}
		
		if(englishList != null)
		for(int i = 0, len = englishList.size(); i < len; i++) 
			if(englishList.get(i).equals(selEnglish)) {
				mEnglish.setSelection(i);
				break;
			}
	}
	
	
	public int setResource(final String grade) {
		if(grade == null)
			return 0;
		
		int a = 0;
		if(grade.equals("一年级")) 
			a = 0;
		 else if(grade.equals("二年级")) 
			a = 1;
		 else if(grade.equals("三年级")) 
			a = 2;
		 else if(grade.equals("四年级")) 
			a = 3;
		 else if(grade.equals("五年级")) 
			a = 4;
		 else if(grade.equals("六年级")) 
			a = 5;
    	
    	return a;
	}
	
	 private ArrayAdapter getAdapter(String[] strings){
		 if(strings == null)
			 return null;
		 
		ArrayAdapter spinnerAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_style, strings);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		return spinnerAdapter;
	}

	 private ArrayAdapter getAdapter(ArrayList<String> list){
		 if(list == null)
			 list = new ArrayList<String>();
		 
		ArrayAdapter spinnerAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_style, list);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		return spinnerAdapter;
	}
	 
	private View.OnClickListener listener = new View.OnClickListener() {
		
		public void onClick(View v) {
			if(v == mSubmit) {
				SharedPreferences sp = getContext().getSharedPreferences(Constant.LOCAL_DATA, Activity.MODE_WORLD_WRITEABLE);
				Editor editor = sp.edit();
				String gradeString = (String) mGrade.getSelectedItem();
				editor.putString("grade", gradeString);
				
				//语文部分
				String chinaString = (String)mChina.getSelectedItem();
				String save_china = Constant.RESOURCE_PATH + "/Chinese/" +
						gradeString + "/"+ chinaString;
				if(chinaString == null || chinaString.equals("")){
					editor.putString("china", "");
				}else{
					System.out.println("save_china="+save_china);
					String size_china_string = FileHandler.readStringFromFile(context,
							"size_" + save_china );
					if(size_china_string == null){
						//如果加密信息被删除了那么重新加密
						com.utils.Util.encodeResource(context, chinaString, com.utils.Util.getPackageUrl(save_china));
					}
					editor.putString("china", save_china);
				}
				
				//数学部分
				String mathString = (String)mMath.getSelectedItem();
				String save_math = Constant.RESOURCE_PATH + "/Math/" +
						gradeString + "/"+ mathString;
				if(mathString == null || mathString.equals("")){
					editor.putString("math", "");
				}else{
					String size_math_string = FileHandler.readStringFromFile(context,
							"size_" + save_math );
					if(size_math_string == null){
						//如果加密信息被删除了那么重新加密
						com.utils.Util.encodeResource(context, mathString, com.utils.Util.getPackageUrl(save_math));
					}
					editor.putString("math", save_math);
				}

				//英语部分
				String englishString = (String)mEnglish.getSelectedItem();
				String save_english = Constant.RESOURCE_PATH + "/English/" +
						gradeString + "/"+ englishString;
				if(englishString == null || englishString.equals("")){
					editor.putString("english", "");
				}else{
					String size_english_string = FileHandler.readStringFromFile(context,
							"size_" + save_english );
					if(size_english_string == null){
						//如果加密信息被删除了那么重新加密
						com.utils.Util.encodeResource(context, englishString, com.utils.Util.getPackageUrl(save_english));
					}
					editor.putString("english", save_english);
				}
				
				editor.commit();
				
				Toast.makeText(getContext(), "数据保存成功", 0).show();
			}
		}
	};
	
}
