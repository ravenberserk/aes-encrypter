package com.example.aesencrypter.utils;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Clase en la que se definiran los metodos encargados en realizar la encriptación/desecnriptacion de una cadena de
 * texto.
 *
 * @author jgrande
 * @since 1.0
 */
@Component
public class AESEncrypter {

    private final EncrypterConfig config;

    @Autowired
    public AESEncrypter(EncrypterConfig config) {
        this.config = config;
    }

    /**
     * Encriptara la cadena de texto recibida como parametro. Como resultado devolvera la cadena encriptada y
     * sal-pimentada en {@code Base64}.
     *
     * @param strToEncrypt Cadena a encriptar.
     * @return Cadena encriptada y sal-pimentada en {@code Base64}.
     */
    @SneakyThrows
    public String encrypt(String strToEncrypt) {
        Cipher cipher = initCipherToEncrypt(config);

        byte[] salt = generateRandomByteArray(config.getSalt());
        byte[] pepper = generateRandomByteArray(config.getPepper());

        return encrypt(strToEncrypt, cipher, salt, pepper);
    }

    @SneakyThrows
    private Cipher initCipherToEncrypt(EncrypterConfig config) {
        Cipher cipher = Cipher.getInstance(config.getCipherAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, generateSecretKeySpec(config));
        return cipher;
    }

    private SecretKeySpec generateSecretKeySpec(EncrypterConfig config) {
        return new SecretKeySpec(config.getPass().getBytes(), config.getAlgorithm());
    }

    /**
     * Metodo auxiliar que devolvera un {@code byte[]} aleatorio, lo cual se empleara para la generacion de la sal,
     * pimienta e {@code IV}.
     *
     * @param length Longitud del {@code byte[]}
     * @return A Random {@code byte[]}
     * @implNote {@linkplain SecureRandom#getSeed(int)} puede ser bloqueante o bastante lenta, ya que emplea
     *         {@code /dev/random} para la generación de la cadena aleatoria. Para evitar esto se puede sustituir por
     *         {@linkplain SecureRandom#nextBytes(byte[])}.
     */
    private byte[] generateRandomByteArray(int length) {
        return SecureRandom.getSeed(length);
    }

    @SneakyThrows
    private String encrypt(String strToEncrypt, Cipher cipher, byte[] salt, byte[] pepper) {
        byte[] result = generateEncriptedArray(cipher.doFinal(strToEncrypt.getBytes()), cipher.getIV(), salt, pepper);
        return Base64.getEncoder().encodeToString(result);
    }

    private byte[] generateEncriptedArray(byte[] encrypted, byte[] iv, byte[] salt, byte[] pepper) {
        return ByteBuffer.allocate(getArrayTotalLength(encrypted, iv, salt, pepper))
                .put(salt)
                .put(iv)
                .put(encrypted)
                .put(pepper)
                .array();
    }

    private int getArrayTotalLength(byte[] encrypted, byte[] iv, byte[] salt, byte[] pepper) {
        return encrypted.length + iv.length + salt.length + pepper.length;
    }

    /**
     * Desencriptara la cadena de texto recibida como parametro, la cual se encontrara en {@code Base64}. Habra que
     * tener en cuenta que la cadena no solo se encuentra en {@code Base64}, si no que tambien contiene sal y pimienta.
     *
     * @param encrypted Cadena en {@code Base64} a desencriptar.
     * @return Cadena desencriptada.
     */
    @SneakyThrows
    public String decrypt(String encrypted) {
        byte[] encryptedArray = Base64.getDecoder().decode(encrypted);

        Cipher cipher = initCipherToDecrypt(config, encryptedArray);
        byte[] data = recoveryEncryptedStr(encryptedArray, config, cipher.getBlockSize());

        return new String(cipher.doFinal(data));
    }

    @SneakyThrows
    private Cipher initCipherToDecrypt(EncrypterConfig config, byte[] encryptedArray) {
        Cipher cipher = Cipher.getInstance(config.getCipherAlgorithm());
        byte[] iv = recoveryIv(encryptedArray, config, cipher.getBlockSize());
        cipher.init(Cipher.DECRYPT_MODE, generateSecretKeySpec(config), new IvParameterSpec(iv));
        return cipher;
    }

    private byte[] recoveryIv(byte[] encryptedArray, EncrypterConfig config, int ivLength) {
        byte[] iv = new byte[ivLength];
        System.arraycopy(encryptedArray, config.getSalt(), iv, 0, ivLength);
        return iv;
    }

    private byte[] recoveryEncryptedStr(byte[] encryptedArray, EncrypterConfig config, int ivLength) {
        byte[] data = new byte[getEncryptedDataSize(encryptedArray, config, ivLength)];
        System.arraycopy(encryptedArray, config.getSalt() + ivLength, data, 0, data.length);
        return data;
    }

    private int getEncryptedDataSize(byte[] encryptedArray, EncrypterConfig config, int ivLength) {
        return encryptedArray.length - ivLength - config.getSalt() - config.getPepper();
    }

}