package se.cenote.safestore.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import se.cenote.safestore.domain.crypto.CryptoManager;

public class Storage {
	
	private char[] pwd;
	private CryptoManager crypto;
	
	public Storage(){
		crypto = new CryptoManager();
		pwd = "abc123".toCharArray();
	}
	
	private File getFile(){
		File file = new File(System.getProperty("user.home"), ".safeStore");
		return file;
	}
	
	public void store(List<Entry> entries){
		
		if(!entries.isEmpty()){
			File file = new File(System.getProperty("user.home"), ".safeStore");
			
			StringBuilder buffer = new StringBuilder();
			for(Entry entry : entries){
				buffer.append(entry.getName());
				buffer.append("\n");
				buffer.append(entry.getUsername());
				buffer.append("\n");
				buffer.append(new String(entry.getPwd()));
				buffer.append("\n");
			}
			buffer.replace(buffer.length()-1, buffer.length(), "");
			
			crypto.storeSecure(buffer.toString(), file, pwd);
		}
	}
	
	public List<Entry> load(){
		
		List<Entry> list = new ArrayList<Entry>();
		
		File file = getFile();
		if(file.exists()){
			
			String data = crypto.readSecure(file, pwd);
			if(data != null){
				String[] parts = data.split("\n");
				int i = 0;
				while(i < parts.length-2){
					String name = parts[i++];
					String username = parts[i++];
					String password = parts[i++];
					
					Entry entry = new Entry(name, username, password.getBytes());
					list.add(entry);
				}
			}
		}
		
		return list;
	}

}
