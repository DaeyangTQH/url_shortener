package com.haichann.url_shortener.helper;

import com.haichann.url_shortener.exception.AppException;
import com.haichann.url_shortener.exception.ErrorCode;
import com.haichann.url_shortener.model.dto.request.LocalLoginRequest;
import com.haichann.url_shortener.model.dto.response.AuthenticationResponse;
import com.haichann.url_shortener.model.entity.User;
import com.haichann.url_shortener.repository.UserRepo;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class JwtHelper {
    UserRepo userRepo;

    @NonFinal
    @Value("${jwt.secret-key}")
    String secretKey;

    @NonFinal
    @Value("${jwt.issuer}")
    String issuer;

    @NonFinal
    @Value("${jwt.expiration}")
    long expiration;

//    public AuthenticationResponse authenticateLocal(LocalLoginRequest) throws AppException {
//
//    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer(issuer)
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(expiration, ChronoUnit.MINUTES)))
                .claim("scope", buildScope(user))
                .claim("email", user.getEmail())
                .build();

        JWSObject jwsObject = new JWSObject(header, new Payload(claims.toJSONObject()));
        try{
            jwsObject.sign(new MACSigner(secretKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private String buildScope(User user) {
        if(user.getRole() == null) return "";
        return user.getRole().getRoleName();
    }
}
