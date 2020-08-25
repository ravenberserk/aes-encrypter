package com.example.aesencrypter.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * Cipher configurator.
 *
 * @author jgrande
 * @since 1.0
 */
@Getter
@Builder(builderMethodName = "config")
public final class EncrypterConfig {

    /**
     * Encrypt algorithm used.
     */
    @NonNull
    private final String cipherAlgorithm = "AES/CFB/PKCS5Padding";

    /**
     * Encryptation/Desencryptation password used.
     */
    @NonNull
    private final String encrypterPass;

    /**
     * Length of the possible salt byte array.
     */
    private final int salt;

    /**
     * Length of the possible pepper byte array.
     */
    private final int pepper;

}
