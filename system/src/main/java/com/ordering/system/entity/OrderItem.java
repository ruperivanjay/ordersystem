package com.ordering.system.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @JsonProperty("itemName")
    @Column(nullable = false)
    private String itemName;

    @JsonProperty("itemPrice")
    @Column(nullable = false)
    private Double itemPrice;

    @JsonProperty("quantity")
    @Column(nullable = false)
    private Integer quantity;

    @JsonProperty("item_id")
    @Column(nullable = false)
    private Long itemId;

    @JsonProperty("subtotal")
    @Column(nullable = false)
    private Double subtotal;

    // Getters and Setters
    public Long getId() { return id; }
    public Order getOrder() { return order; }
    public String getItemName() { return itemName; }
    public Double getItemPrice() { return itemPrice; }
    public Integer getQuantity() { return quantity; }
    public Long getItemId() { return itemId; }
    public Double getSubtotal() { return subtotal; }

    public void setId(Long id) { this.id = id; }
    public void setOrder(Order order) { this.order = order; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setItemPrice(Double itemPrice) { this.itemPrice = itemPrice; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    // Constructor for JSON deserialization
    public OrderItem() {}

    @JsonCreator
    public OrderItem(@JsonProperty("item_id") Long itemId,
                     @JsonProperty("itemName") String itemName,
                     @JsonProperty("itemPrice") Double itemPrice,
                     @JsonProperty("quantity") Integer quantity,
                     @JsonProperty("subtotal") Double subtotal) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }
}