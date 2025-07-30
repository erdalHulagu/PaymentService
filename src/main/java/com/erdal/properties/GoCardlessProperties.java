package com.erdal.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "gocardless")
@Data
public class GoCardlessProperties {
    private String apiKey;
}



