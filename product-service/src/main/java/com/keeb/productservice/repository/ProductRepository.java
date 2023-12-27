package com.keeb.productservice.repository;

import com.keeb.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT * FROM product WHERE id = :id", nativeQuery = true)
    public Optional<Product> findByProductId(Long id);

    @Query(value = "SELECT * FROM product WHERE id IN (:productIds)", nativeQuery = true)
    public List<Product> fetchProducts(List<Long> productIds);

}
