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
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

import se.cenote.safestore.domain.crypto.SymetricCryptoManager.EncryptedMessage;

public class AssymetricCryptoManager {
	
	public static void main(String[] args) {
		
		String text = "Hello World";
		System.out.println("Text: " + text);
		
		String algo = "RSA";
		File pubKeyFile = new File("pub.key");
		File privKeyFile = new File("priv.key");
		
		
		// Key length: 1024 (medium-security), 2048 (high-security), (confidential for more than the next two decades, RSA recommends a key size larger than 2048 bits: -> 4096, 8192)
		// With every doubling of the RSA key length, decryption is 6-7 times times slower.
		KeyPair keyPair = generateKeyPair(algo, 2048);
		System.out.println("Public key: " + keyPair.getPublic());
		System.out.println("Private key: " + keyPair.getPrivate());
		
		savePublicKeyToFile(keyPair.getPublic(), algo, pubKeyFile);
		savePrivateKeyToFile(keyPair.getPrivate(), algo, privKeyFile);
		
		PublicKey pubKey = readPublicKeyFromFile(algo, pubKeyFile);
		PrivateKey privKey = readPrivateKeyFromFile(algo, privKeyFile);
		
		EncryptedMessage msg = encrypt(text, algo, pubKey);
		System.out.println("Encrypted: " + new String(msg.getEnrcyptedText()));
		
		String decryptedText = decrypt(msg, algo, privKey);
		System.out.println("Decrypted: " + decryptedText);
	}

	public static EncryptedMessage encrypt(String text, String algo, PublicKey pubKey){
		EncryptedMessage msg = null;
		try{
			Cipher cipher = Cipher.getInstance(algo);
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			byte[] encryptedText = cipher.doFinal(text.getBytes());
			msg = new EncryptedMessage(encryptedText, null);
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
		return msg;
	}
	
	public static String decrypt(EncryptedMessage msg, String algo, PrivateKey privKey){
		String text = null;
		try{
			Cipher cipher = Cipher.getInstance(algo);
			cipher.init(Cipher.DECRYPT_MODE, privKey);
			byte[] decryptedText = cipher.doFinal(msg.getEnrcyptedText());
			text = new String(decryptedText);
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
		return text;
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
	
}
