package com.libreria.servicio;

import com.libreria.Excepcion.Excepcion;
import com.libreria.entidades.Autor;
import com.libreria.entidades.Cliente;
import com.libreria.entidades.Editorial;
import com.libreria.entidades.Foto;
import com.libreria.entidades.Libro;
import com.libreria.repositorio.AutorRepositorio;
import com.libreria.repositorio.EditorialRepositorio;
import com.libreria.repositorio.LibroRepositorio;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LibroServicio {

    @Autowired
    private LibroRepositorio libroRepositorio;
    @Autowired
    private AutorRepositorio autorRepositorio;
    @Autowired
    private EditorialRepositorio editorialRepositorio;
    @Autowired
    private FotoServicio fotoServicio;
    @Autowired
    private ClienteServicio clienteServicio;
    @Autowired
    private AutorServicio autorServicio;
    @Autowired
    private EditorialServicio editorialServicio;

    @Transactional
    public void crearLibro(MultipartFile archivo, String titulo, Integer anio, Integer ejemplares, boolean alta, String idautor, String ideditorial) throws Excepcion {

        for (int i = 0; i < 2; ++i) {
            
            Autor autor = autorRepositorio.findById(idautor).get();
            Editorial editorial = editorialRepositorio.findById(ideditorial).get();

            validarLibro(titulo, anio, ejemplares);

            Libro libro = new Libro();
            libro.setTitulo(titulo);
            libro.setAnio(anio);
            libro.setEjemplares(ejemplares);
            libro.setEjemplaresRestantes(ejemplares);
            libro.setEjemplaresPrestados(0);
            libro.setAlta(true);
            libro.setAutor(autor);
            libro.setEditorial(editorial);

            Foto foto = fotoServicio.guardar(archivo);
            libro.setFoto(foto);

            libroRepositorio.save(libro);
        }
    }

    //---------------------MODIFICACIONES---------------------------
    @Transactional
    public void modificarLibro(String idLibro, String titulo, Integer anio, Integer ejemplares, String idautor, String ideditorial, MultipartFile archivo) throws Excepcion {
        validarLibroAnio(anio);
        validarLibro(titulo, anio, ejemplares);
        Autor autor = autorRepositorio.findById(idautor).get();
        Editorial editorial = editorialRepositorio.findById(ideditorial).get();

        Libro libro = buscarLibroId(idLibro);
        if (libro != null) {

            libro.setTitulo(titulo);
            libro.setAutor(autor);
            libro.setEditorial(editorial);
            libro.setAnio(anio);
            libro.setEjemplares(ejemplares);

            String idfoto = libro.getFoto().getId();

            Foto foto = fotoServicio.actualizar(idfoto, archivo);
            libro.setFoto(foto);

            libroRepositorio.save(libro);
        } else {
            throw new Excepcion("No se encontro el Libro");
        }
    }

    //----------------PRESTAMOS-----------------------
    @Transactional
    public void prestarLibro(String idLibro, String idCliente) throws Excepcion {
        Integer ejemplaresPresta = 1;
        validarLibroId(idLibro);

        Libro libro = buscarLibroId(idLibro);
        Cliente cliente = clienteServicio.buscarClienteId(idCliente);
        libro.setCliente(cliente);

        if (libro != null) {
            if (libro.getEjemplaresRestantes() <= 0) {
                throw new Excepcion("No quedan libros para prestar");
            }
            if (ejemplaresPresta <= libro.getEjemplaresRestantes()) {
                libro.setEjemplaresRestantes(libro.getEjemplaresRestantes() - ejemplaresPresta);
                libro.setEjemplaresPrestados(libro.getEjemplaresPrestados() + ejemplaresPresta);
            } else {
                throw new Excepcion("No hay suficientes libros para prestar");
            }
        }
        libroRepositorio.save(libro);
    }

    //----------------DEVOLUCIONES-----------------------
    @Transactional
    public void devolverLibro(String idLibro, String idcliente) throws Excepcion {
        Integer ejemplaresVuelta = 1;
        validarLibroId(idLibro);

        Libro libro = buscarLibroId(idLibro);

        Integer valor = libro.getEjemplaresRestantes() + ejemplaresVuelta;

        if (libro != null) {
            if (libro.getEjemplares() >= valor) {
                libro.setEjemplaresRestantes(libro.getEjemplaresRestantes() + ejemplaresVuelta);
                libro.setEjemplaresPrestados(libro.getEjemplaresPrestados() - ejemplaresVuelta);
            } else {
                throw new Excepcion("Se estan devolviendo mas libros que los prestados");
            }
        }
        if (libro.getEjemplaresPrestados() == 0) {
            libro.setCliente(null);
        }
        libroRepositorio.save(libro);
    }
//-------------------BAJAS----------------------

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Excepcion.class})
    public void darDebaja(String idLibro) throws Excepcion {
        Optional<Libro> respuesta = libroRepositorio.findById(idLibro);

        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();
            libro.setAlta(Boolean.FALSE);
            libroRepositorio.save(libro);
        } else {
            throw new Excepcion("No se encontro el libro");
        }
    }

    //------------ALTA-------------
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Excepcion.class})
    public void darDeAlta(String idLibro) throws Excepcion {
        Optional<Libro> respuesta = libroRepositorio.findById(idLibro);

        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();
            libro.setAlta(Boolean.TRUE);
            libroRepositorio.save(libro);
        } else {
            throw new Excepcion("No se encontro el Libro");
        }
    }

    //------BUSQUEDA----------------
    public Libro buscarLibroId(String id) throws Excepcion {
        validarLibroId(id);

        Optional<Libro> respuesta = libroRepositorio.findById(id);
        Libro libro = null;
        if (respuesta.isPresent()) {
            libro = respuesta.get();
        } else {
//            throw new Excepcion("No se encontró el id del Libro");
        }
        return libro;
    }

    @Transactional
    public List<Libro> busquedaLibroNombre(String titulo) throws Excepcion {
        List<Libro> respuesta = libroRepositorio.buscarLibrosPorTitulo(titulo);
//        List<Libro> respuesta = libroRepositorio.buscarLibroNulo(titulo);
        return respuesta;
    }

    @Transactional
    public List<Libro> busquedaLibroAutor(String titulo) throws Excepcion {

        List<Libro> respuesta = libroRepositorio.buscarLibrosPorAutorNombre(titulo);
        return respuesta;
    }

    @Transactional
    public List<Libro> busquedaLibroEditorial(String titulo) throws Excepcion {
        List<Libro> respuesta = libroRepositorio.buscarLibrosPorEditorialNombre(titulo);
        return respuesta;
    }

    public List<Libro> buscarPorParametro(String titulo) {
        return libroRepositorio.searchAssetsByParam(titulo);
    }

    @Transactional
    public List<Libro> buscarLibro(String titulo) throws Excepcion {
        List<Libro> respuesta = null;
        List<Libro> libros = libroRepositorio.buscarLibrosPorTitulo(titulo);
        List<Libro> autores = libroRepositorio.buscarLibrosPorAutorNombre(titulo);
        List<Libro> editoriales = libroRepositorio.buscarLibrosPorEditorialNombre(titulo);

        if (libros != null || libros.isEmpty()) {
            return libros;
        }
        if (autores != null || libros.isEmpty()) {
            return autores;
        }
        if (editoriales != null || libros.isEmpty()) {
            return editoriales;
        }
        return respuesta;
    }

    //------------VALIDACIONES
    private void validarLibro(String titulo, Integer anio, Integer ejemplares) throws Excepcion {
        if (titulo == null || titulo.isEmpty()) {
            throw new Excepcion("El titulo del libro no puede ser nulo");
        }
        if (anio == null || anio.toString().length() < 4 || anio > 2021) {
            throw new Excepcion("Se debe especificar el año de Alta");
        }
        if (ejemplares == null || ejemplares < 0) {
            throw new Excepcion("Debe indicar el número de ejemplares");
        }
    }

    private void validarLibroAnio(Integer anio) throws Excepcion {
        if (anio == null || anio <= 0 || anio >= 2021) {
            throw new Excepcion("El anio no puede ser nulo");
        }
    }

    private void validarLibroId(String id) throws Excepcion {
        if (id == null || id.isEmpty()) {
            throw new Excepcion("El titulo del libro no puede ser nulo");
        }
    }

    private void validarLibroTitulo(String titulo) throws Excepcion {
        if (titulo == null || titulo.isEmpty()) {
            throw new Excepcion("El nombre del autor no puede ser nulo");
        }
    }
}
