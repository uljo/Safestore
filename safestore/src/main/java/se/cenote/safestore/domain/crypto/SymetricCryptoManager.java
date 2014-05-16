package se.cenote.safestore.domain.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SymetricCryptoManager {
	
	private static final String CHARSET_UTF8 = "UTF-8";
	
	private static final String ALGO_AES = "AES"; // key-length = 16 or 128 bytes (128-256 bitar [vanligt: 128, 192, 256])
	private static final String ALGO_DES = "DES"; // key-length = 7 bytes (56 bitar)
	
	public static enum BlockMode {CBC, CTR, OFB, ECB};
	
	public static final String PADDING_PKCS5 = "PKCS5PADDING";

	public static void main(String[] args) {
		
		String text = "Hello World";
		System.out.println("Text: " + text);
		
		
		// 1. Crypto example with a) "AES/CBC/PKCS5PADDING"   b) "DES/ECB/PKCS5Padding"
		
		// Key length
		//DES: 56bit key
		//AES: 128-256bit key (commonly used values are 128, 192 and 256)
		//RSA (assymetric cryptography): 1024, 2048, 4096 bit key

		
		Crypto cryptoAES = new Crypto("1234567890ABCDEF".getBytes(), ALGO_AES, BlockMode.CBC, PADDING_PKCS5);
		Crypto cryptoDES = new Crypto("12345678".getBytes(), ALGO_DES, BlockMode.ECB, PADDING_PKCS5);
		
		Crypto crypto = cryptoAES;
		System.out.println("Key: " + new String(crypto.getKey()) + " (length=" + (crypto.getKey().length * 8) + " bits)");
		
		EncryptedMessage msg = encrypt(text, crypto);
		System.out.println("Encrypted: " + new String(msg.getEnrcyptedText()));
		
		String decryptedText = decrypt(msg, crypto);
		System.out.println("Decrypted: " + decryptedText);
	}
	
	public static EncryptedMessage encrypt(String text, Crypto crypto){
		EncryptedMessage msg = null;
		try{
			Cipher cipher = crypto.getEncryptCipher();
			byte[] encryptedText = cipher.doFinal(text.getBytes(CHARSET_UTF8));
			byte[] iv = cipher.getIV();
			
			msg = new EncryptedMessage(encryptedText, iv);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return msg;
	}
	
	public static String decrypt(EncryptedMessage msg, Crypto crypto){
		String text = null;
		try{
			Cipher cipher = crypto.getDecryptCipher(msg.getIv());
			byte[] decryptedText = cipher.doFinal(msg.getEnrcyptedText());
			
			text = new String(decryptedText, CHARSET_UTF8);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return text;
	}
	
	public static class Crypto{
		
		private byte[] key;
		private String algo;
		private BlockMode blockMode;
		private String padding;
		
		public Crypto(byte[] key, String algo, BlockMode blockMode, String padding) {
			this.key = key;
			this.algo = algo;
			this.blockMode = blockMode;
			this.padding = padding;
		}
		
		public Cipher getEncryptCipher(){
			Cipher cipher = null;
			try{
				SecretKeySpec keySpec = new SecretKeySpec(getKey(), getAlgo());
				cipher = Cipher.getInstance(getAlgo() + "/" + getBlockMode() + "/" + getPadding());
				
				cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			}
			catch(Exception e){
				throw new RuntimeException(e);
			}
			return cipher;
		}
		
		public Cipher getDecryptCipher(byte[] iv){
			Cipher cipher = null;
			try{
				SecretKeySpec keySpec = new SecretKeySpec(getKey(), getAlgo());
				cipher = Cipher.getInstance(getAlgo() + "/" + getBlockMode() + "/" + getPadding());
				
				cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
			}
			catch(Exception e){
				throw new RuntimeException(e);
			}
			return cipher;
		}
		
		public byte[] getKey(){
			return key;
		}
		
		public String getAlgo() {
			return algo;
		}
		
		public BlockMode getBlockMode() {
			return blockMode;
		}
		
		public String getPadding() {
			return padding;
		}
	}
	
	public static class EncryptedMessage{
		
		private byte[] enrcyptedText;
		private byte[] iv;
		
		public EncryptedMessage(byte[] enrcyptedText, byte[] iv) {
			this.enrcyptedText = enrcyptedText;
			this.iv = iv;
		}

		public byte[] getEnrcyptedText() {
			return enrcyptedText;
		}

		public byte[] getIv() {
			return iv;
		}
		
		
	}

}
