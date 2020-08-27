package com.example.aesencrypter.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles("test")
class EncrypterControllerTest {

    private static final String ENCRYPT_ENDPOINT = "/encrypt";
    private static final String DECRYPT_ENDPOINT = "/decrypt";

    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    void encryptNull() {
        mvc.perform(MockMvcRequestBuilders.post(ENCRYPT_ENDPOINT).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @SneakyThrows
    void decryptNull() {
        mvc.perform(MockMvcRequestBuilders.post(DECRYPT_ENDPOINT).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @SneakyThrows
    void encryptEmpty() {
        mvc.perform(MockMvcRequestBuilders.post(ENCRYPT_ENDPOINT).content("").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @SneakyThrows
    void decryptEmpty() {
        mvc.perform(MockMvcRequestBuilders.post(DECRYPT_ENDPOINT).content("").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @SneakyThrows
    void decryptNormalStr() {
        mvc.perform(MockMvcRequestBuilders.post(DECRYPT_ENDPOINT)
                .content("Hello World!!")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is4xxClientError());
    }

    @Test
    @SneakyThrows
    void encryptHello() {
        String original = "Hello World!!";

        String encrypted = mvc.perform(
                MockMvcRequestBuilders.post(ENCRYPT_ENDPOINT).content(original).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertAll("Encrypted", () -> Assertions.assertNotNull(encrypted),
                () -> Assertions.assertNotEquals(original, encrypted),
                () -> Assertions.assertTrue(Base64.isBase64(encrypted)));

        String decrypted = mvc.perform(MockMvcRequestBuilders.post(DECRYPT_ENDPOINT)
                .content(encrypted)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertAll("Decrypted", () -> Assertions.assertNotNull(decrypted),
                () -> Assertions.assertEquals(original, decrypted),
                () -> Assertions.assertFalse(Base64.isBase64(decrypted)));
    }

}