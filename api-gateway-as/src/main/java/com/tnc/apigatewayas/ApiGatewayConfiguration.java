//package com.tnc.apigatewayas;
//
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//
//@Configuration
//public class ApiGatewayConfiguration {
//
//    @Bean
//    public RouteLocator gatewayRoute(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route(p -> p
//                        .path("/shelter-microservice")
//                        .uri("lb://shelter-microservice/"))
//                .build();
//    }
//}
