package ca.gbc.apigateway.routes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@Configuration
@Slf4j
public class Routes {

    @Value("${services.product-url}")
    private String productServiceUrl;

    @Value("${services.order-url}")
    private String orderServiceUrl;

    @Value("${services.inventory-url}")
    private String inventoryServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> productServiceRoute() {
        log.info("Initializing product service route with URL: {}", productServiceUrl);

        return GatewayRouterFunctions.route("product_service")
            .route(RequestPredicates.path("/api/product"), request -> {
                log.info("Request received for product-service: {}", request.uri());
                return HandlerFunctions.http(productServiceUrl).handle(request);
            })
            .filter(CircuitBreakerFilterFunctions
                .circuitBreaker("productServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        log.info("Initializing order service route with URL: {}", orderServiceUrl);

        return GatewayRouterFunctions.route("order_service")
            .route(RequestPredicates.path("/api/order"), request -> {
                log.info("Request received for order-service: {}", request.uri());
                return HandlerFunctions.http(productServiceUrl).handle(request);
            })
            .filter(CircuitBreakerFilterFunctions
                .circuitBreaker("orderServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .build();    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoute() {
        log.info("Initializing inventory service route with URL: {}", inventoryServiceUrl);

        return GatewayRouterFunctions.route("inventory_service")
            .route(RequestPredicates.path("/api/inventory"), request -> {
                log.info("Request received for inventory-service: {}", request.uri());
                return HandlerFunctions.http(productServiceUrl).handle(request);
            })
            .filter(CircuitBreakerFilterFunctions
                .circuitBreaker("inventoryServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productServiceSwaggerRoute() {

        return GatewayRouterFunctions.route("product_service_swagger")
            .route(RequestPredicates.path("/aggregate/product-service/v3/api-docs"),
                HandlerFunctions.http(productServiceUrl))
            .filter(setPath("/api-docs"))
            .filter(CircuitBreakerFilterFunctions
                .circuitBreaker("productSwaggerCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceSwaggerRoute() {

        return GatewayRouterFunctions.route("order_service_swagger")
            .route(RequestPredicates.path("/aggregate/order-service/v3/api-docs"),
                HandlerFunctions.http(orderServiceUrl))
            .filter(setPath("/api-docs"))
            .filter(CircuitBreakerFilterFunctions
                .circuitBreaker("orderSwaggerCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceSwaggerRoute() {

        return GatewayRouterFunctions.route("inventory_service_swagger")
            .route(RequestPredicates.path("/aggregate/inventory-service/v3/api-docs"),
                HandlerFunctions.http(inventoryServiceUrl))
            .filter(setPath("/api-docs"))
            .filter(CircuitBreakerFilterFunctions
                .circuitBreaker("inventorySwaggerCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {

        return GatewayRouterFunctions.route("fallbackRoute")
            .route(RequestPredicates.all(),
                request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Service is Temporarily Unavailable, please try again later"))
            .build();
    }

}
