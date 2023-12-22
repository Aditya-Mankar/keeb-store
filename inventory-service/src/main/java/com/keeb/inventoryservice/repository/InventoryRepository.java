package com.keeb.inventoryservice.repository;

import com.keeb.inventoryservice.model.InventoryDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryDetails, Long> {

    @Query(value = "SELECT * FROM inventory_details WHERE product_id IN (:productIds)", nativeQuery = true)
    List<InventoryDetails> fetchByProductIds(List<Long> productIds);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM inventory_details WHERE product_id = :productId", nativeQuery = true)
    void deleteByProductId(Long productId);

}
