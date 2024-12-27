package com.lopes.order.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.lopes.order.adapters.rest.dto.OrderRequestDTO;
import com.lopes.order.config.MongoTestConfig;
import com.lopes.order.config.WireMockConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({WireMockConfig.class, MongoTestConfig.class})
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        wireMockServer.resetAll();
        configureProductServiceStub();
    }

    private void configureProductServiceStub() {
        wireMockServer.stubFor(WireMock.get(urlEqualTo("/products?ids=prod1,prod2"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("[{\"id\":\"prod1\",\"price\":50.0},{\"id\":\"prod2\",\"price\":75.0}]")));

        // Stub individual product requests
        wireMockServer.stubFor(WireMock.get(urlEqualTo("/products/prod1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":\"prod1\",\"price\":50.0}")));

        wireMockServer.stubFor(WireMock.get(urlEqualTo("/products/prod2"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":\"prod2\",\"price\":75.0}")));
    }

    @Test
    @DisplayName("Deve criar e recuperar pedido com sucesso")
    void shouldCreateAndRetrieveOrder() throws Exception {
        // Arrange
        var request = OrderRequestDTO.builder()
            .customerId("customer123")
            .productIds(List.of("prod1", "prod2"))
            .build();

        // Create Order
        ResultActions createResult = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.customerId").value("customer123"))
            .andExpect(jsonPath("$.totalValue").value(125.0));

        // Extract order ID from response
        String responseJson = createResult.andReturn().getResponse().getContentAsString();
        String orderId = objectMapper.readTree(responseJson).get("id").asText();

        // Get Order
        mockMvc.perform(get("/orders/" + orderId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(orderId))
            .andExpect(jsonPath("$.customerId").value("customer123"))
            .andExpect(jsonPath("$.totalValue").value(125.0))
            .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    @DisplayName("Deve retornar erro 400 ao criar pedido com dados inválidos")
    void shouldReturnBadRequestWhenCreatingOrderWithInvalidData() throws Exception {
        // Arrange
        var request = OrderRequestDTO.builder()
            .customerId("")  // Invalid: empty customer ID
            .productIds(List.of())  // Invalid: empty product list
            .build();

        // Act & Assert
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors[?(@.field == 'customerId')]").exists())
            .andExpect(jsonPath("$.errors[?(@.field == 'productIds')]").exists());
    }

    @Test
    @DisplayName("Deve retornar erro 404 ao buscar pedido inexistente")
    void shouldReturn404WhenGettingNonExistentOrder() throws Exception {
        mockMvc.perform(get("/orders/non-existent-id"))
            .andExpect(status().isNotFound());
    }
}
