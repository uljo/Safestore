package se.cenote.safestore.domain.crypto;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CryptoManager {
	
	private static Integer[] keyLengths = {128, 196, 256};
	
	private static final int DES_ITERATION_COUNT = 12;
	
	private static final String UTF8 = "UTF-8";
	
	private static Logger logger = LoggerFactory.getLogger(CryptoManager.class);

	private static boolean strongChiper = CryptoManager.isAESKeyLengthSupported(keyLengths[2]);
	
	private static String defaultCrypto = PBE_Crypto_AES.ALGO;
	
	private PBE_Crypto crypto;
	

	private static boolean isAESKeyLengthSupported(int keyLength){
		
		boolean supported = false;
		String encoding = UTF8;
		
		String text = "test1\nuffe\nabc123\ntest2\nuffe\nabc321";
		char[] pwd = "123".toCharArray();
		
		try{

			PBE_Crypto crypto = new PBE_Crypto_AES(keyLength, encoding);
			EncryptedData data = crypto.encrypt(text, pwd);
			
			String encrypted = crypto.decrypt(data, pwd);
			
			supported = encrypted.equals(text);
		}
		catch(InvalidKeyLengthException e){
			supported = false;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		logger.debug("[isAESKeyLengthSupported] keyLength=" + keyLength + ", supported=" + supported);
		return supported;
	}

	
	/**
	 * Retrieve list of all known crypto names.
	 * <ul>
	 * <li>AES</li>
	 * <li>DES</li>
	 * </ul>
	 * 
	 * @return list of all known crypto names
	 */
	public static List<String> getCryptoNames(){
		List<String> cryptos = new ArrayList<String>();
		cryptos.add(PBE_Crypto_AES.ALGO);
		cryptos.add(PBE_Crypto_DES.ALGO);
		return cryptos;
	}
	
	
	/**
	 * Retrieve all known key lengths supported by this current JRE.
	 * <ul>
	 * <li>128 - alwways</li>
	 * <li>196 - if supported</li>
	 * <li>256 - if supported</li>
	 * </ul>
	 * @return
	 */
	public static List<Integer> getKeyLengths(){

		if(strongChiper)
			return Arrays.asList(keyLengths);
		else
			return Arrays.asList(keyLengths[0]);
	}

	/**
	 * Retrieve the default crypto.
	 * 
	 * @return name of default crypto
	 */
	public static String getDefaultCrypto(){
		return defaultCrypto;
	}
	
	public static int getDefaultKeyLength(){
		
		int keyLength = strongChiper ? keyLengths[2] : keyLengths[0];
		return keyLength;
	}
	

	public CryptoManager(String cryptoName, int keyLength) throws InvalidKeyLengthException{

		if(!isValidKeyLength(keyLength)){
			throw new InvalidKeyLengthException(keyLength);
		}
				
		crypto = PBE_Crypto_AES.ALGO.equals(cryptoName) ? new PBE_Crypto_AES(keyLength, UTF8) : new PBE_Crypto_DES(DES_ITERATION_COUNT, UTF8);
	}
	
	private boolean isValidKeyLength(int keyLength){
		return strongChiper || keyLength == keyLengths[0];
	}


	public void storeSecure(String text, File file, char[] pwd) throws InvalidKeyLengthException{
		
		EncryptedData data = crypto.encrypt(text, pwd);
		writeToFile(file, data);
	}
	
	public String readSecure(File file, char[] pwd) throws IllegalArgumentException{
		EncryptedData data = readFromFile(file);
		
		return crypto.decrypt(data, pwd);
	}
	
	private static EncryptedData readFromFile(File file) {
		EncryptedData data = null;
		
		try{
			Path path = Paths.get(file.getAbsolutePath());
			byte[] arr = Files.readAllBytes(path);
			
			byte[] iv = Arrays.copyOfRange(arr, 0, 16);
			byte[] salt = Arrays.copyOfRange(arr, 16, 24);
			byte[] encryptedBytes = Arrays.copyOfRange(arr, 24, arr.length);
			data = new EncryptedData(encryptedBytes, iv, salt);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return data;
	}

	private static void writeToFile(File file, EncryptedData data) {
		
	    try {
	    	Path path = Paths.get(file.getAbsolutePath());
	    	Files.write(path, data.getIv());
	    	Files.write(path, data.getSalt(), StandardOpenOption.APPEND);
	    	Files.write(path, data.getEncryptedBytes(), StandardOpenOption.APPEND);
	    }
	    catch(Exception e){
	    	e.printStackTrace();
	    }
	}

	public static class EncryptedData{
		
		private byte[] encryptedBytes;
		private byte[] iv;
		private byte[] salt;
		
		private String encryptedBase64;
		
		public EncryptedData(byte[] encryptedBytes, byte[] iv, byte[] salt) {
			this.encryptedBytes = encryptedBytes;
			this.iv = iv;
			this.salt = salt;
			
			encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);
		}
		public byte[] getEncryptedBytes() {
			return encryptedBytes;
		}
		public byte[] getIv() {
			return iv;
		}
		public byte[] getSalt() {
			return salt;
		}
		public String getEncryptedBase64(){
			return encryptedBase64;
		}
	}

}
