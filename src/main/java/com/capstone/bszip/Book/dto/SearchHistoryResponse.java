package com.capstone.bszip.Book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchHistoryResponse {
    List<?> searchHistory;
    boolean isEnd;
    int totalPages;
    Long totalElements;
}
