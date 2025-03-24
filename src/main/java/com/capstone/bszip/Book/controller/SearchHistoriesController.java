package com.capstone.bszip.Book.controller;

import com.capstone.bszip.Book.dto.SearchHistoryResponse;
import com.capstone.bszip.Book.dto.SearchRequest;
import com.capstone.bszip.Book.service.SearchHistoriesSevice;
import com.capstone.bszip.Member.domain.Member;
import com.capstone.bszip.commonDto.ErrorResponse;
import com.capstone.bszip.commonDto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "검색 기록", description = "검색 기록 관련 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchHistoriesController {

    private final SearchHistoriesSevice searchHistoriesSevice;

    @Operation(
            summary = "검색어 저장",
            description = "검색 성공 후 검색어와 검색 타입을 저장합니다. 책리뷰 검색기록은 searchtype을 'booktitle'로 해주세요."
    )
    @ApiResponse(responseCode = "200", description = "검색어 저장 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    @PostMapping("/search-history")
    public ResponseEntity<?> storeSearchHistories(@AuthenticationPrincipal Member member,
                                                  @Valid @RequestBody SearchRequest searchRequest) {
        try{
            if(member == null){
                return ResponseEntity.status(401).body(
                        ErrorResponse.builder().status(401).result(false).message("로그인 후 이용해주세요.").build()
                );
            }
            if(searchRequest.getSearchWord() == null || searchRequest.getSearchWord().trim().isEmpty()){
                return ResponseEntity.status(400).body(
                        ErrorResponse.builder()
                                .status(400)
                                .result(false)
                                .message("검색어를 입력해야합니다.")
                                .build()
                );
            }
            String searchWord = searchRequest.getSearchWord().toUpperCase();
            String searchType = searchRequest.getSearchType().toUpperCase();

            if(!searchType.equals("BOOKTITLE") && !searchType.equals("AUTHOR")){
                return ResponseEntity.status(400).body(
                        ErrorResponse.builder().status(400).result(false).message("검색타입을 정확히 포함해주세요.").build()
                );
            }

            searchHistoriesSevice.storeSearchHistories(searchType, searchWord, member);
            return ResponseEntity.status(200).body(
                    SuccessResponse.builder().status(200).result(true)
                                    .message("성공적으로 검색어가 저장되었습니다🪱").build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    ErrorResponse.builder().result(false).status(500).message("검색어 저장 실패!").build()
            );
        }
    }

    @Operation(
            summary = "최근 검색어 불러오기",
            description = "검색 타입과 page, size를 받으면 검색기록 id와 검색어를 반환합니다.. 책리뷰 검색기록은 searchtype을 'booktitle'로 해주세요."
    )
    @ApiResponse(responseCode = "200", description = "검색어 불러오기 성공",
    content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = SuccessResponse.class),
            examples = @ExampleObject(value = """
                        {
                            "status": 200,
                            "result": true,
                            "message": "회원 검색 기록 불러오기",
                            "data": {
                                "isEnd": true,
                                "totalElements": 5,
                                "totalPages": 1,
                                "searchHistory": [
                                    {
                                        "id": 1,
                                        "searchWord": "모순"
                                    },
                                    {
                                        "id": 2,
                                        "searchWord": "사람은 무엇으로 사는가"
                                    }
                                ]
                            }
                        }
                        """)
    )
    )
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    @GetMapping("/search-history")
    public ResponseEntity<?> getSearchHistories(@AuthenticationPrincipal Member member,
                                                @RequestParam String searchtype,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        try{
            if(member == null){
                return ResponseEntity.status(401).body(
                        ErrorResponse.builder()
                                .message("유저의 검색 기록은 로그인 후 확인할 수 있습니다.")
                                .result(false)
                                .status(401)
                                .build()
                );
            }
            if(!searchtype.equals("booktitle") && !searchtype.equals("author")){
                return ResponseEntity.status(400).body(
                  ErrorResponse.builder()
                          .message("BOOKTITLE 혹은 AUTHOR로 서치 타입을 정확하게 작성해주세요.")
                          .result(false)
                          .status(400)
                          .build()
                );
            }

            SearchHistoryResponse searchHistoryResponses= searchHistoriesSevice.getSearchHistories(member, searchtype, page, size);
            return ResponseEntity.status(200).body(
                    SuccessResponse.builder().status(200).result(true)
                            .message("회원 검색 기록 불러오기")
                            .data(searchHistoryResponses)
                    .build()
            );
        }catch(Exception e){
            return ResponseEntity.status(500).body(
                    ErrorResponse.builder().result(false).status(500).message("저장된 최근 검색어 불러오기 실패!"+ e.getMessage()).build()
            );
        }
    }

}
