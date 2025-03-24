package com.capstone.bszip.Book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "책 리뷰 수정 dto")
public class BookReviewUpdateDto {
    @Schema(description = "책 리뷰 별점",example = "4")
    private int rating;
    @Schema(description = "책 리뷰 내용",example = "내가 무엇이 될지 궁금했지만 어쩐지 알 것 같았다 라는 말이 슬펐다.")
    private String reviewText;
}
