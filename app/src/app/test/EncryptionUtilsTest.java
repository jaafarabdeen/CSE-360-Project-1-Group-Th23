package app.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import Encryption.EncryptionUtils;

public class EncryptionUtilsTest {

    @Test
    public void testToByteArrayAndToCharArray() {
        String originalString = "Test String";
        char[] chars = originalString.toCharArray();
        byte[] bytes = EncryptionUtils.toByteArray(chars);

        assertNotNull(bytes);

        char[] convertedChars = EncryptionUtils.toCharArray(bytes);

        assertArrayEquals(chars, convertedChars);
    }

    @Test
    public void testGetInitializationVector() {
        char[] text = "password".toCharArray();
        byte[] iv = EncryptionUtils.getInitializationVector(text);

        assertNotNull(iv);
        assertEquals(16, iv.length);
    }

    @Test
    public void testHashPassword() {
        String password = "mySecretPassword";
        byte[] hash1 = EncryptionUtils.hashPassword(password);
        byte[] hash2 = EncryptionUtils.hashPassword(password);

        assertNotNull(hash1);
        assertNotNull(hash2);
        assertArrayEquals(hash1, hash2);

        String differentPassword = "differentPassword";
        byte[] differentHash = EncryptionUtils.hashPassword(differentPassword);
        assertFalse(java.util.Arrays.equals(hash1, differentHash));
    }
}
