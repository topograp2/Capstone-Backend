package com.capstone.bszip.Bookstore.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookstoreReviewResponse {
    private Long bookstoreReviewId;
    private String nickname;
    private double rating;
    private String text;
    private String imageUrl;
    private LocalDateTime createdAt;
}
