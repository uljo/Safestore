package se.cenote.safestore.domain.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class FileCipherManager {
	
	public static final String ALGO_DES_ECB = "DES/ECB/PKCS5Padding";
	
	private String algo;
	private byte[] pwd;

	public static void main(String[] args) {
		
		FileCipherManager mgr = new FileCipherManager(ALGO_DES_ECB, "HignDlPs".getBytes());
		
		File inFile = new File("sample.txt");
		File outFile = new File("sample.enc");
		mgr.encrypt(inFile, outFile);
		
		inFile = new File("sample.enc");
		outFile = new File("sample.txt");
		mgr.decrypt(inFile, outFile);
	}

	public FileCipherManager(String algo, byte[] pwd) { 
		this.algo = algo;
		this.pwd = pwd;
	}

	public void encrypt(File inFile, File outFile) {
		
		FileInputStream input = null;
		CipherOutputStream cout = null;
		
		try{
			input = new FileInputStream(inFile);

			cout = getEncryptStream(algo, new FileOutputStream(outFile));
	
			byte[] buf = new byte[1024];
			int read;
			while ((read = input.read(buf)) != -1){
				cout.write(buf, 0, read);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try {
				input.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			try {
				cout.flush();
				cout.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void decrypt(File inFile, File outFile){
		
		FileOutputStream output = null;
		CipherInputStream cin = null;
		
		try{
			output = new FileOutputStream(outFile);
			
			cin = getDecryptStream(algo, new FileInputStream(inFile));
	
			byte[] buf = new byte[1024];
			int read = 0;
			while ((read = cin.read(buf)) != -1){
				output.write(buf, 0, read); // writing decrypted data
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try {
				cin.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			try {
				output.flush();
				output.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private CipherOutputStream getEncryptStream(String algo, FileOutputStream output) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException{
		
		Cipher cipher = getCipher(algo, Cipher.ENCRYPT_MODE);
		
		return new CipherOutputStream(output, cipher);
	}
	
	private CipherInputStream getDecryptStream(String algo, FileInputStream input) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException{
		
		Cipher cipher = getCipher(algo, Cipher.DECRYPT_MODE);
		
		return new CipherInputStream(input, cipher);
	}
	
	private Cipher getCipher(String algo, int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException{
		
		SecretKeySpec key = new SecretKeySpec(pwd, algo.split("/")[0]);
		
		Cipher cipher = Cipher.getInstance(algo);
		cipher.init(mode, key);
		
		return cipher;
	}
	

}
