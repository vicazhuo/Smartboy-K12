package com.meta;

import java.util.ArrayList;

/**
 * data.xml 文件中item的基本数据对象接口
 * @author lenovo
 *
 */
public interface Item extends Attribute{
	/**
	 * @return : 返回问题列表数据
	 */
	public ArrayList<Question> getQuestionList();
	/**
	 * @return ： 返回选择列表数据对象
	 */
	public ArrayList<Option> getOptionList();
	/**
	 * @return ： 返回答案数据对象
	 */
	public ArrayList<Answer> getAnswerList();
	
	/**
	 * 获取该题目做的情况
	 */
	public int getFirstState();
	public void setFirstState(int firstState);
	
	public int getFinalState();
	public void setFinalState(int finalState);
	//在重复2遍学习中会使用到这个状态
	public int getSecondFinalState();
	public void setSecondFinalState(int scdFinalState);
	
	
	/**
	 *  操作当前题目的题号
	 */
	public int getTopicIndex();
	public void setTopicIndex(int topicIndex);
	/**
	 * 获取当前进度中该题目是否已经做过的标志，在mode为
	 */
	public boolean isDown();
	public void setDown(boolean hasDown);
}
