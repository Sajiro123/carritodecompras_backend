package com.shop.carrito.controller;

import com.shop.carrito.entity.Politica;
import com.shop.carrito.service.PoliticaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/politicas")
public class PoliticaController {

    private final PoliticaService politicaService;

    public PoliticaController(PoliticaService politicaService) {
        this.politicaService = politicaService;
    }

    @GetMapping("/{slug}")
    public Politica getBySlug(@PathVariable String slug) {
        return politicaService.getBySlug(slug);
    }
}
