package com.example.aesencrypter.utils;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

/**
 * <p>
 * This class would be responsible for encrypting/decryption of a string.
 * </p>
 *
 * @author jgrande
 * @since 1.0
 */
@Component("aesEncrypter")
public class AESEncrypter {

    /**
     * <p>
     * Auxiliar method that will encrypt a string.
     * </p>
     *
     * @param strToEncrypt String you want to encrypt
     * @param config Cipher configuration.
     * @return Encrypted string.
     */
    public String encrypt(String strToEncrypt, EncrypterConfig config) {
        byte[] result;
        try {
            // Init the cipher class and encrypt the string.
            Cipher cipher = initEncrypterCipher(config, Cipher.ENCRYPT_MODE);
            byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes());

            // Generate the random salt and pepper, and get iv array from the cipher object.
            byte[] salt = generateRandomByteArray(config.getSalt());
            byte[] pepper = generateRandomByteArray(config.getPepper());
            byte[] iv = cipher.getIV();

            // Finally, a new array that will contain the encrypted string, iv,
            // salt and pepper.
            result = generateCompleteEncrypterArray(encrypted, iv, salt, pepper);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new Error(e);
        }

        return Base64.getEncoder().encodeToString(result);

    }

    private Cipher initEncrypterCipher(EncrypterConfig config, int encryptMode)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(config.getCipherAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, generateSecretKey(config));
        return cipher;
    }

    /**
     * <p>
     * Auxiliar method that will generate a secured key.
     * </p>
     *
     * @param config Cipher configuration.
     * @return Secret key that will use to encrypt/decrypt the string.
     */
    private SecretKeySpec generateSecretKey(EncrypterConfig config) throws NoSuchAlgorithmException {
        return new SecretKeySpec(config.getEncrypterPass().getBytes(), "AES");
    }

    /**
     * <p>
     * Auxiliar method that generate a random byte array, used in salt, pepper and IV.
     * </p>
     *
     * @return A Random {@code byte[]}
     */
    private byte[] generateRandomByteArray(int length) {
        return SecureRandom.getSeed(length);
    }

    private byte[] generateCompleteEncrypterArray(byte[] encrypted, byte[] iv, byte[] salt, byte[] pepper) {
        ByteBuffer result = ByteBuffer.allocate(getArrayTotalLength(encrypted, iv, salt, pepper));
        result.put(salt);
        result.put(iv);
        result.put(encrypted);
        result.put(pepper);
        return result.array();
    }

    private int getArrayTotalLength(byte[] encrypted, byte[] iv, byte[] salt, byte[] pepper) {
        return encrypted.length + iv.length + salt.length + pepper.length;
    }

    /**
     * <p>
     * Auxiliar method that will decrypt a string.
     * </p>
     *
     * @param encrypted Encrypted string
     * @param config Cipher configuration.
     * @return Plain text encrypted.
     */
    public String decrypt(String encrypted, EncrypterConfig config) {
        byte[] decryptedStr;
        try {
            byte[] encryptedArray = Base64.getDecoder().decode(encrypted);

            Cipher cipher = Cipher.getInstance(config.getCipherAlgorithm());

            byte[] iv = recoveryIv(encryptedArray, config, cipher.getBlockSize());
            byte[] data = recoveryEncryptedStr(encryptedArray, config, iv);

            cipher.init(Cipher.DECRYPT_MODE, generateSecretKey(config), new IvParameterSpec(iv));
            decryptedStr = cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new Error(e);
        }
        return new String(decryptedStr);
    }

    private byte[] recoveryIv(byte[] encryptedArray, EncrypterConfig config, int ivLength) {
        byte[] iv = new byte[ivLength];
        System.arraycopy(encryptedArray, config.getSalt(), iv, 0, ivLength);
        return iv;
    }

    private byte[] recoveryEncryptedStr(byte[] encryptedArray, EncrypterConfig config, byte[] iv) {
        byte[] data = new byte[getEncryptedDataSize(encryptedArray, config, iv)];
        System.arraycopy(encryptedArray, config.getSalt() + iv.length, data, 0, data.length);
        return data;
    }

    private int getEncryptedDataSize(byte[] encryptedArray, EncrypterConfig config, byte[] iv) {
        return encryptedArray.length - iv.length - config.getSalt() - config.getPepper();
    }


}