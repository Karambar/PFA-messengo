package com.messengo.tablette.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class Conversation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4891267380525041373L;

	private Integer				userId;
	private String				userName;
	private String				userTel;
	private ArrayList<Message>	conversation;
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Conversation [userId=" + userId + ", userName=" + userName
				+ ", userTel=" + userTel + ", conversation=" + conversation
				+ "]";
	}
	/**
	 * @return the userId
	 */
	public Integer getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the conversation
	 */
	public ArrayList<Message> getConversation() {
		return conversation;
	}
	/**
	 * @param conversation the conversation to set
	 */
	public void setConversation(ArrayList<Message> conversation) {
		this.conversation = conversation;
	}
	/**
	 * @return the userTel
	 */
	public String getUserTel() {
		return userTel;
	}
	/**
	 * @param userTel the userTel to set
	 */
	public void setUserTel(String userTel) {
		this.userTel = userTel;
	}


}
