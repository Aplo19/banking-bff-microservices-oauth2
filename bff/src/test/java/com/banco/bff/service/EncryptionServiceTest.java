package com.banco.bff.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.banco.bff.config.EncryptionProperties;
import org.junit.jupiter.api.Test;

class EncryptionServiceTest {

    @Test
    void encryptAndDecrypt_roundTrip() {
        EncryptionProperties props = new EncryptionProperties();
        props.setSecretKey(EncryptionService.generateNewKey());

        EncryptionService service = new EncryptionService(props);
        String plain = "CLI001";

        String encrypted = service.encrypt(plain);
        String decrypted = service.decrypt(encrypted);

        assertEquals(plain, decrypted);
    }

    @Test
    void isEncrypted_detectsEncryptedPayload() {
        EncryptionProperties props = new EncryptionProperties();
        props.setSecretKey(EncryptionService.generateNewKey());

        EncryptionService service = new EncryptionService(props);
        String encrypted = service.encrypt("CLI002");

        assertTrue(service.isEncrypted(encrypted));
        assertFalse(service.isEncrypted("CLI002"));
    }
}
