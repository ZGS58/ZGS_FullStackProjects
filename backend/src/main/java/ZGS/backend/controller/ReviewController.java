package ZGS.backend.controller;

import ZGS.backend.BaseResponse;
import ZGS.backend.dto.ReviewDto;
import ZGS.backend.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequiredArgsConstructor
public class ReviewController {

        private final ReviewService reviewService;

        @GetMapping("/product/{productId}")
        public ResponseEntity<BaseResponse<List<ReviewDto>>> getProductReviews(@PathVariable Long productId) {
                List<ReviewDto> reviews = reviewService.getProductReviews(productId);
                return ResponseEntity.ok(BaseResponse.ok(reviews));
        }

        @GetMapping("/room/{roomId}")
        public ResponseEntity<BaseResponse<List<ReviewDto>>> getRoomReviews(@PathVariable Long roomId) {
                List<ReviewDto> reviews = reviewService.getRoomReviews(roomId);
                return ResponseEntity.ok(BaseResponse.ok(reviews));
        }

        @PostMapping("/product/{productId}")
        public ResponseEntity<BaseResponse<ReviewDto>> createProductReview(
                        @PathVariable Long productId,
                        @RequestBody Map<String, Object> request,
                        Authentication authentication) {

                if (authentication == null || !authentication.isAuthenticated()) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(BaseResponse.error("請先登入"));
                }

                try {
                        String username = authentication.getName();
                        Integer rating = (Integer) request.get("rating");
                        String comment = (String) request.get("comment");

                        ReviewDto review = reviewService.createProductReview(username, productId, rating, comment);
                        return ResponseEntity.ok(BaseResponse.ok(review));

                } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest()
                                        .body(BaseResponse.error(e.getMessage()));
                }
        }

        @PostMapping("/room/{roomId}")
        public ResponseEntity<BaseResponse<ReviewDto>> createRoomReview(
                        @PathVariable Long roomId,
                        @RequestBody Map<String, Object> request,
                        Authentication authentication) {

                if (authentication == null || !authentication.isAuthenticated()) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(BaseResponse.error("請先登入"));
                }

                try {
                        String username = authentication.getName();
                        Integer rating = (Integer) request.get("rating");
                        String comment = (String) request.get("comment");

                        ReviewDto review = reviewService.createRoomReview(username, roomId, rating, comment);
                        return ResponseEntity.ok(BaseResponse.ok(review));

                } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest()
                                        .body(BaseResponse.error(e.getMessage()));
                }
        }

        @DeleteMapping("/{reviewId}")
        public ResponseEntity<BaseResponse<String>> deleteReview(
                        @PathVariable Long reviewId,
                        Authentication authentication) {

                if (authentication == null || !authentication.isAuthenticated()) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(BaseResponse.error("請先登入"));
                }

                try {
                        String username = authentication.getName();
                        boolean isAdmin = authentication.getAuthorities().stream()
                                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                        reviewService.deleteReview(reviewId, username, isAdmin);
                        return ResponseEntity.ok(BaseResponse.ok("評論已刪除"));

                } catch (IllegalArgumentException e) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                        .body(BaseResponse.error(e.getMessage()));
                } catch (RuntimeException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(BaseResponse.error(e.getMessage()));
                }
        }
}
