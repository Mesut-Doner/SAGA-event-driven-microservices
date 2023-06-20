package com.mdonerprojects.productservices.core.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@Table(name="product_lookup")
@AllArgsConstructor
@NoArgsConstructor
public class ProductLookupEntity implements Serializable {

    private static final long serialVersionUID = 15L;

    @Id
   // @GeneratedValue(strategy = GenerationType.UUID)
    private String productId;

    @Column
    private String title;

}
