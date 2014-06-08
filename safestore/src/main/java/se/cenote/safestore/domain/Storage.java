package se.cenote.safestore.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import se.cenote.safestore.AppContext;

public class Storage {
	
	private static final String FILE_NAME = ".safeStore";

	private File storeFile;
	
	public Storage(){
		storeFile = new File(System.getProperty("user.home"), FILE_NAME);
	}
	
	public boolean isInitialized() {
		return storeFile.exists();
	}
	
	public File getFile(){
		return storeFile;
	}

	public void store(List<Entry> entries, char[] pwd){
		
		if(!entries.isEmpty()){
			
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
			
			AppContext.getInstance().getApp().getCrypoManager().storeSecure(buffer.toString(), storeFile, pwd);
		}
	}
	
	public List<Entry> load(char[] pwd) throws IllegalArgumentException{
		
		List<Entry> list = new ArrayList<Entry>();
		
		if(storeFile.exists()){
			
			String data = null;
			try{
				data = AppContext.getInstance().getApp().getCrypoManager().readSecure(storeFile, pwd);
				
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
