package com.spoteditor.backend.global.constants;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;

public class JwtConstants {
    public static final MacAlgorithm ALGORITHM = Jwts.SIG.HS512;

    public static final String TOKEN_TYPE = "JWT";

    public static final String ACCESS_TOKEN = "AccessToken";

    public static final String REFRESH_TOKEN = "RefreshToken";
}
