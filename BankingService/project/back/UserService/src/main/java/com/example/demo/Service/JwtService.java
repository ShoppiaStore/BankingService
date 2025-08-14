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

     //   Object for parsing JWT tokens
    private final JwtParser jwtParser;


    // Decode token → get claims → return subject (email)
    public String extractEmail(String token) { // (decode) JWT token then return claims
        return parseJwtClaims(token).getSubject();// get subject==get email

    }

    public int extractUserId(String token) { // (decode) JWT token then return claims
        return ((Number) parseJwtClaims(token).get("userId")).intValue();

    }


    // Constructor → initialize parser with secret key
    public JwtService()
    {
        this.jwtParser = Jwts.parser().setSigningKey(secret_key);
    }

    public String createToken(User user , Map<String , Object> extraClaims) {
        System.out.println("1");

        Date expiration = new Date(System.currentTimeMillis() + accessTokenValidity);
        System.out.println("Token expires at: " + expiration);




        extraClaims.put("userId", user.getId());



        System.out.println("createToken");
        // Build and sign JWT token
        return Jwts.builder()
                .setClaims(extraClaims) // Add extra data
                .setSubject(user.getEmail()) // Email as subject
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity)) // Expiry date
                .setIssuedAt(new Date(System.currentTimeMillis())) // Issue date
                .signWith(SignatureAlgorithm.HS256, secret_key) // Sign token
                .compact(); // Convert to String
    }



    private Claims parseJwtClaims(String token)
    {
        return jwtParser.parseClaimsJws(token).getBody();
    }


    // Extract token from HTTP request Authorization header
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

//secret_key → المفتاح اللي بيستخدمه JWT للتوقيع على التوكنات والتحقق منها.
//
//        accessTokenValidity → صلاحية التوكن (هنا 24 ساعة).
//
//extractEmail / extractUserId → بيفك التوكن (decode) ويطلع منه الإيميل أو ID المستخدم.
//
//createToken → بيعمل توكن جديد لليوزر مع الـ claims الإضافية (زي الـ userId)، ويحدد تاريخ الإنشاء والانتهاء.
//
//resolveToken → بيقرأ الهيدر Authorization ويقص كلمة "Bearer " علشان يطلع التوكن نفسه.
//
//resolveClaims → بيجيب الـ claims من التوكن مع التعامل مع الأخطاء (منتهية الصلاحية أو غير صالحة).
//
//isTokenExpired → يتأكد إذا كان التوكن انتهت صلاحيته.
//
//isTokenValid → يتأكد أن اسم المستخدم في التوكن هو نفسه في الـ UserDetails وأن التوكن لسه صالح.
//
//        getCurrentUserDetails → يجيب بيانات اليوزر اللي عامل تسجيل دخول حاليًا من الـ Security Context.
