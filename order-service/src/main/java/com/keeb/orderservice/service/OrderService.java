package com.keeb.orderservice.service;

import com.keeb.orderservice.configuration.InventoryFeignClient;
import com.keeb.orderservice.configuration.UserFeignClient;
import com.keeb.orderservice.dto.OrderDto;
import com.keeb.orderservice.exception.BadRequestException;
import com.keeb.orderservice.exception.CustomException;
import com.keeb.orderservice.model.Order;
import com.keeb.orderservice.model.ProductInventory;
import com.keeb.orderservice.model.User;
import com.keeb.orderservice.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryFeignClient inventoryFeignClient;
    private final UserFeignClient userFeignClient;
    private final StreamBridge streamBridge;

    public ResponseEntity<Object> fetchAllOrders() {
        try {
            List<Order> orders = orderRepository.findAll();

            log.info("Fetching all orders");

            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.info("Error while fetching all orders " + e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public ResponseEntity<Object> fetchOrderById(String orderId) {
        try {
            Optional<Order> order = orderRepository.findById(orderId);

            log.info("Fetching order by id: " + orderId);

            return ResponseEntity.ok(order.orElse(null));
        } catch (Exception e) {
            log.info("Error while fetching order by id " + e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public ResponseEntity<Object> createOrder(Order order) {
        try {
            List<Long> productIds = order.getProducts().stream()
                    .map(ProductInventory::getProductId)
                    .collect(Collectors.toList());

            ResponseEntity<List<ProductInventory>> response = inventoryFeignClient.fetchInventory(productIds);
            List<ProductInventory> inventoryList = response.getBody();

            order.getProducts().forEach(product -> {
                if (product.getQuantity() < 1)
                    throw new BadRequestException("Invalid quantity for product with id: " + product.getProductId());

                Optional<ProductInventory> inventory = inventoryList.stream()
                        .filter(inv -> inv.getProductId().equals(product.getProductId()))
                        .findAny();

                inventory.ifPresent(inv -> {
                    if (inv.getQuantity() == 0)
                        throw new CustomException("No stock for product with id: " + product.getProductId());

                    if (inv.getQuantity() < product.getQuantity())
                        throw new CustomException("Not enough stock for product with id: " + product.getProductId());

                    inv.setQuantity(inv.getQuantity() - product.getQuantity());
                });
            });

            inventoryFeignClient.updateInventory(inventoryList);

            order.setPaymentStatus("In progress");

            Order savedOrder = orderRepository.save(order);

            ResponseEntity<User> userResponse = userFeignClient.fetchUser(order.getOrderedBy());
            User user = userResponse.getBody();

            if (user != null) {
                List<Order> orders = new ArrayList<>();

                if (user.getOrders() != null)
                    orders = user.getOrders();

                orders.add(savedOrder);

                user.setOrders(orders);
                user.setAddress(savedOrder.getAddress());

                userFeignClient.updateUser(user);
            }

            OrderDto orderNotification = new OrderDto(savedOrder.getId(), savedOrder.getOrderedBy(),
                    savedOrder.getAmount(), savedOrder.getAddress(), savedOrder.getPaymentMethod(), savedOrder.getPaymentStatus());

            log.info("Sending order creation email request for the details: " + orderNotification);
            var result = streamBridge.send("sendOrderCreatedEmail-out-0", orderNotification);
            log.info("Order creation email response " + result);

            log.info("Created order having id: " + savedOrder.getId());

            return ResponseEntity.ok(savedOrder);
        } catch (BadRequestException bre) {
            log.info("Error while creating order " + bre);
            return ResponseEntity.badRequest().body(bre.getErrorMessage());
        } catch (CustomException ce) {
            log.info("Error while creating order " + ce);
            return ResponseEntity.badRequest().body(ce.getErrorMessage());
        } catch (Exception e) {
            log.info("Error while creating order " + e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public ResponseEntity<String> updateOrder(Order order) {
        try {
            orderRepository.save(order);

            log.info("Updated order having id: " + order.getId());

            return ResponseEntity.ok("Order updated");
        } catch (Exception e) {
            log.info("Error while updating order by id " + e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public ResponseEntity<String> deleteOrder(String orderId) {
        try {
            Optional<Order> order = orderRepository.findById(orderId);

            orderRepository.deleteById(orderId);

            order.ifPresent(deletedOrder -> {
                ResponseEntity<User> userResponse = userFeignClient.fetchUser(deletedOrder.getOrderedBy());
                User user = userResponse.getBody();

                List<Order> updatedOrders = user.getOrders().stream()
                        .filter(ord -> !ord.getId().equals(deletedOrder.getId()))
                        .collect(Collectors.toList());

                user.setOrders(updatedOrders);

                userFeignClient.updateUser(user);
            });

            log.info("Deleted order having id: " + orderId);

            return ResponseEntity.ok("Order deleted");
        } catch (Exception e) {
            log.info("Error while creating order " + e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
