package com.capstone.bszip.auth.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request,response);
        }catch (ExpiredJwtException e){
            AuthErrorResponseUtil.setErrorResponse(response, HttpStatus.UNAUTHORIZED,e.getMessage());
        } catch (JwtException e) {
            AuthErrorResponseUtil.setErrorResponse(response, HttpStatus.UNAUTHORIZED,e.getMessage());
        } catch (Exception e) {
            AuthErrorResponseUtil.setErrorResponse(response, HttpStatus.UNAUTHORIZED,e.getMessage());
        }
    }
}