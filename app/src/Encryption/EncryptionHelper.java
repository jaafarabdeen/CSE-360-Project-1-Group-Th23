package Encryption;

import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class EncryptionHelper {

	private static String BOUNCY_CASTLE_PROVIDER_IDENTIFIER = "BC";	
	private Cipher cipher;
	
	byte[] keyBytes = new byte[] {
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
            0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17 };
	private SecretKey key = new SecretKeySpec(keyBytes, "AES");

	public EncryptionHelper() throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", BOUNCY_CASTLE_PROVIDER_IDENTIFIER);		
	}
	
	public byte[] encrypt(byte[] plainText, byte[] initializationVector) throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(initializationVector));
		return cipher.doFinal(plainText);
	}
	
	public byte[] decrypt(byte[] cipherText, byte[] initializationVector) throws Exception {
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(initializationVector));
		return cipher.doFinal(cipherText);
	}
	
	/**
     * Attempts to decrypt the article body if it is encrypted.
     *
     * @param encryptedBody The possibly encrypted body text.
     * @return The decrypted body text, or the original text if decryption fails.
     * @throws Exception if decryption fails or if the body is not properly formatted.
     */
    public String decryptBody(String encryptedBody) throws Exception {
        // Split the encrypted body format (assuming it has the format iv:encryptedBody)
        String[] parts = encryptedBody.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid encrypted body format.");
        }

        byte[] iv = Base64.getDecoder().decode(parts[0]);
        byte[] encryptedData = Base64.getDecoder().decode(parts[1]);
        byte[] decryptedData = decrypt(encryptedData, iv);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }
	
}
