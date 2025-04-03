package com.capstone.bszip.Bookstore.repository;

import com.capstone.bszip.Bookstore.domain.BookstoreReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookstoreReviewRepository extends JpaRepository<BookstoreReview, Long> {
    @Query("SELECT r FROM BookstoreReview r " +
            "WHERE r.bookstore.bookstoreId = :bookstoreId " +
            "ORDER BY r.createdAt DESC")
    List<BookstoreReview> findReviewsByBookstoreIdOrderByCreatedAtDesc(@Param("bookstoreId") Long bookstoreId);
}
