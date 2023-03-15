/**
 * 
 */
package com.king;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kingpad.R;

/**
 * @author 陈相伯
 *
 */
public class HelpView extends LinearLayout{

	private TextView textView ;
	
	/**
	 * @param context
	 */
	public HelpView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.layout_help, this, true);
		setLink();
	}

	/**
	 * 
	 */
	private void setLink() {   
		textView = (TextView) this.findViewById(R.id.text_host);   
      //创建一个 SpannableString对象  
      SpannableString sp = new SpannableString("答：请点此访问我们的网站：http://www.rxrj.com.cn");   
      //设置超链接  
      sp.setSpan(new URLSpan("http://www.rxrj.com.cn"), 0, 35,   
              Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
//      //设置高亮样式一  
//      sp.setSpan(new BackgroundColorSpan(Color.RED), 13 ,35,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);   
      //设置高亮样式二  
      sp.setSpan(new ForegroundColorSpan(Color.GREEN),0,12,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);     
      //设置斜体  
      sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 13, 35, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);   
      //SpannableString对象设置给TextView  
      textView.setText(sp);   
      //设置TextView可点击  
      textView.setMovementMethod(LinkMovementMethod.getInstance());   
  }
	
	

}
