package com.capstone.bszip.Book.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Schema(description = "검색된 책 정보 dto")
public class BookSearchResponse {
    @Schema(description = "책 제목",example = "수레바퀴아래서")
    private String title;

    @Schema(description = "작가",example = " [ \"헤르만 헤세\", \"홍지형\"] ")
    private List<String> authors;

    @Schema(description = "출판사",example = "민음사")
    private String publisher;

    @Schema(description = "isbn")
    private String isbn;

    @Schema(description = "책 표지 url")
    private String bookImageUrl;

    @Override
    public String toString() {
        return "BookSearchResponse {" + "title='"
                + title + '\'' +
                ", authors=" + authors +
                ", publisher='" + publisher + '\'' +
                ", isbn='" + isbn + '\'' +
                ", bookImageUrl='" + bookImageUrl + "'}";
    }
}
