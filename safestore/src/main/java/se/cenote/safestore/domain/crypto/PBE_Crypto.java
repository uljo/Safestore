package se.cenote.safestore.domain.crypto;

import se.cenote.safestore.domain.crypto.CryptoManager.EncryptedData;

public interface PBE_Crypto {
	public String getName();
	public EncryptedData encrypt(String text, char[] pwd) throws InvalidKeyLengthException;
	public String decrypt(EncryptedData encryptedData, char[] pwd);
}
