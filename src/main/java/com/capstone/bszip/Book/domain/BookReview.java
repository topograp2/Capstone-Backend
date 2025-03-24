package com.capstone.bszip.Book.domain;

import com.capstone.bszip.Member.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"bookReviewLikesList"})
@Table(name="BookReview")
public class BookReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="bookreview_id", unique = true, nullable = false)
    private Long bookReviewId;

    @Column(name ="bookreview_text")
    private String bookReviewText;

    @Column(name ="book_rating")
    private int bookRating;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "bookReview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookReviewLikes> bookReviewLikesList = new ArrayList<>();

    public BookReview(String bookReviewText, int bookRating, Book book, Member member) {
        this.bookReviewText = bookReviewText;
        this.bookRating = bookRating;
        this.book = book;
        this.member = member;

    }
}
