package se.cenote.safestore.domain;

import java.io.File;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.cenote.safestore.SafeStoreApp;
import se.cenote.safestore.domain.crypto.CryptoManager;

public class Settings {
	
	private static final String STORAGE_FILE_PATH = "storage.file.path";
	private static final String CRYPTO_SELECTED = "crypto.name";
	private static final String CRYPTO_KEY_LENGTH = "crypto.key.length";
	
	private Logger logger = LoggerFactory.getLogger(SafeStoreApp.class);
	
	private File storageFile;
	private String storageFilePath;
	
	private String selectedCrypto;
	
	private int keyLength;
	
	public Settings(File defaultFile){
		
		Preferences prefs = Preferences.userNodeForPackage(se.cenote.safestore.domain.Settings.class);
		if(prefs != null){
			storageFilePath = prefs.get(STORAGE_FILE_PATH, null);
			if(storageFilePath == null){
				storageFilePath = defaultFile.getAbsolutePath();
				prefs.put(STORAGE_FILE_PATH, storageFilePath);
				
				logger.info("No user prefered storageFile. Will use default:" + storageFile.getAbsolutePath());
			}
			storageFile = new File(storageFilePath);
			
			selectedCrypto = prefs.get(CRYPTO_SELECTED, null);
			if(selectedCrypto == null){
				selectedCrypto = CryptoManager.getDefaultCrypto();
				prefs.put(CRYPTO_SELECTED, selectedCrypto);
				logger.info("No user prefered crypto. Will use default:" + selectedCrypto);
			}
			
			keyLength = prefs.getInt(CRYPTO_KEY_LENGTH, 0);
			if(keyLength == 0){
				keyLength = CryptoManager.getDefaultKeyLength();
				prefs.putInt(CRYPTO_KEY_LENGTH, keyLength);
				logger.info("No user prefered key length. Will use default:" + keyLength);
			}
			
			logger.debug("Retrieved user preferences:: storageFile: " + storageFile.getAbsolutePath() + ", crypto: " + selectedCrypto + ", keyLenght: " + keyLength);
		}
		else{
			logger.error("ERROR - Can't look up user preferences!");
			throw new RuntimeException("Can't look up user preferences!");
		}
	}
	
	public File getStorageFile(){
		return storageFile;
	}
	
	public void setStorageFile(File file){
		this.storageFile = file;
	}
	
	public int getKeyLength(){
		return keyLength;
	}
	
	public void setKeyLength(int keyLength){
		this.keyLength = keyLength;
	}
	
	public String getSeletedCrypto(){
		return selectedCrypto;
	}

	public void setSeletedCrypto(String crypto) {
		this.selectedCrypto = crypto;
	}

}
