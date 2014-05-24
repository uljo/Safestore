package se.cenote.safestore.domain.crypto;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoManager {
	
	private byte[] salt;
	private byte[] iv;

	public static void main(String[] args) {
		
		String text = "test1\nuffe\nabc123\ntest2\nuffe\nabc321";
		char[] pwd = "123".toCharArray();
		
		System.out.println("Text: " + text + ", pwd: " + new String(pwd));
		
		CryptoManager cryptoManager = new CryptoManager();
		
		EncryptedData data = cryptoManager.encrypt(text, pwd);
		System.out.println("Encrypted[1]: " + data.getEncryptedBase64());
		
		File file = new File("batman.cry");
		writeToFile(file, data);
		
		EncryptedData data2 = readFromFile(file);
		System.out.println("Encrypted[2]: " + data2.getEncryptedBase64());
		
		String text2 = cryptoManager.decrypt(data2.getEncryptedBytes(), data2.getIv(), pwd);
		
		System.out.println(">>" + text2);
	}
	
	private void test(){
		
		String text = "test1\nuffe\nabc123\ntest2\nuffe\nabc321";
		char[] pwd = "123".toCharArray();
		
		File file = new File("batman.cry");
		
		storeSecure(text, file, pwd);
		
		String encrypted = readSecure(file, pwd);
	}
	
	public void storeSecure(String text, File file, char[] pwd){
		CryptoManager cryptoManager = new CryptoManager();
		EncryptedData data = cryptoManager.encrypt(text, pwd);
		writeToFile(file, data);
	}
	
	public String readSecure(File file, char[] pwd){
		EncryptedData data = readFromFile(file);
		
		CryptoManager cryptoManager = new CryptoManager();
		return cryptoManager.decrypt(data.getEncryptedBytes(), data.getIv(), pwd);
	}
	
	public static EncryptedData readFromFile(File file) {
		EncryptedData data = null;
		
		try{
			Path path = Paths.get(file.getAbsolutePath());
			byte[] arr = Files.readAllBytes(path);
			byte[] iv = Arrays.copyOfRange(arr, 0, 16);
			byte[] encryptedBytes = Arrays.copyOfRange(arr, 16, arr.length);
			data = new EncryptedData(encryptedBytes, iv);
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
	    	Files.write(path, data.getEncryptedBytes(), StandardOpenOption.APPEND);
	    }
	    catch(Exception e){
	    	e.printStackTrace();
	    }
	}

	public CryptoManager(){
		salt = new byte[8];
		new SecureRandom().nextBytes(salt);
	}
	
	public EncryptedData encrypt(String text, char[] pwd){
		EncryptedData data = null;
		
		try{
			
			SecretKey secretKey = generateSecretKey(pwd, salt);
			
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			AlgorithmParameters params = cipher.getParameters();
			iv = params.getParameterSpec(IvParameterSpec.class).getIV();
			byte[] encryptedData = cipher.doFinal(text.getBytes("UTF-8"));
			
			data = new EncryptedData(encryptedData, iv);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return data;
	}
	
	public String decrypt(byte[] encryptedText, byte[] iv, char[] pwd){
		
		byte[] decrypted = null;
		
		try{
			SecretKey secretKey = generateSecretKey(pwd, salt);
			
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
			decrypted = cipher.doFinal(encryptedText);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return new String(decrypted);
	}
	
	
	private static SecretKey generateSecretKey(char[] pwd, byte[] salt){
		SecretKey secretKey = null;
		
		try{
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(pwd, salt, 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			
			secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return secretKey;
	}

	public static class EncryptedData{
		
		private byte[] encryptedBytes;
		private byte[] iv;
		
		private String encryptedBase64;
		
		public EncryptedData(byte[] encryptedBytes, byte[] iv) {
			this.encryptedBytes = encryptedBytes;
			this.iv = iv;
			
			encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);
		}
		public byte[] getEncryptedBytes() {
			return encryptedBytes;
		}
		public byte[] getIv() {
			return iv;
		}
		public String getEncryptedBase64(){
			return encryptedBase64;
		}
	}

}
