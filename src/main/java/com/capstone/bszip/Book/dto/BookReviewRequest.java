package com.capstone.bszip.Book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "책 리뷰 저장 dto")
public class BookReviewRequest {
    @Schema(description = "책 제목",example = "9489838949")
    private Long isbn;
    @Schema(description = "책 리뷰 별점",example = "4")
    private int rating;
    @Schema(description = "책 리뷰 내용",example = "내가 무엇이 될지 궁금했지만 어쩐지 알 것 같았다 라는 말이 슬펐다.")
    private String reviewText;
}
