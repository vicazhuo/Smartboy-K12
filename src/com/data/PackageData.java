package com.data;

import java.io.FileInputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.bean.BaseBean;
import com.bean.CatalogBean;
import com.bean.PackageBean;
import com.constant.Constant;
import com.net.RequestParameter;

public final class PackageData extends BaseData {

	/**
	 * package节点数据属性
	 */
	private BaseBean mBean;
	/**
	 * package下index.xml文件中的catalog节点集合
	 */
	private ArrayList<CatalogBean> mCatalogList;
	

	@Override
	public void startParse(RequestParameter parameter) throws Exception {
		final String path = parameter.getValue("path");
		if(null == path)
			throw new NullPointerException("path参数为null");
		analyzeDocument(path);
	} // startParse

	private void analyzeDocument(final String path) throws Exception{
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
				mBean = new BaseBean();
				mCatalogList = new ArrayList<CatalogBean>();
				break;
			case XmlPullParser.START_TAG:
				
				// 解析catalog节点
				if("catalog".equals(parser.getName())) {
					catalog = new CatalogBean();
					// 解析属性节点
					catalog.title		=	parser.getAttributeValue(null, "title");
					catalog.icon		=	Constant.KING_PAD_ICON + parser.getAttributeValue(null, "icon");
					catalog.intro		=	parser.getAttributeValue(null, "intro");
					catalog.path		=	path + "/" + catalog.title;
					
					analyzeCatalog(path, parser, catalog);
					mCatalogList.add(catalog);
					catalog = null;
					
					event = parser.next();
					continue;
				}
				
				// 解析package节点
				if("package".equals(parser.getName())) {
					mBean.title = parser.getAttributeValue(null, "title");
					String icon = parser.getAttributeValue(null, "icon");
					if(icon != null)
						mBean.icon = Constant.KING_PAD_ICON + icon; 
					String bg = parser.getAttributeValue(null, "bg");
					if(bg != null)
						mBean.bg = Constant.KING_PAD_RES + bg;
					
					event = parser.next();
					continue;
				}
				break;
			}
			
			
			event = parser.next();
		}
		
		inputStream.close();
	} // analyzeDocument
	
	/**
	 * 递归调用解析数据节点
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
					String realPath = path.substring(0, path.lastIndexOf('/'));
					realPath = path.substring(0, realPath.lastIndexOf('/') + 1);
					String name = parser.getAttributeValue(null, "source");
					
					PackageBean packageBean = new PackageBean();
					packageBean.path	=	realPath + name;
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
	
	
	public BaseBean getBaseBean() {
		return this.mBean;
	}
	
	public ArrayList<CatalogBean> getCatalogList() {
		return mCatalogList;
	}
	
	/*public void printData() {
		System.out.println(mBean.bg + "");
		System.out.println(mBean.title + "");
		System.out.println(mBean.icon + "");
		
		for(int i = 0; i < mCatalogList.size(); i++) {
			CatalogBean catalogBean = mCatalogList.get(i);
			
			printCatalog(catalogBean);
			
		} // for
		
	} // printData
	private void printCatalog(CatalogBean catalogBean) {
		
		if(catalogBean.type_msg == CatalogBean.CATALOG) {
			System.out.println("-------------------------------");
			System.out.println(catalogBean.title);
			System.out.println("bg:" + catalogBean.bg); 
			System.out.println(catalogBean.icon);
			System.out.println(catalogBean.path);
			ArrayList<CatalogBean> catalogList = catalogBean.catalogList;
			for(int j = 0; j < catalogList.size(); j++) {
				CatalogBean bean = catalogList.get(j);
				printCatalog(bean);
			}
			
		}
		else if(catalogBean.type_msg == catalogBean.PACKAGE) {
			System.out.println("-------------------------------");
			System.out.println(catalogBean.title);
			System.out.println("bg:" + catalogBean.bg);
			System.out.println(catalogBean.icon);
			System.out.println(catalogBean.path);
			ArrayList<PackageBean> bean = catalogBean.packageList;
			for(int j = 0; j < bean.size(); j ++) {
				PackageBean packageBean = bean.get(j);
				System.out.println(packageBean.title);
				System.out.println(packageBean.path);
			}
		}
		else if(catalogBean.type_msg == catalogBean.SECTION) {
			System.out.println("-------------------------------");
			System.out.println(catalogBean.title);
			System.out.println("intro:" + catalogBean.intro);
			System.out.println(catalogBean.icon);
			System.out.println(catalogBean.path);
			
			ArrayList<CatalogBean> sectionList = catalogBean.sectionList;
			
			for(int j = 0; j < sectionList.size(); j++) {
				BaseBean bean = sectionList.get(j);
				System.out.println(bean.title);
				System.out.println("bean:" + bean.intro);
				System.out.println(bean.icon);
				System.out.println(bean.type);
				System.out.println("mode:" + bean.mode);
				System.out.println(bean.path);
			}
		}
	}*/
}
