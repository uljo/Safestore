package se.cenote.safestore.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import se.cenote.safestore.domain.crypto.CryptoManager;

public class Storage {
	
	//private char[] pwd;
	private CryptoManager crypto;
	
	public Storage(){
		crypto = new CryptoManager();
		//pwd = "abc123".toCharArray();
	}
	
	private File getFile(){
		File file = new File(System.getProperty("user.home"), ".safeStore");
		return file;
	}
	
	public void store(List<Entry> entries, char[] pwd){
		
		if(!entries.isEmpty()){
			File file = new File(System.getProperty("user.home"), ".safeStore");
			
			StringBuilder buffer = new StringBuilder();
			for(Entry entry : entries){
				buffer.append(entry.getName());
				buffer.append("¤");
				buffer.append(entry.getUsername());
				buffer.append("¤");
				buffer.append(new String(entry.getPwd()));
				buffer.append("¤");
				buffer.append(new String(entry.getComments()));
				buffer.append("¤");
				buffer.append(new String(CalendarUtil.formatDateTime(entry.getCreated())));
				buffer.append("¤");
				buffer.append(new String(CalendarUtil.formatDateTime(entry.getEdited())));
				buffer.append("#");
			}
			buffer.replace(buffer.length()-1, buffer.length(), "");
			
			crypto.storeSecure(buffer.toString(), file, pwd);
		}
	}
	
	public List<Entry> load(char[] pwd) throws IllegalArgumentException{
		
		List<Entry> list = new ArrayList<Entry>();
		
		File file = getFile();
		if(file.exists()){
			
			String data = null;
			try{
				data = crypto.readSecure(file, pwd);
				
				if(data != null){
					
					String[] posts = data.split("#");
					for(String post : posts){
						
						String[] parts = post.split("¤");
						String name = parts[0];
						String username = parts[1];
						String password = parts[2];
						String comments = parts[3];
						
						String createdText = null;
						if(parts.length > 4)
							createdText = parts[4];
						
						String editedText = null;
						if(parts.length > 5)
							editedText = parts[5];
						
						Entry entry = new Entry(name, username, password.getBytes(), comments);
						entry.setCreated(CalendarUtil.parse(createdText));
						entry.setEdited(CalendarUtil.parse(editedText));
						list.add(entry);
					}
				}
			}
			catch(Exception e){
				System.out.println("[load] Caught " + e);
				throw new IllegalArgumentException(e);
			}
			
		}
		
		return list;
	}

}
