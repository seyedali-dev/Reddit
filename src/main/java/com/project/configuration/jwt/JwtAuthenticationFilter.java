package com.project.configuration.jwt;

import com.project.configuration.security.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        if (excludeEndpoint(request, response, filterChain)) {
            log.info("ignored the 'http://localhost:8080/api/v1/auth/user/accountVerification/**' path.");
            return;
        }
        final String requestHeader = request.getHeader("Authorization");
        String username = null, token = null;

        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            token = requestHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(token);
            } catch (ExpiredJwtException e) {
                System.out.println("<<<<<<<<<< JwtAuthenticationFilter -> doFilterInternal: \"Jwt token has been expired\" >>>>>>>>>>");
            } catch (Exception e) {
                System.out.println("JwtAuthenticationFilter -> doFilterInternal");
                e.printStackTrace();
            }
        } else System.out.println("<<<<<<<<<< JwtAuthenticationFilter -> doFilterInternal:" +
                                  " \"Invalid Token! there is no token to authorize you\"" +
                                  " \"or token does not start with 'Bearer ' string\" >>>>>>>>>>");

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            final UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else
                System.out.println("<<<<<<<<<< JwtAuthenticationFilter -> doFilterInternal: Token is not valid! >>>>>>>>>>");
        } else System.out.println("<<<<<<<<<< JwtAuthenticationFilter -> doFilterInternal: Invalid Token >>>>>>>>>>");
        filterChain.doFilter(request, response);
    }

    private boolean excludeEndpoint(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String excludePath = "http://localhost:8080/api/v1/auth/user/accountVerification";
        if (path.startsWith(excludePath)) {
            filterChain.doFilter(request, response);
            return true;
        }
        return false;
    }
}