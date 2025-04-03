package com.capstone.bszip.Book.controller;

import com.capstone.bszip.Book.domain.Book;
import com.capstone.bszip.Book.domain.BookReview;
import com.capstone.bszip.Book.dto.*;
import com.capstone.bszip.Book.dto.BooksnapPreviewDto;
import com.capstone.bszip.Book.service.BookReviewService;
import com.capstone.bszip.Book.service.IndepBookService;
import com.capstone.bszip.Member.domain.Member;
import com.capstone.bszip.commonDto.ErrorResponse;
import com.capstone.bszip.commonDto.SuccessResponse;
import com.capstone.bszip.commonDto.exception.ConflictException;
import com.capstone.bszip.commonDto.exception.IOException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/booksnap")
@RequiredArgsConstructor
@Tag(name="Book Review", description = "Booksnap 기능에서 한 줄 리뷰 관련 api")
public class BookReviewController {

    private final BookReviewService bookReviewService;
    private final IndepBookService indepBookService;
    /*
    * 책제목과 작가를 입력하여 도서 검색 api
    * - 책 ID, 이미지 url, 책 제목, 작가,출판사 제공*/
    @GetMapping("/book-search")
    @Operation(summary = "책 검색", description = "책 제목 혹은 작가를 입력하여 책제목, 작가, 출판사, isbn, 책 표지 url을 검색하여 볼러옵니다.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "검색 성공", content = @Content(schema = @Schema(implementation = BookSearchResponse.class),
        examples = {@ExampleObject(
                name = "Success example : 모순 검색 시",
                value = """
                        {
                            "result": true,
                            "status": 200,
                            "message": "검색 성공",
                            "data": [
                                {
                                    "title": "모순",
                                    "authors": [
                                        "양귀자"
                                    ],
                                    "publisher": "쓰다",
                                    "isbn": "8998441012",
                                    "bookImageUrl": "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1500252%3Ftimestamp%3D20240802114042"
                                },
                                {
                                    "title": "모순(리커버:K)",
                                    "authors": [
                                        "양귀자"
                                    ],
                                    "publisher": "쓰다",
                                    "isbn": "8998441101",
                                    "bookImageUrl": "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F5697894%3Ftimestamp%3D20220410173043"
                                }\
                        ]\
                        }"""
        )})),})
    public ResponseEntity<?> searchBook(@RequestParam BookType booktype, @RequestParam BookSearchType searchtype, @RequestParam String query, @RequestParam(required = false, defaultValue = "1")int page) {
        try{
            if(query == null || query.trim().isEmpty()){
                return ResponseEntity.status(400).body(
                        ErrorResponse.builder()
                                .status(400)
                                .message("E01: 검색어를 입력해주세요.")
                                .detail("E01")
                                .build()
                );
            }
            String bookJson = null;
            if(booktype.equals(BookType.normal)){
                if(searchtype.equals(BookSearchType.title)){
                    bookJson = bookReviewService.searchBooksByTitle(query, page);
                }
                if(searchtype.equals(BookSearchType.author)){
                    bookJson = bookReviewService.searchBooksByAuthor(query, page);
                }
            }
            if(booktype.equals(BookType.indep)){
                if(searchtype.equals(BookSearchType.title)){
                    AddIsEndBookResponse addIsEndBookResponse = indepBookService.getIndepBookByBooktitle(query, page);
                    if(addIsEndBookResponse.getBookData().isEmpty()){
                        return ResponseEntity.status(404).body(
                                ErrorResponse.builder()
                                        .status(404)
                                        .message("E02: 해당되는 도서를 찾을 수 없습니다.")
                                        .detail("E02")
                                        .build()
                        );
                    }
                    return ResponseEntity.ok(
                            SuccessResponse.builder()
                                    .status(200)
                                    .data(addIsEndBookResponse)
                                    .message("독립출판물 검색 성공")
                            .build()
                    );
                }
                if(searchtype.equals(BookSearchType.author)){
                    AddIsEndBookResponse addIsEndBookResponse = indepBookService.getIndepBookByAuthor(query, page);
                    if(addIsEndBookResponse.getBookData().isEmpty()){
                        return ResponseEntity.status(404).body(
                                ErrorResponse.builder()
                                        .status(404)
                                        .message("E02: 해당되는 도서를 찾을 수 없습니다.")
                                        .detail("E02")
                                        .build()
                        );
                    }
                    return ResponseEntity.ok(
                            SuccessResponse.builder()
                                    .status(200)
                                    .data(addIsEndBookResponse)
                                    .message("독립출판물 검색 성공")
                                    .build()
                    );
                }
            }

            if(bookJson == null){
                return ResponseEntity.noContent().build();
            }
            AddIsEndBookResponse addIsEndBookResponse = bookReviewService.convertToBookSearchResponse(bookJson);
            return ResponseEntity.ok(
                    SuccessResponse.builder()
                            .result(true)
                            .status(HttpServletResponse.SC_OK)
                            .data(addIsEndBookResponse) // 현재 페이지가 끝인지 확인할 수 있는 것도 추가해야 될듯..!
                            .message("검색 성공")
                            .build()
            );
        } catch (NullPointerException e){
            return ResponseEntity.status(400).body(
                    ErrorResponse.builder()
                            .result(false)
                            .status(HttpServletResponse.SC_BAD_REQUEST).message("검색할 사항을 입력해주세요")
                    .build()
            );
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /*
    * 리뷰 작성 api
    * - 책 ID, 별점, 리뷰 받아서 저장*/
    @Operation(summary = "책 한 줄 리뷰 등록", description = "쿼리 파라미터에 따라서 request body가 다릅니다. 아래의 request body는 booktype이 normal일 때고, booktype이 indep일 때는 https://turquoise-dill-eee.notion.site/1c82ab1fb8c9807293a6e760423e5f63?pvs=4 여기서 확인 하삼")
    @PostMapping("/new-review")
    public ResponseEntity<?> writeBookReview(@AuthenticationPrincipal Member member,
                                             @RequestBody BookReviewRequest.ReviewCreate request,
                                             @RequestParam BookType booktype) {
        try{
            if(member == null){
                return ResponseEntity.status(401).body(
                        ErrorResponse.builder()
                        .result(false)
                        .status(HttpServletResponse.SC_UNAUTHORIZED)
                                .message("인증되지 않은 사용자입니다.")
                                .build()
                );
            }

            Book book = null;
            String reviewText = null;
            int rating = 0;
            // booktype이 normal일 때 - isbn을 가지고 등록해야 함
            if(booktype.equals(BookType.normal)){
                Long isbn = request.getIsbn();
                reviewText = request.getReviewText();
                rating = request.getRating();
                if(!bookReviewService.existsByIsbn(isbn)){
                    String bookJson = bookReviewService.searchBookByIsbn(isbn);
                    bookReviewService.saveBookByKakaoSearch(bookJson);
                }
                book = bookReviewService.getBookByIsbn(isbn);
            }

            // booktype이 indep일 때 - book id를 가지고 등록해야 함
            if(booktype.equals(BookType.indep)){
                Long bookId = request.getBookId();
                reviewText = request.getReviewText();
                rating = request.getRating();
                book = bookReviewService.findBookByBookId(bookId);
                List<Long> bookstoreIds = request.getBookstoreIds();
                if(bookstoreIds != null && !bookstoreIds.isEmpty()){
                    // 서비스에서 bookstoreId랑 book을 이용해서 bookstoreBook에 저장하는 로직 - bookstoreBook에서 해당 책에 이미 서점이 있으면 409 에러 코드 주기
                    bookReviewService.registerBookInBookstores(book, bookstoreIds);
                }
            }

            bookReviewService.saveBookReview(
                    BookReview.builder()
                            .bookReviewText(reviewText)
                            .book(book)
                            .member(member)
                            .bookRating(rating)
                            .build()
            );
            return ResponseEntity.ok(
                    SuccessResponse.builder()
                            .result(true)
                            .status(HttpServletResponse.SC_OK)
                            .data(null)
                            .message("리뷰 저장 성공")
                            .build()
            );
        }catch (NullPointerException e){
            return ResponseEntity.status(400).body(
                    ErrorResponse.builder()
                    .result(false)
                            .status(400)
                            .message("누락된 값 존재")
                            .build()
            );
        }catch (ConflictException e){
            return ResponseEntity.status(409).body(
                    ErrorResponse.builder()
                            .result(false)
                            .status(409)
                            .message(e.getMessage())
                            .build()
            );
        } catch (RuntimeException e){
            return ResponseEntity.status(404).body(
                    ErrorResponse.builder()
                            .result(false)
                            .status(404)
                            .message(e.getMessage())
                            .build()
            );
        }
        catch (Exception e){
            return ResponseEntity.status(500).body(
                    ErrorResponse.builder()
                            .result(false)
                            .status(500)
                            .message("Internal Server Error")
                            .detail(e.getMessage())
                            .build()
            );
        }
    }
    @PostMapping(value = "/indep-book",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @Operation(summary = "독립출판물과 리뷰 동시에 등록", description = "thumnail 이미지와 책 이름, 작가문자열, 별점, 책에 대한 리뷰 텍스트를 받아와서 책과 책 리뷰를 등록합니다.")
    public ResponseEntity<?> createIndepBookAndReview(@RequestPart(value = "thumbnail", required = false)
                                                          @Parameter(description = "책 썸네일 이미지")  MultipartFile image,
                                                      @RequestPart("review")
                                                      @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                              content = @Content(
                                                                      mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                                      schema = @Schema(implementation = BookReviewRequest.BookCreate.class)
                                                              )
                                                      )BookReviewRequest.BookCreate bookReviewRequest,
                                                      @AuthenticationPrincipal Member member
                                                      ) throws IOException {
        try{
            if(member == null){
                return ResponseEntity.status(401).body(
                        ErrorResponse.builder()
                                .result(false)
                                .status(HttpServletResponse.SC_UNAUTHORIZED)
                                .message("로그인 후 이용할 수 있습니다.")
                                .build()
                );
            }
            Book book = bookReviewService.saveIndepBook(bookReviewRequest, image);

            Long bookId = book.getBookId();
            book = bookReviewService.findBookByBookId(bookId);
            List<Long> bookstoreIds = bookReviewRequest.getBookstoreIds();
            if(bookstoreIds != null && !bookstoreIds.isEmpty()){
                // 서비스에서 bookstoreId랑 book을 이용해서 bookstoreBook에 저장하는 로직 - bookstoreBook에서 해당 책에 이미 서점이 있으면 409 에러 코드 주기
                bookReviewService.registerBookInBookstores(book, bookstoreIds);
            }
            bookReviewService.saveBookReview(
                    BookReview.builder()
                            .bookReviewText(bookReviewRequest.getReviewText())
                            .book(book)
                            .member(member)
                            .bookRating(bookReviewRequest.getRating())
                            .build()
            );
            return ResponseEntity.status(201).body("리뷰 생성 완료");
        }catch (Exception e){
            return ResponseEntity.status(500).body(
                    ErrorResponse.builder()
                            .status(500)
                            .result(false)
                            .message("Internal Server Error")
                            .detail(e.getMessage())
                            .build()
            );
        }
    }

    /*
    * 책 리뷰 삭제 api
    * 로그인한 회원이 작성한 리뷰 id 받아서 삭제
    * */
    @Operation(summary = "책 한 줄 리뷰 삭제", description = "[로그인 필수] 로그인한 사용자가 작성한 책 리뷰의 id를 받아와서 삭제")
    @DeleteMapping("/reviews/{bookReviewId}")
    @Transactional
    public ResponseEntity<?> deleteBookReview(@AuthenticationPrincipal Member member, @PathVariable Long bookReviewId) {
        try{
            if(member == null){
                return ResponseEntity.status(401).body(
                        ErrorResponse.builder()
                                .result(false)
                                .status(HttpServletResponse.SC_UNAUTHORIZED)
                                .message("인증되지 않은 사용자입니다.")
                                .build()
                );
            }
            BookReview bookReview = bookReviewService.getBookReviewByIdAndMember(bookReviewId, member);
            if(bookReview == null){
                return ResponseEntity.status(404).body(
                        ErrorResponse.builder()
                        .result(false)
                        .status(HttpServletResponse.SC_NOT_FOUND)
                                .message("찾을 수 없는 리뷰입니다")
                        .build()
                );
            }
            bookReviewService.deleteBookReview(bookReview);
            return ResponseEntity.ok(
                    SuccessResponse.builder()
                            .result(true)
                            .status(HttpServletResponse.SC_OK)
                            .message("책 한 줄 리뷰 삭제 성공")
                            .build()
            );
        }catch (NullPointerException e){
            return ResponseEntity.status(400).body(
                    ErrorResponse.builder()
                            .result(false)
                            .status(400)
                            .message("누락된 값 존재")
                            .build()
            );
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
    * 책 리뷰 수정 api
    * 로그인한 회원이 작성한 리뷰 id 받아서 수정
    * */
    @Operation(summary = "책 한 줄 리뷰 수정", description = "[로그인 필수] 로그인한 사용자가 작성한 책 리뷰의 id를 받아와서 수정")
    @PutMapping("/reviews/{bookReviewId}")
    public ResponseEntity<?> updateBookReview(@AuthenticationPrincipal Member member, @PathVariable Long bookReviewId, @RequestBody BookReviewUpdateDto bookReviewUpdateDto) {
        try{
            if(member == null){
                return ResponseEntity.status(401).body(
                        ErrorResponse.builder()
                                .result(false)
                                .status(HttpServletResponse.SC_UNAUTHORIZED)
                                .message("인증되지 않은 사용자입니다.")
                                .build()
                );
            }
            if(bookReviewService.existsBookReview(bookReviewId)){
                return ResponseEntity.status(404).body(
                        ErrorResponse.builder()
                                .result(false)
                        .status(HttpServletResponse.SC_NOT_FOUND)
                                .message("찾을 수 없는 책 리뷰 입니다")
                                .build()
                );
            }
            bookReviewService.updateBookReview(bookReviewId, member, bookReviewUpdateDto);
            return ResponseEntity.ok(
                    SuccessResponse.builder()
                            .result(true)
                            .status(HttpServletResponse.SC_OK)
                            .message("책 한 줄 리뷰 수정 성공")
                            .build()
            );

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
    * 리뷰 보이기 api
    * - 최신순, 좋아요, 요즘 인기 있는
   */
    @Operation(summary = "리뷰 보여주기", description = """
            **page와 size만** 입력하세요! sort는 없애고 확인해주세요🥲
            [로그인 시] isLiked가 포함되어 로그인한 해당 회원이 좋아요를 눌렀는지 누르지 않았는지를 가져옵니다.
            [공통] last 값으로 헌재 페이지가 끝인지 확인 가능
            """)
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "로그인 후 결과입니다. 로그인 하지 않으면 isLiked는 안 옴", content = @Content(schema = @Schema(implementation = BookSearchResponse.class),
            examples = {@ExampleObject(
                    name = "Success example : 로그인 시",
                    value = "{\n" +
                            "  \"result\": true,\n" +
                            "  \"status\": 200,\n" +
                            "  \"message\": \"최신순 리뷰 \",\n" +
                            "  \"data\": {\n" +
                            "    \"booksnapPreview\": [\n" +
                            "      {\n" +
                            "        \"userName\": \"NerdyBook\",\n" +
                            "        \"createdAt\": \"2025-02-24T10:27:54.146+00:00\",\n" +
                            "        \"like\": \"0\",\n" +
                            "        \"review\": \"자살 ㄴㄴ\",\n" +
                            "        \"isLiked\": false,\n" +
                            "        \"rating\": 4,\n" +
                            "        \"bookInfo\": {\n" +
                            "          \"isbn\": \"9788932912240\",\n" +
                            "          \"title\": \"자살 클럽\",\n" +
                            "          \"bookImageUrl\": \"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F506787%3Ftimestamp%3D20231021152949\",\n" +
                            "          \"authors\": [\n" +
                            "            \"로버트 루이스 스티븐슨\"\n" +
                            "          ],\n" +
                            "          \"publisher\": \"열린책들\"\n" +
                            "        }\n" +
                            "      }\n" +
                            "    ],\n" +
                            "    \"totalPages\": 1,\n" +
                            "    \"last\": true,\n" +
                            "    \"totalElements\": 1\n" +
                            "  }\n" +
                            "}"
            )})),})
    @GetMapping("/reviews")
    public ResponseEntity<?> getRecentReviews(@RequestParam(required = true) ReviewSort sort,
                                              @PageableDefault(size = 10,sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                              @AuthenticationPrincipal Member member) {
        try{
            Page<BooksnapPreviewDto> bookReviews = null;
            if(sort.equals(ReviewSort.createdAt)){
                bookReviews = bookReviewService.getRecentReviews(pageable, member);
            } else if(sort.equals(ReviewSort.liketop) || sort.equals(ReviewSort.trend)){
                bookReviews = bookReviewService.getLikeTopReviews(pageable, member, sort);
            }

            BooksnapPreviewResponse booksnapPreviewResponse = BooksnapPreviewResponse.builder()
                    .booksnapPreview(bookReviews.getContent())
                    .last(bookReviews.isLast())
                    .totalPages(bookReviews.getTotalPages())
                    .totalElements(bookReviews.getTotalElements())
                    .build();
            return ResponseEntity.ok(
                    SuccessResponse.builder()
                            .result(true)
                            .status(HttpServletResponse.SC_OK)
                            .message(sort + " 기준 리뷰 🥐")
                            .data(booksnapPreviewResponse)
                            .build()
            );
        }catch (NullPointerException e){
            return ResponseEntity.status(400).body(
                    ErrorResponse.builder()
                    .result(false)
                    .status(400)
                            .message("입력이 잘 못된 값 존재")
                    .build()
            );
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
