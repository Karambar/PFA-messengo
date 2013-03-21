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
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + (mine ? 1231 : 1237);
		result = prime * result + ((msg == null) ? 0 : msg.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (mine != other.mine)
			return false;
		if (msg == null) {
			if (other.msg != null)
				return false;
		} else if (!msg.equals(other.msg))
			return false;
		return true;
	}
}
