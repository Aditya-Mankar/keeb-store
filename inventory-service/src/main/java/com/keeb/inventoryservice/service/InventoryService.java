package com.keeb.inventoryservice.service;

import com.keeb.inventoryservice.model.InventoryDetails;
import com.keeb.inventoryservice.model.ProductInventory;
import com.keeb.inventoryservice.repository.InventoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public ResponseEntity<Object> fetchInventory(List<Long> productIds) {
        try {
            List<InventoryDetails> inventoryList = inventoryRepository.fetchByProductIds(productIds);
            List<ProductInventory> response = new ArrayList<>();

            inventoryList.forEach(inventory -> {
                ProductInventory productInventory = ProductInventory.builder()
                        .productId(inventory.getProductId())
                        .quantity(inventory.getQuantity())
                        .build();

                response.add(productInventory);
            });

            log.info("Fetching products with ids: " + productIds);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.info("Error while fetching inventory " + e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public ResponseEntity<String> addInventory(ProductInventory productInventory) {
        try {
            InventoryDetails inventory = InventoryDetails.builder()
                    .productId(productInventory.getProductId())
                    .quantity(productInventory.getQuantity())
                    .build();

            inventoryRepository.save(inventory);

            log.info("Saving inventory for product with id: " + productInventory.getProductId());

            return ResponseEntity.ok("Inventory added product with id: " + productInventory.getProductId());
        } catch (Exception e) {
            log.info("Error while saving inventory for product with id: " + productInventory.getProductId() + " " + e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public ResponseEntity<String> updateInventory(List<ProductInventory> request) {
        try {
            List<Long> productIds = request.stream()
                    .map(ProductInventory::getProductId)
                    .collect(Collectors.toList());

            List<InventoryDetails> inventoryList = inventoryRepository.fetchByProductIds(productIds);

            request.forEach(req -> {
                Optional<InventoryDetails> inv = inventoryList.stream()
                        .filter(inventory -> inventory.getProductId().equals(req.getProductId()))
                        .findAny();

                inv.ifPresent(inventory -> inventory.setQuantity(req.getQuantity()));
            });

            inventoryRepository.saveAll(inventoryList);

            log.info("Updating inventory for products: " + request);

            return ResponseEntity.ok("Inventory updated");
        } catch (Exception e) {
            log.info("Error while updating inventory " + e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public ResponseEntity<String> deleteInventory(Long productId) {
        try {
            inventoryRepository.deleteByProductId(productId);

            log.info("Deleting inventory for product with id: " + productId);

            return ResponseEntity.ok("Inventory deleted for product with id: " + productId);
        } catch (Exception e) {
            log.info("Error while deleting inventory for product with id: " + productId + " " + e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}