package com.capstone.bszip.Book.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@ToString(exclude = {"bookReviews", "pickedBooks"})
@Table(name="Books")
public class Book {
    @Id
    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "book_name", nullable = false)
    private String bookName;

    @Column(name = "publisher", nullable = false)
    private String publisher;

    @Column(name = "author", nullable = false)
    private List<String> authors = new ArrayList<>();

    @Column(name = "book_image_url", nullable = true)
    private String bookImageUrl;

    @Column(name = "book_content", nullable = true)
    private String content;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookReview> bookReviews = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PickedBook> pickedBooks = new ArrayList<>();

    public Book(Long bookId, String bookName, String publisher, List<String> authors, String bookImageUrl, String content) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.publisher = publisher;
        this.authors = authors;
        this.bookImageUrl = bookImageUrl;
        this.content = content;
    }
}
