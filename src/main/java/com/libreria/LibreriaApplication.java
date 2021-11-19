package com.libreria;

import com.libreria.Excepcion.Excepcion;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibreriaApplication {

	public static void main(String[] args) throws Excepcion {
		SpringApplication.run(LibreriaApplication.class, args);
                
             
	}
}
