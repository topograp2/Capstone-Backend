package com.capstone.bszip.Book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SearchRequest {
    @NotBlank
    String searchType;
    @NotNull
    String searchWord;
}
