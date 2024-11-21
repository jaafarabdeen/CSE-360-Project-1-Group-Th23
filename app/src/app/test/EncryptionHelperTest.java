package app.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import Encryption.EncryptionHelper;

import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionHelperTest {

    @Test
    public void testEncryptDecrypt() throws Exception {
        EncryptionHelper helper = new EncryptionHelper();
        String originalText = "This is a secret message.";
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        byte[] encrypted = helper.encrypt(originalText.getBytes(), iv);
        assertNotNull(encrypted);

        byte[] decrypted = helper.decrypt(encrypted, iv);
        assertNotNull(decrypted);

        String decryptedText = new String(decrypted);
        assertEquals(originalText, decryptedText);
    }

    @Test
    public void testDecryptBody() throws Exception {
        EncryptionHelper helper = new EncryptionHelper();
        String originalBody = "Encrypted Article Body";
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        byte[] encryptedData = helper.encrypt(originalBody.getBytes(), iv);
        String ivEncoded = Base64.getEncoder().encodeToString(iv);
        String encryptedBodyEncoded = Base64.getEncoder().encodeToString(encryptedData);

        String encryptedBody = ivEncoded + ":" + encryptedBodyEncoded;

        String decryptedBody = helper.decryptBody(encryptedBody);
        assertEquals(originalBody, decryptedBody);
    }

    @Test
    public void testInvalidDecryptBody() {
        EncryptionHelper helper;
        try {
            helper = new EncryptionHelper();
            String invalidEncryptedBody = "invalid_format_body";
            assertThrows(IllegalArgumentException.class, () -> {
                helper.decryptBody(invalidEncryptedBody);
            });
        } catch (Exception e) {
            fail("Failed to initialize EncryptionHelper");
        }
    }
}
