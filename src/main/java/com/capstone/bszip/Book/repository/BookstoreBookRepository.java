package com.capstone.bszip.Book.repository;

import com.capstone.bszip.Book.domain.Book;
import com.capstone.bszip.Book.domain.BookstoreBook;
import com.capstone.bszip.Bookstore.domain.Bookstore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookstoreBookRepository extends JpaRepository<BookstoreBook, Long> {

    boolean existsByBookAndBookstore(Book book, Bookstore bookstore);
}
