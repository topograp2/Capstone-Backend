package com.capstone.bszip.commonDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@Schema(description = "공통 응답 DTO")
public class SuccessResponse<T> {
    @Schema(description = "API 요청 성공 여부", example = "true")
    private boolean result;
    @Schema(description = "HTTP 상태 코드", example = "200")
    private Integer status;
    @Schema(description = "응답 데이터", nullable = true)
    private String message;
    @Schema(description = "응답 메시지", example = "임시 비밀번호 생성 후, 메일 전송 완료")
    private T data;
}
