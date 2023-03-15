/**
 * 
 */
package com.king;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.constant.Constant;
import com.data.UserRegisterData;
import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.security.Security;
import com.kingPadStudy.views.DialogCreator;
import com.kingpad.KingPadApp;
import com.kingpad.R;
import com.net.LoadData;
import com.net.RequestListener;
import com.net.RequestParameter;

/**
 * @author 陈相伯
 */
public class UserRegister extends LinearLayout {
	
	private Button button_submit ;
	private EditText text_name;
	private EditText text_id;
	private EditText text_address;
	private EditText text_email;
	private EditText text_phone;
	private Button button_back;
	private Context context;
	private Activate activate;
	private Activity activity;
	
	private String productId ;
	private String name ;
	private String id ;
	private String address ;
	private String email ;
	private String phone ;
	
	/**
	 * @param context
	 * @param attrs
	 */
	public UserRegister(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.layout_user_register, this,true);
		init();
		this.context = context;
	}
	/**
	 * @param context
	 * @param attrs
	 */
	public UserRegister(Context context,Activity activity) {
		super(context);
		this.context = context;
		this.activity = activity;
		LayoutInflater.from(context).inflate(R.layout.layout_user_register, this,true);
		init();
	}
	
	/**
	 * 初始化
	 */
	private void init() {
		KingPadApp mApp = (KingPadApp) activity.getApplication();
		activate = new Activate(context, mApp, activity);
		text_name = (EditText)findViewById(R.id.name);
		text_id = (EditText)findViewById(R.id.id_number);
		text_address = (EditText)findViewById(R.id.address);
		text_email = (EditText)findViewById(R.id.email);
		text_phone = (EditText)findViewById(R.id.phone_number);
		text_name.requestFocus();
		InputMethodManager m = (InputMethodManager)
				context.getSystemService(context.INPUT_METHOD_SERVICE);
		m.toggleSoftInput(0, InputMethodManager. HIDE_NOT_ALWAYS );
		button_back = (Button)findViewById(R.id.button_back);
		button_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//点击返回
				activity.setContentView(activate);
			}
		});
		button_submit = (Button)findViewById(R.id.button_register);
		button_submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				productId = KingPadStudyActivity.getAndroidId();
				try {
					name = new String(text_name.getText().toString().getBytes(),"utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				id = text_id.getText().toString();
				address = text_address.getText().toString();
				email = text_email.getText().toString();
				phone = text_phone.getText().toString();
				if(check()){
					//向服务器发送注册请求
					final Dialog dlg = DialogCreator.createLoadingDialog(getContext(),"正在注册用户...");
					dlg.show();
					RequestParameter parameter = new RequestParameter();
					//TODO
					parameter.add("productNumber",KingPadStudyActivity.getAndroidId());
					parameter.add("clientName", name);
					parameter.add("ClientAddress", address);
					parameter.add("clientEmail", email);
					parameter.add("clientTelnumber", phone);
					parameter.add("clientRemark", "");
					parameter.add("clientId", id);
//					parameter.add("productNumber","3333333333333");
//					parameter.add("clientName", "华华华");
//					parameter.add("ClientAddress", "成都金牛4");
//					parameter.add("clientEmail", "12345@sina4.com");
//					parameter.add("clientTelnumber", "15882121244");
//					parameter.add("clientRemark", "333");
//					parameter.add("clientId", "511622199006308317");
					LoadData.loadData(Constant.REGIST_USER_DATA, parameter, new RequestListener() {
						public void onError(String errMsg) {
							dlg.dismiss();
							showRegistDialog(-5);
						}
						public void onComplete(Object obj) {
							dlg.dismiss();
							//获取返回数据
							UserRegisterData data = (UserRegisterData)obj;  
							int res = data.getStatus(); 
							System.out.println("注册结果res="+res);
							showRegistDialog(res);
						}
					});
				}
			}
		});
	}
	
	/** 检查信息输入的正确性 
	 * @return
	 */
	protected boolean check() {
		//身份证正确性
		if(!isLegal(id)){
			showRegistDialog(-1);
			return false;
		}
		//姓名长度
		System.out.println("姓名长度："+name.length());
		if(name.length()<2){
			showRegistDialog(-6);
			return false;
		}
		return true;
	}
	

    public static boolean isLegal(String IDNumber){
        boolean result=IDNumber.matches("[0-9]{15}|[0-9]{17}[0-9X]");
        if(result){
            int year,month,date;
            if(IDNumber.length()==15){
                year=Integer.parseInt(IDNumber.substring(6,8));
                month=Integer.parseInt(IDNumber.substring(8,10));
                date=Integer.parseInt(IDNumber.substring(10,12));
            }
            else{
                year=Integer.parseInt(IDNumber.substring(6,10));
                month=Integer.parseInt(IDNumber.substring(10,12));
                date=Integer.parseInt(IDNumber.substring(12,14));
            }
            switch(month){
                case 2:result=(date>=1)&&(year%4==0?date<=29:date<=28);break;
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:result=(date>=1)&&(date<=31);break;
                case 4:
                case 6:
                case 9:
                case 11:result=(date>=1)&&(date<=31);break;
                default:result=false;break;
            }
        }
        return result;
    }
	
	private void showRegistDialog(int res) {
		Dialog dlg = null;
		String show = "";
		if(res == -3){ //参数错误
			show = "序列号错误！";
		} else if(res == -2){	//成功
			show = "不存在此身份证号码 ！";
		}else if(res == -1){
			show = "身份证号码错误！";
		}else if(res == 0){
			show = "此用户已经注册了！";
		}else if(res == 1){
			show = "用户注册成功！";
			activity.setContentView(activate);
		}else if(res == -5){
			show = "用户注册失败，请检查网络情况，或者联系服务商！";
		}else if(res == -6){
			show = "请输入合法的姓名！";
		}else if(res == -7){
			show = "请输入合法的电话号码！";
		}
		dlg = new AlertDialog.Builder(context)
		.setTitle("提示")
		.setMessage(show)
		.setNeutralButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {			
			}
		}) .create();
		dlg.show();
	}
	
	
}
