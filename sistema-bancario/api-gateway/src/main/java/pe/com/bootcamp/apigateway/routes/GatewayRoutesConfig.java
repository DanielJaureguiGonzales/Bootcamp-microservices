package pe.com.bootcamp.apigateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("customer-service", route -> route
                        .path("/api/customer/**")
                        .uri("lb://CUSTOMER-SERVICE")
                )
                .route("account-service", route -> route
                        .path("/api/account/**")
                        .uri("lb://ACCOUNT-SERVICE")
                )
                .route("credit-service", route -> route
                        .path("/api/credit/**")
                        .uri("lb://CREDIT-SERVICE")
                )
                .build();
    }

}
