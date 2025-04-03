package com.capstone.bszip.Bookstore.service;

import com.capstone.bszip.Bookstore.domain.Bookstore;
import com.capstone.bszip.Bookstore.domain.BookstoreReview;
import com.capstone.bszip.Bookstore.repository.BookstoreRepository;
import com.capstone.bszip.Bookstore.repository.BookstoreReviewRepository;
import com.capstone.bszip.Bookstore.service.dto.BookstoreReviewRequest;
import com.capstone.bszip.Bookstore.service.dto.BookstoreReviewResponse;
import com.capstone.bszip.Member.domain.Member;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookstoreReviewService {
    private final BookstoreRepository bookstoreRepository;
    private final BookstoreReviewRepository bookstoreReviewRepository;

    @Transactional
    public BookstoreReviewResponse createReview(Member member, BookstoreReviewRequest request){
        Bookstore bookstore = bookstoreRepository.findById(request.getBookstoreId())
                .orElseThrow(() -> new EntityNotFoundException("해당 서점을 찾을 수 없습니다."));

        BookstoreReview review = new BookstoreReview();
        review.setBookstore(bookstore);
        review.setMember(member);
        review.setRating(request.getRating());
        review.setText(request.getText());
        review.setImageUrl(request.getImageUrl());

        bookstoreReviewRepository.save(review);

        return new BookstoreReviewResponse(
                review.getBookstoreReviewId(),
                member.getNickname(),
                review.getRating(),
                review.getText(),
                review.getImageUrl(),
                review.getCreatedAt()
        );
    }
    public List<BookstoreReviewResponse> getReviewsByBookstoreId(Long bookstoreId){
        List<BookstoreReview> reviews = bookstoreReviewRepository.findReviewsByBookstoreIdOrderByCreatedAtDesc(bookstoreId);
        return reviews.stream()
                .map(this::convertToBookstoreResponse)
                .collect(Collectors.toList());
    }
    private BookstoreReviewResponse convertToBookstoreResponse(BookstoreReview review) {

        return new BookstoreReviewResponse(
                review.getBookstoreReviewId(),
                review.getMember().getNickname(),
                review.getRating(),
                review.getText(),
                review.getImageUrl(),
                review.getCreatedAt()
        );
    }
}
