package com.meta;

import java.util.ArrayList;

/**
 * data.xml文件的解析头
 * @author lenovo
 *
 */
public interface Data extends Attribute{
	public ArrayList<Items> getItemsList();
}
