package edu.ace.infinite.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtil {

    private static final String SECURITY = "ace_security";
    public static String createToken(Integer id){
        Map<String,String> map = new HashMap<>();
        map.put("alg","HS256"); //描述加密类型
        map.put("typ","jwt");   //描述身份验证的工具
        return JWT.create().withHeader(Collections.unmodifiableMap(map))
                .withIssuedAt(new Date())  // 当前时间为签发时间
                //30天过期
                .withExpiresAt(Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant()))
                .withClaim("id",id) //公共信息中加入id
                .sign(Algorithm.HMAC256(SECURITY));
    }
    public static Map<String, Claim> verifyToken(String token)  {
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECURITY)).build();
            jwt = verifier.verify(token);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jwt.getClaims();
    }


    public static Integer getUserId(String token) {
        Map<String, Claim> claims = verifyToken(token);
        Claim user_id_claim = claims.get("id");
        if (null == user_id_claim || user_id_claim.asInt() == null) {
            throw new RuntimeException("id is not right");
        }
        return user_id_claim.asInt();
    }

}
