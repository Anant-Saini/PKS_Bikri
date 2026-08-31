package com.perfectkode.bikri.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;



@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(

    /**
     * Secret key for signing JWT tokens.
     */
    String secret,

    /**
     * Token expiration duration in milliseconds (default 24 hours).
     */
    String expirationMilliseconds
) {

}
