package se.cenote.safestore.domain.crypto;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class KeyManager {
	
	public static void main(String[] args){
		
		String text = "Hello world!";
		
		char[] pwd = "abc".toCharArray();
		byte[] salt = new byte[8];
		new SecureRandom().nextBytes(salt);
		
		
		SecretKey key = generateSecretKeyPBE(pwd, salt);
	}
	
	private static void test0(){
		String text = "Hello world!";
		
		char[] pwd = "abc".toCharArray();
		
		byte[] salt = new byte[8];
		new SecureRandom().nextBytes(salt);
		
		int noIterations = 42;
		
		System.out.println("text: " + text);
		
		byte[] encryptedText = encrypt(text.getBytes(), pwd, salt, noIterations);
		
		String decryptedText = decrypt(encryptedText, pwd, salt, noIterations);
		System.out.println("decryptedText: " + decryptedText);
	}
	
	public static SecretKey generateSecretKey(String algo, int keyLen){
		
		SecretKey secretKey = null;
		try {
			
			KeyGenerator keyGen = KeyGenerator.getInstance(algo);
			keyGen.init(keyLen);
			secretKey = keyGen.generateKey();
		} 
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return secretKey;
	}
	
	public static SecretKey generateSecretKeyPBE(char[] pwd, byte[] salt){
        
		SecretKey secretKey = null;
	    
	    try {
	    	SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(pwd, salt, 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
		} 
	    catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	    
	    return secretKey;
	}
	
	public static KeyPair generateKeyPair(String algo, int length){
		KeyPair keyPair = null;
		try{
			KeyPairGenerator generator = KeyPairGenerator.getInstance(algo);
			generator.initialize(length);
			keyPair = generator.genKeyPair();
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
		return keyPair;
	}
	
	public static PublicKey readPublicKeyFromFile(String algo, File file) {
		
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
			
			BigInteger m = (BigInteger)in.readObject();
		    BigInteger e = (BigInteger)in.readObject();
		    RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
		    
		    KeyFactory factory = KeyFactory.getInstance(algo);
		    PublicKey pubKey = factory.generatePublic(keySpec);
		    return pubKey;
		} 
		catch(Exception e) {
		    throw new RuntimeException(e);
		} 
		finally {
		    try {
		    	in.close();
			} 
		    catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static PrivateKey readPrivateKeyFromFile(String algo, File file) {
		
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
			
			BigInteger m = (BigInteger)in.readObject();
		    BigInteger e = (BigInteger)in.readObject();
		    RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
		    
		    KeyFactory factory = KeyFactory.getInstance(algo);
		    PrivateKey privKey = factory.generatePrivate(keySpec);
		    return privKey;
		} 
		catch(Exception e) {
		    throw new RuntimeException(e);
		} 
		finally {
		    try {
		    	in.close();
			} 
		    catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void savePublicKeyToFile(PublicKey pubKey, String algo, File file){
		try{
			KeyFactory fact = KeyFactory.getInstance(algo);
			RSAPublicKeySpec keySpec = fact.getKeySpec(pubKey, RSAPublicKeySpec.class);
			saveToFile(file, keySpec.getModulus(), keySpec.getPublicExponent());
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public static void savePrivateKeyToFile(PrivateKey privKey, String algo, File file){
		try{
			KeyFactory factory = KeyFactory.getInstance(algo);
			RSAPrivateKeySpec keySpec = factory.getKeySpec(privKey, RSAPrivateKeySpec.class);
			saveToFile(file, keySpec.getModulus(), keySpec.getPrivateExponent());
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	private static void saveToFile(File file, BigInteger mod, BigInteger exp) {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		    out.writeObject(mod);
		    out.writeObject(exp);
		} 
		catch(Exception e) {
		    throw new RuntimeException(e);
		} 
		finally {
			try {
				out.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static byte[] encrypt(byte[] data, char[] password, byte[] salt, int noIterations) {
		  try {
		    String method = "PBEWithMD5AndTripleDES";
		    
		    SecretKeyFactory factory = SecretKeyFactory.getInstance(method);
		    SecretKey key = factory.generateSecret(new PBEKeySpec(password));
		    
		    PBEParameterSpec params = new PBEParameterSpec(salt, noIterations);
		    
		    Cipher ciph = Cipher.getInstance(method);
		    ciph.init(Cipher.ENCRYPT_MODE, key, params);
		    return ciph.doFinal(data);
		    
		  } catch (Exception e) {
		    throw new RuntimeException(e);
		  }
	}
	
	public static String decrypt(byte[] data, char[] password, byte[] salt, int noIterations) {
		String text = null;
		  try {
		    String method = "PBEWithMD5AndTripleDES";
		    
		    SecretKeyFactory factory = SecretKeyFactory.getInstance(method);
		    SecretKey key = factory.generateSecret(new PBEKeySpec(password));
		    
		    PBEParameterSpec params = new PBEParameterSpec(salt, noIterations);
		    
		    Cipher ciph = Cipher.getInstance(method);
		    ciph.init(Cipher.DECRYPT_MODE, key, params);
		    byte[] bytes = ciph.doFinal(data);
		    text = new String(bytes);
		    
		  } catch (Exception e) {
		    throw new RuntimeException(e);
		  }
		  
		  return text;
	}
	
	public static void test1(){
		
		String text = "Hello, World!";
		
		char[] pwd = "1234".toCharArray();
		byte[] salt = new byte[8];
		new SecureRandom().nextBytes(salt);
		
		try{
		
			/* Derive the key, given password and salt. */
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(pwd, salt, 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
			
			/* Encrypt the message. */
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			AlgorithmParameters params = cipher.getParameters();
			byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
			byte[] ciphertext = cipher.doFinal(text.getBytes("UTF-8"));
			
			//PBEStorage store = null;
			
			System.out.println("encrypted: " + new String(ciphertext, "UTF-8"));
			
			/* decrypt */
			Cipher cipher2 = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher2.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
			byte[] decrypted = cipher2.doFinal(ciphertext);
			System.out.println("decrypted: " + new String(decrypted, "UTF-8"));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
