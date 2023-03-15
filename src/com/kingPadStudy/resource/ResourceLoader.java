/** 文件作用：资源加载器 
 *    		封装DATA解析类；
 *    		装载各种数据和资源 
 *	创建时间：2012-11-17 下午8:21:22
 *  作者： 陈相伯 
 *  描述：
 */
package com.kingPadStudy.resource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.content.Context;
import android.os.Message;
import android.widget.Toast;

import com.bean.CatalogBean;
import com.bean.PackageBean;
import com.data.BookData;
import com.data.MetaUtilData;
import com.data.PoemData;
import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.adapter.LeftMenuAdapter;
import com.kingPadStudy.constant.Constant;
import com.kingPadStudy.controller.ViewController;
import com.kingPadStudy.tools.Demension;
import com.kingPadStudy.tools.SWFSizeGetter;
import com.kingPadStudy.views.MenuView;
import com.meta.Data;
import com.meta.Item;
import com.meta.Items;
import com.meta.impl.MetaUtil;
import com.net.LoadData;
import com.net.RequestListener;
import com.net.RequestParameter;

/** 描述：资源加载器 
 *    		封装DATA解析类；
 *    		装载各种数据和资源  
 */
public class ResourceLoader {
	/*
	 * 数据加载完后处理类型
	 */
	static int type_handle = -1;
	/*
	 * 左边菜单的处理对象
	 */
	static LeftMenuAdapter adapter_leftMenu = null;
	/*
	 * 大目录
	 */
	public static BookData bigIndex = null;
	/*
	 * 小目录
	 */
	static BookData smallIndex = null;
	/*
	 * 当前使用新类型的资源
	 */
	static MetaUtilData currentUtilData = null;
	/*
	 * 路径数组    记录路径每一级的下标
	 */
	public static int[] index_path = new int[10];
	/*
	 * 路径显示数组 记录路径每一级的字符串
	 */
	static String[] string_path = new String[10];
	/*
	 * 当前的遍历路径级数
	 */
	private static int index_level_current_recycle = 2;
	/*
	 * 左边显示图标路径
	 */
	private static ArrayList<String> icon_menu_left =null;
	/*
	 * 左边显示名称
	 */
	private static ArrayList<String> name_menu_left =null;
	/*
	 * 左边高亮下标
	 */
	private static int index_highlight_left = -1;
	/*
	 * 中间显示图标路径
	 */
	private static ArrayList<String> icon_menu_center =null;
	/*
	 * 左边显示名称
	 */
	private static ArrayList<String> name_menu_center =null;
	/*
	 * PACKAGE 图标
	 */
	public static String icon_packcage = "mnt/sdcard/kingpad/assets/icon/章节.png";
	/*
	 *  菜单视图实例
	 */
	private static MenuView menuView = null;
	/*
	 * 小目录index.xml 所在路径中的位置
	 */
	private static int position_small = -1;
	/*
	 * 过滤数组
	 */
	private static String [] list_content_big = {"考前达标测试","筛查复习","互动笔记"}; 
	private static String [] list_content_small = {"互动笔记","笔顺笔画知识要点筛查","赛场规则先掌握","查字方法要点筛查","发音要领筛查","语法复习筛查"};  
	
	
	/**
	 * 函数作用：点击左边的菜单
	 * void
	 * @param index
	 */
	public static void touchLeftMenu(final int index){
		if(index_path[0] == 2 ){
			if(index != bigIndex.getmList().size()){
				position_small = 100;
			}
		}
		type_touch = 2;
		//路径数组
		index_path[index_path[0]] = index;
		boolean isLoadSmall = false;
		String path_load = ""; 
		if( index_path[2] == bigIndex.getmList().size()){
			//当为独立package
			if(index_path[0] == 2 ){
				//第二级时，加载小目录
				isLoadSmall = true;
				path_load = bigIndex.getPackageBean().path;
				position_small = 2; 
			}
		}else{
			if((index_path[0] == position_small && index_path[2] != 0)){
				//点击了级别，加载小目录
				isLoadSmall = true;
				CatalogBean bean = bigIndex.getmList().get(index_path[2]);
				boolean isPackage = false;
				for(int i = 3; i<= index_path[0]; i++){
					//System.out.println("在touchLeftMenu中。i="+i+", ");
					ArrayList<PackageBean> list_package = bean.packageList;
					if( list_package == null ){
						//System.out.println("在touchLeftMenu中。  packageList为空 ");
						bean = bean.catalogList.get(index_path[i]);
					}else{
						if(i!=index_path[0]){
							break;
						}
						//System.out.println("在touchLeftMenu中。  packageList不为空 ");
						path_load = list_package.get(index).path;
						//System.out.println("在touchLeftMenu中。  path_load= "+path_load);
						isPackage = true;
					}
				}
			} 
		}
		if(isLoadSmall){
			//要加载小目录了
			//System.out.println("path_load="+path_load);
			RequestParameter parameter = new RequestParameter();
		    parameter.add("path", path_load );
		    LoadData.loadData(Constant.BOOK_DATA, parameter, new RequestListener() {
				public void onError(String error) {
					
				}
				public void onComplete(Object obj) {
					smallIndex = (BookData) obj;
					smallIndex.filterContent(list_content_small);
					handleLeftMenu(index);
				}
		   }); 
		}else{
			handleLeftMenu(index);
		}
	}
	
	
	/**
	 * 函数作用：辅助touchLeftMenu 函数
	 * void
	 * @param index 点击的菜单的下标
	 */
	public static void handleLeftMenu(int index){
		//显示数组
		string_path[index_path[0]] = findPathTitle(index_path[0],index,true);
		//System.out.println("获取到的标题是："+string_path[index_path[0]]);
		//改变左边高亮下标
		index_highlight_left = index;
		//中间图标和数组改变
		name_menu_center = new ArrayList<String>();
		icon_menu_center = new ArrayList<String>();
		if(index_path[0] == 2){
			//System.out.println("在handleLeftMenu中。 index_path[0] == 2");
			if(index == 0){
				//触摸同步学习
				ArrayList<CatalogBean> beans = getCatalogList(index_path[0]+1, true,false);
				int size_content = beans.size();
				for(int i=0;i<size_content;i++){
					String title = beans.get(i).title;
					name_menu_center.add(beans.get(i).title);
					icon_menu_center.add(beans.get(i).icon);
				}
			}else{
				//System.out.println("不是同步学习");
				if(index == bigIndex.getmList().size()){
					//System.out.println("独立package");
					//若点击了独立package
					ArrayList<CatalogBean> beans = smallIndex.getmList();
					int size = beans.size();
					for(int i=0;i<size;i++){
						CatalogBean bean = beans.get(i);
						name_menu_center.add(bean.title);
						icon_menu_center.add(bean.icon);
					}
				}else{
					//System.out.println("不是独立package");
					//如果中间内容是PACKAGE类型
					ArrayList<PackageBean> beans = getPackageList(index_path[0]+1);
					if(beans == null){
						//System.out.println("在handleLeftMenu中。 判断出不是package.");
						//不是package，是catalog
						ArrayList<CatalogBean> beans2 = getCatalogList(index_path[0]+1, true,false);
						int size_content = beans2.size();
						for(int i=0;i<size_content;i++){
							name_menu_center.add(beans2.get(i).title);
							icon_menu_center.add(beans2.get(i).icon);
						}
					}else{
						//System.out.println("在handleLeftMenu中。 判断出是package.");
						int size_content = beans.size();
						for(int i=0;i<size_content;i++){
							name_menu_center.add(beans.get(i).title);
							icon_menu_center.add(icon_packcage);
						}
					}
				}
			}
		}else{
			ArrayList<CatalogBean> beans = null;
			if(index_path[2] == bigIndex.getmList().size() ){
				//使用独立package
				index_level_current_recycle = 3;
				beans = getCatalogList(index_path[0]+1, false,true);
				int size_content = beans.size();
				for(int i=0;i<size_content;i++){
					name_menu_center.add(beans.get(i).title);
					icon_menu_center.add(beans.get(i).icon);
				}
			}else{
				//判断是否是package
				CatalogBean bean = bigIndex.getmList().get(index_path[2]);
				boolean isPackage = false;
				for(int i = 3; i<= index_path[0]+1 ; i++){
					ArrayList<PackageBean> list_package = bean.packageList;
					if( list_package == null ){
						if(i == index_path[0]+1){
							break;
						}
						if(bean.catalogList == null){
							bean = bean.sectionList.get(index_path[i]);
						}else{
							bean = bean.catalogList.get(index_path[i]);
						}
					}else{
						if(i!=index_path[0]+1){
							break;
						}
						isPackage = true;
						int size_p = list_package.size();
						for(int j=0;j<size_p;j++){
							String title = list_package.get(j).title;
							name_menu_center.add(title);
							icon_menu_center.add(icon_packcage);
						}
					}
				}
				if(!isPackage){
					//System.out.println("在handleLeftMenu中。 处理中间目录时，判断中间目录不是package.准备获取index_path[0]+1的级别目录："+(index_path[0]+1));
					boolean isSectionRoot = false;
					//判断是否要使用根目录是否是sectionList的级别
					if(isUsingSmallIndex()){
						//System.out.println("在handleLeftMenu中。使用了SMALLDATA,index_path[0]="+index_path[0]+",position_small="+position_small);
						//System.out.println("smallIndex.getmList().size()="+smallIndex.getmList().size());
						if(index_path[0] == position_small && smallIndex.getmList().size() == 1){
							isSectionRoot = true;
							ArrayList<CatalogBean> beansSection = smallIndex.getmList().get(0).sectionList;
							int size_content = beansSection.size();
							for(int i=0;i<size_content;i++){
								name_menu_center.add(beansSection.get(i).title);
								icon_menu_center.add(beansSection.get(i).icon);
							}
						}
					}
					if(!isSectionRoot){
						beans = getCatalogList(index_path[0]+1, true,false); 
						int size_content = beans.size();
						for(int i=0;i<size_content;i++){
							String title = beans.get(i).title;
							name_menu_center.add(title);
							icon_menu_center.add(beans.get(i).icon);
						}
					}
				}
			}
		}
		//处理界面
		menuView.setData();
		menuView.setShow();
	}
	
	
	public static boolean isUsingSmallIndex(){
		 if(index_path[2]!= 0 && index_path[0]>2){
			 return true;
		 } 
		 return false;
	}
	
	public static boolean isLeafNode(){
		//判断是否到了叶子节点
		if(index_path[2] == bigIndex.getmList().size() && index_path[0]>2 ){
			//使用了独立package
			//System.out.println("在isLeafNode中，判断出使用了独立package");
			CatalogBean catalogBean = smallIndex.getmList().get(index_path[3]);
			if(catalogBean.type_msg == CatalogBean.SECTION){
				return true;
			}
			for(int i = 4;i<=index_path[0];i++ ){
				catalogBean = catalogBean.catalogList.get(index_path[i]);
				if(catalogBean.type_msg == CatalogBean.SECTION){
					return true;
				}
			}
		}else{
			//System.out.println("在isLeafNode中，判断出没有使用独立package");
			if(isUsingSmallIndex()){
				//System.out.println("在isLeafNode中，判断出使用了小目录index_path[0]="+index_path[0]);
				//使用了小目录
				if(index_path[0]<=3){
					//如果现在的目录还没有到第四级别
					return false;
				}
				//System.out.println("在isLeafNode中，position_small="+position_small);
				if(position_small < 4  ){
//					//System.out.println("index_path[position_small]="+index_path[position_small]);
					CatalogBean catalogBean = smallIndex.getmList().get(index_path[position_small+1]);
//					//System.out.println("position_small="+position_small);
					if(catalogBean.type_msg == CatalogBean.SECTION){
						//System.out.println("在isLeafNode中，判断出catalogBean.type_msg == CatalogBean.SECTION");
						return true;
					}
					for(int i = position_small+2;i<=index_path[0];i++ ){
						catalogBean = catalogBean.catalogList.get(index_path[i]);
//						//System.out.println("在isLeafNode中， i="+i+",catalogBean.type_msg="+catalogBean.type_msg+", CatalogBean.SECTION="+ CatalogBean.SECTION);
						if(catalogBean.type_msg == CatalogBean.SECTION){
//							//System.out.println("在isLeafNode中， 找到叶子");
							return true;
						}
					}
				}else{
					//如果小目录级别大于等于4
//					//System.out.println("目录级别大于等于4");
					if(index_path[0] == position_small){
						if(smallIndex.getmList().size() == 1){
							//根目录是sectionList
							return true;
						}else{
							return false;
						}
					}
					CatalogBean catalogBean = smallIndex.getmList().get(index_path[position_small+1]);
					if(catalogBean.type_msg == CatalogBean.SECTION){
//						//System.out.println("在isLeafNode中，判断出catalogBean.type_msg == CatalogBean.SECTION");
						return true;
					}
					for(int i = position_small+2;i<=index_path[0];i++ ){
						catalogBean = catalogBean.catalogList.get(index_path[i]);
//						//System.out.println("在isLeafNode中， i="+i+",catalogBean.type_msg="+catalogBean.type_msg+", CatalogBean.SECTION="+ CatalogBean.SECTION);
						if(catalogBean.type_msg == CatalogBean.SECTION){
//							//System.out.println("在isLeafNode中， 找到叶子");
							return true;
						}
					}
					
				}
			}else{
				//System.out.println("在isLeafNode中，判断出没有使用了小目录index_path[2] = "+index_path[2]+",index_path[0]="+index_path[0] );
				if(index_path[2] == 0 ){
					//如果没有用小目录
					//科目根目录
					CatalogBean catalogBean = bigIndex.getmList().get(index_path[2]);
					//System.out.println("CatalogBean.SECTION="+CatalogBean.SECTION);
					//System.out.println("CatalogBean.PACKAGE="+CatalogBean.PACKAGE);
					//System.out.println("CatalogBean.CATALOG="+CatalogBean.CATALOG);
					for(int i=3;i<=index_path[0];i++ ){
						//System.out.println("i="+i+"   catalogBean.type_msg="+catalogBean.type_msg);
						catalogBean = catalogBean.catalogList.get(index_path[i]);
						if(catalogBean.type_msg == CatalogBean.SECTION){
							//System.out.println("在isLeafNode中，atalogBean.type_msg == CatalogBean.SECTI");
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	
	/**
	 * 函数作用：点击中间的菜单 
	 * void
	 * @param index
	 */
	public static void touchCenterMenu(final int index){
		type_touch = 3;
		//菜单多出一环
		index_path[0]++;
		index_path[index_path[0]] = index;
		if(index_path[2] == bigIndex.getmList().size()){
			//System.out.println("使用了独立package，不加载小目录");
			handleCenterMenu(index);
			return ;
		}
		if(index_path[2] != 0){
			// 不是同步学习
			//是否加载小目录
			boolean isLoadSmall = false;
			CatalogBean bean = bigIndex.getmList().get(index_path[2]);
			for(int i = 3; i<= index_path[0] ; i++){
				ArrayList<PackageBean> list_package = bean.packageList;
				if( list_package == null ){
					bean = bean.catalogList.get(index_path[i]);
				}else{
					if(i != index_path[0]){
						break;
					}
					//System.out.println("加载小目录");
					isLoadSmall = true;
					//要加载小目录了
					RequestParameter parameter = new RequestParameter();
					String path_small  = list_package.get(index).path;
					//System.out.println("touchCenterMenu中，加载小目录的路径："+path_small);
					parameter.add("path", path_small );
					System.out.println("按中间目录，加载小目录");
				    LoadData.loadData(Constant.BOOK_DATA, parameter, new RequestListener() {
						public void onError(String error) {
							
						}
						public void onComplete(Object obj) {
							position_small = index_path[0];
							smallIndex = (BookData) obj;
							smallIndex.filterContent(list_content_small);
//							System.out.println("按中间目录，加载小目录完毕,obj="+obj+",smallIndex="+smallIndex);
//							System.out.println("……………………………………………………………………：");
							handleCenterMenu(index);
						}
				   }); 
				}
				if(isLoadSmall){
					break;
				}
			}
			if(!isLoadSmall){
				//System.out.println("不加载小目录");
				handleCenterMenu(index);
			}
		}else{
			//System.out.println("不加载小目录");
			handleCenterMenu(index);
		}
	}
	
	
	/**
	 * 函数作用： 辅助touchCenterMenu
	 * void
	 * @param index
	 */
	public static void handleCenterMenu(int index){
		//System.out.println("在handleCenterMenu中。。index_path[0]="+index_path[0]);
		//如果获取独立package数据
		if(index_path[2] == bigIndex.getmList().size()){
			//System.out.println("在handleCenterMenu中。判断出获取独立package数据");
			String temp_title = findPathTitle(index_path[0], index, false);
			string_path[index_path[0]] = temp_title;
			//System.out.println("在handleCenterMenu中。temp_title="+temp_title);
		}else{
			//System.out.println("在handleCenterMenu中。判断出没有获取独立package数据");
			CatalogBean bean = bigIndex.getmList().get(index_path[2]);
			boolean isPackage = false;
			for(int i = 3; i<= index_path[0] ; i++){
				//System.out.println("在handleCenterMenu中。i="+i+", ");
				ArrayList<PackageBean> list_package = bean.packageList;
				if( list_package == null ){
					//System.out.println("在handleCenterMenu中。  packageList为空 ");
					bean = bean.catalogList.get(index_path[i]);
				}else{
					if(i!=index_path[0]){
						break;
					}
					//System.out.println("在handleCenterMenu中。  packageList不为空 ");
					string_path[index_path[0]] = list_package.get(index).title;
					isPackage = true;
				}
			}
			if(!isPackage){
				//如果获取其他
				String temp_title = findPathTitle(index_path[0], index, true);
				string_path[index_path[0]] = temp_title;
				//System.out.println("获取到标题是："+temp_title);
			}
		}
		//左边菜单改变
		icon_menu_left = icon_menu_center;
		name_menu_left = name_menu_center;
		//中间菜单改变
		changeCenter(index);
		//处理界面
		menuView.setData();
		menuView.setShow();
	}
	
	/**
     * 将flash路径进行编码，flash文件路径示例：/mnt/sdcard/精讲_山行.swf
     * @param filePath：flash文件的路径
     * @return：经过编码的flash文件路径
     * @throws UnsupportedEncodingException
     */
    public static String encodeFlashFilePath(String filePath) throws UnsupportedEncodingException{
    	StringBuffer stringBuffer = new StringBuffer();
    	String nodes[] = filePath.split("/");
    	for (int i=1; i<nodes.length; i++){
    		stringBuffer.append("/");
    		String temp = URLEncoder.encode(nodes[i], "utf-8");
    		temp = temp.replace("+", "%20");
    		stringBuffer.append(temp);
    	}
    	return stringBuffer.toString();
    }

	
	/**
	 * 函数作用：触摸上方路径按钮
	 * void
	 * @param index
	 */
	public static void touchPathMenu(final int index){
		type_touch = 1;
		//首先改变路径数组
		index_path[0] = index;
		if( index == position_small && index_path[2]!=0  ){
			if(index_path[2] == bigIndex.getmList().size() ){
				handlePathMenu(index);
				return ;
			}
			//如果是点击了级别目录//要加载小目录了
			RequestParameter parameter = new RequestParameter();
			String path_load = null;
			CatalogBean bean = bigIndex.getmList().get(index_path[2]);
			for(int i= 3 ;i <= index ; i++){
				ArrayList<PackageBean> pbs = bean.packageList;
				if(pbs == null){
					//catalogs
					bean = bean.catalogList.get(index_path[i]); 
				}else{
					if(i != index ){
						break;
					}
					path_load = pbs.get(index_path[index]).path;
				}
			}
			//System.out.println("path_load="+path_load);
		    parameter.add("path",path_load );
		    LoadData.loadData(Constant.BOOK_DATA, parameter, new RequestListener() {
				public void onError(String error) {
					
				}
				public void onComplete(Object obj) {
					smallIndex = (BookData) obj;
					smallIndex.filterContent(list_content_small);
					handlePathMenu(index);
				}
		   }); 
		}else if(index == 2){
			//当点击根目录时
			if( index_path[2] == bigIndex.getmList().size()){
				//System.out.println("在处理路径点击事件时，点击了独立package目录 ");
				//点击了独立package
				RequestParameter parameter = new RequestParameter();
				//System.out.println("path_load="+bigIndex.getPackageBean().path);
			    parameter.add("path", bigIndex.getPackageBean().path );
			    LoadData.loadData(Constant.BOOK_DATA, parameter, new RequestListener() {
					public void onError(String error) {
						
					}
					public void onComplete(Object obj) {
//						position_small = 2;
						smallIndex = (BookData) obj;
						smallIndex.filterContent(list_content_small);
						handlePathMenu(index);
					}
			   }); 
			} else{
				handlePathMenu(index);
			}
		} else{
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			handlePathMenu(index);
		}
		
	  
	}
	
	/**
	 * 函数作用：辅助touchPathMenu 函数
	 * void
	 * @param index
	 */
	private static void handlePathMenu(int index) {
		//中间图标和数组改变
		name_menu_center = new ArrayList<String>();
		icon_menu_center = new ArrayList<String>();
		if(index == 2){
			if(index_path[index] == 0){
				//触摸同步学习
				ArrayList<CatalogBean> beans = getCatalogList(index+1, true,false);
				int size_content = beans.size();
				for(int i=0;i<size_content;i++){
					String title = beans.get(i).title;
					name_menu_center.add(title);
					icon_menu_center.add(beans.get(i).icon);
				}
			}else{
				if(index_path[2] == bigIndex.getmList().size()){
					//若点击了独立package
					ArrayList<CatalogBean> beans = smallIndex.getmList();
					int size = beans.size();
					for(int i=0;i<size;i++){
						CatalogBean bean = beans.get(i);
						name_menu_center.add(bean.title);
						icon_menu_center.add(bean.icon);
					}
				}else{
					if(bigIndex.getmList().get(index_path[2]).packageList == null){
						//不是package
						ArrayList<CatalogBean> beans = getCatalogList(index+1,true,false);
						int size_content = beans.size();
						for(int i=0;i<size_content;i++){
							String title = beans.get(i).title;
							name_menu_center.add(title);
							icon_menu_center.add(icon_packcage);
						}
					}else{
						//如果中间内容是PACKAGE类型
						ArrayList<PackageBean> beans = getPackageList(index+1);
						int size_content = beans.size();
						for(int i=0;i<size_content;i++){
							name_menu_center.add(beans.get(i).title);
							icon_menu_center.add(icon_packcage);
						}
					}
				}
			}
		}else{
			boolean isp = false;
			if(index_path[2] == bigIndex.getmList().size()){
				//System.out.println("在处理路径目录中。。 处理独立PACKA");
				//若点击了独立package
				index_level_current_recycle = 3;
				ArrayList<CatalogBean> beans = getCatalogList(index+1 , false, true);
				int size = beans.size(); 
				for(int i=0;i<size;i++){
					CatalogBean bean = beans.get(i);
					String title = bean.title;
					name_menu_center.add(title);
					icon_menu_center.add(bean.icon);
				}
			}else{
				CatalogBean bean = bigIndex.getmList().get(index_path[2]);
				for(int i= 3 ;i <= index+1 ; i++){
					ArrayList<PackageBean> pbs = bean.packageList;
					if(pbs == null){
						//catalogs
						if(bean.catalogList != null){
							bean = bean.catalogList.get(index_path[i]);
						}else{
							bean = bean.sectionList.get(index_path[i]);
						}
					}else{
						if(i != index+1 ){
							break;
						}
						isp  = true;
						int size = pbs.size();
						for(int j=0;j<size;j++){
							name_menu_center.add(pbs.get(j).title);
							icon_menu_center.add(icon_packcage);
						}
					}
				}
				if(!isp){
					ArrayList<CatalogBean> beans = getCatalogList(index+1, true,false);
					int size_content = beans.size();
					for(int i=0;i<size_content;i++){
						String title = beans.get(i).title;
						name_menu_center.add(title);
						icon_menu_center.add(beans.get(i).icon);
					}
				}
			}
		}

		name_menu_left = new ArrayList<String>();
		icon_menu_left = new ArrayList<String>();
		//左边菜单改变
		if(index == 3){
			//如果为第三级
			if(index_path[2] == 0){
				//触摸同步学习
				ArrayList<CatalogBean> beans = getCatalogList(index, true,false);
				int size_content = beans.size();
				for(int i=0;i<size_content;i++){
					name_menu_left.add(beans.get(i).title);
					icon_menu_left.add(beans.get(i).icon);
				}
			}else{
				if(index_path[2] == bigIndex.getmList().size()){
					//System.out.println("在处理路径目录中。。 处理独立PACKA LEFT");
					//若点击了独立package
					index_level_current_recycle = 3;
					ArrayList<CatalogBean> beans = getCatalogList(index , false, true);
					int size = beans.size(); 
					for(int i=0;i<size;i++){
						CatalogBean bean = beans.get(i);
						name_menu_left.add(bean.title);
						icon_menu_left.add(bean.icon);
					}
				}else{
				
					boolean isp = false;
					CatalogBean bean = bigIndex.getmList().get(index_path[2]);
					for(int i= 3 ;i <= index ; i++){
						ArrayList<PackageBean> pbs = bean.packageList;
						if(pbs == null){
							//catalogs
							bean = bean.catalogList.get(index_path[i]); 
						}else{
							if(i != index ){
								break;
							}
							isp  = true;
						}
					}
					if(isp){
						//有级别将变成级别
						ArrayList<PackageBean> beans = getPackageList(index);
						int size_content = beans.size();
						for(int i=0;i<size_content;i++){
							name_menu_left.add(beans.get(i).title);
							icon_menu_left.add(icon_packcage);
						}
					}else{ 
						ArrayList<CatalogBean> beans = getCatalogList(index, true,false);
						int size_content = beans.size();
						for(int i=0;i<size_content;i++){
							name_menu_left.add(beans.get(i).title);
							icon_menu_left.add(beans.get(i).icon);
						}
					}
				}
			}
		}else if(index == 2){
			//System.out.println("在handlePathMenu中。。。index=2");
			//若为根目录
			PackageBean bean_package = bigIndex.getPackageBean();
			if(bean_package != null){
				//若有独立package
				ArrayList<CatalogBean> beans = bigIndex.getmList();
				int size = beans.size();
				for(int i=0;i<size;i++){
					name_menu_left.add(beans.get(i).title);
					icon_menu_left.add(beans.get(i).icon);
				}
				name_menu_left.add(bean_package.title);
				icon_menu_left.add(beans.get(0).icon);
			}else{
				//System.out.println("没有独立package");
				ArrayList<CatalogBean> beans = getCatalogList(index, true,false);
				int size_content = beans.size();
				for(int i=0;i<size_content;i++){
					name_menu_left.add(beans.get(i).title);
					icon_menu_left.add(beans.get(i).icon);
				}
			}
		} else{
			//如果为其他级
			if(index_path[2] == bigIndex.getmList().size()){
				//触摸独立package
				index_level_current_recycle = 3;
				ArrayList<CatalogBean> beans = getCatalogList(index, false,true);
				int size = beans.size();
				for(int i=0;i<size;i++){
					name_menu_left.add(beans.get(i).title);
					icon_menu_left.add(beans.get(i).icon);
				}
			}else{
				boolean isp = false;
				CatalogBean bean = bigIndex.getmList().get(index_path[2]);
				for(int i= 3 ;i <= index ; i++){
					ArrayList<PackageBean> pbs = bean.packageList;
					if(pbs == null){
						//catalogs
						bean = bean.catalogList.get(index_path[i]); 
					}else{
						if(i != index ){
							break;
						}
						isp  = true;
						int size_package = pbs.size();
						for(int j=0;j<size_package;j++){
							name_menu_left.add(pbs.get(j).title);
							icon_menu_left.add(icon_packcage);
						}
					}
				}
				if(! isp){
					ArrayList<CatalogBean> beans = getCatalogList(index, true,false);
					int size_content = beans.size();
					for(int i=0;i<size_content;i++){
						name_menu_left.add(beans.get(i).title);
						icon_menu_left.add(beans.get(i).icon);
					}
				}
			}
		}
		//处理界面
		menuView.setData();
		menuView.setShow();
	}
	
	
	/**
	 * 函数作用：设置科目，及根目录
	 * void
	 * @param major
	 */
	public static void setMajorRoot(final int major,final int index){
		//上方路径
		index_path[0] = 2;
		index_path[1] = major;
		switch(major){
		case Constant.MAJOR_CHINESE:
			string_path[1] = "语文";
			break;
		case Constant.MAJOR_MATH:
			string_path[1] = "数学";
			break;
		case Constant.MAJOR_ENGLISH:
			string_path[1] = "英语";
			break;
		}
		index_path[2] = index;
		ArrayList<CatalogBean> list_catalog = bigIndex.getmList();
		int size = list_catalog.size();
		PackageBean bean_package = bigIndex.getPackageBean();
		boolean isChangeLeft = false;
		boolean isChangePath = false;
		boolean isChangeCenter = false;
		if(bean_package != null){
			isChangeLeft = true;
			if(size == index){	//如果正好是独立Package数据
				isChangePath = true;
				isChangeCenter = true; 
				position_small = 2;
				//System.out.println("isChangeCenter = true; ");
			} 
		}
		if(isChangePath){
			//科目根目录
			string_path[2] = bean_package.title;
		}else {
			//科目根目录
			string_path[2] = list_catalog.get(index).title;
		}
		
		//左边菜单
		ArrayList<String> name_left = new ArrayList<String>();
		ArrayList<String> icon_left = new ArrayList<String>();
		CatalogBean bean = null;
		for(int i=0;i<size;i++){
			bean = list_catalog.get(i);
			name_left.add(bean.title);
			icon_left.add(bean.icon);
		}
		if(isChangeLeft){
			name_left.add(bean_package.title);
			icon_left.add(list_catalog.get(0).icon);
		}
		setIcon_menu_left(icon_left);
		setName_menu_left(name_left);
		//中间菜单
		final ArrayList<String> name_center = new ArrayList<String>();
		final ArrayList<String> icon_center = new ArrayList<String>();
		if(isChangeCenter ){
			if(smallIndex == null){
				PackageBean packageBean = bigIndex.getPackageBean();
				//要加载小目录了
				RequestParameter parameter = new RequestParameter();
				//System.out.println("path_load="+packageBean.path );
			    parameter.add("path", packageBean.path );
			    LoadData.loadData(Constant.BOOK_DATA, parameter, new RequestListener() {
					public void onError(String error) {
						
					}
					public void onComplete(Object obj) {
						smallIndex = (BookData) obj;
						smallIndex.filterContent(list_content_small);
//						position_small = 3;
						ArrayList<CatalogBean> center_list = smallIndex.getmList();
						int size_center = center_list.size();
						for(int i=0;i<size_center;i++){
							name_center.add(center_list.get(i).title);
							icon_center.add(center_list.get(i).icon);
						}
						//System.out.println("setMajorRoot  设置中间目录");
						setIcon_menu_center(icon_center);
						setName_menu_center(name_center);
						//跳转到目录界面
				  		Message message = new Message();
						message.what =  Constant.VIEW_MENU;
						message.arg1 = major; 
						KingPadStudyActivity.handler.sendMessage(message);
					}
			   }); 
			}else{
				ArrayList<CatalogBean> center_list =smallIndex.getmList();
				int size_center = center_list.size();
				for(int i=0;i<size_center;i++){
					name_center.add(center_list.get(i).title);
					icon_center.add(center_list.get(i).icon);
				}
				setIcon_menu_center(icon_center);
				setName_menu_center(name_center);
				//跳转到目录界面
		  		Message message = new Message();
				message.what =  Constant.VIEW_MENU;
				message.arg1 = major; 
				KingPadStudyActivity.handler.sendMessage(message);
			}
		}else{
			CatalogBean centerBean = list_catalog.get(index);
			if(centerBean.type_msg == CatalogBean.CATALOG){
				ArrayList<CatalogBean> center_list =centerBean.catalogList;
				int size_center = center_list.size();
				for(int i=0;i<size_center;i++){
					name_center.add(center_list.get(i).title);
					icon_center.add(center_list.get(i).icon);
				}
			}else{
				//PACKAGE
				ArrayList<PackageBean> center_list =centerBean.packageList;
				int size_center = center_list.size();
				for(int i=0;i<size_center;i++){
					name_center.add(center_list.get(i).title);
					icon_center.add(icon_packcage);
				}
			}
			setIcon_menu_center(icon_center);
			setName_menu_center(name_center);
			//跳转到目录界面
	  		Message message = new Message();
			message.what =  Constant.VIEW_MENU;
			message.arg1 = major; 
			KingPadStudyActivity.handler.sendMessage(message);
		}
	}

	/**
	 * 函数作用：改变中间内容     在点击左边或者中间菜单的时候改变。
	 * void
	 */
	private static void changeCenter(int index) {
		name_menu_center = new ArrayList<String>();
		icon_menu_center = new ArrayList<String>(); 
		if(index_path[2] == bigIndex.getmList().size() ){
			//System.out.println("在changeCenter中。判断出获取独立package数据");
			//如果是独立package
			index_level_current_recycle = 3;
			ArrayList<CatalogBean> beans = getCatalogList(index_path[0]+1, false,true);
			int size_content = beans.size();
			for(int i=0;i<size_content;i++){  
				//System.out.println("在changeCenter中。 i="+i+",title="+beans.get(i).title);
				name_menu_center.add(beans.get(i).title);	
				icon_menu_center.add(beans.get(i).icon);	
			}
		}else{
			//System.out.println("在changeCenter中。判断出没有使用独立package数据");
			if(index_path[0] == 2){
				//System.out.println("在changeCenter中。index_path[0] == 2");
				if(index == 0){
					//触摸同步学习
					ArrayList<CatalogBean> beans = getCatalogList(index_path[0]+1, true,false);
					int size_content = beans.size();
					for(int i=0;i<size_content;i++){
						String title = beans.get(i).title;
						name_menu_center.add(title);
						icon_menu_center.add(beans.get(i).icon);
					}
				}else{
					//如果中间内容是PACKAGE类型
					ArrayList<PackageBean> beans = getPackageList(index_path[0]+1);
					int size_content = beans.size();
					for(int i=0;i<size_content;i++){
						name_menu_center.add(beans.get(i).title);
						icon_menu_center.add(icon_packcage);
					}
				}
			}else{
				//System.out.println("在changeCenter中。index_path[0] != 2");
				CatalogBean bean = bigIndex.getmList().get(index_path[2]);
				PackageBean bean_package = null;
				boolean isPackage = false;
				boolean isUsingPackage = false;
				//寻找当前级别的下一级的目录
				int size_circle = 0;
				if(index_path[2] == 0 ){
					size_circle = index_path[0];
				}else{
					size_circle = index_path[0]+1;
				}
				for(int i = 3; i<= size_circle ; i++){
					//System.out.println("在changeCenter中。i="+i);
					ArrayList<PackageBean> list_package = bean.packageList;
					if( list_package == null ){
						bean = bean.catalogList.get(index_path[i]);
					}else{
						if(i!=size_circle){
							//不是最后一级
							break;
						}
						//如果是最后一级，那么以package的目录为准
						//System.out.println("在changeCenter中。是最后一级");
						int size = list_package.size();
						for(int j=0;j<size;j++){
							String title = list_package.get(j).title;
							name_menu_center.add(title);
							icon_menu_center.add(icon_packcage);
						}
						isPackage = true;
					}
				}
				if(!isPackage){
					boolean isSectionRoot = false;
					//判断是否要使用根目录是否是sectionList的级别
					if(isUsingSmallIndex()){
						if(index_path[0] == position_small && smallIndex.getmList().size() == 1){
							isSectionRoot = true;
							ArrayList<CatalogBean> beans = smallIndex.getmList().get(0).sectionList;
							int size_content = beans.size();
							for(int i=0;i<size_content;i++){
								String title = beans.get(i).title;
								name_menu_center.add(title);
								icon_menu_center.add(beans.get(i).icon);
							}
						}
					}
					if(!isSectionRoot){
						ArrayList<CatalogBean> beans = getCatalogList(index_path[0]+1, true,false);
						int size_content = beans.size();
						for(int i=0;i<size_content;i++){
							String title = beans.get(i).title;
							name_menu_center.add(beans.get(i).title);
							icon_menu_center.add(beans.get(i).icon);
						}
					}
				}
			}
		}
	}
 
	
	/**
	 * 函数作用：寻找到某一级的某个下标的路径标题
	 * Object
	 * @param i
	 * @param index
	 * @return
	 */
	public static String findPathTitle(int level, int index,boolean isBigIndex) {
		if(index_path[2] == bigIndex.getmList().size()){
			//如果是独立package
			if(level == 2){
				return bigIndex.getPackageBean().title;
			}else{
				ArrayList<CatalogBean> catalogs = null;
				catalogs = smallIndex.getmList();
				//System.out.println("独立package的findPathTitle中。index_level_current_recycle="+index_level_current_recycle);
				index_level_current_recycle = 3;
				return getPathTitle(level,index,catalogs.get(index_path[index_level_current_recycle]));
			}
		}else{
			//首先获取大目录的 CatalogBean 列表
			ArrayList<CatalogBean> catalogs = null;
			if(isBigIndex){
				catalogs = bigIndex.getmList();
			}else{
				catalogs = smallIndex.getmList();
			}
			//然后寻找到对应的标题
			//System.out.println("在 findPathTitle中。。。index_level_current_recycle="+index_level_current_recycle);
			return getPathTitle(level,index,catalogs.get(index_path[index_level_current_recycle]));
		}
	}

	/**
	 * 函数作用：递归寻找某级目录下的某一个下标的标题
	 * String
	 * @param i
	 * @param index
	 * @param catalogBean
	 * @return
	 */
	private static String getPathTitle(int level, int index, CatalogBean catalogBean) {
		if(index_level_current_recycle == level){
			//如果已经找到出口———到了要找的目录级别时, 返回对应的标题字符串
			index_level_current_recycle = 2;
			return catalogBean.title;
		}
		if(catalogBean.type_msg == catalogBean.PACKAGE){
			//System.out.println("下一个级别就是package了。");
			if(level == position_small){
				//获取级数
				return getPackageTitle(index,catalogBean);
			}
			//有扩展目录, 那么调用小目录的寻找路径标题函数
			//System.out.println("有扩展目录, 那么调用小目录的寻找路径标题函数。");
			index_level_current_recycle += 2;
			return findPathTitle(level,index,false); 
		}
		if(index_level_current_recycle == level-1){
			index_level_current_recycle ++;
			if(catalogBean.type_msg == catalogBean.CATALOG){
				if(catalogBean.catalogList.size() <= index ){
					index_level_current_recycle = 2;
					return null;
				}
				return getPathTitle(level, index, catalogBean.catalogList.get(index));
			}else if(catalogBean.type_msg == catalogBean.SECTION){
				if(catalogBean.sectionList.size() <= index ){
					index_level_current_recycle = 2;
					return null;
				}
				return getPathTitle(level, index, catalogBean.sectionList.get(index));
			}else{
				index_level_current_recycle = 2;
				return null;
			}
		}else{
			int temp_index = index_path[++index_level_current_recycle];
			if( catalogBean.catalogList != null){
				if(temp_index  >= catalogBean.catalogList.size()){
					index_level_current_recycle = 2;
					return null;
				}else{
					return getPathTitle(level, index, catalogBean.catalogList.get(temp_index));
				}
			}
				 
			if(catalogBean.sectionList != null ){
				//System.out.println("^");
				//System.out.println("^");
				//System.out.println("^");
				//System.out.println("**********************点击了最后一个目录************");
				//System.out.println("^");
				//System.out.println("^");
				//System.out.println("^");
				if(temp_index  >= catalogBean.sectionList.size()){
					index_level_current_recycle = 2;
					return null;
				}else{
					return getPathTitle(level, index, catalogBean.sectionList.get(temp_index));
				}
			}
			
			if(catalogBean.sectionList == null && catalogBean.catalogList == null){
				index_level_current_recycle = 2;
				return null;
			}
			return null;
		}
	}
	
	/**
	 * 函数作用：获取package级数
	 * String
	 * @param index
	 * @return
	 */
	private static String getPackageTitle(int index,CatalogBean bean) {
		return bean.packageList.get(index).title;
	}
	
	
	/**
	 * 函数作用：获取package级数字符串
	 * String
	 * @param index
	 * @return
	 */
	private static String getPackageTitle(int index){
		//找到对应的CatalogBean
		CatalogBean bean = getBigIndex().getmList().get(index_path[2]);
		return bean.packageList.get(index).title;
	}
	

	/**
	 * 函数作用：获取小目录的根目录
	 * ArrayList<PackageBean>
	 * @param level
	 * @return
	 */
	public static ArrayList<PackageBean> getPackageList(int level){
		//System.out.println("getPackageList中。。 level="+level);
		if(level != 3){
			return null;
		}
		return bigIndex.getmList().get(index_path[2]).packageList;
	}
	
	
	/**
	 * 函数作用：获取科目的某个背景图片
	 * String
	 * @param major
	 * @param index
	 * @return
	 */
	public static String getMajorBackImageUrl(int index){
		if(index == bigIndex.getmList().size()){ 
			//System.out.println("独立package设置背景");
			return bigIndex.getmList().get(0).bg;
		}else{
			return bigIndex.getmList().get(index).bg;
		}
	}
	
	
	
	
	/**
	 * 函数作用：获取某级目录的 目录对象列表，当不获取小目录根目录时
	 * ArrayList<String>
	 * @param level
	 * @return
	 */
	public static ArrayList<CatalogBean> getCatalogList(int level,boolean isBigIndex,boolean isConsole){
		ArrayList<CatalogBean> catalogs = null;
		if(isBigIndex){
			if(!isConsole){
				index_level_current_recycle = 2;
			}
			catalogs = bigIndex.getmList();
		}else{
			//System.out.println("smallIndex ="+smallIndex);
			if(smallIndex == null){
				return null;
			}
			catalogs = smallIndex.getmList();
		}
//		index_level_current_recycle = 2;
		return getCatalogListRecycle(level,catalogs);
	}
	
	/**
	 * 函数作用：递归获取第level级的目录列表
	 * ArrayList<CatalogBean>
	 * @param level 级别数
	 * @param catalogs 当前目录列表
	 * @return
	 */
	private static ArrayList<CatalogBean> getCatalogListRecycle(int level,
			ArrayList<CatalogBean> catalogs) {
		//System.out.println("在getCatalogListRecycle中。。level="+level+",catalogs="+catalogs+",index_level_current_recycle="+index_level_current_recycle);
		if(index_level_current_recycle == level){
			index_level_current_recycle = 2;
			return catalogs;
		}
		//获取当前递归级别的CatalogBean
		int temp_index = index_path[index_level_current_recycle];
		//如果当前级别目录超出范围，那么返回空
		if(temp_index >= catalogs.size()){
			index_level_current_recycle = 2;
			//System.out.println("当前级别目录超出范围");
			return null;
		}
		CatalogBean catalogBean = catalogs.get(temp_index);
		//如果当前目录的子标签为package
		if(catalogBean.type_msg == CatalogBean.PACKAGE){
			//System.out.println("循环过程。。。。。。找到PACKAGE。。。。。。。。");
			if(level == position_small){
				//System.out.println("循环过程。。。。。。level == 3");
				//当选择小目录的根目录时，返回空
				//System.out.println("选择小目录的根目录时");
				return null;
			}
			//System.out.println("循环过程。。。。。。level != 3，找小目录");
			index_level_current_recycle +=2 ;
			return getCatalogList(level, false,false);
		}else if(catalogBean.type_msg == CatalogBean.SECTION){
			//当为最后的目录时，
			isLastLevel = true;
			index_level_current_recycle ++;
			return getCatalogListRecycle(level,catalogBean.sectionList);
		}else{
			index_level_current_recycle ++;
			return getCatalogListRecycle(level,catalogBean.catalogList);
		}
	}

	/**
	 * 函数作用：根据路径获取index信息   大目录
	 * BookData
	 * @param path
	 * @param type   
	 * @return
	 */
	public static void loadBigBookData(final Context context,String path,String path_root,final int major){
		//System.out.println("开始解析时，index_level_current_recycle="+index_level_current_recycle);
		RequestParameter parameter = new RequestParameter(); 
        parameter.add("path", path); 
        //System.out.println("path_root="+path_root);
        parameter.add("packagePath", path_root);
        LoadData.loadData(Constant.BOOK_DATA, parameter, new RequestListener() {
			public void onError(String error) {
				Toast.makeText(context , "异常："+error, 0).show();
			}
			public void onComplete(Object obj) {
				//System.out.println("b解析完毕…………………………………………………………");
				bigIndex = (BookData) obj;
				bigIndex.filterContent(list_content_big);
				ViewController.dismissMajorViewDialog();
			}
       });
	}
	
	
	 
	 
	/**
	 * 函数作用：获取当前要使用的资源
	 * void
	 * @param path
	 * @param type
	 */
	public static void loadMetaData(final String path,final String type,final String mode){
		//System.out.println("进入loadMetaData...path="+path+",type="+type);
		if(type.equals(Constant.RESOURCE_TYPE_Flash)|| 
				type.equals(Constant.RESOURCE_TYPE_Chinese_Flash_YW) || 
				type.equals(Constant.RESOURCE_TYPE_InteractiveFlash)){
			//System.out.println("META_DATA.........");
			RequestParameter parameter = new RequestParameter();
	        parameter.add("path", path);
	        parameter.add("type", type);
	        LoadData.loadData(Constant.META_UTIL_DATA, parameter, new RequestListener() {
				public void onError(String error) {
					
				}
				public void onComplete(Object obj) {
					MetaUtilData data = (MetaUtilData) obj;
					currentUtilData = data;
					MetaUtil.printData(data.getData());
					
					
					//播放资源
					playResource(path,type,mode);
				}
	         });
		}else{
			//System.out.println("META_UTIL_DATA.........");
			  //解析index记录
		      RequestParameter parameter = new RequestParameter();
		      parameter.add("type", type); 
		      parameter.add("path", path); 
		      LoadData.loadData(Constant.META_UTIL_DATA, parameter, new RequestListener() {
					public void onError(String error) {
					}
					public void onComplete(Object obj) {
						MetaUtilData data = (MetaUtilData) obj;
						currentUtilData = data;
						MetaUtil.printData(data.getData());
						//播放资源
						playResource(path,type,mode);
					}
		     });
		}
	}
	
	
	/**
	 * 函数作用：根据类型播放学习资源
	 * void
	 */
	public static void playResource(String path,String type,String mode){
		//System.out.println("playResource...");
		if(type.equals(Constant.RESOURCE_TYPE_Chinese_Flash_YW)){
			//语文FLASH
			System.out.println("播放资源："+Constant.RESOURCE_TYPE_Chinese_Flash_YW);
			playFlash(Constant.TYPE_FLASH_YW);
		}
		else if(type.equals( Constant.RESOURCE_TYPE_English_Role)){
			System.out.println("播放资源："+Constant.RESOURCE_TYPE_English_Role);
			playFlash(Constant.TYPE_FLASH_ROLE);
		}
		else if(type.equals( Constant.RESOURCE_TYPE_English_Speaking)){
			System.out.println("播放资源："+Constant.RESOURCE_TYPE_English_Speaking);
			playFlash(Constant.TYPE_FLASH_Reading);
		} 
		else if(type.equals( Constant.RESOURCE_TYPE_Flash)){
			System.out.println("播放资源："+Constant.RESOURCE_TYPE_Flash);
			//普通FLASH
			playFlash(Constant.TYPE_FLASH_NORMAL);
		}
		else if(type.equals( Constant.RESOURCE_TYPE_InteractiveFlash)){
			System.out.println("播放资源："+Constant.RESOURCE_TYPE_InteractiveFlash);
			//互动FLASH
			playFlash(Constant.TYPE_FLASH_HUDONG);
		}
		else if(type.equals( Constant.RESOURCE_TYPE_Chinese_Read)){
			//System.out.println("播放资源："+Constant.RESOURCE_TYPE_Chinese_Read);
			//录音FLASH
			showRecordFlash();
		}
		else if(type.equals( Constant.RESOURCE_TYPE_Print)){
			//System.out.println("播放资源："+Constant.RESOURCE_TYPE_Print);
			//HTML
			ViewController.setDuyinResource(path, type);
			ViewController.controllView(Constant.VIEW_PRINT);
		}
		else if(type.equals( Constant.RESOURCE_TYPE_SubjectiveQuiz)){
			//System.out.println("播放资源："+Constant.RESOURCE_TYPE_SubjectiveQuiz);
			ViewController.setDuyinResource(path, type);
			ViewController.setMode(mode);
			ViewController.controllView(Constant.VIEW_SUBJECTIVE_QUIZ);
		}
		else if(type.equals( Constant.RESOURCE_TYPE_Chinese_Memorization)){
			//System.out.println("播放资源："+Constant.RESOURCE_TYPE_Chinese_Memorization);
			//跟读
			ViewController.setDuyinResource(path, type);
			ViewController.controllView(Constant.VIEW_Chinese_Memorization);
		}
		else if(type.equals( Constant.RESOURCE_TYPE_ObjectiveQuiz)){
			//选择
			//System.out.println("播放资源："+Constant.RESOURCE_TYPE_ObjectiveQuiz);
			ViewController.setDuyinResource(path, type);
			ViewController.controllView(Constant.VIEW_OBJECTIVEQUIZ); 
		}
		else if(type.equals( Constant.RESOURCE_TYPE_Chinese_Recite)){
			//System.out.println("播放资源："+Constant.RESOURCE_TYPE_Chinese_Recite);
			//背诵
			ViewController.setDuyinResource(path, type);
			ViewController.controllView(Constant.VIEW_POEM_RECITE); 
		}
		else if(type.equals( Constant.RESOURCE_TYPE_Chinese_SubjectiveQuiz_ShiZi)){
			//System.out.println("播放资源："+Constant.RESOURCE_TYPE_Chinese_SubjectiveQuiz_ShiZi);
			ViewController.setDuyinResource(path,type);
			ViewController.controllView(Constant.VIEW_SHIZI);
		} 
		else if(type.equals( Constant.RESOURCE_TYPE_English_SubjectiveQuiz)){
			//System.out.println("播放资源："+Constant.RESOURCE_TYPE_English_SubjectiveQuiz);
			ViewController.setDuyinResource(path,type);
			ViewController.setMode(mode);
			ViewController.controllView(Constant.VIEW_ENGLISH_SUB);
		} 
		else if(type.equals( Constant.RESOURCE_TYPE_English_DictationQuiz)){
			//System.out.println("播放资源："+Constant.RESOURCE_TYPE_English_SubjectiveQuiz);
			ViewController.setDuyinResource(path,type);
			ViewController.setMode(mode);
			ViewController.controllView(Constant.VIEW_ENGLISH_DictationQuiz);
		} 
		else if(type.equals( Constant.RESOURCE_TYPE_English_Phonetic)){
			//System.out.println("播放资源："+Constant.RESOURCE_TYPE_English_Phonetic);
			ViewController.setDuyinResource(path,type);
			ViewController.setMode(mode);
			ViewController.controllView(Constant.VIEW_ENGLISH_Phonetic);
		} 
		else if(type.equals( Constant.RESOURCE_TYPE_English_Memory)){
			//System.out.println("播放资源："+Constant.RESOURCE_TYPE_English_Memory);
			ViewController.setDuyinResource(path,type);
			ViewController.setMode(mode);
			ViewController.controllView(Constant.VIEW_ENGLISH_Memory);
		} 
	}
	
	
	/**
	 * 函数作用：
	 * void
	 */
	public static void showRecordRecord() {
		 //解析index记录
	      RequestParameter parameter = new RequestParameter();
	      parameter.add("path", path_xml); 
	      LoadData.loadData(Constant.POEM_DATA, parameter, new RequestListener() {
				public void onError(String error) {
				}
				public void onComplete(Object obj) {
					PoemData data = (PoemData) obj;
					String author = "";
					String content = "";
					author = data.getAuthor();
					ArrayList<String> contentList = data.getContentList();
					int size = contentList.size();
					for(int i=0;i<size;i++){
						content+=contentList.get(i)+"\n";
					}
					ViewController.setFlashRecordData(bg_poem,path_mp3,time_record,title,author,content);
					ViewController.controllView(Constant.VIEW_FLASH_LANGDU);
					ViewController.view_record_record.setGuanKanView(ViewController.view_record_flash);
//					ViewController.switchToRecordRecord(bg_poem,path_mp3,time_record,title,author,content);
				}
	     });
		
		
	}
	
	//古诗数据XML
	private static String path_xml;
	private static String path_mp3;
	private static String bg_poem;
	//古诗标题
	private static String title;
	private static int time_record;
	
	/**
	 * 函数作用：显示录音的FLASH界面
	 * void
	 */
	public static void showRecordFlash() {
		String path = "";
		Data data = currentUtilData.getData();
		/*
		 * 找到数据
		 */
		//背景图
		for(int i = 0; i < data.getAttributeCount(); i++) {
			if(data.getAttribueName(i).equals("bg")){
				bg_poem = data.getAttributeValue(i);
				//System.out.println("恭喜！成功获取背景："+bg_poem);
			}else if(data.getAttribueName(i).equals("title")){
				title = data.getAttributeValue(i);
			}
		}
		//资源路径
		ArrayList<Items> itemsList = data.getItemsList();
		for(int i = 0; i < itemsList.size(); i++) {
			Items items = itemsList.get(i);
			ArrayList<Item> itemList = items.getItemList();
			for(int j = 0; j < itemList.size(); j++) {
				Item item = itemList.get(j);
				for(int k  = 0; k < item.getAttributeCount(); k++) {
					if(item.getAttribueName(k).equals("sourceFile")){
						path = item.getAttributeValue(k);
						//System.out.println("恭喜！成功获取SWF路径："+path);
					}else if(item.getAttribueName(k).equals("textFile")){
						path_xml = item.getAttributeValue(k);
						//System.out.println("恭喜！成功获取xml路径："+path_xml);
					}else if(item.getAttribueName(k).equals("musicFile")){
						path_mp3 = item.getAttributeValue(k);
						//System.out.println("恭喜！成功获取mp3路径："+path_mp3); 
					}else if(item.getAttribueName(k).equals("time")){
						time_record = Integer.parseInt(item.getAttributeValue(k));
						//System.out.println("恭喜！成功获取录音时间："+time_record); 
					}
				}
			}
		} // items
		Demension demension = SWFSizeGetter.getFlashSize(path);
		//WebView坐标
		int x = 0,y = 0;
		//WebView宽高
		int width_web = 0;
		int height_web = 0;
		//Flash宽高
		int width_flash = 0;
		int height_flash = 0;
		//屏幕宽高
		int width_screen = (int) (Constant.getWidthScreen() );
		int height_screen = 717;
		demension.setHeight((int) (demension.getHeight()/1.065)); 
		width_web = demension.getWidth();
		height_web = demension.getHeight();
		width_flash = width_web;
		height_flash = height_web;
		x = (int) ((width_screen - demension.getWidth())/2.0)+10;
		y = (int) ((height_screen - demension.getHeight())/2.0);
		y -= demension.getHeight() * 0.083;
		ViewController.setFlashData( path,bg_poem,width_web,height_web,x,y,width_flash, height_flash,true);
		ViewController.controllView(Constant.VIEW_FLASH_GUANKAN);
	}


	/**
	 * 函数作用：播放flash
	 * void
	 */
	private static void playFlash(int type) { 
		//System.out.println("playFlash   type======"+type); 
		String filePath = currentUtilData.getData().getItemsList().get(0).getItemList().get(0).getAttributeValue("sourceFile");
		String bgPath = currentUtilData.getData().getAttributeValue("bg");
		String height_string = currentUtilData.getData().getItemsList().get(0).getItemList().get(0).getAttributeValue("height");
		int height_intial = -1;
		if(height_string == null || "".equals(height_string)){
			height_intial = -1;
		}else{
			height_intial = Integer.parseInt(height_string);
		}
		String x_string = currentUtilData.getData().getItemsList().get(0).getItemList().get(0).getAttributeValue("x");
		int x_left = -1;
		if(x_string == null || "".equals(x_string) ){
			x_left = -1;
		}else{
			x_left = Integer.parseInt(x_string);
		}
		//System.out.println("playFlash中的BG======"+bgPath);
		//System.out.println("playFlash中的filePath======"+filePath);
		//System.out.println("playFlash中的height======"+height_intial);
		//System.out.println("playFlash中的x_left======"+x_left);
		//System.out.println("type======"+type);
		//System.out.println("Constant.TYPE_FLASH_HUDONG======"+Constant.TYPE_FLASH_HUDONG);
		Demension demension = SWFSizeGetter.getFlashSize(filePath);
		//WebView坐标
		int x = 0,y = 0;
		//WebView宽高
		int width_web = 0;
		int height_web = 0;
		//Flash宽高
		int width_flash = 0;
		int height_flash = 0;
		//屏幕宽高
		int width_screen = (int)(Constant.getWidthScreen());
		int height_screen = 717;
		switch(type){
		case Constant.TYPE_FLASH_Reading:
			System.out.println("英语阅读FLASH");
			//英语阅读FLASH
			if(demension.getWidth()>900){
				//全屏模式
				demension.setWidth(1024);
				demension.setHeight(717);
				width_flash = demension.getWidth();
				height_flash = demension.getHeight();
			}else{
				demension.setWidth((int) (demension.getWidth()/1.066));
				width_flash = demension.getWidth();
				height_flash = demension.getHeight();
				demension.setHeight(460); 
			}
			width_web = demension.getWidth();
			if(demension.getWidth()>900){
				height_web = 717;
			}else{
				if(height_intial != -1){
					height_web = (int) (height_intial/1.04);
				}else{ 
					height_web = demension.getHeight();
				}
			}
			if(x_left != -1){
				x = (int) ((width_screen - width_web )/2.0) +x_left;
			}else{
				x = (int) ((width_screen - width_web)/2.0);
			}
			y = (int) ((height_screen - height_web)/2.0) +3;
			ViewController.setIsFrontHidden(true);
			ViewController.setFrontPosition(487,877);
			ViewController.setFlashData( filePath,bgPath,width_web,height_web,x,y,width_flash,height_flash,false);
			ViewController.controllView(Constant.VIEW_FLASH);
			break;
		case Constant.TYPE_FLASH_ROLE:
			System.out.println("英语角色扮演FLASH");
			//英语角色扮演FLASH
			if(demension.getWidth()>900){
				//全屏模式
				demension.setWidth(1024);
				demension.setHeight(717);
				width_flash = demension.getWidth();
				height_flash = demension.getHeight();
			}else{
				demension.setWidth((int) (demension.getWidth()/1.066));
				width_flash = demension.getWidth();
				height_flash = demension.getHeight();
				demension.setHeight(460); 
			}
			width_web = demension.getWidth();
			if(demension.getWidth()>900){
				height_web = 717;
			}else{
				if(height_intial != -1){
					height_web = (int) (height_intial/1.04);
				}else{ 
					height_web = demension.getHeight();
				}
			}
			if(x_left != -1){
				x = (int) ((width_screen - width_web )/2.0) +x_left;
			}else{
				x = (int) ((width_screen - width_web)/2.0);
			}
			y = (int) ((height_screen - height_web)/2.0) +3;
			ViewController.setIsFrontHidden(true);
			ViewController.setFlashData( filePath,bgPath,width_web,height_web,x,y,width_flash,height_flash,false);
			ViewController.controllView(Constant.VIEW_FLASH);
			break;
		case Constant.TYPE_FLASH_YW:
			//语文FLASH
			System.out.println("语文FLASH");
			if(demension.getWidth()>900){
				//全屏模式
				demension.setWidth(1024);
				demension.setHeight(717);
				if(string_path[index_path[0]].contains("综合练习")  ){
					ViewController.setControllerPositionY(15);
				}
			}else{
				demension.setWidth((int)(demension.getWidth()/1.065));
				demension.setHeight((int)(demension.getHeight()/1.065)); 
			}
			width_web = demension.getWidth();
			if(height_intial != -1){
				height_web = (int) (height_intial/1.03);
			}else{
				height_web = demension.getHeight();
			}
			width_flash = width_web;
			height_flash = height_web;
			x = (int) ((width_screen - demension.getWidth())/2.0);
			y = (int) ((height_screen - demension.getHeight())/2.0);
			ViewController.setFlashData(filePath,bgPath,width_web,height_web,x,y,width_flash,height_flash,true);
			ViewController.controllView(Constant.VIEW_FLASH);
			break;
		case Constant.TYPE_FLASH_NORMAL:
			//普通FLASH
			System.out.println("普通FLASH");
			demension.setWidth((int) (demension.getWidth()/1.065));
			demension.setHeight((int) (demension.getHeight()/1.065)); 
			width_web = demension.getWidth();
			if(height_intial != -1){
				height_web = (int) (height_intial/1.03);
			}else{
				height_web = demension.getHeight();
			}
			width_flash = width_web;
			height_flash = height_web;
			if(x_left != -1){
				x = (int) ((width_screen - width_web )/2.0) +x_left;
			}else{
				x = (int) ((width_screen - width_web)/2.0);
			}
			y = (int) ((height_screen - demension.getHeight())/2.0);
			y -= demension.getHeight() * 0.022 ;
			ViewController.setFlashData(filePath,bgPath,width_web,height_web,x,y,width_flash,height_flash,true);
			ViewController.controllView(Constant.VIEW_FLASH);
			break;
		case Constant.TYPE_FLASH_HUDONG:
			//互动FLASH
			System.out.println("互动FLASH");
			if(demension.getWidth()>900){
				//全屏模式
				demension.setWidth(1024);
				demension.setHeight(717);
				width_flash = demension.getWidth();
				height_flash = demension.getHeight();
			}else{
				//非全屏
				demension.setWidth((int) (demension.getWidth()/1.066));
				width_flash = demension.getWidth();
				height_flash = demension.getHeight();
				if(demension.getWidth() < 800){
					if(demension.getHeight()<=500){
						height_flash = 460;
					}
					demension.setHeight(460);
				}
			}
			width_web = demension.getWidth();
			//System.out.println("height_intial = "+height_intial+"，height_flash="+height_flash);
			if(height_intial != -1){
				height_web = (int) (height_intial/1.03);
			}else{
				if(demension.getWidth()>900){
					height_web = 717;
				}else{
					height_web = demension.getHeight();
				}
			}
			//System.out.println("x_left="+x_left);
			if(x_left >0){
				x = (int) ((width_screen - width_web )/2.0) +x_left;
				ViewController.setIsWebViewHandle(true);
			}else{
				x = (int) ((width_screen - width_web)/2.0);
			}
			y = (int) ((height_screen - height_web)/2.0) +3;
			ViewController.setFlashData( filePath,bgPath,width_web,height_web,x,y,width_flash,height_flash,false);
			ViewController.controllView(Constant.VIEW_FLASH);
			break;
		}
	}




	public static BookData getBigIndex() {
		return bigIndex;
	}


	public static void setBigIndex(BookData bigIndex) {
		ResourceLoader.bigIndex = bigIndex;
	}


	public static BookData getSmallIndex() {
		return smallIndex;
	}


	public static void setSmallIndex(BookData smallIndex) {
		ResourceLoader.smallIndex = smallIndex;
	}


	public static int[] getIndex_path() {
		return index_path;
	}


	public static void setIndex_path(int[] index_path) {
		ResourceLoader.index_path = index_path;
	}


	public static String[] getString_path() {
		return string_path;
	}


	public static void setString_path(String[] string_path) {
		ResourceLoader.string_path = string_path;
	}


	public static int getType_handle() {
		return type_handle;
	}


	public static void setType_handle(int type_handle) {
		ResourceLoader.type_handle = type_handle;
	}


	public static LeftMenuAdapter getAdapter_leftMenu() {
		return adapter_leftMenu;
	}


	public static void setAdapter_leftMenu(LeftMenuAdapter adapter_leftMenu) {
		ResourceLoader.adapter_leftMenu = adapter_leftMenu;
	}




	public static int getIndex_level_current_recycle() {
		return index_level_current_recycle;
	}


	public static void setIndex_level_current_recycle(
			int index_level_current_recycle) {
		ResourceLoader.index_level_current_recycle = index_level_current_recycle;
	}


	public static ArrayList<String> getIcon_menu_left() {
		return icon_menu_left;
	}


	public static void setIcon_menu_left(ArrayList<String> icon_menu_left) {
		ResourceLoader.icon_menu_left = icon_menu_left;
	}


	public static ArrayList<String> getName_menu_left() {
		return name_menu_left;
	}


	public static void setName_menu_left(ArrayList<String> name_menu_left) {
		ResourceLoader.name_menu_left = name_menu_left;
	}


	public static int getIndex_highlight_left() {
		return index_highlight_left;
	}


	public static void setIndex_highlight_left(int index_highlight_left) {
		ResourceLoader.index_highlight_left = index_highlight_left;
	}


	public static ArrayList<String> getIcon_menu_center() {
		return icon_menu_center;
	}


	public static void setIcon_menu_center(ArrayList<String> icon_menu_center) {
		ResourceLoader.icon_menu_center = icon_menu_center;
	}


	public static ArrayList<String> getName_menu_center() {
		return name_menu_center;
	}


	public static void setName_menu_center(ArrayList<String> name_menu_center) {
		ResourceLoader.name_menu_center = name_menu_center;
	}


	public static String getIcon_packcage() {
		return icon_packcage;
	}


	public static void setIcon_packcage(String icon_packcage) {
		ResourceLoader.icon_packcage = icon_packcage;
	}


	public static MenuView getMenuView() {
		return menuView;
	}


	public static void setMenuView(MenuView menuView) {
		ResourceLoader.menuView = menuView;
	}

	/**
	 * 函数作用：获取当前点击的学习资源的类型
	 * String
	 * @return
	 */
	public static void loadResource(int position ) {
		//System.out.println("进入loadResource。。pos="+position+",index_path[2]="+index_path[2]);
		if(index_path[2] == 0){
			//同步学习用大目录
			CatalogBean bean = bigIndex.getmList().get(index_path[2]);
			//System.out.println("CatalogBean.SECTION="+CatalogBean.SECTION);
			for(int i=3;i<=index_path[0]+1;i++){
				//System.out.println("i="+i+",bean.type_msg="+bean.type_msg);
				if(bean.type_msg == CatalogBean.SECTION){
					//找到学习资源
					//System.out.println("找到学习资源...");
					CatalogBean section = bean.sectionList.get(position);
					//加载资源数据
					loadMetaData(section.path, section.type, section.mode);
					break;
				}else{
					bean = bean.catalogList.get(index_path[i]);
				}
			}
		}else{
			//其他用小目录
			//根目录为sectionList
			CatalogBean bean = null;
			if(smallIndex.getmList().size() == 1){
				bean = smallIndex.getmList().get(0);
			}else{
				bean = smallIndex.getmList().get(index_path[position_small+1]);
				//System.out.println("在使用小目录找资源时，index_path[0]");
				for(int i=position_small+2;i<=index_path[0];i++){
					bean = bean.catalogList.get(index_path[i]);
				}
			}
			//找到学习资源
			CatalogBean section = bean.sectionList.get(position);
			//加载资源数据
			loadMetaData(section.path, section.type, section.mode);
		}
	}
	
	
	/**
	 * 函数作用：根据下标获取叶子节点的路径
	 * String
	 * @param position
	 * @return
	 */
	public static CatalogBean getLeafBean(int position){
		if(index_path[2] == 0){
			//同步学习用大目录
			CatalogBean bean = bigIndex.getmList().get(index_path[2]);
			for(int i=3;i<=index_path[0];i++){
				bean = bean.catalogList.get(index_path[i]);
			}
			//找到学习资源
			CatalogBean section = bean.sectionList.get(position);
			return section;
		}else{
			//其他用小目录
			CatalogBean bean = smallIndex.getmList().get(index_path[position_small+1]);
			//System.out.println("position_small="+position_small);
			for(int i=position_small+2;i<=index_path[0];i++){
				bean = bean.catalogList.get(index_path[i]);
			}
			//找到学习资源
			CatalogBean section = bean.sectionList.get(position);
			return section;
		}
	}
	
	public static boolean isLastLevel = false;


	/**
	 * 函数作用：刷新目录视图
	 * void
	 */
	public static void refreshMenuView() {
		//处理界面
//		menuView.setData();
		isLastLevel = true;
		menuView.setData();
		menuView.setShow();		
	}
	
	private static int type_touch = 0;  //1为PATH 2为LEFT  3为CENTER
	
}
