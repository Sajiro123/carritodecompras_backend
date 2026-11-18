package com.shop.carrito.controller;

import com.shop.carrito.dto.CalculoEnvioResponse;
import com.shop.carrito.dto.UbigeoDto;
import com.shop.carrito.entity.AgenciaEnvio;
import com.shop.carrito.entity.Tienda;
import com.shop.carrito.service.EnvioService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EnvioController {

    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @GetMapping("/ubigeos")
    public List<UbigeoDto> searchUbigeos(@RequestParam(required = false) String q) {
        return envioService.searchUbigeos(q);
    }

    @GetMapping("/tiendas")
    public List<Tienda> getTiendas() {
        return envioService.getTiendas();
    }

    @GetMapping("/agencias")
    public List<AgenciaEnvio> getAgencias(@RequestParam(required = false) String q) {
        return envioService.getAgencias(q);
    }

    @GetMapping("/envio/calcular")
    public CalculoEnvioResponse calcularEnvio(
            @RequestParam(required = false) String ubigeo,
            @RequestParam(required = false, defaultValue = "0") BigDecimal subtotal
    ) {
        return envioService.calcularEnvio(ubigeo, subtotal);
    }
}
