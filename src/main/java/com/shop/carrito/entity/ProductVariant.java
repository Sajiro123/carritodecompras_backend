package com.shop.carrito.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "producto_variante")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    @JsonIgnore
    private Product product;

    @Column(nullable = false, unique = true, length = 60)
    private String sku;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock = 0;

    @ManyToMany
    @JoinTable(
        name = "variante_atributo_valor",
        joinColumns = @JoinColumn(name = "variante_id"),
        inverseJoinColumns = @JoinColumn(name = "atributo_valor_id")
    )
    private List<AttributeValue> attributeValues = new ArrayList<>();

    public ProductVariant() {}

    public ProductVariant(Product product, String sku, BigDecimal price, Integer stock) {
        this.product = product;
        this.sku = sku;
        this.price = price;
        this.stock = stock;
    }

    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public List<AttributeValue> getAttributeValues() { return attributeValues; }
    public void setAttributeValues(List<AttributeValue> attributeValues) { this.attributeValues = attributeValues; }
}
