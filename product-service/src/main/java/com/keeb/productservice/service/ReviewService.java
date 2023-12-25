package com.keeb.productservice.service;

import com.keeb.productservice.model.Review;
import com.keeb.productservice.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ResponseEntity<String> addReview(Review review) {
        try {
            reviewRepository.save(review);

            log.info("Creating review for product with id: " + review.getProductId());

            return ResponseEntity.ok("Review added");
        } catch (Exception e) {
            log.info("Error while adding review " + e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

//    public List<Review> getReviewsByProductId(Long productId) {
//        try {
//            log.info("Fetching reviews with product id: " + productId);
//
//            return reviewRepository.findByProductId(productId);
//        } catch (Exception e) {
//            log.info("Error while getting reviews by product id " + e);
//            return ResponseEntity.internalServerError().body(e.getMessage());
//        }
//    }

    public ResponseEntity<String> updateReview(Review review) {
        try {
            reviewRepository.save(review);

            log.info("Updating review for product with id: " + review.getProductId());

            return ResponseEntity.ok("Review updated");
        } catch (Exception e) {
            log.info("Error while updating review " + e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public ResponseEntity<String> deleteReview(Long id) {
        try {
            reviewRepository.deleteById(id);

            log.info("Deleting review for product with id: " + id);

            return ResponseEntity.ok("Review updated");
        } catch (Exception e) {
            log.info("Error while deleting review " + e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
