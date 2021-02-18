package be.kul.apigateway.routing;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("USER-SERVICE", r -> r.path("/users/**")
                        .uri("lb://USER-SERVICE"))
                .route("CAR-SERVICE", r -> r.path("/cars/**")
                        .uri("lb://CAR-SERVICE"))
                .route("CAR-LOG-SERVICE", r -> r.path("/car_logs/**")
                        .uri("lb://CAR-LOG-SERVICE"))
                .route("RIDE-SERVICE", r -> r.path("/rides/**")
                        .uri("lb://RIDE-SERVICE"))
                .route("BILLING-SERVICE", r -> r.path("/billing/**")
                        .uri("lb://BILLING-SERVICE"))
                .build();
    }
}
