package com.capstone.bszip.Bookstore.controller;

import com.capstone.bszip.Bookstore.service.BookstoreReviewService;
import com.capstone.bszip.Bookstore.service.dto.BookstoreReviewRequest;
import com.capstone.bszip.Bookstore.service.dto.BookstoreReviewResponse;
import com.capstone.bszip.Member.domain.Member;
import com.capstone.bszip.commonDto.ErrorResponse;
import com.capstone.bszip.commonDto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/bookstore/reviews")
@RequiredArgsConstructor
@Tag(name = "BookstoreReview", description = "서점 리뷰 관리 API")
public class BookstoreReviewController {

    private final BookstoreReviewService bookstoreReviewService;
    @Operation(
            summary = "서점 리뷰 등록",
            description = "[로그인 필수] 사용자가 특정 서점에 리뷰를 작성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "리뷰 등록 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "서점 ID 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<?> createReview(
            @AuthenticationPrincipal Member member,
            @RequestBody BookstoreReviewRequest request
            ) {
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("인증되지 않은 사용자입니다.")
                            .detail("로그인이 필요한 서비스입니다.")
                            .build());
        }
        try {
            BookstoreReviewResponse response = bookstoreReviewService.createReview(member, request);
            return ResponseEntity.ok(SuccessResponse.<BookstoreReviewResponse>builder()
                    .result(true)
                    .status(HttpStatus.CREATED.value())
                    .message("서점 리뷰 등록 성공")
                    .data(response)
                    .build());
        }catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.NOT_FOUND.value())
                            .message("서점 id 오류")
                            .detail(e.getMessage())
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
