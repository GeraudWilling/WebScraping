package com.geraudwilling.webscraping.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/*
 * Singleton class to read app property file.
 */
public enum PropertiesReader {
	
	INSTANCE;
	
	Properties  prop = null;
	InputStream input = null;
	final String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	final String appConfigPath = rootPath + "app.properties";
	private String salt;
	
	private PropertiesReader() {}

 
    /** 
     * Read a properties from the config file
     * @param key the key to read in property file
     * @throws IOException if property file not found
     */
    public String getProperties(String key) throws IOException {
    	if(prop == null){
    		FileInputStream file = new FileInputStream(appConfigPath);
    		prop = new Properties();
    		prop.load(file);
    	}
        return prop.getProperty(key);
    }
    
    public int getIntProperty(String key) throws IOException {
        return Integer.parseInt(getProperties(key).trim());
    }
    
    /**
     * @param propertyKey, the key to decrypt from properties file
     * @return the decrypted value
     * @throws org.apache.commons.configuration.ConfigurationException if encryption algorithm fail to setup.
     * @throws IOException if file not found or not accessible.
     */
    public  String decryptPropertyValue(String propertyKey) throws  org.apache.commons.configuration.ConfigurationException, IOException {
       String encryptedPropertyValue = getProperties(propertyKey);
       StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
	   encryptor.setPassword(PropertiesReader.INSTANCE.getSalt());
       String decryptedPropertyValue = encryptor.decrypt(encryptedPropertyValue);
       return decryptedPropertyValue;
   }
    
    public String getSalt() {
    	return salt;
    }
    
    public void setSalt(String salt) {
    	this.salt = salt;
    }


	public InputStream getInput() {
		return input;
	}


	public void closeInput() throws IOException {
		if(this.input != null) {
			this.input.close();
		}
	}
}


