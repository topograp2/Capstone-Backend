package com.capstone.bszip.Book.repository;

import com.capstone.bszip.Book.domain.BookReview;
import com.capstone.bszip.Member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookReviewRepository extends JpaRepository<BookReview, Long> {
    Optional<BookReview> findBookReviewByBookReviewId(Long bookReviewId);
    Optional<BookReview> findBookReviewsByBookReviewIdAndMember(Long bookReviewId, Member member);
    boolean existsBookReviewByBookReviewId(Long bookReviewId);

    @Query("SELECT br FROM BookReview br ORDER BY br.createdAt DESC")
    Page<BookReview> findBookReviewsByCreatedAtDesc(Pageable pageable);

    List<BookReview> findBookReviewByBookReviewIdIn(Collection<Long> bookReviewIds);

    @Query("SELECT br.bookReviewId FROM BookReview br")
    List<Long> findAllBookReviewIds();

    @Query("SELECT br.bookReviewId as id, COUNT(bl) as likeCount, br.createdAt FROM BookReview br " +
            "LEFT JOIN br.bookReviewLikesList bl GROUP BY br.bookReviewId")
    List<Object[]> getIdAndBookLikeAndCreatedAtFromAllBookReviews();
}
