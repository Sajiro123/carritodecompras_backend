package com.shop.carrito.service;

import com.shop.carrito.entity.Politica;
import com.shop.carrito.repository.PoliticaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PoliticaService {

    private final PoliticaRepository politicaRepository;

    public PoliticaService(PoliticaRepository politicaRepository) {
        this.politicaRepository = politicaRepository;
    }

    @Transactional(readOnly = true)
    public Politica getBySlug(String slug) {
        return politicaRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Política no encontrada: " + slug));
    }
}
