package com.forge_mind.forge_mind.Config;

import com.forge_mind.forge_mind.Service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {


    private AuthService authService;

    public  JwtAuthFilter(AuthService authService){
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException, IOException {
        String path = request.getRequestURI();
        // Only allow specific unprotected endpoints
        if (path.equals("/signup") || path.equals("/signup/verify") || path.equals("/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");
        System.out.println("Processing request to: " + path + " with auth header: " + (authHeader != null ? "Present" : "Missing"));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("No valid Authorization header found");
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        System.out.println("Extracted JWT token (length: " + token.length() + ")");

        try {
            String username = authService.verifyjwt(authHeader); // Pass full header
            System.out.println("JWT verification successful for user: " + username);
            
            if (username != null && !username.contains("Invalid") && !username.contains("expired")) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, List.of());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("Successfully authenticated user: " + username);
            } else {
                System.out.println("JWT verification failed: " + username);
            }

        } catch (Exception e) {
            System.out.println("JWT verification exception: " + e.getMessage());
            // Token invalid or expired — don't authenticate
        }

        filterChain.doFilter(request, response);
    }
}

