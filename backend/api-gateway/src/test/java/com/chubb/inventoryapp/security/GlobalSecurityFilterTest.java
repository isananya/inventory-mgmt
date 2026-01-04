package com.chubb.inventoryapp.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class GlobalSecurityFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private GatewayFilterChain chain;

    @Mock
    private Claims claims;

    @InjectMocks
    private GlobalSecurityFilter globalSecurityFilter;

    @BeforeEach
    void setUp() {
        lenient().when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    @Test
    void filter_PublicAuthPath_ShouldPermit() {
        MockServerWebExchange exchange = createExchange("/auth/login", HttpMethod.POST);
        
        globalSecurityFilter.filter(exchange, chain).block();

        verify(chain, times(1)).filter(exchange);
        verify(jwtUtil, never()).validateToken(anyString());
    }

    @Test
    void filter_PublicEurekaPath_ShouldPermit() {
        MockServerWebExchange exchange = createExchange("/eureka", HttpMethod.GET);
        
        globalSecurityFilter.filter(exchange, chain).block();

        verify(chain, times(1)).filter(exchange);
        verify(jwtUtil, never()).validateToken(anyString());
    }

    @Test
    void filter_PublicProductGetPath_ShouldPermit() {
        MockServerWebExchange exchange = createExchange("/products", HttpMethod.GET);

        globalSecurityFilter.filter(exchange, chain).block();

        verify(chain, times(1)).filter(exchange);
        verify(jwtUtil, never()).validateToken(anyString());
    }

    @Test
    void filter_PublicCategoryGetPath_ShouldPermit() {
        MockServerWebExchange exchange = createExchange("/category", HttpMethod.GET);

        globalSecurityFilter.filter(exchange, chain).block();

        verify(chain, times(1)).filter(exchange);
        verify(jwtUtil, never()).validateToken(anyString());
    }
    
    @Test
    void filter_ProductPostPath_ShouldCheckAuth() {
        MockServerWebExchange exchange = createExchange("/products", HttpMethod.POST);

        globalSecurityFilter.filter(exchange, chain).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(chain, never()).filter(exchange);
    }

    @Test
    void filter_NoToken_ShouldReturnUnauthorized() {
        MockServerWebExchange exchange = createExchange("/order", HttpMethod.POST);

        globalSecurityFilter.filter(exchange, chain).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void filter_InvalidToken_ShouldReturnUnauthorized() {
        MockServerWebExchange exchange = createExchange("/order", HttpMethod.POST);
        addAuthHeader(exchange, "invalid-token");

        doThrow(new RuntimeException("Invalid token")).when(jwtUtil).validateToken("invalid-token");

        globalSecurityFilter.filter(exchange, chain).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void filter_ValidTokenFromHeader_Success() {
        MockServerWebExchange exchange = createExchange("/order", HttpMethod.POST);
        addAuthHeader(exchange, "valid-token");

        setupMockClaims("valid-token", "CUSTOMER", "123");

        globalSecurityFilter.filter(exchange, chain).block();

        verify(chain).filter(exchange);
        assertEquals("123", exchange.getRequest().getHeaders().getFirst("loggedInUser"));
        assertEquals("CUSTOMER", exchange.getRequest().getHeaders().getFirst("loggedInRole"));
    }

    @Test
    void filter_ValidTokenFromCookie_Success() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/order")
                .cookie(new HttpCookie("jwt_token", "cookie-token"))
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        setupMockClaims("cookie-token", "CUSTOMER", "123");

        globalSecurityFilter.filter(exchange, chain).block();

        verify(chain).filter(exchange);
        assertEquals("CUSTOMER", exchange.getRequest().getHeaders().getFirst("loggedInRole"));
    }

    @Test
    void filter_AdminRole_ShouldAccessAnything() {
        MockServerWebExchange exchange = createExchange("/any/protected/path", HttpMethod.DELETE);
        addAuthHeader(exchange, "admin-token");
        setupMockClaims("admin-token", "ADMIN", "1");

        globalSecurityFilter.filter(exchange, chain).block();

        verify(chain).filter(exchange);
        assertNull(exchange.getResponse().getStatusCode()); 
    }

    @Test
    void filter_InventoryCheck_Allowed() {
        MockServerWebExchange exchange = createExchange("/inventory/check", HttpMethod.POST);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "SALES_EXECUTIVE", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void filter_InventoryProductGet_Allowed() {
        MockServerWebExchange exchange = createExchange("/inventory/product/1", HttpMethod.GET);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "SALES_EXECUTIVE", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void filter_InventoryWarehouseGet_Allowed() {
        MockServerWebExchange exchange = createExchange("/inventory/warehouse/1", HttpMethod.GET);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "SALES_EXECUTIVE", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void filter_InventoryStockUpdate_Allowed() {
        MockServerWebExchange exchange = createExchange("/inventory/stock/add", HttpMethod.PUT);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "WAREHOUSE_MANAGER", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void filter_InventoryStockUpdate_Forbidden() {
        MockServerWebExchange exchange = createExchange("/inventory/stock/add", HttpMethod.PUT);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "SALES_EXECUTIVE", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode());
    }

    @Test
    void filter_InventoryPatch_Allowed() {
        MockServerWebExchange exchange = createExchange("/inventory/123", HttpMethod.PATCH);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "WAREHOUSE_MANAGER", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void filter_InventoryLowStock_Allowed() {
        MockServerWebExchange exchange = createExchange("/inventory/low-stock", HttpMethod.GET);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "WAREHOUSE_MANAGER", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void filter_InventoryPost_Allowed() {
        MockServerWebExchange exchange = createExchange("/inventory", HttpMethod.POST);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "WAREHOUSE_MANAGER", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void filter_InventoryGetAll_Allowed() {
        MockServerWebExchange exchange = createExchange("/inventory", HttpMethod.GET);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "SALES_EXECUTIVE", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void filter_WarehouseGet_Allowed() {
        MockServerWebExchange exchange = createExchange("/warehouse/1", HttpMethod.GET);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "SALES_EXECUTIVE", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void filter_OrderPost_Allowed() {
        MockServerWebExchange exchange = createExchange("/order", HttpMethod.POST);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "CUSTOMER", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void filter_OrderCancel_Allowed() {
        MockServerWebExchange exchange = createExchange("/order/123/cancel", HttpMethod.PUT);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "CUSTOMER", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void filter_OrderStatusUpdate_Allowed() {
        MockServerWebExchange exchange = createExchange("/order/123/status", HttpMethod.PATCH);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "WAREHOUSE_MANAGER", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void filter_OrderStatusGet_Allowed() {
        MockServerWebExchange exchange = createExchange("/order/123/status", HttpMethod.GET);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "FINANCE_OFFICER", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void filter_OrderItemsGet_Allowed() {
        MockServerWebExchange exchange = createExchange("/order/123/items", HttpMethod.GET);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "FINANCE_OFFICER", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void filter_OrderByIdGet_Allowed() {
        MockServerWebExchange exchange = createExchange("/order/123", HttpMethod.GET);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "CUSTOMER", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void filter_BillingPost_Allowed() {
        MockServerWebExchange exchange = createExchange("/billing/order/1", HttpMethod.POST);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "SALES_EXECUTIVE", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void filter_BillingPatch_Allowed() {
        MockServerWebExchange exchange = createExchange("/billing/order/1/status", HttpMethod.PATCH);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "SALES_EXECUTIVE", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void filter_BillingGetAll_Allowed() {
        MockServerWebExchange exchange = createExchange("/billing", HttpMethod.GET);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "FINANCE_OFFICER", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }
    
    @Test
    void filter_BillingGetAll_Forbidden() {
        MockServerWebExchange exchange = createExchange("/billing", HttpMethod.GET);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "CUSTOMER", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode());
    }

    @Test
    void filter_BillingGetById_Allowed() {
        MockServerWebExchange exchange = createExchange("/billing/123", HttpMethod.GET);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "CUSTOMER", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void filter_BillingGetByOrderId_Allowed() {
        MockServerWebExchange exchange = createExchange("/billing/order/123", HttpMethod.GET);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "SALES_EXECUTIVE", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    void filter_UnknownPath_Forbidden() {
        MockServerWebExchange exchange = createExchange("/unknown/path", HttpMethod.GET);
        addAuthHeader(exchange, "token");
        setupMockClaims("token", "CUSTOMER", "1");

        globalSecurityFilter.filter(exchange, chain).block();
        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode());
    }
    
    @Test
    void getOrder_ReturnsMinusOne() {
        assertEquals(-1, globalSecurityFilter.getOrder());
    }

    private MockServerWebExchange createExchange(String path, HttpMethod method) {
        MockServerHttpRequest request = MockServerHttpRequest.method(method, path).build();
        return MockServerWebExchange.from(request);
    }

    private void addAuthHeader(MockServerWebExchange exchange, String token) {
        exchange.getRequest().mutate()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
    }

    private void setupMockClaims(String token, String role, String userId) {
        when(jwtUtil.getAllClaimsFromToken(token)).thenReturn(claims);
        when(claims.get("role", String.class)).thenReturn(role);
        when(claims.getSubject()).thenReturn(userId);
    }
}