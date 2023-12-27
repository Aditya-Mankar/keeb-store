package com.keeb.userservice.service;

import com.keeb.userservice.configuration.ProductFeignClient;
import com.keeb.userservice.model.Product;
import com.keeb.userservice.model.User;
import com.keeb.userservice.repository.UserRepository;
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
public class UserService {

    private final UserRepository userRepository;
    private final ProductFeignClient productFeignClient;

    public ResponseEntity<Object> fetchUser(String emailId) {
        try {
            Optional<User> user = userRepository.findByEmailId(emailId);

            log.info("Fetching user with emailId: " + emailId);

            return ResponseEntity.ok(user.orElse(null));
        } catch (Exception e) {
            log.info("Error while fetching user " + e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public ResponseEntity<String> createUser(User user) {
        try {
            userRepository.save(user);

            log.info("Saving user with emailId: " + user.getEmailId());

            return ResponseEntity.ok("User created");
        } catch (Exception e) {
            log.info("Error while creating user " + e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public ResponseEntity<String> updateUser(User user) {
        try {
            userRepository.save(user);

            log.info("Updating user with emailId: " + user.getEmailId());

            return ResponseEntity.ok("User updated");
        } catch (Exception e) {
            log.info("Error while updating user " + e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public ResponseEntity<String> deleteUser(String emailId) {
        try {
            userRepository.deleteByEmailId(emailId);

            log.info("Deleting user with emailId: " + emailId);

            return ResponseEntity.ok("User deleted");
        } catch (Exception e) {
            log.info("Error while deleting user " + e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public ResponseEntity<String> addProductToWishlist(String emailId, Long productId) {
        try {
            Optional<User> user = userRepository.findByEmailId(emailId);

            ResponseEntity<List<Product>> productsResponse = productFeignClient.fetchProducts(List.of(productId));
            List<Product> products = productsResponse.getBody();

            user.ifPresent(us -> {
                List<Product> wishlist = new ArrayList<>();

                if (us.getWishlist() != null)
                    wishlist = us.getWishlist();

                wishlist.add(products.get(0));
                us.setWishlist(wishlist);

                userRepository.save(us);
            });

            log.info("Added product with id: " + productId + " to wishlist of user with emailId: " + emailId);

            return ResponseEntity.ok("Product added to wishlist");
        } catch (Exception e) {
            log.info("Error while adding product to wishlist " + e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public ResponseEntity<String> removeProductFromWishlist(String emailId, Long productId) {
        try {
            Optional<User> user = userRepository.findByEmailId(emailId);

            user.ifPresent(us -> {
                List<Product> wishlist = us.getWishlist();

                List<Product> updatedWishlist = wishlist.stream()
                        .filter(pro -> !pro.getId().equals(productId))
                        .collect(Collectors.toList());

                us.setWishlist(updatedWishlist);

                userRepository.save(us);
            });

            log.info("Removing product with id: " + productId + " to wishlist of user with emailId: " + emailId);

            return ResponseEntity.ok("Product removed from wishlist");
        } catch (Exception e) {
            log.info("Error while removing product from wishlist " + e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
