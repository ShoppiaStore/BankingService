package com.example.demo.Service;


import com.example.demo.entity.User;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {


    private final String secret_key = "mysecretkey";

  //  private final long accessTokenValidity = 60*60*1000;
  private final long accessTokenValidity = 24 * 60 * 60 * 1000; // 24 ساعة


    private final JwtParser jwtParser;

    public String extractEmail(String token) { // (decode) JWT token then return claims
        return parseJwtClaims(token).getSubject();// get subject==get email

    }

    public int extractUserId(String token) { // (decode) JWT token then return claims
        return ((Number) parseJwtClaims(token).get("userId")).intValue();

    }



    public JwtService()
    {
        this.jwtParser = Jwts.parser().setSigningKey(secret_key);
    }

    public String createToken(User user , Map<String , Object> extraClaims) {
        System.out.println("1");

        Date expiration = new Date(System.currentTimeMillis() + accessTokenValidity);
        System.out.println("Token expires at: " + expiration);
        System.out.println("2");



        extraClaims.put("userId", user.getId());
        System.out.println("3");


        System.out.println("createToken");
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS256, secret_key)
                .compact();


    }

    private Claims parseJwtClaims(String token)
    {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public String resolveToken(HttpServletRequest request)
    {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public Claims resolveClaims(HttpServletRequest req)
    {
        try {
            String token = resolveToken(req);
            if (token != null) {
                return parseJwtClaims(token);
            }
            return null;
        } catch (ExpiredJwtException ex) {
            req.setAttribute("expired", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            req.setAttribute("invalid", ex.getMessage());
            throw ex;
        }
    }


    public boolean isTokenExpired(Date expirationDate) throws AuthenticationException
    {
        try {
            if(expirationDate.before(new Date()))
                return true;
            else
                return false;
        } catch (Exception e) {
            throw e;
        }
    }


    public boolean isTokenValid(String accessToken , UserDetails userDetails) {
        String username = userDetails.getUsername();
        Claims claims = parseJwtClaims(accessToken);
        return username.equals(claims.getSubject()) && !isTokenExpired(claims.getExpiration());
    }


    public UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        }
        return null;
    }
}
