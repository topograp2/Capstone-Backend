package com.capstone.bszip.Book.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Schema(description = "책 리뷰 저장 dto")
public class BookReviewRequest {

    @Getter
    @Schema(description = "리뷰 저장 dto")
    public static class ReviewCreate{
        @Schema(description = "isbn",example = "9489838949")
        private Long isbn;

        @Schema(description = "북 아이디", example = "1")
        private Long bookId;

        @Schema(description = "서점 아이디")
        List<Long> bookstoreIds;

        @Schema(description = "책 리뷰 별점",example = "4")
        private int rating;

        @Schema(description = "책 리뷰 내용",example = "내가 무엇이 될지 궁금했지만 어쩐지 알 것 같았다 라는 말이 슬펐다.")
        private String reviewText;
    }

    @Getter
    public static class BookCreate{
        @Schema(description = "서점 아이디")
        @JsonProperty("bookstoreIds")
        List<Long> bookstoreIds;

        @Schema(description = "책 제목")
        @JsonProperty("title")
        private String title;

        @Schema(description = "작가이름String")
        @JsonProperty("authorsString")
        private String authorsString;

        @JsonProperty("rating")
        @Schema(description = "책 리뷰 별점",example = "4")
        private int rating;

        @JsonProperty("reviewText")
        @Schema(description = "책 리뷰 내용",example = "내가 무엇이 될지 궁금했지만 어쩐지 알 것 같았다 라는 말이 슬펐다.")
        private String reviewText;
    }
}
