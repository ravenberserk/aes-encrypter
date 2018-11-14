package com.example.aesencrypter.controller;


import com.example.aesencrypter.utils.AESEncrypter;
import com.example.aesencrypter.utils.EncrypterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;

/**
 * Controller en el que se implementaran los endPoints para encriptar/desencriptar la cadena de texto.
 *
 * @author jgrande
 * @since 1.0
 */
@RestController
public class EncrypterController {

    @Value("${encryption.pass}")
    private String encryptationPass;

    @Autowired
    private AESEncrypter aesEncypter;

    @PostMapping("/encrypt")
    public ResponseEntity<String> encrypt(@RequestBody @NotEmpty String strToEncrypt) {
        return ResponseEntity.ok(aesEncypter.encrypt(strToEncrypt,
                EncrypterConfig.config().encrypterPass(encryptationPass).salt(8).pepper(8).build()));
    }

    @PostMapping("/decrypt")
    public ResponseEntity<String> decrypter(@RequestBody @NotEmpty String strEncrypted) {
        return ResponseEntity.ok(aesEncypter.decrypt(strEncrypted,
                EncrypterConfig.config().encrypterPass(encryptationPass).salt(8).pepper(8).build()));
    }

}
