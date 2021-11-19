package com.libreria.Controlador;

import com.libreria.Excepcion.Excepcion;
import com.libreria.entidades.Autor;
import com.libreria.entidades.Cliente;
import com.libreria.entidades.Editorial;
import com.libreria.entidades.Libro;
import com.libreria.repositorio.AutorRepositorio;
import com.libreria.repositorio.ClienteRepositorio;
import com.libreria.repositorio.EditorialRepositorio;
import com.libreria.repositorio.LibroRepositorio;
import com.libreria.servicio.AutorServicio;
import com.libreria.servicio.ClienteServicio;
import com.libreria.servicio.EditorialServicio;
import com.libreria.servicio.LibroServicio;
import com.libreria.servicio.UsuarioServicio;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/")
public class PortalControlador {

    @Autowired
    private AutorServicio autorServicio;
    @Autowired
    private AutorRepositorio autorRepositorio;
    @Autowired
    private UsuarioServicio usuarioServicio;
    @Autowired
    private EditorialServicio editorialServicio;
    @Autowired
    private EditorialRepositorio editorialRepositorio;
    @Autowired
    private LibroServicio libroServicio;
    @Autowired
    private LibroRepositorio libroRepositorio;
    @Autowired
    private ClienteServicio clienteServicio;
    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @GetMapping("/")
    public String index() {
        return "index.html";
    }
//---------------REGISTRAR-----------------

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/registro")
    public String registro() {
        return "registro.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/registrar")
    public String registrar(ModelMap modelo, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String mail, @RequestParam String clave1, @RequestParam String clave2) {
        try {
            usuarioServicio.registrar(null, nombre, apellido, mail, clave2);
            modelo.put("exito", "Registro Exitoso!");
            return "login.html";
        } catch (Excepcion ex) {
            modelo.put("error", "Debe completar todos los campos");
            return "registro.html";
        }
    }

    //-----------------------LOGIN--------------
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/inicio")
    public String inicio() {
        return "inicio.html";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, @RequestParam(required = false) String logout, ModelMap modelo) {
        if (error != null) {
            modelo.put("error", "Nombre de usuario o clave incorrectos");
        }
        if (logout != null) {
            modelo.put("logout", "Has salido correctamente");
        }
        return "login.html";
    }

    //---------------------CLIENTES----------------------
//------------------REGISTRAR CLIENTE-----------
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/registrocliente")
    public String registrocliente() {
        return "registrocliente.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/registrocliente")
    public String registrocliente(ModelMap modelo, MultipartFile archivo, @RequestParam String nombre, @RequestParam String apellido, @RequestParam Long documento, @RequestParam Long telefono) {
        try {
            clienteServicio.crearCliente(archivo, nombre, apellido, documento, telefono);
            modelo.put("exito", "Registro Exitoso!");
            return "registrocliente";
        } catch (Excepcion e) {
            modelo.put("error", e.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("documento", documento);
            modelo.put("telefono", telefono);
            return "registrocliente.html";
        }
    }
    //--------------MODIFICAR CLIENTE--------------

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/iniciocliente")
    public String iniciocliente(ModelMap modelo) {
        List<Cliente> clientes = clienteRepositorio.findAll();
        modelo.put("clientes", clientes);
        return "iniciocliente.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/modificarcliente/{id}")
    public String modificarcliente(@PathVariable String id, ModelMap modelo) throws Excepcion {
        modelo.put("clientes", clienteServicio.buscarClienteId(id));
        return "/modificarcliente";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/modificarcliente/{id}")
    public String modificarcliente(ModelMap modelo, @PathVariable String id, @RequestParam String nombre, @RequestParam String apellido, @RequestParam Long documento, @RequestParam Long telefono, MultipartFile archivo) throws Excepcion {
        try {
            clienteServicio.modificarclienteID(id, nombre, apellido, documento, telefono, archivo);
            modelo.put("exito", "Cliente modificada con exito");
        } catch (Excepcion ex) {
            modelo.put("error", "Ingrese el nombre del Cliente");
            modelo.put("autores", clienteServicio.buscarClienteId(id));
            modelo.put("clientes", clienteServicio.buscarClienteId(id));
            return "/modificarcliente";
        }
        List<Cliente> clientes = clienteRepositorio.findAll();
        modelo.put("clientes", clientes);
        return "iniciocliente";
    }

    //---------------------AUTORES--------------
    @GetMapping("/iniautor")
    public String iniautor(ModelMap modelo) {
        List<Autor> autores = autorRepositorio.findAll();
        modelo.put("autores", autores);

        return "iniautor.html";
    }

    //--------REGISTRO DE AUTORES----------------------
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/registroautor")
    public String registroautor() {
        return "registroautor.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/registroautor")
    public String registroautor(ModelMap modelo, @RequestParam String nombre, @RequestParam String apellido, MultipartFile archivo) {
        try {
            autorServicio.crearAutor(nombre, apellido, true, archivo);
            modelo.put("exito", "Autor registrado con exito");
        } catch (Excepcion ex) {
            modelo.put("error", "Error al cargar Autor, ya existe o faltan datos");
            return "registroautor.html";
        }
        return "registroautor.html";
    }

    //--------------MODIFICAR AUTOR--------------
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/inicioautor")
    public String inicioautor(ModelMap modelo) {
        List<Autor> autores = autorRepositorio.findAll();
        modelo.put("autores", autores);
        return "inicioautor.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/modificarautor/{id}")
    public String modificarautor(@PathVariable String id, ModelMap modelo) throws Excepcion {
        modelo.put("autores", autorServicio.buscarAutorId(id));
        return "/modificarautor";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/modificarautor/{id}")
    public String modificarautor(ModelMap modelo, @PathVariable String id, @RequestParam String nombre, MultipartFile archivo) throws Excepcion {
        try {
            autorServicio.modificarAutorID(id, nombre, archivo);
            modelo.put("exito", "Autor modificada con exito");
        } catch (Excepcion ex) {
            modelo.put("error", "Ingrese el nombre del Autor");
            modelo.put("autores", autorServicio.buscarAutorId(id));
            return "/modificarautor";
        }
        List<Autor> autores = autorRepositorio.findAll();
        modelo.put("autores", autores);
        return "inicioautor";
    }

    //---------------EDITORIALES-------------
    @GetMapping("/inieditorial")
    public String inieditorial(ModelMap modelo) {

        List<Editorial> editoriales = editorialRepositorio.findAll();
        modelo.put("editoriales", editoriales);
        return "inieditorial.html";
    }

    //-------REGISTRO DE EDITORIALES---------------------
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/registroeditorial")
    public String registroeditorial() {
        return "registroeditorial.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/registroeditorial")
    public String registroeditorial(ModelMap modelo, @RequestParam String nombre, MultipartFile archivo) {
        try {
            editorialServicio.crearEditorial(nombre, true, archivo);
            modelo.put("exito", "Editorial registrada con exito");
        } catch (Excepcion ex) {
            modelo.put("error", "Error al cargar Editorial, ya existe o faltan datos");
            return "registroeditorial.html";
        }
        return "registroeditorial.html";
    }

    //--------------MODIFICAR EDITORIAL--------------
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/inicioeditorial")
    public String inicioeditorial(ModelMap modelo) {

        List<Editorial> editoriales = editorialRepositorio.findAll();
        modelo.put("editoriales", editoriales);
        return "inicioeditorial.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/modificareditorial/{id}")
    public String modificareditorial(@PathVariable String id, ModelMap modelo) throws Excepcion {
        modelo.put("editoriales", editorialServicio.buscarEditorialId(id));
        return "/modificareditorial";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/modificareditorial/{id}")
    public String modificareditorial(ModelMap modelo, @PathVariable String id, @RequestParam String nombre, MultipartFile archivo) throws Excepcion {
        try {
            editorialServicio.modificarEditorialId(id, nombre, archivo);
            modelo.put("exito", "Editorial modificada con exito");
        } catch (Excepcion ex) {
            modelo.put("error", "Ingrese el nombre de la Editorial ");
            modelo.put("editoriales", editorialServicio.buscarEditorialId(id));
            return "/modificareditorial";
        }
        List<Editorial> editoriales = editorialRepositorio.findAll();
        modelo.put("editoriales", editoriales);
        return "inicioeditorial";
    }

    //---------------LIBROS---------------
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/iniciolibro")
    public String iniciolibro(ModelMap modelo) {
        List<Libro> libros = libroRepositorio.findAll();
        modelo.put("libros", libros);
        return "iniciolibro.html";
    }

    //-----------NO ADMINISTRADORES
    @GetMapping("/inilibro")
    public String inilibro(ModelMap modelo) {
        List<Libro> libros = libroRepositorio.findAll();
        modelo.put("libros", libros);
        return "inilibro.html";
    }

    //-------REGISTRO DE LIBROS
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/registrolibro")
    public String registrolibro(ModelMap modelo) {
        List<Editorial> editoriales = editorialRepositorio.ListarEditorialesAlta();
        modelo.put("editoriales", editoriales);
        List<Autor> autores = autorRepositorio.findAll();
        modelo.put("autores", autores);

        return "registrolibro";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/registrolibro")
    public String registrolibro(ModelMap modelo, MultipartFile archivo, @RequestParam String titulo, @RequestParam String idautor, @RequestParam String ideditorial, @RequestParam Integer anio, @RequestParam Integer ejemplares) {
        try {
            libroServicio.crearLibro(archivo, titulo, anio, ejemplares, true, idautor, ideditorial);
            modelo.put("exito", "Libro registrado con exito");
            List<Editorial> editoriales = editorialRepositorio.ListarEditorialesAlta();
            modelo.put("editoriales", editoriales);
            List<Autor> autores = autorRepositorio.findAll();
            modelo.put("autores", autores);

        } catch (Excepcion ex) {
            List<Editorial> editoriales = editorialRepositorio.findAll();
            modelo.put("editoriales", editoriales);
            List<Autor> autores = autorRepositorio.findAll();
            modelo.put("autores", autores);

            modelo.put("error", "Error en la carga de Libro, faltan datos");
            return "registrolibro";
        }
        return "registrolibro";
    }

//    //--------------MODIFICAR Libros--------------
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/modificarlibro/{id}")
    public String modificarlibro(@PathVariable String id, ModelMap modelo) throws Excepcion {

        List<Editorial> editoriales = editorialRepositorio.findAll();
        modelo.put("editoriales", editoriales);
        List<Autor> autores = autorRepositorio.findAll();
        modelo.put("autores", autores);
        modelo.put("libros", libroServicio.buscarLibroId(id));
        return "/modificarlibro";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/modificarlibro/{id}")
    public String modificarlibro(@PathVariable String id, @RequestParam String titulo, @RequestParam Integer anio, @RequestParam Integer ejemplares, @RequestParam String idautor, @RequestParam String ideditorial, ModelMap modelo, MultipartFile archivo) throws Excepcion {
        try {
            libroServicio.modificarLibro(id, titulo, anio, ejemplares, idautor, ideditorial, archivo);
            modelo.put("exito", "Libro modificado con exito");
            List<Libro> libros = libroRepositorio.findAll();
            modelo.put("libros", libros);
            return "iniciolibro.html";

        } catch (Excepcion ex) {
            modelo.put("error", ex.getMessage());
            List<Editorial> editoriales = editorialRepositorio.findAll();
            modelo.put("editoriales", editoriales);
            List<Autor> autores = autorRepositorio.findAll();
            modelo.put("autores", autores);
            modelo.put("libros", libroServicio.buscarLibroId(id));
            return "/modificarlibro";
        }
    }
//    --------------------BUSQUEDA--------------------------
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/busqueda")
    public String busquedaLibroNombre(ModelMap modelo, @RequestParam String titulo) throws Excepcion {

        List<Libro> libros = libroServicio.buscarPorParametro(titulo);

        if (libros != null) {
            modelo.put("libros", libros);
            return "/busqueda";
        }
        return "/busqueda";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/busqueda")
    public String busquedaLibroNombre(@RequestParam String titulo, @RequestParam String valor, ModelMap modelo) throws Excepcion {
        try {
            modelo.put("exito", "Libro modificado con exito");
            List<Libro> libros = libroServicio.busquedaLibroNombre(titulo);
            List<Libro> autores = libroServicio.busquedaLibroAutor(titulo);
            List<Libro> editoriales = libroServicio.busquedaLibroEditorial(titulo);
            modelo.put("libros", libros);
            modelo.put("libros", autores);
            modelo.put("libros", editoriales);
            return "busqueda.html";

        } catch (Excepcion ex) {
            modelo.put("error", ex.getMessage());
            return "/inicio";
        }
    }

    //-------------------PRESTAMO LIBROS------------
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/prestar/{id}")
    public String prestar(@PathVariable String id, ModelMap modelo) throws Excepcion {
        Libro libro = libroRepositorio.buscarLibrosPorId(id);
        modelo.put("libros", libro);
        List<Cliente> clientes = clienteRepositorio.findAll();
        modelo.put("clientes",clientes);
        return "prestar";
    }
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/prestar/{id}")
    public String prestar(@PathVariable String id, @RequestParam String idcliente, ModelMap modelo) throws Excepcion {
        try {
            libroServicio.prestarLibro(id,idcliente);
            modelo.put("exito", "Libro modificado con exito");
            List<Libro> libros = libroRepositorio.findAll();
            modelo.put("libros", libros);
            return "iniciolibro.html";

        } catch (Excepcion ex) {
            modelo.put("error", ex.getMessage());
            List<Editorial> editoriales = editorialRepositorio.findAll();
            modelo.put("editoriales", editoriales);
            List<Autor> autores = autorRepositorio.findAll();
            modelo.put("autores", autores);
            modelo.put("libros", libroServicio.buscarLibroId(id));
            return "/prestar";
        }
    }

    //----------------DEVOLVER LIBROS------------
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/devolverlibro/{id}")
    public String devolver(@PathVariable String id, ModelMap modelo) throws Excepcion {
        Libro libro = libroRepositorio.buscarLibrosPorId(id);
        modelo.put("libros", libro);
        List<Cliente> clientes = clienteRepositorio.findAll();
        modelo.put("clientes",clientes);
        return "devolverlibro";
    }
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/devolverlibro/{id}")
    public String devolver(@PathVariable String id, @RequestParam String idcliente, ModelMap modelo) throws Excepcion {
        try {
            libroServicio.devolverLibro(id,idcliente);
            modelo.put("exito", "Libro modificado con exito");
            List<Libro> libros = libroRepositorio.findAll();
            modelo.put("libros", libros);
            return "iniciolibro.html";

        } catch (Excepcion ex) {
            modelo.put("error", ex.getMessage());
            List<Editorial> editoriales = editorialRepositorio.findAll();
            modelo.put("editoriales", editoriales);
            List<Autor> autores = autorRepositorio.findAll();
            modelo.put("autores", autores);
            modelo.put("libros", libroServicio.buscarLibroId(id));
            return "/devolverlibro";
        }
    }

    //---------------------BAJA LIBRO-------------------
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/baja/{id}")
    public String baja(@PathVariable String id, ModelMap modelo) throws Excepcion {
        try {
            libroServicio.darDebaja(id);
            return "redirect:/iniciolibro";
        } catch (Excepcion ex) {
            return "redirect:/";
        }
    }

    //---------------------ALTA LIBRO-------------------
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/alta/{id}")
    public String alta(@PathVariable String id, ModelMap modelo) throws Excepcion {
        try {
            libroServicio.darDeAlta(id);
            return "redirect:/iniciolibro";
        } catch (Excepcion ex) {
            return "redirect:/";
        }
    }

    //---------------------BAJA AUTOR-------------------
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/bajaAutor/{id}")
    public String bajaAutor(@PathVariable String id, ModelMap modelo) throws Excepcion {
        try {
            autorServicio.darDebaja(id);
            return "redirect:/inicioautor";
        } catch (Excepcion ex) {
            return "redirect:/";
        }
    }

    //---------------------ALTA AUTOR-------------------
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/altaAutor/{id}")
    public String altaAutor(@PathVariable String id, ModelMap modelo) throws Excepcion {
        try {
            autorServicio.darDeAlta(id);
            return "redirect:/inicioautor";
        } catch (Excepcion ex) {
            return "redirect:/";
        }
    }

    //---------------------BAJA EDITORIAL-------------------
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/bajaEditorial/{id}")
    public String bajaEditorial(ModelMap modelo, @PathVariable String id) {
        try {
            editorialServicio.darDeBaja(id);
            return "redirect:/inicioeditorial";
        } catch (Excepcion e) {
            return "redirect:/";
        }
    }
//---------------------ALTA EDITORIAL-------------------

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/altaEditorial/{id}")
    public String altaEditorial(ModelMap modelo, @PathVariable String id) {

        try {
            editorialServicio.darDeAlta(id);
            return "redirect:/inicioeditorial";
        } catch (Excepcion e) {
            return "redirect:/";
        }
    }

    //---------------------BAJA LIBRO-------------------
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/bajaCliente/{id}")
    public String bajaCliente(@PathVariable String id, ModelMap modelo) throws Excepcion {
        try {
            clienteServicio.darDeBaja(id);
            return "redirect:/iniciocliente";
        } catch (Excepcion ex) {
            return "redirect:/";
        }
    }

    //---------------------ALTA LIBRO-------------------
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/altaCliente/{id}")
    public String altaCliente(@PathVariable String id, ModelMap modelo) throws Excepcion {
        try {
            clienteServicio.darDeAlta(id);
            return "redirect:/iniciocliente";
        } catch (Excepcion ex) {
            return "redirect:/";
        }
    }

    //--------------------CONTROLADOR FOTO--------------
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/foto")
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
            Logger.getLogger(PortalControlador.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
