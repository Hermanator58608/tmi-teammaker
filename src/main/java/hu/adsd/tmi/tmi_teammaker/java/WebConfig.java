package hu.adsd.tmi.tmi_teammaker.java;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Define the mapping pattern for which CORS configuration is applied (in this case, all URLs)
                .allowedOriginPatterns("http://localhost:*")  // Specify the allowed origin patterns (in this case, any origin starting with "http://localhost:")
                .allowCredentials(true)  // Allow including credentials (e.g., cookies) in CORS requests
                .maxAge(3600)  // Specify the maximum age of the CORS configuration in seconds (in this case, 3600 seconds or 1 hour)
                .allowedHeaders("Accept", "Content-Type", "Origin", "Authorization", "X-Auth-Token")  // Specify the allowed request headers
                .exposedHeaders("X-Auth-Token", "Authorization", "Access-Control-Allow-Credentials")  // Specify the headers that are exposed to clients
                .allowedMethods("POST", "GET", "DELETE", "PUT", "OPTIONS");  // Specify the allowed HTTP methods for CORS requests
    }
}
