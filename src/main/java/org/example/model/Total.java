package org.example.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Total implements Serializable {
    @SerializedName("event_time")
    String eventTime;

    @SerializedName("product_id")
    String productId;

    @SerializedName("quantity")
    Integer quantity;

    @SerializedName("total_purchases")
    BigDecimal totalPurchases;
}