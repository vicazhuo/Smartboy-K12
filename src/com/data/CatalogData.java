package com.data;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

import com.bean.BaseBean;
import com.bean.CatalogBean;
import com.net.RequestParameter;
/**
 * 解析目录结构。例如 一级，需要发送一个一级的目录
 * <path> </path> : 目录url地址
 * 
 * 不建议使用该类来解析index.xml
 * 
 * @author lenovo
 *
 */
public final class CatalogData extends BaseData {
	
	private final String TAG = "catalog_data";
	/**
	 * 保存当前目录下的所有信息
	 */
	private CatalogBean mCatalog;
	
	@Override
	public void startParse(RequestParameter parameter) throws Exception {
		Log.i(TAG, "开始解析index.xml文件数据");
		
		String filePath = parameter.getValue("path");
		if(filePath == null || filePath.equals(""))
			throw new NullPointerException("请求参数为null");
		
		analyzeDocment(filePath);
		Log.i(TAG, "数据解析成功");
	}

	/**
	 * 解析xml文件
	 * @param path
	 */
	private void analyzeDocment(final String path) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File(path + "/index.xml"));
		Element root	= 	document.getDocumentElement();
		
		// <package title="一级" icon="icon/章节.png">
		mCatalog 		=	new CatalogBean();
		mCatalog.title	=	root.getAttribute("title");
		mCatalog.icon	=	root.getAttribute("icon");
		mCatalog.intro	=	root.getAttribute("intro");
		mCatalog.type	=	root.getAttribute("type");
		mCatalog.mode	=	root.getAttribute("mode");
		mCatalog.path	=	path;
		
		
		// 不能再此处设置，因为该处表示的是下一级目录数据
		//	mCatalog.type	=	BaseBean.CATALOG;

		NodeList list = root.getChildNodes();
		
		for(int i = 0, len = list.getLength(); i < len; i++) {
			Node node = list.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			// 判断该目录的属性，是目录还是文件
			String text = node.getLocalName();
			if(text != null && text.equals("catalog")) {
				
				// 该级为目录
				if(mCatalog.type_msg == 0) {
					mCatalog.type_msg = CatalogBean.CATALOG;
					mCatalog.catalogList = new ArrayList<CatalogBean>();
				}
					
				CatalogBean bean = analyzeCatalog((Element)node, path);
				mCatalog.catalogList.add(bean);
			}
			else if(text != null && text.equals("section")) {
				
				// 该级为文件
				if(mCatalog.type_msg == 0) {
					mCatalog.type_msg = CatalogBean.SECTION;
					mCatalog.sectionList  = new ArrayList<CatalogBean>();
				}
				CatalogBean bean = analyzeSection((Element)node, path);
				mCatalog.sectionList.add(bean);
			}
		} // for
		
		
	} // analyzeDocment
	
	/**
	 * 解析目录结构,递归调用分析是目录还是节点
	 * @param element
	 * @param path
	 * @return ：返回目录结构递归树
	 */
	private CatalogBean analyzeCatalog(final Element element, final String path) {
		CatalogBean catalog = new CatalogBean();
		catalog.title	=	element.getAttribute("title");
		catalog.icon	=	element.getAttribute("icon");
		catalog.intro	=	element.getAttribute("intro");
		catalog.type	=	element.getAttribute("type");
		catalog.mode	=	element.getAttribute("mode");
		catalog.path	=	path + "/" + catalog.title;
		
		// 不能再此处设置，因为该处表示的是下一级目录数据
		//	mCatalog.type	=	BaseBean.CATALOG;
		NodeList list = element.getChildNodes();
		
		for(int i = 0, len = list.getLength(); i < len; i++) {
			Node node = list.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			// 判断该目录的属性，是目录还是文件
			String text = node.getLocalName();
			if(text != null && text.equals("catalog")) {
				
				// 该级为目录
				if(catalog.type_msg == 0) {
					catalog.type_msg = CatalogBean.CATALOG;
					catalog.catalogList = new ArrayList<CatalogBean>();
				}
					
				CatalogBean bean = analyzeCatalog((Element)node, catalog.path);
				catalog.catalogList.add(bean);
			}
			else if(text != null && text.equals("section")) {
				
				// 该级为文件
				if(catalog.type_msg == 0) {
					catalog.type_msg = CatalogBean.SECTION;
					catalog.sectionList = new ArrayList<CatalogBean>();
				}
				CatalogBean bean = analyzeSection((Element)node, catalog.path);
				catalog.sectionList.add(bean);
			}
		} // for
		
		return catalog;
	} // analyzeCatalog
	
	/**
	 * 解析每个节点的可选择操作
	 * @param element ：当前节点Element队形
	 * @param path ： 当前路径
	 * @return ： 返回当前节点的数据属性
	 */
	private CatalogBean analyzeSection(final Element element, final String path) {
		CatalogBean section = new CatalogBean();
		section.title	=	element.getAttribute("title");
		section.icon	=	element.getAttribute("icon");
		section.intro	=	element.getAttribute("intro");
		section.mode	=	element.getAttribute("mode");
		section.type	=	element.getAttribute("type");
		section.path	=	path + "/" + section.title;
		
		
		return section;
	}
	
	/**
	 * 返回当前目录的递归树
	 * @return : 返回当前目录递归树
	 */
	public CatalogBean getCatalog() {
		return this.mCatalog;
	}
	

	/*public void printData() {
		
		System.out.println(mCatalog.title);
		System.out.println(mCatalog.icon);
		System.out.println(mCatalog.path);
		System.out.println(mCatalog.intro);
		System.out.println(mCatalog.mode);
		System.out.println(mCatalog.type);
		
		printCatalog(mCatalog);
	}
	
	public void printCatalog(CatalogBean catalog) {
		System.out.println(catalog.title);
		System.out.println(catalog.icon);
		System.out.println(catalog.path);
		System.out.println(catalog.intro);
		System.out.println(catalog.type);
		System.out.println(catalog.mode);
		if(catalog.type_msg == CatalogBean.CATALOG) {
			for(int i = 0; i < catalog.catalogList.size(); i++) {
				CatalogBean bean = catalog.catalogList.get(i);
				printCatalog(bean);
			}//
		}
		else if(catalog.type_msg == CatalogBean.SECTION) {
			for(int i = 0; i < catalog.sectionList.size(); i++) {
				BaseBean bean = catalog.sectionList.get(i);
				System.out.println(bean.title);
				System.out.println(bean.icon);
				System.out.println(bean.path);
				System.out.println(bean.intro);
				System.out.println(bean.type);
				System.out.println(bean.mode);
			}
		}
	}*/
}
