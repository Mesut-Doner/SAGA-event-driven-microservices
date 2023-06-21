package com.mdonerprojects.orderservices.model;


import com.mdonerprojects.orderservices.util.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.SerializationUtils;
import org.axonframework.serialization.SerialVersionUIDRevisionResolver;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_table")
public class OrderEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 17L;

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    public String orderId;
    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
}
