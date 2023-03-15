package com.king;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.constant.Constant;
import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.tools.SimpleCrypto;
import com.kingpad.KingPadApp;
import com.kingpad.R;
public class Login extends LinearLayout {
	
	private Button mLoginBtn, mQuitBtn;
	private EditText  mPassword;
	private KingPadApp mApp;
	private Context context;
	
	public Login(Context context, KingPadApp app) {
		super(context);
		this.context = context;
		KingPadStudyActivity.dismissWaitDialog();
		this.mApp = app;
		LayoutInflater.from(context).inflate(R.layout.login, this, true);
		init();
	}
	
			
	private void init() {
		mPassword = (EditText) findViewById(R.id.edittext_password);
		mPassword.requestFocus();
		InputMethodManager m = (InputMethodManager)
				context.getSystemService( context.INPUT_METHOD_SERVICE );
		m.toggleSoftInput(0, InputMethodManager. HIDE_NOT_ALWAYS );
		mLoginBtn = (Button) findViewById(R.id.button_login);
		mQuitBtn = (Button) findViewById(R.id.button_quit);
		mLoginBtn.setOnClickListener(listener);
		mQuitBtn.setOnClickListener(listener);
	}
	
	private View.OnClickListener listener = new View.OnClickListener() {
		public void onClick(View v) {
			if(v == mLoginBtn) {
				final String password = mPassword.getText().toString();
				if(password.equals("")) {
					Toast.makeText(getContext(), "口令不能为空", 0).show();
					return;
				}
				//TODO
				SharedPreferences sp = getContext().getSharedPreferences(Constant.LOCAL_DATA, Activity.MODE_WORLD_READABLE);
				String productPassword = sp.getString("productPassword", "");
				//将序列号解密
				try {
					productPassword = SimpleCrypto.decrypt(Constant.PASSWORD_SimpleCrypto, productPassword);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(password.equals(productPassword)) {
					Toast.makeText(getContext(), "恭喜！登录成功！", 0).show();
					mApp.setProductPassword(productPassword);
					String android_id = KingPadStudyActivity.getAndroidId();
					mApp.setProductNumber(android_id);
					//关闭输入键盘
					((InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(mPassword.getWindowToken() , 0);
					((KingPadStudyActivity)getContext()).showMain();
				}
				else {
					Toast.makeText(getContext(), "用户名或密码错误 ", 0).show();
				}
			}
			if(v == mQuitBtn) {
				//TODO
				((InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(mPassword.getWindowToken() , 0);
				((KingPadStudyActivity)getContext()).enterMainView();
			}
		}
	};
	
	
	
	

}
