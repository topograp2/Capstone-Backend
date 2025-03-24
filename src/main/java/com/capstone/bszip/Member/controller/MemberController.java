package com.capstone.bszip.Member.controller;

import com.capstone.bszip.auth.dto.TokenRequest;
import com.capstone.bszip.auth.dto.TokenResponse;
import com.capstone.bszip.auth.AuthService;
import com.capstone.bszip.auth.security.JwtUtil;
import com.capstone.bszip.Member.service.MemberService;
import com.capstone.bszip.Member.service.dto.LoginRequest;
import com.capstone.bszip.Member.service.dto.SignupAddRequest;
import com.capstone.bszip.Member.service.dto.SignupRequest;
import com.capstone.bszip.commonDto.ErrorResponse;
import com.capstone.bszip.commonDto.SuccessResponse;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 관리 API")
@RequestMapping("/auth")
public class MemberController {
    private final MemberService memberService;
    private final AuthService authService;

    //회원 가입
    @PostMapping("/signup")
    @Operation(summary = "회원 가입", description = "이메일과 비밀번호로 회원 가입을 진행합니다.")
    @ApiResponse(responseCode = "200", description = "회원가입-이메일,비밀번호 성공",
            content = @Content(schema = @Schema(implementation = Map.class)))
    @ApiResponse(responseCode = "500", description = "서버 오류",
            content = @Content(schema = @Schema(implementation = Map.class)))
    public ResponseEntity<?> signup(
            @Valid @RequestBody SignupRequest signupRequest){
        try {
            // 이메일 및 비밀번호 저장
            memberService.registerMember(signupRequest);
            // 이메일을 포함한 임시 JWT 토큰 발급
            String token = JwtUtil.issueTempToken(signupRequest.getEmail());
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(SuccessResponse.builder()
                            .result(true)
                            .status(HttpStatus.OK.value())
                            .message("임시 토큰 발급 완료")
                            .data(Map.of("token", token))
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("서버 오류가 발생했습니다.")
                            .detail(e.getMessage())
                            .build());
        }
    }
    //회원 가입-닉네임
    @PostMapping("/signup/add")
    @SecurityRequirement(name = "jwtAuth")
    @Operation(summary = "회원 가입 추가 정보", description = "닉네임을 추가하여 회원 가입을 완료합니다.")
    @ApiResponse(responseCode = "200", description = "회원가입 완료",
            content = @Content(schema = @Schema(implementation = Map.class)))
    @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰",
            content = @Content(schema = @Schema(implementation = Map.class)))
    @ApiResponse(responseCode = "500", description = "서버 오류",
            content = @Content(schema = @Schema(implementation = Map.class)))
    public ResponseEntity<?> add(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody SignupAddRequest signupAddRequest){
        System.out.println(token);
        try {
            // 토큰에서 이메일 추출
            String email = JwtUtil.extractEmail(token);
            System.out.println(email);
            // 닉네임과 이메일을 이용해 최종 회원가입
            memberService.registerMemberNickname(email, signupAddRequest.getNickname());

            return ResponseEntity.ok(SuccessResponse.builder()
                    .result(true)
                    .status(HttpStatus.OK.value())
                    .message("회원가입 완료")
                    .build());
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("유효하지 않은 토큰입니다.")
                            .detail(e.getMessage())
                            .build());
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("서버 오류가 발생했습니다.")
                            .detail(e.getMessage())
                            .build());
        }
    }
    //로그인
    @PostMapping("/signin")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = TokenResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패 - 잘못된 자격 증명",
            content = @Content(schema = @Schema(implementation = Map.class)))
    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = Map.class)))

    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        try{
             TokenResponse tokens = memberService.loginUser(loginRequest);
             authService.login(loginRequest.getEmail(), tokens.getRefreshToken());
            return ResponseEntity.ok(SuccessResponse.builder()
                    .result(true)
                    .status(HttpStatus.OK.value())
                    .message("로그인 성공")
                    .data(tokens)
                    .build());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("인증 실패")
                            .detail("잘못된 자격 증명")
                            .build());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.NOT_FOUND.value())
                            .message("존재하지 않는 사용자입니다.")
                            .detail(e.getMessage())
                            .build());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("서버 오류가 발생했습니다.")
                            .detail(e.getMessage())
                            .build());
        }
    }
    //로그아웃
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "refresh token을 삭제하며 로그아웃합니다.")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공",
            content = @Content(schema = @Schema(implementation = TokenResponse.class)))
    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = Map.class)))
    public ResponseEntity<?> logout(HttpServletRequest httpServletRequest,@RequestBody TokenRequest tokenRequest){
        String authorizationHeader = httpServletRequest.getHeader("Authorization");

        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Access token을 찾을 수 없습니다.")
                            .build());
        }
        String accessToken = authorizationHeader.substring(7);
        String refreshToken = tokenRequest.getRefreshToken();
        try {
            Date expirationDate = JwtUtil.getExpiration(accessToken);
            authService.logout(refreshToken, accessToken, expirationDate);
            return ResponseEntity.ok(SuccessResponse.builder()
                    .result(true)
                    .status(HttpStatus.OK.value())
                    .message("로그아웃 성공")
                    .build());
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("유효하지 않은 토큰입니다.")
                            .detail(e.getMessage())
                            .build());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("서버 오류가 발생했습니다.")
                            .detail(e.getMessage())
                            .build());
        }
    }
}

