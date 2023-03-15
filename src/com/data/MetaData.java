package com.data;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.bean.CommonModeBean;
import com.bean.LearnBean;
import com.bean.LearnModeBean;
import com.constant.Constant;
import com.net.RequestParameter;

/**
 * 解析资源文件下对应的data.xml文件
 * 请求参数：
 * <path> : 当前目录路径,不是/data.xml文件路径
 * <type> : 该目录文件所对应的type
 * <mode> : 在index.xml文件中返回，标志解析方式，“study”
 * 
 * @author lenovo
 *
 */
public final class MetaData extends BaseData {
	
	private final static String TAG = "meta_data";
	/**
	 * 该章节标题
	 */
	private String mTitle;
	/**
	 * 该章节图片背景
	 */
	private String mBg;
	/**
	 * 两层Arrayist，每一个ArrayList表示一个item，在普通模式中使用，在学习模式中该字段为null
	 */
	private ArrayList<ArrayList<CommonModeBean>> mList;
	//---------------------------------------------
	/**
	 * 学习模式中的介绍头数据
	 */
	private LearnBean mIntro;
	/**
	 * 学习模式中item数据
	 */
	private ArrayList<LearnModeBean> mLearnModeList;
	
	@Override
	public void startParse(RequestParameter parameter) throws Exception {
		Log.i(TAG, "开始解析data.xml文件");
		String path = parameter.getValue("path");
		if(path == null || path.equals(""))
			throw new NullPointerException("请求参数为path为null");
		
		String type = parameter.getValue("type");
		if(type == null || type.equals(""))
			throw new NullPointerException("请求参数type为null");
			
		if(! "study".equals(parameter.getValue("mode"))) {
			Log.i(TAG, "以普通模式解析数据");
			analyzeDocument(path);
		}
		else {
			// 解析mode为study的数据
			// 在该数据中现在已知两种数据类型
			
			// 客观提问
			if(type.equals("ObjectiveQuiz")) {
				Log.i(TAG, "以客观模式解析数据");
				analyzeStudyObjectiveDocument(path);
			}
			// 主观提问
			else if(type.equals("SubjectiveQuiz")) {
				Log.i(TAG, "以主管模式解析数据");
				analyzeStudySubjectiveDocument(path);
			}
			
		} // else
		
		
		Log.i(TAG, "解析data.xml数据成功！");
		
	} // startParse
	
	/**
	 * 解析数据中包含CDATA格式的xml数据
	 * @param data ： 数据内容
	 * @param bean ： 解析后返回的数据对象
	 */
	private void analyzeCDATA(final String data, LearnBean bean) throws Exception{

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(data.getBytes());
		parser.setInput(inputStream, "UTF-8");
		
		int event = parser.getEventType();
		
		while( event != XmlPullParser.END_DOCUMENT) {
		
			switch(event) {
			
			case XmlPullParser.START_TAG:
				if("span".equals(parser.getName()) || "p".equals(parser.getName())) {
					bean.color 	=	parser.getAttributeValue(null, "color");
					bean.font	=	parser.getAttributeValue(null, "fontFamily");
					bean.fontSize	=	parser.getAttributeValue(null, "fontSize");
					bean.ownText	=	parser.nextText();
				}
				break;
			} // switch
			
			event = parser.next();
		} // while

		inputStream.close();
	} // analyzeCDATA
	
	
	/**
	 * 处理该资源文件的路径，判断是当前目录还是系统目录
	 * @param path ： 当前资源路径
	 * @param text ： 当前获取到的资源路径
	 * @return
	 */
	private String dealResPath(final String path, String text) {
		
		if(text != null && text.contains("app:/")) {
			return text.replaceAll("app:/", Constant.KING_PAD_RES);
		}
		
		return path + "/" + text;
	}
	
	/**
	 * 按客观模式解析数据
	 * @param path : 文件夹路径不包含data.xml
	 */
	private void analyzeStudyObjectiveDocument(final String path) throws Exception{
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();
		FileInputStream inputStream = new FileInputStream(new File(path + "/data.xml"));
		parser.setInput(inputStream, "UTF-8");
		int event = parser.getEventType();
		
		LearnModeBean learnModeBean = null;
		
		while( event != XmlPullParser.END_DOCUMENT ) {
			
			switch(event) {
			case XmlPullParser.START_DOCUMENT:
				mLearnModeList = new ArrayList<LearnModeBean>();
				break;
				
			case XmlPullParser.START_TAG:
				// 解析question节点数据
				if("question".equals(parser.getName())) {
					learnModeBean.question = new LearnBean();
					
					// 判断该处是app路径还是当前文件夹路径
					String text = parser.getAttributeValue(null, "soundFile");
					//TODO
					Log.e("问题数据解析音乐文件路径", "问题数据解析音乐文件路径"+text);
					
					learnModeBean.question.file	=	dealResPath(path, text);
						
					text = parser.nextText();
					if(text != null) 
						analyzeCDATA(text, learnModeBean.question);
					event = parser.next();
					continue;
				}
				
				// 解析answer节点数据
				if("answer".equals(parser.getName())) {
					learnModeBean.answer = new LearnBean();
					
					// 判断一下资源路径
					String text = parser.getAttributeValue(null, "soundFile");
					//TODO
					Log.e("答案数据解析音乐文件路径", "答案数据解析音乐文件路径"+text);
					learnModeBean.answer.file	=	dealResPath(path, text);
					
					text = parser.nextText();
					if(text != null) 
						analyzeCDATA(text, learnModeBean.answer);
					event = parser.next();
					continue;
				}
				
				// 解析item节点数据
				if("item".equals(parser.getName())) {
					// 解析每一个item选项
					learnModeBean = new LearnModeBean();
					event = parser.next();
					continue;
				}
				
				// 解析intro节点数据
				if( "intro".equals(parser.getName()) ) {
					mIntro 			= 	new LearnBean();
					
					String text = parser.getAttributeValue(null, "soundFile");
					//TODO
					Log.e("引言数据解析音乐文件路径", "引言数据解析音乐文件路径"+text);
					mIntro.file	=	dealResPath(path, text);
					//mIntro.file		=	text.replaceAll("app:/", Constant.KING_PAD_RES);
					
					text = parser.nextText();
					if(text != null ) {
						analyzeCDATA(text, mIntro);
					}
					event = parser.next();
					continue;
				}
				
				if("items".equals(parser.getName())) {
					// 解析到items节点
					mLearnModeList = new ArrayList<LearnModeBean>();
					
					event = parser.next();
					continue;
				}
				
				if( "data".equals( parser.getName() )) {
					// 表示为开始标题，解析标题和背景，该图片目录不许要判断，一定是在应用程序目录下
					mTitle 	= 	parser.getAttributeValue(0);
					mBg		=	Constant.KING_PAD_RES + parser.getAttributeValue(1);
					event = parser.next();
					continue;
				}
				
				break;
			case XmlPullParser.END_TAG:
				// 把每一个item数据加载到ArrayList中
				if("item".equals(parser.getName())) {
					mLearnModeList.add(learnModeBean);
					learnModeBean = null;
				}
				break;
				
			} // switch
			
			event = parser.next();
			
		} // while
		
		inputStream.close();
	} // analyzeStudyObjectiveDocument
	
	/**
	 * 按主观模式解析数据
	 */
	private void analyzeStudySubjectiveDocument(final String path) throws Exception{
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();
		FileInputStream inputStream = new FileInputStream(new File(path + "/data.xml"));
		parser.setInput(inputStream, "UTF-8");
		
		int event = parser.getEventType();
		
		// 在每个item中的数据
		LearnModeBean learnModeBean = null;
		
//		// 在每个item中的option结点数据 
//		LearnBean optionBean = null;
		
		while ( event != XmlPullParser.END_DOCUMENT ) {
			
			switch(event) {
			case XmlPullParser.START_DOCUMENT:
				mLearnModeList = new ArrayList<LearnModeBean>();
				break;
			case XmlPullParser.START_TAG:
				
				if("option".equals(parser.getName())) {
					
					boolean bFlag = false;
					LearnBean optionBean = new LearnBean();
					
					String text = parser.getAttributeValue(null, "answer");
					if("true".equals(text)) {
						bFlag = true;
					}
					
					text = parser.nextText();
					if(text != null) 
						analyzeCDATA(text, optionBean);
					
					learnModeBean.option.add(optionBean);
					
					if(bFlag == true)
						learnModeBean.answer = new LearnBean(optionBean);
					
					event = parser.next();
					continue;
					
				} // if("option")
				
				// 解析question节点数据
				if("question".equals(parser.getName())) {
					
					learnModeBean.question = new LearnBean();

					// 设置默认大小为4
					learnModeBean.option = new ArrayList<LearnBean>(4);
					learnModeBean.question.file	=	parser.getAttributeValue(null, "soundFile");
					String text = parser.nextText();
					if(text != null) 
						analyzeCDATA(text, learnModeBean.question);
					
					event = parser.next();
					continue;
				} // if("question")
				
				// 解析item节点数据
				if("item".equals(parser.getName())) {
					
					// 解析每一个item选项
					learnModeBean = new LearnModeBean();
					event = parser.next();
					continue;
				}
				
				// 解析intro节点数据
				if( "intro".equals(parser.getName()) ) {
					mIntro 			= 	new LearnBean();
					
					String text = parser.getAttributeValue(null, "soundFile");
					mIntro.file		=	dealResPath(path, text);
					
					text = parser.nextText();
					if(text != null ) {
						analyzeCDATA(text, mIntro);
					}
					
					event = parser.next();
					continue;
				}
				
				if("items".equals(parser.getName())) {
					// 解析到items节点
					mLearnModeList = new ArrayList<LearnModeBean>();
					
					event = parser.next();
					continue;
				}
				
				if( "data".equals( parser.getName() )) {
					// 表示为开始标题，解析标题和背景
					mTitle 	= 	parser.getAttributeValue(0);
					mBg		=	Constant.KING_PAD_RES + parser.getAttributeValue(1);
					event = parser.next();
					continue;
				}
				break;
			case XmlPullParser.END_TAG:
				
				// 把每一个item数据加载到ArrayList中
				if("item".equals(parser.getName())) {
					mLearnModeList.add(learnModeBean);
					learnModeBean = null;
				}
				break;
			}
			
			event = parser.next();
		} // while
		
		
		inputStream.close();
	} // analyzeStudySubjectiveDocument
	
	
	/**
	 * 按普通模式解析数据
	 * @param path ： 文件夹路径，不包含data.xml
	 * @throws Exception
	 */
	private void analyzeDocument(final String path) throws Exception {

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();
		FileInputStream inputStream = new FileInputStream(new File(path + "/data.xml"));
		parser.setInput(inputStream, "UTF-8");
		
		int event = parser.getEventType();
		
		
		while(event != XmlPullParser.END_DOCUMENT) {
			
			switch(event) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				if("data".equals(parser.getName())) {
					// 获取到标题和背景
					mTitle = parser.getAttributeValue(0);
					mBg = Constant.KING_PAD_RES  + parser.getAttributeValue(1);
					event = parser.next();
					continue;
				} // 解析标题和背景
				
				if("items".equals(parser.getName())) {
					//解析items标签,设置其默认大小为3
					mList = new ArrayList<ArrayList<CommonModeBean>>();
					event = parser.next();
					continue;
				}
				
				if("item".equals(parser.getName())) {
					boolean bFlag = false;
					ArrayList<CommonModeBean> list = new ArrayList<CommonModeBean>();
					
					int count = parser.getAttributeCount();
					
					for ( int i = 0; i < count; i++ ) {
						
						String text = parser.getAttributeValue(i);
						if(text == null) 
							continue;
						
						text = path+"/" + text;
						
						if(text.contains(".swf"))
						{
							CommonModeBean bean = new CommonModeBean();
							bean.file = text;
							bean.type = CommonModeBean.SWF;
							list.add(bean);
							bFlag = true;
						} 
						else if(text.contains(".xml")) 
						{
							CommonModeBean bean = new CommonModeBean();
							bean.file = text;
							bean.type = CommonModeBean.XML;
							list.add(bean);
							bFlag = true;
						} 
						else if(text.contains(".mp3")) 
						{
							CommonModeBean bean = new CommonModeBean();
							bean.file = text;
							bean.type = CommonModeBean.MP3;
							list.add(bean);
							bFlag = true;
						}
						else if(text.contains(".htm")) {
							CommonModeBean bean = new CommonModeBean();
							bean.file = text;
							bean.type = CommonModeBean.HTM;
							list.add(bean);
							bFlag = true;
						}

					} // for
					if(bFlag == true)
						mList.add(list);
					list = null;
					
					event = parser.next();
					continue;
				} // if('item')
				
				break;
			}
	
			event = parser.next();
		} // for
		
		inputStream.close();
		
	} // analyzeDocument
	
	public String getTitle() {
		return this.mTitle;
	}
	
	public String getBg() {
		return this.mBg;
	}
	
	public ArrayList<ArrayList<CommonModeBean>> getList() {
		return mList;
	}

	public LearnBean getIntro() {
		return this.mIntro;
	}
	
	public ArrayList<LearnModeBean> getLearnList() {
		return this.mLearnModeList;
	}
	
	/*public void printData() {
		System.out.println("title:" + mTitle);
		System.out.println("bg:" + mBg);
		if(mList != null ) {
			for(int i = 0; i< mList.size(); i++) {
				ArrayList<CommonModeBean> list = mList.get(i);
				System.out.println("i = " + i);
				for(int j = 0; j < list.size(); j++) {
					//System.out.println("j = " + j);
					CommonModeBean bean = list.get(j);
					System.out.println("title1:" + bean.file);
					System.out.println("type: " + bean.type);
				}
			}
		}
		
		if(mLearnModeList != null) {
			System.out.println("intro数据如下：");
			printBean(mIntro);
			
			for(int i = 0; i < mLearnModeList.size(); i++) {
				LearnModeBean bean = mLearnModeList.get(i);
				System.out.println("问题数据如下：");
				printBean(bean.question);
				System.out.println("答案数据如下：");
				printBean(bean.answer);
				if(bean.option != null) {
					for(int j = 0; j < bean.option.size(); j++) {
						printBean(bean.option.get(j));
					}
				}
			}
		}
	}
	
	private void printBean(LearnBean bean) {
		System.out.println(bean.color + "");
		System.out.println(bean.font + "");
		System.out.println(bean.fontSize + "");
		System.out.println(bean.ownText + "");
		System.out.println("file:" + bean.file);
	}*/
}
