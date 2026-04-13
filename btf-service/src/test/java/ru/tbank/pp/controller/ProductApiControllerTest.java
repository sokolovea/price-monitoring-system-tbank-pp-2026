package ru.tbank.pp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.tbank.pp.model.*;
import ru.tbank.pp.service.ProductService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductApiControllerTest {

    private MockMvc mockMvc;
    private ProductService productService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        ProductApiController controller = new ProductApiController(productService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void addProduct_Success() throws Exception {
        ProductsProduct productsProduct = new ProductsProduct();
        productsProduct.setId(1L);
        productsProduct.setName("Test Product");
        productsProduct.setUrl("https://example.com/product");

        ProductsUrl productUrl = new ProductsUrl();
        productUrl.setUrl("https://example.com/product");

        when(productService.addProduct(any(ProductsUrl.class))).thenReturn(productsProduct);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productUrl)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void deleteProduct_Success() throws Exception {
        doNothing().when(productService).deleteProduct(anyLong());

        mockMvc.perform(delete("/products/{productId}", 1L))
                .andExpect(status().isOk());

        verify(productService).deleteProduct(1L);
    }

    @Test
    void getProductById_Success() throws Exception {
        ProductsProductDetail productDetail = new ProductsProductDetail();
        productDetail.setId(1L);
        productDetail.setName("Test Product");
        productDetail.setBrand("Test Brand");
        productDetail.setUrl("https://example.com/product");
        productDetail.setCurrentPrice(new BigDecimal("1000"));
        productDetail.setLastChecked(OffsetDateTime.now());

        when(productService.getProductDetail(1L)).thenReturn(productDetail);

        mockMvc.perform(get("/products/{productId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void getProducts_Success() throws Exception {
        ProductsProduct productsProduct = new ProductsProduct();
        productsProduct.setId(1L);
        productsProduct.setName("Test Product");

        when(productService.getAllUserProducts()).thenReturn(List.of(productsProduct));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    @Test
    void subscribeNotification_Success() throws Exception {
        ProductsNotificationUpdate notificationUpdate = new ProductsNotificationUpdate();
        notificationUpdate.setEnabled(true);
        notificationUpdate.setThresholdPrice(new BigDecimal("800"));

        ProductsNotification notification = new ProductsNotification();
        notification.setEnabled(true);
        notification.setThresholdPrice(new BigDecimal("800"));

        when(productService.subscribeNotification(anyLong(), any(ProductsNotificationUpdate.class)))
                .thenReturn(notification);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/products/{productId}/notification", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.thresholdPrice").value(800));
    }

    @Test
    void unsubscribeNotification_Success() throws Exception {
        when(productService.unsubscribeNotification(1L)).thenReturn(false);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/products/{productId}/notification/unsubscribe", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    void getProductPreview_Success() throws Exception {
        ProductsProductPreview preview = new ProductsProductPreview();
        preview.setTitle("Test Product");
        preview.setCurrentPrice(new BigDecimal("1000"));
        preview.setImage("https://example.com/image.jpg");

        ProductsUrl productUrl = new ProductsUrl();
        productUrl.setUrl("https://example.com/product");

        when(productService.getProductPreview(any(ProductsUrl.class))).thenReturn(preview);

        mockMvc.perform(post("/products/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productUrl)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Product"))
                .andExpect(jsonPath("$.currentPrice").value(1000));
    }

    @Test
    void getProductCompare_Success() throws Exception {
        ProductsIdList idList = new ProductsIdList();
        idList.setIds(List.of(1L, 2L));

        ProductsProductDetail productDetail = new ProductsProductDetail();
        productDetail.setId(1L);
        productDetail.setName("Test Product");

        when(productService.getProductDetailList(List.of(1L, 2L))).thenReturn(List.of(productDetail));

        mockMvc.perform(post("/products/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(idList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }
}
