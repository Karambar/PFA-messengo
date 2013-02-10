package com.messengo.tablette.bean;

import java.io.Serializable;

public class Message implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2900535537414869385L;
	
	private boolean mine;
	private String	msg;
	private String	date;
	/**
	 * @return the mine
	 */
	public boolean isMine() {
		return mine;
	}
	/**
	 * @param mine the mine to set
	 */
	public void setMine(boolean mine) {
		this.mine = mine;
	}
	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}
	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	
}
