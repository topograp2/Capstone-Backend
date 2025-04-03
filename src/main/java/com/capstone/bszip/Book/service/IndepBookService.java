package com.capstone.bszip.Book.service;

import com.capstone.bszip.Book.dto.AddIsEndBookResponse;
import com.capstone.bszip.Book.dto.BookSearchResponse;
import com.capstone.bszip.Book.dto.BookType;
import com.capstone.bszip.Book.repository.BookRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class IndepBookService {

    private final BookRepository bookRepository;

    public AddIsEndBookResponse getIndepBookByBooktitle(String title, int page){
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<BookSearchResponse.IndepBook> indepBooks = bookRepository.findByBookNameContainingAndBookType(title, BookType.indep, pageable)
                .map(indepBook -> {
                    return BookSearchResponse.IndepBook.builder()
                        .bookId(indepBook.getBookId())
                        .bookImageUrl(indepBook.getBookImageUrl())
                        .authors(indepBook.getAuthors())
                        .title(indepBook.getBookName())
                        .build();}

                );
        return new AddIsEndBookResponse(indepBooks.isLast(), indepBooks.getContent());
    }

    public AddIsEndBookResponse getIndepBookByAuthor(String author, int page){
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<BookSearchResponse.IndepBook> indepBooks = bookRepository.findByAuthorsContainingAndBookType(author, BookType.indep, pageable)
                .map(indepBook -> {
                    return BookSearchResponse.IndepBook.builder()
                            .bookId(indepBook.getBookId())
                            .bookImageUrl(indepBook.getBookImageUrl())
                            .authors(indepBook.getAuthors())
                            .title(indepBook.getBookName())
                            .build();}

                );
        return new AddIsEndBookResponse(indepBooks.isLast(), indepBooks.getContent());
    }
}
