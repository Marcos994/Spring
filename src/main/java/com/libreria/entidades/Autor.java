package com.libreria.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Autor {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column
    private String nombre;
    private Boolean alta;

    @OneToOne
    private Foto foto;
    
    public Autor() {
    }

    public Autor(String id, String nombre, Boolean alta, Foto foto) {
        this.id = id;
        this.nombre = nombre;
        this.alta = alta;
        this.foto = foto;
    }    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getAlta() {
        return alta;
    }

    public void setAlta(Boolean alta) {
        this.alta = alta;
    }

    @Override
    public String toString() {
        return "Autor{" + "id=" + getId() + ", nombre=" + getNombre() + ", alta=" + getAlta() + '}';
    }

    public Foto getFoto() {
        return foto;
    }

    public void setFoto(Foto foto) {
        this.foto = foto;
    }

}
