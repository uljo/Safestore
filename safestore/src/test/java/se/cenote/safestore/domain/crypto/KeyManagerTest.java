package se.cenote.safestore.domain.crypto;

import static org.junit.Assert.fail;

import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Set;

import javax.crypto.Cipher;

import org.junit.Test;

import se.cenote.safestore.domain.crypto.CryptoManager.EncryptedData;

public class KeyManagerTest {
	
	private byte[] iv;
	
	public static void main(String[] args){
		KeyManagerTest test = new KeyManagerTest();

		//printKeyLengths();
		
		test1();
		test2();
	}
	
	public static void printKeyLengths(){
		try {
            Set<String> algorithms = Security.getAlgorithms("Cipher");
            for(String algorithm: algorithms) {
                int max;
                max = Cipher.getMaxAllowedKeyLength(algorithm);
                System.out.printf("%-22s: %dbit%n", algorithm, max);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
	}
	
	public static void test1(){
		long start = System.currentTimeMillis();
		String text = "test123";
		char[] pwd = "123".toCharArray();
		
		System.out.println("AES - Pwd: " + new String(pwd));
		System.out.println("Plaintext: " + text);
	
		try{
			PBE_Crypto pBE_Crypto = new PBE_Crypto_AES(128, "UTF-8");
			EncryptedData data = pBE_Crypto.encrypt(text, pwd);
			System.out.println("Encrypted: " + data.getEncryptedBase64());
			
			String text2 = pBE_Crypto.decrypt(data, pwd);
			long stop = System.currentTimeMillis();
			
			System.out.println("Decrypted: " + text2);
			System.out.println("Time: " + (stop-start) + " msek.");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void test2(){
		long start = System.currentTimeMillis();
		String text = "test123";
		char[] pwd = "123".toCharArray();
		
		System.out.println("DES - Pwd: " + new String(pwd));
		System.out.println("Plaintext: " + text);
	
		try{
			PBE_Crypto pBE_Crypto = new PBE_Crypto_DES(12, "UTF-8");
			EncryptedData data = pBE_Crypto.encrypt(text, pwd);
			System.out.println("Encrypted: " + data.getEncryptedBase64());
			
			String text2 = pBE_Crypto.decrypt(data, pwd);
			long stop = System.currentTimeMillis();
			
			System.out.println("Decrypted: " + text2);
			System.out.println("Time: " + (stop-start) + " msek.");
		
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}


	//@Test
	public void testGenerateKeyPair() {
		fail("Not yet implemented");
	}

}
