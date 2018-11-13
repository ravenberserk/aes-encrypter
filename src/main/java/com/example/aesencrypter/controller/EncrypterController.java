package com.example.aesencrypter.controller;


import com.example.aesencrypter.utils.AESEncrypter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Controller en el que se implementaran los endPoints para encriptar/desencriptar la cadena de texto.
 *
 * @author jgrande
 * @since 13/11/2018
 */
@RestController
public class EncrypterController {

    @Value("${encryption.pass}")
    private String encryptationPass;

    @Autowired
    private AESEncrypter aesEncypter;

    @PostMapping("/encrypt")
    public ResponseEntity<String> encrypt(@RequestBody @NotEmpty String strToEncrypt){
        return ResponseEntity.ok(aesEncypter.encrypt(strToEncrypt));
    }

    @PostMapping("/decrypter")
    public ResponseEntity<String> decrypter(@RequestBody @NotEmpty String strEncrypted){
        return ResponseEntity.ok(aesEncypter.decrypt(strEncrypted));
    }

}