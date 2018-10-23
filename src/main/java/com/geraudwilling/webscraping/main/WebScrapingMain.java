/**
 * 
 */
package com.geraudwilling.webscraping.main;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import com.geraudwilling.webscraping.bean.ScrapingResult;
import com.geraudwilling.webscraping.callable.MainCallable;
import com.geraudwilling.webscraping.util.AppHelper;
import com.geraudwilling.webscraping.util.PropertiesKey;
import com.geraudwilling.webscraping.util.PropertiesReader;

import rx.subjects.ReplaySubject;

/**
 * @author geraudwilling
 * The Entry point of te application
 *
 */
public class WebScrapingMain {

	static final Logger logger = Logger.getLogger(WebScrapingMain.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ScheduledExecutorService scheduledExecutorService =
		        Executors.newScheduledThreadPool(1);
		MainCallable mainCallable = new MainCallable();

		logger.debug("Starting application ...");
		
		if(args == null || args.length < 1) {
			logger.fatal("Missing arg...Stopping app...");
			return;
		}else {
			PropertiesReader.INSTANCE.setSalt(args[0]);
		}
		
		try {
		    // Turn the Callable into a Runnable that adds the last result to the stream of results
	        ReplaySubject<List<ScrapingResult>> results = ReplaySubject.create();
	        Runnable runnable = () -> {
	            try {
	            	List<ScrapingResult> result = mainCallable.call();
	                results.onNext(result);
	            } catch (Exception e) {
	    			logger.error("Fatal error occurs withing the callable thread, stopping the thread...",e);
	            }
	        };
	        // Periodically run the Runnable
			scheduledExecutorService.scheduleAtFixedRate(
					runnable,
					0L,
					Long.parseLong(PropertiesReader.INSTANCE.getProperties(PropertiesKey.SCHEDULING_INTERVAL)), 
					TimeUnit.MINUTES);
			// Handling the results as they arrive
	        results.forEach((List<ScrapingResult> result) -> {
	        	int guichetNumber = AppHelper.isRdvAvailable(result);
	        	if(guichetNumber != 0) {
	        		logger.info("Ending application... At least one Rdv found for Guichet numero" + guichetNumber );
	        		try {
						AppHelper.sendMail(result);
					} catch (MessagingException | ConfigurationException | IOException e) {
						logger.error("Fatal error occurs when sending mail",e);
					}
	        	}else {
	        		logger.info("Ending application... No rdv found ");
	        		try {
						AppHelper.sendMail(result);
					} catch (MessagingException | ConfigurationException | IOException e) {
						logger.error("Fatal error occurs when sending mail",e);
					}
	        	}
	        });
		} catch (NumberFormatException | IOException e) {
			logger.error("Fatal error occurs, stopping application...",e);
		}
		
		
	}
	
	
	

}
