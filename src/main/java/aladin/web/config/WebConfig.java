//package aladin.web.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.config.EnableWebFlux;
//import org.springframework.web.reactive.config.ViewResolverRegistry;
//import org.springframework.web.reactive.config.WebFluxConfigurer;
//import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfigurer;
//
//@Configuration
//@EnableWebFlux
//public class WebConfig implements WebFluxConfigurer {
//
//    @Override
//    public void configureViewResolvers(ViewResolverRegistry registry) {
//        registry.freeMarker();
//    }
//
//    // Configure FreeMarker...
//
//    @Bean
//    public FreeMarkerConfigurer freeMarkerConfigurer() {
//        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
//        configurer.setTemplateLoaderPath("classpath:/templates/freemarker");
//        return configurer;
//    }
//}