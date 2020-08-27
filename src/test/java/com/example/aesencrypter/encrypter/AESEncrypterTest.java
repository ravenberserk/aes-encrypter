package com.example.aesencrypter.encrypter;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = EncrypterConfig.class)
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles("test")
class AESEncrypterTest {

    @Autowired
    private EncrypterConfig config;

    private AESEncrypter aesEncypter;

    @BeforeEach
    void setUp() {
        this.aesEncypter = new AESEncrypter(config);
    }

    @Test
    void nullStr() {
        Assertions.assertThrows(NullPointerException.class, () -> aesEncypter.encrypt(null));
    }

    @Test
    void emptyStr() throws BadPaddingException, IllegalBlockSizeException {
        String result = aesEncypter.encrypt("");
        Assertions.assertAll(() -> Assertions.assertNotNull(result), () -> Assertions.assertNotEquals("", result),
                () -> Assertions.assertTrue(Base64.isBase64(result)));
    }

    @Test
    void encryptStr() throws BadPaddingException, IllegalBlockSizeException {
        String original = "Hello World!!";

        String encrypted = aesEncypter.encrypt(original);
        Assertions.assertAll("Encrypted", () -> Assertions.assertNotNull(encrypted),
                () -> Assertions.assertNotEquals(original, encrypted),
                () -> Assertions.assertTrue(Base64.isBase64(encrypted)));

        String unencrypted = aesEncypter.decrypt(encrypted);
        Assertions.assertAll("Decrypted", () -> Assertions.assertNotNull(unencrypted),
                () -> Assertions.assertEquals(original, unencrypted),
                () -> Assertions.assertFalse(Base64.isBase64(unencrypted)));
    }

    @Test
    void decryptNormalStr() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> aesEncypter.decrypt("Hola Mundo!!"));
    }

    @Test
    void decryptBadBase64Str() throws BadPaddingException, IllegalBlockSizeException {
        String original = "Hello World!!";
        String encrypted = aesEncypter.encrypt(original);
        Assertions.assertThrows(BadPaddingException.class, () -> aesEncypter.decrypt(encrypted.toLowerCase()));
    }

}