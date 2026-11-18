package com.shop.carrito.controller;

import com.shop.carrito.dto.NewsletterRequest;
import com.shop.carrito.service.NewsletterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/newsletter")
public class NewsletterController {

    private final NewsletterService newsletterService;

    public NewsletterController(NewsletterService newsletterService) {
        this.newsletterService = newsletterService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> subscribe(@Valid @RequestBody NewsletterRequest request) {
        boolean nuevo = newsletterService.subscribe(request.email());
        if (nuevo) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "¡Gracias por suscribirte! Te avisaremos de nuestras novedades y ofertas exclusivas."
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Tu correo ya se encuentra registrado en nuestro boletín informativo."
            ));
        }
    }
}
