package com.messengo.tablette.bean;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = -6497961359542728699L;
	
	private String idGoogle;
	private String eMail;
	private String	name;
	private String	shortName;
	private String	birthday;
	private boolean	male;
	private String	locale;
	private	String	picture;
	private String	passphrase;
	
	/**
	 * @return the idGoogle
	 */
	public String getIdGoogle() {
		return idGoogle;
	}
	/**
	 * @param idGoogle the idGoogle to set
	 */
	public void setIdGoogle(String idGoogle) {
		this.idGoogle = idGoogle;
	}
	/**
	 * @return the eMail
	 */
	public String geteMail() {
		return eMail;
	}
	/**
	 * @param eMail the eMail to set
	 */
	public void seteMail(String eMail) {
		this.eMail = eMail;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return shortName;
	}
	/**
	 * @param shortName the shortName to set
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	/**
	 * @return the birthday
	 */
	public String getBirthday() {
		return birthday;
	}
	/**
	 * @param birthday the birthday to set
	 */
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	/**
	 * @return the male
	 */
	public boolean isMale() {
		return male;
	}
	/**
	 * @param male the male to set
	 */
	public void setMale(boolean male) {
		this.male = male;
	}
	/**
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}
	/**
	 * @param locale the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}
	/**
	 * @return the picture
	 */
	public String getPicture() {
		return picture;
	}
	/**
	 * @param picture the picture to set
	 */
	public void setPicture(String picture) {
		this.picture = picture;
	}
	/**
	 * @return the passphrase
	 */
	public String getPassphrase() {
		return passphrase;
	}
	/**
	 * @param passphrase the passphrase to set
	 */
	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}
	

}
