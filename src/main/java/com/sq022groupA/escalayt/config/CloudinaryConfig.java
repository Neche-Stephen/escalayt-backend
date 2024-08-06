package com.sq022groupA.escalayt.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

//    @Value("${CLOUD_NAME}")
//    private String cloudName;
//
//    @Value("${API_KEY}")
//    private String apiKey;
//
//    @Value("${API_SECRET}")
//    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dfco8gsmu");
        config.put("api_key", "271727554632952");
        config.put("api_secret", "kLMecTehIIO5lFdNg5PwFNveH90");

        return new Cloudinary(config);
    }
}
