package com.capstone.bszip.Bookstore.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder(toBuilder = true) // toBuilder = true 옵션 추가
@Table(name="bookstores")
@NoArgsConstructor
@AllArgsConstructor
public class Bookstore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="bookstore_id")
    private Long bookstoreId;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category",nullable = false)
    private BookstoreCategory bookstoreCategory;

    @Column(name = "phone")
    private String phone;

    @Column(name = "hours")
    private String hours;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "description")
    private String description;

    @Column(name="rating")
    private Double rating;

    @Column(name="keyword")
    private String keyword;

    // 별점만 변경하는 메서드
    public Bookstore updateRating(double newRating) {
        return this.toBuilder().rating(newRating).build();
    }
}