package com.data;

import java.io.FileInputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.net.RequestParameter;

/**
 * 解析古诗欣赏模块
 * <path>: 诗词文件目录路径，是圈路径包含文件名/ .../1.xml
 * @author lenovo
 *
 */
public final class PoemData extends BaseData {

	/**
	 * 诗词标题
	 */
	private String mTitle;
	/**
	 * 诗词作者
	 */
	private String mAuthor;
	/**
	 * 诗词内容
	 */
	private ArrayList<String> mContentList;
	
	private final static String TAG = "poem_data";
	
	@Override
	public void startParse(RequestParameter parameter) throws Exception {
		Log.i(TAG, "开始解析诗词信息");
		
		final String path = parameter.getValue("path");
		if(path == null || path.equals(""))
			throw new NullPointerException("请求参数path为null");
		
		analyzeDocument(path);
		
		Log.i(TAG, "解析诗词成功！");
		
	} // startParse

	/**
	 * 解析数据
	 * @param path
	 * @throws Exception
	 */
	private void analyzeDocument(final String path) throws Exception {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();
		FileInputStream inputStream = new FileInputStream(path);
		parser.setInput(inputStream, "UTF-8");
		
		int event = parser.getEventType();
		
		while( event != XmlPullParser.END_DOCUMENT ) {
			switch(event) {
			case XmlPullParser.START_DOCUMENT:
				mContentList = new ArrayList<String>();
				break;
			case XmlPullParser.START_TAG:
				
				if("s".equals(parser.getName())) {
					String text = parser.nextText();
					if(text != null)
						mContentList.add(text);
					event = parser.next();
					continue;
				}
				
				// 解析作者
				if("author".equals(parser.getName())) {
					mAuthor = parser.nextText();
					event = parser.next();
					continue;
				}
				
				// 解析标题
				if("title".equals(parser.getName())) {
					mTitle = parser.nextText();
					event = parser.next();
					continue;
				}
				break;
			} // switch
			
			event = parser.next();
		} // while
		
		
		inputStream.close();
		
	} // analyzeDocument
	
	
	public String getTitle() {
		return this.mTitle;
	}
	
	public String getAuthor() {
		return this.mAuthor;
	}
	
	public ArrayList<String> getContentList() {
		return this.mContentList;
	}
	
	/*public void printData() {
		System.out.println(mTitle);
		System.out.println(mAuthor);
		for(int i = 0; i < mContentList.size(); i++) {
			System.out.println(mContentList.get(i));
		}
	}*/
}
