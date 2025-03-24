package com.capstone.bszip.Book.domain;

import com.capstone.bszip.Member.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class BookReviewLikes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name="bookreview_id")
    private BookReview bookReview;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;

    // 정적 팩토리 메서드로 객체 생성
    public static BookReviewLikes create(BookReview bookReview, Member member) {
        BookReviewLikes bookReviewLikes = new BookReviewLikes();
        bookReviewLikes.bookReview = bookReview;
        bookReviewLikes.member = member;
        return bookReviewLikes;
    }
}
