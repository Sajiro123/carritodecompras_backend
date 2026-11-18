package com.shop.carrito.service;

import com.shop.carrito.dto.CreateOrderRequest;
import com.shop.carrito.dto.OrderDetailResponse;
import com.shop.carrito.dto.OrderResponse;
import com.shop.carrito.entity.Order;
import com.shop.carrito.entity.OrderItem;
import com.shop.carrito.entity.OrderStatus;
import com.shop.carrito.repository.OrderRepository;
import com.shop.carrito.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        var order = new Order();
        order.setCustomerName(request.customer().name().trim());
        order.setCustomerEmail(request.customer().email().trim());
        order.setCustomerPhone(request.customer().phone() != null ? request.customer().phone().trim() : null);
        order.setAddress(request.customer().address().trim());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;

        for (var reqItem : request.items()) {
            var product = productRepository.findActiveByIdForUpdate(reqItem.productId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Producto " + reqItem.productId() + " no existe"));

            if (product.getStock() < reqItem.quantity()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Stock insuficiente para " + product.getName());
            }

            // Regla de oro: el precio siempre sale de la base de datos, nunca del frontend
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(reqItem.quantity()));
            total = total.add(lineTotal);

            product.setStock(product.getStock() - reqItem.quantity());

            var orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(reqItem.quantity());
            orderItem.setUnitPrice(product.getPrice());
            order.addItem(orderItem);
        }

        order.setTotal(total);
        var saved = orderRepository.save(order);

        return new OrderResponse(saved.getId(), saved.getTotal(), saved.getStatus().name());
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getById(Long id) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
        return OrderDetailResponse.from(order);
    }
}
