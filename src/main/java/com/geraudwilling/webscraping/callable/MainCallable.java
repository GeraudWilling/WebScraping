package com.geraudwilling.webscraping.callable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.geraudwilling.webscraping.bean.ScrapingResult;
import com.geraudwilling.webscraping.pages.ChoicePage;
import com.geraudwilling.webscraping.pages.ResultPage;
import com.geraudwilling.webscraping.util.PropertiesReader;

public class MainCallable implements Callable<List<ScrapingResult>>{

	PropertiesReader propertiesReader = PropertiesReader.INSTANCE;
	static final Logger logger = Logger.getLogger(MainCallable.class);


	public List<ScrapingResult> call() throws Exception {
		List<ScrapingResult> results = new ArrayList<>();
		logger.info("Starting callable ...");
		try(WebClient client = new WebClient(BrowserVersion.CHROME)) {
			client.setAjaxController(new NicelyResynchronizingAjaxController()); 
			client.getOptions().setCssEnabled(false);  
			client.getOptions().setJavaScriptEnabled(false);  
			client.getOptions().setThrowExceptionOnFailingStatusCode(false);
			client.getOptions().setThrowExceptionOnScriptError(false);
					
			//Land on main page for appointments
			ChoicePage choicePage = new ChoicePage(client);
			List<HtmlRadioButtonInput> items = choicePage.getRadioButtonList();
			
			//Iterate through all 'guichet', check them and then click on submit 
			for(int i=1; i<= items.size(); i++){
				logger.debug("Before checking input ...");
				// Check the radio
				choicePage.checkButton(i-1);
				logger.debug("Before clicking on submit ...");
				//Submit the choice
				HtmlPage rdvExistPage = choicePage.submitPage(true);
				//Check whether a new Rdv exists or not
				ResultPage resultPage=(new ResultPage(rdvExistPage));	
				//Wait for page loading
				resultPage.waitForPageLoadingComplete();
				//Chek results
				if(!resultPage.rdvExist()){
					//If page contains previous text, then a rdv doesn't exist for this Guichet
					logger.info("Rendez-vous not found for Guichet "+ i + " on date : "+LocalDateTime.now() 
					+ " page content: " + resultPage.getResultFormContentAsText());
					results.add(new ScrapingResult(false,i, LocalDateTime.now(),resultPage.getResultFormContentAsXml()));
				}else {
					logger.info("!!!****Rendez-vous found for Guichet "+ i + " on date : "+LocalDateTime.now());
					results.add(new ScrapingResult(true,i, LocalDateTime.now(),resultPage.getResultFormContentAsXml()));
				}
			}
		}
		//Close the stream
		PropertiesReader.INSTANCE.closeInput();
		return results;
	}

}
