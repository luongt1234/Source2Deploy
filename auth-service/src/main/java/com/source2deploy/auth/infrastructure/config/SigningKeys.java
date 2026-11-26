package com.source2deploy.auth.infrastructure.config;

import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.HashSet;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

@Configuration(proxyBeanMethods = false)
class SigningKeys {
    @Bean
    RSAKey signingKey(AuthSettings settings, Environment environment) throws Exception {
        Path file = settings.signingKey().toAbsolutePath();
        if (environment.acceptsProfiles(Profiles.of("dev & !prod"))) {
            generateOnce(file);
        }
        JWK key = JWK.parse(Files.readString(file));
        if (!(key instanceof RSAKey rsa) || !rsa.isPrivate()) {
            throw new IllegalStateException("Configure a private RSA signing JWK");
        }
        return normalize(rsa);
    }

    @Bean
    JWKSource<SecurityContext> jwkSource(RSAKey signingKey, AuthSettings settings) throws Exception {
        var keys = new ArrayList<JWK>();
        keys.add(signingKey);
        var ids = new HashSet<String>();
        ids.add(signingKey.getKeyID());
        if (settings.previousKeys() != null) {
            for (JWK key : JWKSet.parse(Files.readString(settings.previousKeys())).getKeys()) {
                if (!(key instanceof RSAKey rsa) || rsa.isPrivate()) {
                    throw new IllegalStateException("Previous signing keys must contain public RSA JWKs only");
                }
                RSAKey publicKey = normalize(rsa);
                if (!ids.add(publicKey.getKeyID())) {
                    throw new IllegalStateException("Duplicate signing key identifier");
                }
                keys.add(publicKey);
            }
        }
        return new ImmutableJWKSet<>(new JWKSet(keys));
    }

    private static RSAKey normalize(RSAKey key) throws Exception {
        if (key.size() < 2048) {
            throw new IllegalStateException("RSA signing keys must have at least 2048 bits");
        }
        String thumbprint = key.computeThumbprint().toString();
        if (key.getKeyID() != null && !thumbprint.equals(key.getKeyID())) {
            throw new IllegalStateException("Signing key identifiers must use the JWK thumbprint");
        }
        return new RSAKey.Builder(key).algorithm(JWSAlgorithm.RS256).keyUse(KeyUse.SIGNATURE)
                .keyID(thumbprint).build();
    }

    private static void generateOnce(Path file) throws Exception {
        if (Files.exists(file)) {
            return;
        }
        Files.createDirectories(file.getParent());
        Path lockFile = file.resolveSibling(file.getFileName() + ".lock");
        try (var channel = FileChannel.open(lockFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                var lock = channel.lock()) {
            if (Files.exists(file)) {
                return;
            }
            var generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(3072);
            var pair = generator.generateKeyPair();
            var key = new RSAKey.Builder((java.security.interfaces.RSAPublicKey) pair.getPublic())
                    .privateKey(pair.getPrivate()).build();
            Path temporary = Files.createTempFile(file.getParent(), ".signing-", ".key");
            try {
                Files.writeString(temporary, normalize(key).toJSONString());
                Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE);
            } finally {
                Files.deleteIfExists(temporary);
            }
        }
    }
}
