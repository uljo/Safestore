package se.cenote.safestore.domain.crypto;

/**
 * Thrown if an attempt is made to encrypt a stream with an invalid AES key
 * length.
 */
public class InvalidKeyLengthException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidKeyLengthException(int length) {
		super("Invalid AES key length: " + length);
	}
}