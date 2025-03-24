package com.capstone.bszip.auth.security;

import com.capstone.bszip.Member.domain.Member;
import com.capstone.bszip.Member.repository.MemberRepository;
import com.capstone.bszip.auth.AuthService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final MemberRepository memberRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();
        // 로그인 관련 경로는 필터 적용 제외
        if (path.startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        //헤더에서 jwt 추출
        String authHeader = request.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); //token 추출
        } else { // 토큰 없는 경우 다음 필터
            filterChain.doFilter(request, response);
            return;
        }

        //유효성 검사
        Claims claims;
        try {
            claims = jwtUtil.extractToken(token);
        } catch (Exception e){
            SecurityContextHolder.clearContext();
            System.out.println("유효성 검사 실패 .... jwtExceptionFilter");
            throw e; // 예외를 다시 던짐
        }

        //로그아웃 검사
        if(authService.isTokenBlackList(token)) {
            AuthErrorResponseUtil.setErrorResponse(response, HttpStatus.UNAUTHORIZED,"로그아웃된 사용자입니다.");
            return;
        }

        String email = claims.getSubject();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Username not found: "+ email));
        List <GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(member, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println("유효성 검사 성공 - Claims: " + claims);

        filterChain.doFilter(request, response);
    }
}