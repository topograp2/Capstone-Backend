package com.capstone.bszip.Book.service;

import com.capstone.bszip.Book.domain.BookReview;
import com.capstone.bszip.Book.domain.BookReviewLikes;
import com.capstone.bszip.Book.repository.BookReviewLikesRepository;
import com.capstone.bszip.Book.repository.BookReviewRepository;
import com.capstone.bszip.Member.domain.Member;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

@Slf4j
@Service
public class BookReviewLikeService {
    private final BookReviewLikesRepository bookReviewLikesRepository;
    private final BookReviewRepository bookReviewRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String BOOK_REVIEW_LIKES_KEY = "book_review_likes:";
    private static final String LAST7DAYS_BOOK_REVIEW_LIKES_KEY = "last7days_book_review_likes:";
    private static final int LAST_7DAYS_LIKE_WEIGHT = 1000;

    public BookReviewLikeService(BookReviewLikesRepository bookReviewLikesRepository, BookReviewRepository bookReviewRepository, RedisTemplate<String, Object> redisTemplate) {
        this.bookReviewLikesRepository = bookReviewLikesRepository;
        this.bookReviewRepository = bookReviewRepository;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public void saveLike(BookReviewLikes bookReviewLikes) {
        try{
            bookReviewLikesRepository.save(bookReviewLikes);
            BookReview bookReview = bookReviewLikes.getBookReview();
            redisTemplate.opsForZSet().incrementScore(BOOK_REVIEW_LIKES_KEY,
                    bookReview.getBookReviewId().toString(),
                    1);
            redisTemplate.opsForZSet().incrementScore(LAST7DAYS_BOOK_REVIEW_LIKES_KEY,
                    bookReview.getBookReviewId().toString(),
                    LAST_7DAYS_LIKE_WEIGHT);
        }catch (DataIntegrityViolationException e){
            throw new DataIntegrityViolationException("무결성 제약 조건 위반: ", e);
        } catch (Exception e){
            throw new RuntimeException("알수 없는 에러");
        }
    }


    public boolean isAleadyLiked(BookReview bookReview, Member member) {
        return bookReviewLikesRepository.existsBookReviewLikesByBookReviewAndMember(bookReview, member);
    }

    public BookReviewLikes getLike(BookReview bookReview, Member member) {
        return bookReviewLikesRepository.findBookReviewLikesByBookReviewAndMember(bookReview, member).orElseThrow(()-> new EntityNotFoundException("해당 리뷰와 멤버로 리뷰를 찾을 수 없음"));
    }

    @Transactional
    public void deleteLike(BookReviewLikes bookReviewLikes, boolean isLikeFrom7Days) {
        try{
            bookReviewLikesRepository.delete(bookReviewLikes);
            BookReview bookReview = bookReviewLikes.getBookReview();
            int score = isLikeFrom7Days ? LAST_7DAYS_LIKE_WEIGHT : 1;
            log.info("📍 좋아요 ID - "+bookReviewLikes.getId()+" - 좋아요 삭제 시 redis 반영되는 score : "+ score);
            redisTemplate.opsForZSet().incrementScore(BOOK_REVIEW_LIKES_KEY, bookReview.getBookReviewId().toString(), -1);
            redisTemplate.opsForZSet().incrementScore(LAST7DAYS_BOOK_REVIEW_LIKES_KEY,
                    bookReview.getBookReviewId().toString(),
                    -score);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    public int getLikeCount(Long reviewId) {
        Double score = redisTemplate.opsForZSet().score(BOOK_REVIEW_LIKES_KEY, reviewId.toString());
        if (score != null) {
            return score.intValue();
        }

        int likeCount = bookReviewLikesRepository.countByBookReview_BookReviewId(reviewId);
        redisTemplate.opsForZSet().add(BOOK_REVIEW_LIKES_KEY, reviewId.toString(), likeCount);

        return likeCount;
    }

    public boolean isLikedFromLast7Days(LocalDateTime bookReviewLikeCreatedAt) {
        LocalDateTime last7days = LocalDateTime.now().minusDays(7);
        LocalDateTime last7daysMidnight = LocalDateTime.of(last7days.getYear(), last7days.getMonth(), last7days.getDayOfMonth(), 0, 0);

        return bookReviewLikeCreatedAt.isAfter(last7daysMidnight);
    }


}
