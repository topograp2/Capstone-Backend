package com.capstone.bszip.Book.repository;

import com.capstone.bszip.Book.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByBookId(Long bookId);
    Optional<Book> findByBookId(Long bookId);
}
