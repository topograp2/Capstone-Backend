package com.capstone.bszip.Book.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class BooksnapPreviewDto {
    private Long bookReviewId;
    private String userName;
    private Date createdAt;
    private int like;
    private String review;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isLiked;

    private int rating;

    BookInfoDto bookInfo;

}
