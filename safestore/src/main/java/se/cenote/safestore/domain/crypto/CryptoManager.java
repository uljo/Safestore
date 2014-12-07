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

public class CryptoManager {
	
	private static Integer[] keyLengths = {128, 196, 256};
	
	private static final int DES_ITERATION_COUNT = 12;
	
	private static final String UTF8 = "UTF-8";
	
	private boolean strongChiper;
	
	private PBE_Crypto cryptoAES;
	private PBE_Crypto cryptoDES;
	
	private String selectedCryptoName;
	
	private int selectedKeyLength;
	private String selectedEncoding = UTF8;

	public static void main(String[] args) {
		
		test1();
	}
	
	public CryptoManager(){
		
		strongChiper = CryptoManager.isAESKeyLengthSupported(keyLengths[2]);
		
		selectedKeyLength = strongChiper ? keyLengths[2] : keyLengths[0];
		
		try{
			cryptoAES = new PBE_Crypto_AES(selectedKeyLength, selectedEncoding);
			cryptoDES = new PBE_Crypto_DES(DES_ITERATION_COUNT, selectedEncoding);
			
			selectedCryptoName = PBE_Crypto_AES.ALGO;
		}
		catch(InvalidKeyLengthException impossible){}
	}

	public CryptoManager(int keyLength, String encoding) throws InvalidKeyLengthException{

		if(!CryptoManager.isAESKeyLengthSupported(keyLength)){
			throw new InvalidKeyLengthException(keyLength);
		}
		
		selectedKeyLength = keyLength;
				
		cryptoAES = new PBE_Crypto_AES(selectedKeyLength, selectedEncoding);
		cryptoDES = new PBE_Crypto_DES(DES_ITERATION_COUNT, selectedEncoding);
		
		selectedCryptoName = PBE_Crypto_AES.ALGO;
	}
	
	public List<String> getCryptoNames(){
		List<String> cryptos = new ArrayList<String>();
		cryptos.add(PBE_Crypto_AES.ALGO);
		cryptos.add(PBE_Crypto_DES.ALGO);
		return cryptos;
	}
	
	public String getSelectedCryptoName(){
		return selectedCryptoName;
	}
	
	public List<Integer> getKeyLengths(){
		if(strongChiper)
			return Arrays.asList(keyLengths);
		else
			return Arrays.asList(keyLengths[0]);
	}
	
	public int getSelectedKeyLength(){
		return selectedKeyLength;
	}
	
	public void setSelectedKeyLength(int keyLength){
		this.selectedKeyLength = keyLength;
	}
	
	public String getSelectedEncoding(){
		return selectedEncoding;
	}
	
	public void setSelectedEncoding(String encoding){
		this.selectedEncoding = encoding;
	}
	
	/*
	public void test0(){
		
		String text = "test1\nuffe\nabc123\ntest2\nuffe\nabc321";
		char[] pwd = "123".toCharArray();
		
		System.out.println("Text: " + text + ", pwd: " + new String(pwd));
	
		
		EncryptedData data = crypto.encrypt(text, pwd);
		System.out.println("Encrypted[1]: " + data.getEncryptedBase64());
		
		File file = new File("batman.cry");
		writeToFile(file, data);
		
		EncryptedData data2 = readFromFile(file);
		System.out.println("Encrypted[2]: " + data2.getEncryptedBase64());
		
		String text2 = crypto.decrypt(data2, pwd);
		
		System.out.println(">>" + text2);
	}
	*/
	
	private static void test1(){
		
		int keyLength = 196;
		String encoding = "UTF-8";
		
		String text = "test1\nuffe\nabc123\ntest2\nuffe\nabc321";
		char[] pwd = "123".toCharArray();
		
		File file = new File("batman.cry");
		
		CryptoManager cryptoManager;
		try {
			cryptoManager = new CryptoManager(keyLength, encoding);
		
			cryptoManager.storeSecure(text, file, pwd);
			
			String encrypted = cryptoManager.readSecure(file, pwd);
			System.out.println(">> " + encrypted);
		
		} catch (InvalidKeyLengthException e) {
			System.out.println("Error - " + e.getMessage());
		}
	}
	
	public void storeSecure(String text, File file, char[] pwd) throws InvalidKeyLengthException{
		
		PBE_Crypto crypto = getSelectedCrypto();
		
		EncryptedData data = crypto.encrypt(text, pwd);
		writeToFile(file, data);
	}
	
	public String readSecure(File file, char[] pwd) throws IllegalArgumentException{
		EncryptedData data = readFromFile(file);
		
		PBE_Crypto crypto = getSelectedCrypto();
		
		return crypto.decrypt(data, pwd);
	}
	
	
	public static boolean isAESKeyLengthSupported(int keyLength){
		
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
		System.out.println("[isAESKeyLengthSupported] keyLength=" + keyLength + ", supported=" + supported);
		return supported;
	}
	
	private PBE_Crypto getSelectedCrypto(){
		PBE_Crypto crypto = null;
		if(PBE_Crypto_AES.ALGO.equals(getSelectedCryptoName())){
			crypto = cryptoAES;
		}
		else{
			crypto = cryptoDES;
		}
		return crypto;
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
