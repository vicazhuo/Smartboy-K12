package com.kingPadStudy.views;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bean.BasicBean;
import com.bean.Chinese_memory_bean;
import com.data.MetaUtilData;
import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.constant.Constant;
import com.kingPadStudy.controller.ViewController;
import com.kingPadStudy.resource.ResourceLoader;
import com.kingpad.R;
import com.meta.Data;
import com.meta.Intro;
import com.meta.Item;
import com.meta.Items;
import com.meta.Question;
import com.meta.impl.MetaUtil;
import com.net.LoadData;
import com.net.RequestListener;
import com.net.RequestParameter;
import com.utils.Util;
import com.views.GameView;

public class EnglishMemoryView extends RelativeLayout {

	
	
	/**
	 * 函数作用：开始
	 * void
	 * @param path
	 * @param type
	 */
	public void setStart(String path,String type){
		com.utils.Util.initSoundPool(context);
		intiViews();
		setListener();
		LoadData(path,type);
	}
	
	/**
	 * 函数作用：开始学习
	 * void
	 */
	private void startStudy() {
		isItemsStart = true;
		// 隐藏题目控件
        showQuestion(false);
		//隐藏题控
		showNextPrevious(false);
		//播放引导MP3
		try {
			text_yindao.setText(text_intro.get(index_current_items));
			com.utils.Util.playSound_OnComplete(mp3_intro.get(index_current_items));
			com.utils.Util.player_onComplete.setOnCompletionListener(new OnCompletionListener()    
            {
                public void onCompletion(MediaPlayer arg0)
                {
                	//显示文字
                	playQuestion(index_current_ju);
					canTouch = true;
                }
            });   
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 函数作用：装载数据
	 * void
	 */
	private void LoadData(String path,String type) {
		  //解析index记录
	      RequestParameter parameter = new RequestParameter();
	      parameter.add("type", type); 
	      parameter.add("path", path); 
	      LoadData.loadData(Constant.META_UTIL_DATA, parameter, new RequestListener() {
				public void onError(String error) {
					
				}
				public void onComplete(Object obj) {
					//System.out.println("解析完毕。。。。onComplete");
					MetaUtilData Utildata = (MetaUtilData) obj;
					MetaUtil.printData(Utildata.getData());
					Data data = Utildata.getData();
					int index_load = 0;
					num_items = 0;
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
							//System.out.println("恭喜！成功获取背景："+bg);
						}
					}
					//资源路径
					ArrayList<Items> itemsList = data.getItemsList();
					for(int i = 0; i < itemsList.size(); i++) {
						items_index[num_items++] = index_load;
						Items items = itemsList.get(i);
						Intro intro= items.getIntro();
						if(intro != null) {
							for(int j  = 0; j < intro.getAttributeCount(); j++) {
								if(intro.getAttribueName(j).equals("soundFile")){
									//System.out.println("恭喜！成功获取引导MP3："+intro.getAttributeValue(j));
									mp3_intro.add(intro.getAttributeValue(j));
									break;
								}
							}
							BasicBean bean = intro.getBasicBean(); 	
							//System.out.println("恭喜！成功获取引导文字"+bean.getOwnText());
							text_intro.add(bean.getOwnText());	 
						}
						
						ArrayList<Item> itemList = items.getItemList();
						for(int j = 0; j < itemList.size(); j++) {
							Item item = itemList.get(j);
							index_load ++;
							if(item.getQuestionList() != null) {
								for(int m = 0; m < item.getQuestionList().size(); m++) {
									Question question = item.getQuestionList().get(m);
									for(int n  = 0; n < question.getAttributeCount(); n++) {
										if(question.getAttribueName(n).equals("soundFile")){
											String question_mp3 = question.getAttributeValue(n);
											mp3_question.add(question_mp3);
											total_question ++ ;
										}
									}
									ArrayList<BasicBean> list = question.getList();
									if(list != null){
										for(int n = 0; n < list.size(); n++) {
											BasicBean bean_B = list.get(n);
											if(n == 0){
												//图片
												if(bean_B.getPath()==null){
													continue;
												}
												path_image.add(bean_B.getPath());
												width_image.add(bean_B.getWidth_img());
												height_image.add(bean_B.getHeight_img());
//												System.out .println("找到图片："+bean_B.getPath());
											}else if (n==1){
												//左边红字
												text_red_left.add(bean_B.getOwnText());
												color_red_left.add(Color.parseColor(bean_B.getColor()));
												size_red_left.add(Integer.parseInt(bean_B.getFontSize()));
												System.out .println("找到左边红字："+bean_B.getOwnText());
											}else if(n ==2){
												//右边红字
												text_red_right.add(bean_B.getOwnText());
												color_red_right.add(Color.parseColor(bean_B.getColor()));
												size_red_right.add(Integer.parseInt(bean_B.getFontSize()));
												System.out .println("找到右边文字："+bean_B.getOwnText());
											}else if(n ==3){
												if(bean_B.getOwnText()==null){
													continue;
												}
												//下边红字
												text_black.add(bean_B.getOwnText());
												color_black.add(Color.parseColor(bean_B.getColor()));
												size_black.add(Integer.parseInt(bean_B.getFontSize()));
												System.out .println("找到下边文字："+bean_B.getOwnText());
											}
										}
									}
								}
							}
						}
					} // items
					//当前字
					index_current_ju = 0;
					//开始学习
					startStudy();
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
		mBackView.setOnClickListener(listener);
		button_prvious.setOnClickListener(listener);
		button_next.setOnClickListener(listener);
	}
	
	class ButtonListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			//System.out.println("点击...");
			if (v == button_prvious){
				if(canTouch){
					canTouch = false;
					index_current_ju --;
					if(index_current_ju <0){
						index_current_ju ++;
						canTouch = true;
					}else{
						Util.playSoundPool(context, Constant.AUDIO_MARK_BUTTON);
						//点击上一题
						//展示动画
						Util.stopGendu();
						if(isChangeIndex(index_current_ju,false)){
							startStudy();
						}else{
							playQuestion(index_current_ju);
							canTouch = true;
						}
					}
				}
			}else if (v == button_next){
				if(canTouch){
					//System.out.println("点击下一题。。。index_current_ju="+index_current_ju+",total_question="+total_question);
					canTouch = false;
					index_current_ju ++;
					if(index_current_ju > total_question -1){
						index_current_ju --;
						canTouch = true;
					}else{
						Util.playSoundPool(context, Constant.AUDIO_MARK_BUTTON);
						//点击下一题
						//展示动画
						if(isChangeIndex(index_current_ju,true)){
							startStudy();
						}else{
							playQuestion(index_current_ju);
							canTouch = true;
						}
					}
				}
			}else if (v==mBackView){
				Util.playSoundPool(context, Constant.AUDIO_MARK_BUTTON);
				//点击了返回按钮
				//System.out.println(" 点击返回");
				Util.clearMp3();
				ViewController.backToNormal_from_relative();
				ResourceLoader.refreshMenuView();
				if (bmp_bg!= null && !bmp_bg.isRecycled()) {
					bmp_bg.recycle();
				}
				if (bmp_bg_small!= null && !bmp_bg_small.isRecycled()) {
					bmp_bg_small.recycle();
				}
				if (bmp_show != null && !bmp_show.isRecycled()) {
					bmp_show.recycle();
				}
			} 
		}
	}
	
	
	/**
	 * 函数作用：播放某个题目
	 * void
	 * @param num
	 */
	private void playQuestion(final int num){
		isPalyTwo = false;
		com.utils.Util.stopGendu();
		com.utils.Util.playSound_Gendu(mp3_question.get(num));
		com.utils.Util.palyer_gendu_two.setOnCompletionListener(new OnCompletionListener()    
        {   
             public void onCompletion(MediaPlayer arg0)   
             {
            	 try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
             	//显示文字
            	 if(!isPalyTwo){
            		 if(!isItemsStart){
            			 playQuestion(num);
            			 isPalyTwo = true;
            		 }else{
            			 isItemsStart = false;
            		 }
            	 }
             }
        });   
		
		if(path_image!= null && index_current_ju<path_image.size() && path_image.get(index_current_ju)!=null ){
			//显示图片
	        File file=new File(path_image.get(index_current_ju));
	        if (file.exists()) {//若该文件存在
	        	bmp_show = BitmapFactory.decodeFile(path_image.get(index_current_ju));
	        	image_show.setImageDrawable(new BitmapDrawable(bmp_show));
	        	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
	  				width_image.get(index_current_ju) , height_image.get(index_current_ju)      
	        	); 
	        	image_show.setLayoutParams(params);  
	        }
		}
		//显示文字
        textview_red_left.setText(text_red_left.get(index_current_ju));
        textview_red_left.setTextColor(color_red_left.get(index_current_ju));
        textview_red_left.setTextSize(size_red_left.get(index_current_ju));
        textview_red_right.setText(text_red_right.get(index_current_ju));
        textview_red_right.setTextColor(color_red_right.get(index_current_ju));
        textview_red_right.setTextSize(size_red_right.get(index_current_ju));
        if(text_black.size()!=0){
	        textview_black.setText(text_black.get(index_current_ju));
	        textview_black.setTextColor(color_black.get(index_current_ju));
	        textview_black.setTextSize(size_black.get(index_current_ju));
        }else{
        	layout_text.setOrientation(LinearLayout.VERTICAL);
        }
        showQuestion(true);
        //显示提控
		showNextPrevious(true);
	}
	 
	private void showQuestion(boolean isvisible){
		if(isvisible){
			textview_red_left.setVisibility(View.VISIBLE);
			textview_red_right.setVisibility(View.VISIBLE);
			textview_black.setVisibility(View.VISIBLE);
			image_show.setVisibility(View.VISIBLE);
		}else{
			textview_red_left.setVisibility(View.INVISIBLE);
			textview_red_right.setVisibility(View.INVISIBLE);
			textview_black.setVisibility(View.INVISIBLE);
			image_show.setVisibility(View.INVISIBLE);
		}
	}
	
	

	/**
	 * 函数作用：
	 * boolean
	 * @param index_current_ju2
	 * @param b
	 * @return
	 */
	public boolean isChangeIndex(int num, boolean b) {
		if(b){
			//点击下一题
			for(int i=0;i<num_items;i++){
				if(num == items_index[i]){
					//System.out.println("找到第+"+i+"个items.  items_index["+i+"]"+items_index[i]);
					//如果题号刚好为某个items的开始下标 
					index_current_items = i;
					return true;
				}
			}
		}else{
			//点击上一题
			for(int i=0;i<num_items;i++){
				if(num == items_index[i]-1 ){
					//如果题号刚好为某个items的开始下标 
					index_current_items = i-1;
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 函数作用：开启视图
	 * void
	 */
    private void intiViews() {
    	/*
		 * 背景
		 */  
		layout_bg_screen = (RelativeLayout) findViewById(R.id.bg_flash_screen);
		String file_imageString = ResourceLoader.getMajorBackImageUrl(2);
        File file=new File(file_imageString);
        if (file.exists()) {//若该文件存在
        	bmp_bg = BitmapFactory.decodeFile(file_imageString);
        	layout_bg_screen.setBackgroundDrawable(new BitmapDrawable(bmp_bg));
        }
        flashBgLayout = (RelativeLayout) findViewById(R.id.bg_object_small);
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
		/*
		 * 上下题
		 */
		button_prvious = (GameView)findViewById(R.id.button_last);
		button_next = (GameView)findViewById(R.id.button_next);

		button_prvious.addDrawable(R.drawable.button_previous, 200);
		button_prvious.addDrawable(R.drawable.button_previous2, 200);
		button_prvious.addDrawable(R.drawable.button_previous, 200);
	    /*
	     *  引导文字
	     */
		text_yindao = (TextView)findViewById(R.id.text_yindao);
	    /*
	     * 题目
	     */
		image_show = (ImageView)findViewById(R.id.image_show);
	    textview_red_left = (TextView)findViewById(R.id.text_red_left);
	    textview_red_right = (TextView)findViewById(R.id.text_red_right);
	    textview_black = (TextView)findViewById(R.id.text_black);
	    layout_text = (LinearLayout)findViewById(R.id.layout_text);
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
		}else if(index_current_ju == total_question - 1){
			//最后一题，不显示下一题
			button_prvious.setVisibility(View.VISIBLE);
			button_next.setVisibility(View.INVISIBLE);
		}else{
			button_prvious.setVisibility(View.VISIBLE);
			button_next.setVisibility(View.VISIBLE);
		}
	}
	 
 
	public EnglishMemoryView(Context context) {
		super(context);
	}

	public EnglishMemoryView(Context context,AttributeSet set) {
		super(context,set);
		this.context =context;
	}
	
	/*
     * 返回按钮
     */
    private GameView mBackView;
    /*
     * 引导文字
     */
    private TextView text_yindao;
    /*
     * 题目视图控件
     */
    private ImageView image_show;
    private Bitmap bmp_show;
    private LinearLayout layout_text;
    private TextView textview_red_left;
    private TextView textview_red_right;
    private TextView textview_black;
    
	/*
	 * 上下题	
	 */
	private GameView button_prvious;
	private GameView button_next; 
	/*
	 * 背景
	 */
	RelativeLayout layout_bg_screen ;
    RelativeLayout flashBgLayout  ;
    String bg;
    /*
     * 跟踪布局
     */
    private LinearLayout jump_layout ; 
	/*
	 * 上下文环境
	 */
	private Context context;
	/*
	 * 介绍文字和MP3
	 */
	private ArrayList<String> text_intro = new ArrayList<String>();
	private ArrayList<String> mp3_intro = new ArrayList<String>();

	/*
	 * 题目数据
	 */
	//声音
	private ArrayList<String> mp3_question = new ArrayList<String>();
	//图片
	private ArrayList<String> path_image = new ArrayList<String>();
	private ArrayList<Integer> width_image = new ArrayList<Integer>();
	private ArrayList<Integer> height_image = new ArrayList<Integer>();
	//左边红字
	private ArrayList<String> text_red_left = new ArrayList<String>();
	private ArrayList<Integer> color_red_left = new ArrayList<Integer>();
	private ArrayList<Integer> size_red_left = new ArrayList<Integer>();
	//右边红字
	private ArrayList<String> text_red_right = new ArrayList<String>();
	private ArrayList<Integer> color_red_right = new ArrayList<Integer>();
	private ArrayList<Integer> size_red_right = new ArrayList<Integer>();
	//黑字
	private ArrayList<String> text_black = new ArrayList<String>();
	private ArrayList<Integer> color_black = new ArrayList<Integer>();
	private ArrayList<Integer> size_black = new ArrayList<Integer>();
	
	/*
	 * 当前题号
	 */
	private int index_current_ju = 0;
	/*
	 * 当前items
	 */
	private int index_current_items = 0;
	private int[] items_index = new int[3]; 
	private int num_items = 0;
	/*
	 * 总共字
	 */
	private int total_question = 0 ;
	
	private boolean isPalyTwo = false;
	/*
	 */
	private Bitmap bmp_bg = null;
	private Bitmap bmp_bg_small = null;
	/*
	 * 是否可以点击
	 */
	private boolean canTouch = true;
	/*
	 * 是否开启了新的items
	 */
	private boolean isItemsStart = false;
	
}
