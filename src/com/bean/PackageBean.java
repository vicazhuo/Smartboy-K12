package com.bean;
/**
 * 解析目录为package时数据bean
 * 
 * <catalog title="语文基础" bg="bg/Chinese/3_1_3.png" icon="icon/语法专家.png">
 * 	<package source="package/语文基础/一级"/>
 * 	<package source="package/语文基础/二级"/>
 * </catalog>
 * 
 * @author lenovo
 *
 */
public class PackageBean {
	/**
	 * 目录路径
	 */
	public String path;
	/**
	 * 目标目录名
	 */
	public String title;
}
