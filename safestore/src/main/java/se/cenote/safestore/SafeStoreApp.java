/**
 * SafeStore - A simple desktop tool for storing login credentials.
 *
 * Copyright (C) 2014 Ulf M Johannesson.
 *
 * This file is part of SafeStore.
 * 
 * SafeStore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SafeStore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SafeStore. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package se.cenote.safestore;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.util.StatusPrinter;
import se.cenote.safestore.domain.Entry;
import se.cenote.safestore.domain.Settings;
import se.cenote.safestore.domain.Storage;
import se.cenote.safestore.domain.crypto.InvalidKeyLengthException;
import se.cenote.safestore.ui.SafeStoreGui;
import se.cenote.util.calendar.CalendarUtil;

/**
 * Main Application class.
 * <p>
 * Methods for accessing and updating login credentials for different systems.
 * <p>
 * Holds reference to Settings and Storage.
 * 
 * @author uffe
 *
 */
public class SafeStoreApp {

	private static final String STOREFILE_NAME = ".safeStore";
	
	private Logger logger = LoggerFactory.getLogger(SafeStoreApp.class);
	
	private Map<String, Entry> entryMap;
	
	private Settings settings;
	private Storage storage;
	
	private char[] pwd;
	
	private LocalDateTime loginTime; 
	
	/**
	 * Main method for starting application.
	 * <p>
	 * Will bring up the main gui.
	 * 
	 * @param args - not used
	 */
	public static void main(String[] args) {

		
		initLog();
		Properties props = System.getProperties();
		props.setProperty("SAFESTORE_DIR", System.getProperty("user.home"));
		
		AppContext.getInstance().getApp();
        SafeStoreGui.show();
    }
	
	
	public SafeStoreApp(){

		entryMap = new HashMap<String, Entry>();
		
		try{
			settings = new Settings(getDefaultStorageFile());
			
			storage = new Storage(settings);
		}
		catch(Exception e){
			logger.error("Caught ex: " + e);
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * Check if this is first time app is running.
	 * 
	 * @return true if first time, else false.
	 */
	public boolean isFirstTime(){
		return !storage.isInitialized();
	}
	
	/**
	 * Try to login user with specified password.
	 * 
	 * @param pwd specified user password
	 * @throws IllegalArgumentException if authentication fails
	 */
	public void login(char[] pwd) throws IllegalArgumentException{
		
		this.pwd = pwd;
		
		try{
			loadEntries(pwd);
			
			loginTime = LocalDateTime.now();
			
			logger.debug("[login] loginTime: " + loginTime);
		}
		catch(Exception e){
			logger.error("[login] ERROR - caught: " + e);
			throw e;
		}
	}
	
	/**
	 * Log out current user.
	 * 
	 */
	public void logout(){
		if(loginTime != null){
			
			storeEntries();

			long secs = Duration.between(loginTime, LocalDateTime.now()).getSeconds();
			logger.debug("[logout] session time: " + CalendarUtil.formatDuration(secs) + " minutes.");
			
			loginTime = null;
		}
	}
	
	public Settings getSettings(){
		return settings;
	}
	
	public void changeStorage(File file, String crypto, int keyLength) {
		boolean test = true;
		if(!test){
			storage.update(file, crypto, keyLength, pwd);
			
			settings.setStorageFile(file);
			settings.setSeletedCrypto(crypto);
			settings.setKeyLength(keyLength);
		}
		logger.debug("Changing storage file from: " + settings.getStorageFile() + " to: " + storage.getFile() + ", crypto: " + crypto + ", keyLen: " + keyLength);
	}

	
	/*
	public List<String> getSuggestions(String text){
		List<String> list = new ArrayList<String>();
		
		List<Entry> entries = storage.load(pwd);
		if(entries != null){
			for(Entry entry : entries){
				String value = entry.getUsername();
				if(value.startsWith(text)){
					list.add(value);
				}
			}
		}
		return list;
	}
	*/
	
	/**
	 * Retrieve list of all login entry names.
	 * 
	 * @return list of entry names
	 */
	public List<String> getNames(){
		List<String> list = new ArrayList<String>(entryMap.keySet());
		Collections.sort(list);
		return list;
	}
	
	/**
	 * Retrieve specified entry.
	 * 
	 * @param name name of entry
	 * @return matching entry, or Null if no one found.
	 */
	public Entry getEntry(String name){
		return entryMap.get(name);
	}
	
	/**
	 * Create a new login entry with specified values.
	 * 
	 * @param name entry name
	 * @param user user name for entry
	 * @param pwd password for entry
	 * @param comments any comments
	 * @return created entry instance
	 */
	public Entry add(String name, String user, byte[] pwd, String comments){
		Entry entry = new Entry(name, user, pwd, comments);
		entry.setCreated(LocalDateTime.now());
		entryMap.put(name, entry);
		
		logger.debug("[add] Added " + entry + " as name: " + name);
		
		return entry;
	}
	
	/**
	 * Update an entry by replacing it with provided entry.
	 * 
	 * @param name name of entry
	 * @param entry new entry instance
	 */
	public void update(String name, Entry entry){
		if(entry != null && entryMap.containsKey(name)){
			entryMap.remove(name);
			entryMap.put(entry.getName(), entry);
			
			logger.debug("[update] Updated " + entry);
		}
	}
	
	/**
	 * Persist all current entries to file.
	 * 
	 * @throws InvalidKeyLengthException if JRE security policy can't handle long key. 
	 */
	private void storeEntries(){
		List<Entry> list = new ArrayList<Entry>(entryMap.values());
		try {
			storage.store(list, pwd);
			logger.debug("[storeEntries] stored " + list.size() + " entities.");
		} 
		catch (InvalidKeyLengthException e) {
			System.out.println("[storeEntries] invalid key length: " + e.getMessage());
		}
		
	}

	/**
	 * Load entries from persistent file.
	 * 
	 * @param pwd central password
	 * @throws IllegalArgumentException if password don't validate.
	 */
	private void loadEntries(char[] pwd) throws IllegalArgumentException{
		List<Entry> entries = storage.load(pwd);
		for(Entry entry : entries){
			entryMap.put(entry.getName(), entry);
		}
		logger.debug("[loadEntries] loaded " + entries.size() + " entities.");
	}
	
	
	private static File getDefaultStorageFile(){
		String home = System.getProperty("user.home");
		return new File(home, STOREFILE_NAME);
	}

	private static void initLog() {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

	    FileAppender fileAppender = new FileAppender();
	    fileAppender.setContext(loggerContext);
	    fileAppender.setName("timestamp");
	    // set the file name
	    fileAppender.setFile("log/uffe.log");

	    PatternLayoutEncoder encoder = new PatternLayoutEncoder();
	    encoder.setContext(loggerContext);
	    encoder.setPattern("%r %thread %level - %msg%n");
	    encoder.start();

	    fileAppender.setEncoder(encoder);
	    fileAppender.start();

	    // attach the rolling file appender to the logger of your choice
	    Logger logbackLogger = loggerContext.getLogger("Main");
	    ((ch.qos.logback.classic.Logger) logbackLogger).addAppender(fileAppender);

	    // OPTIONAL: print logback internal status messages
	    StatusPrinter.print(loggerContext);

	    // log something
	    logbackLogger.debug("hello");
	  }
}
