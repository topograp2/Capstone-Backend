package com.capstone.bszip.Bookstore.domain;

import com.capstone.bszip.Book.domain.Book;
import com.capstone.bszip.Member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name="bookstore_reviews")
@NoArgsConstructor
@AllArgsConstructor
public class BookstoreReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="bookstorereview_id")
    private Long bookstoreReviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookstore_id",referencedColumnName = "bookstore_id", nullable = false)
    private Bookstore bookstore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

    @Column(name="rating")
    private double rating;

    @Column(name="text")
    private String text;

    @Column(name="image_url")
    private String imageUrl;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
