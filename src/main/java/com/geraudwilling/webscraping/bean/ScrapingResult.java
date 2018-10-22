/**
 * 
 */
package com.geraudwilling.webscraping.bean;

import java.time.LocalDateTime;

/**
 * @author geraudwilling
 *
 */
public class ScrapingResult {
	
	//Wether a rdv is available for this desk or not
	boolean isRdvAvailable;
	
	//The desk number
	int number;
	
	//The time of the check
	LocalDateTime date;
	
	//Page content
	String content;

	public boolean isRdvAvailable() {
		return isRdvAvailable;
	}

	public void setRdvAvailable(boolean isRdvAvailable) {
		this.isRdvAvailable = isRdvAvailable;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 
	 */
	public ScrapingResult() {
	}

	public ScrapingResult(boolean isRdvAvailable, int number, LocalDateTime date,String content) {
		super();
		this.isRdvAvailable = isRdvAvailable;
		this.number = number;
		this.date = date;
		this.content = content;
	}
	
	

}
