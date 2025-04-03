package com.capstone.bszip.Book.repository;

import com.capstone.bszip.Book.domain.Book;
import com.capstone.bszip.Book.dto.BookType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(Long isbn);
    Optional<Book> findByIsbn(Long isbn);
    Optional<Book> findByBookId(Long bookId);
    Page<Book> findByBookNameContainingAndBookType(String bookName, BookType bookType, Pageable pageable);
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a LIKE %:author% AND b.bookType = :bookType")
    Page<Book> findByAuthorsContainingAndBookType(@Param("author") String author, @Param("bookType") BookType bookType, Pageable pageable);

}
