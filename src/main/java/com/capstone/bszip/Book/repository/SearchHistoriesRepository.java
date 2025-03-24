package com.capstone.bszip.Book.repository;

import com.capstone.bszip.Book.domain.SearchHistories;
import com.capstone.bszip.Book.dto.SearchType;
import com.capstone.bszip.Member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface SearchHistoriesRepository extends CrudRepository<SearchHistories, Long> {
    Page<SearchHistories> findByMemberAndSearchType(Member member, SearchType searchType, Pageable pageable);
}
