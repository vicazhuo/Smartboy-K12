/** 文件作用：视图控制类
 *	创建时间：2012-11-17 下午7:49:28
 *  作者： 陈相伯 
 *  描述：
 */
package com.kingPadStudy.controller;

import java.io.File;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.constant.Constant;
import com.kingPadStudy.resource.ResourceLoader;
import com.kingPadStudy.views.BaseView;
import com.kingPadStudy.views.Chinese_Menmory_View;
import com.kingPadStudy.views.EnglishMemoryView;
import com.kingPadStudy.views.EnglishPhoneticView;
import com.kingPadStudy.views.FlashGuankanView;
import com.kingPadStudy.views.FlashLangduView;
import com.kingPadStudy.views.FlashView;
import com.kingPadStudy.views.MainView;
import com.kingPadStudy.views.MajorView;
import com.kingPadStudy.views.MenuView;
import com.kingPadStudy.views.ObjectiveQuizView;
import com.kingPadStudy.views.PoemReciteView;
import com.kingPadStudy.views.PoemTeachFlashView;
import com.kingPadStudy.views.ShiZiRememberView;
import com.kingPadStudy.views.StudyView;
import com.kingpad.EnglishDictationQuizEngine;
import com.kingpad.EnglishSubjectiveQuizEngine;
import com.kingpad.InitCompleted;
import com.kingpad.PrintEngine;
import com.kingpad.R;
import com.kingpad.StudyViewHandle;
import com.kingpad.SubjectiveGameEngine;
import com.utils.Util;

/**
 * 描述：视图控制类 注意：1，一共有三种视图：普通画画的，有LISTVIEW控件的，和学习视图三种；
 * 2，在controlView中，要能够选择是否销毁，当为学习视图时，上一个视图将不被销毁。
 * 3，不同的视图有不同的构造条件，因此在NEW的时候有一个函数来处理
 */
public class ViewController {
	/**
	 * 作用：初始化视图 时间：20122012-11-18下午1:01:51 void
	 */
	public static void intiView(Activity outActivity, Context context) {
		// 设置环境变量
		setActivity(outActivity);
		setContext(context);
		// view_record_record = null;
		// view_record_flash = null;
		// //System.out.println("结束、intiView。。。");
	}

	public static void stopView() {
		if (currentBaseView != null) {
			currentBaseView.recycle();
			System.gc();
			currentBaseView = null;
		}
	}
	
	/**
	 * 用于设置主视图使用
	 * @param view
	 * @param viewId
	 */
	public static void controllMainView(MainView view,int viewId){
		// 普通视图或者菜单视图，那么销毁
		if (currentBaseView != null) {
			currentBaseView.recycle();
		}
		currentBaseView = null;// 对象设置为空
		currentLinearLayout = null;// 对象设置为空
		currentRelativeLayout = null;
		setCurrentMainView(view);
		setCurrentID(viewId);
		System.gc(); // 系统回收
	
	}

	/**
	 * 作用：设置当前的视图，通过视图的字符串ID 时间：20122012-11-18下午1:03:20 void
	 * 
	 * @param viewId
	 */
	public static void controllView(int viewId) {
		// //System.out.println("进入、controllView。。。");
		if (viewId >= Constant.VIEW_STUDY) {
			// 如果是学习视图，那么上次的不销毁
			previousBaseView = currentBaseView;
			previousLinearLayout = currentLinearLayout;
			previousID = currentID;
			setCurrentView(viewId);
			setCurrentID(viewId);
		} else {
			// 普通视图或者菜单视图，那么销毁
			if (currentBaseView != null) {
				currentBaseView.recycle();
			}
			currentBaseView = null;// 对象设置为空
			currentLinearLayout = null;// 对象设置为空
			currentRelativeLayout = null;
			setCurrentView(viewId);
			setCurrentID(viewId);
			System.gc(); // 系统回收
		}
	}

	/**
	 * 函数作用：设置当前的目录下标 void
	 * 
	 * @param index_c
	 */
	public static void setIndex(int index_c) {
		index = index_c;
	}

	/*
	 * 读音视图的资源
	 */
	public static void setDuyinResource(String p, String y) {
		path = p;
		type = y;
	}

	/**
	 * 作用：在熟读古诗中进行切换 时间：2013-1-3 下午1:11:53 void
	 * 
	 * @param type
	 */
	public static void switchRecord(int type) {
		switch (type) {
		case 1:
			// 观看
			view_record_flash.setLandDuView(view_record_record);
			current_relativelayout_study = view_record_flash;
			activity.setContentView(current_relativelayout_study);
			break;
		case 2:
			// 朗读
			if (view_record_record == null) {
				// 第一次进入朗读视图
				ResourceLoader.showRecordRecord();
			} else {
				// 不是第一次了
				current_relativelayout_study = view_record_record;
				activity.setContentView(current_relativelayout_study);
			}
			break;
		}
	}

	/**
	 * 作用：重置熟读古诗的变量 时间：2013-1-3 下午1:59:15 void
	 */
	public static void resetRecord() {
		view_record_flash = null;
		view_record_record = null;
	}

	public static FlashGuankanView view_record_flash = null;
	public static FlashLangduView view_record_record = null;

	
	/**
	 * 设置当前的视图为主视图
	 */
	private static void setCurrentMainView(MainView mainView){
		currentBaseView = mainView;
		activity.setContentView(currentBaseView);
		KingPadStudyActivity.dismissWaitDialog();
	}
	
	
	
	/**
	 * 作用：创建一个当前的视图，并设置为主活动的显示视图 时间：20122012-11-18下午1:27:40 void
	 * 
	 * @param viewId
	 */
	private static void setCurrentView(int viewId) {
		// //System.out.println("进入、setCurrentView。。。");
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// //System.out.println("LayoutInflater完毕。。。");
		switch (viewId) {
		case Constant.VIEW_ENGLISH_Memory:
			// 英语预习
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			EnglishMemoryView EnmemoryView = (EnglishMemoryView) inflater
					.inflate(R.layout.layout_english_menmory, null);
			EnmemoryView.setStart(path, type);
			current_relativelayout_study = EnmemoryView;
			activity.setContentView(current_relativelayout_study);
			break;
		case Constant.VIEW_ENGLISH_DictationQuiz:
			// 英语拼写
			studyViewHandle = new EnglishDictationQuizEngine(activity, path,
					ResourceLoader.getMajorBackImageUrl(index), studyState,
					mode, new InitCompleted() {
						@Override
						public void doNext() {
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									activity.setContentView(studyViewHandle
											.getGameView());
								}
							});
							KingPadStudyActivity.dismissWaitDialog();
						};
					});

			break;
		case Constant.VIEW_ENGLISH_SUB:
			// 处理type="English/SubjectiveQuiz"
			studyViewHandle = new EnglishSubjectiveQuizEngine(activity, path,
					ResourceLoader.getMajorBackImageUrl(index), studyState,
					mode, new InitCompleted() {
						@Override
						public void doNext() {
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									activity.setContentView(studyViewHandle
											.getGameView());
								}
							});
							KingPadStudyActivity.dismissWaitDialog();
						};
					});
			break;
		case Constant.VIEW_FLASH_LANGDU:
			// 朗读视图
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			FlashLangduView flashLangduView = (FlashLangduView) inflater
					.inflate(R.layout.layout_flash_langdu, null);
			flashLangduView.setStart(index, background_poem, url_mp3,
					count_record, title_poem, author_poem, content_poem);
			current_relativelayout_study = flashLangduView;
			view_record_record = flashLangduView;
			activity.setContentView(current_relativelayout_study);
			break;
		case Constant.VIEW_FLASH_GUANKAN:
			// Flash视图
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			FlashGuankanView flashGuankanView = (FlashGuankanView) inflater
					.inflate(R.layout.layout_flash_guankan, null);
			flashGuankanView.setStart(index, url_flash, bg_frame, w_web, h_web,
					x_flash, y_flash, w_flash, h_flash, isControll);
			current_relativelayout_study = flashGuankanView;
			view_record_flash = flashGuankanView;
			activity.setContentView(current_relativelayout_study);
			break;
		case Constant.VIEW_FLASH:
			// Flash视图
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			FlashView flashView = (FlashView) inflater.inflate(
					R.layout.layout_flash, null);
			flashView.setControllerPositionY(positionY);
			flashView.setStart(index, url_flash, bg_frame, w_web, h_web,
					x_flash, y_flash, w_flash, h_flash, isControll,
					isFrontHidden, top_front, right_front, isWebViewHandle);
			current_relativelayout_study = flashView;
			activity.setContentView(current_relativelayout_study);
			break;
		case Constant.VIEW_PRINT:
			studyViewHandle = new PrintEngine(context, path,
					ResourceLoader.getMajorBackImageUrl(index),
					new InitCompleted() {
						@Override
						public void doNext() {
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									activity.setContentView(studyViewHandle
											.getGameView());
								}
							});
							current_relativelayout_study = (RelativeLayout) studyViewHandle
									.getGameView();
							KingPadStudyActivity.dismissWaitDialog();
						}
					});
			break;
		case Constant.VIEW_MAIN:
			// 主界面视图
			
//			d
			currentBaseView = new MainView(context, activity);
			
			
			activity.setContentView(currentBaseView);
			KingPadStudyActivity.dismissWaitDialog();
			break;
		case Constant.VIEW_MENU:
			// 菜单视图
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			MenuView menuView = (MenuView) inflater.inflate(R.layout.menuframe,
					null);
			menuView.setContent(index);
			currentRelativeLayout = menuView;
			activity.setContentView(currentRelativeLayout);
			break;
		case Constant.VIEW_STUDY:
			// 学习视图
			currentBaseView = new StudyView(context);
			activity.setContentView(currentBaseView);
			break;
		case Constant.VIWE_MAJOR:
			// System.out.println("进入setCurrentView的VIWE_MAJOR情况中。。。");
			// 科目视图
			majorView = new MajorView(context, major);
			currentBaseView = majorView;
			activity.setContentView(currentBaseView);
			break;
		case Constant.VIEW_Chinese_Memorization:
			// 语文预习
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			Chinese_Menmory_View memoryView = (Chinese_Menmory_View) inflater
					.inflate(R.layout.layout_chinese_menmory, null);
			memoryView.setStart(path, type);
			current_relativelayout_study = memoryView;
			activity.setContentView(current_relativelayout_study);
			break;
		case Constant.VIEW_POEM_RECITE:
			// 古诗背诵
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			PoemReciteView poem_recite = (PoemReciteView) inflater.inflate(
					R.layout.poem_recite, null);
			poem_recite.setStart(path, type);
			current_relativelayout_study = poem_recite;
			activity.setContentView(current_relativelayout_study);
			break;
		case Constant.VIEW_SHIZI:
			// 识字
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ShiZiRememberView view_shizi = (ShiZiRememberView) inflater
					.inflate(R.layout.layout_shizi, null);
			view_shizi.setStart(path, type, index, studyState);
			currentLinearLayout = view_shizi;
			activity.setContentView(currentLinearLayout);
			break;
		case Constant.VIEW_BOOK_SET:
			Toast.makeText(activity, "点击设置教材", 0).show();
			break;
		case Constant.VIEW_BOOK_UPDATE:
			Toast.makeText(activity, "点击更新教材", 0).show();
			break;
		case Constant.VIEW_MYKINGPAD:
			Toast.makeText(activity, "点击我的睿学", 0).show();
			break;
		case Constant.VIEW_SUBJECTIVE_QUIZ:
			// 处理type="SubjectiveQuiz" 的视图
			studyViewHandle = new SubjectiveGameEngine(activity, path,
					ResourceLoader.getMajorBackImageUrl(index), studyState,
					mode, new InitCompleted() {
						@Override
						public void doNext() {
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									activity.setContentView(studyViewHandle
											.getGameView());
								}
							});
							KingPadStudyActivity.dismissWaitDialog();
						};
					});
			break;
		case Constant.VIEW_OBJECTIVEQUIZ:
			// 处理type="ObjectiveQuiz" 的视图
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ObjectiveQuizView view_ojective = (ObjectiveQuizView) inflater
					.inflate(R.layout.layout_objuectivequiz, null);
			view_ojective.setStart(path, type, index, studyState);
			currentLinearLayout = view_ojective;
			activity.setContentView(currentLinearLayout);
			break;
		case Constant.VIEW_ENGLISH_Phonetic:
			// 英语矫正
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			EnglishPhoneticView englishPhoneticView = (EnglishPhoneticView) inflater
					.inflate(R.layout.layout_english_phonetic, null);
			englishPhoneticView.setStart(path, type, index);
			current_relativelayout_study = englishPhoneticView;
			activity.setContentView(current_relativelayout_study);
			break;
		}
	}

	/*
	 * 进入资源的类型 0——普通 1——重点 2——次重点
	 */
	public static int studyState = 0;
	private static StudyViewHandle studyViewHandle; // 获取不同type视图的接口

	/**
	 * 函数作用：返回上个视图 void
	 */
	public static void backToNormal() {
		flash_view = null;
		flash_box.destory();
		if (currentRelativeLayout != null) {
			activity.setContentView(currentRelativeLayout);
		} else {
			if (currentLinearLayout != null) {
				activity.setContentView(currentLinearLayout);
			} else {
				if (currentBaseView != null) {
					activity.setContentView(currentBaseView);
				}
			}
		}
	}

	/**
	 * 函数作用：从学习相对布局中返回 void
	 */
	public static void backToNormal_from_relative() {
		// 返回
		// System.out.println("current_relativelayout_study="+current_relativelayout_study);
		// System.out.println("currentRelativeLayout="+currentRelativeLayout);

		current_relativelayout_study = null;
		if (currentRelativeLayout != null) {
			// System.out.println("设置当前视图为currentRelativeLayout");
			activity.setContentView(currentRelativeLayout);
		} else {
			if (currentLinearLayout != null) {
				// System.out.println("设置当前视图为currentLinearLayout");
				activity.setContentView(currentLinearLayout);
			} else {
				if (currentBaseView != null) {
					// System.out.println("设置当前视图为currentBaseView");
					activity.setContentView(currentBaseView);
				}
			}
		}
	}

	public static RelativeLayout getCurrentRelativeLayout() {
		return currentRelativeLayout;
	}

	public static void backToNormal_print() {
		backToNormal_from_relative();
	}

	/**
	 * 函数作用：返回上个视图 void
	 */
	public static void backToNormal_record() {
		flash_view = null;
		chinese_read_flash_view = null;
		chinese_read_view = null;
		flash_box.destory();
		hasReadFlash = false;
		hasReadRecord = false;
		if (currentRelativeLayout != null) {
			activity.setContentView(currentRelativeLayout);
		} else {
			if (currentLinearLayout != null) {
				activity.setContentView(currentLinearLayout);
			} else {
				if (currentBaseView != null) {
					activity.setContentView(currentBaseView);
				}
			}
		}
	}

	public static void backToNormalDuyin() {
		currentLinearLayout = null;
		if (currentRelativeLayout != null) {
			activity.setContentView(currentRelativeLayout);
		} else {
			if (currentLinearLayout != null) {
				activity.setContentView(currentLinearLayout);
			} else {
				if (currentBaseView != null) {
					activity.setContentView(currentBaseView);
				}
			}
		}
	}

	public static BaseView getcurrentBaseView() {
		return currentBaseView;
	}

	public static void setcurrentBaseView(BaseView currentBaseView) {
		ViewController.currentBaseView = currentBaseView;
	}

	public static LinearLayout getCurrentLinearLayout() {
		return currentLinearLayout;
	}

	public static void setCurrentLinearLayout(LinearLayout currentLinearLayout) {
		ViewController.currentLinearLayout = currentLinearLayout;
	}

	public LinearLayout.LayoutParams getParams() {
		return params;
	}

	public void setParams(LinearLayout.LayoutParams params) {
		this.params = params;
	}

	public static Context getContext() {
		return context;
	}

	public static void setContext(Context context) {
		ViewController.context = context;
	}

	public static Activity getActivity() {
		return activity;
	}

	public static void setActivity(Activity activity) {
		ViewController.activity = activity;
	}

	public static int getCurrentID() {
		return currentID;
	}

	public static void setCurrentID(int currentID) {
		ViewController.currentID = currentID;
	}

	static View chinese_read_view = null;

	static View chinese_read_flash_view = null;

	static View flash_view = null;

	static PoemTeachFlashView flash_box = null;

	/*
	 * 当前视图
	 */
	static BaseView currentBaseView = null;
	/*
	 * 当前组合布局
	 */
	static LinearLayout currentLinearLayout = null;
	static RelativeLayout currentRelativeLayout = null;
	/*
	 * 当前学习的RELATIVELAYOUT
	 */
	private static RelativeLayout current_relativelayout_study = null;
	LinearLayout.LayoutParams params = null;
	/*
	 * 当前视图或组合布局的ID
	 */
	static int currentID = -1;
	/*
	 * 上个视图
	 */
	static BaseView previousBaseView = null;
	/*
	 * 上个组合布局
	 */
	static LinearLayout previousLinearLayout = null;
	/*
	 * 上个视图或组合布局的ID
	 */
	static int previousID = -1;
	/*
	 * 环境变量
	 */
	static Context context = null;
	static Activity activity = null;
	/*
	 * 下标
	 */
	public static int index = 0;

	static String path;
	static String type;

	/**
	 * 播放声音
	 * 
	 * @param soundPath
	 *            ：声音文件在sd卡上的路径
	 */
	private static void playBackgroundSound(String soundPath) {
		if (soundPath == null) {
			return;
		}
		File file = new File(soundPath);
		if (file.exists()) {
			// 如果文件存在
			// System.out.println("古诗背景音乐存在..............");
			try {
				Util.playSound(soundPath, true);
			} catch (Exception e) {

			}
		} else {
			// System.out.println("古诗背景音乐不存在..............");
			// 不存在
		}

	}

	/**
	 * 停止声音
	 * 
	 * @param soundPath
	 *            ：声音文件在sd卡上的路径
	 */
	private static void stopPlayBackgroundSound() {
		try {
			Util.releasePlayer();
		} catch (Exception e) {

		}
	}

	/*
	 * 处理进度条的chuliqi
	 */
	Handler handler;
	/*
	 * 录音是否开始
	 */
	private static boolean isRecordStart = false;
	/*
	 * 录音当前的进度
	 */
	private static int progress_current = 0;
	/*
	 * 录音进度条
	 */
	private static ProgressBar progressHorizontal;
	/*
	 * 进度秒数
	 */
	private static TextView text_time;
	/*
	 * 是否已经录下一段音
	 */
	private static boolean isRecord = false;
	/*
	 * 以录音的时间
	 */
	private static int time_recorded = 0;
	/*
	 * 是否正在播放录音
	 */
	public static boolean isPlayingRecord = false;
	/*
	 * 进度条父布局
	 */
	private static LinearLayout layout_progress;
	/*
	 * 播放线程是否终止的标志
	 */
	private static boolean isPlayThreadStop = false;
	/*
	 * 录音线程是否终止的标志
	 */
	private static boolean isRecordThreadStop = false;
	/*
	 * 录音是否正在进行
	 */
	public static boolean isRecording = false;
	/*
	 * 是否可以点击“完成录音”按钮
	 */
	public static boolean canTouchFinish = false;
	/*
	 * 是否可以点“开始录音”
	 */
	public static boolean canTouchbegin = true;
	/*
	 * 是否有了录音FLASH 界面
	 */
	public static boolean hasReadFlash = false;
	/*
	 * 是否有了录音 界面
	 */
	public static boolean hasReadRecord = false;

	private static String mode;

	/**
	 * 函数作用：设置mode值 void
	 * 
	 * @param mode
	 */
	public static void setMode(String m) {
		mode = m;
	}

	/*
	 */
	private static Bitmap bmp_bg = null;
	private static Bitmap bmp_bg_small = null;

	private static String url_flash;
	private static String bg_frame;
	private static int x_flash;
	private static int y_flash;
	private static int w_flash;
	private static int h_flash;
	private static int w_web;
	private static int h_web;
	private static boolean isControll;

	/**
	 * 函数作用：设置Flash数据 void
	 * 
	 * @param filePath
	 * @param bgPath
	 * @param xFlashNormal
	 * @param yFlashNormal
	 * @param width
	 * @param height
	 * @param b
	 */
	public static void setFlashData(String filePath, String bgPath,
			int width_web, int height_web, int xFlashNormal, int yFlashNormal,
			int width_flash, int height_flash, boolean b) {
		// System.out.println("width_web="+width_web);
		// System.out.println("height_web="+height_web);
		// System.out.println("xFlashNormal="+xFlashNormal);
		// System.out.println("yFlashNormal="+yFlashNormal);
		// System.out.println("width_flash="+width_flash);
		// System.out.println("height_flash="+height_flash);
		url_flash = filePath;
		bg_frame = bgPath;
		x_flash = xFlashNormal;
		y_flash = yFlashNormal;
		w_flash = width_flash;
		h_flash = height_flash;
		w_web = width_web;
		h_web = height_web;
		isControll = b;
	}

	/**
	 * 函数作用：设置朗读数据 void
	 * 
	 * @param bg_poem
	 * @param path_mp3
	 * @param time_record
	 * @param title
	 * @param author
	 * @param content
	 */
	public static void setFlashRecordData(String bg_poem, String path_mp3,
			int time_record, String title, String author, String content) {
		background_poem = bg_poem;
		url_mp3 = path_mp3;
		count_record = time_record;
		title_poem = title;
		author_poem = author;
		content_poem = content;
	}

	static String background_poem;
	static String url_mp3;
	static int count_record;
	static String title_poem;
	static String author_poem;
	static String content_poem;
	static int major;
	private static boolean isFrontHidden = false;
	private static int top_front = -1;
	private static int right_front = -1;
	private static MajorView majorView;

	/**
	 * 作用：设置科目 时间：2013-1-2 下午9:00:04 void
	 * 
	 * @param arg1
	 */
	public static void setMajor(int m) {
		major = m;
	}

	/**
	 * 作用：设置FLASH透视图中是否有遮挡物 时间：2013-1-9 上午11:47:12 void
	 * 
	 * @param b
	 */
	public static void setIsFrontHidden(boolean b) {
		isFrontHidden = b;
	}

	/**
	 * 作用： 时间：2013-1-9 下午2:11:41 void
	 * 
	 * @param i
	 * @param j
	 */
	public static void setFrontPosition(int i, int j) {
		top_front = i;
		right_front = j;
	}

	public static boolean isBigLoad = false;
	public static boolean isWebViewHandle = false;

	/**
	 * 作用：关闭学科视图中的数据加载条 时间：2013-1-9 下午3:15:54 void
	 */
	public static void dismissMajorViewDialog() {
		isBigLoad = true;
		if (majorView != null) {
			majorView.dismissDialog();
		}
	}

	/**
	 * 作用：设置 WebView 的处理 时间：2013-1-9 下午4:45:43 void
	 * 
	 * @param b
	 */
	public static void setIsWebViewHandle(boolean b) {
		isWebViewHandle = b;
	}

	/**
	 * 作用：设置Flash的控制条Y坐标 时间：2013-1-28 下午3:45:30 void
	 * 
	 * @param i
	 */
	public static void setControllerPositionY(int y) {
		positionY = y;
	}

	private static int positionY = -1;

}
