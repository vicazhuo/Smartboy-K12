package com.kingpad;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.BasicBean;
import com.bean.ContentStateBean;
import com.bean.TopicBean;
import com.constant.Constant;
import com.data.MetaUtilData;
import com.kingPadStudy.controller.ViewController;
import com.kingPadStudy.resource.ResourceLoader;
import com.kingpad.SubjectiveGameEngine.Action;
import com.kingpad.SubjectiveGameEngine.ButtonListener;
import com.meta.Answer;
import com.meta.Data;
import com.meta.Item;
import com.meta.Items;
import com.meta.Question;
import com.meta.impl.ItemImpl;
import com.meta.impl.MetaUtil;
import com.utils.DatabaseAdapter;
import com.net.LoadData;
import com.net.RequestListener;
import com.net.RequestParameter;
import com.utils.Util;
import com.views.GameView;

public class EnglishSubjectiveQuizEngine implements StudyViewHandle{

	private Context context;
	private LayoutInflater mInflater;
	private Timer timer;
	private TimerTask timerTask;
	private View mainView;
	private InitCompleted initCompleted;	//界面初始化之后调用的接口
	
	//提示面板
	private RelativeLayout mHintLayout;		
	private TextView mHintTextView;
	private ImageView mButtonYes;			//提示面板上面的确定按钮
	
	private RelativeLayout mMainLayout;		//主要操作区，可以设置背景
	private TextView mIntro;				//标题
	private ImageView horn;					//喇叭
	private LinearLayout mCheckSelfLayout;	//自我评定界面，包含：完全不会，模糊，完全掌握
	private LinearLayout mJumpLayout;		//上下题界面，包含，上下题的按钮
	private GameView mBackView,				//返回键的动画
	 				 mCheckAns,				//核对答案
	 				 mBtnLast,				//上一题
	 				 mBtnNext,				//下一题
	 				 mChkBad,				//完全不会
	 				 mChkJust,				//模糊
	 				 mChkGood,				//完全掌握
	 				 mLight;				//闪灯
	//问题数据控件
	private TextView mTextStudy1, mTextStudy2;
	private ImageView mImgStudy;			//学习的图片内容
	//答案数据控件
	private TextView mTextAnswer;
	private ImageView mImgAnswer;
	//学习记录控件及相应的计数变量
	private TextView mTextTotalCount,		//本章所有题目
   					 mTextNotAvi,			//未完成的题目
   					 mTextNotUnd,			//不理解的题目
   					 mTextVague,			//模糊的题目
   					 mTextUnd;				//理解的题目
	private int nTotalCount,				//学习记录中总的题目数
				nNotAvi,					//未完成题目数量
				nNotUnd,					//完全不会的题数
				nVague,						//模糊数
				nUnder;						//完全掌握的题数
	
	private boolean hasChecked = false;				//是否已经对自己的学习进行记录,true-已经记录
	
	private ArrayList<Items> itemsList;				//当前使用的items list数据
	private ArrayList<Item> mCurrentStudyItemList;	//当前使用的item list数据
	private int nItemsIndex, nItemIndex;			//当前学习题目的指针
	
	private ArrayList<Integer> mTaskTopicIndex;		//当前需要做题的题号数组
	private int pCurrentTopicIndex;					// 当前题目的题号的指针
	
	// 从数据库获取的学习记录的题目编号
	private ArrayList<TopicBean> topicBeans;
	
	// 需要重新学习的题目数量
	private int nReStudyCount;
	
	private String currentSoundFile;
	private int currentSoundType;					//0-播放currentSoundFile中的语音，1-播放问题语音，2-播放答案语音
	
	public static final int NORMAL_STUDY = 0;		//正常学习状态
	public static final int IMP_STUDY	 = 1;		//重点复习
	public static final int SCD_STUDY 	 = 2;		//次重点复习
	public static final int RESTART		 = 3;		//重新学习
	public static int CURRENT_STUDY_MODEL;			//当前学习模式
	
	//一次完整的正常学习有4个阶段，按照顺序是：
	//正常学习->学习遗漏题目->重复学习->浏览题目
	private static final int PHASE_NORMAL = 0;		//正常学习阶段
	private static final int PHASE_RECORD = 1;		//接题阶段
	private static final int PHASE_REPEAT = 2;		//重复学习阶段
	private static final int PHASE_SCAN = 3;		//浏览题目阶段
	private static int CURRENT_PHASE = 0;
	
	private DatabaseAdapter mDbAdapter;				//数据库操作
	
	private String mCurrentXmlPath;					//当前学习模块的路径	
	private String mode;							//mode参数
	
	private Bitmap bgBitmap;						// 主背景图片
	private Bitmap studyBitmapBg;					//学习区域背景图片
	private Bitmap mCrtStudyBitmap,
				   mCrtAnswerBitmap;				//当前学习的问题图片和答案图片
	
	
	
	
	public EnglishSubjectiveQuizEngine(Context context, String xmlFilePath
			, String bgPath, int studyModel, String mode, InitCompleted initCompleted){
		
		CURRENT_PHASE = PHASE_NORMAL;
		CURRENT_STUDY_MODEL = studyModel;
		if (mode == null)
			this.mode = "";
		else {
			this.mode = mode;
		}
		
		this.mCurrentXmlPath = xmlFilePath;
		this.context = context;
		this.initCompleted = initCompleted;
		this.mInflater = LayoutInflater.from(context);
		this.mDbAdapter = DatabaseAdapter.getInstance(context);
		
		mainView = mInflater.inflate(R.layout.english_sub_layout, null);
		bgBitmap = BitmapFactory.decodeFile(bgPath);
		if (null != bgBitmap)
			mainView.setBackgroundDrawable(new BitmapDrawable(bgBitmap));
		
		
		initViews(mainView);
		initAnimation();
		setListener();
		
		loadData(xmlFilePath);
	}
	
	private void initViews(View mainView){
		mMainLayout		= (RelativeLayout) mainView.findViewById(R.id.control_part);
		mBackView 		= (GameView) mainView.findViewById(R.id.button_back);
		mIntro			= (TextView) mainView.findViewById(R.id.intro);
		horn			= (ImageView) mainView.findViewById(R.id.imageview_play_music);
		mTextStudy1		= (TextView) mainView.findViewById(R.id.text_study1);
		mTextStudy2		= (TextView) mainView.findViewById(R.id.text_study2);
		mImgStudy		= (ImageView) mainView.findViewById(R.id.image_study);
		mCheckAns		= (GameView) mainView.findViewById(R.id.imageview_check_answer);
		mTextAnswer		= (TextView) mainView.findViewById(R.id.text_answer);
		mImgAnswer		= (ImageView) mainView.findViewById(R.id.image_answer);
		mCheckSelfLayout= (LinearLayout) mainView.findViewById(R.id.check_layout);
		mChkBad			= (GameView) mainView.findViewById(R.id.check_bad);
		mChkJust		= (GameView) mainView.findViewById(R.id.check_just);
		mChkGood		= (GameView) mainView.findViewById(R.id.check_good);
		mLight			= (GameView) mainView.findViewById(R.id.light);
		mJumpLayout		= (LinearLayout) mainView.findViewById(R.id.jump_layout);
		mBtnLast		= (GameView) mainView.findViewById(R.id.button_last);
		mBtnNext		= (GameView) mainView.findViewById(R.id.button_next);
		mTextTotalCount	= (TextView) mainView.findViewById(R.id.textview_total_count);
		mTextNotAvi		= (TextView) mainView.findViewById(R.id.textview_not_avi);
		mTextNotUnd		= (TextView) mainView.findViewById(R.id.textview_not_understand);
		mTextVague		= (TextView) mainView.findViewById(R.id.textview_vague);
		mTextUnd		= (TextView) mainView.findViewById(R.id.textview_understand);
		mHintLayout		= (RelativeLayout) mainView.findViewById(R.id.hint_layout);
		mButtonYes		= (ImageView) mainView.findViewById(R.id.button_yes);
		mHintTextView	= (TextView) mainView.findViewById(R.id.text_hint);
	}
	
	private void setListener(){
		ButtonListener listener = new ButtonListener();
		mBackView.setOnClickListener(listener);
		mBtnLast.setOnClickListener(listener);
		mBtnNext.setOnClickListener(listener);
		mChkBad.setOnClickListener(listener);
		mChkGood.setOnClickListener(listener);
		mChkJust.setOnClickListener(listener);
		mCheckAns.setOnClickListener(listener);
		horn.setOnClickListener(listener);
		mButtonYes.setOnClickListener(listener);
	}

	private void loadData(final String xmlFilePath){
		RequestParameter parameter = new RequestParameter();
		parameter.add("type", "ObjectiveQuiz");
		parameter.add("path", xmlFilePath);
		
        LoadData.loadData(Constant.META_UTIL_DATA, parameter, new RequestListener() {
			@Override
			public void onComplete(Object obj) {
				MetaUtilData dataUtil = (MetaUtilData) obj;
				Data data = dataUtil.getData();
				
//				MetaUtil.printData(data);
				
				itemsList = data.getItemsList();
				studyBitmapBg = BitmapFactory.decodeFile(data.getAttributeValue("bg"));
				if (null != studyBitmapBg)
					mMainLayout.setBackgroundDrawable(new BitmapDrawable(studyBitmapBg));
				
				initMp3Files();
				hideStudyWidget();
				initModel(xmlFilePath);
				
				initCompleted.doNext();
			}

			@Override
			public void onError(String error) {
				Log.e("解析错误", error);
			}
        });
	}
	
	private void initMp3Files(){
		Util.initSoundPool(context);
		
		//初始化问题声音和答案声音数据
		for (int i=0, n=itemsList.size(); i<n; i++){
			ArrayList<Item> itemList = itemsList.get(i).getItemList();
			for (int j=0, m=itemList.size(); j<m; j++){
				Item item = itemList.get(j);
				String uri = item.getQuestionList().get(0).getAttributeValue("soundFile");
				if (null != uri)
					Util.addMp3List(context, uri);
				uri = item.getAnswerList().get(0).getAttributeValue("soundFile");
				
				if (null != uri)
					Util.addAnswerMp3List(context, uri);
			}
		}
	}
	
	/**
	 * 隐藏主学习界面中的控件
	 */
	private void hideStudyWidget(){
		horn.setVisibility(View.INVISIBLE);
		mTextStudy1.setVisibility(View.INVISIBLE);
		mTextStudy2.setVisibility(View.INVISIBLE);
		mImgStudy.setVisibility(View.INVISIBLE);
		mCheckAns.setVisibility(View.INVISIBLE);
		mTextAnswer.setVisibility(View.INVISIBLE);
		mImgAnswer.setVisibility(View.INVISIBLE);
		mCheckSelfLayout.setVisibility(View.INVISIBLE);
		mLight.setVisibility(View.INVISIBLE);
		mJumpLayout.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * 根据当前请求的studyModel来初始化学习模式
	 */
	private void initModel(String xmlFilePath){
		//从数据库中提取学习记录
		topicBeans = mDbAdapter.getRecord(xmlFilePath);
		
		initTopicIndex();
		
		if (CURRENT_STUDY_MODEL == RESTART){
			//重新开始学习，清除学习记录
//			mDbAdapter.deleteOnePathRecord(xmlFilePath);
			initFirstTime();
			
		}else if (CURRENT_STUDY_MODEL == IMP_STUDY) {
			//重点复习
			recoverRecord();
			removeItem(true);
			addTopicIndex();
			
		}else if (CURRENT_STUDY_MODEL == SCD_STUDY) {
			//次重点复习
			recoverRecord();
			removeItem(false);
			addTopicIndex();
			
		}else if (CURRENT_STUDY_MODEL == NORMAL_STUDY) {
			//正常学习状态
			recoverRecord();
			
			int totalTopics = getTotalTopicCount();
			if ("".equals(mode)){
				initFirstTime();
				
			}else if (null != topicBeans && totalTopics > topicBeans.size()){
				//还有未完成的题目，进入接题阶段
				enterContinueStudyPhase();
				
			}else if (null != topicBeans && totalTopics==topicBeans.size() && hasNotUnderstand()) {
				//进入重复2遍学习阶段
				enterRepeatTwice(false);
				
			}else if (null != topicBeans && totalTopics==topicBeans.size() && !hasNotUnderstand()) {
				//进入浏览题目阶段
				enterScan();
				
			}else {
				//第一次进入，直接做题
				initFirstTime();
			}
		}
	}
	
	/**
	 * 检查原来题目中是否有还没有掌握和模糊的题目
	 * @return：true-有，false-所有题目都已经掌握
	 */
	private boolean hasNotUnderstand(){
		boolean result = false;
		for(int i=0, n=itemsList.size(); i<n; i++){
			for (int j=0, m=itemsList.get(i).getItemList().size(); j<m; j++){
				if (itemsList.get(i).getItemList().get(j).getFinalState() != ItemImpl.FLAG_UNDERSTAND){
					result = true;
					break;
				}
			}
		}
		
		return result;
	}
	
	private void initFirstTime(){
		initRecordFrame();		//初始化学习记录面板
		//初始化当前做题的题号
		mTaskTopicIndex = new ArrayList<Integer>();
		int topicIndex = 1;
		for (int i=0, n=itemsList.size(); i<n; i++){
			for (int j=0, m=itemsList.get(i).getItemList().size(); j<m; j++){
				mTaskTopicIndex.add(topicIndex++);
			}
		}
		pCurrentTopicIndex = 0;
		nItemIndex = 0;
		nItemsIndex = 0;
		
		mHintLayout.setVisibility(View.VISIBLE);
		mHintTextView.setText(com.kingPadStudy.constant.Constant.TEXT_BEGIN_TO_LEARN);
		Util.playBackGroundSound(context, "hint_to_learn.mp3",false);  //开始筛查
	}
	
	/**
	 * 恢复做题记录，将数据库中以前做题的记录恢复到程序中
	 */
	private void recoverRecord(){
		//将第一次和最后一次的学习记录复原到原items list中
		if (topicBeans!=null && topicBeans.size()!=0
				&& itemsList!=null && itemsList.size()!=0){
			
			for (TopicBean bean : topicBeans) {
				Item item = getRightItemFormOriginalItemsList(itemsList, bean.number);
				if (item != null) {
					item.setFirstState(bean.firstState);
					item.setFinalState(bean.finalState);
				}
			}
		}
	}
	
	/**
	 * 根据题号，从原items list 中获取对应的item
	 * @param topicIndex：题号
	 * @return：题号对应的item
	 */
	private Item getRightItemFormOriginalItemsList(ArrayList<Items> origList, int topicIndex){
		Item item = null;
		int perCount = 0;
		for (int i=0, n=origList.size(); i<n; i++){
			if (topicIndex <= (origList.get(i).getItemList().size()+perCount)){
				item = origList.get(i).getItemList().get(topicIndex-perCount-1);
				break;
			}else {
				perCount += origList.get(i).getItemList().size();
			}
		}
		
		return item;
	}
	
	/**
	 * 根据isImportant移除item
	 * @param isUnderstand：true-移除掌握的item，false-移除未掌握和模糊的item
	 */
	private void removeItem(boolean isUnderstand){
		Util.clearMp3();
		Util.initSoundPool(context);
		
		for (int i=0, n=itemsList.size(); i<n; i++){
			ArrayList<Item> itemList = itemsList.get(i).getItemList();
			for (int j=0, m=itemList.size(); j<m; j++){
				Item item = itemList.get(j);
				if (isUnderstand){
					if (item.getFirstState() == ItemImpl.FLAG_UNDERSTAND) {
						itemList.remove(j);
						j--;
						m--;
					}else {
						addMp3File(item);
					}
				}else {
					if (item.getFirstState() != ItemImpl.FLAG_UNDERSTAND) {
						itemList.remove(j);
						j--;
						m--;
					}else {
						addMp3File(item);
					}
				}
			}
		}
	}
	
	private void addMp3File(Item item){
		//重新初始化问题和答案的声音文件
		String uri = item.getQuestionList().get(0).getAttributeValue("soundFile");
		if (null != uri)
			Util.addMp3List(context, uri);
		uri = item.getAnswerList().get(0).getAttributeValue("soundFile");
		if (null != uri)
			Util.addAnswerMp3List(context, uri);
	}
	
	private void addTopicIndex(){
		//有一些题目被删除了，需要重新题目编号
		initTopicIndex();
		
		mTaskTopicIndex = new ArrayList<Integer>();
		
		for (int i=0,n=itemsList.size(); i<n; i++){
			ArrayList<Item> itemList = itemsList.get(i).getItemList();
			for (int j=0, m=itemList.size(); j<m; j++){
				Item item = itemList.get(j);
				mTaskTopicIndex.add(item.getTopicIndex());
				item.setFinalState(ItemImpl.FLAG_NOT_DO);
				item.setSecondFinalState(ItemImpl.FLAG_NOT_DO);
			}
		}
		
		
		pCurrentTopicIndex = 0;
		ArrayList<Integer> coordinates = convertTpIndex2ItIndex(mTaskTopicIndex.get(pCurrentTopicIndex));
		nItemIndex = coordinates.get(0);
		nItemsIndex = coordinates.get(1);
		
		initRecordFrame();		//初始化学习记录
		
		//处理界面显示 进入吊牌模式 显示文字以及播放声音
		mHintLayout.setVisibility(View.VISIBLE);
		mHintTextView.setText(com.kingPadStudy.constant.Constant.TEXT_BEGIN_TO_LEARN);
		Util.playBackGroundSound(context, "hint_to_learn.mp3",false);  //开始筛查
	}
	
	/**
	 * 将题号转换为nItemIndex和nItemsIndex
	 * @param topicIndex：要转换的题号
	 * @return：含有nItemIndex和nItemsIndex的数组，第0号元素是nItemIndex，第一号元素是nItemsIndex
	 */
	private ArrayList<Integer> convertTpIndex2ItIndex(int topicIndex){
		ArrayList<Integer> data = new ArrayList<Integer>();
		int perCount = 0;
		
		for (int i=0, n=itemsList.size(); i<n; i++){
			int size = itemsList.get(i).getItemList().size();
			if (topicIndex <= (size + perCount)) {
				data.add(topicIndex - perCount - 1);
				data.add(i);
				break;
			} else {
				perCount += size;
			}
		}
		
		return data;
	}
	
	
	/**
	 * 给原数据初始化题号
	 */
	private void initTopicIndex(){
		int topicIndex = 1;
		for (int i=0, n=itemsList.size(); i<n; i++){
			for (int j=0, m=itemsList.get(i).getItemList().size(); j<m; j++){
				Item item = itemsList.get(i).getItemList().get(j);
				item.setTopicIndex(topicIndex++);
			}
		}
	}
	
	/**
	 * 初始化学习记录面板
	 */
	private void initRecordFrame(){
		nTotalCount = 0;
		// 从数据库中提取学习记录
		topicBeans = mDbAdapter.getRecord(mCurrentXmlPath);
		
		for (Items items : itemsList){
			nTotalCount += items.getItemList().size();
		}
		
		nNotUnd		= 0;
		nUnder		= 0;
		nVague		= 0;
		
		if (topicBeans != null && topicBeans.size() != 0){
			for (TopicBean bean : topicBeans) {
				switch (bean.finalState) {
				case ItemImpl.FLAG_NOT_UND:
					nNotUnd += 1;
					break;
				case ItemImpl.FLAG_VAGUE:
					nVague += 1;
					break;
				case ItemImpl.FLAG_UNDERSTAND:
					nUnder += 1;
					break;
				default:
					break;
				}
			}
		}
		
		if (nTotalCount >= (nNotUnd+nVague+nUnder)){
			nNotAvi = nTotalCount - nNotUnd - nVague - nUnder;
		}
			
		refreshRecordFrame(nNotAvi, nNotUnd, nVague, nUnder);
	}
	
	/**
	 * 取得本次题目原题的总数量
	 * @return
	 */
	private int getTotalTopicCount(){
		int totalTopics = 0;
		for (int i=0,n=itemsList.size(); i<n; i++){
			totalTopics += itemsList.get(i).getItemList().size();
		}
		return totalTopics;
	}
	
	/**
	 * 进入接题阶段，根据topicBeans来初始化数据
	 */
	private void enterContinueStudyPhase(){
		//TODO
		Log.e("阶段信息", "进入接题阶段");
		
		CURRENT_PHASE = PHASE_RECORD;
		
		
		//初始化题号
		mTaskTopicIndex = new ArrayList<Integer>();
		for (int i=0, n=itemsList.size(); i<n; i++){
			for (int j=0, m=itemsList.get(i).getItemList().size(); j<m; j++){
				Item item = itemsList.get(i).getItemList().get(j);
				if (item.getFinalState() == ItemImpl.FLAG_NOT_DO){
					mTaskTopicIndex.add(convert2Index(itemsList, i, j));
				}
			}
		}
		pCurrentTopicIndex = 0;
		ArrayList<Integer> coordinates = convertTpIndex2ItIndex(mTaskTopicIndex.get(pCurrentTopicIndex));
		nItemIndex = coordinates.get(0);
		nItemsIndex = coordinates.get(1);
		initRecordFrame();		//初始化学习记录
		
		//处理界面显示 进入吊牌模式 显示文字以及播放声音
		mHintLayout.setVisibility(View.VISIBLE);
		mHintTextView.setText(com.kingPadStudy.constant.Constant.TEXT_REMAIN_SELECT);
		Util.playBackGroundSound(context, "hint_remani_select.mp3",false); //还有没筛完
	}
	
	/**
	 * 进入重复学习2遍阶段
	 * @param isDataFromRepeat：重复学习阶段的题目是否是从上一次重复学习阶段的题目当中来寻找。true-是
	 */
	private void enterRepeatTwice(boolean isDataFromLastRepeat){
		// TODO
		Log.e("阶段信息", "进入重复学习2遍阶段");
		
		CURRENT_PHASE = PHASE_REPEAT;
		nReStudyCount = 0;
		
		//初始化题号，并且清空程序中的学习记录
		ArrayList<Integer> topicIndexList = new ArrayList<Integer>();
		if (isDataFromLastRepeat){
			for (int topicIndex=0, n=mTaskTopicIndex.size()/2; topicIndex<n; topicIndex++){
				Item item = getRightItemFormOriginalItemsList(itemsList, mTaskTopicIndex.get(topicIndex));
				if (item.getFinalState()!=ItemImpl.FLAG_UNDERSTAND || item.getSecondFinalState()!=ItemImpl.FLAG_UNDERSTAND){
					topicIndexList.add(item.getTopicIndex());
					item.setFinalState(ItemImpl.FLAG_NOT_DO);
					item.setSecondFinalState(ItemImpl.FLAG_NOT_DO);
					nReStudyCount++;
				}
			}
			
		}else {
			for (int i=0, n=itemsList.size(); i<n; i++){
				for (int j=0, m=itemsList.get(i).getItemList().size(); j<m; j++){
					Item item = itemsList.get(i).getItemList().get(j);
					if(item.getFinalState()==ItemImpl.FLAG_NOT_UND || item.getFinalState()==ItemImpl.FLAG_VAGUE){
						topicIndexList.add(item.getTopicIndex());
						item.setFinalState(ItemImpl.FLAG_NOT_DO);
						item.setSecondFinalState(ItemImpl.FLAG_NOT_DO);
						nReStudyCount++;
					}
				}
			}
		}
		mTaskTopicIndex = topicIndexList;
		mTaskTopicIndex.addAll(mTaskTopicIndex);
		
		pCurrentTopicIndex = 0;
		ArrayList<Integer> coordinates = convertTpIndex2ItIndex(mTaskTopicIndex.get(pCurrentTopicIndex));
		nItemIndex = coordinates.get(0);
		nItemsIndex = coordinates.get(1);
		
		initRecordFrame();		//初始化学习记录
		
		mHintLayout.setVisibility(View.VISIBLE);
		mHintTextView.setText("你已经完成筛查，有"+ nReStudyCount +"个知识点没有掌握，你需要把它们再做2遍。");
		Util.playBackGroundSound(context, "hint_remain_do.mp3",false);  //还有没掌握						
	}
	
	/**
	 * 进入浏览题目阶段
	 */
	private void enterScan(){
		// TODO
		Log.e("阶段信息", "进入浏览题目阶段");
		
		CURRENT_PHASE = PHASE_SCAN;
		//初始化当前做题的题号
		mTaskTopicIndex = new ArrayList<Integer>();
		int topicIndex = 1;
		for (int i=0, n=itemsList.size(); i<n; i++){
			for (int j=0, m=itemsList.get(i).getItemList().size(); j<m; j++){
				mTaskTopicIndex.add(topicIndex++);
			}
		}
		pCurrentTopicIndex = 0;
		nItemIndex = 0;
		nItemsIndex = 0;
		
		initRecordFrame();		//初始化学习记录
		mHintLayout.setVisibility(View.VISIBLE);
		mHintTextView.setText(com.kingPadStudy.constant.Constant.TEXT_ALL_UNDERSTAND);
		Util.playBackGroundSound(context, "hint_done.mp3",false); //全部掌握了
	}
	
	
	/**
	 * 根据items list的行标和列标转换为题号
	 * @param rowIndex：行标
	 * @param listIndex：列标
	 * @return：题号
	 */
	private int convert2Index(ArrayList<Items> items, int rowIndex, int listIndex){
		int topicIndex = 1;
		
		for (int i=0; i<rowIndex; i++){
			topicIndex += items.get(i).getItemList().size();
		}
		topicIndex += listIndex;
		
		return topicIndex;
	}
	
	
	/**
	 * 刷新学习记录面板
	 * @param nNotAvi
	 * @param nNotUnd
	 * @param nVague
	 * @param nUnder
	 */
	private void refreshRecordFrame(int nNotAvi, int nNotUnd, int nVague, int nUnder){
		mTextTotalCount.setText("本章共计:" + nTotalCount);
		if (CURRENT_STUDY_MODEL==RESTART || CURRENT_STUDY_MODEL==NORMAL_STUDY){
			mTextNotAvi.setText("未完成:" + nNotAvi);
			mTextNotUnd.setText("未掌握:" + nNotUnd);
			mTextVague.setText("模    糊:" + nVague);
			mTextUnd.setText("已掌握:" + nUnder);
		}else {
			mTextNotAvi.setText("未完成:0");
			mTextNotUnd.setText("未掌握:0");
			mTextVague.setText("模    糊:0");
			mTextUnd.setText("已掌握:0");
		}
	}
	
	/**
	 * 上一题
	 */
	private void lastTopic(){
		hasChecked = false;
		initAnimation();
		
		Util.playSoundPool(context, com.kingPadStudy.constant.Constant.AUDIO_MARK_BUTTON);
		mBtnLast.play(true);
		
		if (pCurrentTopicIndex > 0)
			pCurrentTopicIndex--;
		ArrayList<Integer> coordinates = convertTpIndex2ItIndex(mTaskTopicIndex.get(pCurrentTopicIndex));
		if (nItemsIndex != coordinates.get(1)){
			//进入上一个做题的内容
			nItemsIndex = coordinates.get(1);
			nItemIndex = coordinates.get(0);
			mCurrentStudyItemList = itemsList.get(nItemsIndex).getItemList();
			initNewStudyLink(nItemsIndex, nItemIndex);
			
		}else {
			nItemIndex = coordinates.get(0);
			mBtnLast.postDelayed(new Runnable() {
				@Override
				public void run() {
					gotoQuestionModel(mCurrentStudyItemList.get(nItemIndex), mTaskTopicIndex, pCurrentTopicIndex);
				}
			}, 0);
		}
	}
	
	/**
	 * 下一题
	 */
	private void nextTopic(){
		hasChecked = false;
		initAnimation();
		
		Util.playSoundPool(context, com.kingPadStudy.constant.Constant.AUDIO_MARK_BUTTON);
		mBtnNext.play(true);
		
		if (pCurrentTopicIndex < mTaskTopicIndex.size()-1)
			pCurrentTopicIndex++;
		ArrayList<Integer> coordinates = convertTpIndex2ItIndex(mTaskTopicIndex.get(pCurrentTopicIndex));
		
		if (nItemsIndex != coordinates.get(1)){
			//进入下一个做题的内容
			nItemsIndex = coordinates.get(1);
			nItemIndex = coordinates.get(0);
			mCurrentStudyItemList = itemsList.get(nItemsIndex).getItemList();
			initNewStudyLink(nItemsIndex, nItemIndex);
			
		}else if (nItemsIndex == coordinates.get(1)) {
			//同一个做题的内容
			nItemIndex = coordinates.get(0);
			mBtnNext.postDelayed(new Runnable() {
				@Override
				public void run() {
					gotoQuestionModel(mCurrentStudyItemList.get(nItemIndex), mTaskTopicIndex, pCurrentTopicIndex);
				}
			}, 0);
		}
	}
	
	/**
	 * 进入问题模式
	 * @param item:将要进入的问题模式中的数据item
	 * @param mTaskTopicIndex：当前所有的学习题目序列
	 * @param pCurrentTopicIndex：当前正在学习的题目在题目序列中的指针
	 */
	private void gotoQuestionModel(Item item, ArrayList<Integer> mTaskTopicIndex, int pCurrentTopicIndex){
		Question question = item.getQuestionList().get(0);
		
		//处理问题中的语音
		currentSoundFile = question.getAttributeValue("soundFile");
		currentSoundType = 1;
		if (null == currentSoundFile || "".equals(currentSoundFile)){
			//没有语音文件，界面隐藏播放语音的喇叭
			horn.setVisibility(View.INVISIBLE);
		}else {
			horn.setVisibility(View.VISIBLE);
			//处理循环多次播放语音
			String soundRepeat = question.getAttributeValue("soundRepeat");
			String soundDelay = question.getAttributeValue("soundDelay");
			int k = 1;
			if (soundRepeat!=null && !"".equals(soundRepeat))
				k = Integer.parseInt(soundRepeat);
			int delay = 0;
			if (soundDelay!=null && !"".equals(soundDelay))
				delay = Integer.parseInt(soundDelay);
			
			int sd = playSound(currentSoundFile, 0, currentSoundType);
			for (int t=1; t<k; t++){
				timer = new Timer();
				timerTask = new TimerTask() {
					@Override
					public void run() {
						playSound(currentSoundFile, 0, currentSoundType);
					}
				};
				timer.schedule(timerTask, delay*1000 + sd);
			}
		}
		
		//处理问题文字和图片数据
		ArrayList<BasicBean> beans = question.getList();
		BasicBean basicBean;
		switch (beans.size()) {
		case 0:
			mTextStudy1.setVisibility(View.GONE);
			mTextStudy2.setVisibility(View.GONE);
			mImgStudy.setVisibility(View.GONE);
			break;
		case 1:
			mTextStudy1.setVisibility(View.VISIBLE);
			mTextStudy2.setVisibility(View.GONE);
			mImgStudy.setVisibility(View.GONE);
			basicBean = beans.get(0);
			renderText(mTextStudy1, basicBean.getOwnText(), basicBean.getColor(), basicBean.getFontSize());
			break;
		case 2:
			mTextStudy1.setVisibility(View.VISIBLE);
			mTextStudy2.setVisibility(View.VISIBLE);
			mImgStudy.setVisibility(View.GONE);
			basicBean = beans.get(0);
			renderText(mTextStudy1, basicBean.getOwnText(), basicBean.getColor(), basicBean.getFontSize());
			basicBean = beans.get(1);
			renderText(mTextStudy2, basicBean.getOwnText(), basicBean.getColor(), basicBean.getFontSize());
			break;
		case 3:
			mTextStudy1.setVisibility(View.VISIBLE);
			mTextStudy2.setVisibility(View.VISIBLE);
			basicBean = beans.get(0);
			renderText(mTextStudy1, basicBean.getOwnText(), basicBean.getColor(), basicBean.getFontSize());
			basicBean = beans.get(1);
			renderText(mTextStudy2, basicBean.getOwnText(), basicBean.getColor(), basicBean.getFontSize());
			basicBean = beans.get(2);
			if (basicBean.getPicture()){
				if (mCrtStudyBitmap!=null && !mCrtStudyBitmap.isRecycled()){
					mCrtStudyBitmap.recycle();//回收图片，释放内存资源
					mCrtStudyBitmap = null;
				}
				mImgStudy.setVisibility(View.VISIBLE);
				mCrtStudyBitmap = BitmapFactory.decodeFile(basicBean.getPath());
				if (null != mCrtStudyBitmap)
					mImgStudy.setBackgroundDrawable(new BitmapDrawable(mCrtStudyBitmap));
			}else {
				mImgStudy.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}
		
		//检测是否有答案数据
		if (null == item.getAnswerList()){
			mCheckAns.setVisibility(View.INVISIBLE);
		}else {
			mCheckAns.setVisibility(View.VISIBLE);
		}
		
		mCheckSelfLayout.setVisibility(View.INVISIBLE);
		//检测该题目是否已经做过，这里使用状态来判断
		int state = ItemImpl.FLAG_NOT_DO;
		if (CURRENT_PHASE == PHASE_REPEAT){
			if (pCurrentTopicIndex >= mTaskTopicIndex.size()/2)
				state = item.getSecondFinalState();
			else {
				state = item.getFinalState();
			}
		}else {
			state = item.getFinalState();
		}
		
		//处理mode为空的情况，隐藏做题的结果
		if ("".equals(mode) && CURRENT_PHASE!=PHASE_SCAN && !item.isDown()){
			//需要隐藏所有的题目，不直接显示答案
			state = ItemImpl.FLAG_NOT_DO;
		}
		
		
		if (state == ItemImpl.FLAG_NOT_DO){
			// 隐藏或者显示界面其它控件
			mLight.setVisibility(View.INVISIBLE);
			mTextAnswer.setVisibility(View.INVISIBLE);
			mImgAnswer.setVisibility(View.INVISIBLE);
			
		}else if (state == ItemImpl.FLAG_UNDERSTAND) {
			//已经掌握，显示答案和绿灯
			mLight.setVisibility(View.VISIBLE);
			showLight(ItemImpl.FLAG_UNDERSTAND);
			mCheckAns.setVisibility(View.INVISIBLE);
			mTextAnswer.setVisibility(View.INVISIBLE);
			mImgAnswer.setVisibility(View.INVISIBLE);
			showAnswer(item);
			
		}else if (state == ItemImpl.FLAG_VAGUE) {
			//模糊，显示答案和黄灯
			mLight.setVisibility(View.VISIBLE);
			showLight(ItemImpl.FLAG_VAGUE);
			mCheckAns.setVisibility(View.INVISIBLE);
			mTextAnswer.setVisibility(View.INVISIBLE);
			mImgAnswer.setVisibility(View.INVISIBLE);
			showAnswer(item);
			
		}else if (state == ItemImpl.FLAG_NOT_UND) {
			//完全不会，显示答案和红灯
			mLight.setVisibility(View.VISIBLE);
			showLight(ItemImpl.FLAG_NOT_UND);
			mCheckAns.setVisibility(View.INVISIBLE);
			mTextAnswer.setVisibility(View.INVISIBLE);
			mImgAnswer.setVisibility(View.INVISIBLE);
			showAnswer(item);
		}
		
		//标志该题目已经做过，在mode为“”的时候使用该标志
		item.setDown(true);
		
		mJumpLayout.setVisibility(View.VISIBLE);
		consoleJumpLayout(mTaskTopicIndex, pCurrentTopicIndex);
	}
	
	/**
	 * 控制上下题按钮的显示与隐藏
	 * @param nItemIndex
	 * @param nItemsIndex
	 */
	private void consoleJumpLayout(ArrayList<Integer> mTaskTopicIndex, int pCurrentTopicIndex){
		if (null != mTaskTopicIndex && mTaskTopicIndex.size() == 1){
			mBtnLast.setVisibility(View.INVISIBLE);
			mBtnNext.setVisibility(View.INVISIBLE);
			
		}else if (pCurrentTopicIndex == 0) {
			// 第一次进入学习界面，“上一题”的按钮不显示
			mBtnLast.setVisibility(View.INVISIBLE);
			mBtnNext.setVisibility(View.VISIBLE);
			
		} else if (pCurrentTopicIndex == mTaskTopicIndex.size()-1) {
			// 已经是最后一题的学习界面，“下一题”的按钮不显示
			mBtnLast.setVisibility(View.VISIBLE);
			mBtnNext.setVisibility(View.INVISIBLE);
			
		}else {
			mBtnLast.setVisibility(View.VISIBLE);
			mBtnNext.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 点击“核对答案”之后进入答案模式
	 */
	private void gotoAnswerModel(Item item){
		showAnswer(item);
		
		//处理界面其它控件的显示与隐藏
		mCheckAns.setVisibility(View.INVISIBLE);
		mCheckSelfLayout.setVisibility(View.VISIBLE);
		mJumpLayout.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * 显示答案
	 * @param item
	 */
	private void showAnswer(Item item){
		Answer answer = item.getAnswerList().get(0);
		
		//处理答案中的声音
		currentSoundFile = answer.getAttributeValue("soundFile"); 
		currentSoundType = 2;
		if (null == currentSoundFile || "".equals(currentSoundFile)){
			//没有语音文件，界面隐藏播放语音的喇叭
			horn.setVisibility(View.INVISIBLE);
		}else {
			horn.setVisibility(View.VISIBLE);
			playSound(currentSoundFile, 0, currentSoundType);
		}
		
		//处理答案中的文字和图片
		if (null != answer.getList()) {
			ArrayList<BasicBean> beans = answer.getList();
			BasicBean basicBean;
			switch (beans.size()) {
			case 0:
				mTextAnswer.setVisibility(View.GONE);
				mImgAnswer.setVisibility(View.GONE);
				break;
			case 1:
				//只有文字
				mTextAnswer.setVisibility(View.VISIBLE);
				mImgAnswer.setVisibility(View.GONE);
				basicBean = beans.get(0);
				renderText(mTextAnswer, basicBean.getOwnText(), basicBean.getColor(), basicBean.getFontSize());
				break;
			case 2:
				//有文字和图片
				mTextAnswer.setVisibility(View.VISIBLE);
				basicBean = beans.get(0);
				if (basicBean.getPicture()){
					if (mCrtAnswerBitmap!=null && !mCrtAnswerBitmap.isRecycled()){
						mCrtAnswerBitmap.recycle();//回收图片，释放内存资源
						mCrtAnswerBitmap = null;
					}
					mImgAnswer.setVisibility(View.VISIBLE);
					mCrtAnswerBitmap = BitmapFactory.decodeFile(basicBean.getPath());
					if (null != mCrtAnswerBitmap)
						mImgAnswer.setBackgroundDrawable(new BitmapDrawable(mCrtAnswerBitmap));
				}else {
					mImgAnswer.setVisibility(View.GONE);
				}
				
				basicBean = beans.get(1);
				renderText(mTextAnswer, basicBean.getOwnText(), basicBean.getColor(), basicBean.getFontSize());
					
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * 点击“自我评价”的三个小人之一，进入自我检查模式
	 * @param choice: 自我评价的选择;0-完全不会，1-模糊，2-完全掌握
	 */
	private void gotoCheckSelfModel(final int choice, int delay){
		currentSoundFile = null;
		
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				//处理界面
				mIntro.post(new Runnable() {
					@Override
					public void run() {
						mCheckSelfLayout.setVisibility(View.INVISIBLE);
						mJumpLayout.setVisibility(View.VISIBLE);
						showLight(choice);
					}
				});
			}
		};
		timer.schedule(timerTask, delay);
//		timer.schedule(timerTask, 0);
	}
	
	private void showLight(int choice) {
		mLight.setVisibility(View.VISIBLE);
		mLight.reset();
		switch (choice) {
		case ItemImpl.FLAG_NOT_UND:
			// 完全不会，亮红灯
			mLight.addDrawable(R.drawable.light_red1, 300);
			mLight.addDrawable(R.drawable.light_red2, 500);
			break;
		case ItemImpl.FLAG_VAGUE:
			// 一般，亮黄灯
			mLight.addDrawable(R.drawable.light_yellow1, 300);
			mLight.addDrawable(R.drawable.light_yellow2, 500);
			break;
		case ItemImpl.FLAG_UNDERSTAND:
			// 完全掌握，亮绿灯
			mLight.addDrawable(R.drawable.light_green1, 300);
			mLight.addDrawable(R.drawable.light_green2, 500);
			break;
		}
		mLight.play(false);
	}
	
	/**
	 * 初始化一种新的学习环节
	 * @param nItemsIndex:当前学习模式对应的 items 的索引
	 */
	private void initNewStudyLink(final int nItemsIndex, final int nItemIndex){
		if (null == itemsList || nItemsIndex == itemsList.size())
			return;
		mCurrentStudyItemList = itemsList.get(nItemsIndex).getItemList();
		
		mHintLayout.setVisibility(View.GONE);
		hideStudyWidget();
		
		//初始化引导文字和语音
		currentSoundType = 0;
		int duration = playSound(itemsList.get(nItemsIndex).getIntro().getAttributeValue("soundFile"), 0, currentSoundType);
		BasicBean basicBean = itemsList.get(nItemsIndex).getIntro().getBasicBean();
		mIntro.setText(basicBean.getOwnText());
		mIntro.setTextColor(Color.parseColor(basicBean.getColor()));
		mIntro.setTextSize(Float.parseFloat(basicBean.getFontSize()));
		
		//初始化第一条学习item
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				mIntro.post(new Runnable() {
					@Override
					public void run() {
						gotoQuestionModel(mCurrentStudyItemList.get(nItemIndex), mTaskTopicIndex, pCurrentTopicIndex);
					}
				});
			}
		};
		timer.schedule(timerTask, duration + 1000*1);
	}
	
	class ButtonListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if (mBtnLast == v){
				//上一题
				lastTopic();
				
			}else if (mBtnNext == v) {
				//下一题
				nextTopic();
				
			}else if (mBackView == v) {
				//返回
				Util.playSoundPool(context, com.kingPadStudy.constant.Constant.AUDIO_MARK_BUTTON);
				
				Util.clearMp3();
				Util.releasePlayer();
//				Util.stopHint();
				release();
				ViewController.backToNormalDuyin();
				ResourceLoader.refreshMenuView();
				
			}else if (mChkBad == v) {
				//完全不会
				iDontKnow();
				
			}else if (mChkJust == v) {
				//模糊
				itIsVague();
				
			}else if (mChkGood == v) {
				//完全掌握
				iUnderstand();
				
			}else if (mCheckAns == v) {
				//核对答案
				gotoAnswerModel(mCurrentStudyItemList.get(nItemIndex));
				
			}else if (horn == v) {
				playSound(currentSoundFile, 0, currentSoundType);
				
			}else if (mButtonYes == v) {
				//点击提示面板上的确定按钮
				initNewStudyLink(nItemsIndex, nItemIndex);
			}
		}
	}
	
	private void release(){
		if (bgBitmap != null && !bgBitmap.isRecycled()){
			bgBitmap.recycle();
			bgBitmap = null;
		}
		if (studyBitmapBg!=null && !studyBitmapBg.isRecycled()){
			studyBitmapBg.recycle();
			studyBitmapBg = null;
		}
		if (mCrtStudyBitmap!=null && !mCrtStudyBitmap.isRecycled()){
			mCrtStudyBitmap.recycle();
			mCrtStudyBitmap = null;
		}
		if (mCrtAnswerBitmap!=null && !mCrtAnswerBitmap.isRecycled()){
			mCrtAnswerBitmap.recycle();
			mCrtAnswerBitmap = null;
		}
		
		mainView = null;
	}
	
	private void iDontKnow(){
		if(hasChecked)
			return;
		hasChecked = true;
		mChkBad.play(false);
		Util.playSoundPool(context, com.kingPadStudy.constant.Constant.AUDIO_MARK_BUTTON);
		
		if ("".equals(mode) && nNotAvi > 0){
			//当mode为空的时候并且还有没有完成的题目。只能将没有完成的题目修改记录为完全不会.
			Item item = getRightItemFormOriginalItemsList(itemsList, mTaskTopicIndex.get(pCurrentTopicIndex));
			if (item.getFinalState() == ItemImpl.FLAG_NOT_DO){
				nNotUnd += 1;
			}
			
		}else if(!"".equals(mode) && CURRENT_PHASE != PHASE_REPEAT){
			//重复学习阶段不需要增加
			nNotUnd += 1;
		}
		remember(mCurrentStudyItemList.get(nItemIndex), ItemImpl.FLAG_NOT_UND);
		
		if (pCurrentTopicIndex == mTaskTopicIndex.size()-1){
			mCheckSelfLayout.setVisibility(View.INVISIBLE);
			checkEndToGo();
			
		}else {
			gotoCheckSelfModel(ItemImpl.FLAG_NOT_UND, 1200);
		}
	}
	
	private void itIsVague(){
		if(hasChecked)
			return;
		hasChecked = true;
		mChkJust.play(false);
		Util.playSoundPool(context, com.kingPadStudy.constant.Constant.AUDIO_MARK_BUTTON);
		
		if ("".equals(mode) && nNotAvi > 0){
			//当mode为空的时候并且还有没有完成的题目。只能将没有完成的题目修改记录为模糊.
			Item item = getRightItemFormOriginalItemsList(itemsList, mTaskTopicIndex.get(pCurrentTopicIndex));
			if (item.getFinalState() == ItemImpl.FLAG_NOT_DO){
				nVague += 1;
			}
			
		}else if(!"".equals(mode) && CURRENT_PHASE != PHASE_REPEAT)	//重复学习阶段不需要增加
			nVague += 1;
		remember(mCurrentStudyItemList.get(nItemIndex), ItemImpl.FLAG_VAGUE);
		
		if (pCurrentTopicIndex == mTaskTopicIndex.size()-1){
			mCheckSelfLayout.setVisibility(View.INVISIBLE);
			checkEndToGo();
			
		}else {
			gotoCheckSelfModel(ItemImpl.FLAG_VAGUE, 1200);
		}
	}
	
	private void iUnderstand(){
		if(hasChecked)
			return;
		hasChecked = true;
		mChkGood.play(false);
		Util.playSoundPool(context, com.kingPadStudy.constant.Constant.AUDIO_MARK_BUTTON);
		
		if ("".equals(mode)){
			//当mode为空的时候只需要将模糊和完全不会的记录改为掌握的记录
			modeNullRemember();
			
		}else if(CURRENT_PHASE != PHASE_REPEAT){
			//重复学习阶段不需要增加
			nUnder += 1;
		}
			
		remember(mCurrentStudyItemList.get(nItemIndex), ItemImpl.FLAG_UNDERSTAND);
		
		//检查是否是最后一个题目，如果是则检查接题和重复学习和浏览题目
		if (pCurrentTopicIndex == mTaskTopicIndex.size()-1){
			if ("".equals(mode)){
				gotoCheckSelfModel(ItemImpl.FLAG_UNDERSTAND, 1200);
				//进入浏览题目阶段
				CURRENT_PHASE = PHASE_SCAN;
				finishOneTime();
				
			}else {
				mCheckSelfLayout.setVisibility(View.INVISIBLE);
				checkEndToGo();
			}
			
		}else {
			gotoCheckSelfModel(ItemImpl.FLAG_UNDERSTAND, 1200);
		}
	}
	
	private void modeNullRemember(){
		Item item = getRightItemFormOriginalItemsList(itemsList, mTaskTopicIndex.get(pCurrentTopicIndex));
		switch (item.getFinalState()) {
		case ItemImpl.FLAG_NOT_UND:
			nNotUnd--;
			nUnder++;
			break;
		case ItemImpl.FLAG_VAGUE:
			nVague--;
			nUnder++;
			break;
		case ItemImpl.FLAG_NOT_DO:
			nUnder++;
			break;
		}
	}
	
	/**
	 * 学习到最后一个题目的时候检查应该进入哪个学习阶段
	 */
	private void checkEndToGo(){
		hasChecked = false;
		initAnimation();
		boolean hasFindPlace = false;
		
		//已经是重复学习阶段则单独处理
		if (CURRENT_PHASE == PHASE_REPEAT){
			//统计经过一次重复学习还没有掌握的题目
			for (int topicIndex=0, n=mTaskTopicIndex.size()/2; topicIndex<n; topicIndex++){
				Item item = getRightItemFormOriginalItemsList(itemsList, mTaskTopicIndex.get(topicIndex));
				if (item.getFinalState()!=ItemImpl.FLAG_UNDERSTAND || item.getSecondFinalState()!=ItemImpl.FLAG_UNDERSTAND){
					hasFindPlace = true;
					break;
				}
			}
			
			if (hasFindPlace){
				this.enterRepeatTwice(true);
				return;
			}
		}
		
		//首先检查是否有未做完的题目
		if (!hasFindPlace)
		for (int i=0, n=itemsList.size(); i<n; i++){
			for (int j=0, m=itemsList.get(i).getItemList().size(); j<m; j++){
				Item item = itemsList.get(i).getItemList().get(j);
				if (item.getFinalState() == ItemImpl.FLAG_NOT_DO){
					hasFindPlace = true;
					this.enterContinueStudyPhase();
					break;
				}
			}
		}
		
		//所有题目都已经做完一遍，检查是否有“完全不会”和“模糊”的题目
		if (!hasFindPlace){
			hasFindPlace = finishOneTime();
			if (hasFindPlace){
				this.enterRepeatTwice(false);
			}
		}
		
		//所有题目都已经掌握，直接进入浏览题目的模式
		if(!hasFindPlace){
			this.enterScan();
		}
	}
	
	private boolean finishOneTime(){
		boolean result = false;
		int lightState = 0, impState = 0, scdState = 0;
		
		for (int i=0, n=itemsList.size(); i<n; i++){
			for (int j=0, m=itemsList.get(i).getItemList().size(); j<m; j++){
				Item item = itemsList.get(i).getItemList().get(j);
				int lst = item.getFirstState();
				if (lst==ItemImpl.FLAG_NOT_UND || lst==ItemImpl.FLAG_VAGUE){
					impState = 1;
				}else {
					scdState = 1;
				}
				if (lightState < item.getFinalState())
					lightState = item.getFinalState();
				if (item.getFinalState()==ItemImpl.FLAG_NOT_UND || item.getFinalState()==ItemImpl.FLAG_VAGUE){
					result = true;
				}
			}
		}
		
		// 此处必须是在所有题目都已经掌握的情况下才显示两个按钮开关
		if (result) {
//			lightState = 0;
			impState = 0;
			scdState = 0;
		}
		
		//保存目录灯一个“重点复习”，“次重点复习”按钮的状态
		if (CURRENT_STUDY_MODEL==RESTART || CURRENT_STUDY_MODEL==NORMAL_STUDY){
			ContentStateBean bean = mDbAdapter.getContentState(mCurrentXmlPath);
			if (null != bean) {
				bean.lightState = lightState;
				bean.importantState = impState;
				bean.secondState = scdState;
				mDbAdapter.alterContentState(mCurrentXmlPath, bean);
			}
		}
		
		return result;
	}
	
	
	/**
	 * 做学习记录
	 * @param resultState:是三种情况之一
	 */
	private void remember(Item item, int resultState){
		
		//在数据库中记录
		if (CURRENT_PHASE==PHASE_NORMAL || CURRENT_PHASE==PHASE_RECORD){
			
			item.setFirstState(resultState);
			item.setFinalState(resultState);
			
			//正常学习和重新学习模式中正常学习阶段以及接题阶段，将学习结果记录到数据库中
			if (CURRENT_STUDY_MODEL!=IMP_STUDY && CURRENT_STUDY_MODEL!=SCD_STUDY){
				
				mDbAdapter.add2Record(mCurrentXmlPath, item.getTopicIndex(), resultState, resultState);
				ContentStateBean bean = mDbAdapter.getContentState(mCurrentXmlPath);
				if (bean == null){
					mDbAdapter.add2Content(mCurrentXmlPath, 0, 0, 0, 1);	//开启“重新开始”按钮
				}
			}
			
			//更新学习记录的界面
			if (nTotalCount >= (nNotUnd+nVague+nUnder)){
				nNotAvi = nTotalCount - nNotUnd - nVague - nUnder;
			}
			refreshRecordFrame(nNotAvi, nNotUnd, nVague, nUnder);
			
		}else if (CURRENT_PHASE==PHASE_REPEAT) {
			//正常学习和重新学习模式中重复学习阶段，将最后 结果记录到数据库中
			if (pCurrentTopicIndex >= mTaskTopicIndex.size()/2){
				//将记录记在secondfinalstate中
				item.setSecondFinalState(resultState);
			}else {
				item.setFinalState(resultState);
			}
			
			if (CURRENT_STUDY_MODEL!=IMP_STUDY && CURRENT_STUDY_MODEL!=SCD_STUDY
				&& item.getFinalState()==ItemImpl.FLAG_UNDERSTAND && item.getSecondFinalState()==ItemImpl.FLAG_UNDERSTAND){
				mDbAdapter.alterFinalState(mCurrentXmlPath, item.getTopicIndex(), resultState);
				//更新学习记录的界面
				switch (item.getFirstState()) {
				case ItemImpl.FLAG_NOT_UND:
					nNotUnd--;
					break;
				case ItemImpl.FLAG_VAGUE:
					nVague--;
					break;
				}
				nUnder++;
				if (nTotalCount >= (nNotUnd+nVague+nUnder)){
					nNotAvi = nTotalCount - nNotUnd - nVague - nUnder;
				}
				refreshRecordFrame(nNotAvi, nNotUnd, nVague, nUnder);
			}
		}
	}
	
	/**
	 * 渲染textview中的文字
	 * @param textView
	 * @param color
	 * @param size
	 */
	private void renderText(TextView textView, String content, String color, String size){
		if (null != content)
			textView.setText(content);
		
		if (color!=null && !"".equals(color)){
			textView.setTextColor(Color.parseColor(color));
		}
		
		if (null != size && !"".equals(size)){
			textView.setTextSize(Float.parseFloat(size));
		}
	}
	
	private void initAnimation() {
		mBtnLast.stop();
		mBtnNext.stop();
		mLight.stop();
		mChkBad.stop();
		mChkGood.stop();
		mChkJust.stop();
		
		mBackView.addDrawable(R.drawable.back_default, 1000);
		mBackView.addDrawable(R.drawable.back_hot, 1000);
		mBackView.play(false);
		mCheckAns.addDrawable(R.drawable.check_answer1, 1000);
		mCheckAns.addDrawable(R.drawable.check_answer2, 1000);
		mCheckAns.play(false);
		mBtnLast.addDrawable(R.drawable.jump_left1, 0);
		mBtnLast.addDrawable(R.drawable.jump_left2, 600);
		mBtnLast.addDrawable(R.drawable.jump_left3, 600);
		
		mBtnNext.addDrawable(R.drawable.jump_right1, 0);
		mBtnNext.addDrawable(R.drawable.jump_right2, 600);
		
		mChkBad.addDrawable(R.drawable.check_bad1, 200);
		mChkBad.addDrawable(R.drawable.check_bad2, 200);
		mChkBad.addDrawable(R.drawable.check_bad3, 200);
		mChkBad.addDrawable(R.drawable.check_bad2, 200);
		mChkBad.addDrawable(R.drawable.check_bad1, 200);
		
		mChkJust.addDrawable(R.drawable.check_just1, 200);
		mChkJust.addDrawable(R.drawable.check_just2, 200);
		
		mChkGood.addDrawable(R.drawable.check_good1, 200);
		mChkGood.addDrawable(R.drawable.check_good2, 200);
	}
	
	/**
	 * 播放声音
	 * 
	 * @param soundPath
	 *            ：声音文件在sd卡上的路径
	 * @param totalDelay
	 *            :延迟播放的时间，秒为单位
	 * @param type:0-引导语音，1-问题声音，2-答案声音
	 * @return :播放时间加上总的延迟时间
	 */
	private int playSound(final String soundPath, int totalDelay, final int type) {
		if (soundPath == null || "".equals(soundPath.trim())) {
			return 0;
		}
		
		Util.stopHint();

		int delay = 1000 * totalDelay;
		
		mBackView.postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					switch (type) {
					case 0:
						Util.playSound(soundPath, false);
						break;
					case 1:
						Util.playSoundPoolMp3(context, mTaskTopicIndex.get(pCurrentTopicIndex)-1);
						break;
					case 2:
						Util.playSoundPoolAnswerMp3(context, mTaskTopicIndex.get(pCurrentTopicIndex)-1);
						break;
					}
					

				} catch (Exception e) {
					Toast.makeText(context, "播放声音异常，请检查声音文件是否存在！", Toast.LENGTH_SHORT).show();
				}
			}

		}, delay);
		
		return Util.getSoundDuration(soundPath) + delay;
	}
	
	@Override
	public View getGameView() {
		return mainView;
	}

}
