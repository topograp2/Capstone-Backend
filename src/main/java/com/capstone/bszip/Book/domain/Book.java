package com.capstone.bszip.Book.domain;

import com.capstone.bszip.Book.dto.BookType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@ToString(exclude = {"bookReviews", "pickedBooks", "bookstoreBookList"})
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Table(name="Books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "isbn")
    private Long isbn;

    @Column(name = "book_name", nullable = false)
    private String bookName;

    @Column(name = "publisher", nullable = true)
    private String publisher;

    @Column(name = "author", nullable = true)
    @ElementCollection
    @CollectionTable(name = "book_authors", joinColumns = @JoinColumn(name = "book_id"))
    private List<String> authors = new ArrayList<>();

    @Column(name = "book_image_url", nullable = true)
    private String bookImageUrl;

    @Column(name = "book_content", nullable = true)
    private String content;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookReview> bookReviews = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PickedBook> pickedBooks = new ArrayList<>();


    @Column(name = "book_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookType bookType;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookstoreBook> bookstoreBookList = new ArrayList<>();

}
