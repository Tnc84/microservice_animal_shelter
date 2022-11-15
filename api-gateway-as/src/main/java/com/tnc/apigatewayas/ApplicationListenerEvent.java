//package com.tnc.apigatewayas;
//
//import org.springframework.cloud.gateway.event.RefreshRoutesResultEvent;
//import org.springframework.cloud.gateway.route.CachingRouteLocator;
//import org.springframework.cloud.gateway.route.Route;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import reactor.core.publisher.Flux;
//
//@Configuration
//public class ApplicationListenerEvent {
//
//    @Bean
//    ApplicationListener<RefreshRoutesResultEvent> routesRefreshed() {
//        return rre -> {
//            System.out.println("******* route updated ********");
//            var crl = (CachingRouteLocator) rre.getSource();
//            Flux<Route> routes = crl.getRoutes();
//            routes.subscribe(System.out::println);
//        };
//    }
//}
