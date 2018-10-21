/**
 * 
 */
package com.geraudwilling.webscraping.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.geraudwilling.webscraping.bean.ScrapingResult;
import com.geraudwilling.webscraping.callable.MainCallable;
import com.geraudwilling.webscraping.util.PropertiesKey;
import com.geraudwilling.webscraping.util.PropertiesReader;

import rx.subjects.ReplaySubject;

/**
 * @author geraudwilling
 * The Entry point of te application
 *
 */
public class WebScrapingMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//TODO Add Custom Logger
		
		ScheduledExecutorService scheduledExecutorService =
		        Executors.newScheduledThreadPool(5);
		MainCallable mainCallable = new MainCallable();
		
		try {
		    // Turn the Callable into a Runnable that adds the last result to the stream of results
	        ReplaySubject<ScrapingResult> results = ReplaySubject.create();
	        Runnable runnable = () -> {
	            try {
	            	ScrapingResult result = mainCallable.call();
	                results.onNext(result);
	            } catch (Exception e) {
	                // Needed since Callable can throw an exception, but Runnable cannot
	            }
	        };
	        // Periodically run the Runnable
			scheduledExecutorService.scheduleAtFixedRate(
					runnable,
					0L,
					Long.parseLong(PropertiesReader.INSTANCE.getProperties(PropertiesKey.SCHEDULING_INTERVAL)), 
					TimeUnit.MINUTES);
			// Handling the results as they arrive
	        results.forEach(result -> System.out.println("Result: " + result));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
