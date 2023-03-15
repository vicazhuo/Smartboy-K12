package com.kingPadStudy.views;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.bean.BasicBean;
import com.bean.ContentStateBean;
import com.bean.ObjectiveQuizBean;
import com.bean.TopicBean;
import com.data.MetaUtilData;
import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.constant.Constant;
import com.kingPadStudy.controller.ViewController;
import com.kingPadStudy.resource.ResourceLoader;
import com.kingpad.R;
import com.meta.Answer;
import com.meta.Data;
import com.meta.Intro;
import com.meta.Item;
import com.meta.Items;
import com.meta.Option;
import com.meta.Question;
import com.meta.impl.MetaUtil;
import com.utils.DatabaseAdapter;
import com.net.LoadData;
import com.net.RequestListener;
import com.net.RequestParameter;
import com.utils.Util;
import com.views.GameView;

import android.R.bool;
import android.R.integer;
import android.R.layout;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.sax.Element;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
public class ObjectiveQuizView extends LinearLayout{
	/**
	 * 函数作用:开始运行
	 * void
	 * @param path
	 * @param type
	 */
	public void setStart(String path,String type,int index,int studyState) {
		this.path = path;
		this.type = type;
		this.index = index;
		this.studySate = studyState ;
		Util.initSoundPool(context);
		initView();
		setListener();
		setMode();
		loadData();
	}
	
	/**
	 * 函数作用:初始化学习模式
	 * void
	 */
	private void setMode() {
		/*
		 * 找到记录列表
		 */
		list_record = DatabaseAdapter.getInstance(context).getRecord(path);
		int size_done = 0 ;
		if(list_record != null){
			size_done = list_record.size();
		}
		if(studySate == 0){
			for(int i=0;i<size_done;i++){
				TopicBean bean = list_record.get(i);
				int state = bean.finalState; 
				switch(state){
				case 1:
					num_understand ++;
					break;
				case 2:
					num_puzzle ++;
					break;
				case 3:
					num_not_understand ++;
					break;
				}
			}
			textview_not_understand.setText("未掌握:"+num_not_understand);
			textview_understand.setText("已掌握:"+num_understand);
			textview_vague.setText("模    糊:"+num_puzzle);
			int content_state = DatabaseAdapter.getInstance(context).getLishtState(path);
			//根据目录状态，判断是否是循环学习
			if(content_state == 0){
				//普通学习
				Util.playBackGroundSound(context, "hint_remani_select.mp3",false);
				text_hint.setText(Constant.TEXT_REMAIN_SELECT);
			}else if(content_state == -1){
				//第一次进入
				Util.playBackGroundSound(context, "hint_to_learn.mp3",false);
				text_hint.setText(Constant.TEXT_BEGIN_TO_LEARN);
			}else if(content_state == 1){
				//浏览
				isReview = true;
				Util.playBackGroundSound(context, "hint_done.mp3",false);
				text_hint.setText(Constant.TEXT_ALL_UNDERSTAND);
			}else{
				//循环学习
				isFirstLearn = false;
				isFirstSection = true;
				isCircleStudy = true;
				Util.playBackGroundSound(context, "hint_remain_do.mp3",false);
				text_hint.setText("你已经完成筛查，有"+(num_not_understand+num_puzzle)+"个知识点没有掌握，你需要把它们再做2遍。");
			}
		}else{
			Util.playBackGroundSound(context, "hint_to_learn.mp3",false);
			text_hint.setText(Constant.TEXT_BEGIN_TO_LEARN);
			textview_not_understand.setText("未掌握:"+num_not_understand);
			textview_understand.setText("已掌握:"+num_understand);
			textview_vague.setText("模    糊:"+num_puzzle);
		}
		layout_hint.setVisibility(View.VISIBLE);
	}

	private void loadData(){
		 //解析index记录
	      RequestParameter parameter = new RequestParameter();
	      parameter.add("type", type); 
	      parameter.add("path", path); 
	      LoadData.loadData(Constant.META_UTIL_DATA, parameter, new RequestListener() {
				public void onError(String error) {
					
				}
				public void onComplete(Object obj) {
					MetaUtilData Utildata = (MetaUtilData) obj;
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
						}
					}
					
					//资源路径
					ArrayList<Items> itemsList = data.getItemsList();
					for(int i = 0; i < itemsList.size(); i++) {
						Items items = itemsList.get(i);
						Intro intro= items.getIntro();
						if(intro != null) {
							for(int j  = 0; j < intro.getAttributeCount(); j++) {
								if(intro.getAttribueName(j).equals("soundFile")){
									String mp3 = intro.getAttributeValue(j);
									mp3_guide.add(mp3);
									break;
								}
							}
							BasicBean bean = intro.getBasicBean(); 	
							String text = bean.getOwnText();
							text_guide.add(text);
							isFirstMp3LoadDone = true;
						}
						//题目
						ArrayList<Item> itemList = items.getItemList();
						//初始化一个items的题目列表
						ArrayList<ObjectiveQuizBean> problems = new ArrayList<ObjectiveQuizBean>(); 
						for(int j = 0; j < itemList.size(); j++) {
							Item item = itemList.get(j);
							/*
							 * 声明一道题的bean
							 */
							ObjectiveQuizBean objectiveQuizBean = new ObjectiveQuizBean();
							/*
							 * 问题数据挖掘
							 */
							ArrayList<String> question_text = null;
							ArrayList<Integer> question_size = null;
							ArrayList<Integer> question_color = null;
							String question_mp3 =  null; 		//为空时，表示没有
							if(item.getQuestionList() != null) {
								for(int m = 0; m < item.getQuestionList().size(); m++) {
									Question question = item.getQuestionList().get(m);
									for(int n  = 0; n < question.getAttributeCount(); n++) {
										if(question.getAttribueName(n).equals("soundFile")){
											question_mp3 = question.getAttributeValue(n);
											//System.out.println("找到MP3：question_mp3="+question_mp3);
											if(!"".equals(question_mp3)){
												Util.addMp3List(context, question_mp3);
											}
										}
									}
									ArrayList<BasicBean> list = question.getList();
									if(list != null){
										total_question ++;
										question_text = new ArrayList<String>();
										question_size = new ArrayList<Integer>(); //为空时，默认40
										question_color = new ArrayList<Integer>(); //为空时，默认Color.BLACK
										for(int n = 0; n < list.size(); n++) {
											BasicBean bean = list.get(n);
											question_text.add(bean.getOwnText());
											if(bean.getFontSize() != null){
												question_size.add(new Integer(bean.getFontSize()));
												question_color.add(Color.parseColor(bean.getColor()));
											}
										}
									}
								}
							}
							objectiveQuizBean.setQuestion_text(question_text);
							objectiveQuizBean.setQuestion_colr(question_color);
							objectiveQuizBean.setQuestion_size(question_size);
							objectiveQuizBean.setQuestion_mp3(question_mp3);
							/*
//							 * 选项数据挖掘									
							 */
							ArrayList<String> option_text = null;	
							ArrayList<Integer> option_size = null;	//为空时，默认25
							ArrayList<Integer> option_colr = null;	//为空时，默认Color.BLACK
							int right_anwser = 0;
							if(item.getOptionList() != null) {	
								option_text = new ArrayList<String>();
								option_size = new ArrayList<Integer>(); //为空时，默认40
								option_colr = new ArrayList<Integer>(); //为空时，默认Color.BLACK
								for(int m = 0; m < item.getOptionList().size(); m++) {
									Option answer = item.getOptionList().get(m);
									for(int n  = 0; n < answer.getAttributeCount(); n++) {
										if(answer.getAttribueName(n).equals("answer")){
											right_anwser = m;
										}
									}
									ArrayList<BasicBean> list = answer.getList();
									if(list != null){
										for(int n = 0; n < list.size(); n++) {
											BasicBean bean = list.get(n);
											option_text.add(bean.getOwnText());
											if(bean.getFontSize() != null){
												option_size.add(new Integer(bean.getFontSize()));
												option_colr.add(Color.parseColor(bean.getColor()));
											}
										}
									}
								}
							}
							objectiveQuizBean.setOption_text(option_text);
							objectiveQuizBean.setOption_size(option_size);
							objectiveQuizBean.setOption_colr(option_colr);
							objectiveQuizBean.setRight_anwser(right_anwser);
							/*
							 * 答案数据挖掘
							 */
							String answer_mp3 = null;	    //为空时，表示没有
							ArrayList<String> answer_text = null;	
							ArrayList<Integer> answer_size = null;		//为空时，默认25 
							ArrayList<Integer> answer_colr = null;		//为空时，默认Color.BLACK
							if(item.getAnswerList() != null) {
								for(int m = 0; m < item.getAnswerList().size(); m++) {
									Answer answer = item.getAnswerList().get(m);
									for(int n  = 0; n < answer.getAttributeCount(); n++) {
										if(answer.getAttribueName(n).equals("soundFile")){
											answer_mp3 = answer.getAttributeValue(n);
											if(!"".equals(answer_mp3)){
												Util.addAnswerMp3List(context, answer_mp3);
											}
										}
									}
									ArrayList<BasicBean> list = answer.getList();
									if(list != null){
										answer_text = new ArrayList<String>();
										answer_size = new ArrayList<Integer>(); //为空时，默认40
										answer_colr = new ArrayList<Integer>(); //为空时，默认Color.BLACK
										for(int n = 0; n < list.size(); n++) {
											BasicBean bean = list.get(n);
											answer_text.add(bean.getOwnText());
											if(bean.getFontSize() != null){
												answer_size.add(new Integer(bean.getFontSize()));
												answer_colr.add(Color.parseColor(bean.getColor()));
											}
										}
									}
								}
							} // if
							objectiveQuizBean.setAnswer_mp3(answer_mp3);
							objectiveQuizBean.setAnswer_text(answer_text);
							objectiveQuizBean.setAnswer_size(answer_size);
							objectiveQuizBean.setAnswer_colr(answer_colr);
							problems.add(objectiveQuizBean);
						}
						list_problems.add(problems);
					}
					isFirstMp3LoadDone = true;
					int size_done = 0;
					if(list_record != null){
						size_done = list_record.size();
					}
					isFirstInitQuestion = true;
					
					if(studySate == 0){
						/*
						 * 更新数字 和状态
						 */
						num_not_done = total_question - size_done;
						//当前题号
						index_current_question = 0;
						//状态
						state_question = new int[total_question];
						for(int i=0;i<size_done;i++){
							TopicBean bean = list_record.get(i);
							int number = bean.number;
							int state = bean.finalState; 
							state_question[number] = state;
						}
						state_circle_study1 = new int[total_question];
						state_circle_study2 = new int[total_question];
						benjiegongji = total_question;
						//循环学习数组
						circle_study = new boolean[total_question];
						/*
						 * 记分牌初始化
						 */
						//本节共计
						textview_total_count.setText("本节共计:"+benjiegongji);
						//未完成
						textview_not_avi.setText("未完成:"+num_not_done);
					}else{
						//重点复习 和 次重点复习
						/*
						 * 更新数字 和状态
						 */
						//状态
						state_question = new int[total_question];
						//循环学习数组
						circle_study = new boolean[total_question];
						total_current_circle = 0;
						for(int i=0;i<size_done;i++){
							TopicBean bean = list_record.get(i);
							int number = bean.number;
							int state = bean.firstState; 
							if(state == 1){
								if(studySate == 1){
									state_question[number] = -1;
									circle_study[number] = false;
								}else if(studySate == 2){
									state_question[number] = 0;
									circle_study[number] = true;
									total_current_circle ++ ;
								}
							}else{
								if(studySate == 1){
									state_question[number] = 0;
									circle_study[number] = true;
									total_current_circle ++;
								}else if(studySate == 2){
									state_question[number] = -1;
									circle_study[number] = false;
								}
							}
						}
						num_not_done = total_current_circle;
						//当前题号
						index_current_question = 0;
						state_circle_study1 = new int[total_question];
						state_circle_study2 = new int[total_question];
						benjiegongji = total_current_circle;
						//本节共计
						textview_total_count.setText("本节共计:"+benjiegongji);
						//未完成
						textview_not_avi.setText("未完成:"+num_not_done);
					}
					KingPadStudyActivity.dismissWaitDialog();
				}
			
	      });
	}


	/**
	 * 函数作用:开始学习
	 * void
	 */
	private void startStudy() {
		//System.out.println("startStudy");
		//显示主体
		layout_main.setVisibility(View.VISIBLE);
		//隐藏题控
		showNextPrevious(false);
		//隐藏题目、选项、答案
		question_layout.setVisibility(View.INVISIBLE);
		option_layout.setVisibility(View.INVISIBLE);
		answer_layout.setVisibility(View.INVISIBLE);
		//隐藏灯
		view_deng.setVisibility(View.INVISIBLE);
		//隐藏对错
		right_wrong.setVisibility(View.INVISIBLE);
		//隐藏喇叭
		button_laba.setVisibility(View.INVISIBLE);
		//播放引导MP3
		//显示引导文字
		intro_text.setText(text_guide.get(index_current_items));
		if(mp3_guide!=null && mp3_guide.size() != 0){
			com.utils.Util.playSound_OnComplete(mp3_guide.get(index_current_items));
		}else{
			if(index_current_items == 0){
				com.utils.Util.playSound_OnComplete(Constant.path_guide1);
			}else{
				com.utils.Util.playSound_OnComplete(Constant.path_guide2);
			}
		}
		com.utils.Util.player_onComplete.setOnCompletionListener(new OnCompletionListener()    
        {
            public void onCompletion(MediaPlayer arg0)   
            {
            	if(isFirstLearn){
            		//普通切换题目
					if(state_question[index_current_question] == 0 ){
						//还没有做过
						playQuestion(index_current_question);
					}else{
						//已经做过
						showDone(index_current_question);
					}
            	}else{
            		if(isFirstSection){
            			//普通切换题目
    					if(state_circle_study1[index_current_question] == 0 ){
    						//还没有做过
    						playQuestion(index_current_question);
    					}else{
    						//已经做过
    						showDone(index_current_question);
    					}
            		}else{
            			//普通切换题目
    					if(state_circle_study2[index_current_question] == 0 ){
    						//还没有做过
    						playQuestion(index_current_question);
    					}else{
    						//已经做过
    						showDone(index_current_question);
    					}
            		}
            	}
            	
            }
        });
	}
	
	
	/**
	 * 函数作用:寻找标号
	 * void
	 */
	private void findIndex(boolean isFromHeadToEnd) {
		if(isFromHeadToEnd){
			for(int i=0;i<total_question;i++){
				if(circle_study[i] == true){
					index_current_question = i;
					//找items下标
					index_current_items = findIndexItems(index_current_question);
					return ;
				}
			}
		}else{
			for(int i=total_question-1;i>=0;i--){
				if(circle_study[i] == true){
					index_current_question = i;
					//找items下标
					index_current_items = findIndexItems(index_current_question);
					return ;
				}
			}
		}
	}

	/**
	 * 函数作用:找到items的下标
	 * int
	 * @param num
	 * @return
	 */
	private int findIndexItems(int num){
		int total = 0;
		int temp = 0;
		for(int i=0;i<list_problems.size();i++){
			temp = total ;
			total += list_problems.get(i).size();
			if(num >= temp && num < total){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 函数作用:播放某个题目
	 * void
	 * @param num
	 */
	private void playQuestion(int num){
		//隐藏对错
		right_wrong.setVisibility(View.INVISIBLE);
		//隐藏答案
		answer_layout.setVisibility(View.INVISIBLE);
		//隐藏灯
		view_deng.setVisibility(View.INVISIBLE);
		//TODO
////		//隐藏喇叭
		button_laba.setVisibility(View.INVISIBLE);
		//隐藏小人
		showGenzong(false);
		//显示提控
		showNextPrevious(true);
		//显示一个问题资源
		showQuestionAndOption(num,true);
	}

	/**
	 * 函数作用:显示一个问题的资源
	 * void
	 * @param num
	 */
	 private void showQuestionAndOption(int num,boolean isClick) {
		 //System.out.println("showQuestionAndOption。。。。isClick="+isClick);
		 //获取当前问题的bean
		 bean_current = getCurrentBean(num);
		 //显示问题文字
		 ArrayList<String> question_text = bean_current.getQuestion_text() ;
		 ArrayList<Integer> question_size = bean_current.getQuestion_size(); //为空时，默认40
		 ArrayList<Integer> question_colr = bean_current.getQuestion_colr(); //为空时，默认Color.BLACK
		 String question_mp3 = bean_current.getQuestion_mp3(); 		//为空时，表示没有
		 if(question_mp3!=null && !question_mp3.equals("")){
			 Util.playSoundPoolMp3(context, num);
		 }
		 int size_text = question_text.size();
		 question_layout.removeAllViews();
		 for(int i=0;i<size_text;i++ ){
			 TextView textView = new TextView(context);
			 textView.setText(question_text.get(i));
			 if(question_size.size()>0){
				 textView.setTextSize(question_size.get(i));
				 textView.setTextColor(question_colr.get(i));
			 }else{
				 textView.setTextSize(30);
				 textView.setTextColor(Color.BLACK);
			 }
			 question_layout.addView(textView);
		 }
		 question_layout.setVisibility(View.VISIBLE);
		 //显示选项
		ArrayList<String> option_text = bean_current.getOption_text();	
		ArrayList<Integer> option_size = bean_current.getOption_size();	//为空时，默认25
		ArrayList<Integer> option_col= bean_current.getOption_colr();	//为空时，默认Color.BLACK
		current_right_answer = bean_current.getRight_anwser();
		 size_text = option_text.size();
		 //先将有可能不显示的选项隐藏
		 textView_C.setVisibility(View.GONE);
		 textView_D.setVisibility(View.GONE);
		 for(int i=0;i<size_text;i++ ){
			 String text = "";
			 //将上选项标号
			 switch(i){
			 case 0:
				 text = "A. " + option_text.get(i);
				 textView_A.setText(text);
				 textView_A.setVisibility(View.VISIBLE);
				 if(option_size.size()>0){
					 textView_A.setTextSize(option_size.get(i));
					 textView_A.setTextColor(option_col.get(i));
				 }else{
					 textView_A.setTextSize(25);
					 textView_A.setTextColor(Color.BLACK);
				 }
				 break;
			 case 1:
				 text = "B. "+ option_text.get(i);
				 textView_B.setText(text);
				 textView_B.setVisibility(View.VISIBLE);
				 if(option_size.size()>0){
					 textView_B.setTextSize(option_size.get(i));
					 textView_B.setTextColor(option_col.get(i));
				 }else{
					 textView_B.setTextSize(25);
					 textView_B.setTextColor(Color.BLACK);
				 }
				 break;
			 case 2:
				 text = "C. "+ option_text.get(i);
				 textView_C.setText(text);
				 textView_C.setVisibility(View.VISIBLE);
				 if(option_size.size()>0){
					 textView_C.setTextSize(option_size.get(i));
					 textView_C.setTextColor(option_col.get(i));
				 }else{
					 textView_C.setTextSize(25);
					 textView_C.setTextColor(Color.BLACK);
				 }
				 break;
			 case 3:
				 text = "D. "+ option_text.get(i);
				 textView_D.setText(text);
				 textView_D.setVisibility(View.VISIBLE);
				 if(option_size.size()>0){
					 textView_D.setTextSize(option_size.get(i));
					 textView_D.setTextColor(option_col.get(i));
				 }else{
					 textView_D.setTextSize(25);
					 textView_D.setTextColor(Color.BLACK);
				 }
				 break;  
			 }
		 }
		 option_layout.setVisibility(View.VISIBLE);
	 }

	 
	 /**
	  * 函数作用:显示正确答案
	  * void
	  */
	 protected void showAnswer(ObjectiveQuizBean bean,int right_anwser,final int number,boolean isShowDone) {
		 //获取答案信息
		final String answer_mp3 = bean.getAnswer_mp3();    //为空时，表示没有
		final ArrayList<String> answer_text = bean.getAnswer_text() ;
		final ArrayList<Integer> answer_size = bean.getAnswer_size() ;	//为空时，默认25 
		final ArrayList<Integer> answer_colr = bean.getAnswer_colr() ;	//为空时，默认Color.BLACK
		//System.out.println("显示答案。。isShowDone="+isShowDone+",answer_mp3="+answer_mp3);
//		显示喇叭
		if(answer_mp3 != null && !answer_mp3.equals("") && !isShowDone){
			isQuestionMp3 = true;
			//System.out.println("显示喇叭");
			button_laba.setVisibility(View.VISIBLE);
			new Thread(new Runnable() {
				@Override
				public void run() {
					//睡眠1.5秒播放答案
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Util.playSoundPoolAnswerMp3(context, number);
				}
			}).start();
		}
		
		answer_layout.removeAllViews();
		//显示正确答案
		TextView textView = new TextView(context);
		textView.setText("正确答案是:"+getABCD(right_anwser));
		textView.setTextSize(20);
		textView.setTextColor(Color.RED);
		answer_layout.addView(textView);
			
		if(answer_text != null){
			LinearLayout layout_explain = new LinearLayout(context);
			layout_explain.setOrientation(HORIZONTAL);
			//显示解释
			for(int i=0;i<answer_text.size();i++){
				TextView textView2 = new TextView(context);
				textView2.setText(answer_text.get(i));
				if(answer_colr.size()>0){
					textView2.setTextSize(answer_size.get(i));
					textView2.setTextColor(answer_colr.get(i));
				}else{
					textView2.setTextSize(22);
					textView2.setTextColor(Color.BLUE);
				}
				layout_explain.addView(textView2);
			}
			answer_layout.addView(layout_explain);
		}
		
		answer_layout.setVisibility(View.VISIBLE);
	}

	 
	 
	 
	public String getABCD(int index){
		 switch(index){
		 case 0:
			 return "A";
		 case 1:
			 return "B";
		 case 2:
			 return "C";
		 case 3:
			 return "D";
		 }
		 return null;
	 }
	 
	 /**
	  * 函数作用:获取当前的问题bean
	  * ObjectiveQuizBean
	  * @param num
	  * @return
	  */
	private ObjectiveQuizBean getCurrentBean(int num) {
		int total = 0;
		for(int i=0;i<list_problems.size();i++){
			int num_probem = list_problems.get(i).size();
			int temp = total;
			total += num_probem;
			if(num >= temp && num < total){
				int offset = num - temp;
				return list_problems.get(i).get(offset);
			}
		}
		return null;
	}

	/**
	 * 函数作用:改变题目控制布局的显示属性
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
		if(isFirstQuestion() ){
			//第一题，不显示上一题
			button_prvious.setVisibility(View.INVISIBLE);
			button_next.setVisibility(View.VISIBLE);
		}else if(isLastQuestion()){
			//最后一题，不显示下一题
			button_prvious.setVisibility(View.VISIBLE);
			button_next.setVisibility(View.INVISIBLE);
		}else{
			button_prvious.setVisibility(View.VISIBLE);
			button_next.setVisibility(View.VISIBLE);
		}
		button_prvious.getFrameAnimationDrawable().stop();
	}
	
	
	/**
	 * 函数作用:是否是最后一题
	 * boolean
	 * @return
	 */
	public boolean isLastQuestion(){
		for(int i=total_question-1;i>= 0;i--){
			if( circle_study[i] ){
				if(i == index_current_question){
					if(!isFirstLearn && isFirstSection){
						return false;
					}
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}
	
	
	/**
	 * 函数作用:判断目前是否是第一个题目，没有前一题。
	 * boolean
	 * @return
	 */
	public boolean isFirstQuestion(){
		for(int i=0;i<total_question;i++){
			if( circle_study[i] ){
				if(i == index_current_question){
					if(!isFirstLearn && !isFirstSection){
						return false;
					}
					return true;
				}else{
					return false;
				}
			}
		}
		
		return false;
	}
		
	/**
	 * 函数作用:判断目前是否是第一个题目，没有前一题。
	 * boolean
	 * @return
	 */
	public boolean isFirstQuestion_previous(){
		for(int i=0;i<total_question;i++){
			if( circle_study[i] ){
				if(i == index_current_question){
					return true;
				}else{
					return false;
				}
			}
		}
		
		return false;
	}
	

	/**
	 * 函数作用:改变跟踪布局的显示属性
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
	 * 函数作用:设置监听器`
	 * void
	 */
	private void setListener() {
		ButtonListener listener = new ButtonListener(); 
		OptionListener listener2 = new OptionListener();
		//选项文字
		textView_A.setOnClickListener(listener2);
		textView_B.setOnClickListener(listener2);
		textView_C.setOnClickListener(listener2);
		textView_D.setOnClickListener(listener2);
		//提示牌的确认按钮
		button_hint.setOnClickListener(listener);
	    //返回
		mBackView.setOnClickListener(listener);
	    //返回
		button_laba.setOnClickListener(listener);
		//跟踪小人
		button_ok.setOnClickListener(listener);
		button_puzzle.setOnClickListener(listener);
		button_no.setOnClickListener(listener);
		//上下题
		button_prvious.setOnClickListener(listener);
		button_next.setOnClickListener(listener);
	} 
	
	
	class OptionListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(isOptionJustClick){
				return ;
			}else{
				isOptionJustClick = true;
			}
			
			//点击了选项, 完成一个
			if(isFirstLearn){
				num_not_done --;
				textview_not_avi.setText("未完成:"+num_not_done);
			}else{
				number_done_circle ++ ;
			}
			
			boolean isRight = false;
			// 如果选项开关打开
			if(v == textView_A){
				//将本文字的颜色变成蓝色，并放大1.2倍
				textView_A.setTextColor(Color.BLUE);
				textView_A.setTextSize((float) (textView_A.getTextSize()*1.2));
				if(0 == current_right_answer){
					isRight = true;
				} 
				
			}else if(v == textView_B){
				//将本文字的颜色变成蓝色，并放大1.2倍
				textView_B.setTextColor(Color.BLUE);
				textView_B.setTextSize((float) (textView_B.getTextSize()*1.2));
				if(1 == current_right_answer){
					isRight = true;
				} 
				
			}else if(v == textView_C){
				//将本文字的颜色变成蓝色，并放大1.2倍
				textView_C.setTextColor(Color.BLUE);
				textView_C.setTextSize((float) (textView_C.getTextSize()*1.2));
				if(2 == current_right_answer){
					isRight = true;
				} 
			}else if(v == textView_D){
				//将本文字的颜色变成蓝色，并放大1.2倍
				textView_D.setTextColor(Color.BLUE);
				textView_D.setTextSize((float) (textView_D.getTextSize()*1.2));
				if(3 == current_right_answer){
					isRight = true;
				} 
			}
			if(isRight){
				//点中了正确答案
				Toast.makeText(context, "正确", 0).show();
				//显示对错图片
				right_wrong.setImageResource(R.drawable.check_right);
				right_wrong.setVisibility(View.VISIBLE);
				//显示跟踪小人
				showGenzong(true);
				//隐藏题控
				showNextPrevious(false);
				//显示正确答案和解释
				showAnswer(bean_current,current_right_answer,index_current_question,false);
				//播放正确音乐
				Util.playSoundPool(context,Constant.AUDIO_MARK_APPLAUD);
			}else{
				//点中错误答案
				Toast.makeText(context, "错误答案", 0).show();
				//显示对错图片
				right_wrong.setImageResource(R.drawable.check_error);
				right_wrong.setVisibility(View.VISIBLE);
				//显示正确答案和解释
				showAnswer(bean_current,current_right_answer,index_current_question,false);
				// 灯
				showLight(true,3);
				//播放错误音乐
				Util.playSoundPool(context,Constant.AUDIO_MARK_FAILED);
				//改变状态
				if(isFirstLearn){
					state_question[index_current_question] = 3;
					//向DB中增加一条记录信息
					if(studySate == 0){
						DatabaseAdapter.getInstance(context).add2Record(path, index_current_question, 3, 3);
					}
				}else{
					if(isFirstSection){
						state_circle_study1[index_current_question] = 3;
					}else{
						state_circle_study2[index_current_question] = 3;
					}
				}
				if(isFirstLearn){
					//未掌握 数加1
					num_not_understand ++ ;
					textview_not_understand.setText("未掌握:"+num_not_understand);
				}
				//处理学习一次完成时
				handleLastWrong();
			}
		}
	}
	
	class ButtonListener implements OnClickListener{
		@Override
		public void onClick(View v
				) {
			//System.out.println("点击了某个东东:");
			if(v == button_hint){
				button_hint.setBackgroundResource(R.drawable.sure);
				//System.out.println("isFirstMp3LoadDone="+isFirstMp3LoadDone);
				if(!isFirstMp3LoadDone){
					
				}else{
					Util.stopHint();
					if(isLearnFinish){
						isReview = true;
					}
					//点击了提示牌的确认按钮
					//初始化题目
					initQuestion();
					isCircleStudy = false;
					//System.out.println("初始过后;各题的状态:");
					for(int i=0;i<total_question ;i++){
						//System.out.println("satae["+i+"]="+state_question[i]);
					}
					//找目前的题目标号和items标号
					findIndex(true);
					//记录牌显示
					showRegistBoard();
					//开始学习
					startStudy();
					//隐藏提示布局
					layout_hint.setVisibility(View.INVISIBLE);
				}
			}else  if (v == button_ok){
				// 点击没问题
				Util.playSoundPool(context,Constant.AUDIO_MARK_BUTTON);
				//展示动画
				button_ok.play(false);
				handleAnimation(button_ok,1);
				// 记录数组
				if(isFirstLearn){
					state_question[index_current_question] = 1;
					//向DB中增加一条记录信息
					if(studySate == 0){
						DatabaseAdapter.getInstance(context).add2Record(path, index_current_question, 1, 1);
					}
				}else{ 
					if(isFirstSection){
						state_circle_study1[index_current_question] = 1;
					}else{
						state_circle_study2[index_current_question] = 1;
					}
					if(state_circle_study1[index_current_question] 
							== state_circle_study2[index_current_question]){
						//这个题目攻克了
						if(state_question[index_current_question] == 2){
							num_puzzle -- ;	
							num_understand ++; 
							textview_vague.setText("模    糊:"+num_puzzle);
							textview_understand.setText("已掌握:"+num_understand);
						}else if(state_question[index_current_question] == 3){
							num_not_understand -- ; 
							num_understand ++; 
							textview_not_understand.setText("未掌握:"+num_not_understand);
							textview_understand.setText("已掌握:"+num_understand);
						}
						state_question[index_current_question] = 1; 
						//修改DB中改信息的最终状态
						if(studySate == 0){
							DatabaseAdapter.getInstance(context).alterFinalState(path, index_current_question, 1);
						}
					}
				}
				// 掌握数加1
				if(isFirstLearn){
					num_understand ++ ;
					textview_understand.setText("已掌握:"+num_understand);
				}
				handleCircleStudyDone();
			}else if (v == button_puzzle){
				//点击模糊
				Util.playSoundPool(context,Constant.AUDIO_MARK_BUTTON);
				//展示动画
				button_puzzle.play(false); 
				handleAnimation(button_puzzle,2);
				if(isFirstLearn){
					state_question[index_current_question] = 2;
					//向DB中增加一条记录信息
					if(studySate == 0){
						DatabaseAdapter.getInstance(context).add2Record(path, index_current_question, 2, 2);
					}
				}else{
					if(isFirstSection){
						state_circle_study1[index_current_question] = 2;
					}else{
						state_circle_study2[index_current_question] = 2;
					}
				}
				if(isFirstLearn){
					// 模糊数加1
					num_puzzle ++ ;
					textview_vague.setText("模    糊:"+num_puzzle);
				}
				
				handleCircleStudyDone();
				
			}else if (v == button_no){
				//点击完全不会
				Util.playSoundPool(context,Constant.AUDIO_MARK_BUTTON);
				//展示动画
				button_no.play(false);
				handleAnimation(button_no,3);
				if(isFirstLearn){
					state_question[index_current_question] = 3;
					//向DB中增加一条记录信息
					if(studySate == 0){
						DatabaseAdapter.getInstance(context).add2Record(path, index_current_question, 3, 3);
					}
				}else{
					if(isFirstSection){
						state_circle_study1[index_current_question] = 3;
					}else{
						state_circle_study2[index_current_question] = 3;
					}
				}
				if(isFirstLearn){
					//未掌握 数加1
					num_not_understand ++ ;
					textview_not_understand.setText("未掌握:"+num_not_understand);
				}
				handleCircleStudyDone();

			}else if (v == button_prvious){
				Util.playSoundPool(context,Constant.AUDIO_MARK_BUTTON);
				isOptionJustClick = false;
				//点击上一题
				//展示动画
				button_prvious.play(false);
				handleAnimation(button_prvious,4);
			}else if (v == button_next){
				Util.playSoundPool(context,Constant.AUDIO_MARK_BUTTON);
				if(isLastWrong){
					handleCircleStudyDone();
					isLastWrong = false;
				}else{
					isOptionJustClick = false;
					//点击下一题
					//展示动画
					button_next.play(false);
					handleAnimation(button_next,5);
				}
			}
			else if (v==button_laba){
				Util.playSoundPoolAnswerMp3(context, index_current_question);
			} else if (v==mBackView){
				//点击了返回按钮
				Util.playSoundPool(context, Constant.AUDIO_MARK_BUTTON);
				Util.clearMp3();
				ViewController.backToNormalDuyin();
				//向DB记录目录的数据
				//System.out.println("studySate="+studySate);
				if(studySate == 0){ 
					//System.out.println("isFirstLearn="+isFirstLearn);
					if(isFirstLearn){
						//第一次学习
						boolean isExitst = DatabaseAdapter.getInstance(context).isValueExists(DatabaseAdapter.CONTENT_TABLE, path);
						//System.out.println("isExitst="+isExitst);
						int state_restart = DatabaseAdapter.getInstance(context).getLishtState(path);
						//System.out.print("重新学习状态state_restart="+state_restart);
						if(!isExitst){
							DatabaseAdapter.getInstance(context).add2Content(path, 0, 0, 0, 1);
						}
					} 
				}
				ResourceLoader.refreshMenuView();
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
	 * 函数作用:显示做过的题目
	 * void
	 * @param num
	 */
	private void showDone(int num){
		isOptionJustClick = true;
		//这个题已经做过
		//隐藏对错
		right_wrong.setVisibility(View.INVISIBLE);
		//显示问题和选项
		showQuestionAndOption(num,false);
		//显示答案
		ObjectiveQuizBean bean = getCurrentBean(num);
		showAnswer(bean,bean.getRight_anwser(),num,true);
		//如果有声音，显示喇叭
		if(bean.getAnswer_mp3() != null){
			isQuestionMp3 = true;
			button_laba.setVisibility(View.VISIBLE);
		}
		// 跟踪不显示
		showGenzong(false);
		// 题目控制显示
		showNextPrevious(true);
		int state = -1;
		if(!isFirstLearn && isFirstSection){
			state = state_circle_study1[index_current_question];
		}else if(!isFirstLearn && !isFirstSection){
			state = state_circle_study2[index_current_question];
		}else{
			state =state_question[index_current_question];
		}
		//显示灯
		showLight(true, state);
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
                		playPrevious();
                		break;
                	case 5:
                		playNext();
                		break;
                	}
                	animation.getFrameAnimationDrawable().stop();
                }
        }, duration);		
	}

	/**
	 * 函数作用:播放下一题的动作
	 * void
	 */
	protected void playNext() {
		if(!isFirstLearn && isFirstSection && isLastQuestion_next() ){
			isFirstSection = false;
			//System.out.println("第一轮最后一题。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。");
			//第二次学习的第二轮,找到第一个题目
			findIndex(true);
			if(state_circle_study2[index_current_question] == 0){
				//还没有做过
				playQuestion(index_current_question);
			}else{
				//已经做过
				showDone(index_current_question);
			}
			return ;
		}
		//寻找下一个题目
		if(findNext()){
			//items改变
			startStudy();
		}else{
			//普通切换题目
			if(isFirstLearn){
				if(state_question[index_current_question] == 0 ){
					//还没有做过
					playQuestion(index_current_question);
				}else{
					//已经做过
					showDone(index_current_question);
				}
			}else{
				if(isFirstSection){
					if(state_circle_study1[index_current_question] == 0 ){
						//还没有做过
						playQuestion(index_current_question);
					}else{
						//已经做过
						showDone(index_current_question);
					}
				}else{
					if(state_circle_study2[index_current_question] == 0 ){
						//还没有做过
						playQuestion(index_current_question);
					}else{
						//已经做过
						showDone(index_current_question);
					}
				}
			}
		}		
	}

	/**
	 * 函数作用:播放下一题的动作
	 * void
	 */
	protected void playPrevious() {
		if(!isFirstLearn && !isFirstSection && isFirstQuestion_previous() ){
			isFirstSection = true;
			//第二次学习的第二轮,找到最后一个题目
			findIndex(false);
			if(state_circle_study1[index_current_question] == 0){
				//还没有做过
				playQuestion(index_current_question);
			}else{
				//已经做过
				showDone(index_current_question);
			}
			return ;
		}
		//寻找上一个题
		if(findPrevious()){
			//items改变
			startStudy();
		}else{
			//普通切换题目
			if(isFirstLearn){
				if(state_question[index_current_question] == 0 ){
					//还没有做过
					playQuestion(index_current_question);
				}else{
					//已经做过
					showDone(index_current_question);
				}
			}else{
				if(isFirstSection){
					if(state_circle_study1[index_current_question] == 0 ){
						//还没有做过
						playQuestion(index_current_question);
					}else{
						//已经做过
						showDone(index_current_question);
					}
				}else{
					if(state_circle_study2[index_current_question] == 0 ){
						//还没有做过
						playQuestion(index_current_question);
					}else{
						//已经做过
						showDone(index_current_question);
					}
				}
			}
		}		
	}

	
	/**
	 * 函数作用：处理最后一题选项错误的情况
	 * void
	 */
	public void handleLastWrong(){
		button_laba.setVisibility(View.INVISIBLE);
		isLastWrong = false;
		if(isFirstLearn){
			//System.out.println("num_not_done。。。。="+num_not_done);
			if(num_not_done == 0){ 
				isLastWrong = true;
			}else{
				if(isLastQuestion_next()){ 
					isLastWrong = true;
				}else{
					//还没完成一圈
					if(isQuestionMp3 == true){
						button_laba.setVisibility(View.VISIBLE);
					}
				}
			}
		}else{
			//向DB中存储目录的状态
			saveStateContent();
			//System.out.println("number_done_circle ="+number_done_circle+",total_current_circle="+total_current_circle);
			if(number_done_circle == total_current_circle * 2){
				isLastWrong = true;
			}else{
				if(isLastQuestion()){
					isLastWrong = true;
				}else{
					//还没完成一圈
					if(isQuestionMp3 == true){
						button_laba.setVisibility(View.VISIBLE);
					}
				}
			}
		}
		if(isLastWrong){
			showNext();
		}else{
			showNextPrevious(true);
		}
	}
	
	
	/**
	 * 函数作用：显示下一题按钮
	 * void
	 */
	private void showNext() {
		jump_layout.setVisibility(View.VISIBLE);
		button_next.setVisibility(View.VISIBLE);
		button_prvious.setVisibility(View.INVISIBLE);
	}

	/**
	 * 函数作用:处理循环学习一次完成时
	 * void
	 */
	public void handleCircleStudyDone() {
		button_laba.setVisibility(View.INVISIBLE);
		if(isFirstLearn){
			//System.out.println("num_not_done。。。。="+num_not_done);
			if(num_not_done == 0){
				//所有题目都做了一遍
				isFirstLearn = false;
				//向DB中存储目录的状态
				saveStateContent();
				//将主体布局隐藏
				layout_main.setVisibility(View.INVISIBLE);
				//如果是第一次学习完毕，那么就记录数据库
				//显示文字，根据已掌握数量来判断是否循环
				//System.out.println("num_not_understand。。。。="+num_not_understand);
				//System.out.println("num_puzzle。。。。="+num_puzzle);
				if(num_not_understand==0 && num_puzzle == 0){
					// 全部掌握，学完一遍了
					//显示全部掌握文字。
					isLearnFinish = true;
					text_hint.setText(Constant.TEXT_ALL_UNDERSTAND);
					Util.playBackGroundSound(context, "hint_done.mp3",false);
				}else{
					isCircleStudy = true;
					// 还未全部掌握，需要循环学习
					//显示循环学习文字。
					text_hint.setText("你已经完成筛查，有"+(num_not_understand+num_puzzle)+"个知识点没有掌握，你需要把它们再做2遍。");
					Util.playBackGroundSound(context, "hint_remain_do.mp3",false);
				}
				//弹出提示框
				layout_hint.setVisibility(View.VISIBLE);
			}else{
				if(isLastQuestion_next()){
					//是最后一题，并且没有做完题目
					//将主体布局隐藏
					layout_main.setVisibility(View.INVISIBLE);
					text_hint.setText(Constant.TEXT_REMAIN_SELECT);
					Util.playBackGroundSound(context, "hint_remani_select.mp3",false);
					//弹出提示框
					layout_hint.setVisibility(View.VISIBLE);
				}else{
					//还没完成一圈
					if(isQuestionMp3 == true){
						button_laba.setVisibility(View.VISIBLE);
					}
				}
			}
		}else{
			//向DB中存储目录的状态
			saveStateContent();
			//System.out.println("number_done_circle ="+number_done_circle+",total_current_circle="+total_current_circle);
			if(number_done_circle == total_current_circle * 2){
				//System.out.println("number_done_circle == total_current_circle * 2循环时题目做完了");
				//System.out.println("全部掌握，学完一遍了全部掌握，学完一遍了全部掌握，学完一遍了");
				for(int i=0;i<total_question ;i++){
					//System.out.println("satae["+i+"]="+state_question[i]);
				}
				//当循环时题目做完了
				//将主体布局隐藏
				layout_main.setVisibility(View.INVISIBLE);
				//如果是第一次学习完毕，那么就记录数据库
				//TODO
				//显示文字，根据已掌握数量来判断是否循环
				//System.out.println("num_not_understand。。。。="+num_not_understand);
				//System.out.println("num_puzzle。。。。="+num_puzzle);
				if(num_not_understand == 0 && num_puzzle == 0){
					// 全部掌握，学完一遍了
					//显示全部掌握文字。
					isLearnFinish = true;
					text_hint.setText(Constant.TEXT_ALL_UNDERSTAND);
					Util.playBackGroundSound(context, "hint_done.mp3",false);
				}else{
					// 还未全部掌握，需要循环学习
					//显示循环学习文字。
					isCircleStudy = true;
					text_hint.setText("你已经完成筛查，有"+(num_not_understand+num_puzzle)+"个知识点没有掌握，你需要把它们再做2遍。");
					Util.playBackGroundSound(context, "hint_remain_do.mp3",false);
				}
				//弹出提示框
				layout_hint.setVisibility(View.VISIBLE);
			}else{
				if(isLastQuestion()){
					//是最后一题，并且没有做完题目
					//将主体布局隐藏
					layout_main.setVisibility(View.INVISIBLE);
					//显示循环学习文字。
					isCircleStudy = true;
					text_hint.setText("你已经完成筛查，有"+(num_not_understand+num_puzzle)+"个知识点没有掌握，你需要把它们再做2遍。");
					Util.playBackGroundSound(context, "hint_remain_do.mp3",false);
					//弹出提示框
					layout_hint.setVisibility(View.VISIBLE);
				}else{
					//还没完成一圈
					if(isQuestionMp3 == true){
						button_laba.setVisibility(View.VISIBLE);
					}
				}
			}
		}
	}

	
	/**
	 * 函数作用: 保存目录状态信息
	 * void
	 */
	private void saveStateContent() {
		int state_max = 0;
		for(int i=0;i<total_question;i++){
			if(state_question[i] > state_max ){
				state_max = state_question[i];
			}
		}
		int impState = 0;
		int scdState = 0;
		if(state_max == 1){
			//本次学习完毕
			//根据第一次记录，计算重、次重点复习
			ArrayList<TopicBean> list_first = DatabaseAdapter.getInstance(context).getRecord(path);
			//第一次状态 的最大值
			int size_first = list_first.size();
			int max_first_state = 0; 
			int state_last = 0;
			boolean allthesame = true;
			for(int i = 0 ;i< size_first ;i++){
				TopicBean bean = list_first.get(i);
				int state = bean.firstState;
				if(i>0){
					if(state_last == 1){
						 if(state != 1){
							 allthesame = false;
						 }
					}else{
						 if(state == 1){
							 allthesame = false;
						 }
					}
				}
				if(state > max_first_state){
					max_first_state = state;
				}
				state_last = state;
			}
			
			if(max_first_state == 3 || max_first_state == 2){
				impState = 1; 
				if(allthesame){
					scdState = 0;
				}else{
					scdState = 1;
				}
			}else {
				impState = 0;
				scdState = 1;
			}
		}else{
			impState = 0;
			scdState = 0;
		}

		if(studySate == 0){
			if(DatabaseAdapter.getInstance(context).isValueExists(DatabaseAdapter.CONTENT_TABLE, path)){
				ContentStateBean bean = new ContentStateBean();
				bean.importantState = impState;
				bean.secondState = scdState;
				bean.lightState = state_max;
				bean.restartState = 1;
				DatabaseAdapter.getInstance(context).alterContentState(path,bean);
			}else{
				DatabaseAdapter.getInstance(context).add2Content(path, state_max, impState, scdState, 1);
			}
		}
	}

	/**
	 * 函数作用:再点击下一题时，判断是否是最后一题。
	 * boolean
	 * @return
	 */
	public boolean isLastQuestion_next() {
		for(int i=total_question-1;i>= 0;i--){
			if( circle_study[i] ){
				if(i == index_current_question){
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * 函数作用:显示记录牌
	 * void
	 */ 
	public void showRegistBoard() {
		textview_total_count.setText("本节共计:"+benjiegongji);
		textview_not_avi.setText("未完成:"+num_not_done);
		textview_not_understand.setText("未掌握:"+num_not_understand);
		textview_understand.setText("已掌握:"+num_understand);
		textview_vague.setText("模    糊:"+num_puzzle);
	}

	
	/**
	 * 函数作用:寻找上一个题目
	 * boolean
	 * @return
	 */
	public boolean findPrevious() {
		for(int i=index_current_question-1;i>=0;i--){
			if(circle_study[i]){
				index_current_question = i;
				int temp = findIndexItems(index_current_question);
				if(temp != index_current_items){
					//items改变
					index_current_items = temp;
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * 函数作用:寻找下一个题目
	 * void
	 * 返回是否为items
	 */
	public boolean findNext() {
		//System.out.println("准备找到下一题的题号。。当前题号:"+index_current_question);
		
		
		for(int i=index_current_question+1;i<total_question;i++){
			if(circle_study[i]){
				index_current_question = i;
				//System.out.println("下一题的题号:"+index_current_question+"          &&&&&&&&&&&&&&&");
				int temp = findIndexItems(index_current_question);
				//System.out.println("下一题的items号:"+temp+"          &&&&&&&&&&&&&&&");
				if(temp != index_current_items){
					//items改变
					//System.out.println("items改变");
					index_current_items = temp;
					return true;
				}else{
					return false;
				}
			}
		}
		//System.out.println("没找到下一题的题号。。");
		return false;
	}

	/**
	 * 函数作用:初始化题目
	 * void
	 */
	public void initQuestion() {
		number_done_circle = 0;
		//初始化题目总数	
		isFirstSection = true;
		//释放选项的点击锁
		isOptionJustClick = false; 
		if(isReview){
			isFirstLearn = true;
			if(studySate == 0){
				total_current_circle = 0;
				for(int i=0;i<total_question;i++){
					circle_study[i] = true; 
					total_current_circle ++;
				}
			}else{
				isFirstLearn = true;
				ArrayList<TopicBean> list_record = DatabaseAdapter.getInstance(context).getRecord(path);
				int size_done = 0 ;
				if(list_record != null){
					size_done = list_record.size();
				}
				total_current_circle = 0;
				for(int i=0;i<size_done;i++){
					TopicBean bean = list_record.get(i);
					int number = bean.number;
					int state = bean.firstState; 
					if(state == 1){
						if(studySate == 1){
							circle_study[number] = false;
						}else if(studySate == 2){
							circle_study[number] = true;
							total_current_circle ++ ;
							//System.out.println("第"+number+"个题目需要重做");
						}
					}else{
						if(studySate == 1){
							circle_study[number] = true;
							total_current_circle ++;
							//System.out.println("第"+number+"个题目需要重做");
						}else if(studySate == 2){
							circle_study[number] = false;
						}
					}
				}
			}
			return ;
		}
		
		//初始化循环学习数组	
		total_current_circle = 0;
		for(int i=0;i<total_question;i++){
			if(isFirstLearn){
				if(studySate == 0){
					if( state_question[i] == 0){
						circle_study[i] = true; 
						total_current_circle ++;
					}else{
						circle_study[i] = false;
					}
				}else{
					if(circle_study[i] && state_question[i] == 0){
						circle_study[i] = true; 
						total_current_circle ++;
					}else{
						circle_study[i] = false;
					}
				}
			}else{
				if(isCircleStudy){
						if(state_question[i] ==1  ){
							circle_study[i] = false;
						}else if(state_question[i] == 2 ||  state_question[i] == 3 ){
							state_circle_study1[i] = state_circle_study2[i] = 0;
							total_current_circle ++;
							circle_study[i] = true; 
						}
				}else{
					if(state_question[i] ==1 ){
						circle_study[i] = false; 
					}else if( studySate !=0 && circle_study[i]){
						state_circle_study1[i] = state_circle_study2[i] = 0; 
						total_current_circle ++; 
						circle_study[i] = true; 
					}else if(studySate == 0){
						state_circle_study1[i] = state_circle_study2[i] = 0;
						total_current_circle ++;
						circle_study[i] = true; 
					}
				}
			}
		}
	}

	/**
	 * 函数作用:是否该调整items的下标了
	 * boolean
	 * @param isAdd
	 * @return
	 */
	public boolean isChangeItems(boolean isAdd){
		int total = 0;
		for(int i=0;i<list_problems.size();i++){
			total += list_problems.get(i).size();
			if(isAdd){
				//增加过来的
				if(index_current_question == total){
					return true;
				} 
			}else{
				//减少过来的
				if(index_current_question == total -1){
					return true;
				}
			}
		}
		return false;
	}
	

	/**
	 * 函数作用:显示灯
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
	
	
	private void initView() {
		/*
		 * 主体布局
		 */
		layout_main = (RelativeLayout)findViewById(R.id.layout_main);
		/*
		 * 提示牌
		 */
		layout_hint = (RelativeLayout)findViewById(R.id.hint_layout);
		text_hint = (TextView)findViewById(R.id.text_hint);
		button_hint = (ImageView)findViewById(R.id.button_yes);
		//初始化记分牌
		//本节共计
		textview_total_count = (TextView)findViewById(R.id.textview_total_count);
		textview_total_count.setText("本节共计:");
		//未完成
		textview_not_avi = (TextView)findViewById(R.id.textview_not_avi);
		textview_not_avi.setText("未完成:");
		//未掌握
		textview_not_understand = (TextView)findViewById(R.id.textview_not_understand);
		textview_not_understand.setText("未掌握  ");
		//模糊
		textview_vague = (TextView)findViewById(R.id.textview_vague);
		textview_vague.setText("模    糊:"+num_puzzle);
		//已掌握
		textview_understand = (TextView)findViewById(R.id.textview_understand);
		textview_understand.setText("已掌握:");
		//引导文字
		intro_text = (TextView)findViewById(R.id.intro_text); 
		
		
		//问题布局
		question_layout = (LinearLayout)findViewById(R.id.question_layout);
		//选择布局
		option_layout = (LinearLayout)findViewById(R.id.option_layout);
		//选项文字
		textView_A = new TextView(context);
		textView_B = new TextView(context);
		textView_C = new TextView(context);
		textView_D = new TextView(context);
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(       
		           LinearLayout.LayoutParams.WRAP_CONTENT,       
		           LinearLayout.LayoutParams.WRAP_CONTENT       
			 );       
		p.topMargin = 10;
		option_layout.addView(textView_A,p);
		option_layout.addView(textView_B,p);
		option_layout.addView(textView_C,p);
		option_layout.addView(textView_D,p);
		//答案 解释 布局
		answer_layout = (LinearLayout)findViewById(R.id.answer_layout);
		//对错
		right_wrong = (ImageView)findViewById(R.id.image_judge); 
		/*
		 * 背景
		 */  
		layout_bg_screen = (LinearLayout) findViewById(R.id.bg_object_big); 
		String file_imageString = ResourceLoader.getMajorBackImageUrl(index);
        File file=new File(file_imageString);
        if (file.exists()) {//若该文件存在
        	bmp_bg = BitmapFactory.decodeFile(file_imageString); 
        	layout_bg_screen.setBackgroundDrawable(new BitmapDrawable(bmp_bg));
        }
        flashBgLayout = (RelativeLayout) findViewById(R.id.bg_object_small);
        /*
         * 灯
         */
        view_deng = (GameView)findViewById(R.id.light);
        /*
         * 返回按钮
         */
		mBackView = (GameView) findViewById(R.id.button_back);
		mBackView.addDrawable(R.drawable.back_default, 800);
		mBackView.addDrawable(R.drawable.back_hot, 800);
		mBackView.play(false);
		
		/*
		 * 喇叭
		 */
		button_laba = (ImageButton) findViewById(R.id.button_laba);
		
        /*
         * 跟踪布局
         */
        jump_layout = (LinearLayout) findViewById(R.id.jump_layout);
        check_layout = (LinearLayout) findViewById(R.id.check_layout);
		/*
		 * 跟踪小人
		 */
		button_ok = (GameView)findViewById(R.id.check_good);
		button_puzzle = (GameView)findViewById(R.id.check_just);
		button_no = (GameView)findViewById(R.id.check_bad);
		
		button_ok.addDrawable(R.drawable.button_good1, 300);
		button_ok.addDrawable(R.drawable.button_good2, 300);
		
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
		
		button_next.addDrawable(R.drawable.button_next, 200);
		button_next.addDrawable(R.drawable.button_next2, 200);
		button_next.addDrawable(R.drawable.button_next, 200);

	}
	
	
	/** 
	 * 视图变量
	 */
	//本节共计
	private TextView textview_total_count ;
	//未完成
	private TextView textview_not_avi;
	//未掌握 
	private TextView textview_not_understand;
	//模糊
	private TextView textview_vague;
	//已掌握
	private TextView textview_understand;
	//引导文字
	private TextView intro_text;
	//喇叭按钮

	
	
	//主体布局
	private RelativeLayout layout_main;
	//问题布局
	private LinearLayout question_layout;
	//选择布局
	private LinearLayout option_layout;
	//答案 解释 布局
	private LinearLayout answer_layout;
	//对错
	private ImageView right_wrong;
	//正确答案
	private TextView textview_right_answer;
	

    /*
     * 跟踪布局
     */
    private LinearLayout jump_layout ;
    private LinearLayout check_layout ;
	/*
	/*
     * 返回按钮
     */
    private GameView mBackView;
    
    private ImageButton button_laba;
    
    /*
     * 灯
     */
    private GameView view_deng;
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
	 * 背景
	 */
	LinearLayout layout_bg_screen ;
    RelativeLayout flashBgLayout  ;
    String bg;
	
	/**
	 * 数据变量
	 */
	//引导字
	ArrayList<String> text_guide = new ArrayList<String>();
	//引导MP3
	ArrayList<String> mp3_guide = new ArrayList<String>();
	ArrayList<TopicBean> list_record ;
	//题目数据
	ArrayList<ArrayList<ObjectiveQuizBean>> list_problems  = new ArrayList<ArrayList<ObjectiveQuizBean>>(); 
	/**
	 * 控制变量
	 */
	//当前题目
	ObjectiveQuizBean bean_current;
	//当前的items下标
	int index_current_items = 0 ;
	//诗句 背诵记录 数组，每个值包括:1,2，3 分别代表 没问题，有点模糊，完全不懂 
	private int[] state_question;
	// 当前题号
	private int index_current_question = 0;
	//总共字
	private int total_question = 0 ; 
	//未完成
	private int num_not_done;
	//未掌握
	private int num_not_understand;
	//模糊
	private int num_puzzle;
	//掌握
	private int num_understand;

	
	/**
	 * 函数作用:根据#ff0000 获取颜色值
	 * int
	 * @param str
	 * @return
	 */
    public int getColor(String str){
		return Integer.parseInt(str.substring(1), 16);
	}
	
	
	
	public ObjectiveQuizView(Context context) {
		super(context);
		this.context = context;
	}
	
	public ObjectiveQuizView(Context context,AttributeSet set) {
		super(context,set);
		this.context = context;
	}
	private Context context;
	private String path ;
	private String type ;
	private int index;
	/*
	 * 提示的视图
	 */
	private RelativeLayout layout_hint;
	private TextView text_hint;
	private ImageView button_hint;
	/*
	 * 选项是否刚被点击的变量
	 */
	private boolean isOptionJustClick = false;
	/*
	 * 本次循环学习的题目
	 */
	private boolean circle_study[];
	/*
	 * 当前循环学习的题目总数（与total_question不一样）
	 */
	private int total_current_circle;
	/*
	 * //TODO 测试使用
	 */
	private boolean firstTime = true;
	/*
	 * 是否学习完毕
	 */
	private boolean isLearnFinish = false;
	/*
	 * 选项文字
	 */
	private TextView textView_A;
	private TextView textView_B;
	private TextView textView_C;
	private TextView textView_D;
	/*
	 * 是否第二次循环学习 
	 */
	private boolean isFirstLearn = true;
	private boolean isFirstSection = true;
	/*
	 * 循环学习时，记录的已做题目数量
	 */
	private int number_done_circle = 0; 
	
	//记录循环学习时的状态数组
	private int[] state_circle_study1;
	private int[] state_circle_study2;
	
	private int current_right_answer;
	private boolean isOptionsClickable = false;
	//是否完成筛查
	private boolean isSelectDone = false;
	//是否是浏览
	private boolean isReview = false;
	//目录视图
	private MenuView menuView ;
	//学习状态 0 —— 普通 ， 1——重点复习 2——次重点复习 
	private int studySate = 0;
	//本节共计
	private int benjiegongji = 0 ;
	/*
	 * 是否是进入本次学习的第一次initQuestion
	 */
	private boolean isFirstInitQuestion = true;
	/*
	 * 是否是循环学习
	 */
	private boolean isCircleStudy = false;
	/*
	 * 第一个导读音乐是否加载完毕
	 */
	private boolean isFirstMp3LoadDone = false;
	/*
	 * 最后一题是否选错
	 */
	private boolean isLastWrong = false;
	/*
	 * 问题中是否有MP3
	 */
	private boolean isQuestionMp3 = false;
	/*
	 * 背景Bitmap
	 */
	private Bitmap bmp_bg = null;
	private Bitmap bmp_bg_small = null;
}
