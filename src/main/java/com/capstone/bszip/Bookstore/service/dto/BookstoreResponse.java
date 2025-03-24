package com.capstone.bszip.Bookstore.service.dto;

import com.capstone.bszip.Bookstore.domain.BookstoreCategory;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookstoreResponse {
    private Long bookstoreId;
    private String name;
    private double rating;
    //private BookstoreCategory bookstoreCategory;
    private String keyword;
    private String address;
    private boolean liked;

}
