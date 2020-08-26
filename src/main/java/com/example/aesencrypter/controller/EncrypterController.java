package com.example.aesencrypter.controller;


import com.example.aesencrypter.utils.AESEncrypter;
import javax.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller en el que se implementaran los endPoints para encriptar/desencriptar la cadena de texto.
 *
 * @author jgrande
 * @since 1.0
 */
@RestController
public class EncrypterController {

    @Autowired
    private AESEncrypter aesEncypter;

    @PostMapping("/encrypt")
    public ResponseEntity<String> encrypt(@RequestBody @NotEmpty String strToEncrypt) {
        return ResponseEntity.ok(aesEncypter.encrypt(strToEncrypt));
    }

    @PostMapping("/decrypt")
    public ResponseEntity<String> decrypter(@RequestBody @NotEmpty String strEncrypted) {
        return ResponseEntity.ok(aesEncypter.decrypt(strEncrypted));
    }

}
