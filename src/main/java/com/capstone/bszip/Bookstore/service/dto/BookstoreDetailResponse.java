package com.capstone.bszip.Bookstore.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BookstoreDetailResponse {
    private Long bookstoreId;
    private String name;
    private String phone;
    private String hours;
    private double rating;
    private String keyword;
    private String address;
    private String description;
    private boolean liked;
}
