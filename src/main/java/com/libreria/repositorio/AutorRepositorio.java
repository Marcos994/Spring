package com.libreria.repositorio;

import com.libreria.entidades.Autor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AutorRepositorio extends JpaRepository<Autor, String> {

    @Query("SELECT a FROM Autor a WHERE a.id = :id")
    public Autor buscarPorId(@Param("id") String id);
    
    @Query("SELECT a FROM Autor a WHERE a.nombre LIKE %:nombre%")
    public Autor buscarPorNombre(@Param("nombre")String nombre);
    
    ///VER
    @Query("SELECT a FROM Autor a WHERE a.nombre LIKE %:nombre%")
    public List<Autor> listarAutoresPorNombre(@Param("nombre") String nombre);
    
    // Alta y baja
    @Query("SELECT a FROM Autor a WHERE a.alta = true ORDER BY a.nombre DESC")
    public List<Autor> ListarAutoresAlta();

    @Query("SELECT a FROM Autor a WHERE a.alta = false ORDER BY a.nombre DESC")
    public List<Autor> ListarAutoresBaja();
    
    // Todos
    @Query("SELECT a FROM Autor a ORDER BY a.nombre DESC")
    public List<Autor> listarAutores();
    
    @Query("SELECT a FROM Autor a WHERE a.nombre LIKE :nombre ORDER BY a.nombre DESC")
    public List<Autor> listarNombresAutores(@Param("nombre") String nombre);
    
    @Query("SELECT a from Autor a WHERE a.alta = true ")
	public List<Autor> buscarActivos();
        @Query("SELECT a from Autor a WHERE a.alta = false ")
	public List<Autor> buscarBajas();
    
}
