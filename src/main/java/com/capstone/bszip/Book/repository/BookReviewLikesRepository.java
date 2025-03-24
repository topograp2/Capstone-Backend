package com.capstone.bszip.Book.repository;

import com.capstone.bszip.Book.domain.BookReview;
import com.capstone.bszip.Book.domain.BookReviewLikes;
import com.capstone.bszip.Member.domain.Member;
import io.lettuce.core.dynamic.annotation.Param;
import net.bytebuddy.asm.Advice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookReviewLikesRepository extends JpaRepository<BookReviewLikes, Long> {
    boolean existsBookReviewLikesByBookReviewAndMember(BookReview bookReview, Member member);
    Optional<BookReviewLikes> findBookReviewLikesByBookReviewAndMember(BookReview bookReview, Member member);

    //Page<BookReview> findAllByBookReview_CreatedAt(Pageable pageable);
    int countByBookReview(BookReview bookReview);

    @Query("SELECT br.bookReviewId, COUNT(brl) FROM BookReviewLikes brl JOIN brl.bookReview br GROUP BY br.bookReviewId")
    List<Object[]> countBookReviewLikeForAllReviews();

    int countByBookReview_BookReviewId(long bookReviewId);

    @Query("SELECT br1.bookReview.bookReviewId, COUNT(br1)"+
    "FROM BookReviewLikes br1 " +
    "WHERE br1.bookReview.createdAt >= :sevenDaysAgo " +
    "GROUP BY br1.bookReview.bookReviewId " +
    "ORDER BY COUNT (br1) DESC ")
    List<Object[]> countBookReviewLikeForLast7Days(@Param("sevenDaysAgo")LocalDateTime sevenDaysAgo);
}
