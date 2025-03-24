package com.capstone.bszip.Book.controller;

import com.capstone.bszip.Book.domain.BookReview;
import com.capstone.bszip.Book.domain.BookReviewLikes;
import com.capstone.bszip.Book.dto.BookReviewLikeRequest;
import com.capstone.bszip.Book.service.BookReviewLikeService;
import com.capstone.bszip.Book.service.BookReviewService;
import com.capstone.bszip.Member.domain.Member;
import com.capstone.bszip.commonDto.ErrorResponse;
import com.capstone.bszip.commonDto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name="Book Review Like", description = "책 한 줄 리뷰의 좋아요 관련 기능 api")
@RequestMapping("/api")
public class BookReviewLikeController {
    private final BookReviewLikeService bookReviewLikeService;
    private final BookReviewService bookReviewService;

    public BookReviewLikeController(BookReviewLikeService bookReviewLikeService, BookReviewService bookReviewService) {
        this.bookReviewLikeService = bookReviewLikeService;
        this.bookReviewService = bookReviewService;
    }

    /*
    * 책 리뷰에 좋아요 누르기 (로그인 필수)
    * */
    @Operation(summary = "책 한 줄 리뷰에 좋아요", description = "[로그인 필수] 책 리뷰 아이디를 넘겨주어야 합니다!")
    @PostMapping("/booksnap/like")
    public ResponseEntity<?> likeBookReview(@AuthenticationPrincipal Member member, @RequestBody BookReviewLikeRequest bookReviewLikeRequest){

        try{
            // 멤버 가지고 오기
            if(member == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        ErrorResponse.builder()
                                .result(false)
                                .status(401)
                                .message("인증되지 않은 사용자입니다.")
                                .build()
                );
            }
            // 해당 책 한 줄 리뷰 가지고 오기
            Long bookReviewId = bookReviewLikeRequest.getBookReviewId();
            BookReview bookReview = bookReviewService.getBookReviewById(bookReviewId);
            if(bookReview == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ErrorResponse.builder()
                                .result(false)
                                .message("해당되는 리뷰를 찾을 수 없습니다.")
                                .build()
                );
            }

            if(bookReviewLikeService.isAleadyLiked(bookReview, member)){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        ErrorResponse.builder()
                                        .result(false)
                                        .message(member.getNickname()+"님이 이미 좋아요하셨습니다...😅")
                                        .build()

                );
            }
            // 좋아요 객체 만들기
            BookReviewLikes bookReviewLikes = BookReviewLikes.create(bookReview, member);
            // 리뷰 저장하기
            bookReviewLikeService.saveLike(bookReviewLikes);
            return ResponseEntity.ok(
                    SuccessResponse.builder()
                            .result(true)
                            .status(HttpServletResponse.SC_OK)
                            .data(null)
                            .message(member.getNickname() + "의 좋아요 완료")
                            .build()
            );
        } catch (NullPointerException e){
            return ResponseEntity.status(400).body(
                    ErrorResponse.builder()
                            .result(false)
                            .message("누락된 값 존재")
                            .detail(e.getMessage())
                    .build()
            );
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "책 한 줄 리뷰에 좋아요 취소", description = "[로그인 필수] 책 리뷰 아이디를 넘겨주어야 합니다!")
    @DeleteMapping("/booksnap/unlike")
    public ResponseEntity<?> unlikeBookReview(@AuthenticationPrincipal Member member, @RequestBody BookReviewLikeRequest bookReviewLikeRequest){
        try{
            // 멤버 객체랑 북 리뷰 객체 가져오기
            if(member == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        ErrorResponse.builder()
                                .result(false)
                                .status(401)
                                .message("인증되지 않은 사용자입니다.")
                                .build()
                );
            }
            Long bookReviewId = bookReviewLikeRequest.getBookReviewId();
            BookReview bookReview = bookReviewService.getBookReviewById(bookReviewId);
            // 좋아요 누른 적이 없는데 삭제하려고 하는 경우
            if(!bookReviewLikeService.isAleadyLiked(bookReview, member)){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        ErrorResponse.builder()
                                .result(false)
                                .message(member.getNickname()+"님이 좋아요 한 적이 없는 리뷰입니다...😅")
                                .build()

                );
            }
            // 북 리뷰랑 멤버로 좋아요 객체 가져옴
            BookReviewLikes bookReviewLikes = bookReviewLikeService.getLike(bookReview, member);
            // 해당 좋아요 객체 삭제
            boolean isLikedFromlast7days = bookReviewLikeService.isLikedFromLast7Days(bookReviewLikes.getCreatedAt());
            bookReviewLikeService.deleteLike(bookReviewLikes, isLikedFromlast7days);
            return ResponseEntity.ok(
                    SuccessResponse.builder()
                    .result(true)
                    .status(HttpServletResponse.SC_OK)
                    .data(null)
                    .message("리뷰 삭제 성공😊")
                    .build()
            );
        } catch (NullPointerException e){
            return ResponseEntity.status(400).body(
                    ErrorResponse.builder()
                            .result(false)
                            .message("누락된 값 존재")
                            .detail(e.getMessage())
                            .build()
            );
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
