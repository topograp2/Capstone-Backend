package com.capstone.bszip.Bookstore.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class BookstoreDetailWithReviews {
    private BookstoreDetailResponse bookstoreDetail;
    private List<BookstoreReviewResponse> reviewList;
}