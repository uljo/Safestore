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


	public static void main(String[] args) {
		
		test1();
	}
	
	public static void test0(){
		
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
		
		String text2 = cryptoManager.decrypt(data2.getEncryptedBytes(), data2.getIv(), data2.getSalt(), pwd);
		
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
		
		EncryptedData data = encrypt(text, pwd);
		writeToFile(file, data);
	}
	
	public String readSecure(File file, char[] pwd){
		EncryptedData data = readFromFile(file);
		
		return decrypt(data.getEncryptedBytes(), data.getIv(), data.getSalt(), pwd);
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

	public CryptoManager(){
		
	}
	
	public EncryptedData encrypt(String text, char[] pwd){
		EncryptedData data = null;
		
		try{
			
			byte[] salt = new byte[8];
			new SecureRandom().nextBytes(salt);
			
			SecretKey secretKey = generateSecretKey(pwd, salt);
			
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			AlgorithmParameters params = cipher.getParameters();
			byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
			byte[] encryptedData = cipher.doFinal(text.getBytes("UTF-8"));
			
			data = new EncryptedData(encryptedData, iv, salt);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return data;
	}
	
	public String decrypt(byte[] encryptedText, byte[] iv, byte[] salt, char[] pwd){
		
		byte[] decrypted = null;
		
		try{
			if(encryptedText != null){
				SecretKey secretKey = generateSecretKey(pwd, salt);
				
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
				decrypted = cipher.doFinal(encryptedText);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return decrypted != null ? new String(decrypted) : null;
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
