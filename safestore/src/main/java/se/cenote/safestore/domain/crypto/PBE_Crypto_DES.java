package se.cenote.safestore.domain.crypto;

import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import se.cenote.safestore.domain.crypto.CryptoManager.EncryptedData;

public final class PBE_Crypto_DES implements PBE_Crypto{
	
	public static final String ALGO = "PBEWithMD5AndTripleDES";
	
	private static final int SALT_LENGTH = 8;
	
	private int iterationCount = 12;
	private String encoding = "UTF-8";
	
	public PBE_Crypto_DES(int iterationCount, String encoding){
		this.iterationCount = iterationCount;
		this.encoding = encoding;
	}
	
	public String getName(){
		return ALGO;
	}
	
	public String getEncoding(){
		return encoding;
	}
	
	public int getIterationCount(){
		return iterationCount;
	}
	
	public EncryptedData encrypt(String text, char[] pwd){
		EncryptedData data = null;
		
		try{
			
			byte[] salt = new byte[SALT_LENGTH];
			new SecureRandom().nextBytes(salt);
			
			SecretKey secretKey = generateSecretKey(pwd, salt, iterationCount);
			
			Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
			byte[] encryptedData = cipher.doFinal(text.getBytes(encoding));
			
			data = new EncryptedData(encryptedData, null, salt);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return data;
	}
	
	public String decrypt(EncryptedData data, char[] pwd){
		
		byte[] decrypted = null;
		
		byte[] encryptedText = data.getEncryptedBytes();
		//byte[] iv = null;
		byte[] salt = data.getSalt();
		
		try{
			if(encryptedText != null){
				
				SecretKey secretKey = generateSecretKey(pwd, salt, iterationCount);
				
				Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
				AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
				cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
				decrypted = cipher.doFinal(encryptedText);
			}
		}
		catch(Exception e){
			throw new IllegalArgumentException(e);
		}
		return decrypted != null ? new String(decrypted) : null;
	}
	
	
	private static SecretKey generateSecretKey(char[] pwd, byte[] salt, int iterationCount){
		SecretKey secretKey = null;
		
		try{
			KeySpec keySpec = new PBEKeySpec(pwd, salt, iterationCount);
			secretKey = SecretKeyFactory.getInstance(ALGO).generateSecret(keySpec);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return secretKey;
	}

}
