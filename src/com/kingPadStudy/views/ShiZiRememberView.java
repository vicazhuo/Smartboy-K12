package com.kingPadStudy.views;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.bean.BasicBean;
import com.bean.ContentStateBean;
import com.bean.TopicBean;
import com.data.MetaUtilData;
import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.constant.Constant;
import com.kingPadStudy.controller.ViewController;
import com.kingPadStudy.resource.ResourceLoader;
import com.kingpad.R;
import com.meta.Answer;
import com.meta.Data;
import com.meta.Intro;
import com.meta.Item;
import com.meta.Items;
import com.meta.Question;
import com.utils.DatabaseAdapter;
import com.net.LoadData;
import com.net.RequestListener;
import com.net.RequestParameter;
import com.utils.Util;
import com.views.GameView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShiZiRememberView extends LinearLayout {
	/**
	 * 函数作用:开始运行 void
	 * 
	 * @param path
	 * @param type
	 */
	public void setStart(String path, String type, int index, int studyState) {
		this.path = path;
		this.type = type;
		this.index = index;
		this.studySate = studyState;
		Util.initSoundPool(context);
		initView();
		setListener();
		setMode();
		loadData();
	}

	/**
	 * 函数作用:设置模式 void
	 */
	private void setMode() {
		/*
		 * 找到记录列表
		 */
		list_record = DatabaseAdapter.getInstance(context).getRecord(path);
		int size_done = 0;
		if (list_record != null) {
			size_done = list_record.size();
		}
		if (studySate == 0) {
			for (int i = 0; i < size_done; i++) {
				TopicBean bean = list_record.get(i);
				int state = bean.finalState;
				switch (state) {
				case 1:
					num_understand++;
					break;
				case 2:
					num_puzzle++;
					break;
				case 3:
					num_not_understand++;
					break;
				}
			}
			int content_state = DatabaseAdapter.getInstance(context)
					.getLishtState(path);
			// 根据目录状态，判断是否是循环学习
			if (content_state == 0) {
				// 普通学习
				Util.playBackGroundSound(context, "hint_remani_select.mp3",
						false);
				text_hint.setText(Constant.TEXT_REMAIN_SELECT);
			} else if (content_state == -1) {
				// 第一次进入
				Util.playBackGroundSound(context, "hint_to_learn.mp3", false);
				text_hint.setText(Constant.TEXT_BEGIN_TO_LEARN);
			} else if (content_state == 1) {
				// 浏览
				isReview = true;
				Util.playBackGroundSound(context, "hint_done.mp3", false);
				text_hint.setText(Constant.TEXT_ALL_UNDERSTAND);
			} else {
				// 循环学习
				isFirstLearn = false;
				isFirstSection = true;
				isCircleStudy = true;
				Util.playBackGroundSound(context, "hint_remain_do.mp3", false);
				text_hint.setText("你已经完成筛查，有"
						+ (num_not_understand + num_puzzle)
						+ "个知识点没有掌握，你需要把它们再做2遍。");
			}
		} else {
			Util.playBackGroundSound(context, "hint_to_learn.mp3", false);
			text_hint.setText(Constant.TEXT_BEGIN_TO_LEARN);
		}
		textview_not_understand.setText("未掌握:"+num_not_understand);
		textview_understand.setText("已掌握:"+num_understand);
		textview_vague.setText("模    糊:"+num_puzzle);
		layout_hint.setVisibility(View.VISIBLE);
	}

	private void loadData() {
		// 解析index记录
		RequestParameter parameter = new RequestParameter();
		parameter.add("type", type);
		parameter.add("path", path);
		LoadData.loadData(Constant.META_UTIL_DATA, parameter,
				new RequestListener() {
					public void onError(String error) {

					}

					public void onComplete(Object obj) {
						MetaUtilData Utildata = (MetaUtilData) obj;
						Data data = Utildata.getData();
						/*
						 * 找到数据
						 */
						// 背景图
						for (int i = 0; i < data.getAttributeCount(); i++) {
							if (data.getAttribueName(i).equals("bg")) {
								bg = data.getAttributeValue(i);
								File mfile = new File(bg);
								if (mfile.exists()) {// 若该文件存在
									bmp_bg_small = BitmapFactory.decodeFile(bg);
									flashBgLayout
											.setBackgroundDrawable(new BitmapDrawable(
													bmp_bg_small));
								}
							}
						}
						// 资源路径
						ArrayList<Items> itemsList = data.getItemsList();
						for (int i = 0; i < itemsList.size(); i++) {
							Items items = itemsList.get(i);
							Intro intro = items.getIntro();
							if (intro != null) {
								for (int j = 0; j < intro.getAttributeCount(); j++) {
									if (intro.getAttribueName(j).equals(
											"soundFile")) {
										String mp3 = intro.getAttributeValue(j);
										isFirstMp3LoadDone = true;
										mp3_guide.add(mp3);
										break;
									}
								}
								BasicBean bean = intro.getBasicBean();
								String text = bean.getOwnText();
								text_guide.add(text);
							}
							// 题目
							ArrayList<Item> itemList = items.getItemList();
							for (int j = 0; j < itemList.size(); j++) {
								Item item = itemList.get(j);
								if (item.getQuestionList() != null) {
									for (int m = 0; m < item.getQuestionList()
											.size(); m++) {
										Question question = item
												.getQuestionList().get(m);
										ArrayList<BasicBean> list = question
												.getList();
										if (list != null)
											for (int n = 0; n < list.size(); n++) {
												BasicBean bean = list.get(n);
												list_dazi.add(bean.getOwnText());
												total_question++;
											}
									}
								}
								if (item.getAnswerList() != null) {
									for (int m = 0; m < item.getAnswerList()
											.size(); m++) {
										Answer answer = item.getAnswerList()
												.get(m);
										for (int n = 0; n < answer
												.getAttributeCount(); n++) {
											Util.addAnswerMp3List(context,
													answer.getAttributeValue(n));
										}
										ArrayList<BasicBean> list = answer
												.getList();
										if (list != null)
											for (int n = 0; n < list.size(); n++) {
												BasicBean bean = list.get(n);
												list_img.add(bean.getPath());
//												//System.out.println("获取图片:"
//														+ bean.getPath());
												list_img_height.add(bean
														.getHeight_img());
												list_img_width.add(bean
														.getWidth_img());
											}
									}
								} // if
							}
						}
						/*
						 * 找到记录列表
						 */
						list_record = DatabaseAdapter.getInstance(context)
								.getRecord(path);
						int size_done = 0;
						if (list_record != null) {
							size_done = list_record.size();
						}
						if (studySate == 0) {
							/*
							 * 更新数字 和状态
							 */
							num_not_done = total_question - size_done;
							// 当前题号
							index_current_question = 0;
							// 状态
							state_question = new int[total_question];
							for (int i = 0; i < size_done; i++) {
								TopicBean bean = list_record.get(i);
								int number = bean.number;
								int state = bean.finalState;
								state_question[number] = state;
							}
							state_circle_study1 = new int[total_question];
							state_circle_study2 = new int[total_question];
							// 循环学习数组
							circle_study = new boolean[total_question];
							benjiegongji = total_question;
							/*
							 * 记分牌初始化
							 */
							//本节共计
							textview_total_count.setText("本节共计:"+benjiegongji);
							//未完成
							textview_not_avi.setText("未完成:"+num_not_done);
						} else {
							// 重点复习 和 次重点复习
							/*
							 * 更新数字 和状态
							 */
							// 状态
							state_question = new int[total_question];
							// 循环学习数组
							circle_study = new boolean[total_question];
							total_current_circle = 0;
							for (int i = 0; i < size_done; i++) {
								TopicBean bean = list_record.get(i);
								int number = bean.number;
								int state = bean.firstState;
								if (state == 1) {
									if (studySate == 1) {
										state_question[number] = -1;
										circle_study[number] = false;
									} else if (studySate == 2) {
										state_question[number] = 0;
										circle_study[number] = true;
										total_current_circle++;
									}
								} else {
									if (studySate == 1) {
										state_question[number] = 0;
										circle_study[number] = true;
										total_current_circle++;
									} else if (studySate == 2) {
										state_question[number] = -1;
										circle_study[number] = false;
									}
								}
							}
							num_not_done = total_current_circle;
							// 当前题号
							index_current_question = 0;
							state_circle_study1 = new int[total_question];
							state_circle_study2 = new int[total_question];
							benjiegongji = total_current_circle;
							//本节共计
							textview_total_count.setText("本节共计:"+benjiegongji);
							//未完成
							textview_not_avi.setText("未完成:"+num_not_done);
						}
						KingPadStudyActivity.dismissWaitDialog();
					}

				});
	}

	/**
	 * 函数作用:开始学习 void
	 */
	private void startStudy() {
		// 显示主体
		layout_main.setVisibility(View.VISIBLE);
		// 隐藏题控
		showNextPrevious(false);
		// 隐藏大字和图片、核对
		text_dazi.setVisibility(View.INVISIBLE);
		view_image.setVisibility(View.INVISIBLE);
		view_hedui.setVisibility(View.INVISIBLE);
		// 隐藏灯
		view_deng.setVisibility(View.INVISIBLE);
		// //隐藏喇叭
		button_laba.setVisibility(View.INVISIBLE);
		// 播放引导MP3
		try {
			// 显示引导文字
			intro_text.setText(text_guide.get(index_current_items));
			com.utils.Util.playSound_OnComplete(mp3_guide
					.get(index_current_items));
			com.utils.Util.player_onComplete
					.setOnCompletionListener(new OnCompletionListener() {
						public void onCompletion(MediaPlayer arg0) {
							if (isFirstLearn) {
								// 普通切换题目
								if (state_question[index_current_question] == 0) {
									// 还没有做过
									playQuestion(index_current_question);
								} else {
									// 已经做过
									showDone(index_current_question);
								}
							} else {
								if (isFirstSection) {
									// 普通切换题目
									if (state_circle_study1[index_current_question] == 0) {
										// 还没有做过
										playQuestion(index_current_question);
									} else {
										// 已经做过
										showDone(index_current_question);
									}
								} else {
									// 普通切换题目
									if (state_circle_study2[index_current_question] == 0) {
										// 还没有做过
										playQuestion(index_current_question);
									} else {
										// 已经做过
										showDone(index_current_question);
									}
								}
							}

						}
					});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 函数作用:寻找标号 void
	 */
	private void findIndex(boolean isFromHeadToEnd) {
		if (isFromHeadToEnd) {
			for (int i = 0; i < total_question; i++) {
				if (circle_study[i] == true) {
					index_current_question = i;
					// 找items下标
					index_current_items = findIndexItems(index_current_question);
					return;
				}
			}
		} else {
			for (int i = total_question - 1; i >= 0; i--) {
				if (circle_study[i] == true) {
					index_current_question = i;
					// 找items下标
					index_current_items = findIndexItems(index_current_question);
					return;
				}
			}
		}
	}

	/**
	 * 函数作用:找到items的下标 int
	 * 
	 * @param num
	 * @return
	 */
	private int findIndexItems(int num) {
		return 0;
	}

	/**
	 * 函数作用:播放某个题目 void
	 * 
	 * @param num
	 */
	private void playQuestion(int num) {
		// 隐藏灯
		view_deng.setVisibility(View.INVISIBLE);
		// 隐藏喇叭
		button_laba.setVisibility(View.INVISIBLE);
		// 隐藏小人
		showGenzong(false);
		// 显示提控
		showNextPrevious(true);
		// 显示一个问题资源
		showQuestionAndOption(num, true);
	}

	/**
	 * 函数作用:显示一个问题的资源 void
	 * 
	 * @param num
	 */
	private void showQuestionAndOption(int num, boolean isClick) {
		// 图片消失
		view_image.setVisibility(View.INVISIBLE);
		text_dazi.setText(list_dazi.get(num));
		text_dazi.setVisibility(View.VISIBLE);
		// 显示核对答案
		view_hedui.setVisibility(View.VISIBLE);
	}

	/**
	 * 函数作用:显示正确答案 void
	 */
	protected void showAnswer() {
		// 隐藏核对答案
		view_hedui.setVisibility(View.INVISIBLE);
		Util.playSoundPoolAnswerMp3(context, index_current_question);
		// 显示图片
		String img = list_img.get(index_current_question);
		File mfile = new File(img);
		if (mfile.exists()) {
			// 若该文件存在
			bmp_bg_show = BitmapFactory.decodeFile(img);
			view_image.setVisibility(View.VISIBLE);
			view_image.setImageBitmap(bmp_bg_show);
		}
		// 显示喇叭
		button_laba.setVisibility(View.VISIBLE);
	}

	public String getABCD(int index) {
		switch (index) {
		case 0:
			return "A";
		case 1:
			return "B";
		case 2:
			return "C";
		case 3:
			return "D";
		}
		return null;
	}

	/**
	 * 函数作用:改变题目控制布局的显示属性 void
	 * 
	 * @param b
	 */
	public void showNextPrevious(boolean b) {
		if (b) {
			jump_layout.setVisibility(View.VISIBLE);
		} else {
			jump_layout.setVisibility(View.INVISIBLE);
		}
		// 根据题号，确定显示按钮
		if (isFirstQuestion()) {
			// 第一题，不显示上一题
			button_prvious.setVisibility(View.INVISIBLE);
			button_next.setVisibility(View.VISIBLE);
		} else if (isLastQuestion()) {
			// 最后一题，不显示下一题
			button_prvious.setVisibility(View.VISIBLE);
			button_next.setVisibility(View.INVISIBLE);
		} else {
			button_prvious.setVisibility(View.VISIBLE);
			button_next.setVisibility(View.VISIBLE);
		}
		button_prvious.getFrameAnimationDrawable().stop();
	}

	/**
	 * 函数作用:是否是最后一题 boolean
	 * 
	 * @return
	 */
	public boolean isLastQuestion() {
		for (int i = total_question - 1; i >= 0; i--) {
			if (circle_study[i]) {
				if (i == index_current_question) {
					if (!isFirstLearn && isFirstSection) {
						return false;
					}
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * 函数作用:判断目前是否是第一个题目，没有前一题。 boolean
	 * 
	 * @return
	 */
	public boolean isFirstQuestion() {
		for (int i = 0; i < total_question; i++) {
			if (circle_study[i]) {
				if (i == index_current_question) {
					if (!isFirstLearn && !isFirstSection) {
						return false;
					}
					return true;
				} else {
					return false;
				}
			}
		}

		return false;
	}

	/**
	 * 函数作用:判断目前是否是第一个题目，没有前一题。 boolean
	 * 
	 * @return
	 */
	public boolean isFirstQuestion_previous() {
		for (int i = 0; i < total_question; i++) {
			if (circle_study[i]) {
				if (i == index_current_question) {
					return true;
				} else {
					return false;
				}
			}
		}

		return false;
	}

	/**
	 * 函数作用:改变跟踪布局的显示属性 void
	 * 
	 * @param b
	 */
	private void showGenzong(boolean b) {
		if (b) {
			check_layout.setVisibility(View.VISIBLE);
		} else {
			check_layout.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 函数作用:设置监听器` void
	 */
	private void setListener() {
		ButtonListener listener = new ButtonListener();
		// 大字
		text_dazi.setOnClickListener(listener);
		// 核对答案
		view_hedui.setOnClickListener(listener);
		// 提示牌的确认按钮
		button_hint.setOnClickListener(listener);
		// 返回
		mBackView.setOnClickListener(listener);
		// 返回
		button_laba.setOnClickListener(listener);
		// 跟踪小人
		button_ok.setOnClickListener(listener);
		button_puzzle.setOnClickListener(listener);
		button_no.setOnClickListener(listener);
		// 上下题
		button_prvious.setOnClickListener(listener);
		button_next.setOnClickListener(listener);
	}

	class ButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			//System.out.println("点击了某个东东:");
			if (v == button_hint) {
				if (!isFirstMp3LoadDone) {

				} else {
					// 停止提示音
					Util.stopHint();
					if (isLearnFinish) {
						isReview = true;
					}
					// 点击了提示牌的确认按钮
					// 隐藏提示布局
					layout_hint.setVisibility(View.INVISIBLE);
					// 初始化题目
					initQuestion();
					isCircleStudy = false;
					//System.out.println("初始过后;各题的状态:");
//					for (int i = 0; i < total_question; i++) {
//						//System.out.println("satae[" + i + "]="
//								+ state_question[i]);
//						//System.out.println("circlestudy[" + i + "]="
//								+ circle_study[i]);
//					}
					// 找目前的题目标号和items标号
					findIndex(true);
					// 记录牌显示
					showRegistBoard();
					// 开始学习
					startStudy();
				}
			} else if (v == view_hedui) {
				Util.playSoundPool(context, Constant.AUDIO_MARK_BUTTON);
				// 点击了核对答案
				// 显示答案
				showAnswer();
				// 显示喇叭
				button_laba.setVisibility(View.VISIBLE);
				// 跟踪显示
				showGenzong(true);
				// 题目控制消失
				showNextPrevious(false);
				// 灯消失
				showLight(false, 0);
			} else if (v == button_ok) {
				// 点击没问题
				Util.playSoundPool(context, Constant.AUDIO_MARK_BUTTON);
				// 展示动画
				button_ok.play(false);
				handleAnimation(button_ok, 1);
				number_done_circle++;
				// 记录数组
				if (isFirstLearn) {
					num_not_done--;
					textview_not_avi.setText("未完成:" + num_not_done);
					state_question[index_current_question] = 1;
					// 向DB中增加一条记录信息
					if (studySate == 0) {
						DatabaseAdapter.getInstance(context).add2Record(path,
								index_current_question, 1, 1);
					}
				} else {
					if (isFirstSection) {
						state_circle_study1[index_current_question] = 1;
					} else {
						state_circle_study2[index_current_question] = 1;
					}
					if (state_circle_study1[index_current_question] == state_circle_study2[index_current_question]) {
						// 这个题目攻克了
						if (state_question[index_current_question] == 2) {
							num_puzzle--;
							num_understand++;
							textview_vague.setText("模    糊:" + num_puzzle);
							textview_understand
									.setText("已掌握:" + num_understand);
						} else if (state_question[index_current_question] == 3) {
							num_not_understand--;
							num_understand++;
							textview_not_understand.setText("未掌握:"
									+ num_not_understand);
							textview_understand
									.setText("已掌握:" + num_understand);
						}
						state_question[index_current_question] = 1;
						// 修改DB中改信息的最终状态
						if (studySate == 0) {
							DatabaseAdapter.getInstance(context)
									.alterFinalState(path,
											index_current_question, 1);
						}
					}
				}
				// 掌握数加1
				if (isFirstLearn) {
					num_understand++;
					textview_understand.setText("已掌握:" + num_understand);
				}
				handleCircleStudyDone();
			} else if (v == button_puzzle) {
				// 点击模糊
				Util.playSoundPool(context, Constant.AUDIO_MARK_BUTTON);
				// 展示动画
				button_puzzle.play(false);
				number_done_circle++;
				handleAnimation(button_puzzle, 2);
				if (isFirstLearn) {
					num_not_done--;
					textview_not_avi.setText("未完成:" + num_not_done);
					state_question[index_current_question] = 2;
					// 向DB中增加一条记录信息
					if (studySate == 0) {
						DatabaseAdapter.getInstance(context).add2Record(path,
								index_current_question, 2, 2);
					}
				} else {
					if (isFirstSection) {
						state_circle_study1[index_current_question] = 2;
					} else {
						state_circle_study2[index_current_question] = 2;
					}
				}
				if (isFirstLearn) {
					// 模糊数加1
					num_puzzle++;
					textview_vague.setText("模    糊:" + num_puzzle);
				}
				handleCircleStudyDone();
			} else if (v == button_no) {
				// 点击完全不会
				Util.playSoundPool(context, Constant.AUDIO_MARK_BUTTON);
				// 展示动画
				number_done_circle++;
				button_no.play(false);
				number_done_circle++;
				handleAnimation(button_no, 3);
				if (isFirstLearn) {
					num_not_done--;
					textview_not_avi.setText("未完成:" + num_not_done);
					state_question[index_current_question] = 3;
					// 向DB中增加一条记录信息
					if (studySate == 0) {
						DatabaseAdapter.getInstance(context).add2Record(path,
								index_current_question, 3, 3);
					}
				} else {
					if (isFirstSection) {
						state_circle_study1[index_current_question] = 3;
					} else {
						state_circle_study2[index_current_question] = 3;
					}
				}
				if (isFirstLearn) {
					// 未掌握 数加1
					num_not_understand++;
					textview_not_understand.setText("未掌握:"
							+ num_not_understand);
				}
				handleCircleStudyDone();

			} else if (v == button_prvious) {
				Util.playSoundPool(context, Constant.AUDIO_MARK_BUTTON);
				// 点击上一题
				// 展示动画
				button_prvious.play(false);
				handleAnimation(button_prvious, 4);
			} else if (v == button_next) {
				// 点击下一题
				Util.playSoundPool(context, Constant.AUDIO_MARK_BUTTON);
				// 展示动画
				button_next.play(false);
				//System.out.println(" 点击下一题");
				//System.out.println(" 点击下一题");
				//System.out.println(" 点击下一题");
				//System.out.println(" 点击下一题");
				//System.out.println(" 点击下一题");
				//System.out.println("点击下一题时，state_qustion["
//						+ index_current_question + "]= "
//						+ state_question[index_current_question]);
				handleAnimation(button_next, 5);
			} else if (v == button_laba) {
				// 点击喇叭
				// 播放对应MP3
				Util.playSoundPoolAnswerMp3(context, index_current_question);
			} else if (v == mBackView) {
				// 点击了返回按钮
				Util.playSoundPool(context, Constant.AUDIO_MARK_BUTTON);
				ViewController.backToNormalDuyin();
				Util.clearMp3();
				// 向DB记录目录的数据
				//System.out.println("studySate=" + studySate);
				if (studySate == 0) {
					//System.out.println("isFirstLearn=" + isFirstLearn);
					if (isFirstLearn) {
						// 第一次学习
						boolean isExitst = DatabaseAdapter.getInstance(context)
								.isValueExists(DatabaseAdapter.CONTENT_TABLE,
										path);
						//System.out.println("isExitst=" + isExitst);
						int state_restart = DatabaseAdapter
								.getInstance(context).getLishtState(path);
//						//System.out
//								.print("重新学习状态state_restart=" + state_restart);
						if (!isExitst) {
							DatabaseAdapter.getInstance(context).add2Content(
									path, 0, 0, 0, 1);
						}
					}
				}
				ResourceLoader.refreshMenuView();
				if (bmp_bg!= null && !bmp_bg.isRecycled()) {
					bmp_bg.recycle();
				}
				if (bmp_bg_small!= null && !bmp_bg_small.isRecycled()) {
					bmp_bg_small.recycle();
				}
				if(bmp_bg_show!= null && !bmp_bg_show.isRecycled()){
					bmp_bg_show.recycle();
				}
			}
		}
	}

	/**
	 * 函数作用:显示做过的题目 void
	 * 
	 * @param num
	 */
	private void showDone(int num) {
		// 显示问题和选项
		showQuestionAndOption(num, false);
		// 显示答案
		showAnswer();
		// 跟踪不显示
		showGenzong(false);
		// 题目控制显示
		showNextPrevious(true);
		int state = -1;
		if (!isFirstLearn && isFirstSection) {
			state = state_circle_study1[index_current_question];
		} else if (!isFirstLearn && !isFirstSection) {
			state = state_circle_study2[index_current_question];
		} else {
			state = state_question[index_current_question];
		}
		// 显示灯
		showLight(true, state);
	}

	/**
	 * 函数作用:处理动画 void
	 * 
	 * @param button_ok2
	 */
	public void handleAnimation(final GameView animation, final int type) {
		int duration = 0;
		AnimationDrawable animationDrawable = animation
				.getFrameAnimationDrawable();
		for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
			duration += animationDrawable.getDuration(i);
		}
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				switch (type) {
				case 1:
					// OK按钮
					// 跟踪不显示
					showGenzong(false);
					// 显示题目控制
					showNextPrevious(true);
					// 灯
					showLight(true, 1);
					break;
				case 2:
					// 模糊按钮
					// 跟踪不显示
					showGenzong(false);
					// 显示题目控制
					showNextPrevious(true);
					// 灯
					showLight(true, 2);
					break;
				case 3:
					// 不会按钮
					// 跟踪不显示
					showGenzong(false);
					// 显示题目控制
					showNextPrevious(true);
					// 灯
					showLight(true, 3);
					break;
				case 4:
					playPrevious();
					break;
				case 5:
					playNext();
					break;
				}
				animation.getFrameAnimationDrawable().stop();
			}
		}, duration);
	}

	/**
	 * 函数作用:播放下一题的动作 void
	 */
	protected void playNext() {
		if (!isFirstLearn && isFirstSection && isLastQuestion_next()) {
			isFirstSection = false;
			//System.out.println("第一轮最后一题。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。");
			// 第二次学习的第二轮,找到第一个题目
			findIndex(true);
			if (state_circle_study2[index_current_question] == 0) {
				// 还没有做过
				playQuestion(index_current_question);
			} else {
				// 已经做过
				showDone(index_current_question);
			}
			return;
		}
		// 寻找下一个题目
		if (findNext()) {
			// items改变
			startStudy();
		} else {
			// 普通切换题目
			if (isFirstLearn) {
				if (state_question[index_current_question] == 0) {
					// 还没有做过
					playQuestion(index_current_question);
				} else {
					// 已经做过
					showDone(index_current_question);
				}
			} else {
				if (isFirstSection) {
					if (state_circle_study1[index_current_question] == 0) {
						// 还没有做过
						playQuestion(index_current_question);
					} else {
						// 已经做过
						showDone(index_current_question);
					}
				} else {
					if (state_circle_study2[index_current_question] == 0) {
						// 还没有做过
						playQuestion(index_current_question);
					} else {
						// 已经做过
						showDone(index_current_question);
					}
				}
			}
		}
	}

	/**
	 * 函数作用:播放下一题的动作 void
	 */
	protected void playPrevious() {
		if (!isFirstLearn && !isFirstSection && isFirstQuestion_previous()) {
			isFirstSection = true;
			// 第二次学习的第二轮,找到最后一个题目
			findIndex(false);
			if (state_circle_study1[index_current_question] == 0) {
				// 还没有做过
				playQuestion(index_current_question);
			} else {
				// 已经做过
				showDone(index_current_question);
			}
			return;
		}
		// 寻找上一个题
		if (findPrevious()) {
			// items改变
			startStudy();
		} else {
			// 普通切换题目
			if (isFirstLearn) {
				if (state_question[index_current_question] == 0) {
					// 还没有做过
					playQuestion(index_current_question);
				} else {
					// 已经做过
					showDone(index_current_question);
				}
			} else {
				if (isFirstSection) {
					if (state_circle_study1[index_current_question] == 0) {
						// 还没有做过
						playQuestion(index_current_question);
					} else {
						// 已经做过
						showDone(index_current_question);
					}
				} else {
					if (state_circle_study2[index_current_question] == 0) {
						// 还没有做过
						playQuestion(index_current_question);
					} else {
						// 已经做过
						showDone(index_current_question);
					}
				}
			}
		}
	}

	/**
	 * 函数作用:处理循环学习一次完成时 void
	 */
	public void handleCircleStudyDone() {
		button_laba.setVisibility(View.INVISIBLE);
		//System.out.println("在handleCircleStudyDone中。。。。");
		//System.out.println("在handleCircleStudyDone中。。。。");
		//System.out.println("在handleCircleStudyDone中。。。。");
		//System.out.println("在handleCircleStudyDone中。。。。");
		//System.out.println("isFirstLearn。。。。=" + isFirstLearn);
		if (isFirstLearn) {
			//System.out.println("num_not_done。。。。=" + num_not_done);
			if (num_not_done == 0) {
				// 所有题目都做了一遍
				isFirstLearn = false;
				// 向DB中存储目录的状态
				saveStateContent();
				// 将主体布局隐藏
				layout_main.setVisibility(View.INVISIBLE);
				// 如果是第一次学习完毕，那么就记录数据库
				// 显示文字，根据已掌握数量来判断是否循环
				//System.out.println("num_not_understand。。。。="
//						+ num_not_understand);
				//System.out.println("num_puzzle。。。。=" + num_puzzle);
				if (num_not_understand == 0 && num_puzzle == 0) {
					// 全部掌握，学完一遍了
					// 显示全部掌握文字。
					isLearnFinish = true;
					text_hint.setText(Constant.TEXT_ALL_UNDERSTAND);
					Util.playBackGroundSound(context, "hint_done.mp3", false);
				} else {
					isCircleStudy = true;
					// 还未全部掌握，需要循环学习
					// 显示循环学习文字。
					text_hint.setText("你已经完成筛查，有"
							+ (num_not_understand + num_puzzle)
							+ "个知识点没有掌握，你需要把它们再做2遍。");
					Util.playBackGroundSound(context, "hint_remain_do.mp3",
							false);
				}
				// 弹出提示框
				layout_hint.setVisibility(View.VISIBLE);
			} else {
				if (isLastQuestion_next()) {
					// 是最后一题，并且没有做完题目
					// 将主体布局隐藏
					layout_main.setVisibility(View.INVISIBLE);
					text_hint.setText(Constant.TEXT_REMAIN_SELECT);
					Util.playBackGroundSound(context, "hint_remani_select.mp3",
							false);
					// 弹出提示框
					layout_hint.setVisibility(View.VISIBLE);
				}
			}
		} else {
			// 向DB中存储目录的状态
			saveStateContent();
//			//System.out.println("number_done_circle =" + number_done_circle
//					+ ",total_current_circle=" + total_current_circle);
			if (number_done_circle == total_current_circle * 2) {
//				//System.out
//						.println("number_done_circle == total_current_circle * 2循环时题目做完了");
				//System.out.println("全部掌握，学完一遍了全部掌握，学完一遍了全部掌握，学完一遍了");
				for (int i = 0; i < total_question; i++) {
					//System.out.println("satae[" + i + "]=" + state_question[i]);
				}
				// 当循环时题目做完了
				// 将主体布局隐藏
				layout_main.setVisibility(View.INVISIBLE);
				// 如果是第一次学习完毕，那么就记录数据库
				// 显示文字，根据已掌握数量来判断是否循环
//				//System.out.println("num_not_understand。。。。="
//						+ num_not_understand);
				//System.out.println("num_puzzle。。。。=" + num_puzzle);
				if (num_not_understand == 0 && num_puzzle == 0) {
					// 全部掌握，学完一遍了
					// 显示全部掌握文字。
					isLearnFinish = true;
					text_hint.setText(Constant.TEXT_ALL_UNDERSTAND);
					Util.playBackGroundSound(context, "hint_done.mp3", false);
				} else {
					// 还未全部掌握，需要循环学习
					// 显示循环学习文字。
					isCircleStudy = true;
					text_hint.setText("你已经完成筛查，有"
							+ (num_not_understand + num_puzzle)
							+ "个知识点没有掌握，你需要把它们再做2遍。");
					Util.playBackGroundSound(context, "hint_remain_do.mp3",
							false);
				}
				// 弹出提示框
				layout_hint.setVisibility(View.VISIBLE);
			} else {
				if (isLastQuestion()) {
					// 是最后一题，并且没有做完题目
					// 将主体布局隐藏
					layout_main.setVisibility(View.INVISIBLE);
					// 显示循环学习文字。
					isCircleStudy = true;
					text_hint.setText("你已经完成筛查，有"
							+ (num_not_understand + num_puzzle)
							+ "个知识点没有掌握，你需要把它们再做2遍。");
					Util.playBackGroundSound(context, "hint_remain_do.mp3",
							false);
					// 弹出提示框
					layout_hint.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	/**
	 * 函数作用: 保存目录状态信息 void
	 */
	private void saveStateContent() {
		int state_max = 0;
		for (int i = 0; i < total_question; i++) {
			if (state_question[i] > state_max) {
				state_max = state_question[i];
			}
		}
		int impState = 0;
		int scdState = 0;
		if (state_max == 1) {
			// 本次学习完毕
			// 根据第一次记录，计算重、次重点复习
			ArrayList<TopicBean> list_first = DatabaseAdapter.getInstance(
					context).getRecord(path);
			// 第一次状态 的最大值
			int size_first = list_first.size();
			int max_first_state = 0;
			int state_last = 0;
			boolean allthesame = true;
			for (int i = 0; i < size_first; i++) {
				TopicBean bean = list_first.get(i);
				int state = bean.firstState;
				if (i > 0) {
					if (state_last == 1) {
						if (state != 1) {
							allthesame = false;
						}
					} else {
						if (state == 1) {
							allthesame = false;
						}
					}
				}
				if (state > max_first_state) {
					max_first_state = state;
				}
				state_last = state;
			}

			if (max_first_state == 3 || max_first_state == 2) {
				impState = 1;
				if (allthesame) {
					scdState = 0;
				} else {
					scdState = 1;
				}
			} else {
				impState = 0;
				scdState = 1;
			}
		} else {
			impState = 0;
			scdState = 0;
		}

		if (studySate == 0) {
			if (DatabaseAdapter.getInstance(context).isValueExists(
					DatabaseAdapter.CONTENT_TABLE, path)) {
				ContentStateBean bean = new ContentStateBean();
				bean.importantState = impState;
				bean.secondState = scdState;
				bean.lightState = state_max;
				bean.restartState = 1;
				DatabaseAdapter.getInstance(context).alterContentState(path,
						bean);
			} else {
				DatabaseAdapter.getInstance(context).add2Content(path,
						state_max, impState, scdState, 1);
			}
		}
	}

	/**
	 * 函数作用:再点击下一题时，判断是否是最后一题。 boolean
	 * 
	 * @return
	 */
	public boolean isLastQuestion_next() {
		for (int i = total_question - 1; i >= 0; i--) {
			if (circle_study[i]) {
				if (i == index_current_question) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * 函数作用:显示记录牌 void
	 */
	public void showRegistBoard() {
		textview_total_count.setText("本节共计:" + benjiegongji);
		textview_not_avi.setText("未完成:" + num_not_done);
		textview_not_understand.setText("未掌握:" + num_not_understand);
		textview_understand.setText("已掌握:" + num_understand);
		textview_vague.setText("模    糊:" + num_puzzle);
	}

	/**
	 * 函数作用:寻找上一个题目 boolean
	 * 
	 * @return
	 */
	public boolean findPrevious() {
		for (int i = index_current_question - 1; i >= 0; i--) {
			if (circle_study[i]) {
				index_current_question = i;
				int temp = findIndexItems(index_current_question);
				if (temp != index_current_items) {
					// items改变
					index_current_items = temp;
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * 函数作用:寻找下一个题目 void 返回是否为items
	 */
	public boolean findNext() {
		//System.out.println("准备找到下一题的题号。。当前题号:" + index_current_question);
		for (int i = index_current_question + 1; i < total_question; i++) {
			if (circle_study[i]) {
				index_current_question = i;
//				//System.out.println("下一题的题号:" + index_current_question
//						+ "          &&&&&&&&&&&&&&&");
				int temp = findIndexItems(index_current_question);
//				//System.out.println("下一题的items号:" + temp
//						+ "          &&&&&&&&&&&&&&&");
				if (temp != index_current_items) {
					// items改变
					//System.out.println("items改变");
					index_current_items = temp;
					return true;
				} else {
					return false;
				}
			}
		}
		//System.out.println("没找到下一题的题号。。");
		return false;
	}

	/**
	 * 函数作用:初始化题目 void
	 */
	public void initQuestion() {
		number_done_circle = 0;
		// 初始化题目总数
		isFirstSection = true;
		//System.out.println("initQuestion中。。。");
		//System.out.println("initQuestion中。。。");
		//System.out.println("initQuestion中。。。");
		//System.out.println("initQuestion中。。。");
		//System.out.println("isReview=" + isReview);
		//System.out.println("studySate=" + studySate);
		//System.out.println("isFirstLearn=" + isFirstLearn);
		//System.out.println("各题的状态::");
		for (int i = 0; i < total_question; i++) {
			//System.out.println("satae[" + i + "]=" + state_question[i]);
			//System.out.println("cirlce_study[" + i + "]=" + circle_study[i]);
		}

		if (isReview) {
			isFirstLearn = true;
			if (studySate == 0) {
				total_current_circle = 0;
				for (int i = 0; i < total_question; i++) {
					circle_study[i] = true;
					total_current_circle++;
				}
			} else {
				isFirstLearn = true;
				ArrayList<TopicBean> list_record = DatabaseAdapter.getInstance(
						context).getRecord(path);
				int size_done = 0;
				if (list_record != null) {
					size_done = list_record.size();
				}
				total_current_circle = 0;
				for (int i = 0; i < size_done; i++) {
					TopicBean bean = list_record.get(i);
					int number = bean.number;
					int state = bean.firstState;
					if (state == 1) {
						if (studySate == 1) {
							circle_study[number] = false;
						} else if (studySate == 2) {
							circle_study[number] = true;
							total_current_circle++;
							//System.out.println("第" + number + "个题目需要重做");
						}
					} else {
						if (studySate == 1) {
							circle_study[number] = true;
							total_current_circle++;
							//System.out.println("第" + number + "个题目需要重做");
						} else if (studySate == 2) {
							circle_study[number] = false;
						}
					}
				}
			}
			return;
		}

		// 初始化循环学习数组
		total_current_circle = 0;
		for (int i = 0; i < total_question; i++) {
			if (isFirstLearn) {
				if (studySate == 0) {
					if (state_question[i] == 0) {
						circle_study[i] = true;
						total_current_circle++;
					} else {
						circle_study[i] = false;
					}
				} else {
					if (circle_study[i] && state_question[i] == 0) {
						circle_study[i] = true;
						total_current_circle++;
					} else {
						circle_study[i] = false;
					}
				}
			} else {
				if (isCircleStudy) {
					if (state_question[i] == 1) {
						circle_study[i] = false;
					} else if (state_question[i] == 2 || state_question[i] == 3) {
						state_circle_study1[i] = state_circle_study2[i] = 0;
						total_current_circle++;
						circle_study[i] = true;
					}
				} else {
					if (state_question[i] == 1) {
						circle_study[i] = false;
					} else if (studySate != 0 && circle_study[i]) {
						state_circle_study1[i] = state_circle_study2[i] = 0;
						total_current_circle++;
						circle_study[i] = true;
					} else if (studySate == 0) {
						state_circle_study1[i] = state_circle_study2[i] = 0;
						total_current_circle++;
						circle_study[i] = true;
					}
				}
			}
		}
	}

	/**
	 * 函数作用:是否该调整items的下标了 boolean
	 * 
	 * @param isAdd
	 * @return
	 */
	public boolean isChangeItems(boolean isAdd) {
		return false;
	}

	/**
	 * 函数作用:显示灯 void
	 * 
	 * @param i
	 */
	public void showLight(boolean isshow, int i) {
		// 1 绿灯， 2 黄灯 3 红灯
		if (isshow) {
			view_deng.clear();
			switch (i) {
			case 1:
				view_deng.addDrawable(R.drawable.light_green1, 300);
				view_deng.addDrawable(R.drawable.light_green2, 300);
				view_deng.play(false);
				break;
			case 2:
				view_deng.addDrawable(R.drawable.light_yellow1, 300);
				view_deng.addDrawable(R.drawable.light_yellow2, 300);
				view_deng.play(false);
				break;
			case 3:
				view_deng.addDrawable(R.drawable.light_red1, 300);
				view_deng.addDrawable(R.drawable.light_red2, 300);
				view_deng.play(false);
				break;
			}
			view_deng.setVisibility(View.VISIBLE);
		} else {
			view_deng.setVisibility(View.INVISIBLE);
		}
	}

	private void initView() {
		/*
		 * 主体布局
		 */
		layout_main = (RelativeLayout) findViewById(R.id.layout_main);
		/*
		 * 提示牌
		 */
		layout_hint = (RelativeLayout) findViewById(R.id.hint_layout);
		text_hint = (TextView) findViewById(R.id.text_hint);
		button_hint = (ImageView) findViewById(R.id.button_yes);
		// 初始化记分牌
		// 本节共计
		textview_total_count = (TextView) findViewById(R.id.textview_total_count);
		textview_total_count.setText("本节共计:");
		// 未完成
		textview_not_avi = (TextView) findViewById(R.id.textview_not_avi);
		textview_not_avi.setText("未完成:");
		// 未掌握
		textview_not_understand = (TextView) findViewById(R.id.textview_not_understand);
		textview_not_understand.setText("未掌握:");
		// 模糊
		textview_vague = (TextView) findViewById(R.id.textview_vague);
		textview_vague.setText("模    糊:" + num_puzzle);
		// 已掌握
		textview_understand = (TextView) findViewById(R.id.textview_understand);
		textview_understand.setText("已掌握:");
		// 引导文字
		intro_text = (TextView) findViewById(R.id.intro_text);
		// 大字
		text_dazi = (TextView) findViewById(R.id.text_dazi);
		// 图片
		view_image = (ImageView) findViewById(R.id.image_show);
		// 核对答案
		view_hedui = (GameView) findViewById(R.id.button_hedui);
		view_hedui.addDrawable(R.drawable.check_answer1, 1000);
		view_hedui.addDrawable(R.drawable.check_answer2, 1000);
		view_hedui.play(false);
		/*
		 * 背景
		 */
		layout_bg_screen = (LinearLayout) findViewById(R.id.bg_object_big);
		String file_imageString = ResourceLoader.getMajorBackImageUrl(index);
		File file = new File(file_imageString);
		if (file.exists()) {// 若该文件存在
			bmp_bg = BitmapFactory.decodeFile(file_imageString);
			layout_bg_screen.setBackgroundDrawable(new BitmapDrawable(bmp_bg));
		}
		flashBgLayout = (RelativeLayout) findViewById(R.id.bg_object_small);
		/*
		 * 灯
		 */
		view_deng = (GameView) findViewById(R.id.light);
		/*
		 * 返回按钮
		 */
		mBackView = (GameView) findViewById(R.id.button_back);
		mBackView.addDrawable(R.drawable.back_default, 800);
		mBackView.addDrawable(R.drawable.back_hot, 800);
		mBackView.play(false);

		/*
		 * 喇叭
		 */
		button_laba = (ImageButton) findViewById(R.id.button_laba);
		/*
		 * 跟踪布局
		 */
		jump_layout = (LinearLayout) findViewById(R.id.jump_layout);
		check_layout = (LinearLayout) findViewById(R.id.check_layout);
		/*
		 * 跟踪小人
		 */
		button_ok = (GameView) findViewById(R.id.check_good);
		button_puzzle = (GameView) findViewById(R.id.check_just);
		button_no = (GameView) findViewById(R.id.check_bad);

		button_ok.addDrawable(R.drawable.button_good1, 300);
		button_ok.addDrawable(R.drawable.button_good2, 300);

		button_puzzle.addDrawable(R.drawable.button_puzzle1, 200);
		button_puzzle.addDrawable(R.drawable.button_puzzle2, 200);
		button_puzzle.addDrawable(R.drawable.button_puzzle1, 200);

		button_no.addDrawable(R.drawable.button_bad1, 200);
		button_no.addDrawable(R.drawable.button_bad2, 200);
		button_no.addDrawable(R.drawable.button_bad3, 200);
		button_no.addDrawable(R.drawable.button_bad1, 200);

		/*
		 * 上下题
		 */
		button_prvious = (GameView) findViewById(R.id.button_last);
		button_next = (GameView) findViewById(R.id.button_next);

		button_prvious.addDrawable(R.drawable.button_previous, 200);
		button_prvious.addDrawable(R.drawable.button_previous2, 200);
		button_prvious.addDrawable(R.drawable.button_previous, 200);

		button_next.addDrawable(R.drawable.button_next, 200);
		button_next.addDrawable(R.drawable.button_next2, 200);
		button_next.addDrawable(R.drawable.button_next, 200);

	}

	/**
	 * 函数作用:根据#ff0000 获取颜色值 int
	 * 
	 * @param str
	 * @return
	 */
	public int getColor(String str) {
		return Integer.parseInt(str.substring(1), 16);
	}

	public ShiZiRememberView(Context context) {
		super(context);
		this.context = context;
	}

	public ShiZiRememberView(Context context, AttributeSet set) {
		super(context, set);
		this.context = context;
	}

	/**
	 * 视图变量
	 */
	/*
	 * 记分牌
	 */
	// 本节共计
	private TextView textview_total_count;
	// 未完成
	private TextView textview_not_avi;
	// 未掌握
	private TextView textview_not_understand;
	// 模糊
	private TextView textview_vague;
	// 已掌握
	private TextView textview_understand;

	// 引导文字
	private TextView intro_text;
	// 主体布局
	private RelativeLayout layout_main;

	// 大字
	private TextView text_dazi;
	// 图片
	private ImageView view_image;
	// 核对答案
	private GameView view_hedui;

	/*
	 * 跟踪布局
	 */
	private LinearLayout jump_layout;
	private LinearLayout check_layout;
	/*
	 * /* 返回按钮
	 */
	private GameView mBackView;

	private ImageButton button_laba;

	/*
	 * 灯
	 */
	private GameView view_deng;
	/*
	 * 跟踪小人
	 */
	private GameView button_ok;
	private GameView button_puzzle;
	private GameView button_no;
	/*
	 * 上下题
	 */
	private GameView button_prvious;
	private GameView button_next;
	/*
	 * 背景
	 */
	LinearLayout layout_bg_screen;
	RelativeLayout flashBgLayout;
	/*
	 * 提示的视图
	 */
	private RelativeLayout layout_hint;
	private TextView text_hint;
	private ImageView button_hint;

	/**
	 * 数据变量
	 */
	// 引导字
	ArrayList<String> text_guide = new ArrayList<String>();
	// 引导MP3
	ArrayList<String> mp3_guide = new ArrayList<String>();
	/*
	 * 大字列表
	 */
	private ArrayList<String> list_dazi = new ArrayList<String>();
	/*
	 * 图片列表
	 */
	private ArrayList<String> list_img = new ArrayList<String>();
	private ArrayList<Integer> list_img_width = new ArrayList<Integer>();
	private ArrayList<Integer> list_img_height = new ArrayList<Integer>();
	/**
	 * 控制变量
	 */
	// 当前题目
	// 当前的items下标
	int index_current_items = 0;
	// 诗句 背诵记录 数组，每个值包括:1,2，3 分别代表 没问题，有点模糊，完全不懂
	private int[] state_question;
	// 当前题号
	private int index_current_question = 0;
	// 总共字
	private int total_question = 0;
	// 未完成
	private int num_not_done;
	// 未掌握
	private int num_not_understand;
	// 模糊
	private int num_puzzle;
	// 掌握
	private int num_understand;

	private Context context;
	private String path;
	private String type;
	private int index;
	String bg;
	/*
	 * 选项是否刚被点击的变量
	 */
	/*
	 * 本次循环学习的题目
	 */
	private boolean circle_study[];
	/*
	 * 当前循环学习的题目总数（与total_question不一样）
	 */
	private int total_current_circle;
	/*
	 * 循环学习时，记录的已做题目数量
	 */
	private int number_done_circle = 0;

	// 记录循环学习时的状态数组
	private int[] state_circle_study1;
	private int[] state_circle_study2;

	// 学习状态 0 —— 普通 ， 1——重点复习 2——次重点复习
	private int studySate = 0;
	// 本节共计
	private int benjiegongji = 0;
	/*
	 * 是否是循环学习
	 */
	private boolean isCircleStudy = false;
	// 是否是浏览
	private boolean isReview = false;
	/*
	 * 是否学习完毕
	 */
	private boolean isLearnFinish = false;
	/*
	 * 是否第二次循环学习
	 */
	private boolean isFirstLearn = true;
	private boolean isFirstSection = true;
	ArrayList<TopicBean> list_record;
	/*
	 * 第一个导读音乐是否加载完毕
	 */
	private boolean isFirstMp3LoadDone = false;
	private Bitmap bmp_bg = null;
	private Bitmap bmp_bg_small = null;
	//学习显示的bitmap
	private Bitmap bmp_bg_show = null;
}
