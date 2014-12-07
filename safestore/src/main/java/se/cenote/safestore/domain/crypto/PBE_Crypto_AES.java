package se.cenote.safestore.domain.crypto;

import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import se.cenote.safestore.domain.crypto.CryptoManager.EncryptedData;

public final class PBE_Crypto_AES implements PBE_Crypto{
	
	private static final int SALT_LENGTH = 8;
	
	public static final String ALGO = "PBKDF2WithHmacSHA1";
	
	public static final String BLOCK_MODE = "AES/CBC/PKCS5Padding";
	
	public static final int ITERATIONS = 65536;		// wifi-router = 4096 but better with > 10 000
	
	private int keyLength;
	private String encoding = "UTF-8";
	
	public PBE_Crypto_AES(int keyLength, String encoding) throws InvalidKeyLengthException{
		
		// Check validity of key length
		if (keyLength != 128 && keyLength != 192 && keyLength != 256) {
			throw new InvalidKeyLengthException(keyLength);
		}
		
		this.keyLength = keyLength;
		this.encoding = encoding;
	}
	
	public String getName(){
		return ALGO;
	}
	
	public int getKeyLength(){
		return keyLength;
	}
	
	public String getEncoding(){
		return encoding;
	}
	
	public EncryptedData encrypt(String text, char[] pwd) throws InvalidKeyLengthException{
		EncryptedData data = null;
		
		try{
			
			byte[] salt = new byte[SALT_LENGTH];
			new SecureRandom().nextBytes(salt);
			
			SecretKey secretKey = generateSecretKey(pwd, salt, keyLength);

			Cipher cipher = Cipher.getInstance(BLOCK_MODE);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			AlgorithmParameters params = cipher.getParameters();
			byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
			byte[] encryptedData = cipher.doFinal(text.getBytes(encoding));
			
			data = new EncryptedData(encryptedData, iv, salt);
		}
		catch(InvalidKeyException e){
			throw new InvalidKeyLengthException(keyLength);
		} 
		catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidParameterSpecException
				| IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
			throw new RuntimeException(e);
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
				SecretKey secretKey = generateSecretKey(pwd, salt, keyLength);
				
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
	 * @param keyLength - use 128, 196, 256. NOTE: Key length > 128 Need updated judication policy files in JVM
	 * @return
	 */
	private static SecretKey generateSecretKey(char[] pwd, byte[] salt, int keyLength){
		SecretKey secretKey = null;
		
		try{
			PBEKeySpec keySpec = new PBEKeySpec(pwd, salt, ITERATIONS, keyLength);
			SecretKey tmp = SecretKeyFactory.getInstance(ALGO).generateSecret(keySpec);
			keySpec.clearPassword();
			
			secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return secretKey;
	}

}
