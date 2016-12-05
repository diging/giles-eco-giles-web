package edu.asu.diging.gilesecosystem.web.service.system.impl;

import io.jsonwebtoken.impl.crypto.MacProvider;

import java.security.Key;
import java.util.Base64;

import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.web.service.system.ISigningSecretGenerator;

@Service
public class SigningSecretGenerator implements ISigningSecretGenerator {

    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.system.impl.ISigningSecretManager#generateSigningSecret()
     */
    @Override
    public String generateSigningSecret() {
        Key key = MacProvider.generateKey();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}
