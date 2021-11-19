package com.libreria.repositorio;

import com.libreria.entidades.Editorial;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EditorialRepositorio extends JpaRepository<Editorial, String> {

    @Query("SELECT e FROM Editorial e WHERE e.id = :id")
    public Editorial buscarPorId(@Param("id") String id);

    @Query("SELECT e FROM Editorial e WHERE e.nombre = :nombre")
    public Editorial buscarPorNombre(@Param("nombre") String nombre);

    //---------VER
    // Todos
    @Query("SELECT a FROM Editorial a ORDER BY a.nombre DESC")
    public List<Editorial> listarEditoriales();

    //----------VER
    @Query("SELECT e FROM Editorial e WHERE e.nombre LIKE %:nombre%")
    public List<Editorial> listarAutoresPorNombre(@Param("nombre") String nombre);

    @Query("SELECT a FROM Editorial a WHERE a.alta = true ORDER BY a.nombre DESC")
    public List<Editorial> ListarEditorialesAlta();

    @Query("SELECT a FROM Editorial a WHERE a.alta = false ORDER BY a.nombre DESC")
    public List<Editorial> ListarEditorialesBaja();

    @Query("SELECT a FROM Editorial a WHERE a.nombre LIKE :nombre ORDER BY a.nombre DESC")
    public List<Editorial> listarNombresEditorial(@Param("nombre") String nombre);

}
