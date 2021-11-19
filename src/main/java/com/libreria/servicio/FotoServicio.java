package com.libreria.servicio;

import com.libreria.Excepcion.Excepcion;
import com.libreria.entidades.Foto;
import com.libreria.repositorio.FotoRepositorio;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FotoServicio {

    @Autowired
    private FotoRepositorio fotoRepositorio;

    @Transactional
    public Foto guardar(MultipartFile archivo) throws Excepcion {
        if (archivo != null) {
            try {
                Foto foto = new Foto();
                foto.setMime(archivo.getContentType());
                foto.setNombre(archivo.getName());
                foto.setContenido(archivo.getBytes());

                return fotoRepositorio.save(foto);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return null;
    }
    

    @Transactional
    public Foto actualizar(String idFoto, MultipartFile archivo) throws Excepcion {
        if (archivo != null) {
            try {
                Foto foto = new Foto();

                if (idFoto != null) {
                    Optional<Foto> respuesta = fotoRepositorio.findById(idFoto);
                    if (respuesta.isPresent()) {
                        foto = respuesta.get();
                        foto.setMime(archivo.getContentType());
                        foto.setNombre(archivo.getName());
                        foto.setContenido(archivo.getBytes());
                        return fotoRepositorio.save(foto);
                    }
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return null;
    }
//    public Foto actualizar (String idFoto, MultipartFile archivo) throws Excepcion{
//        if(archivo != null){
//            try{
//                Foto foto = new Foto();
//                if(idFoto != null){
//                    Optional<Foto> respuesta = fotoRepositorio.findById(idFoto);
//                    if(respuesta.isPresent()){
//                        foto = respuesta.get();
//                    }
//                }
//                foto.setMime(archivo.getContentType());
//                foto.setNombre(archivo.getName());
//                foto.setContenido(archivo.getBytes());
//            
//                return fotoRepositorio.save(foto);
//            }catch(Exception e){
//                System.err.println(e.getMessage());
//            }
//        }
//        return null;
//    }
}
