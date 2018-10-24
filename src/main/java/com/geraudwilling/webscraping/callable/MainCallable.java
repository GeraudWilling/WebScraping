package com.geraudwilling.webscraping.callable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.geraudwilling.webscraping.bean.ScrapingResult;
import com.geraudwilling.webscraping.util.PropertiesKey;
import com.geraudwilling.webscraping.util.PropertiesReader;

public class MainCallable implements Callable<List<ScrapingResult>>{

	PropertiesReader propertiesReader = PropertiesReader.INSTANCE;
	static final Logger logger = Logger.getLogger(MainCallable.class);


	public List<ScrapingResult> call() throws Exception {
		String url = propertiesReader.getProperties(PropertiesKey.GET_ALL_DESK_URL);
		List<ScrapingResult> results = new ArrayList<>();
		
		try(WebClient client = new WebClient(BrowserVersion.FIREFOX_38)) {
			client.setAjaxController(new NicelyResynchronizingAjaxController()); 
			client.getOptions().setCssEnabled(false);  
			client.getOptions().setJavaScriptEnabled(false);  
			client.getOptions().setThrowExceptionOnFailingStatusCode(false);
			client.getOptions().setThrowExceptionOnScriptError(false);
			
			
			logger.info("Starting callable ...");
			
			//Land on main page for appointments
			HtmlPage landingPage = client.getPage(url);
			List<HtmlRadioButtonInput> items = (List<HtmlRadioButtonInput>) landingPage.getByXPath("//input[@type='radio']");
			
			//Iterate through all 'guichet', check them and then click on submit 
			for(int i=1; i<= items.size(); i++){
				logger.debug("Before checking input ...");
				// Check the radio
				items.get(i-1).setChecked(true);
				// Get the submit button
				HtmlElement button = landingPage.getFirstByXPath("//input[@type='submit']");
				logger.debug("Before clicking on submit ...");
				//Submit the choice
				HtmlPage rdvExistPage = button.click();
				//Wait for js to complete for 5 seconds max
				client.waitForBackgroundJavaScript(5000L);
				//Check whether a new Rdv exists or not
				String targetText= propertiesReader.getProperties(PropertiesKey.TARGET_TEXT);
				HtmlForm response = rdvExistPage
						.getFirstByXPath("//*[contains(text(),'" +targetText +"')]");
				
				if(response != null && response.isDisplayed() && response.asText().contains(targetText)){
					//If page contains previous text, then a rdv doesn't exist for this Guichet
					logger.info("Rendez-vous not found for Guichet "+ i + " on date : "+LocalDateTime.now() 
					+ " page content: " + response.asText());
					results.add(new ScrapingResult(false,i, LocalDateTime.now(),response.asXml()));

				}else {
					logger.info("!!!****Rendez-vous found for Guichet "+ i + " on date : "+LocalDateTime.now());
					results.add(new ScrapingResult(true,i, LocalDateTime.now(),""));
				}
			}
		}
		//Close the stream
		PropertiesReader.INSTANCE.closeInput();
		return results;
	}

}
