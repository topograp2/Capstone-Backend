package com.capstone.bszip.Book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "끝까지 포함된 책 data 인지 확인 + 기존 책 response 추가한 DTO")
public class AddIsEndBookResponse {

    @Schema(description = "다 검색된 건지 안된 건지 알려줌",example = "true")
    private Boolean isEnd;

    @Schema(description = "기존 책 response")
    private List<BookSearchResponse> bookData;
}
