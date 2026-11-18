package com.shop.carrito.service;

import com.shop.carrito.dto.CreateOrderRequest;
import com.shop.carrito.dto.OrderDetailResponse;
import com.shop.carrito.dto.OrderResponse;
import com.shop.carrito.entity.Order;
import com.shop.carrito.entity.OrderItem;
import com.shop.carrito.entity.OrderStatus;
import com.shop.carrito.repository.OrderRepository;
import com.shop.carrito.repository.ProductRepository;
import com.shop.carrito.repository.UbigeoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.SecureRandom;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UbigeoRepository ubigeoRepository;
    private final SecureRandom random = new SecureRandom();

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            UbigeoRepository ubigeoRepository
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.ubigeoRepository = ubigeoRepository;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        var order = new Order();

        // 1. Datos de Contacto
        String name = "Cliente";
        String email = "cliente@example.com";
        String phone = "999999999";

        if (request.contacto() != null) {
            name = request.contacto().nombre() != null ? request.contacto().nombre().trim() : name;
            email = request.contacto().email() != null ? request.contacto().email().trim() : email;
            phone = request.contacto().telefono() != null ? request.contacto().telefono().trim() : phone;
        } else if (request.customer() != null) {
            name = request.customer().name() != null ? request.customer().name().trim() : name;
            email = request.customer().email() != null ? request.customer().email().trim() : email;
            phone = request.customer().phone() != null ? request.customer().phone().trim() : phone;
        }

        // Limpiar teléfono (+51 o espacios)
        phone = phone.replaceAll("[^0-9]", "");
        if (phone.startsWith("51") && phone.length() == 11) {
            phone = phone.substring(2);
        }

        order.setCustomerName(name);
        order.setCustomerEmail(email);
        order.setCustomerPhone(phone);

        // Generar Código de Seguimiento Único (ej. PED-592810)
        String trackingCode;
        do {
            int num = 100000 + random.nextInt(900000);
            trackingCode = "PED-" + num;
        } while (orderRepository.findByCodigoSeguimientoIgnoreCase(trackingCode).isPresent());

        order.setCodigoSeguimiento(trackingCode);

        // 2. Método de Entrega
        String tipoEntrega = "DOMICILIO";
        if (request.entrega() != null && request.entrega().tipo() != null) {
            tipoEntrega = request.entrega().tipo().trim().toUpperCase();
        }
        order.setTipoEntrega(tipoEntrega);

        if ("DOMICILIO".equalsIgnoreCase(tipoEntrega)) {
            if (request.entrega() != null) {
                order.setUbigeoCodigo(request.entrega().ubigeoCodigo());
                order.setDireccionEnvio(request.entrega().direccion());
                order.setReferenciaEnvio(request.entrega().referencia());
            } else if (request.customer() != null) {
                order.setDireccionEnvio(request.customer().address());
            }
        } else if ("SHALOM".equalsIgnoreCase(tipoEntrega)) {
            if (request.entrega() != null) {
                order.setAgenciaId(request.entrega().agenciaId());
                order.setReceptorNombre(request.entrega().receptorNombre());
                order.setReceptorDni(request.entrega().receptorDni());
            }
        } else if ("TIENDA".equalsIgnoreCase(tipoEntrega)) {
            if (request.entrega() != null) {
                order.setTiendaId(request.entrega().tiendaId());
            }
        }

        // 3. Comprobante de Pago
        String tipoComprobante = "BOLETA";
        String tipoDoc = "DNI";
        String numDoc = "00000000";

        if (request.comprobante() != null) {
            if (request.comprobante().tipo() != null) {
                tipoComprobante = request.comprobante().tipo().trim().toUpperCase();
            }
            tipoDoc = request.comprobante().tipoDocumento() != null ? request.comprobante().tipoDocumento().trim() : tipoDoc;
            numDoc = request.comprobante().numeroDocumento() != null ? request.comprobante().numeroDocumento().trim() : numDoc;

            if ("FACTURA".equalsIgnoreCase(tipoComprobante)) {
                tipoDoc = "RUC";
                order.setRazonSocial(request.comprobante().razonSocial());
                order.setDireccionFiscal(request.comprobante().direccionFiscal());
            }
        }

        order.setTipoComprobante(tipoComprobante);
        order.setTipoDocumento(tipoDoc);
        order.setNumeroDocumento(numDoc);

        // 4. Método de Pago
        String metodoPago = "BCP";
        if (request.pago() != null && request.pago().metodo() != null) {
            metodoPago = request.pago().metodo().trim().toUpperCase();
        }
        order.setMetodoPago(metodoPago);
        order.setStatus(OrderStatus.PENDING);

        // 5. Procesar Ítems y Calcular Subtotal desde la Base de Datos
        BigDecimal subtotal = BigDecimal.ZERO;

        for (var reqItem : request.items()) {
            var product = productRepository.findActiveByIdForUpdate(reqItem.productId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Producto " + reqItem.productId() + " no existe"));

            if (product.getStock() < reqItem.quantity()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Stock insuficiente para " + product.getName() + " (Disponible: " + product.getStock() + ")");
            }

            BigDecimal lineSubtotal = product.getPrice().multiply(BigDecimal.valueOf(reqItem.quantity()));
            subtotal = subtotal.add(lineSubtotal);

            // Descontar stock
            product.setStock(product.getStock() - reqItem.quantity());

            var orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(reqItem.quantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setSubtotal(lineSubtotal);
            order.addItem(orderItem);
        }

        order.setSubtotal(subtotal);

        // 6. Calcular Costo de Envío
        BigDecimal costoEnvio = BigDecimal.ZERO;
        BigDecimal umbralEnvioGratis = BigDecimal.valueOf(300.00);

        if ("TIENDA".equalsIgnoreCase(tipoEntrega)) {
            costoEnvio = BigDecimal.ZERO;
        } else if (subtotal.compareTo(umbralEnvioGratis) >= 0) {
            // Envío gratis por superar los S/ 300.00
            costoEnvio = BigDecimal.ZERO;
        } else if ("SHALOM".equalsIgnoreCase(tipoEntrega)) {
            costoEnvio = BigDecimal.valueOf(15.00);
        } else {
            // Domicilio: según ubigeo
            costoEnvio = BigDecimal.valueOf(18.00); // base provincias
            if (order.getUbigeoCodigo() != null && !order.getUbigeoCodigo().trim().isEmpty()) {
                var ubOpt = ubigeoRepository.findById(order.getUbigeoCodigo().trim());
                if (ubOpt.isPresent()) {
                    costoEnvio = ubOpt.get().getCostoEnvio();
                }
            }
        }

        order.setCostoEnvio(costoEnvio);
        order.setTotal(subtotal.add(costoEnvio));

        var saved = orderRepository.save(order);

        return new OrderResponse(
                saved.getId(),
                saved.getCodigoSeguimiento(),
                saved.getSubtotal(),
                saved.getCostoEnvio(),
                saved.getTotal(),
                saved.getStatus().name()
        );
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getById(Long id) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado con ID: " + id));
        return OrderDetailResponse.from(order);
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getSeguimiento(String codigo, String telefono) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El código de seguimiento es obligatorio");
        }
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El número de teléfono es obligatorio");
        }

        String cleanCode = codigo.trim();
        String cleanPhone = telefono.replaceAll("[^0-9]", "");
        if (cleanPhone.startsWith("51") && cleanPhone.length() == 11) {
            cleanPhone = cleanPhone.substring(2);
        }

        var orderOpt = orderRepository.findByCodigoSeguimientoIgnoreCaseAndCustomerPhone(cleanCode, cleanPhone);

        if (orderOpt.isEmpty()) {
            // Intentar también buscar solo por código para verificar si existe el código pero el teléfono difiere
            var byCode = orderRepository.findByCodigoSeguimientoIgnoreCase(cleanCode);
            if (byCode.isPresent()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El teléfono ingresado no coincide con los datos del pedido " + cleanCode);
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró ningún pedido con el código " + cleanCode);
        }

        return OrderDetailResponse.from(orderOpt.get());
    }
}
