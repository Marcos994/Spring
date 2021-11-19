package com.libreria.servicio;

import com.libreria.Excepcion.Excepcion;
import com.libreria.entidades.Autor;
import com.libreria.entidades.Foto;
import com.libreria.repositorio.AutorRepositorio;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AutorServicio {

    @Autowired
    private AutorRepositorio autorRepositorio;
    @Autowired
    private FotoServicio fotoServicio;

    @Transactional
    public void crearAutor(String nombre, String apellido, boolean alta, MultipartFile archivo) throws Excepcion {
        validarAutorNombre(nombre);
        validarAutorNombre(apellido);
        
        String completo = nombre +" "+ apellido;
        validarAutorRepetido(completo);
        
        Autor autor = new Autor();
        autor.setNombre(completo);
        autor.setAlta(true);
        
        Foto foto = fotoServicio.guardar(archivo);
        autor.setFoto(foto);

        autorRepositorio.save(autor);
    }
//---------MODIFICAR AUTOR------
    @Transactional
    public void modificarAutorID(String id,String nombrenuevo, MultipartFile archivo ) throws Excepcion {
        validarAutorId(id);
        validarAutorNombre(nombrenuevo);

        Optional<Autor> respuesta = autorRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Autor autor = respuesta.get();
            autor.setNombre(nombrenuevo);
            
            String idFoto = null;
            if (autor.getFoto() != null) {
                idFoto = autor.getFoto().getId();
            }
            Foto foto = fotoServicio.actualizar(idFoto, archivo);
            autor.setFoto(foto);

            autorRepositorio.save(autor);
        } else {
            throw new Excepcion("No se encontró el id del Autor");
        }
    }
    //-------------------BAJA--------------------
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Excepcion.class})
    public void darDebaja(String idAutor) throws Excepcion{
        Optional<Autor> respuesta = autorRepositorio.findById(idAutor);       
        
        if(respuesta.isPresent()){
            Autor autor = respuesta.get();
            autor.setAlta(Boolean.FALSE);
            autorRepositorio.save(autor);
            } else {
                throw new Excepcion("No se encontro el libro");
            }
        }
    //------------ALTA-------------
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Excepcion.class})
    public void darDeAlta(String idAutor) throws Excepcion{
        Optional<Autor> respuesta = autorRepositorio.findById(idAutor);       
        
        if(respuesta.isPresent()){
            Autor autor = respuesta.get();
            autor.setAlta(Boolean.TRUE);
            autorRepositorio.save(autor);
            } else {
                throw new Excepcion("No se encontro el libro");
            }
        }
    //------------BUSQUEDAS
    public Autor buscarAutorId(String id) throws Excepcion {
        validarAutorId(id);

        Optional<Autor> respuesta = autorRepositorio.findById(id);
        Autor autor = null;
        if (respuesta.isPresent()) {
            autor = respuesta.get();
        } else {
            throw new Excepcion("No se encontró el id del Autor");
        }
        return autor;
    }

    public void buscarAutorNombre(String nombre) throws Excepcion {
        validarAutorNombre(nombre);
        
        List<Autor> respuesta = autorRepositorio.listarAutoresPorNombre(nombre);
        if (respuesta != null) {
            for (Autor a : respuesta) {
                a.getNombre();
            }
        } else {
            throw new Excepcion("No se encontró el nombre del Autor");
        }
    }
    //----------------IMPRIMIR LISTA
    @Transactional
    public List<Autor> listarAutores() throws Exception{
        List<Autor> autores = autorRepositorio.listarAutores();
        
        return autores;
    }

    //----------------VALIDACION DE DATOS
    public void validarAutorNombre(String nombre) throws Excepcion {
        if (nombre == null||nombre.isEmpty()) {
            throw new Excepcion("El nombre del autor no puede ser nulo");
        }
    }

    public void validarAutorId(String id) throws Excepcion {
        if (id == null || id.isEmpty()) {
            throw new Excepcion("El nombre del autor no puede ser nulo");
        }
    }
    private void validarAutorRepetido(String completo) throws Excepcion {
        Autor respuesta = autorRepositorio.buscarPorNombre(completo);
        if (respuesta != null) {
            throw new Excepcion("La editorial con ese nombre ya existe");
        }
    }
}
