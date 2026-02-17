package com.banco.bff.service;

import com.banco.bff.config.EncryptionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class EncryptionService {
    
    private final EncryptionProperties properties;
    private static final String ALGORITHM = "AES";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    public String encrypt(String plainText) {
        try {
            log.info("🔐 ENCRIPTANDO: '{}'", plainText);
            
            // 1. Generar IV aleatorio
            byte[] iv = new byte[IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);
            log.debug("IV generado ({} bytes)", iv.length);
            
            // 2. Obtener clave desde Base64
            byte[] keyBytes = decodeBase64Key(properties.getSecretKey());
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            log.debug("Clave cargada ({} bytes)", keyBytes.length);
            
            // 3. Configurar cipher
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);
            
            // 4. Encriptar
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            // 5. Combinar IV + datos encriptados
            byte[] combined = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);
            
            // 6. Convertir a Base64
            String result = Base64.getEncoder().encodeToString(combined);
            log.info("✅ ENCRIPTADO: '{}' -> {} caracteres", 
                     plainText, result.length());
            
            return result;
            
        } catch (Exception e) {
            log.error(" ERROR encriptando: {}", e.getMessage(), e);
            throw new RuntimeException("Error de encriptación: " + e.getMessage());
        }
    }
    
    public String decrypt(String encryptedText) {
        try {
            log.info("🔓 DESENCRIPTANDO: {} caracteres", encryptedText.length());
            
            // 1. Decodificar Base64
            byte[] combined = Base64.getDecoder().decode(encryptedText.trim());
            log.debug("Datos decodificados: {} bytes", combined.length);
            
            if (combined.length <= IV_LENGTH) {
                throw new RuntimeException("Datos encriptados insuficientes");
            }
            
            // 2. Separar IV y datos encriptados
            byte[] iv = new byte[IV_LENGTH];
            byte[] encryptedBytes = new byte[combined.length - IV_LENGTH];
            
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
            System.arraycopy(combined, IV_LENGTH, encryptedBytes, 0, encryptedBytes.length);
            
            log.debug("IV extraído: {} bytes", iv.length);
            log.debug("Datos encriptados: {} bytes", encryptedBytes.length);
            
            // 3. Obtener clave
            byte[] keyBytes = decodeBase64Key(properties.getSecretKey());
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            
            // 4. Configurar cipher
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);
            
            // 5. Desencriptar
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            
            String result = new String(decryptedBytes, StandardCharsets.UTF_8);
            log.info("✅ DESENCRIPTADO: '{}'", result);
            
            return result;
            
        } catch (IllegalArgumentException e) {
            log.error(" ERROR Base64 inválido: {}", e.getMessage());
            throw new RuntimeException("Formato Base64 incorrecto: " + e.getMessage());
        } catch (javax.crypto.AEADBadTagException e) {
            log.error(" ERROR Tag mismatch (clave incorrecta o datos corruptos): {}", e.getMessage());
            throw new RuntimeException("Error de autenticación: clave incorrecta o datos modificados");
        } catch (Exception e) {
            log.error(" ERROR desencriptando: {}", e.getMessage(), e);
            throw new RuntimeException("Error de desencriptación: " + e.getMessage());
        }
    }
    
    /**
     * Decodifica clave Base64 a bytes (32 bytes para AES-256)
     */
    private byte[] decodeBase64Key(String base64Key) {
        if (base64Key == null || base64Key.trim().isEmpty()) {
            throw new RuntimeException("Clave secreta no configurada");
        }
        
        try {
            // Decodificar Base64
            byte[] keyBytes = Base64.getDecoder().decode(base64Key.trim());
            log.debug("Clave Base64 '{}' decodificada a {} bytes", 
                     base64Key.substring(0, Math.min(20, base64Key.length())) + "...", 
                     keyBytes.length);
            
       
            if (keyBytes.length != 32) {
                log.warn("⚠️ La clave debe ser de 32 bytes para AES-256, actual: {} bytes", 
                        keyBytes.length);
                
             
                byte[] adjustedKey = new byte[32];
                int copyLength = Math.min(keyBytes.length, 32);
                System.arraycopy(keyBytes, 0, adjustedKey, 0, copyLength);
                keyBytes = adjustedKey;
            }
            
            return keyBytes;
            
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Clave Base64 inválida: " + e.getMessage());
        }
    }
    
    public boolean isEncrypted(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        
        try {
            byte[] decoded = Base64.getDecoder().decode(text.trim());
            return decoded.length > IV_LENGTH;
        } catch (Exception e) {
            return false;
        }
    }
    
 
    public static String generateNewKey() {
        try {
            SecureRandom random = new SecureRandom();
            byte[] keyBytes = new byte[32]; 
            random.nextBytes(keyBytes);
            return Base64.getEncoder().encodeToString(keyBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error generando clave: " + e.getMessage());
        }
    }
}