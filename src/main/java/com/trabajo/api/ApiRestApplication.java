package com.trabajo.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicacion Spring Boot.
 *
 * @SpringBootApplication combina:
 * - @Configuration: Indica que es una clase de configuracion
 * - @EnableAutoConfiguration: Activa la auto-configuracion de Spring Boot
 * - @ComponentScan: Escanea componentes en este paquete y subpaquetes
 */
@SpringBootApplication
public class ApiRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiRestApplication.class, args);
    }

}
