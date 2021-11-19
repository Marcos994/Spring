package com.libreria.servicio;

import com.libreria.Excepcion.Excepcion;
import com.libreria.entidades.Cliente;
import com.libreria.entidades.Foto;
import com.libreria.repositorio.ClienteRepositorio;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ClienteServicio {

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Autowired
    private FotoServicio fotoServicio;

    @Transactional
    public void crearCliente(MultipartFile archivo, String nombre, String apellido, Long documento, Long telefono) throws Excepcion {

        validarCliente(nombre, apellido, documento, telefono);

        Cliente cliente = new Cliente();

        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setDocumento(documento);
        cliente.setTelefono(telefono);
        cliente.setAlta(true);

        Foto foto = fotoServicio.guardar(archivo);
        cliente.setFoto(foto);

        clienteRepositorio.save(cliente);
    }

//-----------------------modificar------------

    @Transactional
    public void modificarclienteID(String id, String nombrenuevo, String apellidonuevo, Long documentonuevo, Long telefononuevo, MultipartFile archivo) throws Excepcion {
        validarClienteId(id);
        validarCliente(nombrenuevo,apellidonuevo,documentonuevo,telefononuevo);

        Optional<Cliente> respuesta = clienteRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Cliente cliente = respuesta.get();
            cliente.setNombre(nombrenuevo);
            cliente.setApellido(apellidonuevo);
            cliente.setDocumento(documentonuevo);
            cliente.setTelefono(telefononuevo);

            String idFoto = null;
            if (cliente.getFoto() != null) {
                idFoto = cliente.getFoto().getId();
            }
            Foto foto = fotoServicio.actualizar(idFoto, archivo);
            cliente.setFoto(foto);

            clienteRepositorio.save(cliente);
        } else {
            throw new Excepcion("No se encontró el id del Cliente");
        }
    }
    //---------------Busqueda-----------
    public Cliente buscarClienteId(String id) throws Excepcion {
        validarClienteId(id);

        Optional<Cliente> respuesta = clienteRepositorio.findById(id);
        Cliente cliente = null;
        if (respuesta.isPresent()) {
            cliente = respuesta.get();
        } else {
            throw new Excepcion("No se encontró el id del Cliente");
        }
        return cliente;
    }

    // ----------- ALTA y baja---------------------
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void darDeBaja(String id) throws Excepcion {
        Optional<Cliente> respuesta = clienteRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Cliente cliente = respuesta.get();
            cliente.setAlta(Boolean.FALSE);
            clienteRepositorio.save(cliente);
        } else {
            throw new Excepcion("No se encontro el cliente");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void darDeAlta(String id) throws Excepcion {
        Optional<Cliente> respuesta = clienteRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Cliente cliente = respuesta.get();
            cliente.setAlta(Boolean.TRUE);
            clienteRepositorio.save(cliente);
        } else {
            throw new Excepcion("No se encontro el cliente");
        }
    }
    
    //-------------VALIDACIONES--------------
private void validarCliente(String nombre, String apellido, Long documento, Long telefono) throws Excepcion {

        if (nombre == null || nombre.isEmpty()) {
            throw new Excepcion("El nombre del cliente no puede ser nulo");
        }
        if (apellido == null || apellido.isEmpty()) {
            throw new Excepcion("El apellido del cliente no puede ser nulo");
        }
        if (documento == null || documento <= 0) {
            throw new Excepcion("Se debe especificar el año de Alta");
        }
        if (telefono == null || telefono < 0) {
            throw new Excepcion("Debe indicar el número de ejemplares");
        }
    }

    private void validarClienteId(String id) throws Excepcion {

        if (id == null || id.isEmpty()) {
            throw new Excepcion("El nombre del cliente no puede ser nulo");
        }
    }

    private void validarClienteNombre(String nombre, String apellido) throws Excepcion {

        if (nombre == null || nombre.isEmpty()) {
            throw new Excepcion("El nombre del cliente no puede ser nulo");
        }
        if (apellido == null || apellido.isEmpty()) {
            throw new Excepcion("El apellido del cliente no puede ser nulo");
        }
    }

}
