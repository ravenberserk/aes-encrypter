package com.example.aesencrypter.utils;

import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * <p>
 * Encrypt/Decrypt example.
 * </p>
 */
@Component("aesEncrypter")
public class AESEncrypter {

    /**
     * Encrypt algorithm used.
     */
    private static final String ALGORITHM = "AES/CFB/PKCS5Padding";

    /**
     * Secret key used to encrypt and decrypt.
     */
    private static final String SECRET_KEY = "MySuperSecretKey";

    /**
     * Length of the IV.
     */
    private static final int IV_LENGTH = 16;

    /**
     * Length of the salt array
     */
    private static final int SALT_LENGTH = 16;

    /**
     * Length of the pepper array
     */
    private static final int PEPPER_LENGTH = 16;

    /**
     * <p>
     * Auxiliar method that will generate a secured key.
     * </p>
     *
     * @return Secret key that will use to encrypt/decrypt the string.
     */
    private static SecretKeySpec generateSecretKey() throws NoSuchAlgorithmException {
        return new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
    }

    /**
     * <p>
     * Auxiliar method that generate a random byte array, used in salt, pepper
     * and IV.
     * </p>
     *
     * @return A Random {@code byte[]}
     */
    private static byte[] generateRandomByteArray(int length) {
        return SecureRandom.getSeed(length);
    }

    /**
     * <p>
     * Auxiliar method that will encrypt a string.
     * </p>
     *
     * @return Encrypted string.
     */
    public String encrypt(String strToEncrypt) {
        byte[] result;
        try {
            // Generate the random iv and spec.
            byte[] iv = generateRandomByteArray(IV_LENGTH);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Generate the random salt and pepper.
            byte[] salt = generateRandomByteArray(SALT_LENGTH);
            byte[] pepper = generateRandomByteArray(PEPPER_LENGTH);

            // Init the cipher class and encrypt the string.
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, generateSecretKey(), ivSpec);

            byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes());

            // Finally, a new array that will contain the encrypted string, iv,
            // salt and pepper.
            result = new byte[encrypted.length + IV_LENGTH + SALT_LENGTH + PEPPER_LENGTH];
            System.arraycopy(salt, 0, result, 0, SALT_LENGTH);
            System.arraycopy(iv, 0, result, SALT_LENGTH, IV_LENGTH);
            System.arraycopy(encrypted, 0, result, SALT_LENGTH + IV_LENGTH, encrypted.length);
            System.arraycopy(pepper, 0, result, SALT_LENGTH + IV_LENGTH + encrypted.length, PEPPER_LENGTH);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new Error(e);
        }

        return Base64.getEncoder().encodeToString(result);

    }

    /**
     * <p>
     * Auxiliar method that will decrypt a string.
     * </p>
     *
     * @param encrypted Encrypted string
     * @return Plain text encrypted.
     */
    public String decrypt(String encrypted) {
        byte[] plainText;
        try {
            byte[] encryptedArray = Base64.getDecoder().decode(encrypted);

            // The IV is recovered from the decode encrypted string.
            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(encryptedArray, SALT_LENGTH, iv, 0, IV_LENGTH);

            // The encrypted data are recovered from the array.
            byte[] data = new byte[encryptedArray.length - IV_LENGTH - SALT_LENGTH - PEPPER_LENGTH];
            System.arraycopy(encryptedArray, SALT_LENGTH + IV_LENGTH, data, 0, data.length);

            // Init the cipher class and decrypt the string.
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, generateSecretKey(), new IvParameterSpec(iv));

            plainText = cipher.doFinal(data);

        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException
                | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new Error(e);
        }
        return new String(plainText);
    }

}