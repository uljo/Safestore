package se.cenote.safestore.domain.crypto;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class CryptoManager {
	
	private PBE_Crypto_AES crypto;


	public static void main(String[] args) {
		
		test1();
	}
	

	public CryptoManager(){
		crypto = new PBE_Crypto_AES();
	}
	
	public List<String> getCryptos(){
		List<String> cryptos = new ArrayList<String>();
		cryptos.add(PBE_Crypto_AES.ALGO);
		cryptos.add(PBE_Crypto_DES.ALGO);
		return cryptos;
	}
	
	public String getSelectedCrypto(){
		return PBE_Crypto_AES.ALGO;
	}
	
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
	
	private static void test1(){
		
		String text = "test1\nuffe\nabc123\ntest2\nuffe\nabc321";
		char[] pwd = "123".toCharArray();
		
		File file = new File("batman.cry");
		
		CryptoManager cryptoManager = new CryptoManager();
		
		cryptoManager.storeSecure(text, file, pwd);
		
		String encrypted = cryptoManager.readSecure(file, pwd);
		System.out.println(">> " + encrypted);
	}
	
	public void storeSecure(String text, File file, char[] pwd){
		
		EncryptedData data = crypto.encrypt(text, pwd);
		writeToFile(file, data);
	}
	
	public String readSecure(File file, char[] pwd) throws IllegalArgumentException{
		EncryptedData data = readFromFile(file);
		
		return crypto.decrypt(data, pwd);
	}
	
	public static EncryptedData readFromFile(File file) {
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

	public static void writeToFile(File file, EncryptedData data) {
		
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
