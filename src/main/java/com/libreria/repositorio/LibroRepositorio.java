
package com.libreria.repositorio;

import com.libreria.entidades.Libro;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LibroRepositorio extends JpaRepository<Libro, String> {

    @Query("SELECT l FROM Libro l WHERE l.autor.nombre LIKE %:titulo%")
    public List<Libro> buscarLibrosPorAutorNombre(@Param("titulo") String titulo);

    @Query("SELECT l FROM Libro l WHERE l.editorial.nombre LIKE %:titulo%")
    public List<Libro> buscarLibrosPorEditorialNombre(@Param("titulo") String titulo);
    
    @Query("SELECT l FROM Libro l WHERE l.titulo LIKE %:titulo%")
    public List<Libro> buscarLibrosPorTitulo(@Param("titulo") String titulo);
    
    @Query("SELECT l FROM Libro l WHERE l.id = :id")
    public Libro buscarLibrosPorId(@Param("id") String id);
    
    @Query("SELECT l FROM Libro l WHERE l.titulo LIKE %:titulo%")
    public List<Libro> listarLibrosPorTitulo(@Param("titulo") String titulo);
    
    @Query("SELECT l FROM Libro l")
    public List<Libro> listarLibros();
    
    @Query("SELECT a from Libro a WHERE a.autor.nombre LIKE %:titulo% OR a.editorial.nombre LIKE %:titulo% OR a.titulo LIKE %:titulo%")
    public List<Libro> searchAssetsByParam(@Param("titulo") String titulo);
    
    
    @Query("SELECT l FROM Libro l WHERE l.cliente.nombre LIKE %:nombre")
    public List<Libro> buscarLibroPorCliente(@Param("nombre") String nombre);
    
    
        
        
    }