package se.cenote.safestore.domain;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class CryptoManager {
	
	private static final String UTF8 = "UTF-8";
	
	private static final String CRYPTO = "PBEWithMD5AndDES";
	
	private static final char[] PASSWORD = "enfldsgbnlsngdlksdsgm".toCharArray();
	
    private static final byte[] SALT = {
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    };

    public static void main(String[] args) throws Exception {
        
    	String originalPassword = "secret";
    	
        System.out.println("Original password: " + originalPassword);
        
        String encryptedPassword = encrypt(originalPassword);
        System.out.println("Encrypted password: " + encryptedPassword);
        
        String decryptedPassword = decryptToString(encryptedPassword);
        System.out.println("Decrypted password: " + decryptedPassword);
    }
    
    public static String encrypt(String text) throws GeneralSecurityException, UnsupportedEncodingException {
        return encrypt(text.getBytes(UTF8));
    }

    public static String encrypt(byte[] bytes) throws GeneralSecurityException, UnsupportedEncodingException {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        return base64Encode(cipher.doFinal(bytes));
    }
    
    public static String decryptToString(String data) throws GeneralSecurityException, IOException {
        return new String(decrypt(data), UTF8);
    }

    public static byte[] decrypt(String data) throws GeneralSecurityException, IOException {
    	Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
        return cipher.doFinal(base64Decode(data));
    }
    
    private static Cipher getCipher(int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException{
    	SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(CRYPTO);
    	SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher cipher = Cipher.getInstance(CRYPTO);
        cipher.init(mode, key, new PBEParameterSpec(SALT, 20));
        return cipher;
    }
    
    /*
    private static String getCipher(){
    	SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    	KeySpec spec = new PBEKeySpec(PASSWORD, SALT, 65536, 256);
    	SecretKey tmp = factory.generateSecret(spec);
    	SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
    	
    	// Encrypt the message.
    	Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    	cipher.init(Cipher.ENCRYPT_MODE, secret);
    	AlgorithmParameters params = cipher.getParameters();
    	byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
    	byte[] ciphertext = cipher.doFinal("Hello, World!".getBytes(UTF8));
    	
    	// Decrypt the message, given derived key and initialization vector.
    	Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    	cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
    	String plaintext = new String(cipher.doFinal(ciphertext), UTF8);
    	System.out.println(plaintext);
    }
    */

    private static String base64Encode(byte[] bytes) {
    	return Base64.getEncoder().encodeToString(bytes);
    }

    private static byte[] base64Decode(String data) throws IOException {
        return Base64.getDecoder().decode(data);
    }
}
