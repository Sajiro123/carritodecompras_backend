package com.shop.carrito.service;

import com.shop.carrito.dto.CalculoEnvioResponse;
import com.shop.carrito.dto.UbigeoDto;
import com.shop.carrito.entity.AgenciaEnvio;
import com.shop.carrito.entity.Tienda;
import com.shop.carrito.entity.Ubigeo;
import com.shop.carrito.repository.AgenciaEnvioRepository;
import com.shop.carrito.repository.TiendaRepository;
import com.shop.carrito.repository.UbigeoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class EnvioService {

    private final UbigeoRepository ubigeoRepository;
    private final TiendaRepository tiendaRepository;
    private final AgenciaEnvioRepository agenciaEnvioRepository;

    public EnvioService(
            UbigeoRepository ubigeoRepository,
            TiendaRepository tiendaRepository,
            AgenciaEnvioRepository agenciaEnvioRepository
    ) {
        this.ubigeoRepository = ubigeoRepository;
        this.tiendaRepository = tiendaRepository;
        this.agenciaEnvioRepository = agenciaEnvioRepository;
    }

    @Transactional(readOnly = true)
    public List<UbigeoDto> searchUbigeos(String q) {
        if (q == null || q.trim().isEmpty()) {
            return ubigeoRepository.findAll().stream()
                    .limit(20)
                    .map(UbigeoDto::from)
                    .toList();
        }
        return ubigeoRepository.searchByQuery(q.trim()).stream()
                .limit(25)
                .map(UbigeoDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CalculoEnvioResponse calcularEnvio(String ubigeoCodigo, BigDecimal subtotal) {
        BigDecimal costoBase = BigDecimal.valueOf(18.00);
        String zona = "PROVINCIAS / NACIONAL";
        String diasEntrega = "48 a 72 horas";

        if (ubigeoCodigo != null && !ubigeoCodigo.trim().isEmpty()) {
            var ubigeoOpt = ubigeoRepository.findById(ubigeoCodigo.trim());
            if (ubigeoOpt.isPresent()) {
                Ubigeo u = ubigeoOpt.get();
                costoBase = u.getCostoEnvio();
                zona = u.getZona();
                if ("LIMA_METROPOLITANA".equalsIgnoreCase(zona) || "CALLAO".equalsIgnoreCase(zona)) {
                    diasEntrega = "24 a 48 horas";
                }
            }
        }

        return CalculoEnvioResponse.calcular(subtotal, costoBase, zona, diasEntrega);
    }

    @Transactional(readOnly = true)
    public List<Tienda> getTiendas() {
        return tiendaRepository.findByActivoTrueOrderByIdAsc();
    }

    @Transactional(readOnly = true)
    public List<AgenciaEnvio> getAgencias(String q) {
        if (q == null || q.trim().isEmpty()) {
            return agenciaEnvioRepository.findByActivoTrueOrderByDepartamentoAscProvinciaAscDistritoAsc();
        }
        return agenciaEnvioRepository.searchByQuery(q.trim());
    }
}
