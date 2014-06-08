package se.cenote.safestore;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.cenote.safestore.domain.CalendarUtil;
import se.cenote.safestore.domain.Entry;
import se.cenote.safestore.domain.Settings;
import se.cenote.safestore.domain.Storage;
import se.cenote.safestore.domain.crypto.CryptoManager;
import se.cenote.safestore.ui.SafeStoreGui;

public class SafeStoreApp {
	
	private CryptoManager cryptoMgr;

	private Map<String, Entry> entryMap;
	
	private Settings settings;
	private Storage storage;
	
	private char[] pwd;
	
	private LocalDateTime loginTime; 
	
	public static void main(String[] args) {
		AppContext.getInstance().getApp();
        SafeStoreGui.show();
    }
	
	public SafeStoreApp(){
		
		cryptoMgr = new CryptoManager();
		
		entryMap = new HashMap<String, Entry>();
		
		storage = new Storage();
	}
	
	public boolean isFirstTime(){
		return !storage.isInitialized();
	}
	
	public void login(char[] pwd) throws IllegalArgumentException{
		
		this.pwd = pwd;
		
		try{
			loadEntries(pwd);
		
			settings = new Settings(cryptoMgr.getCryptos());
			settings.setSeletedCrypto(cryptoMgr.getSelectedCrypto());
			settings.setPath(storage.getFile().getAbsolutePath());
			
			loginTime = LocalDateTime.now();
			
			System.out.println("[login] loginTime: " + loginTime);
		}
		catch(Exception e){
			System.out.println("[login] ERROR - caught: " + e);
			throw e;
		}
	}
	
	public void logout(){
		if(loginTime != null){
			
			storeEntries();

			long secs = Duration.between(loginTime, LocalDateTime.now()).getSeconds();
			System.out.println("[logout] session time: " + CalendarUtil.formatDuration(secs) + " minutes.");
			
			loginTime = null;
		}
	}
	
	public CryptoManager getCrypoManager(){
		return cryptoMgr;
	}
	
	public Settings getSettings(){
		return settings;
	}
	
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
	
	public List<String> getNames(){
		List<String> list = new ArrayList<String>(entryMap.keySet());
		Collections.sort(list);
		return list;
	}
	
	public Entry getEntry(String name){
		return entryMap.get(name);
	}
	
	public Entry add(String name, String user, byte[] pwd, String comments){
		Entry entry = new Entry(name, user, pwd, comments);
		entry.setCreated(LocalDateTime.now());
		entryMap.put(name, entry);
		
		System.out.println("[add] Added " + entry + " as name: " + name);
		
		return entry;
	}
	
	private void storeEntries(){
		List<Entry> list = new ArrayList<Entry>(entryMap.values());
		storage.store(list, pwd);
		System.out.println("[storeEntries] stored " + list.size() + " entities.");
	}

	
	private void loadEntries(char[] pwd) throws IllegalArgumentException{
		List<Entry> entries = storage.load(pwd);
		for(Entry entry : entries){
			entryMap.put(entry.getName(), entry);
		}
		System.out.println("[loadEntries] loaded " + entries.size() + " entities.");
	}

	
}
