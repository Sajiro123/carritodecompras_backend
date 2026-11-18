package com.shop.carrito.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tienda")
public class Tienda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 255)
    private String direccion;

    @Column(nullable = false, length = 80)
    private String distrito;

    @Column(nullable = false, length = 60)
    private String departamento = "Lima";

    @Column(length = 30)
    private String telefono;

    @Column(nullable = false, length = 120)
    private String horario;

    @Column(nullable = false)
    private Boolean activo = true;

    public Tienda() {}

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
