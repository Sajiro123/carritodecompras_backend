package com.shop.carrito.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "ubigeo")
public class Ubigeo {

    @Id
    @Column(length = 6)
    private String codigo;

    @Column(nullable = false, length = 60)
    private String departamento;

    @Column(nullable = false, length = 60)
    private String provincia;

    @Column(nullable = false, length = 60)
    private String distrito;

    @Column(nullable = false, length = 30)
    private String zona;

    @Column(name = "costo_envio", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoEnvio;

    public Ubigeo() {}

    public Ubigeo(String codigo, String departamento, String provincia, String distrito, String zona, BigDecimal costoEnvio) {
        this.codigo = codigo;
        this.departamento = departamento;
        this.provincia = provincia;
        this.distrito = distrito;
        this.zona = zona;
        this.costoEnvio = costoEnvio;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    public BigDecimal getCostoEnvio() { return costoEnvio; }
    public void setCostoEnvio(BigDecimal costoEnvio) { this.costoEnvio = costoEnvio; }

    public String getLabel() {
        return distrito + ", " + provincia + " (" + departamento + ")";
    }
}
