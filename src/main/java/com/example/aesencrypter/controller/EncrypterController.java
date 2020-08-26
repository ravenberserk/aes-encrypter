package com.example.aesencrypter.controller;


import com.example.aesencrypter.encrypter.AESEncrypter;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller en el que se implementaran los endPoints para encriptar/desencriptar la cadena de texto.
 *
 * @author jgrande
 * @since 1.0
 */
@RestController
public class EncrypterController {

    @Autowired
    private AESEncrypter aesEncrypter;

    @PostMapping("/encrypt")
    public ResponseEntity<String> encrypt(@RequestBody @NotEmpty String strToEncrypt) {
        try {
            return ResponseEntity.ok(aesEncrypter.encrypt(strToEncrypt));
        } catch (BadPaddingException | IllegalBlockSizeException | IllegalArgumentException exc) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exc.getLocalizedMessage(), exc);
        }
    }

    @PostMapping("/decrypt")
    public ResponseEntity<String> decrypter(@RequestBody @NotEmpty String strEncrypted) {
        try {
            return ResponseEntity.ok(aesEncrypter.decrypt(strEncrypted));
        } catch (BadPaddingException | IllegalBlockSizeException | IllegalArgumentException exc) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exc.getLocalizedMessage(), exc);
        }
    }

}
