package com.king;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.constant.Constant;
import com.data.ActivateData;
import com.data.RegistData;
import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.security.Security;
import com.kingPadStudy.tools.EuipmentInfoCatcher;
import com.kingPadStudy.tools.SimpleCrypto;
import com.kingPadStudy.views.DialogCreator;
import com.kingpad.KingPadApp;
import com.kingpad.R;
import com.net.LoadData;
import com.net.RequestListener;
import com.net.RequestParameter;
/**
 * 激活应用程序处理类
 * @author lenovo
 *
 */
@SuppressLint("NewApi")
public class Activate extends LinearLayout {
	private EditText  mProductPassword;
	private Button mActivate,mRegist,button_regist_user;
	private KingPadApp mApp;
	private Activity activity;
	private Context context ;
	public Activate(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.activation, this,true);
		init();
	}
	
	public Activate(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.activation, this,true);
		init();
	}
	
	public Activate(Context context, KingPadApp app,Activity activity) {
		super(context);
		this.context = context;
		this.activity = activity;
		LayoutInflater.from(context).inflate(R.layout.activation, this,true);
		init();
		this.mApp = app;
	}
	
	
	private void init() {
		mProductPassword = (EditText) findViewById(R.id.edittext_product_password);
		mProductPassword.requestFocus();
		InputMethodManager m = (InputMethodManager)
				context.getSystemService( context.INPUT_METHOD_SERVICE );
		m.toggleSoftInput(0, InputMethodManager. HIDE_NOT_ALWAYS );
		mActivate = (Button) findViewById(R.id.button_activate);
		button_regist_user = (Button)findViewById(R.id.button_regist_user);
		button_regist_user.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				System.out.println("点击用户注册。。");
				//关闭输入法  
				((InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(mProductPassword.getWindowToken() , 0);
				//跳转到用户注册界面 
				UserRegister userRegister = new UserRegister(context,activity);
				activity.setContentView(userRegister);
			}
		});
		mRegist = (Button)findViewById(R.id.button_regist);
		mRegist.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("点击平板注册。。");
				//注册
				if(!Security.isregistPad(context)){
					//若没有注册过，那么注册  
					final Dialog dlg = DialogCreator.createLoadingDialog(getContext(),"正在注册平板...");
					dlg.show();
					String imei = KingPadStudyActivity.getAndroidId();
					int SysVersion = EuipmentInfoCatcher.getSysVersion();
					String CupInfo = EuipmentInfoCatcher.getCpuInfo();
					String ProductName = EuipmentInfoCatcher.getProductName();
					String ModelName = EuipmentInfoCatcher.getModelName();
					String ManufacturerName = EuipmentInfoCatcher.getManufacturerName();
					RequestParameter parameter = new RequestParameter();
					parameter.add("productNumber", imei);
					parameter.add("productName", ProductName);
					parameter.add("productCupinfo", CupInfo);
					parameter.add("productModelname", ModelName);
					parameter.add("productManufacturername", ManufacturerName);
					parameter.add("productSysversion", SysVersion+"");
					LoadData.loadData(Constant.REGIST_DATA, parameter, new RequestListener() {
						public void onError(String errMsg) {
							dlg.dismiss();
							showRegistDialog(-1);
						}
						public void onComplete(Object obj) {
							dlg.dismiss();
							RegistData data = (RegistData)obj;
							int res = data.getStatus();
							if(res == 0 || res == 1){
								//首次，或者二次注册都算注册成功
								Security.registPad(context);
								showRegistDialog(res);
							}else{
								showRegistDialog(-1);
							}
						}
					});
				}else{
					//显示已经注册过了
					showRegistDialog(-2);
				}
			}
		});

		
		mActivate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				System.out.println("点击激活");
				final String productNumber = KingPadStudyActivity.getAndroidId();
				if(productNumber == null || productNumber.equals("")) {
					makeText("产品序列号不能为空");
					return;
				}
				final String productPassword = mProductPassword.getText().toString().trim();
				if(productPassword == null || productNumber.equals("")) {
					makeText("密码不能为空");
					return;
				}
				final Dialog dlg = DialogCreator.createLoadingDialog(getContext(),"正在激活...");
				dlg.show();
				RequestParameter parameter = new RequestParameter();
				parameter.add("productNumber", productNumber);
				parameter.add("clientPassword", productPassword);
				LoadData.loadData(Constant.ACTIVATE_DATA, parameter, new RequestListener() {
					public void onError(String errMsg) {
						dlg.dismiss();
						makeText("激活程序失败");
					}
					public void onComplete(Object obj) {
						dlg.dismiss();
						ActivateData data = (ActivateData) obj;
						mApp.setProductPassword(productPassword);
						mApp.setProductNumber(productNumber);
						//TODO
						// 保存用户配置文件
						SharedPreferences sp = getContext().getSharedPreferences(Constant.LOCAL_DATA, Activity.MODE_WORLD_WRITEABLE);
						Editor editor = sp.edit();
						//给产品序列号加密
						String productNumber_encode = null;
						String productPassword_encode = null;
						try {
							productNumber_encode = SimpleCrypto.encrypt(Constant.PASSWORD_SimpleCrypto, productNumber);
							productPassword_encode = SimpleCrypto.encrypt(Constant.PASSWORD_SimpleCrypto, productPassword);
						} catch (Exception e) {
							e.printStackTrace();
						}
						editor.putString("productNumber", productNumber_encode);
						editor.putString("productPassword", productPassword_encode);
						editor.commit();
			            //保存设备唯一标识
			            String android_id = KingPadStudyActivity.getAndroidId();
						boolean res_activate = Security.registSoftWareAuthorityPassword(context,android_id);
//			            boolean res_login = Security.login(context, android_id);
			            if(res_activate ){
							makeText("数据激活成功！"); 
							//关闭输入法  
							((InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(mProductPassword.getWindowToken() , 0);
							((KingPadStudyActivity)getContext()).enterMainView();
			            }else{
			            	dlg.dismiss();
							makeText("激活程序失败");
			            }
					}
				});
			}
		});
	}
	
	
	private void showRegistDialog(int res) {
		Dialog dlg = null;
		String show = "";
		if(res == -1){ //参数错误
			show = "平板注册失败，请检查网络情况，或者联系服务商！";
		} else if(res == 1){	//成功
			show = "恭喜！您的平板电脑已经在服务器上注册成功！";
		}else if(res == -2){
			show = "您的平板已经注册过了，可以正常使用！";
		}else if(res == 0){
			show = "您的平板已经注册过了，可以正常使用！";
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
	
	
	private void makeText(final String text) {
		Toast.makeText(getContext(), text, 0).show();
	}
}
