
package com.libreria.servicio;

import com.libreria.Excepcion.Excepcion;
import com.libreria.entidades.Foto;
import com.libreria.entidades.Usuario;
import com.libreria.repositorio.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private FotoServicio fotoServicio;
//    @Autowired
//    private NotificacionServicio notificacionServicio;

    @Transactional
    public void registrar(MultipartFile archivo, String nombre, String apellido, String mail, String clave) throws Excepcion {

        validar(nombre, apellido, mail, clave);

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setMail(mail);

        String encriptada = new BCryptPasswordEncoder().encode(clave);
        usuario.setClave(encriptada);

        Foto foto = fotoServicio.guardar(archivo);
        usuario.setFoto(foto);
        usuarioRepositorio.save(usuario);

//        notificacionServicio.enviar("Bienvenidos a la Libreria", "Libreria Egg", usuario.getMail());

    }

    @Transactional
    public void modificar(MultipartFile archivo, String idUsuario, Boolean alta, String nombre, String apellido, String mail, String clave) throws Excepcion {

        validar(nombre, apellido, mail, clave);
        Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setApellido(apellido);
            usuario.setNombre(nombre);
            usuario.setMail(mail);
            usuario.setAlta(true);

            String encriptada = new BCryptPasswordEncoder().encode(clave);
            usuario.setClave(encriptada);

            String idFoto = null;
            if (usuario.getFoto() != null) {
                idFoto = usuario.getFoto().getId();
            }
//            Foto foto = fotoServicio.actualizar(idFoto, archivo);
//            usuario.setFoto(foto);

            usuarioRepositorio.save(usuario);
        } else {
            throw new Excepcion("no se encontro el usuario solicitado");

        }
    }

    private void validar(String nombre, String apellido, String mail, String clave) throws Excepcion {

        if (nombre == null || nombre.isEmpty()) {
            throw new Excepcion("El nombre del usuario no puede ser nulo");
        }

        if (apellido == null || apellido.isEmpty()) {
            throw new Excepcion("El apellido del usuario no puede ser nulo");
        }
        if (mail == null || mail.isEmpty()) {
            throw new Excepcion("El mail del usuario no puede ser nulo");
        }
        if (clave == null || clave.isEmpty() || clave.length() <= 3) {
            throw new Excepcion("La clave del usuario no puede ser nulo y tiene que tener mas de 4 digitos");
        }
    }

    @Transactional
    public void deshabilitar(String idUsuario) throws Excepcion {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setAlta(false);
            usuarioRepositorio.save(usuario);
        } else {
            throw new Excepcion("No se encontro el usuario");
        }
    }

    @Transactional
    public void habilitar(String idUsuario) throws Excepcion {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setAlta(true);
            usuarioRepositorio.save(usuario);
        } else {
            throw new Excepcion("No se encontro el usuario");

        }
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.buscarPorMail(mail);
        if (usuario != null) {
            List<GrantedAuthority> permisos = new ArrayList<>();
            
            GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_USUARIO_REGISTRADO");
            permisos.add(p1);

            GrantedAuthority p2 = new SimpleGrantedAuthority("MODULO_LIBROS");
            permisos.add(p2);

            GrantedAuthority p3 = new SimpleGrantedAuthority("MODULO_PRESTAMO");
            permisos.add(p3);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);

            User user = new User(usuario.getMail(), usuario.getClave(), permisos);
            return user;
        } else {
            return null;
        }
    }
}
