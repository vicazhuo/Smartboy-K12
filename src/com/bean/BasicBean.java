package com.bean;

/**
 * 在intro，question，option和answer中CDATA的基本数据
 * @author lenovo
 *
 */
public final class BasicBean {
	/**
	 * 图片高度
	 */
	private int height_img;
	private int width_img;
	
	private boolean bPicture;
	
	/**
	 * 字体颜色
	 */
	private String color;
	/**
	 * 字体
	 */
	private String font;
	/**
	 * 字体大小
	 */
	private String fontSize;
	/**
	 * 该标签中内容
	 */
	private String ownText;
	/**
	 * 该标签中可能的sourceFile数据，没有数据则为null
	 */
	private String path;
	
	

	public BasicBean() {
	}
	
	public BasicBean(String color, String font, String fontSize,
			String ownText, String path) {
		super();
		this.color = color;
		this.font = font;
		this.fontSize = fontSize;
		this.ownText = ownText;
		this.path = path;
	}
	

	public boolean getPicture() {
		return bPicture;
	}
	public void setPicture(boolean bPicture) {
		this.bPicture = bPicture;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public String getFontSize() {
		return fontSize;
	}

	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}

	public String getOwnText() {
		return ownText;
	}

	public void setOwnText(String ownText) {
		this.ownText = ownText;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getHeight_img() {
		return height_img;
	}

	public void setHeight_img(int height_img) {
		this.height_img = height_img;
	}

	public int getWidth_img() {
		return width_img;
	} 

	public void setWidth_img(int width_img) {
		this.width_img = width_img;
	}
	
}
