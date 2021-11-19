
package com.libreria.repositorio;

import com.libreria.entidades.Cliente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, String> {
    
    @Query("SELECT a FROM Cliente a WHERE a.id = :id")
    public Cliente buscarPorId(@Param("id") String id);
//
    @Query("SELECT a FROM Cliente a WHERE a.alta = true")
    public List<Cliente> buscarActivos();

    @Query("SELECT a FROM Cliente a WHERE a.alta = false")
    public List<Cliente> buscarBajas();

//    @Query("SELECT c FROM Cliente a WHERE a.alta = true ORDER BY a.nombre")
//    public List<Cliente> searchAssets();
}

