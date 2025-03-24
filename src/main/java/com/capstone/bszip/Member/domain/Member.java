package com.capstone.bszip.Member.domain;

import com.capstone.bszip.Book.domain.BookReview;
import com.capstone.bszip.Book.domain.PickedBook;
import com.capstone.bszip.Book.domain.SearchHistories;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = {"bookReviews", "bookReviewLikes", "pickedBooks", "searchHistoriesList"})
@Table(name = "members") // 테이블 이름 매핑
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "join_type",nullable = false)
    private MemberJoinType memberJoinType;

    @Column(name = "temp_password", nullable = false)
    private int tempPassword; // 0이면 사용자가 입력한 비밀번호고, 1이면 임시비밀번호임

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    //권한 설정
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="member_roles",joinColumns = @JoinColumn(name = "member_id"))
    @Column(name="role")
    private Set<String> roles;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookReview> bookReviews = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookReview> bookReviewLikes = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PickedBook> pickedBooks = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SearchHistories> searchHistoriesList = new ArrayList<>();
}