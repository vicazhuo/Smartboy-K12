package com.data;

import java.io.FileInputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.bean.BaseBean;
import com.bean.CatalogBean;
import com.bean.PackageBean;
import com.constant.Constant;
import com.net.RequestParameter;
import com.utils.Util;

/**
 * 解析教材目录
 * 请求参数：
 * <path> : 文件夹目录，不包含‘/’
 * <packagePath> : 
 *  
 * 该解析方式适合教程的inde.xml和package下的.xml解析方式
 * 
 * 解析过程说明：
 * 在函数中使用了递归的方式解析catalog数据节点，
 * 递归的出口为catalog的END_TAG事件，在每一次递归之后，返回到上一层时，该曾的END_TAG事件已执行，所以不能在上层的END_TAG事件中add到ArrayList中
 * 
 * 在mList列表中的CatalogBean数据中包含bg参数，表示当前多少课程的背景，其他节点中没有该参数，
 * 在每一个单元的字节点中都包含了intro介绍参数，该参数为null获取“”,在使用时需要判断两者
 * 在modey节点中，数值可为null，“”， “study”等。使用时不需要判断，直接传递到MetaData.java中。
 * 
 * 使用是注意：
 * 在getList()返回的数据列表中，CatalogBean类型有两种，CATALOG和PACKAGE。
 * 
 * @author lenovo
 *
 */
public final class BookData extends BaseData {

	/**
	 * 打开该教程时的启动动画
	 */
	private String mIndexBg;
	/**
	 * 该教程所对应资源的文件目录
	 */
	private ArrayList<CatalogBean> mList;
	
	private PackageBean mPackageBean;
	
	private BaseBean mBean;

	private final static String TAG = "book_data";
	private String mPackagePath;
	
	@Override
	public void startParse(RequestParameter parameter) throws Exception {

		Log.i(TAG, "开始解析教材目录数据");
		
		final String path = parameter.getValue("path");
		if(path == null || path.equals("")) 
			throw new NullPointerException("请求path参数为null");
		mPackagePath = parameter.getValue("packagePath");
		if(mPackagePath != null){
			mPackagePath += "/";
		}
		analyzeDocument(path);
			
		Log.i(TAG, "解析教材目录数据成功");
		
	} // startParse

	private void analyzeDocument(final String path) throws Exception {
		
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();
		FileInputStream inputStream = new FileInputStream(path + "/index.xml");
		parser.setInput(inputStream, "UTF-8");
		
		int event = parser.getEventType();
		CatalogBean catalog = null;
		
		while(event != XmlPullParser.END_DOCUMENT) {
			
			switch(event) {
			case XmlPullParser.START_DOCUMENT:
				mList = new ArrayList<CatalogBean>();
				break;
			case XmlPullParser.START_TAG:
				
				// 注意在该处是递归调用解析'catalog'标签
				// 递归出口为该catalog标签结束
				
				if("catalog".equals(parser.getName())) {
					catalog 		=	new CatalogBean();
					catalog.title	=	parser.getAttributeValue(null, "title");
					catalog.bg		=	Constant.KING_PAD_ICON + parser.getAttributeValue(null, "bg");
					catalog.icon	=	Constant.KING_PAD_ICON + parser.getAttributeValue(null, "icon");
					catalog.path	=	path + "/" + catalog.title;
					
					analyzeCatalog(catalog.path, parser, catalog);
					
					// 此时已解析了catalog的end节点
					// 在此处把catalog数据加入到列表中
					mList.add(catalog);
					catalog = null;
					
					event = parser.next();
					continue;
				} // if("catalog".equals(parse.getName()))
				
				
				// 解析教程启动动画
				if("book".equals(parser.getName())) {
					
					// 该资源文件路径为当前目录
					mIndexBg = Util.dealResPath(path, parser.getAttributeValue(null, "indexBG"));
					//mIndexBg = path + parser.getAttributeValue(null, "indexBG");
					Log.i(TAG, "解析教材启动动画成功");
					
					event = parser.next();
					continue;
				} 
				if("package".equals(parser.getName())) {
					// 处理package节点数据
					if("title".equals(parser.getAttributeName(0)) ) {
						mBean = new BaseBean();
						mBean.title = parser.getAttributeValue(null, "title");
						String icon = parser.getAttributeValue(null, "icon");
						if(icon != null)
							mBean.icon = Constant.KING_PAD_ICON + icon; 
						String bg = parser.getAttributeValue(null, "bg");
						if(bg != null)
							mBean.bg = Constant.KING_PAD_RES + bg;
					
					}
					else if("source".equals(parser.getAttributeName(0))) {
//						// 去掉目录路径和传递过来的标题路径
						String realPath = path.substring(0, path.lastIndexOf('/'));
//						realPath = path.substring(0, realPath.lastIndexOf('/') + 1);
						String name = parser.getAttributeValue(null, "source");
						
						mPackageBean = new PackageBean();
						mPackageBean.path	=	realPath + "/" + name;
						mPackageBean.title	=	name.substring(name.lastIndexOf('/') + 1 );
					}
					event = parser.next();
					continue;
				}
				// 解析section节点
				if("section".equals(parser.getName())) {
					if(catalog == null) {
						catalog = new CatalogBean();
						catalog.type_msg = CatalogBean.SECTION;
						catalog.sectionList = new ArrayList<CatalogBean>();
						mList.add(catalog);
					}
					CatalogBean sectionBean = new CatalogBean();
					sectionBean.title = parser.getAttributeValue(null, "title");
					sectionBean.intro = parser.getAttributeValue(null, "intro");
					sectionBean.icon = Constant.KING_PAD_ICON + parser.getAttributeValue(null, "icon");
					sectionBean.type = parser.getAttributeValue(null, "type");
					// 设置路径
					sectionBean.path = path + "/" + sectionBean.title;
					catalog.sectionList.add(sectionBean);
					sectionBean = null;
					event = parser.next();
					continue;
				}
				break;
				
			case XmlPullParser.END_TAG:
				
//				// 把节点数据加入到列表中
//				if("catalog".equals(parser.getName())) {
//					mList.add(catalog);
//					catalog = null;
//				}
//				break;
			} // switch
			
			
			event = parser.next();

		} // while
		
		
		
		inputStream.close();
	} // analyzeDocument
	
	/**
	 * 该处递归解析catalog节点数据
	 * @param path
	 * @param catalog
	 */
	private void analyzeCatalog(final String path, final XmlPullParser parser, CatalogBean catalog) throws Exception {
	
		// 跳转到下一个节点
		int event = parser.next();
		
		
		while(event != XmlPullParser.END_DOCUMENT) {
			switch(event) {
			case XmlPullParser.START_TAG:
				
				//解析目录数据
				if("catalog".equals(parser.getName())) {
					
					// 赋值上一层文件目录属性
					catalog.type_msg = CatalogBean.CATALOG;
					
					// 申请当前目录节点数据
					CatalogBean bean = new CatalogBean();
					
					// 解析标题，图标，描述
					bean.title		=	parser.getAttributeValue(null, "title");
					bean.icon		=	Constant.KING_PAD_ICON + parser.getAttributeValue(null, "icon");
					bean.intro		=	parser.getAttributeValue(null, "intro");
					bean.path		=	path + "/" + bean.title;
					
					// 递归调用获取所有子节点数据
					analyzeCatalog(bean.path, parser, bean);
					if(catalog.catalogList == null) 
						catalog.catalogList = new ArrayList<CatalogBean>(5);
					
					catalog.catalogList.add(bean);
					bean = null;
					
					event = parser.next();
					continue;
				}
				
				
				// 解析最终数据节点
				if("section".equals(parser.getName())) {
					
					catalog.type_msg = CatalogBean.SECTION;
					
					CatalogBean section = new CatalogBean();
					section.title	=	parser.getAttributeValue(null, "title");
					section.intro	=	parser.getAttributeValue(null, "intro");
					section.icon	=	Constant.KING_PAD_ICON + parser.getAttributeValue(null, "icon");
					section.type	=	parser.getAttributeValue(null, "type");
					section.mode	=	parser.getAttributeValue(null, "mode");
					
					section.path	=	path + "/" + section.title;
					
					if(catalog.sectionList == null)
						catalog.sectionList = new ArrayList<CatalogBean>(5);
					
					catalog.sectionList.add(section);
					
					event = parser.next();
					continue;
				}
				// 解析package数据
				if("package".equals(parser.getName())) {
					catalog.type_msg = CatalogBean.PACKAGE;
					// 去掉目录路径和传递过来的标题路径
//					String realPath = path.substring(0, path.lastIndexOf('/'));
//					realPath = path.substring(0, realPath.lastIndexOf('/') + 1);
					String name = parser.getAttributeValue(null, "source");
					
					PackageBean packageBean = new PackageBean();
//					packageBean.path	=	realPath + name;
					packageBean.path = mPackagePath + name;
					packageBean.title	=	name.substring(name.lastIndexOf('/') + 1 );
					
					if(catalog.packageList == null)
						catalog.packageList = new ArrayList<PackageBean>(3);
					
					catalog.packageList.add(packageBean);
					
					event = parser.next();
					continue;
				}
				break;
			case XmlPullParser.END_TAG:
				// 该处为递归接口的出口点
				if("catalog".equals(parser.getName())) {
					return;
				}
				break;
			} // switch
			
			event = parser.next();
			
		}// while
	} // analyzeCatalog
	
//	public void printData() {
//		for(int i = 0; i < mList.size(); i++) {
//			CatalogBean catalogBean = mList.get(i);
//			
//			printCatalog(catalogBean);
//			
//		} // for
//		
//	} // printData
//
//	private void printCatalog(CatalogBean catalogBean) {
//		
//		if(catalogBean.type_msg == CatalogBean.CATALOG) {
//			System.out.println("-------------------------------");
//			System.out.println(catalogBean.title);
//			System.out.println("bg:" + catalogBean.bg); 
//			System.out.println(catalogBean.icon);
//			System.out.println(catalogBean.path);
//			ArrayList<CatalogBean> catalogList = catalogBean.catalogList;
//			for(int j = 0; j < catalogList.size(); j++) {
//				CatalogBean bean = catalogList.get(j);
//				printCatalog(bean);
//			}
//		}
//		else if(catalogBean.type_msg == catalogBean.PACKAGE) {
//			System.out.println("-------------------------------");
//			System.out.println(catalogBean.title);
//			System.out.println("bg:" + catalogBean.bg);
//			System.out.println(catalogBean.icon);
//			System.out.println(catalogBean.path);
//			ArrayList<PackageBean> bean = catalogBean.packageList;
//			for(int j = 0; j < bean.size(); j ++) {
//				PackageBean packageBean = bean.get(j);
//				System.out.println(packageBean.title);
//				System.out.println(packageBean.path);
//			}
//		}
//		else if(catalogBean.type_msg == catalogBean.SECTION) {
////			System.out.println("-------------------------------");
////			System.out.println(catalogBean.title);
////			System.out.println("intro:" + catalogBean.intro);
////			System.out.println(catalogBean.icon);
////			System.out.println(catalogBean.path);
//			ArrayList<CatalogBean> sectionList = catalogBean.sectionList;
//			for(int j = 0; j < sectionList.size(); j++) {
//				BaseBean bean = sectionList.get(j);
//				System.out.println(bean.title);
//				System.out.println("bean:" + bean.intro);
//				System.out.println(bean.icon);
//				System.out.println(bean.type);
//				System.out.println("mode:" + bean.mode);
//				System.out.println(bean.path);
//			}
//		}
//	}

	
	
	public ArrayList<CatalogBean> getmList() {
		return mList;
	}

	public void setmList(ArrayList<CatalogBean> mList) {
		this.mList = mList;
	}
	
	public PackageBean getPackageBean() {
		return this.mPackageBean;
	}
	
	public BaseBean getBaseBean() {
		return this.mBean;
	}
	
	
	

	/**
	 * 作用：过滤目录，可以选择性的将某些目录过滤掉
	 * 时间：2013-1-28 下午1:45:56
	 * void
	 */
	public void filterContent(String [] list_content){
		for(int i = 0; i < mList.size(); i++) {
			CatalogBean catalogBean = mList.get(i);
			filterContentCatalog(catalogBean,list_content);
		}  
	}
	
	/** 作用：过滤一个CatalogBean的某些目录
	 * 时间：2013-1-28 下午1:47:46
	 * void
	 * @param catalogBean
	 * @param list_content
	 */
	private void filterContentCatalog(CatalogBean catalogBean,
			String[] list_content) {
		int size_content = list_content.length;
		/*
		 * 仅当次目录类型为 SECTION 时，才进行过滤
		 */
		 if(catalogBean.type_msg == catalogBean.SECTION) {
				System.out.println("从目录中找到一个SECTION...");
				ArrayList<CatalogBean> sectionList = catalogBean.sectionList;
				for(int j = 0; j < sectionList.size(); j++) {
					BaseBean bean = sectionList.get(j);
//					System.out.println("j="+j+"时，title="+bean.title);
					//过滤
					for(int k=0;k<size_content;k++){
						if(bean.title.equals(list_content[k])){
							System.out.println("过滤掉");
							System.out.println(bean.title);
							sectionList.remove(j);
						}
					}
				}
		}else if(catalogBean.type_msg == CatalogBean.CATALOG) {
			//如果目录类型是Catalog，那么继续递归过滤...
			ArrayList<CatalogBean> catalogList = catalogBean.catalogList;
			for(int j = 0; j < catalogList.size(); j++) {
				CatalogBean bean = catalogList.get(j);
				//过滤
				for(int k=0;k<size_content;k++){
					if(bean.title.equals(list_content[k])){
//						System.out.println("过滤掉");
//						System.out.println(bean.title);
						catalogList.remove(j);
					}
				}
				//递归
				filterContentCatalog(bean,list_content);
			}
		}
	}

	
	
}
