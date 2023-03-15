package com.meta.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.bean.BasicBean;
import com.constant.Constant;
import com.meta.Answer;
import com.meta.Data;
import com.meta.Intro;
import com.meta.Item;
import com.meta.Items;
import com.meta.Option;
import com.meta.Question;

public final class MetaUtil{
	
	/**
	 * 非学习模式解析数据
	 * @param path : 文件路径
	 * @return
	 * @throws Exception
	 */
	public static Data analyzeSubjective(final String path) throws Exception {
		
		DataImpl dataImpl = null;
		ItemsImpl itemsImpl = null, secondItemsImpl = null;
		ItemImpl itemImpl = null;
		IntroImpl introImp = null;
		
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();
		FileInputStream inputStream = new FileInputStream(new File(path + "/data.xml"));
		parser.setInput(inputStream, "UTF-8");
		int event = parser.getEventType();
		
		while(event != XmlPullParser.END_DOCUMENT) {
			
			switch(event) {
			case XmlPullParser.START_DOCUMENT:
				dataImpl = new DataImpl();
	
				break;
				
			case XmlPullParser.START_TAG:
				
				if("question".equals(parser.getName())) {
					QuestionImpl question = new QuestionImpl();
					
					// 解析属性节点
					int count = parser.getAttributeCount();
					for(int i = 0; i < count; i++) {
						String name = parser.getAttributeName(i);
						String value = parser.getAttributeValue(i);
						if (!"".equals(value))
							question.addAttribute(parser.getAttributeName(i), dealResPath(path, value, name));
					}
					
					// 解析数据内容
					String text = parser.nextText();
					if(text != null) {
						
						// 判断是否存在</p>或</span>标签
						if(text.contains("</p>") || text.contains("</span>")) {
						
							ArrayList<BasicBean> list =analyzeCDATA(text, path);
							question.addList(list);
						}
						else {
							BasicBean bean = new BasicBean();
							bean.setOwnText(text);
							question.addList(bean);
						}
					} 
					
					itemImpl.addQuestion(question);
					
					event = parser.next();
					continue;
				} // question
				if("answer".equals(parser.getName())) {
					AnswerImpl answer = new AnswerImpl();
					// 解析属性节点
					int count = parser.getAttributeCount();
					for(int i = 0; i < count; i++) {
						String name = parser.getAttributeName(i);
						String value = parser.getAttributeValue(i);
						if(value == null || "".equals(value)){
							continue;
						}
						answer.addAttribute(parser.getAttributeName(i), dealResPath(path, value, name));
					}
					
					// 解析数据内容
					String text = null;
					try {
						text = parser.nextText();
						if(text != null) {
							
							// 判断是否存在</p>或</span>标签
							if(text.contains("</p>") || text.contains("</span>")) {
							
								ArrayList<BasicBean> list =analyzeCDATA(text, path);
								answer.addList(list);
							}
							else {
								BasicBean bean = new BasicBean();
								bean.setOwnText(text);
								answer.addList(bean);
							}
						}
					} catch (Exception e) {
						// 此时数据中没有CDATA标签
						//parser.next();
						if("span".equals(parser.getName())) {
							BasicBean bean = new BasicBean();
							bean.setColor(parser.getAttributeValue(null, "color"));
							bean.setFont(parser.getAttributeValue(null, "fontFamily"));
							bean.setFontSize(parser.getAttributeValue(null, "fontSize"));
							bean.setOwnText(parser.nextText());
							
							answer.addList(bean);
						}
					}
					
					itemImpl.addAnswer(answer);
					event = parser.next();
					continue;
				} // answer
				
				if("option".equals(parser.getName())) {
					OptionImpl option = new OptionImpl();
					// 解析属性节点
					int count = parser.getAttributeCount();
					for(int i = 0; i < count; i++) {
						String name = parser.getAttributeName(i);
						String value = parser.getAttributeValue(i);
						if (!"".equals(value))
							option.addAttribute(parser.getAttributeName(i), dealResPath(path, value, name));
					}
					
					// 解析数据内容
					String text = parser.nextText();
					if(text != null) {
						
						// 判断是否存在</p>或</span>标签
						if(text.contains("</p>") || text.contains("</span>")) {
						
							ArrayList<BasicBean> list =analyzeCDATA(text, path);
							option.addList(list);
						}
						else {
							BasicBean bean = new BasicBean();
							bean.setOwnText(text);
							option.addList(bean);
						}
					}
					
					itemImpl.addOption(option);
					event = parser.next();
					continue;
				}
				
				// 解析item
				if("item".equals(parser.getName())) {
					itemImpl = new ItemImpl();
					
					// 解析item的属性
					int count = parser.getAttributeCount(); 
					for(int i = 0; i < count; i++) {
						String name = parser.getAttributeName(i);
						if("sourceFile".equals(name)  || "textFile".equals(name) || "musicFile".equals(name)){
							String value = parser.getAttributeValue(i);
							value = path+"/"+value;
							itemImpl.addAttribute(name,value);
						}else{
							String value = parser.getAttributeValue(i);
							itemImpl.addAttribute(parser.getAttributeName(i), dealResPath(path, value, name));
						}
					}
					event = parser.next();
					continue;
				}
				
				
				// 解析intro // 解析intro
				if("intro".equals(parser.getName())) {
					introImp = new IntroImpl();
					
					String soundFile = parser.getAttributeValue(null, "soundFile");
					introImp.addAttribute("soundFile", dealResPath(path, soundFile));
					int count = parser.getAttributeCount();
//					if(count == 0)
//					{
//						event = parser.next();
//						continue;
//					}
					
					for(int i = 0; i < count; i++) {
						String name = parser.getAttributeName(i);
						String value = parser.getAttributeValue(i);
						if(!"".equals(value))
							introImp.addAttribute(parser.getAttributeName(i), dealResPath(path, value, name));
					}
					
					try
					{
						String text = parser.nextText();
						if(text != null) {
							ArrayList<BasicBean> list = analyzeCDATA(text, path);
							if(list.get(0) != null)
								introImp.setIntro(list.get(0));
						}
					}
					catch (Exception e) {
						parser.next();
						if(!"span".equals(parser.getName())) {
							parser.next();
						}
						
						BasicBean bean = new BasicBean();
						bean.setColor(parser.getAttributeValue(null, "color"));
						bean.setFont(parser.getAttributeValue(null, "fontFamily"));
						bean.setFontSize(parser.getAttributeValue(null, "fontSize"));
						
						//introImp.setIntro(bean);
						
					} 
					
					
					
					// 把介绍添加到items中
					if(secondItemsImpl != null)
						secondItemsImpl.setIntro(introImp);
					else
						itemsImpl.setIntro(introImp);
					
					event = parser.next();
					

					continue;
				} // intro


				
				// 解析items节点
				if("items".equals(parser.getName())) {
					if(itemsImpl == null) {
						// 申请节点数据
						itemsImpl = new ItemsImpl();
					} 
					else if(secondItemsImpl == null) {
						secondItemsImpl = new ItemsImpl();
					}
					event = parser.next();
					continue;
					
				} // items
				
				
				// 解析data节点
				if("data".equals(parser.getName())) {
					String title = parser.getAttributeValue(null, "title");
					String bg = parser.getAttributeValue(null, "bg");
					dataImpl.addAttribute("title", title);
					dataImpl.addAttribute("bg", Constant.KING_PAD_RES + bg);
					event = parser.next();
					continue;
				}
				break;
				
			case XmlPullParser.END_TAG:
				if("item".equals(parser.getName())) {
					if(secondItemsImpl == null) {
					itemsImpl.addList(itemImpl);
					itemImpl = null;
				} else {
					secondItemsImpl.addList(itemImpl);
					itemImpl = null;
				}
					event = parser.next();
					continue;
				}
 				
				if("items".equals(parser.getName())) {
					if(secondItemsImpl == null) {
						dataImpl.addList(itemsImpl);
						itemsImpl = null;
					} else {
						itemsImpl.setItems(secondItemsImpl);
						secondItemsImpl = null;
					}
					event = parser.next();
					continue;
				}
				break;
			}
			
			event = parser.next();
			
		} // while
		
		return dataImpl;
		
	} // analyzeSubjective
	
	
	/**
	 * 解析数据中包含CDATA格式的xml数据
	 * @param data ： 数据内容
	 * @param bean ： 解析后返回的数据对象
	 */
	private static ArrayList<BasicBean> analyzeCDATA(final String data, final String path) throws Exception{

//		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//		factory.setNamespaceAware(true);
//		XmlPullParser parser = factory.newPullParser();
//		
//		String test = data;
//		test = test.replace('\n', ' ');
//		test = test.replace((char)12288, ' ');
//		test = test.trim();
//		
//		ByteArrayInputStream inputStream = new ByteArrayInputStream(test.getBytes());
//		
//		parser.setInput(inputStream, "UTF-8");
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();
		String test = data;
		test = test.replace('\n', ' ');
		test = test.replace((char)12288, ' ');
		test = test.trim();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(test.getBytes());
		parser.setInput(inputStream, "UTF-8");
		
		
		
		
		

		int event = parser.getEventType();
		ArrayList<BasicBean> list = null;
		BasicBean bean = null;
		while( event != XmlPullParser.END_DOCUMENT) {
			switch(event) {
			case XmlPullParser.START_DOCUMENT:
				list = new ArrayList<BasicBean>(1);
			case XmlPullParser.START_TAG:
				if("img".equals(parser.getName())) {
					bean = new BasicBean(); 
					String str_width = parser.getAttributeValue(null, "width");
					String str_height = parser.getAttributeValue(null, "height");
					if(str_width == null){
						bean.setWidth_img(0);
					}else{
						bean.setWidth_img(Integer.parseInt(str_width));
					}
					if(str_width == null){
						bean.setHeight_img(0);
					}else{
						bean.setHeight_img(Integer.parseInt(str_height));
					}
					String source = parser.getAttributeValue(null, "source"); 
					bean.setPath(source.replaceFirst("sectionData:",path));
					bean.setPicture(true);
					list.add(bean);
				}
				if("span".equals(parser.getName()) || "p".equals(parser.getName())) {
					
					if(parser.getAttributeCount() == 0){
						event = parser.next();
						continue;
					}
					
					
					bean = new BasicBean();
					bean.setColor(parser.getAttributeValue(null, "color"));
					bean.setFont(parser.getAttributeValue(null, "fontFamily"));
					bean.setFontSize(parser.getAttributeValue(null, "fontSize"));
					bean.setOwnText(parser.nextText());
					list.add(bean);
				}
				break;
			} // switch
			event = parser.next();
		} // while
		inputStream.close();
		return list;
	} // analyzeCDATA
	
	
	public static String dealResPath(final String path, String text) {
		if(text != null && text.contains("app:/")) {
			return text.replaceAll("app:/", Constant.KING_PAD_RES);
		}
		return path + "/" + text;
	}
	
	
	public static String dealResPath(final String path, String text, final String name) {
		if("soundFile".equals(name) || "sourceFile".equals(name)) {
			return dealResPath(path, text);
		}
		else if ("bg".equals(name)) {
			return Constant.KING_PAD_RES + text;
		}

		
		return text;
	}
	
	//-------------------------------------------------------
	// 打印消息接口
	public static void printData(Data data) {
		System.out.println("开始打印数据。。。");
		if(data == null)
			return;
		for(int i = 0; i < data.getAttributeCount(); i++) {
			System.out.println(data.getAttribueName(i)  + " = " + data.getAttributeValue(i));
		}
		ArrayList<Items> itemsList = data.getItemsList();
		if(itemsList == null)
			return;
		for(int i = 0; i < itemsList.size(); i++) {
			Items items = itemsList.get(i);
			printItems(items);
		} // items
	}
	public static void printItems(Items items) {
		Intro intro= items.getIntro();
		if(intro != null) {
			BasicBean bean = intro.getBasicBean(); 
			System.out.println("intro...声音文件：" + intro.getAttributeValue(0));
			if(bean != null)
				printBasicBean(bean);
		}
		
		ArrayList<Item> list = items.getItemList();
		for (int i=0, n=list.size(); i<n; i++){
			printItem(list.get(i));
		}
	}
	
	public static void printItem(Item item) {
		
		for(int i  = 0; i < item.getAttributeCount(); i++) {
			System.out.println("第"+i+"个item属性");
			System.out.println(item.getAttribueName(i)  + " = " + item.getAttributeValue(i));
		}
		
		
		if(item.getQuestionList() != null) {
			System.out.println("QuestionList不为空。。");
			for(int j = 0; j < item.getQuestionList().size(); j++) {
				Question question = item.getQuestionList().get(j);
				System.out.println("第"+j+"个question");
				for(int i  = 0; i < question.getAttributeCount(); i++) {
					System.out.println("QuestionList的第+"+i+"个属性："+question.getAttribueName(i)  + " = " + question.getAttributeValue(i));
				}
				System.out.println("question的 BasicBean...");
				ArrayList<BasicBean> list = question.getList();
				if(list != null)
					for(int i = 0; i < list.size(); i++) {
						System.out.println("i="+i+",BasicBean...");
						printBasicBean(list.get(i));
					}
			}
		}
		
		if(item.getAnswerList() != null) {
			System.out.println("AnswerList不为空。。");
			for(int j = 0; j < item.getAnswerList().size(); j++) {
				Answer answer = item.getAnswerList().get(j);
				System.out.println("第"+j+"个answer");
				for(int i  = 0; i < answer.getAttributeCount(); i++) {
					System.out.println("answer的第+"+i+"个属性："+answer.getAttribueName(i)  + " = " + answer.getAttributeValue(i));
				}

				System.out.println("question的 BasicBean...");
				ArrayList<BasicBean> list = answer.getList();
				if(list != null)
					for(int i = 0; i < list.size(); i++) {
						System.out.println("i="+i+",BasicBean...");
						printBasicBean(list.get(i));
					}
			}
		} // if
		
		if(item.getOptionList() != null) {	
			System.out.println("OptionList不为空。。");
			for(int j = 0; j < item.getOptionList().size(); j++) {
				Option answer = item.getOptionList().get(j);
				System.out.println("第"+j+"Option");
				for(int i  = 0; i < answer.getAttributeCount(); i++) {
					System.out.println("Option的第+"+i+"个属性："+answer.getAttribueName(i)  + " = " + answer.getAttributeValue(i));
				}

				System.out.println("Option的 BasicBean...");
				ArrayList<BasicBean> list = answer.getList();
				if(list != null)
					for(int i = 0; i < list.size(); i++) {
						System.out.println("i="+i+",BasicBean...");
						printBasicBean(list.get(i));
					}
			}
		}
	}
	
	public static void printBasicBean(BasicBean bean) {
		System.out.println("color=" + bean.getColor());
		System.out.println("font="+ bean.getFont());
		System.out.println("fontsize=" + bean.getFontSize());
		System.out.println("owntext=" + bean.getOwnText());
		System.out.println("path = " + bean.getPath());
	}
}
