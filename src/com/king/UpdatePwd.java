package com.king;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.constant.Constant;
import com.data.UpdatePwdData;
import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.tools.SimpleCrypto;
import com.kingPadStudy.views.DialogCreator;
import com.kingpad.KingPadApp;
import com.kingpad.R;
import com.net.LoadData;
import com.net.RequestListener;
import com.net.RequestParameter;
//import com.server.data.UpdatePwdData;
/**
 * 更改密码界面
 * @author lenovo
 *
 */
public final class UpdatePwd extends LinearLayout{
	/**
	 * 密码框
	 */
	private EditText mOldPwd, mNewPwd, mRenewPwd;
	/**
	 * 提交按钮
	 */
	private Button mCommit;
	/**
	 * 上下文
	 */
	private Context context;
	
	
	public UpdatePwd(Context context) {
		super(context);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.update_pwd, this, true);
		init();
		this.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
//				System.out.println("onFocusChange......");
//				System.out.println("onFocusChange......");
				if(!hasFocus){
					dismissInput();
				}
			}
		});
	}
	
	private void init() {
		mOldPwd = (EditText) findViewById(R.id.edittext_old_pwd);
		mNewPwd = (EditText) findViewById(R.id.edittext_new_pwd);
		mRenewPwd = (EditText) findViewById(R.id.edittext_renew_pwd);
		mOldPwd.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				((InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(mOldPwd.getWindowToken() , 0);
			}
		});
		mNewPwd.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				((InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(mNewPwd.getWindowToken() , 0);
			}
		});
		mRenewPwd.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				((InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(mRenewPwd.getWindowToken() , 0);
			}
		});
		mCommit = (Button) findViewById(R.id.button_commit);
		mCommit.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				final String oldPwd = mOldPwd.getText().toString();
				if(oldPwd == null || oldPwd.equals("")) {
					makeText("密码为空");
					dismissInput();
					return;
				}
				
				final String newPwd = mNewPwd.getText().toString();
				final String renewPwd = mRenewPwd.getText().toString();
				if(newPwd == null || newPwd.equals("") || !newPwd.equals(renewPwd)) {
					makeText("两次密码不匹配");
					dismissInput();
					return;
				}
				if(newPwd.length() < 3) {
					makeText("密码太短");
					dismissInput();
					return;
				}
				// 向服务器提交数据
				final Dialog dlg =   DialogCreator.createLoadingDialog(getContext(),"提交中...");
				dlg.show();
				RequestParameter parameter = new RequestParameter();
				KingPadStudyActivity activity = (KingPadStudyActivity) context;
				KingPadApp mApp = activity.getmApp();
				parameter.add("productNumber", mApp.getProductNumber());
				parameter.add("clientPassword", oldPwd);
				parameter.add("newPassword", newPwd);
				LoadData.loadData(Constant.UPDATE_PWD_DATA, parameter, new RequestListener() {
					public void onError(String errMsg) {
						makeText("更改密码失败");
						dlg.dismiss();
					}
					
					public void onComplete(Object obj) {
						dlg.dismiss();
						UpdatePwdData data = (UpdatePwdData) obj;
						if(data.getStatus() == 1) {
							dismissInput();
							// 保存用户配置文件
							SharedPreferences sp = getContext().getSharedPreferences(Constant.LOCAL_DATA, Activity.MODE_WORLD_WRITEABLE);
							Editor editor = sp.edit();
							//给产品序列号加密
							String productPassword_encode = null;
							try {
								productPassword_encode = SimpleCrypto.encrypt(Constant.PASSWORD_SimpleCrypto, newPwd);
							} catch (Exception e) {
								e.printStackTrace();
							}
							editor.putString("productPassword", productPassword_encode);
							editor.commit();
							makeText("更改密码成功");
						}
						else {
							dismissInput();
							makeText("更改密码失败");
						}
						clearInput();
					}

					
				});
				
			}
		});
	}
	
	private void clearInput() {
		mOldPwd.setText("");
		mNewPwd.setText("");
		mRenewPwd.setText("");
	}
	
	private void dismissInput(){
		((InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE))
		.hideSoftInputFromWindow(mOldPwd.getWindowToken() , 0);
		((InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE))
		.hideSoftInputFromWindow(mNewPwd.getWindowToken() , 0);
		((InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE))
		.hideSoftInputFromWindow(mRenewPwd.getWindowToken() , 0);
	}
	
	
	private void makeText(final String text) {
		Toast.makeText(getContext(), text, 0).show();
	}
}
