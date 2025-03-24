package com.capstone.bszip.Book.service;

import com.capstone.bszip.Book.repository.BookReviewLikesRepository;
import com.capstone.bszip.Book.repository.BookReviewRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class RedisInitializer {
    private final RedisTemplate<String, Object> redisTemplate;
    private final BookReviewLikesRepository bookReviewLikesRepository;

    private static final String BOOK_REVIEW_LIKES_KEY = "book_review_likes:";
    private static final String LAST7DAYS_BOOK_REVIEW_LIKES_KEY = "last7days_book_review_likes:";
    private static final String LAST7DAYS_BOOK_REVIEW_ID_KEY = "last7days_book_review_id_";
    private static final int LAST_7DAYS_WEIGHT = 1000;
    private final BookReviewRepository bookReviewRepository;

    @PostConstruct
    public void init() {
        loadBookReviewLikes();
        getLikesForBookReviewFromLast7Days();
    }


    public void loadBookReviewLikes() {
        System.out.println("🔹 BookReviewLikes - RedisInitializer 실행 중...");

        List<Object[]> likeCounts = bookReviewRepository.getIdAndBookLikeAndCreatedAtFromAllBookReviews();
        for(Object[] row : likeCounts) {
            long reviewId = (Long) row[0];
            long likeCount = (long) row[1];
            double createdAt = ( (LocalDateTime ) row[2] ).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1_000_000_000_000_0.0;
            // Redis에 저장
            redisTemplate.opsForZSet().add(BOOK_REVIEW_LIKES_KEY, Long.toString(reviewId), likeCount + createdAt);
            // 콘솔 출력
            System.out.println("✅ Redis 저장: reviewId=" + reviewId + ", likeCount=" + likeCount);
        }

    }


    @Scheduled(cron="0 0 0 * * ?")
    public void getLikesForBookReviewFromLast7Days(){
        // 오늘(실시간)부터 7일 전의 생성된 좋아요 데이터를 가지고 좋아요 순으로 ZSet에 저장
        System.out.println("🥦 BookReviewFromLast7Days - redisinitializer 실행 중 ...");
        redisTemplate.opsForZSet().removeRangeByScore(LAST7DAYS_BOOK_REVIEW_LIKES_KEY, 0, -1);
        copy2Set(BOOK_REVIEW_LIKES_KEY, LAST7DAYS_BOOK_REVIEW_LIKES_KEY);
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Object []> likeCounts = bookReviewLikesRepository.countBookReviewLikeForLast7Days(sevenDaysAgo);
        for (Object[] row : likeCounts) {
            Long reviewId = (Long) row[0];
            Long likeCount = (Long) row[1];
            redisTemplate.opsForZSet().incrementScore(LAST7DAYS_BOOK_REVIEW_LIKES_KEY, reviewId.toString(), likeCount * LAST_7DAYS_WEIGHT);

            System.out.println("🥦 Redis 저장 : reviewId=" + reviewId + ", likeCount=" + likeCount);
        }
    }

    public void copy2Set(String fromkey, String tokey) {
        Set< ZSetOperations.TypedTuple<Object>> data = redisTemplate.opsForZSet().rangeWithScores(fromkey, 0, -1);

        if(data != null && !data.isEmpty()) {
            redisTemplate.opsForZSet().add(tokey, data);
        }
    }
}
