package com.keeb.productservice.repository;

import com.keeb.productservice.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query(value = "SELECT * FROM review WHERE product_id = :productId", nativeQuery = true)
    public List<Review> findByProductId(Long productId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM review WHERE product_id = :productId", nativeQuery = true)
    public void deleteByProductId(Long productId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM review WHERE id = :id", nativeQuery = true)
    public void deleteById(Long id);

}
