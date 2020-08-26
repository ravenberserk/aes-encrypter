package com.example.aesencrypter.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion para el encriptado/desencriptado de las cadenas de textos. Se corespondera con los parametros definidos
 * en el fichero {@code bootstrap.yml}.
 *
 * @author jgrande
 * @since 1.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "encrypterconfig")
public class EncrypterConfig {

    @NonNull
    private String cipherAlgorithm;

    @NonNull
    private String pass;

    @NonNull
    private String algorithm;

    private int iv;
    private int salt;
    private int pepper;

}
