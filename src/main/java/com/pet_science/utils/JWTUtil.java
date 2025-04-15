package com.pet_science.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtil {

    private static final String SECURITY = "pet_security_0qwer";
    public static String createToken(Integer id,boolean isAdmin){
        Map<String,String> map = new HashMap<>();
        map.put("alg","HS256"); //描述加密类型
        map.put("typ","jwt");   //描述身份验证的工具
        return JWT.create().withHeader(Collections.unmodifiableMap(map))
                .withIssuedAt(new Date())  // 当前时间为签发时间
                //30天过期
                .withExpiresAt(Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant()))
                .withClaim("id",id) //公共信息中加入id
                .withClaim("isAdmin",isAdmin)
                .sign(Algorithm.HMAC256(SECURITY));
    }
    private static Map<String, Claim> getTokenMessage(String token)  {
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECURITY)).build();
            jwt = verifier.verify(token);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jwt.getClaims();
    }


    public static boolean verifyAdmin(String token) {
        Map<String, Claim> claims = getTokenMessage(token);
        Claim user_id_claim = claims.get("id");
        Claim isAdmin = claims.get("isAdmin");
        return user_id_claim != null && user_id_claim.asInt() != null && isAdmin.asBoolean();
    }

    public static boolean verifyUser(String token){
        Map<String, Claim> claims = getTokenMessage(token);
        Claim user_id_claim = claims.get("id");
        return user_id_claim != null && user_id_claim.asInt() != null;
    }

    public static Integer getUserId(String token) {
        Map<String, Claim> claims = getTokenMessage(token);
        Claim user_id_claim = claims.get("id");
        if (null == user_id_claim || user_id_claim.asInt() == null) {
            throw new RuntimeException("id is not right");
        }
        return user_id_claim.asInt();
    }

    public static int verifyToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null) {
            return 0;
        }
        if(verifyAdmin(token)){
            return 1;
        }
        if(verifyUser(token)){
            return 2;
        }
        return 0;
    }
}
