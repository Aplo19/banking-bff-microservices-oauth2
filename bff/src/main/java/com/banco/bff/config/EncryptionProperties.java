package com.banco.bff.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "encryption")
public class EncryptionProperties {
    private String secretKey;
    private String algorithm = "AES/GCM/NoPadding";
    private int keySize = 256;
    private int tagLength = 128;
}