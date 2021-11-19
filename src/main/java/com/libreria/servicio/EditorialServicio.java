package com.libreria.servicio;

import com.libreria.Excepcion.Excepcion;
import com.libreria.entidades.Editorial;
import com.libreria.entidades.Foto;
import com.libreria.repositorio.EditorialRepositorio;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EditorialServicio {

    @Autowired
    private EditorialRepositorio editorialRepositorio;

    @Autowired
    private FotoServicio fotoServicio;

    @Transactional
    public void crearEditorial(String nombre, boolean alta, MultipartFile archivo) throws Excepcion {

        validarEditorialNombre(nombre);
        validarEditorialRepetido(nombre);

        Editorial editorial = new Editorial();
        editorial.setNombre(nombre);
        editorial.setAlta(true);

        Foto foto = fotoServicio.guardar(archivo);
        editorial.setFoto(foto);

        editorialRepositorio.save(editorial);
    }
//------MODIFICACIONES---- VER

    @Transactional
    public void modificarEditorialNombre(String nombre, String nombrenuevo) throws Excepcion {

        validarEditorialNombre(nombre);
        validarEditorialNombre(nombrenuevo);

        Editorial respuesta = editorialRepositorio.buscarPorNombre(nombre);
        if (respuesta != null) {

            respuesta.setNombre(nombrenuevo);

            editorialRepositorio.save(respuesta);
        } else {
            throw new Excepcion("No se encontró el nombre del Editorial");
        }
    }

    @Transactional
    public void modificarEditorialId(String id, String nombrenuevo, MultipartFile archivo) throws Excepcion {
        validarEditorialId(id);
        

        Optional<Editorial> respuesta = editorialRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Editorial editorial = respuesta.get();
            editorial.setNombre(nombrenuevo);

            String idFoto = null;
            if (editorial.getFoto() != null) {
                idFoto = editorial.getFoto().getId();
            }
            Foto foto = fotoServicio.actualizar(idFoto, archivo);
            editorial.setFoto(foto);

            editorialRepositorio.save(editorial);
        } else {
            throw new Excepcion("No se encontró el id del Editorial");
        }
    }

    //-------BAJAS 
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})

    public void darDeBaja(String id) throws Excepcion {
        Optional<Editorial> respuesta = editorialRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Editorial editorial = respuesta.get();
            editorial.setAlta(Boolean.FALSE);
            editorialRepositorio.save(editorial);
        } else {
            throw new Excepcion("No se encontro el editorial");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void darDeAlta(String id) throws Excepcion {
        Optional<Editorial> respuesta = editorialRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Editorial editorial = respuesta.get();
            editorial.setAlta(Boolean.TRUE);
            editorialRepositorio.save(editorial);
        } else {
            throw new Excepcion("No se encontro el autor");
        }
    }

//------BUSQUEDAS 
    public Editorial buscarEditorialId(String id) throws Excepcion {
        validarEditorialId(id);

        Optional<Editorial> respuesta = editorialRepositorio.findById(id);
        Editorial editorial = respuesta.get();
        if (respuesta.isPresent()) {
            return editorial;
        } else {
            throw new Excepcion("No se encontró el id del Editorial");
        }
    }

    private void buscarEditorialNombre(String nombre) throws Excepcion {

        validarEditorialNombre(nombre);
        Editorial respuesta = editorialRepositorio.buscarPorNombre(nombre);
        if (respuesta != null) {
            respuesta.getNombre();

            editorialRepositorio.save(respuesta);
        } else {
            throw new Excepcion("No se encontró el nombre del Editorial");
        }
    }

    //-------  VALIDACIONES
    private void validarEditorialNombre(String nombre) throws Excepcion {
        if (nombre == null || nombre.isEmpty()) {
            throw new Excepcion("El nombre del editorial no puede ser nulo");
        }
    }

    public void validarEditorialId(String id) throws Excepcion {
        if (id == null || id.isEmpty()) {
            throw new Excepcion("El nombre del editorial no puede ser nulo");
        }
    }

    private void validarEditorialRepetido(String nombre) throws Excepcion {
        Editorial respuesta = editorialRepositorio.buscarPorNombre(nombre);
        
        if (respuesta != null) {
            throw new Excepcion("La editorial con ese nombre ya existe");
        }
    }
}
