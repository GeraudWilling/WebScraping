package com.geraudwilling.webscraping.pages;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.geraudwilling.webscraping.util.PropertiesKey;
import com.geraudwilling.webscraping.util.PropertiesReader;

public class ResultPage {
	
	private PropertiesReader propertiesReader = PropertiesReader.INSTANCE;
	private HtmlPage page;
	private String targetText;
	private DomNode resultForm;
	static final Logger logger = Logger.getLogger(ResultPage.class);


	/**Constructor
	 * @param page the page to search for rdv.
	 * @throws IOException
	 */
	public ResultPage(HtmlPage page) throws IOException {
		this.page = page;
	}
	
	/**
	 * @return the searched text from properties files.
	 * @throws IOException
	 */
	public String getTargetText() throws IOException{
		return this.targetText=propertiesReader.getProperties(PropertiesKey.TARGET_TEXT);
	}
	
	/**
	 * @return the searched text from properties files.
	 * @throws IOException
	 */
	public String getTargetTextOk() throws IOException{
		return this.targetText=propertiesReader.getProperties(PropertiesKey.TARGET_TEXT_OK);
	}
	
	/**
	 * @return true if rdv exist and false otherwise.
	 * @throws IOException 
	 */
	public boolean rdvExist() throws IOException{
		HtmlElement elementKO  = this.page.getFirstByXPath("//*[contains(text(),'" +getTargetText() +"')]");
		HtmlElement elementOK  = this.page.getFirstByXPath(getTargetTextOk());
		if(elementKO instanceof HtmlForm){
			//If target.ko text found, then rdv doesn't exists.
			this.resultForm = (HtmlForm) elementKO;
			if(elementKO != null && elementKO.isDisplayed()){
				return false;
			}
		}else if(elementOK instanceof HtmlSubmitInput && elementOK != null && elementOK.isDisplayed()){
			//If target.ok text found, then rdv doesn't exists.
			this.resultForm = (HtmlSubmitInput) elementOK;
			return true;
		}
		// if no target text not found, then return false.
		return false;
	}
	
	/**
	 * Wait for page to complete loading.
	 * @throws InterruptedException if page.wait() fails.
	 */
	public void waitForPageLoadingComplete() throws InterruptedException{
		HtmlElement element = this.page.getFirstByXPath("//form[@id='FormBookingCreate']") ;
		boolean aCondition = element instanceof HtmlForm && element != null;
		int tries = 5;  // Amount of tries to avoid infinite loop
		while (tries > 0 && aCondition) {
		    tries--;
		    synchronized(page) {
		        page.wait(2000L);  // How often to check
		    }
		}
	}

	/**
	 * @return the form content as text.
	 */
	public String getResultFormContentAsText() {
		return resultForm == null? "" : resultForm.asText() ;
	}
	
	/**
	 * @return the form content as Xml.
	 */
	public String getResultFormContentAsXml() {
		return resultForm == null? "" : resultForm.asXml() ;
	}



	
	
	
}
