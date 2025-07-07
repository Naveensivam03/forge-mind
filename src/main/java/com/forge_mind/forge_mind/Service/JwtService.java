package com.forge_mind.forge_mind.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private Integer expirationTime;






    //expiration time
    public long expirationtime(){
        return expirationTime;
    }

    //to convert the inputs into jwt tokens
    public String generateTokens(UserDetails userDetails ){
        return generateTokens(new HashMap<>() , userDetails);
    }

    public String generateTokens(Map<String , Object> extraclaims , UserDetails userDetails){
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts
                .builder()
                .setClaims(extraclaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expirationTime))
                .signWith(key , SignatureAlgorithm.HS256)
                .compact();

    }

//    public String extractUSername(String token){
//        return extractclaims(token,claims::getSubject);
//    }
//
//    public String extractclaims



        //to extract the claims from the token and then check them with the signature
//        public <T> T extractClaims(String Token){
//        return "";
//        }

    //to verify the jwt token after  the first login
    public Claims verifyToken(String  token ){
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return  Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


}
