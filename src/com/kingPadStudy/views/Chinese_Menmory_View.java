package com.kingPadStudy.views;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class Chinese_Menmory_View extends RelativeLayout {

	public Chinese_Menmory_View(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public Chinese_Menmory_View(Context context,AttributeSet set) {
		super(context,set);
		this.context =context;
	}
	
	/**
	 * 函数作用：开始
	 * void
	 * @param path
	 * @param type
	 */
	public void setStart(String path,String type){
		//System.out.println("在setStart中。。。"+path);
		//System.out.println("在setStart中。。。"+type);
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
		// 隐藏中间文字
		text_dazi.setVisibility(View.INVISIBLE);
		text_dazi2.setVisibility(View.INVISIBLE);
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
		//System.out.println("在LoadData中。。。"+path);
		  //解析index记录
	      RequestParameter parameter = new RequestParameter();
	      //System.out.println();
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
					list_dazi = new ArrayList<Chinese_memory_bean>();
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
											list_mp3.add(question_mp3);
										}
									}
									ArrayList<BasicBean> list = question.getList();
									if(list != null){
										Chinese_memory_bean bean = new Chinese_memory_bean() ;
										ArrayList<String> list_text = new ArrayList<String>();
										ArrayList<Integer> list_size = new ArrayList<Integer>();
										//System.out.println("恭喜，寻找到文字：！！！");
										for(int n = 0; n < list.size(); n++) {
											BasicBean bean_B = list.get(n);
											list_text.add(bean_B.getOwnText());
											list_size.add(new Integer(bean_B.getFontSize()));
											//System.out.println("文字"+n+":"+bean_B.getOwnText());
										}
										bean.setList_text(list_text);
										bean.setList_textSize(list_size); 
										list_dazi.add(bean);
									}
								}
							}
						}
					} // items
					//总共字
					total_zi = list_dazi.size();
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
					//System.out.println("点击下一题。。。index_current_ju="+index_current_ju+",total_zi="+total_zi);
					canTouch = false;
					index_current_ju ++;
					if(index_current_ju > total_zi -1){
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
		com.utils.Util.playSound_Gendu(list_mp3.get(num));
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
		 
		//显示大字
		Chinese_memory_bean bean = list_dazi.get(num);
		ArrayList<String> list_text = bean.getList_text();
		ArrayList<Integer> list_size = bean.getList_textSize();
		int size = list_text.size();
		for(int i=0;i<size;i++){
			if(i==0){
				text_dazi.setText(list_text.get(i));
				text_dazi.setTextSize(list_size.get(i));
			}else{
				text_dazi2.setText(list_text.get(i));
				text_dazi2.setTextSize(list_size.get(i));
			}
		} 
		text_dazi.setVisibility(View.VISIBLE);
		text_dazi2.setVisibility(View.VISIBLE);
		//显示提控
		showNextPrevious(true);
	}
	 
	

	/**
	 * 函数作用：
	 * boolean
	 * @param index_current_ju2
	 * @param b
	 * @return
	 */
	public boolean isChangeIndex(int num, boolean b) {
		//System.out.println("isChangeIndex...num="+num);
		//System.out.println("items的数量："+num_items);
		for(int i=0;i<num_items;i++){
			//System.out.println("items_index["+i+"]="+items_index[i]);
		}
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
	     * 大字 
	     */
	    text_dazi = (TextView)findViewById(R.id.text_dazi);
	    text_dazi2 = (TextView)findViewById(R.id.text_dazi2);
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
		}else if(index_current_ju == total_zi - 1){
			//最后一题，不显示下一题
			button_prvious.setVisibility(View.VISIBLE);
			button_next.setVisibility(View.INVISIBLE);
		}else{
			button_prvious.setVisibility(View.VISIBLE);
			button_next.setVisibility(View.VISIBLE);
		}
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
     * 大字 
     */
    private TextView text_dazi;
    private TextView text_dazi2;
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
	 * 大字列表
	 */
	private ArrayList<Chinese_memory_bean> list_dazi = new ArrayList<Chinese_memory_bean>();
	private ArrayList<String> list_mp3 = new ArrayList<String>();
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
	private int total_zi ;
	
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
