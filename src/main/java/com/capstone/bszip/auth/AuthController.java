package com.capstone.bszip.auth;

import com.capstone.bszip.auth.dto.TokenRequest;
import com.capstone.bszip.auth.dto.TokenResponse;
import com.capstone.bszip.auth.refreshToken.RefreshTokenService;
import com.capstone.bszip.auth.security.JwtUtil;
import com.capstone.bszip.commonDto.ErrorResponse;
import com.capstone.bszip.commonDto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 관련")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;


    @Operation(summary = "Access Token 재발급", description = "유효한 Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access Token 재발급 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "401", description = "유효하지 않거나 만료된 Refresh Token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/auth/reissue")
    public ResponseEntity reissue(@RequestBody TokenRequest tokenRequest){
        String refreshToken = tokenRequest.getRefreshToken();
        System.out.println(refreshToken);
        try {
            // 1. Redis에서 Refresh Token 검증 (MySQL 조회 대신)
            String email = refreshTokenService.getEmailByRefreshToken(refreshToken);
            System.out.println(email);
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ErrorResponse.builder()
                                .result(false)
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .message("인증 실패")
                                .detail("INVALID_OR_EXPIRED_REFRESH_TOKEN")
                                .build());
            }
            String newAccessToken = jwtUtil.createAccessToken(email);

            return ResponseEntity.ok(SuccessResponse.builder()
                    .result(true)
                    .status(HttpStatus.OK.value())
                    .message("access token 재발급 성공")
                    .data(new TokenResponse(newAccessToken,refreshToken))
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
}
