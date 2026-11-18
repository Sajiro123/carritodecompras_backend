package com.shop.carrito.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "agencia_envio")
public class AgenciaEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String empresa = "Shalom";

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 255)
    private String direccion;

    @Column(nullable = false, length = 60)
    private String departamento;

    @Column(nullable = false, length = 60)
    private String provincia;

    @Column(nullable = false, length = 60)
    private String distrito;

    @Column(length = 30)
    private String telefono;

    @Column(nullable = false)
    private Boolean activo = true;

    public AgenciaEnvio() {}

    public Long getId() { return id; }
    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
