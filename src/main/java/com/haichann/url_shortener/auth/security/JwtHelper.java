package com.haichann.url_shortener.auth.security;

import com.haichann.url_shortener.common.exception.AppException;
import com.haichann.url_shortener.common.exception.ErrorCode;
import com.haichann.url_shortener.config.JwtProperties;
import com.haichann.url_shortener.user.entity.User;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class JwtHelper {

    JwtProperties jwtProperties;

    public String validateToken(String token) {
        return validateTokenAndGetClaims(token).getSubject();
    }

    public JWTClaimsSet validateTokenAndGetClaims(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            JWSVerifier verifier = new MACVerifier(jwtProperties.getSecretKey().getBytes());
            if (jwsObject.verify(verifier)) {
                JWTClaimsSet claimsSet = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
                if (!jwtProperties.getIssuer().equals(claimsSet.getIssuer())) {
                    throw new AppException(ErrorCode.INVALID_TOKEN);
                }
                if (claimsSet.getExpirationTime().before(new Date())) {
                    throw new AppException(ErrorCode.TOKEN_EXPIRED);
                }
                return claimsSet;
            } else {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }
        } catch (JOSEException | java.text.ParseException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    public String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer(jwtProperties.getIssuer())
                .jwtID(UUID.randomUUID().toString())
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(jwtProperties.getExpirationMinutes(), ChronoUnit.MINUTES)))
                .claim("scope", buildScope(user))
                .claim("email", user.getEmail())
                .build();

        JWSObject jwsObject = new JWSObject(header, new Payload(claims.toJSONObject()));
        try{
            jwsObject.sign(new MACSigner(jwtProperties.getSecretKey().getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.INTERNAL_ERROR);
        }
    }

    public String generateRefreshToken() {
        byte[] randomBytes = new byte[64];
        new SecureRandom().nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding().encodeToString(randomBytes);
    }

    private String buildScope(User user) {
        if(user.getRole() == null) return "";
        return user.getRole().getRoleName();
    }


}
