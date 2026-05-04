package com.ordering.system.service;

import com.ordering.system.entity.Order;
import com.ordering.system.entity.OrderItem;
import com.ordering.system.repository.ItemRepository;
import com.ordering.system.repository.OrderRepository;
import com.ordering.system.entity.Item;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    public OrderService(OrderRepository orderRepository,
                        ItemRepository itemRepository) {
        this.orderRepository = orderRepository;
        this.itemRepository  = itemRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    @Transactional
    public Order createOrder(Order order) {

        // ===== STEP 1: Check stock for ALL items first =====
        if (order.getOrderItems() != null) {
            for (OrderItem orderItem : order.getOrderItems()) {

                // Find item by name
                Item item = itemRepository
                        .findByNameIgnoreCase(orderItem.getItemName())
                        .orElseThrow(() -> new RuntimeException(
                            "Item not found: " + orderItem.getItemName()));

                // Check if enough stock
                if (item.getQuantity() < orderItem.getQuantity()) {
                    throw new RuntimeException(
                        "Insufficient stock for: " + item.getName() +
                        ". Available: " + item.getQuantity() +
                        ", Requested: " + orderItem.getQuantity());
                }
            }
        }

        // ===== STEP 2: Deduct stock for ALL items =====
        if (order.getOrderItems() != null) {
            for (OrderItem orderItem : order.getOrderItems()) {
                Item item = itemRepository
                        .findByNameIgnoreCase(orderItem.getItemName())
                        .orElseThrow(() -> new RuntimeException(
                            "Item not found: " + orderItem.getItemName()));

                // Deduct stock
                item.setQuantity(item.getQuantity() - orderItem.getQuantity());
                itemRepository.save(item);

                System.out.println("Stock deducted: " + item.getName() +
                    " | Remaining: " + item.getQuantity());
            }
        }

        // ===== STEP 3: Create order =====
        order.setOrderNumber("ORD-" + UUID.randomUUID()
                .toString().substring(0, 8).toUpperCase());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setPaymentStatus("Unpaid");

        // Link order items to order
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                item.setOrder(order);
                item.setSubtotal(item.getItemPrice() * item.getQuantity());
            }
        }

        // Compute totals
        double total = order.getOrderItems() == null ? 0 :
                order.getOrderItems().stream()
                        .mapToDouble(OrderItem::getSubtotal).sum();
        order.setTotalAmount(total);

        double discount = order.getDiscount() != null ? order.getDiscount() : 0;
        order.setDiscount(discount);
        order.setFinalAmount(total - discount);

        return orderRepository.save(order);
    }

    /**
     * Updated to handle payment status synchronization
     */
    public Order updateStatus(Long id, String status) {
        Order order = getOrderById(id);
        order.setStatus(status);

        // If the order is marked as COMPLETED, automatically mark it as Paid
        if ("COMPLETED".equalsIgnoreCase(status)) {
            order.setPaymentStatus("Paid");
        }

        return orderRepository.save(order);
    }

    public Order updatePaymentStatus(Long id, String paymentStatus) {
        Order order = getOrderById(id);
        order.setPaymentStatus(paymentStatus);
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public List<Order> searchByCustomer(String name) {
        return orderRepository.findByCustomerNameContainingIgnoreCase(name);
    }

    public List<Order> filterByStatus(String status) {
        return orderRepository.findByStatus(status);
    }
}
