package be.kul.apigateway.routing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.factory.TokenRelayGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@Configuration
public class RoutingConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, TokenRelayGatewayFilterFactory filterFactory) {
        return builder.routes()
                .route("USER-SERVICE", r -> r.path("/users/**")
                        .filters(f -> f.filter(filterFactory.apply()))
                        .uri("lb://USER-SERVICE"))
                .route("CAR-SERVICE", r -> r.path("/cars/**")
                        .filters(f -> f.filter(filterFactory.apply()))
                        .uri("lb://CAR-SERVICE"))
                .route("CAR-LOG-SERVICE", r -> r.path("/car_logs/**")
                        .filters(f -> f.filter(filterFactory.apply()))
                        .uri("lb://CAR-LOG-SERVICE"))
                .route("RIDE-SERVICE", r -> r.path("/rides/**")
                        .filters(f -> f.filter(filterFactory.apply()))
                        .uri("lb://RIDE-SERVICE"))
                .route("BILLING-SERVICE", r -> r.path("/billing/**")
                        .filters(f -> f.filter(filterFactory.apply()))
                        .uri("lb://BILLING-SERVICE"))
                .build();
    }
}
