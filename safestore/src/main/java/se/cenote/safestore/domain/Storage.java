package se.cenote.safestore.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import se.cenote.safestore.AppContext;
import se.cenote.safestore.domain.crypto.InvalidKeyLengthException;

public class Storage {
	
	private static final String FILE_NAME = ".safeStore";
	
	private static final String BAR = "\\|";
	private static final String NL = "\n";

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

	public void store(List<Entry> entries, char[] pwd) throws InvalidKeyLengthException{
		
		if(!entries.isEmpty()){
			
			StringBuilder buffer = new StringBuilder();
			for(Entry entry : entries){
				buffer.append(entry.getName());
				buffer.append("\t");
				buffer.append(entry.getUsername());
				buffer.append("\t");
				buffer.append(new String(entry.getPwd()));
				buffer.append("\t");
				buffer.append(new String(removeNL(entry.getComments())));
				buffer.append("\t");
				buffer.append(new String(CalendarUtil.formatDateTime(entry.getCreated())));
				buffer.append("\t");
				buffer.append(new String(CalendarUtil.formatDateTime(entry.getEdited())));
				buffer.append("\n");
			}
			buffer.replace(buffer.length()-1, buffer.length(), "");
			
			AppContext.getInstance().getApp().getCrypoManager().storeSecure(buffer.toString(), storeFile, pwd);
		}
	}
	
	private String removeNL(String text) {
		return text.replaceAll(NL, BAR);
	}
	
	private String addNL(String text) {
		return text.replaceAll(BAR, NL);
	}
	

	public List<Entry> load(char[] pwd) throws IllegalArgumentException{
		
		List<Entry> list = new ArrayList<Entry>();
		
		if(storeFile.exists()){
			
			debug("[load] Trying open file: " + storeFile.getAbsolutePath());
			
			String data = null;
			try{
				data = AppContext.getInstance().getApp().getCrypoManager().readSecure(storeFile, pwd);
				
				if(data != null){
					
					String[] posts = data.split("\n");
					
					//System.out.println("[load] retrieved: " + Arrays.asList(posts));
					
					for(String post : posts){
						
						String[] parts = post.split("\t");
						String name = parts[0];
						String username = parts[1];
						String password = parts[2];
						String comments = addNL(parts[3]);
						
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
				error("[load] Caught " + e);
				throw new IllegalArgumentException(e);
			}
			
		}
		else{
			debug("[load] There is no file: " + storeFile.getAbsolutePath());
		}
		
		return list;
	}
	
	private static void error(String text){
		System.out.println(text);
	}
	
	private static void debug(String text){
		System.out.println(text);
	}

}
