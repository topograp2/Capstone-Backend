package com.capstone.bszip.Bookstore.service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookstoreReviewRequest {
    private Long bookstoreId;
    @Min(1) @Max(5)
    private int rating;
    private String text;
    private String imageUrl;
}
