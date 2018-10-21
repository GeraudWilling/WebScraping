package com.geraudwilling.webscraping.callable;

import java.util.List;
import java.util.concurrent.Callable;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.geraudwilling.webscraping.bean.ScrapingResult;
import com.geraudwilling.webscraping.util.PropertiesKey;
import com.geraudwilling.webscraping.util.PropertiesReader;

public class MainCallable implements Callable<ScrapingResult>{

	PropertiesReader propertiesReader = PropertiesReader.INSTANCE;
	
	public ScrapingResult call() throws Exception {
		String url = propertiesReader.getProperties(PropertiesKey.GET_ALL_DESK_URL);
		WebClient client = new WebClient();  
		client.getOptions().setCssEnabled(true);  
		client.getOptions().setJavaScriptEnabled(true);  
		client.getOptions().setThrowExceptionOnFailingStatusCode(false);
		client.getOptions().setThrowExceptionOnScriptError(false);
		
		//Land on main page for appointments
		HtmlPage page = client.getPage(url);
		List<HtmlRadioButtonInput> items = (List<HtmlRadioButtonInput>) page.getByXPath("//input[@type='radio']");
		
		//Iterate through all 'guichet', check them and then click on submit 
		for(HtmlRadioButtonInput item: items){
			// Check the radio
			item.setChecked(true);
			// Get the submit button
			HtmlElement button = (HtmlElement) page.getFirstByXPath("//input[@type='submit']");
			//Submit the choice
			page = button.click();
			//Check whether a new Rdv exists or not
			HtmlElement response = (HtmlElement) page
					.getFirstByXPath(propertiesReader.getProperties(PropertiesKey.TARGET_TEXT));
			
			if(response != null){
				System.out.println("null html elementnot found");
			}
		}
		System.out.println(items.size());

		return null;
	}

}
