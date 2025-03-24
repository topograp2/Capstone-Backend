package com.capstone.bszip.auth.security;

import com.capstone.bszip.commonDto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class AuthErrorResponseUtil {
    public static void setErrorResponse(HttpServletResponse response, HttpStatus status, String errorMsg){
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ErrorResponse errorResponse = ErrorResponse.builder()
                .result(false)
                .status(status.value())
                .message("JWT 인증 실패")
                .detail(errorMsg)
                .build();
        try{
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}