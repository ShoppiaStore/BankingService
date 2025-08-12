package com.example.demo.auth;


import com.example.demo.Service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("doFilterInternal");
        System.out.println("Filter thread: " + Thread.currentThread().getName());

        try {
            System.out.println("try1");
            String authHeader = request.getHeader("Authorization");
            String accessToken = null;
            String userEmail = null;

            String path = request.getServletPath();

            if (path.startsWith("/activateUser")) {
                System.out.println("if1");
                filterChain.doFilter(request, response); // skip JWT filter
                return;
            }
            if (path.startsWith("/regenerateOtp")) {
                System.out.println("if3");
                filterChain.doFilter(request, response); // skip JWT filter
                return;
            }
            if (path.startsWith("/rest/auth/")) {
                System.out.println("if2");
                filterChain.doFilter(request, response);
                return;
            }



//            if (path.startsWith("/validateToken")) {
//                System.out.println("if3");
//                filterChain.doFilter(request, response);
//                return;
//            }
//            if (path.startsWith("/extractUserId")) {
//                System.out.println("if3");
//                filterChain.doFilter(request, response);
//                return;
//            }






            accessToken = authHeader.substring("Bearer ".length());
            Claims claims = jwtService.resolveClaims(request);
            userEmail = claims.getSubject();


            if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
                System.out.println("if100");

                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                if(userDetails != null && jwtService.isTokenValid(accessToken , userDetails)) {
                    System.out.println("if4");

                    System.out.println("email : " + userEmail);

                    Authentication authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                System.out.println("Filter thread: " + Thread.currentThread().getName());

            }

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("catch");

        }
        filterChain.doFilter(request, response);
    }
    }
