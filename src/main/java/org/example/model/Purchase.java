package org.example.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Purchase {
    @SerializedName("transaction_time")
    private String transactionTime;

    @SerializedName("product_id")
    private String productId;

    @SerializedName("price")
    private BigDecimal price;

    @SerializedName("quantity")
    private Integer quantity;

    @SerializedName("is_member")
    private Boolean isMember;

    @SerializedName("member_discount")
    private BigDecimal memberDiscount;

    @SerializedName("add_supplements")
    private Boolean addSupplements;

    @SerializedName("supplement_price")
    private BigDecimal supplementPrice;

    @SerializedName("total_purchase")
    private BigDecimal totalPurchase;
}