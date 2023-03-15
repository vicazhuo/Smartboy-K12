package com.meta.impl;

import java.util.ArrayList;

import org.xml.sax.helpers.AttributesImpl;

import com.meta.Answer;
import com.meta.Attribute;
import com.meta.Item;
import com.meta.Option;
import com.meta.Question;
/**
 * 每一个item节点的数据
 * @author lenovo
 *
 */
public final class ItemImpl extends AttributeImpl implements Item {
	/**
	 * 问题列表
	 */
	private ArrayList<Question> mQuestion;
	/**
	 * 选择列表
	 */
	private ArrayList<Option> mOption;
	/**
	 * 答案列表
	 */
	private ArrayList<Answer> mAnswer;
	
	public static final int FLAG_NOT_DO 	= 0;	//没有做过
	public static final int FLAG_UNDERSTAND = 1;	//已经掌握
	public static final int FLAG_VAGUE 		= 2;	//模糊
	public static final int FLAG_NOT_UND 	= 3;	//完全不会
	
	/**
	 * 一个题目做的结果标志，0-还没有做过，1-已经掌握，2-模糊，3-没有掌握
	 */
	private int firstState = 0;
	private int finalState = 0;
	private int secondFinalState = 0;
	// 当前题目的题号，最小值为1，最大值为题目总数.
	private int pCurrentTopicIndex;
	private boolean hadDown = false;	//该题目是否在当前进度中已经做过，true-做过
	
	
	@Override
	public ArrayList<Question> getQuestionList() {
		return mQuestion;
	}

	@Override
	public ArrayList<Option> getOptionList() {
		return mOption;
	}

	@Override
	public ArrayList<Answer> getAnswerList() {
		return mAnswer;
	}
	
	protected void addQuestion(final Question question) {
		if(mQuestion == null) {
			mQuestion = new ArrayList<Question>(1);
		}
		
		mQuestion.add(question);
	}

	protected void addOption(final Option option) {
		if(mOption == null) {
			mOption = new ArrayList<Option>(4);
		}
		
		mOption.add(option);
	}
	
	protected void addAnswer(final Answer answer) {
		if(mAnswer == null) {
			mAnswer = new ArrayList<Answer>(1);
		}
		
		mAnswer.add(answer);
	}

	@Override
	public int getFirstState() {
		return firstState;
	}

	@Override
	public void setFirstState(int firstState) {
		this.firstState = firstState;
	}

	@Override
	public int getFinalState() {
		return finalState;
	}

	@Override
	public void setFinalState(int finalState) {
		this.finalState = finalState;
	}

	@Override
	public int getTopicIndex() {
		return pCurrentTopicIndex;
	}

	@Override
	public void setTopicIndex(int topicIndex) {
		this.pCurrentTopicIndex = topicIndex;
	}

	@Override
	public int getSecondFinalState() {
		return secondFinalState;
	}

	@Override
	public void setSecondFinalState(int scdFinalState) {
		this.secondFinalState = scdFinalState;
	}

	@Override
	public boolean isDown() {
		return hadDown;
	}

	@Override
	public void setDown(boolean hasDown) {
		this.hadDown = hasDown;
	}
}
