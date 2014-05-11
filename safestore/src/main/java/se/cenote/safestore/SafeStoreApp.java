package se.cenote.safestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.cenote.safestore.domain.Entry;
import se.cenote.safestore.domain.Storage;
import se.cenote.safestore.ui.SafeStoreGui;

public class SafeStoreApp {

	private List<Entry> entries;
	private Map<String, Entry> entryMap;
	
	private Storage storage;
	
	public static void main(String[] args) {
		AppContext.getInstance().getApp();
        SafeStoreGui.show();
    }
	
	public SafeStoreApp(){
		
		entryMap = new HashMap<String, Entry>();
		
		storage = new Storage();
		entries = storage.load();
		for(Entry entry : entries){
			entryMap.put(entry.getName(), entry);
		}
		System.out.println("[init] loaded " + entries.size() + " entities.");
	}
	
	public List<String> getSuggestions(String text){
		List<String> list = new ArrayList<String>();
		
		for(Entry entry : entries){
			String value = entry.getUsername();
			if(value.startsWith(text)){
				list.add(value);
			}
		}
		return list;
	}
	
	public void close(){
		List<Entry> list = new ArrayList<Entry>(entryMap.values());
		storage.store(list);
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
	
	public Entry add(String name, String user, byte[] pwd){
		Entry entry = new Entry(name, user, pwd);
		entryMap.put(name, entry);
		
		return entry;
	}
	
}
