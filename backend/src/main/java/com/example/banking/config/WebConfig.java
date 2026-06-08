package com.example.banking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // מאשר את כל ה-endpoints במערכת
                .allowedOrigins("*") // מאשר הגעה מכל כתובת (כולל ה-React שלכן)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // מאשר את כל סוגי הבקשות
                .allowedHeaders("*"); // מאשר את כל סוגי ההדרים
    }
}