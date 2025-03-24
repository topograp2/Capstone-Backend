package com.capstone.bszip.Member.controller;

import com.capstone.bszip.Member.service.KakaoService;
import com.capstone.bszip.Member.service.MemberService;
import com.capstone.bszip.Member.service.dto.SignupRequest;
import com.capstone.bszip.auth.AuthService;
import com.capstone.bszip.auth.dto.TokenResponse;
import com.capstone.bszip.auth.security.JwtUtil;
import com.capstone.bszip.commonDto.ErrorResponse;
import com.capstone.bszip.commonDto.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kakao/oauth")
@Tag(name="카카오 로그인", description = "카카오 로그인")
public class KakaoLoginController {
    private final KakaoService kakaoService;
    private final MemberService memberService;
    private final AuthService authService;

    @ResponseBody
    @GetMapping("/login")
    public ResponseEntity<?> kakaoLogin(@RequestParam String code, HttpServletResponse response){

        String kakaoEmail = kakaoService.getkakaoEmail(code);
        int loginWay = kakaoService.whichLoginWay(kakaoEmail);



       if(loginWay == 2){
           return ResponseEntity.status(409).body(
                   ErrorResponse.builder()
                   .result(false)
                   .status(409)
                           .message("기본 로그인으로 로그인해야 합니다.")
                           .build()
           );
       }

       // 로그인
       if(loginWay == 3){
           try{
               TokenResponse tokens = kakaoService.loginUser(kakaoEmail);
               authService.login(kakaoEmail,tokens.getRefreshToken());

               return ResponseEntity.ok(
                       SuccessResponse.builder()
                               .result(true)
                               .status(200)
                               .data(tokens)
                               .build()
               );
           }catch (BadCredentialsException e) {
               return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                       .body(Map.of("error", "잘못된 접근"));
           }catch (HttpClientErrorException e){
               return ResponseEntity.status(400)
                       .body(
                               ErrorResponse.builder()
                               .result(false)
                               .status(400)
                               .message("카카오 api 오류")
                                       .detail(e.getMessage())
                                       .build()
                       );
           }
           catch(Exception e){
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                       .body(Map.of("message", e.getMessage()));
           }

       }
       // 카카오 회원가입
       if(loginWay == 1) {
           try{
           SignupRequest signupRequest = new SignupRequest();
           signupRequest.setEmail(kakaoEmail);
           signupRequest.setPassword("kakao-password");
           memberService.registerMember(signupRequest);

           String token = JwtUtil.issueTempToken(kakaoEmail);
           return ResponseEntity.ok()
                   .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                   .body(
                           SuccessResponse.builder()
                           .result(true)
                                   .status(HttpStatus.OK.value())
                                   .message("임시 토큰 발급 완료")
                                   .data(Map.of("token", token))
                                   .build());
            }
           catch (Exception e) {
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                       .body(
                               ErrorResponse.builder()
                               .result(false)
                               .status(500)
                               .message("서버 오류")
                                       .detail(e.getMessage())
                                       .build()
                       );
           }
       }

        return ResponseEntity.status(400).body(
                ErrorResponse.builder()
                .result(false)
                .status(400)
                        .message("Internal Server Error")
                        .build()
        );

    }

}
