package com.chubb.inventoryapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GlobalSecurityFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        HttpMethod method = exchange.getRequest().getMethod();

        if (path.startsWith("/auth/") || 
            path.startsWith("/eureka") ||
            (path.startsWith("/products") && method == HttpMethod.GET) || 
            (path.startsWith("/category") && method == HttpMethod.GET)) { 
            return chain.filter(exchange);
        }

        String token = null;
        if (exchange.getRequest().getCookies().containsKey("jwt_token")) {
            token = exchange.getRequest().getCookies().getFirst("jwt_token").getValue();
        } else {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }

        if (token == null) {
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }

        String userRole;
        String userId;
        try {
            jwtUtil.validateToken(token);
            var claims = jwtUtil.getAllClaimsFromToken(token);
            userRole = claims.get("role", String.class);
            userId = claims.getSubject();
        } catch (Exception e) {
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }

        boolean isAuthorized = checkAuthorization(path, method, userRole);

        if (!isAuthorized) {
            return onError(exchange, HttpStatus.FORBIDDEN);
        }

        exchange.getRequest().mutate()
                .header("loggedInUser", userId)
                .header("loggedInRole", userRole)
                .build();

        return chain.filter(exchange);
    }

    private boolean checkAuthorization(String path, HttpMethod method, String role) {
        
        if ("ADMIN".equalsIgnoreCase(role)) return true;

        if (path.startsWith("/inventory/check") && method == HttpMethod.POST) {
            return hasRole(role, "CUSTOMER", "SALES_EXECUTIVE", "WAREHOUSE_MANAGER");
        }
        
        if (path.startsWith("/inventory/product/") && method == HttpMethod.GET) {
            return hasRole(role, "CUSTOMER", "SALES_EXECUTIVE", "WAREHOUSE_MANAGER");
        }
        
        if (path.startsWith("/inventory/warehouse/") && method == HttpMethod.GET) {
            return hasRole(role, "WAREHOUSE_MANAGER", "SALES_EXECUTIVE");
        }
        
        if (path.startsWith("/inventory") && method == HttpMethod.PATCH) {
            return hasRole(role, "WAREHOUSE_MANAGER");
        }
        
        if (path.startsWith("/inventory/stock") && (method == HttpMethod.PUT || method == HttpMethod.PATCH)) {
            return hasRole(role, "WAREHOUSE_MANAGER");
        }
        
        if (path.startsWith("/inventory/low-stock") && method == HttpMethod.GET) {
            return hasRole(role, "WAREHOUSE_MANAGER");
        }
        
        if (path.equals("/inventory") && method == HttpMethod.POST) {
            return hasRole(role, "WAREHOUSE_MANAGER");
        }
        
        if (path.startsWith("/inventory") && method == HttpMethod.GET) {
            return hasRole(role, "WAREHOUSE_MANAGER", "SALES_EXECUTIVE");
        }
        
        if (path.startsWith("/warehouse") && method == HttpMethod.GET) {
            return hasRole(role, "WAREHOUSE_MANAGER", "SALES_EXECUTIVE");
        }

        if (path.equals("/order") && method == HttpMethod.POST) {
            return hasRole(role, "CUSTOMER", "SALES_EXECUTIVE");
        }

        if (path.equals("/order") && method == HttpMethod.GET) {
            return hasRole(role, "ADMIN", "SALES_EXECUTIVE", "WAREHOUSE_MANAGER");
        }
        
        if (path.matches("/order/.*/cancel") && method == HttpMethod.PUT) {
            return hasRole(role, "CUSTOMER", "SALES_EXECUTIVE");
        }
        
        if (path.matches("/order/.*/status") && method == HttpMethod.PATCH) {
            return hasRole(role, "WAREHOUSE_MANAGER");
        }
        
        if (path.matches("/order/.*/status") && method == HttpMethod.GET) {
            return hasRole(role, "CUSTOMER", "SALES_EXECUTIVE", "WAREHOUSE_MANAGER", "FINANCE_OFFICER");
        }
        
        if (path.matches("/order/.*/items") && method == HttpMethod.GET) {
            return hasRole(role, "CUSTOMER", "SALES_EXECUTIVE", "WAREHOUSE_MANAGER", "FINANCE_OFFICER");
       }

       if (path.startsWith("/order/customer") && method == HttpMethod.GET) {
            return hasRole(role, "CUSTOMER", "SALES_EXECUTIVE", "WAREHOUSE_MANAGER", "FINANCE_OFFICER");
        }
        
        if (path.matches("/order/\\d+") && method == HttpMethod.GET) {
            return hasRole(role, "CUSTOMER", "SALES_EXECUTIVE", "WAREHOUSE_MANAGER", "FINANCE_OFFICER");
        }

        if (path.startsWith("/billing") && method == HttpMethod.POST) {
            return hasRole(role, "CUSTOMER", "SALES_EXECUTIVE");
        }
        
        if (path.startsWith("/billing") && method == HttpMethod.PATCH) {
            return hasRole(role, "CUSTOMER", "SALES_EXECUTIVE");
        }
        
        if (path.equals("/billing") && method == HttpMethod.GET) {
            return hasRole(role, "FINANCE_OFFICER");
        }
        
        if (path.matches("/billing/\\d+") && method == HttpMethod.GET) {
            return hasRole(role, "CUSTOMER", "SALES_EXECUTIVE", "FINANCE_OFFICER");
        }
        
        if (path.startsWith("/billing/order/") && method == HttpMethod.GET) {
            return hasRole(role, "CUSTOMER", "SALES_EXECUTIVE", "FINANCE_OFFICER");
        }

        return false;
    }

    private boolean hasRole(String userRole, String... allowedRoles) {
        for (String allowed : allowedRoles) {
            if (allowed.equalsIgnoreCase(userRole)) return true;
        }
        return false;
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}