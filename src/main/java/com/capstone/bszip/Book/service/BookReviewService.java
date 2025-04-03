package com.capstone.bszip.Book.service;

import com.capstone.bszip.Book.domain.Book;
import com.capstone.bszip.Book.domain.BookReview;
import com.capstone.bszip.Book.domain.BookstoreBook;
import com.capstone.bszip.Book.dto.*;
import com.capstone.bszip.Book.repository.BookRepository;
import com.capstone.bszip.Book.repository.BookReviewLikesRepository;
import com.capstone.bszip.Book.repository.BookReviewRepository;
import com.capstone.bszip.Book.dto.BooksnapPreviewDto;
import com.capstone.bszip.Book.repository.BookstoreBookRepository;
import com.capstone.bszip.Bookstore.domain.Bookstore;
import com.capstone.bszip.Bookstore.repository.BookstoreRepository;
import com.capstone.bszip.Member.domain.Member;
import com.capstone.bszip.cloudinary.service.CloudinaryService;
import com.capstone.bszip.commonDto.exception.ConflictException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
public class BookReviewService {
    private final BookRepository bookRepository;
    private final BookReviewRepository bookReviewRepository;
    private final CloudinaryService  cloudinaryService;
    private final ObjectMapper objectMapper;
    private final BookReviewLikeService bookReviewLikeService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final BookReviewLikesRepository bookReviewLikesRepository;
    private final BookstoreBookRepository bookstoreBookRepository;
    private final BookstoreRepository bookstoreRepository;
    @Value("${kakao.client.id}")
    private String kakaoApiKey;
    private static final String BOOK_REVIEW_LIKES_KEY = "book_review_likes:";
    private static final String LAST7DAYS_BOOK_REVIEW_LIKES_KEY = "last7days_book_review_likes:";

    public BookReviewService(BookRepository bookRepository,
                             ObjectMapper objectMapper,
                             BookReviewRepository bookReviewRepository,
                             BookReviewLikeService bookReviewLikeService,
                             BookReviewLikesRepository bookReviewLikesRepository,
                             RedisTemplate<String, Object> redisTemplate,
                             BookstoreBookRepository bookstoreBookRepository,
                             BookstoreRepository bookstoreRepository,
                             CloudinaryService cloudinaryService) {
        this.bookRepository = bookRepository;
        this.objectMapper = objectMapper;
        this.bookReviewRepository = bookReviewRepository;
        this.bookReviewLikeService = bookReviewLikeService;
        this.bookReviewLikesRepository = bookReviewLikesRepository;
        this.redisTemplate = redisTemplate;
        this.bookstoreBookRepository = bookstoreBookRepository;
        this.bookstoreRepository = bookstoreRepository;
        this.cloudinaryService = cloudinaryService;
    }

    // kakao book api에서 책 제목로 검색된 책 정보 json 가져오기 -> 책제목 검색이랑 작가 검색이랑 너무 공통되는 부분이 많아서 걍 통일시켜야 될 거 같으다...
    public String searchBooksByTitle(String title, int page) throws Exception {
        try{
            String kakaoUri = "https://dapi.kakao.com/v3/search/book";
            // http 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            // 쿼리 파라미터 설정
            UriComponents uri = UriComponentsBuilder.fromHttpUrl(kakaoUri)
                    .queryParam("query", title)
                    .queryParam("target", "title")
                    .queryParam("page", page)
                    .queryParam("size", 12)
                    .build();

            // kakao api 책 검색
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    uri.toString(),
                    HttpMethod.GET,
                    entity,
                    String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            // 4xx 클라이언트 오류
            throw new Exception("클라이언트 오류: " + e.getStatusCode() + " - " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            // 5xx 서버 오류
            throw new Exception("서버 오류: " + e.getStatusCode() + " - " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            // 네트워크 오류 (타임아웃, 연결 불가 등)
            throw new Exception("네트워크 오류: " + e.getMessage(), e);
        } catch (RestClientException e) {
            // 기타 RestTemplate 관련 예외
            throw new Exception("API 요청 실패: " + e.getMessage(), e);
        }
    }

    public String searchBooksByAuthor(String author, int page) throws Exception {
        try{
            String kakaoUri = "https://dapi.kakao.com/v3/search/book";
            // http 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            // 쿼리 파라미터 설정
            UriComponents uri = UriComponentsBuilder.fromHttpUrl(kakaoUri)
                    .queryParam("query", author)
                    .queryParam("target", "person")
                    .queryParam("page", page)
                    .queryParam("size", 12)
                    .build();

            // kakao api 책 검색
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    uri.toString(),
                    HttpMethod.GET,
                    entity,
                    String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            // 4xx 클라이언트 오류
            throw new Exception("클라이언트 오류: " + e.getStatusCode() + " - " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            // 5xx 서버 오류
            throw new Exception("서버 오류: " + e.getStatusCode() + " - " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            // 네트워크 오류 (타임아웃, 연결 불가 등)
            throw new Exception("네트워크 오류: " + e.getMessage(), e);
        } catch (RestClientException e) {
            // 기타 RestTemplate 관련 예외
            throw new Exception("API 요청 실패: " + e.getMessage(), e);
        }
    }


    // 역직렬화 - Jackson
    public AddIsEndBookResponse convertToBookSearchResponse(String bookJson){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(bookJson);
            // 페이지가 끝인지 아닌지 확인하기 위해서...
            JsonNode meta = rootNode.get("meta");
            boolean is_end = meta.get("is_end").asBoolean(); // is_end가 true면 더 이상받을 검색 결과가 없닷
            JsonNode documents = rootNode.path("documents");

            List<BookSearchResponse> bookSearchResponses = new ArrayList<>();
            String title = "";
            for (JsonNode document : documents) {
                title = document.path("title").asText();
                List<String> authorsList = new ArrayList<>();

                for (JsonNode authorNode : document.path("authors")) {
                    authorsList.add(authorNode.asText());
                }

                String publisher = document.path("publisher").asText();
                String [] isbns = document.path("isbn").asText().split(" ");
                String isbn = isbns[1];
                String thumbnail = document.path("thumbnail").asText();

                bookSearchResponses.add(new BookSearchResponse(title, authorsList, publisher, isbn, thumbnail));
            }
            AddIsEndBookResponse addIsEndBookResponse = new AddIsEndBookResponse();
            addIsEndBookResponse.setBookData(bookSearchResponses);
            addIsEndBookResponse.setIsEnd(is_end);
            return addIsEndBookResponse;
        } catch (Exception e) {
            throw new RuntimeException("JSON 변환 오류: " + e.getMessage(), e);
        }
    }

    // isbn코드를 통해 db에 해당 책 있는지 판별하는 메서드
    public boolean existsByIsbn(Long isbn) {
        return bookRepository.existsByIsbn(isbn);
    }

    // isbn으로 책 찾기
    public String searchBookByIsbn(Long isbn){
        try{
            String kakaoUri = "https://dapi.kakao.com/v3/search/book";
            // http 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            // 쿼리 파라미터 설정
            UriComponents uri = UriComponentsBuilder.fromHttpUrl(kakaoUri)
                    .queryParam("query", isbn)
                    .queryParam("target", "isbn")
                    .queryParam("size", 1)
                    .build();

            // kakao api 책 검색
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    uri.toString(),
                    HttpMethod.GET,
                    entity,
                    String.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // 검색된 결과로 book 객체 만들고 저장
    public void saveBookByKakaoSearch(String bookJson){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(bookJson);
            JsonNode documents = rootNode.path("documents");

            String bookName = documents.get(0).path("title").asText();
            String [] isbns = documents.get(0).path("isbn").asText().split(" ");
            Long bookId = Long.parseLong(isbns[1]);
            String bookImageUrl = documents.get(0).path("thumbnail").asText();
            String publisher = documents.get(0).path("publisher").asText();
            String content = documents.get(0).path("contents").asText();

            List<String> authors = new ArrayList<>();
            for (JsonNode authorNode : documents.get(0).path("authors")) {
                authors.add(authorNode.asText());
            }

            Book book = Book.builder()
                    .isbn(bookId)
                    .bookName(bookName)
                    .publisher(publisher)
                    .authors(authors)
                    .content(content)
                    .bookImageUrl(bookImageUrl)
                    .bookType(BookType.normal)
                    .build();
            bookRepository.save(book);

        }catch (Exception e){
            throw new RuntimeException("북 객체 만들기 실패: "+e);
        }
    }

    // book 객체를 저장
    public void saveBook(Book book){
        try{
            bookRepository.save(book);
        }catch (Exception e){
            throw new RuntimeException("책 저장 실패: "+e);
        }

    }

    public Book getBookByIsbn(Long isbn){
        return bookRepository.findByIsbn(isbn).orElseThrow(() -> new IllegalArgumentException("책을 찾을 수 없습니다."));
    }


    public void saveBookReview(BookReview bookReview){
        try{
            bookReviewRepository.save(bookReview);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public BookReview getBookReviewById(Long bookReviewId) {
        return bookReviewRepository.findBookReviewByBookReviewId(bookReviewId).orElseThrow(()-> new EntityNotFoundException("BookReview not found : "+ bookReviewId));
    }

    public BookReview getBookReviewByIdAndMember(Long bookReviewId, Member member){
        return bookReviewRepository.findBookReviewsByBookReviewIdAndMember(bookReviewId, member).orElseThrow(()-> new EntityNotFoundException("BookReview not found : "+ bookReviewId));
    }

    public void deleteBookReview(BookReview bookReview){
        try{
            bookReviewRepository.delete(bookReview);
        }catch (Exception e){
            throw new RuntimeException("책 삭제 실패 : " +e);
        }
    }

    public void updateBookReview(Long bookReviewId, Member member, BookReviewUpdateDto bookReviewUpdateDto) {
        try{
            BookReview bookReview = bookReviewRepository.findBookReviewsByBookReviewIdAndMember(bookReviewId, member)
                    .orElseThrow(()-> new EntityNotFoundException("BookReview not found : "+ bookReviewId));
            int rating = bookReviewUpdateDto.getRating();
            String reviewText = bookReviewUpdateDto.getReviewText();
            bookReview = bookReview.toBuilder()
                    .bookRating(rating)
                    .bookReviewText(reviewText)
                    .build();

            bookReviewRepository.save(
                    bookReview
            );
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    public boolean existsBookReview(Long bookReviewId){
        return bookReviewRepository.existsById(bookReviewId);
    }

    public Page<BooksnapPreviewDto> getRecentReviews(Pageable pageable, Member member){
        return bookReviewRepository.findBookReviewsByCreatedAtDesc(pageable)
                .map(bookReview -> {
                    Boolean isLiked = null;
                    if(member != null){
                        isLiked = bookReviewLikesRepository.existsBookReviewLikesByBookReviewAndMember(bookReview, member);
                    }
                    Book book = bookReview.getBook();
                    List<BookStoreDto> bookStoreDtos = null;
                    if(book.getBookType().equals(BookType.indep) && !book.getBookstoreBookList().isEmpty()){
                        bookStoreDtos = book.getBookstoreBookList().stream().map(bookstoreBook ->
                                {
                                    Bookstore bookstore = bookstoreBook.getBookstore();
                                    String bookStoreName = bookstore.getName();
                                    Long bookStoreId = bookstore.getBookstoreId();
                                    return BookStoreDto.builder()
                                            .bookStoreId(bookStoreId)
                                            .bookStoreName(bookStoreName)
                                            .build();
                                }
                        ).toList();;
                    }
                    boolean isLikes = bookReviewLikesRepository.existsBookReviewLikesByBookReviewAndMember(bookReview, member);
                    BookInfoDto bookInfoDto = BookInfoDto.builder()
                            .bookId(book.getBookId().toString())
                            .title(book.getBookName())
                            .bookType(book.getBookType())
                            .bookImageUrl(book.getBookImageUrl())
                            .authors(book.getAuthors())
                            .bookStores(bookStoreDtos)
                            .publisher(book.getPublisher())
                            .build();

                    return BooksnapPreviewDto.builder()
                            .bookReviewId(bookReview.getBookReviewId())
                            .userName(bookReview.getMember().getNickname())
                            .createdAt(Timestamp.valueOf( bookReview.getCreatedAt() ) )
                            .like(bookReviewLikeService.getLikeCount(bookReview.getBookReviewId()))
                            .review(bookReview.getBookReviewText())
                            .isLiked(isLiked)
                            .rating(bookReview.getBookRating())
                            .bookInfo(bookInfoDto)
                            .build();
                        }

                );
    }

    public Page<BooksnapPreviewDto> getLikeTopReviews(Pageable pageable, Member member, ReviewSort sort){
        long start = (long) pageable.getPageNumber() * pageable.getPageSize();
        long end = start + pageable.getPageSize() - 1;
        String key = (sort.equals(ReviewSort.liketop)) ? BOOK_REVIEW_LIKES_KEY : LAST7DAYS_BOOK_REVIEW_LIKES_KEY;
        // Redis에서 좋아요 개수가 많은 리뷰 ID 목록 가져오기
        List<Long> topReviewIds = redisTemplate.opsForZSet()
                .reverseRange(Objects.requireNonNull(key), start, end)
                .stream()
                .map(obj -> Long.valueOf(obj.toString()))
                .toList();

        if (topReviewIds.isEmpty()) {
            return Page.empty();
        }

//        List<Long> allReviewIds = bookReviewRepository.findAllBookReviewIds();
//
//        // Redis에 없는 리뷰(좋아요 0개)를 추가
//        List<Long> mergedReviewIds = new ArrayList<>(new HashSet<>(allReviewIds)); // 중복 제거
//        mergedReviewIds.sort((id1, id2) -> {
//            int idx1 = topReviewIds.indexOf(id1);
//            int idx2 = topReviewIds.indexOf(id2);
//            if (idx1 == -1) idx1 = Integer.MAX_VALUE; // 좋아요 0개 리뷰는 마지막으로 정렬
//            if (idx2 == -1) idx2 = Integer.MAX_VALUE;
//            return Integer.compare(idx1, idx2);
//        });

        // DB에서 해당 리뷰 ID에 해당하는 리뷰 조회
        List<BookReview> reviews = bookReviewRepository.findBookReviewByBookReviewIdIn(topReviewIds);


        // 원래 정렬 유지 (Redis에서 가져온 순서대로)
        Map<Long, BookReview> reviewMap = reviews.stream()
                .collect(Collectors.toMap(BookReview::getBookReviewId, Function.identity()));

        List<BookReview> sortedReviews = topReviewIds.stream()
                .map(reviewMap::get)
                .filter(Objects::nonNull)
                .toList();

        // BookReview -> BooksnapPreviewDto 변환
        List<BooksnapPreviewDto> reviewDtos = sortedReviews.stream()
                .map(bookReview -> {
                    Boolean isLiked = null;
                    if (member != null) {
                        isLiked = bookReviewLikesRepository.existsBookReviewLikesByBookReviewAndMember(bookReview, member);
                    }
                    Book book = bookReview.getBook();
                    List<BookStoreDto> bookStoreDtos = null;
                    if(book.getBookType().equals(BookType.indep) && !book.getBookstoreBookList().isEmpty()){
                        bookStoreDtos = book.getBookstoreBookList().stream().map(bookstoreBook ->
                                {
                                    Bookstore bookstore = bookstoreBook.getBookstore();
                                    String bookStoreName = bookstore.getName();
                                    Long bookStoreId = bookstore.getBookstoreId();
                                    return BookStoreDto.builder()
                                            .bookStoreId(bookStoreId)
                                            .bookStoreName(bookStoreName)
                                            .build();
                                }
                                ).toList();;
                    }
                    BookInfoDto bookInfoDto = BookInfoDto.builder()
                            .bookId(book.getBookId().toString())
                            .title(book.getBookName())
                            .bookImageUrl(book.getBookImageUrl())
                            .authors(book.getAuthors())
                            .bookStores(bookStoreDtos)
                            .bookType(book.getBookType())
                            .publisher(book.getPublisher())
                            .build();

                    return BooksnapPreviewDto.builder()
                            .bookReviewId(bookReview.getBookReviewId())
                            .userName(bookReview.getMember().getNickname())
                            .createdAt(Timestamp.valueOf(bookReview.getCreatedAt()))
                            .like(bookReviewLikeService.getLikeCount(bookReview.getBookReviewId()))
                            .review(bookReview.getBookReviewText())
                            .isLiked(isLiked)
                            .rating(bookReview.getBookRating())
                            .bookInfo(bookInfoDto)
                            .build();
                })
                .toList();

        // 최종적으로 Page 객체로 변환하여 반환
        return new PageImpl<>(reviewDtos, pageable, redisTemplate.opsForZSet().size(key));

    }

    public Book findBookByBookId(Long bookId) {
            return bookRepository.findByBookId(bookId).orElseThrow(()-> new RuntimeException("Book not found"));
    }

    public void registerBookInBookstores(Book book, List<Long> bookstoreIds) {
        for(Long bookstoreId : bookstoreIds) {
            Bookstore bookstore = bookstoreRepository.findById(bookstoreId).orElseThrow(()-> new RuntimeException("Bookstore not found"));
            if(bookstoreBookRepository.existsByBookAndBookstore(book, bookstore)){
                throw new ConflictException(bookstore.getName()+ "은(는) 이미 추가되어 있는 서점입니다.");
            }
            BookstoreBook bookstoreBook = BookstoreBook.builder()
                    .book(book)
                    .bookstore(bookstore)
                    .build();
            bookstoreBookRepository.save(bookstoreBook);
        }
    }

    @Transactional
    public Book saveIndepBook(BookReviewRequest.BookCreate bookReviewRequest, MultipartFile thumbnail) {
        String title = bookReviewRequest.getTitle();
        String authorsString = bookReviewRequest.getAuthorsString();
        List<Long> bookstoreIds = bookReviewRequest.getBookstoreIds();
        String cloudinaryFolderName = "indep_books_image";
        List<String> authors = Arrays.asList(authorsString.split(",\\s*|/"));


        Book book = Book.builder()
                .bookType(BookType.indep)
                .bookName(title)
                .authors(authors)
                .bookImageUrl(cloudinaryService.uploadfile(thumbnail, cloudinaryFolderName))
                .build();

        bookRepository.save(book);
            // 서점 저장
        return book;
        }

}
