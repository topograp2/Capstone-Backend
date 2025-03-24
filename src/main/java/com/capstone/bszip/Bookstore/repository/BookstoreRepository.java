package com.capstone.bszip.Bookstore.repository;

import com.capstone.bszip.Bookstore.domain.Bookstore;
import com.capstone.bszip.Bookstore.domain.BookstoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookstoreRepository extends JpaRepository<Bookstore,Long> {
    @Query(value = "SELECT *, (6371 * acos(cos(radians(:userLat)) * cos(radians(latitude)) " +
            "* cos(radians(longitude) - radians(:userLng)) + sin(radians(:userLat)) * sin(radians(latitude)))) " +
            "AS distance FROM bookstores WHERE (:name IS NULL OR name LIKE %:name%)" +
            "OR (:address IS NULL OR address LIKE %:address%) ORDER BY distance ASC", nativeQuery = true)
    List<Bookstore> findByNameOrAddressOrderByDistance(String name, String address,
                                                       @Param("userLat") double userLat,
                                                       @Param("userLng") double userLng);

    @Query(value = "SELECT *, (6371 * acos(cos(radians(:userLat)) * cos(radians(latitude)) " +
            "* cos(radians(longitude) - radians(:userLng)) + sin(radians(:userLat)) * sin(radians(latitude)))) " +
            "AS distance FROM bookstores ORDER BY distance ASC", nativeQuery = true)
    List<Bookstore> findAllOrderByDistance(@Param("userLat") double userLat,
                                           @Param("userLng") double userLng);

    @Query(value = "SELECT *, (6371 * acos(cos(radians(:userLat)) * cos(radians(latitude)) " +
            "* cos(radians(longitude) - radians(:userLng)) + sin(radians(:userLat)) * sin(radians(latitude)))) " +
            "AS distance FROM bookstores WHERE category LIKE %:category% ORDER BY distance ASC  ", nativeQuery = true)
    List<Bookstore> findByBookstoreCategoryByDistance(@Param("category") BookstoreCategory category,
                                                      @Param("userLat") double userLat,
                                                      @Param("userLng") double userLng);

    @Query(value = "SELECT *, (6371 * acos(cos(radians(:userLat)) * cos(radians(latitude)) " +
            "* cos(radians(longitude) - radians(:userLng)) + sin(radians(:userLat)) * sin(radians(latitude)))) " +
            "AS distance FROM bookstores WHERE bookstore_id IN :bookstoreIds ORDER BY distance ASC", nativeQuery = true)
    List<Bookstore> findAllByIdOrderByDistance(@Param("bookstoreIds") List<Long> bookstoreIds,
                                               @Param("userLat") double userLat,
                                               @Param("userLng") double userLng);
}
