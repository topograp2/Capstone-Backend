package com.capstone.bszip.Book.service;

import com.capstone.bszip.Book.domain.SearchHistories;
import com.capstone.bszip.Book.dto.SearchType;
import com.capstone.bszip.Book.dto.SearchDto;
import com.capstone.bszip.Book.dto.SearchHistoryResponse;
import com.capstone.bszip.Book.repository.SearchHistoriesRepository;
import com.capstone.bszip.Member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

// 📍성능 개선을 위해 추후에 Redis로 변경할 것
@Service
@Slf4j
@AllArgsConstructor
public class SearchHistoriesSevice {

    private final SearchHistoriesRepository searchHistoriesRepository;


    public void storeSearchHistories(String searchType, String searchWord, Member member) {
        SearchType type = SearchType.valueOf(searchType.toUpperCase());
        SearchHistories searchHistories = SearchHistories.builder()
                .member(member)
                .searchWord(searchWord)
                .searchType(type)
                .build();
        searchHistoriesRepository.save(searchHistories);
    }

    public SearchHistoryResponse getSearchHistories(Member member, String searchType, int page, int size) {
        SearchType type = SearchType.valueOf(searchType.toUpperCase());
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "searchDate"));
        Page<?> s = searchHistoriesRepository.findByMemberAndSearchType(member, type, pageable);
        Page<SearchDto> searchDtos =  searchHistoriesRepository.findByMemberAndSearchType(member, SearchType.BOOKTITLE, pageable)
                .map(searchHistory -> {
                            System.out.println(searchHistory.getSearchDate());
                            System.out.println(searchHistory.getSearchType());
                            System.out.println(searchHistory.getSearchWord());
                    return SearchDto.builder()
                            .id(searchHistory.getId())
                            .searchWord(searchHistory.getSearchWord())
                            .build();
                        }
                        );
        System.out.println("💛" + searchDtos);
        return SearchHistoryResponse.builder()
                .searchHistory(searchDtos.getContent())
                .isEnd(searchDtos.isLast())
                .totalPages(searchDtos.getTotalPages())
                .totalElements(searchDtos.getTotalElements())
                .build();
    }



}
