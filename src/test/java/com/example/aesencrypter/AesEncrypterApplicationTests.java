package com.example.aesencrypter;

import com.example.aesencrypter.encrypter.EncrypterConfig;
import org.junit.jupiter.api.Assertions;
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
class AesEncrypterApplicationTests {

    @Autowired
    private EncrypterConfig config;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(config, "Config is null");
        Assertions.assertEquals(16, config.getSalt());
        Assertions.assertEquals(8, config.getPepper());
        Assertions.assertEquals("MyTestsSecretKey", config.getPass());
    }

}
