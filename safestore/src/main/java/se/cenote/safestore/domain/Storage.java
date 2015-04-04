package se.cenote.safestore.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.cenote.safestore.domain.crypto.CryptoManager;
import se.cenote.safestore.domain.crypto.InvalidKeyLengthException;
import se.cenote.util.calendar.CalendarUtil;

/**
 * Class responsible for reading from/to storage file.
 * 
 * @author uffe
 *
 */
public class Storage {
	
	private static final String BAR = "\\|";
	private static final String NL = "\n";
	
	private Logger logger = LoggerFactory.getLogger(Storage.class);

	private File storeFile;
	private CryptoManager cryptoMgr;
	
	/**
	 * Constructor with specified path for storage file.
	 * 
	 * @param file specified storage file.
	 * @throws InvalidKeyLengthException 
	 */
	public Storage(Settings settings) throws InvalidKeyLengthException{
		storeFile = settings.getStorageFile();
		
		cryptoMgr = new CryptoManager(settings.getSeletedCrypto(), settings.getKeyLength());
	}
	
	/**
	 * Check if store has been initialized.
	 * 
	 * @return true if store has been initialized, else false,
	 */
	public boolean isInitialized() {
		return storeFile.exists();
	}
	
	/**
	 * Retrieve the storage file.
	 * 
	 * @return
	 */
	public File getFile(){
		return storeFile;
	}
	
	public void update(File file, String crypto, int keyLength, char[] pwd) {
		try {
			
			List<Entry> entries = load(pwd);
			
			storeFile = file;
			cryptoMgr = new CryptoManager(crypto, keyLength);
			
			store(entries, pwd);
		} 
		catch(InvalidKeyLengthException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Store specified entries to storage file.
	 * 
	 * @param entries specified entries to be stored
	 * @param pwd password for opening the storage file
	 * @throws InvalidKeyLengthException if JDK security policy has wrong key length.
	 */
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
			
			cryptoMgr.storeSecure(buffer.toString(), storeFile, pwd);
		}
	}
	
	/**
	 * Retrieve all stored entries from storage file.
	 * 
	 * @param pwd password for opening the storage file
	 * @return
	 * @throws IllegalArgumentException
	 */
	public List<Entry> load(char[] pwd) throws IllegalArgumentException{
		
		List<Entry> list = new ArrayList<Entry>();
		
		if(storeFile.exists()){
			
			logger.debug("[load] Trying open file: " + storeFile.getAbsolutePath());
			
			String data = null;
			try{
				data = cryptoMgr.readSecure(storeFile, pwd);
				
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
				logger.error("[load] Caught " + e);
				throw new IllegalArgumentException(e);
			}
			
		}
		else{
			logger.debug("[load] There is no file: " + storeFile.getAbsolutePath());
		}
		
		return list;
	}
	
	/**
	 * Replace any occurrences of new-line characters in specified text string with bar-characters.
	 * 
	 * @param text specified text string
	 * @return text string without new-line characters.
	 */
	private String removeNL(String text) {
		return text.replaceAll(NL, BAR);
	}
	
	/**
	 * Replace any occurrences of bar-characters in specified text string with new-line-characters.
	 * 
	 * @param text specified text string
	 * @return text string without new-line characters.
	 */
	private String addNL(String text) {
		return text.replaceAll(BAR, NL);
	}

	
}
