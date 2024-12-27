package com.lopes.order.adapters.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    @JsonProperty("customerId")
    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @JsonProperty("productIds")
    @NotEmpty(message = "Order must have at least one product")
    private List<String> productIds;
}
