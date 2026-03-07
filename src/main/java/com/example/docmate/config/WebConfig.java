package com.example.docmate.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map /uploads/** to your local folder
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:C:/Users/ANIKET/OneDrive/Desktop/docmate/uploads/"); // "uploads/" folder in your project root
    }
}
