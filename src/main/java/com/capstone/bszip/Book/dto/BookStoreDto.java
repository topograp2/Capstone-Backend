package com.capstone.bszip.Book.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookStoreDto {
    private String bookStoreName;
    private Long bookStoreId;
}
