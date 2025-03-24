package com.capstone.bszip.Book.domain;

import com.capstone.bszip.Book.dto.SearchType;
import com.capstone.bszip.Member.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_histories")
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SearchHistories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name="search_word")
    private String searchWord;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name="search_type")
    private SearchType searchType;

    @CreatedDate
    @Column(nullable = false, name="search_date")
    private LocalDateTime searchDate;

    @ManyToOne
    @JoinColumn(name = "member_id")
    Member member;
}
