package com.shop.carrito.controller;

import com.shop.carrito.dto.CreateOrderRequest;
import com.shop.carrito.dto.OrderDetailResponse;
import com.shop.carrito.dto.OrderResponse;
import com.shop.carrito.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/seguimiento")
    public OrderDetailResponse getSeguimiento(
            @RequestParam String codigo,
            @RequestParam String telefono
    ) {
        return orderService.getSeguimiento(codigo, telefono);
    }

    @GetMapping("/{id}")
    public OrderDetailResponse getById(@PathVariable Long id) {
        return orderService.getById(id);
    }
}
