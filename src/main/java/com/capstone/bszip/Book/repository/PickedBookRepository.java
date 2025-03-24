package com.capstone.bszip.Book.repository;

import com.capstone.bszip.Book.domain.Book;
import com.capstone.bszip.Book.domain.PickedBook;
import com.capstone.bszip.Member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PickedBookRepository extends JpaRepository<PickedBook, Long> {
    Boolean existsByBookAndMember(Book book, Member member);

    Optional<PickedBook> findByBookAndMember(Book book, Member member);
}
