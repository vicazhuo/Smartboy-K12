package com.kingPadStudy.views;

import java.io.File;
import java.util.ArrayList;

import com.data.MetaUtilData;
import com.data.PoemData;
import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.constant.Constant;
import com.kingPadStudy.controller.ViewController;
import com.kingPadStudy.resource.ResourceLoader;
import com.kingpad.R;
import com.meta.Data;
import com.meta.Item;
import com.meta.Items;
import com.meta.impl.MetaUtil;
import com.utils.*;
import com.views.GameView;

import android.R.color;
import android.R.integer;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PoemReciteView extends RelativeLayout {

	public PoemReciteView(Context context) {
		super(context);
		this.context =context;
	}

	public PoemReciteView(Context context,AttributeSet set) {
		super(context,set);
		this.context =context;
	}
	
	/**
	 * 函数作用：传入必需参数
	 * void
	 */
	public void setStart(String path,String type){
		Util.initSoundPool(context);
		intiViews();
		setListener();
		LoadData(path,type);
	}
	
	
	/**
	 * 函数作用：装载数据
	 * void
	 */
	private void LoadData(String path,String type) {
		  //解析index记录
		com.net.RequestParameter parameter = new com.net.RequestParameter();
	      parameter.add("type", type); 
	      parameter.add("path", path); 
	      com.net.LoadData.loadData(Constant.META_UTIL_DATA, parameter, new com.net.RequestListener() {
				public void onError(String error) {
					
				}
				public void onComplete(Object obj) {
					MetaUtilData Utildata = (MetaUtilData) obj;
					MetaUtil.printData(Utildata.getData());
					Data data = Utildata.getData();
					/*
					 * 找到数据
					 */
					//背景图
					for(int i = 0; i < data.getAttributeCount(); i++) {
						if(data.getAttribueName(i).equals("bg")){
							bg = data.getAttributeValue(i);
							 File mfile=new File(bg);
						        if (mfile.exists()) {//若该文件存在
						        	bmp_bg_small = BitmapFactory.decodeFile(bg);
						            flashBgLayout.setBackgroundDrawable(new BitmapDrawable(bmp_bg_small));  
						        }
							////System.out.println("恭喜！成功获取背景："+bg);
						}else if(data.getAttribueName(i).equals("title")){
							title = data.getAttributeValue(i);
						}
					}
					//资源路径
					ArrayList<Items> itemsList = data.getItemsList();
					for(int i = 0; i < itemsList.size(); i++) {
						Items items = itemsList.get(i);
						ArrayList<Item> itemList = items.getItemList();
						for(int j = 0; j < itemList.size(); j++) {
							Item item = itemList.get(j);
							for(int k  = 0; k < item.getAttributeCount(); k++) {
								if(item.getAttribueName(k).equals("sourceFile")){
									path_xml = item.getAttributeValue(k);
									//System.out.println("恭喜！成功获取xml路径："+path_xml);
									/*
									 * 解析古诗
									 */
									com.net.RequestParameter parameter = new com.net.RequestParameter();
								      parameter.add("path", path_xml); 
								      com.net.LoadData.loadData(Constant.POEM_DATA, parameter, new com.net.RequestListener() {
											public void onError(String error) {
											}
											public void onComplete(Object obj) {
												PoemData data = (PoemData) obj;
												author = data.getAuthor();
												view_title.setText(title);
												view_autho.setText(author);
												LinearLayout layout  = (LinearLayout) findViewById(R.id.layout);
												ArrayList<String> contentList = data.getContentList();
												int size = contentList.size();
												for(int i=0;i<size;i++){
													/*
													 * 将解析的古诗放入对应的数组，并放入界面
													 */
													String text = contentList.get(i);
													//System.out.println("第"+i+"句:"+text);
													TextView textView2 = new TextView(context);
											        textView2.setText(text);
											        textView2.setTextColor(Color.BLACK);
											        textView2.setTextSize(30);
											        layout.addView(textView2);
											        list_poems.add(textView2);
													content.add(text);
												}
												/*
												 * 初始化跟踪数组
												 */
												state_recite = new int[size];
												length_poem = size;
											}
								     });
								}
							}
						}
					} // items
			        KingPadStudyActivity.dismissWaitDialog();
				}
	     });
	}

	
	/**
	 * 函数作用：设置监听器
	 * void
	 */
	private void setListener() {
		ButtonListener listener = new ButtonListener();
		button_gendu.setOnClickListener(listener);
		button_danju.setOnClickListener(listener);
		button_shuangju.setOnClickListener(listener);
		button_quanwen.setOnClickListener(listener);
		
		mBackView.setOnClickListener(listener);
		button_ok.setOnClickListener(listener);
		button_puzzle.setOnClickListener(listener);
		button_no.setOnClickListener(listener);
		button_prvious.setOnClickListener(listener);
		button_next.setOnClickListener(listener);
	}

	/**
	 * 函数作用：初始化视图控件
	 * void
	 */
	private void intiViews() {
		/*
		 * 背景
		 */  
		layout_bg_screen = (RelativeLayout) findViewById(R.id.bg_flash_screen);
		String file_imageString = ResourceLoader.getMajorBackImageUrl(5);
        File file=new File(file_imageString);
        if (file.exists()) {//若该文件存在
        	bmp_bg = BitmapFactory.decodeFile(file_imageString);
        	layout_bg_screen.setBackgroundDrawable(new BitmapDrawable(bmp_bg));
        }
        flashBgLayout = (RelativeLayout) findViewById(R.id.flash_bg);
        /*
         * 灯
         */
        view_deng = (GameView)findViewById(R.id.light);
        /*
         * 返回按钮
         */
		mBackView = (GameView) findViewById(R.id.button_back);
		mBackView.addDrawable(R.drawable.back_default, 1000);
		mBackView.addDrawable(R.drawable.back_hot, 1000);
		mBackView.play(false);
		
        /*
         * 跟踪布局
         */
        jump_layout = (LinearLayout) findViewById(R.id.jump_layout);
        check_layout = (LinearLayout) findViewById(R.id.check_layout);
		/*
		 * 四个按钮
		 */
		button_gendu = (ImageButton) findViewById(R.id.button_gendu);
		button_danju = (ImageButton) findViewById(R.id.button_danju);
		button_shuangju = (ImageButton) findViewById(R.id.button_shuangju);
		button_quanwen = (ImageButton) findViewById(R.id.button_quanwen);
		/*
		 * 诗句线性布局
		 */
		layout_poem = (LinearLayout) findViewById(R.id.linear_text);
		list_poems = new ArrayList<TextView>();
		TextView textView = new TextView(context);
		textView.setTextColor(color.black);
		textView.setTextSize(30);
		textView.setText("d");
		layout_poem.addView(textView);
		/*
		 * 跟踪小人
		 */
		button_ok = (GameView)findViewById(R.id.check_good);
		button_puzzle = (GameView)findViewById(R.id.check_just);
		button_no = (GameView)findViewById(R.id.check_bad);
		
		button_ok.addDrawable(R.drawable.button_good1, 200);
		button_ok.addDrawable(R.drawable.button_good2, 200);
		button_ok.addDrawable(R.drawable.button_good1, 200);
		
		button_puzzle.addDrawable(R.drawable.button_puzzle1, 200);
		button_puzzle.addDrawable(R.drawable.button_puzzle2, 200);
		button_puzzle.addDrawable(R.drawable.button_puzzle1, 200);
		
		button_no.addDrawable(R.drawable.button_bad1, 200);
		button_no.addDrawable(R.drawable.button_bad2, 200);
		button_no.addDrawable(R.drawable.button_bad3, 200);
		button_no.addDrawable(R.drawable.button_bad1, 200);
		
		/*
		 * 上下题
		 */
		button_prvious = (GameView)findViewById(R.id.button_last);
		button_next = (GameView)findViewById(R.id.button_next);

		button_prvious.addDrawable(R.drawable.button_previous, 200);
		button_prvious.addDrawable(R.drawable.button_previous2, 200);
		button_prvious.addDrawable(R.drawable.button_previous, 200);
		
		button_puzzle.addDrawable(R.drawable.button_puzzle1, 200);
		button_puzzle.addDrawable(R.drawable.button_puzzle2, 200);
		button_puzzle.addDrawable(R.drawable.button_puzzle1, 200);
		
		/*
		 *  标题和作者
		 */
		view_title = (TextView)findViewById(R.id.title);
		view_autho = (TextView)findViewById(R.id.author);
	}

	

	/**
	 * 函数作用:处理动画
	 * void
	 * @param button_ok2
	 */
	public void handleAnimation(final GameView animation,final int type) {
		  int duration = 0;
		  AnimationDrawable animationDrawable = animation.getFrameAnimationDrawable();
        for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
                duration += animationDrawable.getDuration(i);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
                public void run() {
                	switch(type){
                	case 1:
                		//OK按钮
	    				// 跟踪不显示
		    			showGenzong(false);
		  				// 显示题目控制
		  				showNextPrevious(true);
		  				// 灯
		  				showLight(true,1);
                		break;
                	case 2:
                		//模糊按钮
        				// 跟踪不显示
        				showGenzong(false);
        				// 显示题目控制
        				showNextPrevious(true);
        				// 灯
        				showLight(true,2);
                		break;
                	case 3:
                		//不会按钮
        				// 跟踪不显示
        				showGenzong(false);
        				// 显示题目控制
        				showNextPrevious(true);
        				// 灯
        				showLight(true,3);
                		break;
                	case 4:
        				//显示上一题的
        				if(state_recite[index_current_ju] == 0 ){
        					//上一题还没有做过
        					// 跟踪显示
        					showGenzong(true);
        					// 题目控制消失
        					showNextPrevious(false);
        					//灯消失
        					showLight(false, 0);
        				}else{
        					//这个题已经做过
        					// 跟踪不显示
        					showGenzong(false);
        					// 题目控制显示
        					showNextPrevious(true);
        					//显示灯
        					showLight(true, state_recite[index_current_ju]);
        				}
                		break;
                	case 5:
        				if(state_recite[index_current_ju] == 0 ){
        					//这个题还没有做过
        					// 跟踪显示
        					showGenzong(true);
        					// 题目控制消失
        					showNextPrevious(false);
        					//灯消失
        					showLight(false, 0);
        				}else{
        					//这个题已经做过
        					// 跟踪不显示
        					showGenzong(false);
        					// 题目控制显示
        					showNextPrevious(true);
        					//显示灯
        					showLight(true, state_recite[index_current_ju]);
        				}
                		break;
                	}
                	animation.getFrameAnimationDrawable().stop();
					canTouchButton = true;
                }
        }, duration);		
	}
	
	
	class ButtonListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			//System.out.println("点击/...index_current_ju="+index_current_ju);
			if (v == button_gendu){
				index = 0;
				//不显示跟踪
				showGenzong(false);
				//不显示题目控制
				showNextPrevious(false);
				//不显示灯
				showLight(false, 0);
				//消除下划线
				setLine(4);
			}else if (v == button_danju){
				index = 1;
				index_current_ju = 0;
				//重置跟踪数据
				state_recite = new int[length_poem];
				//将单句改造成为下划线
				setLine(1);
				//显示跟踪
				showGenzong(true);
				//不显示题目控制
				showNextPrevious(false);
				//不显示灯
				showLight(false, 0);
				//总共体题数
				total_ju = (length_poem+1) / 2;
			}else if (v == button_shuangju){
				index = 2;
				index_current_ju = 0;
				//重置跟踪数据
				state_recite = new int[length_poem];
				//将双句改造成为下划线
				setLine(2);
				//显示跟踪
				showGenzong(true);
				//不显示题目控制
				showNextPrevious(false);
				//不显示灯
				showLight(false, 0);
				//总共体题数
				total_ju = length_poem / 2;
			}else if (v == button_quanwen){
				index = 3;
				//重置跟踪数据
				index_current_ju = 0;
				state_recite = new int[length_poem];
				//全部设置下划线
				setLine(3);
				//显示跟踪
				showGenzong(true);
				//不显示题目控制
				showNextPrevious(false);
				//不显示灯
				showLight(false, 0);
				//总共体题数
				total_ju = length_poem;
			}else if (v == button_ok){
				if(canTouchButton){
					canTouchButton = false;
					Util.playSoundPool(context, Constant.AUDIO_MARK_BUTTON);
					//System.out.println("点击没问题");
					// 点击没问题
					//用绿色的字填充对应的空
					fillLine( Color.rgb(0, 127, 0));
					// 记录数组
					state_recite[index_current_ju] = 1;
					//展示动画
					button_ok.play(true);  
					handleAnimation(button_ok,1);
				}
			}else if (v == button_puzzle){
				if(canTouchButton){
					canTouchButton = false;
					Util.playSoundPool(context, Constant.AUDIO_MARK_BUTTON);
					//点击模糊
					//展示动画
					fillLine(Color.rgb(248, 208, 128));
					state_recite[index_current_ju] = 2;
					button_puzzle.play(false); 
					handleAnimation(button_puzzle,2);
				}
			}else if (v == button_no){
				if(canTouchButton){
					canTouchButton = false;
					Util.playSoundPool(context, Constant.AUDIO_MARK_BUTTON);
					//点击完全不会
					fillLine(Color.rgb(246, 75, 45));
					state_recite[index_current_ju] = 3;
					button_no.play(false); 
					handleAnimation(button_no,3);
				}
			}else if (v == button_prvious){
				//System.out.println("canTouchButton="+canTouchButton);
				if(canTouchButton){
					canTouchButton = false;
					index_current_ju --;
					if(index_current_ju >=0){
						Util.playSoundPool(context, Constant.AUDIO_MARK_BUTTON);
						//点击上一题
						//展示动画
						//展示动画
						button_prvious.play(false);
						handleAnimation(button_prvious,4);
					}else{
						index_current_ju ++;
						canTouchButton = true;
					}
				}
			}else if (v == button_next){
				
				//System.out.println("canTouchButton="+canTouchButton);
				
				if(canTouchButton){
					canTouchButton = false;
					index_current_ju ++;
					if(index_current_ju <= total_ju-1){
						Util.playSoundPool(context, Constant.AUDIO_MARK_BUTTON);
						//点击下一题
						//展示动画
						button_next.play(true);
						handleAnimation(button_next,5);
					} else{
						index_current_ju ++;
						canTouchButton = true;
					}
				}
			}else if (v==mBackView){
				Util.playSoundPool(context, Constant.AUDIO_MARK_BUTTON);
				//点击了返回按钮
				//System.out.println(" 点击返回");
				ViewController.backToNormal_from_relative();
				//回收内存
				if(bmp_bg!= null && !bmp_bg.isRecycled()){
					bmp_bg.recycle();
				}
				if(bmp_bg_small!= null && !bmp_bg_small.isRecycled()){
					bmp_bg_small.recycle();
				}
			}
		}

		
	}
	
	/**
	 * 函数作用：改变跟踪布局的显示属性
	 * void
	 * @param b
	 */
	private void showGenzong(boolean b) {
		if(b){
	        check_layout.setVisibility(View.VISIBLE);
		}else{
	        check_layout.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * 函数作用：显示灯
	 * void
	 * @param i
	 */
	public void showLight(boolean isshow,int i) {
		//1  绿灯， 2 黄灯  3 红灯
		if(isshow){
    		view_deng.clear();
			switch(i){
		    	case 1:
		        	view_deng.addDrawable(R.drawable.light_green1, 300);
		        	view_deng.addDrawable(R.drawable.light_green2, 300);
		        	view_deng.play(false);
		    		break;
		    	case 2:
		        	view_deng.addDrawable(R.drawable.light_yellow1, 300);
		        	view_deng.addDrawable(R.drawable.light_yellow2, 300);
		        	view_deng.play(false);
		    		break;
		    	case 3:
		        	view_deng.addDrawable(R.drawable.light_red1, 300);
		        	view_deng.addDrawable(R.drawable.light_red2, 300);
		        	view_deng.play(false);
		    		break;
			}
			view_deng.setVisibility(View.VISIBLE);
		}else{
			view_deng.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 函数作用：改变题目控制布局的显示属性
	 * void
	 * @param b
	 */
	public void showNextPrevious(boolean b) {
		if(b){
			jump_layout.setVisibility(View.VISIBLE);
		}else{
			jump_layout.setVisibility(View.INVISIBLE);
		}
		
		// 根据题号，确定显示按钮
		if(index_current_ju == 0 ){
			//第一题，不显示上一题
			button_prvious.setVisibility(View.INVISIBLE);
			button_next.setVisibility(View.VISIBLE);
		}else if(index_current_ju == total_ju - 1){
			//最后一题，不显示下一题
			button_prvious.setVisibility(View.VISIBLE);
			button_next.setVisibility(View.INVISIBLE);
		}else{
			button_prvious.setVisibility(View.VISIBLE);
			button_next.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 函数作用：根据当前的视图下标，获取应该填充的位置
	 * int
	 * @param index2
	 * @return
	 */
	public int getCurrentLine(int index2,int current_ju) {
		//System.out.println("index="+index2);
		//System.out.println("current_ju="+current_ju);
		switch(index2){
		//1 为单，2为双，3为全文
		case 1:
			return current_ju*2 + 1;
		case 2:
			return (current_ju+1)*2;
		case 3:
			return current_ju+1;
		}
		return 0;
	}

	/**
	 * 函数作用：用一种颜色的字体填充空缺
	 * void
	 * @param green
	 */
	public void fillLine(int color) {
		//System.out.println("index=..."+index);
		int current_line = getCurrentLine(index,index_current_ju);
		TextView tvTextView = list_poems.get(current_line-1);
		tvTextView.setText(content.get(current_line-1));
		tvTextView.setTextColor(color);
		postInvalidate();
	}

	/**
	 * 函数作用：设置下划线
	 * void
	 * @param type  1为单句 2为双句 3为全文 4为跟读
	 */
	public void setLine(int type) {
		int size = list_poems.size();
		switch(type){
			case 1: 
				for(int i=0;i<size;i++){
					TextView tv = list_poems.get(i);
					tv.setTextColor(Color.BLACK);
					String text = content.get(i);
					if(i%2==0){
						int size_text = text.length();
						//System.out.println("text="+text);
						//System.out.println("size_text="+size_text);
						text = "";
						for(int j=0;j<size_text;j++){
							text+= "    ";
						}
						tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);  
					}else{
						tv.getPaint().setFlags(Paint.LINEAR_TEXT_FLAG);  
					}
					tv.setText(text);
				}
				break;
			case 2: 
				for(int i=0;i<size;i++){
					TextView tv = list_poems.get(i);
					tv.setTextColor(Color.BLACK);
					String text = content.get(i);
					if(i%2==1){
						int size_text = text.length();
						//System.out.println("text="+text);
						//System.out.println("size_text="+size_text);
						text = "";
						for(int j=0;j<size_text;j++){
							text+= "    ";
						}
						tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);  
					}else{
						tv.getPaint().setFlags(Paint.LINEAR_TEXT_FLAG);  
					}
					tv.setText(text);
				}
				break;
			case 3: 
				for(int i=0;i<size;i++){
					TextView tv = list_poems.get(i);
					tv.setTextColor(Color.BLACK);
					String text = content.get(i);
					int size_text = text.length();
					//System.out.println("text="+text);
					text = "";
					for(int j=0;j<size_text;j++){
						//System.out.println("text="+text);
						//System.out.println("size_text="+size_text);
						text+= "    ";
					}
					tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);  
					tv.setText(text);
				}
				break;
			case 4: 
				for(int i=0;i<size;i++){
					TextView tv = list_poems.get(i);
					tv.setTextColor(Color.BLACK);
					String text = content.get(i);
					tv.setText(text);
					tv.getPaint().setFlags(Paint.LINEAR_TEXT_FLAG);  
				}
		}
		postInvalidate();
	}


	/*
	 * 视图的当前位置下标
	 */
	private int index =0;
	
	/*
	 * 诗句 背诵记录 数组，每个值包括：1,2，3 分别代表 没问题，有点模糊，完全不懂 
	 */
	private int[] state_recite;
	/*
	 * 当前题号
	 */
	private int index_current_ju = 0;
	/*
	 * 总共题数
	 */
	private int total_ju = 0;
	
	private Context context ;	
	private ImageButton button_gendu ;	
	private ImageButton button_danju ;	
	private ImageButton button_shuangju ;	
	private ImageButton button_quanwen ;	
	/*	
	 * 诗句线性布局		
	 */	
	private LinearLayout layout_poem;
	/*	
	 * 跟踪小人	
	 */	
	private GameView button_ok;	
	private GameView button_puzzle;	
	private GameView button_no;	
	/*
	 * 上下题	
	 */
	private GameView button_prvious;
	private GameView button_next;
	/*
	 *  标题
	 */
	private TextView view_title;
	private TextView view_autho;
	
	private LinearLayout layout;
	/*
	 * 灯
	 */
	private GameView view_deng;
	
	/*
	 * 诗句TextView列表
	 */
	private ArrayList<TextView> list_poems;
	/*
	 * 古诗长度
	 */
	private int length_poem;
	
	private String title;
	private String bg;
	private String path_xml;
	private String author;
	private ArrayList<String> content = new ArrayList<String>();
	/*
	 * 背景
	 */
	RelativeLayout layout_bg_screen ;
    RelativeLayout flashBgLayout  ;
    /*
     * 跟踪布局
     */
    private LinearLayout jump_layout ;
    private LinearLayout check_layout ;
    /*
     * 返回按钮
     */
    private GameView mBackView;
	/*
	 * 背景Bitmap
	 */
	private Bitmap bmp_bg = null;
	private Bitmap bmp_bg_small = null;
	private boolean canTouchButton = true;
}
