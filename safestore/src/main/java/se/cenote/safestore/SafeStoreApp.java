package se.cenote.safestore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.cenote.safestore.domain.Entry;
import se.cenote.safestore.domain.Storage;
import se.cenote.safestore.ui.SafeStoreGui;

public class SafeStoreApp {

	private Map<String, Entry> entryMap;
	
	private Storage storage;
	
	private char[] pwd;
	
	public static void main(String[] args) {
		AppContext.getInstance().getApp();
        SafeStoreGui.show();
    }
	
	public SafeStoreApp(){
		
		entryMap = new HashMap<String, Entry>();
		
		storage = new Storage();
	}
	
	private void load(char[] pwd) throws IllegalArgumentException{
		List<Entry> entries = storage.load(pwd);
		for(Entry entry : entries){
			entryMap.put(entry.getName(), entry);
		}
		System.out.println("[init] loaded " + entries.size() + " entities.");
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
	
	public void close(){
		List<Entry> list = new ArrayList<Entry>(entryMap.values());
		storage.store(list, pwd);
		System.out.println("[init] stored " + list.size() + " entities.");
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
		
		return entry;
	}

	public void login(char[] pwd) throws IllegalArgumentException{
		load(pwd);
		this.pwd = pwd;
	}
	
}
