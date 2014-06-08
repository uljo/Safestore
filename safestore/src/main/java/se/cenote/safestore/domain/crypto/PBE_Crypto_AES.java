package se.cenote.safestore.domain.crypto;

import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import se.cenote.safestore.domain.crypto.CryptoManager.EncryptedData;

public final class PBE_Crypto_AES implements PBE_Crypto{
	
	public static final String ALGO = "PBKDF2WithHmacSHA1";
	
	public static final String BLOCK_MODE = "AES/CBC/PKCS5Padding";
	
	public String getName(){
		return ALGO;
	}
	
	public EncryptedData encrypt(String text, char[] pwd){
		EncryptedData data = null;
		
		try{
			
			byte[] salt = new byte[8];
			new SecureRandom().nextBytes(salt);
			
			SecretKey secretKey = generateSecretKey(pwd, salt);

			Cipher cipher = Cipher.getInstance(BLOCK_MODE);
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
	
	public String decrypt(EncryptedData data, char[] pwd){
		
		byte[] decrypted = null;
		
		byte[] encryptedText = data.getEncryptedBytes();
		byte[] iv = data.getIv();
		byte[] salt = data.getSalt();
		
		try{
			if(encryptedText != null){
				SecretKey secretKey = generateSecretKey(pwd, salt);
				
				Cipher cipher = Cipher.getInstance(BLOCK_MODE);
				cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
				decrypted = cipher.doFinal(encryptedText);
			}
		}
		catch(Exception e){
			System.out.println("[decrypt] Caught " + e);
			throw new IllegalArgumentException(e);
		}
		return decrypted != null ? new String(decrypted) : null;
	}
	
	/**
	 * 
	 * @param pwd - entropy should be greater than the key length
	 * @param salt - entropy should be greater than the key length, eg. 128bit key -> 128 bit random or 22 random alfanum
	 * @return
	 */
	private static SecretKey generateSecretKey(char[] pwd, byte[] salt){
		SecretKey secretKey = null;
		
		int iterations = 65536;		// wifi-router = 4096 but better with > 10 000
		int keyLength = 256; 		// Use 128, 192 or 256
		
		try{
			PBEKeySpec keySpec = new PBEKeySpec(pwd, salt, iterations, keyLength);
			SecretKey tmp = SecretKeyFactory.getInstance(ALGO).generateSecret(keySpec);
			keySpec.clearPassword();
			
			secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
		
			//System.out.println("[generateSecretKey] algo=" + secretKey.getAlgorithm());

		}
		catch(Exception e){
			e.printStackTrace();
		}
		return secretKey;
	}

}
