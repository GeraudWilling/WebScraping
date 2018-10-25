package com.geraudwilling.webscraping.pages;

import java.io.IOException;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.geraudwilling.webscraping.util.PropertiesKey;
import com.geraudwilling.webscraping.util.PropertiesReader;

public class ChoicePage {
	
	private PropertiesReader propertiesReader = PropertiesReader.INSTANCE;
	private String url;
	private HtmlPage page;
	private List<HtmlRadioButtonInput> radios;
	/**
	 * Class constructor
	 * @param webClient, the web browser.
	 * @throws IOException if property file is not found.
	 */
	public ChoicePage(WebClient webClient) throws IOException {
		this.url = propertiesReader.getProperties(PropertiesKey.GET_ALL_DESK_URL);
		this.page = webClient.getPage(this.url);
	}
	
	
	
	/**
	 * @return the html page.
	 */
	public HtmlPage getHtmlPage() {
		return this.page;
	}
	
	/**
	 * @return the list of radio button on the web page.
	 */
	@SuppressWarnings("unchecked")
	public List<HtmlRadioButtonInput> getRadioButtonList(){
		return this.radios=(List<HtmlRadioButtonInput>) 
				this.page.getByXPath("//input[@type='radio']");

	}
	
	/**
	 * @return the submit button to submit guichet choice.
	 */
	public HtmlElement getSubmitButton(){
		return this.page.getFirstByXPath("//input[@type='submit']");
	}
	
	/**
	 * @param newWindow: weither the click should hold the ctrl key or not.
	 * @return the page of rendez-vous exist or not
	 * @throws IOException
	 */
	public HtmlPage submitPage(boolean newWindow) throws IOException{
		return getSubmitButton().click(false,newWindow,false);//boolean shiftKey,boolean ctrlKey,boolean altKey
	}
	
	/**
	 * @param position, the position of radio item to check
	 */
	public void checkButton(int position){
		this.radios.get(position).setChecked(true);
	}

}
