package com.bean;

import java.util.ArrayList;

public class ObjectiveQuizBean {
	/*
	 * 显示在一个横排的问题 字符串、大小、颜色
	 */
	ArrayList<String> question_text;
	ArrayList<Integer> question_size; //为空时，默认40
	ArrayList<Integer> question_colr; //为空时，默认Color.BLACK
	String question_mp3; 		//为空时，表示没有
	/*
	 * 选项
	 */
	ArrayList<String> option_text;	
	ArrayList<Integer> option_size;	//为空时，默认25
	ArrayList<Integer> option_colr;	//为空时，默认Color.BLACK
	int right_anwser;
	/*
	 * 答案解释
	 */
	String answer_mp3;    //为空时，表示没有
	ArrayList<String> answer_text;	
	ArrayList<Integer> answer_size;	//为空时，默认25 
	ArrayList<Integer> answer_colr;	//为空时，默认Color.BLACK
	
	
	/**
	 * 函数作用：数据打印
	 * void
	 */
	public void print(){
		System.out.println("………………………………………………问题……………………………………………………");
		System.out.println("问题mp3="+question_mp3);
		for(int i=0;i<question_text.size();i++){
			System.out.println("第"+i+"个："+question_text.get(i));
			if(question_colr.size()>0){
				System.out.println("color="+question_colr.get(i)+",size="+question_size.get(i));
			}
		}
		System.out.println("………………………………………………选项…………………………………………………");
		for(int i=0;i<option_text.size();i++){
			if(i==right_anwser){
				System.out.println("right_anwser="+right_anwser);
				System.out.println("答案！");
			}
			System.out.println("第"+i+"个："+option_text.get(i));
			if(option_colr.size()>0){
				System.out.println("color="+option_colr.get(i)+",size="+option_size.get(i));
			}
		}
		System.out.println("………………………………………………解释…………………………………………………");
		System.out.println("解释mp3="+answer_mp3);
		if(answer_text != null){
			for(int i=0;i<answer_text.size();i++){
				System.out.println("第"+i+"个："+answer_text.get(i));
				if(answer_colr.size()>0){
					System.out.println("color="+answer_colr.get(i)+",size="+answer_size.get(i));
				}
			}
		}
	}
	
	
	
	public ArrayList<String> getQuestion_text() {
		return question_text;
	}
	public void setQuestion_text(ArrayList<String> question_text) {
		this.question_text = question_text;
	}
	public ArrayList<Integer> getQuestion_size() {
		return question_size;
	}
	public void setQuestion_size(ArrayList<Integer> question_size) {
		this.question_size = question_size;
	}
	public ArrayList<Integer> getQuestion_colr() {
		return question_colr;
	}
	public void setQuestion_colr(ArrayList<Integer> question_colr) {
		this.question_colr = question_colr;
	}
	public String getQuestion_mp3() {
		return question_mp3;
	}
	public void setQuestion_mp3(String question_mp3) {
		this.question_mp3 = question_mp3;
	}
	public ArrayList<String> getOption_text() {
		return option_text;
	}
	public void setOption_text(ArrayList<String> option_text) {
		this.option_text = option_text;
	}
	public ArrayList<Integer> getOption_size() {
		return option_size;
	}
	public void setOption_size(ArrayList<Integer> option_size) {
		this.option_size = option_size;
	}
	public ArrayList<Integer> getOption_colr() {
		return option_colr;
	}
	public void setOption_colr(ArrayList<Integer> option_colr) {
		this.option_colr = option_colr;
	}
	public int getRight_anwser() {
		return right_anwser;
	}
	public void setRight_anwser(int right_anwser) {
		this.right_anwser = right_anwser;
	}
	public String getAnswer_mp3() {
		return answer_mp3;
	}
	public void setAnswer_mp3(String answer_mp3) {
		this.answer_mp3 = answer_mp3;
	}
	public ArrayList<String> getAnswer_text() {
		return answer_text;
	}
	public void setAnswer_text(ArrayList<String> answer_text) {
		this.answer_text = answer_text;
	}
	public ArrayList<Integer> getAnswer_size() {
		return answer_size;
	}
	public void setAnswer_size(ArrayList<Integer> answer_size) {
		this.answer_size = answer_size;
	}
	public ArrayList<Integer> getAnswer_colr() {
		return answer_colr;
	}
	public void setAnswer_colr(ArrayList<Integer> answer_colr) {
		this.answer_colr = answer_colr;
	}
	
	
	
	
	
	
	
}
