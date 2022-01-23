package com.makridin.instazoo.security;

public interface SecurityConstants {

    String SIGN_UP_URLS = "/api/auth/**";
    String SECRET = "SecretKeyGenJWT";
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
    String CONTENT_TYPE = "application/json";
    long EXPIRATION_TIME = 86400000;
}
