package com.libreria.Controlador;

import com.libreria.Excepcion.Excepcion;
import com.libreria.entidades.Autor;
import com.libreria.entidades.Cliente;
import com.libreria.entidades.Editorial;
import com.libreria.entidades.Libro;
import com.libreria.servicio.AutorServicio;
import com.libreria.servicio.ClienteServicio;
import com.libreria.servicio.EditorialServicio;
import com.libreria.servicio.LibroServicio;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/foto")
public class FotoControlador {

    @Autowired
    private LibroServicio libroServicio;
    @Autowired
    private AutorServicio autorServicio;
    @Autowired
    private EditorialServicio editorialServicio;
    @Autowired
    private ClienteServicio clienteServicio;

    @GetMapping("/libro")
    public ResponseEntity<byte[]> foto(@RequestParam String id) {

        try {
            Libro libro = libroServicio.buscarLibroId(id);
            if (libro.getFoto() == null) {
                throw new Excepcion("El libro no tiene una imagen");
            }
            byte[] foto = libro.getFoto().getContenido();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return new ResponseEntity<>(foto, headers, HttpStatus.OK);
        } catch (Excepcion ex) {
            Logger.getLogger(FotoControlador.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/autor")
    public ResponseEntity<byte[]> fotoAutor(@RequestParam String id) {
        try {
            Autor autor = autorServicio.buscarAutorId(id);
            if (autor.getFoto() == null) {
                throw new Excepcion("El Autor no tiene una imagen");
            }

            byte[] foto = autor.getFoto().getContenido();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return new ResponseEntity<>(foto, headers, HttpStatus.OK);
        } catch (Excepcion ex) {
            Logger.getLogger(FotoControlador.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/editorial")
    public ResponseEntity<byte[]> fotoEditorial(@RequestParam String id) {
        try {
            Editorial editorial = editorialServicio.buscarEditorialId(id);
            if (editorial.getFoto() == null) {
                throw new Excepcion("La Editorial  no tiene una imagen");
            }

            byte[] foto = editorial.getFoto().getContenido();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(foto, headers, HttpStatus.OK);
        } catch (Excepcion ex) {
            Logger.getLogger(FotoControlador.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/cliente")
    public ResponseEntity<byte[]> fotoCliente(@RequestParam String id) {

        try {
            Cliente cliente = clienteServicio.buscarClienteId(id);
            if (cliente.getFoto() == null) {
                throw new Excepcion("El cliente no tiene una imagen");
            }
            byte[] foto = cliente.getFoto().getContenido();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return new ResponseEntity<>(foto, headers, HttpStatus.OK);
        } catch (Excepcion ex) {
            Logger.getLogger(FotoControlador.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
