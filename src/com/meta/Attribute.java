package com.meta;
/**
 * xml节点的属性管理器
 * @author lenovo
 *
 */
public interface Attribute {
	/**
	 * @param value : 属性名
	 * @return ： 属性值
	 */
	public String getAttributeValue(String name);
	public String getAttributeValue(int index);
	/**
	 * @return ： 返回属性数量
	 */
	public int getAttributeCount();
	/**
	 * @param index : 序号
	 * @return ： 返回属性名
	 */
	public String getAttribueName(int index);
}
