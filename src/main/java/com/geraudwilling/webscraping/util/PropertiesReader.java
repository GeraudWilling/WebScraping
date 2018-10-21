package com.geraudwilling.webscraping.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * Singleton class to read app property file.
 */
public enum PropertiesReader {
	
	INSTANCE;
	
	Properties  prop = new Properties();
	InputStream input = null;
	final String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	final String appConfigPath = rootPath + "app.properties";
	
	private PropertiesReader() {}

 
    /** 
     * Read a properties from the config file
     * @param key the key to read in property file
     * @throws IOException if property file not found
     * @throws FileNotFoundException */
    public String getProperties(String key) throws FileNotFoundException, IOException {
    	if(input == null){
    		prop.load(new FileInputStream(appConfigPath));
    	}
        return prop.getProperty(key);
    }
}


